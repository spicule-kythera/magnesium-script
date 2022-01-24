package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.spicule.magnesium_script.Parser;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.*;

@SuppressWarnings("rawtypes")
abstract public class Expression {
  // Error specs
  public static class InvalidExpressionSyntax extends Exception {
    InvalidExpressionSyntax(Exception e) {
      super(e.getMessage());
    }

    InvalidExpressionSyntax(String key) {
      super("Invalid syntax for expression `" + key + "`!");
    }
  }

  // Static things
  public static final Logger LOG = LoggerFactory.getLogger(Expression.class);

  // Instance things
  WebDriver driver;
  Expression parent;
  Map<String, Object> context = new HashMap<>();

  // Constructor
  Expression(WebDriver driver, Expression parent) {
    this.driver = driver;
    this.parent = parent;
  }

  // Abstract interface
  abstract public @Nullable Object execute() throws Break.StopIterationException;

  abstract public Expression parse(Map<String, Object> tokens)
      throws InvalidExpressionSyntax, Parser.InvalidExpressionType;

  // Setters and Getters
  protected final Map<String, Object> getContext() {
    return context;
  }

  public final Expression getParent() {
    return parent;
  }

  public void setParent(Expression parent) {
    this.parent = parent;
  }



  // Common expression utilities
  public Object by(String locatorType, String locator) {
    String type = locatorType.toLowerCase();
    type = type.replace("_", "-");

    switch (type) {
      case "class":
        return By.className(locator);
      case "css":
        return By.cssSelector(locator);
      case "id":
        return By.id(locator);
      case "link-text":
        return By.linkText(locator);
      case "name":
        return By.name(locator);
      case "partial-link-text":
        return By.partialLinkText(locator);
      case "tag-name":
        return By.tagName(locator);
      case "xpath":
        return By.xpath(locator);
      case "variable":
        return resolveVariableName(locator);
      default:
        throw new InvalidArgumentException("Unsupported locator type: `" + locatorType + "`!");
    }
  }

  public static String classPathToSlugName(Object e) {
    String[] parts = e.getClass().toString().split("\\.");
    return parts[parts.length - 1].toLowerCase();
  }

  public static void guardedSleep(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      // Do nothing
    }
  }

  public static String validateTypeClass(Class enumeration, String value) throws InvalidExpressionSyntax {
    List<Object> constants = Arrays.asList(enumeration.getEnumConstants());
    for(Object e : constants) {
      if(e.toString().equalsIgnoreCase(value.replace("-", "_"))) {
        return e.toString();
      }
    }
    throw new InvalidExpressionSyntax("Invalid type `" + value + "` for " + enumeration.getName() + "! Value must be one of the following: " + constants);
  }

  protected static void assertRequiredField(String fieldName, Type fieldType, Map<String, Object> tokens) throws InvalidExpressionSyntax {
    if(!tokens.containsKey(fieldName)) {
      throw new InvalidExpressionSyntax("Expected `" + fieldName + "` in: " + tokens);
    }

    Type tokenType = tokens.get(fieldName).getClass();
    if(fieldType != tokenType) {
      throw new InvalidExpressionSyntax("Expected `" + fieldName + "` to be of type `" +  fieldType + "` but got `" + tokenType + "` instead!");
    }
  }

  protected static void assertRequiredMultiTypeField(String fieldName, List<Type> types, Map<String, Object> tokens) throws InvalidExpressionSyntax {
    boolean matchedType = false;
    Object name = tokens.get(fieldName);
    if(name == null) {
      throw new InvalidExpressionSyntax("Expected `" + fieldName + "` to exist but it was not found! Tokens: " + tokens);
    }

    Type tokenType = name.getClass();

    for(Type type : types) {
      try{
        assertRequiredField(fieldName, type, tokens);
        matchedType = true;
        break;
      } catch (InvalidExpressionSyntax e) {
        // Do nothing if the type does not match, check for failure later
      }
    }

    if(!matchedType) {
      throw new InvalidExpressionSyntax("Expected `" + fieldName + "` to be one of types: `" +  types + "` but got `" + tokenType + "` instead!");
    }
  }

  protected static boolean assertOptionalField(String fieldName, Type fieldType, Map<String, Object> tokens) throws InvalidExpressionSyntax {
    if(tokens.containsKey(fieldName)) {
      Type tokenType = tokens.get(fieldName).getClass();
      if(fieldType != tokenType) {
        throw new InvalidExpressionSyntax("Expected optional-field `" + fieldName + "` to be of type `" +  fieldType + "` but got `" + tokenType + "` instead!");
      }
      return true;
    }
    return false;
  }

  public void appendContext(Map<String, Object> context) {
    for(Map.Entry<String, Object> entry : context.entrySet()) {
      if(!this.context.containsKey(entry.getKey())) {
        this.context.put(entry.getKey(), entry.getValue());
      }
    }
  }

  public String getWebElementXPath(WebElement element) {
    return getWebElementXPath(element, "");
  }

  private String getWebElementXPath(WebElement element, String context) {
    String tag = element.getTagName();

    if(tag.equalsIgnoreCase("html")) {
      return "/html[1]" + context;
    }

    WebElement parent = element.findElement(By.xpath(".."));
    List<WebElement> ancestors = parent.findElements(By.xpath("*"));

    int count = 0;
    for(int i = 0; i < ancestors.size(); ++i) {
      WebElement child = ancestors.get(i);
      String childTag = child.getTagName();

      if(tag.equalsIgnoreCase(childTag)) {
        count++;
      }
      if(element.equals(child)) {
        return getWebElementXPath(parent, "/" + tag + "[" + count + "]" + context);
      }
    }
    return null;
  }

  protected Object resolveVariableName(String variableName) {
    Object value = context.get(variableName);

    if(value != null){
      LOG.debug("Found variable `" + variableName + "` = `" + value + "` in context: `" + this.getClass() + "`");
      return value;
    }

    LOG.warn("Could not find `" + variableName + "` in context: `" + this.getClass() + "` moving up a scope...");
    Expression parent = this.parent;
    while(parent != null) {
      value = parent.getContext().get(variableName);

      if(value != null) {
        LOG.debug("Found variable `" + variableName + "` = `" + value + "` in context: `" + parent.getClass() + "`");
        return value;
      }

      LOG.warn("Could not find `" + variableName + "` in context: `" + parent.getClass() + "` moving up a scope...");
      parent = parent.getParent();
    }

    LOG.error("Tried to resolve the variable name `" + variableName + "` but was undefined!");
    return null;
  }
}

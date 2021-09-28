package uk.co.spicule.magnesium_script.expressions;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.spicule.magnesium_script.Parser;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

abstract public class Expression {
  // Static things
  public static final String DATE_FMT = "yyyy_MM_dd_HH-mm-ss.SSS";
  public static final Logger LOG = LoggerFactory.getLogger(Expression.class);

  // Error specs
  public static class InvalidExpressionSyntax extends Exception {
    InvalidExpressionSyntax(Exception e) {
      super(e.getMessage());
    }

    InvalidExpressionSyntax(String key) {
      super("Invalid syntax for expression `" + key + "`!");
    }

    InvalidExpressionSyntax(String key, String message) {
      super("Invalid syntax for expression `" + key + "`: " + message + "!");
    }
  }

  // Instance things
  WebDriver driver;
  Expression parent;
  Map<String, Object> context = new HashMap<>();

  Expression(WebDriver driver, Expression parent) {
    this.driver = driver;
    this.parent = parent;
  }



  abstract public @Nullable Object execute() throws Break.StopIterationException;

  abstract public Expression parse(Map<String, Object> tokens)
      throws InvalidExpressionSyntax, Parser.InvalidExpressionType;

  protected String getElementXPath(WebElement element) {
    return (String) ((JavascriptExecutor) driver).executeScript(
        "gPt=function(c){if(c.id!==''){return'[@id=\"'+c.id+'\"]'}if(c===document.body){return c.tagName}var a=0;var e=c.parentNode.childNodes;for(var b=0;b<e.length;b++){var d=e[b];if(d===c){return gPt(c.parentNode)+'/'+c.tagName+'['+(a+1)+']'}if(d.nodeType===1&&d.tagName===c.tagName){a++}}};return gPt(arguments[0]);",
        element);
  }

  protected static String nowToString() {
    return dateToString(new Date());
  }

  protected static String dateToString(Date date) {
    return new SimpleDateFormat(DATE_FMT).format(date);
  }

  @Getter
  protected final Map<String, Object> getContext() {
    return context;
  }

  @Getter
  public final Expression getParent() {
    return parent;
  }

  @Setter
  public void setParent(Expression parent) {
    this.parent = parent;
  }

  public static final By by(String locatorType, String locator) {
    switch (locatorType.toLowerCase()) {
      case "class":
        return By.className(locator);
      case "css":
        return By.cssSelector(locator);
      case "id":
        return By.id(locator);
      case "link-text":
      case "link_text":
        return By.linkText(locator);
      case "name":
        return By.name(locator);
      case "partial-link-text":
      case "partial_link_text":
        return By.partialLinkText(locator);
      case "tag-name":
      case "tag_name":
        return By.tagName(locator);
      case "xpath":
        return By.xpath(locator);
      default:
        throw new InvalidArgumentException("Unsupported locator type: `" + locatorType + "`!");
    }
  }

  public static String classPathToSlugName(Object e) {
    String[] parts = e.getClass().toString().split("\\.");
    return parts[parts.length - 1].toLowerCase();
  }

  protected void guardedSleep(long time) {
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

  protected void assertRequiredField(String dependent, String fieldName, Type fieldType, Map<String, Object> tokens) throws InvalidExpressionSyntax {
    if(!tokens.containsKey(fieldName)) {
      throw new InvalidExpressionSyntax(dependent + " expected `" + fieldName + "` in: " + tokens);
    }

    Type tokenType = tokens.get(fieldName).getClass();
    if(fieldType != tokenType) {
      throw new InvalidExpressionSyntax("Expected `" + fieldName + "` to be of type `" +  fieldType + "` but got `" + tokenType + "` instead!");
    }
  }

  protected void assertRequiredMultiTypeField(String fieldName, List<Type> types, Map<String, Object> tokens) throws InvalidExpressionSyntax {
    boolean matchedType = false;
    Type tokenType = tokens.get(fieldName).getClass();

    for(Type type : types) {
      try{
        assertRequiredField(fieldName, fieldName, type, tokens);
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

  protected void assertRequiredFields(String dependent, Map<String, Type> fields, Map<String, Object> tokens) throws InvalidExpressionSyntax{
    for(Map.Entry<String, Type> field : fields.entrySet()) {
      assertRequiredField(dependent, field.getKey(), field.getValue(), tokens);
    }
  }

  protected boolean assertOptionalField(String fieldName, Type fieldType, Map<String, Object> tokens) throws InvalidExpressionSyntax {
    if(tokens.containsKey(fieldName)) {
      Type tokenType = tokens.get(fieldName).getClass();
      if(fieldType != tokenType) {
        throw new InvalidExpressionSyntax("Expected optional-field `" + fieldName + "` to be of type `" +  fieldType + "` but got `" + tokenType + "` instead!");
      }
      return true;
    }
    return false;
  }

  protected List<Boolean> assertOptionalFields(Map<String, Type> fields, Map<String, Object> tokens) throws InvalidExpressionSyntax{
    List<Boolean> fieldsExist = new ArrayList<>();
    for(Map.Entry<String, Type> field : fields.entrySet()) {
      boolean exists = assertOptionalField(field.getKey(), field.getValue(), tokens);
      fieldsExist.add(exists);
    }
    return fieldsExist;
  }

  public void appendContext(Map<String, Object> context) {
    for(Map.Entry<String, Object> entry : context.entrySet()) {
      if(!this.context.containsKey(entry.getKey())) {
        this.context.put(entry.getKey(), entry.getValue());
      }
    }
  }
}

package uk.co.spicule.magnesium_script.expressions;

import jdk.nashorn.internal.objects.annotations.Getter;
import org.openqa.selenium.*;
import uk.co.spicule.magnesium_script.Parser;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

abstract public class Expression {
  public static final String DATE_FMT = "yyyy_MM_dd_HH-mm-ss.SSS";

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

  WebDriver driver;
  Expression parent;
  Map<String, Object> context = new HashMap<>();

  Expression(WebDriver driver, Expression parent) {
    this.driver = driver;
    this.parent = parent;
  }

  abstract public @Nullable Object execute();

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
  protected final Expression getParent() {
    return parent;
  }

  protected final By by(String locatorType, String locator) {
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

  protected void assertRequiredField(String dependent, String fieldName, Type fieldType, Map<String, Object> tokens) throws InvalidExpressionSyntax {
    if(!tokens.containsKey(fieldName)) {
      throw new InvalidExpressionSyntax(dependent + " expected `" + fieldName + "` in: " + tokens);
    }

    Type tokenType = tokens.get(fieldName).getClass();
    if(fieldType != tokenType) {
      throw new InvalidExpressionSyntax("Expected `" + fieldName + "` to be of type `" +  fieldType + "` but got `" + tokenType + "` instead!");
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
}

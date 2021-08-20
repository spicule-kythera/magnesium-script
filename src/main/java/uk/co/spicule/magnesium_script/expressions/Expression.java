package uk.co.spicule.magnesium_script.expressions;

import uk.co.spicule.magnesium_script.Parser;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

  abstract public void execute();

  abstract public Expression parse(Map<String, Object> tokens)
      throws InvalidExpressionSyntax, Parser.InvalidExpressionType;

  protected String getElementXPath(WebElement element) {
    return (String) ((JavascriptExecutor) driver).executeScript(
        "gPt=function(c){if(c.id!==''){return'[@id=\"'+c.id+'\"]'}if(c===document.body){return c.tagName}var a=0;var e=c.parentNode.childNodes;for(var b=0;b<e.length;b++){var d=e[b];if(d===c){return gPt(c.parentNode)+'/'+c.tagName+'['+(a+1)+']'}if(d.nodeType===1&&d.tagName===c.tagName){a++}}};return gPt(arguments[0]);",
        element);
  }

  protected static String getDateString() {
    return getDateString(new Date());
  }

  protected static String getDateString(Date date) {
    return new SimpleDateFormat(DATE_FMT).format(date);
  }

  protected Map<String, Object> getContext() {
    return context;
  }

  protected final Expression getParent() {
    return parent;
  }
}

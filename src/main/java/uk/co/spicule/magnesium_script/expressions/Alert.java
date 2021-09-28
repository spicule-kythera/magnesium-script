package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


public class Alert extends Expression {
  enum AlertAction {
    ACCEPT,
    DISMISS,
    SEND_KEYS;

    protected static AlertAction stringToEnum(String name) throws InvalidExpressionSyntax {
      return AlertAction.valueOf(Expression.validateTypeClass(AlertAction.class, name));
    }
  }

  AlertAction action = null;
  SendKeys keys = null;
  Integer timeout = null;


  public Alert(WebDriver driver, Expression parent) {
    super(driver, parent);
  }

  public Object execute() {
    LOG.debug("Resolving expression: `" + this.getClass() + "`!");

    // Wait for an alert to appear and focus on it
    new Wait(driver, this, Wait.WaitType.ALERT_EXISTS, null, timeout).execute();
    org.openqa.selenium.Alert alert = driver.switchTo().alert();

    // Perform the action
    switch(action) {
      case ACCEPT:
        alert.accept();
      case DISMISS:
        alert.dismiss();
      case SEND_KEYS:
        keys.execute(alert);
    }
    return null;
  }

  public Alert parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
    // Assert the required and optional fields
    HashMap<String, Type> requiredFields = new HashMap<>();
    requiredFields.put("alert", String.class);
    assertRequiredFields("alert", requiredFields, tokens);
    boolean hasTimeout = assertOptionalField("timeout", Integer.class, tokens);

    // Populate timeout if it exists
    if(hasTimeout) {
      timeout = Integer.parseInt(tokens.get("timeout").toString());
    }

    // Populate the action
    String actionToken = tokens.get("alert")
                               .toString()
                               .toUpperCase()
                               .replace("-", "_");
    action = AlertAction.stringToEnum(actionToken);

    // Populate send-keys if that is the alert action
    if(action == AlertAction.SEND_KEYS) {
      parseSendKeys(tokens);
    }

    return this;
  }

  private void parseSendKeys(Map<String, Object> tokens) throws InvalidExpressionSyntax {
    // Assert the required and optional fields
    HashMap<String, Type> requiredFields = new HashMap<>();
    requiredFields.put("send-keys", String.class);
    assertRequiredFields("alert-send-keys", requiredFields, tokens);

    keys = new SendKeys(driver, this, tokens.get("send-keys").toString(), null);
  }
}

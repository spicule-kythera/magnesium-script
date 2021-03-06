package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;

import java.util.Map;

public class Alert extends Expression {
  enum AlertAction {
    ACCEPT,
    DISMISS,
    SEND_KEYS;

    private static AlertAction stringToEnum(String name) throws InvalidExpressionSyntax {
      return AlertAction.valueOf(Expression.validateTypeClass(AlertAction.class, name));
    }
  }

  Wait wait = null;
  AlertAction action = null;
  SendKeys keys = null;
  Integer timeout = null;

  public Alert() {
    super(null, null);
  }

  public Alert(WebDriver driver, Expression parent) {
    super(driver, parent);
  }

  public Object execute() {
    LOG.debug("Resolving expression: `" + this.getClass() + "`!");

    // Wait for an alert to appear and focus on it
    wait.execute();
    org.openqa.selenium.Alert alert = driver.switchTo().alert();

    // Perform the action
    switch(action) {
      case ACCEPT:
        alert.accept();
        break;
      case DISMISS:
        alert.dismiss();
        break;
      case SEND_KEYS:
        keys.execute(alert);
        break;
      default:
        throw new RuntimeException("FATAL: Invalid alert-action: " + action);
    }
    return null;
  }

  public Alert parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
    // Assert the required and optional fields
    assertRequiredField("alert", String.class, tokens);

    // Populate timeout if it exists
    if(assertOptionalField("timeout", Integer.class, tokens)) {
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

    // Populate wait
    wait = new Wait(driver, this).parse(Wait.WaitType.ALERT_EXISTS, timeout);

    return this;
  }

  private void parseSendKeys(Map<String, Object> tokens) throws InvalidExpressionSyntax {
    // Assert the required and optional fields
    assertRequiredField("send-keys", String.class, tokens);

    keys = new SendKeys(driver, this, tokens.get("send-keys").toString(), null);
  }
}

package com.kytheralabs.magnesium_script.expressions;

import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Alert extends Expression {
    enum Action {
        ACCEPT, DISMISS, KEYS
    }

    int timeout = 10;
    Action action = null;
    org.openqa.selenium.Alert alert;
    String keys = null;

    public Alert(WebDriver driver) {
        super(driver);
    }

    public Alert parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Process alert `action`
        if(!tokens.containsKey("alert")) {
            throw new InvalidExpressionSyntax("alert", "expression must specify action: `[accept, dismiss, keys]`");
        }
        action = Action.valueOf(tokens.get("alert").toString().toUpperCase());

        // Process `input` field
        if(action.equals(Action.KEYS)) {
            if(!tokens.containsKey("input")) {
                throw new InvalidExpressionSyntax("alert", "`keys` action requires `input` field containing string of input to send!");
            }

            keys = tokens.get("input").toString();
        }

        return this;
    }

    public void eval() {
        try {
            alert = new WebDriverWait(driver, timeout).until(ExpectedConditions.alertIsPresent());
        } catch(NoAlertPresentException e) {
            System.out.println("Waited " + timeout + "s for an alert but timed out!");
            return;
        }

        switch (action) {
            case ACCEPT:
               alert.accept();
            case DISMISS:
                alert.dismiss();
            case KEYS:
                alert.sendKeys(keys);
        }
    }
}
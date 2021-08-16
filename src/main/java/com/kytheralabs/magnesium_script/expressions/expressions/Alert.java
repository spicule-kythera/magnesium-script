package com.kytheralabs.magnesium_script.expressions.expressions;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.Map;

public class Alert extends Expression {
    enum Action {
        ACCEPT, DISMISS, KEYS
    }

    long timeout = -1;
    Action action = null;
    org.openqa.selenium.Alert alert;
    String keys = null;

    public Alert(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Alert parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Process alert `action`
        if(!tokens.containsKey("alert")) {
            throw new InvalidExpressionSyntax("alert", "Expected `alert` field");
        }
        String actionToken = tokens.get("alert").toString();
        try {
            action = Action.valueOf(tokens.get("alert").toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidExpressionSyntax("alert", "Invalid action type: `" + actionToken + "`! Token must be one of the following: " + Arrays.toString(Action.values()));
        }

        // Process `input` field
        if(action.equals(Action.KEYS)) {
            if(!tokens.containsKey("input")) {
                throw new InvalidExpressionSyntax("alert", "`keys` action requires `input` field containing string of input to send!");
            }

            keys = tokens.get("input").toString();
        }

        // Process timeout
        String timeoutToken = tokens.getOrDefault("timeout", 10).toString();
        try {
            timeout = Long.parseLong(timeoutToken);
        } catch(NumberFormatException e) {
            throw new InvalidExpressionSyntax("alert", "Invalid `timeout` value: `" + timeoutToken + "`! Value must be whole integer!`");
        }

        return this;
    }

    public void execute() {
        try {
            // TODO: Find the replacement for deprecated WebDriverWait
            alert = driver.switchTo().alert();
//            alert = new WebDriverWait(driver, timeout).until(ExpectedConditions.alertIsPresent());
        } catch(NoAlertPresentException |TimeoutException e) {
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
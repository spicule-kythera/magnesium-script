package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Click extends Expression {
    enum ClickType {
        ELEMENT,
        JS;

        private static ClickType stringToEnum(String name) throws InvalidExpressionSyntax {
            return ClickType.valueOf(Expression.validateTypeClass(ClickType.class, name));
        }
    }

    ClickType type = ClickType.ELEMENT;
    By locator = null;
    String variableName = null;
    int index = 0;
    Integer timeout = null;

    public Click(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        // Fetch the element
        WebElement element;
        if(locator != null){
            element = driver.findElements(locator).get(index);
        } else {
            Object rawVariableValue = resolveVariableName(variableName);
            Type expected = WebElement.class;
            Type got = rawVariableValue.getClass();
            if(!(rawVariableValue instanceof WebElement)) {
                throw new RuntimeException("Click expected variable `" + variableName + "` to be of type `" + expected + "` but got `" + got + "`!");
            }

            element = (WebElement) rawVariableValue;
        }

        // Wait for the element to be clickable
        String xpath = getWebElementXPath(element);
        LOG.debug("Click Locator derevation: `" + xpath + "`");
//        new Wait(driver, this, Wait.WaitType.ELEMENT_CLICKABLE, "xpath", xpath).execute();

        // Scrolls element into view before clicking
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);

        switch (type) {
            case ELEMENT:
                element.click();
                break;
            case JS:
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                break;
            default:
                throw new RuntimeException("FATAL: Invalid click-type: " + type);
        }

        return null;
    }

    public Click parse(ClickType type, String locatorType, String locator) throws InvalidExpressionSyntax {
        Map<String, Object> tokens = new HashMap<>();
        tokens.put("click", type.toString());
        tokens.put("locator-type", locatorType);
        tokens.put("locator", locator);

        return parse(tokens);
    }

    public Click parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        assertRequiredField("click", String.class, tokens);
        assertRequiredField("locator-type", String.class, tokens);
        assertRequiredField("locator", String.class, tokens);

        // Assert optional fields
        boolean hasTimeout = assertOptionalField("timeout", Integer.class, tokens);
        if(hasTimeout) {
            timeout = Integer.parseInt(tokens.get("timeout").toString());
        }

        boolean hasIndex = assertOptionalField("index", Integer.class, tokens);
        if(hasIndex) {
            this.index = Integer.parseInt(tokens.get("index").toString());
        }

        // Click type
        type = ClickType.stringToEnum(tokens.get("click").toString());

        // Get the locator type and subsequent clickable pieces
        String locatorType = tokens.get("locator-type").toString().toLowerCase();
        String locator = tokens.get("locator").toString();

        // Populate the locator
        switch(locatorType){
            case "variable":
                variableName = locator.substring(1, locator.length() - 1);
                break;
            default:
                this.locator = (By) by(locatorType, locator);
        }

        return this;
    }
}

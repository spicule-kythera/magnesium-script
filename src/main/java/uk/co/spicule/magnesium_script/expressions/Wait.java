package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Wait extends Expression {
    enum WaitType {
        ELEMENT_EXISTS,
        ELEMENT_CLICKABLE,
        PAGE_LOADS,
        UNCONDITIONAL
    }

    // Operation instance things
    WaitType type = WaitType.UNCONDITIONAL;
    long timeout = 30; // Wait-Timeout in seconds
    ExpectedCondition condition = null;

    public Wait(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Wait(WebDriver driver, Expression parent, WaitType condition, By locator) {
        super(driver, parent);

        switch(condition) {
            case ELEMENT_EXISTS:
                this.condition = ExpectedConditions.presenceOfElementLocated(locator);
                break;
            case ELEMENT_CLICKABLE:
                this.condition = ExpectedConditions.elementToBeClickable(locator);
        }
    }

    public Object execute() {
        new WebDriverWait(driver, timeout).until(condition);
        return null;
    }

    public Wait parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        HashMap<String, Type> requiredFields = new HashMap<>();
        requiredFields.put("wait", Integer.class);
        requiredFields.put("until", String.class);
        assertRequiredFields("wait", requiredFields, tokens);

        // Populate timeout
        timeout = Long.parseLong(tokens.get("wait").toString());

        // Determine the wait type
        String forToken = tokens.get("until").toString().toUpperCase().replace('-', '_');
        type = WaitType.valueOf(forToken);
        switch(type) {
            case ELEMENT_EXISTS:
                return parseWaitUntilElementExists(tokens);
            case ELEMENT_CLICKABLE:
                return parseWaitUntilElementClickable(tokens);
            case PAGE_LOADS:
                return parseWaitUntilPageLoads(tokens);
            case UNCONDITIONAL:
                return parseWaitUnconditionally(tokens);
            default:
                throw new InvalidExpressionSyntax("Invalid `wait` type: (`" + forToken+ "`: " + type + ")");
        }
    }

    private Wait parseWaitUntilElementExists(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required and optional fields
        HashMap<String, Type> requiredFields = new HashMap<>();
        requiredFields.put("locator-type", String.class);
        requiredFields.put("locator", String.class);
        assertRequiredFields("wait-forCondition: element-exits", requiredFields, tokens);

        // Create the necessary wait object
        By locator = Expression.by(tokens.get("locator-type").toString(), tokens.get("locator").toString());
        condition = ExpectedConditions.presenceOfElementLocated(locator);

        return this;
    }

    private Wait parseWaitUntilElementClickable(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required and optional fields
        HashMap<String, Type> requiredFields = new HashMap<>();
        requiredFields.put("locator-type", String.class);
        requiredFields.put("locator", String.class);
        assertRequiredFields("wait-forCondition: element-clickable", requiredFields, tokens);

        // Create the necessary wait object
        By locator = Expression.by(tokens.get("locator-type").toString(), tokens.get("locator").toString());
        condition = ExpectedConditions.elementToBeClickable(locator);

        return this;
    }

    private Wait parseWaitUntilPageLoads(Map<String, Object> tokens) {
        // Create the necessary wait object
        condition = (driver) -> ((JavascriptExecutor) driver).executeScript("return document.readyState")
                                                             .toString()
                                                             .equals("complete");

        return this;
    }

    private Wait parseWaitUnconditionally(Map<String, Object> tokens) {
        condition = (driver) -> {
            try {
                // Thread.sleep() takes time in ms so multiply value by 1000
                Thread.sleep(timeout * 1000);
            } catch (InterruptedException e) {
                // Do nothing
            }
            return true;
        };
        return this;
    }
}

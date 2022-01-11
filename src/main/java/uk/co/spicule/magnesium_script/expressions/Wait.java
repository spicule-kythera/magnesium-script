package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Wait extends Expression {
    enum WaitType {
        ALERT_EXISTS,
        ELEMENT_EXISTS,
        ELEMENT_VISIBLE,
        ELEMENT_CLICKABLE,
        PAGE_LOADS,
        TRUE,
        FALSE;

        private static WaitType stringToEnum(String name) throws InvalidExpressionSyntax {
            return WaitType.valueOf(Expression.validateTypeClass(WaitType.class, name));
        }
    }

    // Operation instance things
    long timeout = 30; // Wait-Timeout in seconds
    WaitType type = WaitType.TRUE;
    ExpectedCondition condition = null;

    public Wait() {
        super(null, null);
    }

    public Wait(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    protected Wait(WebDriver driver, Expression parent, WaitType type, String locatorType, String locator) {
        super(driver, parent);

        try {
            parse(type, locatorType, locator);
        } catch (InvalidExpressionSyntax e) {

        }
    }

    public String toString() {
        return "Waiting for " + timeout + "s until: " + condition;
    }

    public Object execute() {
        LOG.debug("Waiting up to " + timeout + "s for " + type + " until: (" + condition + ")");

        return new WebDriverWait(driver, timeout).until(condition);
    }

    public Wait parse(WaitType type) throws InvalidExpressionSyntax{
        return parse(type, null, null, null);
    }

    public Wait parse(WaitType type, int timeout) throws InvalidExpressionSyntax{
        return parse(type, null, null, timeout);
    }

    public Wait parse(WaitType type, @Nullable String locatorType, @Nullable String locator) throws InvalidExpressionSyntax{
        return parse(type, locatorType, locator, null);
    }

    public Wait parse(WaitType type, @Nullable String locatorType, @Nullable String locator, @Nullable Integer timeout) throws InvalidExpressionSyntax{
        if(timeout == null){
            timeout = (int) this.timeout;
        }

        Map<String, Object> tokens = new HashMap<>();
        tokens.put("wait", timeout);
        tokens.put("until", type.toString());

        if(locatorType != null && locator != null) {
            tokens.put("locator-type", locatorType);
            tokens.put("locator", locator);
        }

        return parse(tokens);
    }

    public Wait parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        assertRequiredMultiTypeField("wait", Arrays.asList(Integer.class, Long.class), tokens);
        assertRequiredMultiTypeField("until", Arrays.asList(String.class, Boolean.class), tokens);

        // Populate timeout
        timeout = Long.parseLong(tokens.get("wait").toString());

        // Determine and parse the wait type
        String forToken = tokens.get("until").toString().toUpperCase().replace('-', '_');
        type = WaitType.stringToEnum(forToken);

        switch(type) {
            case ALERT_EXISTS:
                return parseWaitUntilAlertExists();
            case ELEMENT_EXISTS:
                return parseWaitUntilElementExists(tokens);
            case ELEMENT_VISIBLE:
                return parseWaitUntilElementVisible(tokens);
            case ELEMENT_CLICKABLE:
                return parseWaitUntilElementClickable(tokens);
            case PAGE_LOADS:
                return parseWaitUntilPageLoads();
            case TRUE:
                return parseWaitUntilTrue();
            case FALSE:
                return parseWaitUntilFalse();
            default:
                throw new InvalidExpressionSyntax("FATAL: Invalid wait-type: " + type);
        }
    }

    private Wait parseWaitUntilAlertExists() {
        condition = ExpectedConditions.alertIsPresent();
        return this;
    }

    private Wait parseWaitUntilElementExists(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required and optional fields
        assertRequiredField("locator-type", String.class, tokens);
        assertRequiredField("locator", String.class, tokens);

        // Create the necessary wait object
        By locator = (By) by(tokens.get("locator-type").toString(), tokens.get("locator").toString());
        condition = ExpectedConditions.presenceOfElementLocated(locator);

        return this;
    }

    private Wait parseWaitUntilElementVisible(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required and optional fields
        assertRequiredField("locator-type", String.class, tokens);
        assertRequiredField("locator", String.class, tokens);

        // Create the necessary wait object
        By locator = (By) by(tokens.get("locator-type").toString(), tokens.get("locator").toString());
        condition = ExpectedConditions.visibilityOfElementLocated(locator);

        return this;
    }

    private Wait parseWaitUntilElementClickable(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required and optional fields
        assertRequiredField("locator-type", String.class, tokens);
        assertRequiredField("locator", String.class, tokens);

        // Create the necessary wait object
        By locator = (By) by(tokens.get("locator-type").toString(), tokens.get("locator").toString());
        condition = ExpectedConditions.elementToBeClickable(locator);

        return this;
    }

    private Wait parseWaitUntilPageLoads() {
        // JS to run
        String js = "function sleep(delay){return new Promise(resolve => setTimeout(resolve, delay))};" +
        "while(document.readyState != 'complete') {sleep(250)};" +
        "return document.readyState;";

        // Create the necessary wait object
        condition =  ExpectedConditions.jsReturnsValue(js);

        return this;
    }

    private Wait parseWaitUntilTrue() {
        condition = (x) -> {
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

    private Wait parseWaitUntilFalse() {
        condition = (x) -> true;
        return this;
    }
}
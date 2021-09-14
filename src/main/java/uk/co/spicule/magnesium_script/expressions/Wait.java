package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Wait extends Expression {
    enum WaitType {
        ELEMENT, PAGE_LOAD, UNCONDITIONAL
    }

    // Static things
    private final static long defaultTimeout = 30; // Default timeout in seconds

    // Operation instance things
    WaitType type = WaitType.UNCONDITIONAL;
    long timeout = defaultTimeout;
    Map<String, Object> condition = new HashMap<>();


    public Wait(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        switch (type) {
            case ELEMENT:
                return executeWaitForElement();
            case PAGE_LOAD:
                return executeWaitForPageLoad();
            case UNCONDITIONAL:
                return executeWaitUnconditional();
            default:
                throw new UnknownError("Invalid wait-for type: `" + type + "`!");
        }
    }

    private Object executeWaitForElement() {
        String locatorType = condition.get("locator-type").toString();
        String locator = condition.get("locator").toString();


        return  null;
    }

    private Object executeWaitForPageLoad() {

        return  null;
    }

    private Object executeWaitUnconditional() {

        return  null;
    }

    public Wait parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        HashMap<String, Type> requiredFields = new HashMap<>();
        requiredFields.put("wait", Integer.class);
        requiredFields.put("for", String.class);
        assertRequiredFields("wait", requiredFields, tokens);

        // Determine the wait type
        String forToken = tokens.get("for").toString().toUpperCase().replace('-', '_');
        type = WaitType.valueOf(forToken);
        switch(type) {
            case ELEMENT:
                return parseWaitForElement(tokens);
            case PAGE_LOAD:
                return parseWaitForPageLoad(tokens);
            case UNCONDITIONAL:
                return parseWaitUnconditional(tokens);
            default:
                throw new InvalidExpressionSyntax("Invalid `wait` type: (`" + forToken+ "`: " + type + ")");
        }
    }

    private Wait parseWaitForElement(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required and optional fields
        HashMap<String, Type> requiredFields = new HashMap<>();
        requiredFields.put("locator-type", String.class);
        requiredFields.put("locator", String.class);
        assertRequiredFields("wait-for: element", requiredFields, tokens);
        boolean hasTimeout = assertOptionalField("timeout", Long.class, tokens);

        // Store the necessary condition data
        condition.put("locator-type", tokens.get("locator-type"));
        condition.put("locator", tokens.get("locator"));
        if(hasTimeout) {
            timeout = Long.parseLong(tokens.get("timeout").toString());
        }

        return this;
    }

    private Wait parseWaitForPageLoad(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        HashMap<String, Type> requiredFields = new HashMap<>();
        assertRequiredFields("wait-for: page-load", requiredFields, tokens);
        return this;
    }

    private Wait parseWaitUnconditional(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        HashMap<String, Type> requiredFields = new HashMap<>();
        assertRequiredFields("wait-for: unconditional", requiredFields, tokens);
        return this;
    }
}

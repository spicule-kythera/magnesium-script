package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.Alert;
import org.openqa.selenium.*;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.regex.Matcher;

public class SendKeys extends Expression {
    enum InputType {
        STRING, SPECIAL
    }

    By locator = null;
    InputType type = InputType.STRING;
    String keys = null;
    Keys specialKeys = null;
    int repeat = 1;
    long inputRate = 100; // Delay between keys in ms

    public SendKeys(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public SendKeys(WebDriver driver, Expression parent, String keys, @Nullable Long inputRate) {
        super(driver, parent);
        this.keys = keys;
        if(inputRate != null){
            this.inputRate = inputRate;
        }

        parseSpecialKeys();
    }

    public Object execute() {
        LOG.debug("Sending " + type.toString() + ": `" + ((type == InputType.STRING) ? keys : specialKeys) + "` to " + locator + " " + repeat + " times at a rate of " + inputRate + "ms/char!");

        // Get the web element and send the keys
        WebElement element = driver.findElement(locator);

        // Send the input
        for(int i = 0; i < repeat; ++i){
            switch (type) {
                case SPECIAL:
                    element.sendKeys(specialKeys);
                    break;
                case STRING:
                    return subExecuteString();
                default:
                    throw new RuntimeException("FATAL: Invalid input-type: " + type);
            }
        }

        return null;
    }

    public Object execute(Alert alert) {
        // Send the input
        switch (type) {
            case SPECIAL:
                throw new RuntimeException("Special-characters are not supported for alert-elements!");
            case STRING:
                for (Character c : keys.toCharArray()) {
                    alert.sendKeys(c.toString());
                    Expression.guardedSleep(inputRate);
                }
                break;
            default:
                throw new RuntimeException("FATAL: Invalid input-type: " + type);
        }

        return null;
    }

    private Object subExecuteString() {
        // Make a deep copy of the original input as not to change it
        String keys = String.copyValueOf(this.keys.toCharArray());

        // Inject the variable name
        Matcher matcher = SPECIAL_CHARACTER_PATTERN.matcher(keys);
        if(matcher.find()){
            String variableName = keys.substring(1 + matcher.start(), matcher.end() - 1).replaceAll("-", "_");
            Object value = resolveVariableName(variableName);
            if(value != null){
                LOG.debug("Injecting `" + value + "` into `" + keys + "`");
                keys = keys.replaceAll("\\{" + variableName + "}", value.toString());
                LOG.debug("New input: " + keys);
            }
        }

        // Get the element
        WebElement input = driver.findElement(locator);

        // Send the keys
        for(Character c: keys.toCharArray()) {
            input.sendKeys(c.toString());
            Expression.guardedSleep(inputRate);
        }

        return null;
    }

    public SendKeys parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        assertRequiredField("send-keys", String.class, tokens);
        assertRequiredField("locator-type", String.class, tokens);
        assertRequiredField("locator", String.class, tokens);

        // Populate optional fields
        if(assertOptionalField("input-rate", Integer.class, tokens)) {
            inputRate = Long.parseLong(tokens.get("input-rate").toString());
        }
        if(assertOptionalField("repeat", Integer.class, tokens)) {
            repeat = Integer.parseInt(tokens.get("repeat").toString());
        }

        // Populate the locator
        locator = (By) by(tokens.get("locator-type").toString(), tokens.get("locator").toString());

        // Populate the raw keys input
        keys = tokens.get("send-keys").toString();

        // Populate the special keys if it matches the special-keys pattern
        parseSpecialKeys();

        return this;
    }

    private void parseSpecialKeys() {
        Matcher matcher = SPECIAL_CHARACTER_PATTERN.matcher(keys);
        if(matcher.find()) {
            String specialKeyName = keys.substring(1, keys.length() - 1).toUpperCase().replaceAll("-", "_");

            try {
                specialKeys = Keys.valueOf(specialKeyName);
                type = InputType.SPECIAL;
            } catch(IllegalArgumentException e) {
                LOG.warn("send-keys could not identify special character: `" + specialKeyName + "`! Defaulting to interpreting as string literal!");
            }
        }
    }
}

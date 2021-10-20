package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Click extends Expression {
    enum ClickType {
        ELEMENT,
        JS;

        protected static ClickType stringToEnum(String name) throws InvalidExpressionSyntax {
            return ClickType.valueOf(Expression.validateTypeClass(ClickType.class, name));
        }
    }

    ClickType type = ClickType.ELEMENT;
    By locator = null;
    int index = 0;
    Integer timeout = null;
    Wait wait = null;

    public Click(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        // Wait for the element to be clickable
        wait.execute();

        // Find the element
        WebElement element = driver.findElements(locator).get(index);
        
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
                throw new RuntimeException("Click type not available!");
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
        HashMap<String, Type> requiredFields = new HashMap<>();
        requiredFields.put("click", String.class);
        requiredFields.put("locator-type", String.class);
        requiredFields.put("locator", String.class);
        assertRequiredFields("click", requiredFields, tokens);

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

        // Get the locator
        String locatorType = tokens.get("locator-type").toString();
        String locator = tokens.get("locator").toString();
        this.locator = Expression.by(locatorType, locator);

        // Populate wait
        wait = new Wait(driver, this).parse(Wait.WaitType.ELEMENT_CLICKABLE, locatorType, locator);

        return this;
    }
}

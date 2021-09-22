package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    Integer timeout = null;

    public Click(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Click(WebDriver driver, Expression parent, ClickType type, By locator) {
        super(driver, parent);
        this.type = type;
        this.locator = locator;
    }

    public Object execute() {
        // Wait for the element to be clickable
        new Wait(driver, this, Wait.WaitType.ELEMENT_CLICKABLE, locator, timeout).execute();

        // Find the element
        WebElement element = driver.findElement(locator);

        switch (type) {
            case ELEMENT:
                element.click();
                break;
            case JS:
                throw new NotImplementedException();
        }

        return null;
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

        // Click type
        type = ClickType.stringToEnum(tokens.get("click").toString());

        // Get the locator
        locator = Expression.by(tokens.get("locator-type").toString(), tokens.get("locator").toString());

        return this;
    }
}

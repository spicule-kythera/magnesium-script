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
        ELEMENT, JS
    }

    ClickType type = ClickType.ELEMENT;
    By locator = null;

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
        new Wait(driver, this, Wait.WaitType.ELEMENT_CLICKABLE, locator).execute();

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

        // Click type
        type = ClickType.valueOf(tokens.get("click").toString().toUpperCase().replace("-", "_"));

        // Get the locator
        locator = Expression.by(tokens.get("locator-type").toString(), tokens.get("locator").toString());

        return this;
    }
}

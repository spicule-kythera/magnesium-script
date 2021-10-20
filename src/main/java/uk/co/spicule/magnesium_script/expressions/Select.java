package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Select extends Expression {
    enum SelectType {
        INDEX,
        TEXT,
        VALUE;

        protected static SelectType stringToEnum(String name) throws InvalidExpressionSyntax {
            return SelectType.valueOf(Expression.validateTypeClass(SelectType.class, name));
        }
    }

    By locator = null;
    SelectType type = SelectType.VALUE;
    String value = null;
    Integer index = null;
    Wait wait = null;

    public Select(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        LOG.debug("Selecting from drop-down element `" + locator + "` option by " + type + ": `" + ((type == SelectType.INDEX) ? index : value) + "`!");

        // Wait for and find the element
        wait.execute();
        WebElement rawMenu = driver.findElement(locator);
        org.openqa.selenium.support.ui.Select menu = new org.openqa.selenium.support.ui.Select(rawMenu);

        switch (type) {
            case INDEX:
                menu.selectByIndex(index);
                break;
            case TEXT:
                menu.selectByVisibleText(value);
                break;
            case VALUE:
                menu.selectByValue(value);
                break;
        }

        return null;
    }

    public Select parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        HashMap<String, Type> requiredFields = new HashMap<>();
        requiredFields.put("select", String.class);
        requiredFields.put("locator-type", String.class);
        requiredFields.put("locator", String.class);
        assertRequiredFields("select", requiredFields, tokens);
        assertRequiredMultiTypeField("by", Arrays.asList(String.class, Integer.class), tokens);

        // Populate the locator
        String locatorType = tokens.get("locator-type").toString();
        String locator = tokens.get("locator").toString();
        this.locator = by(locatorType, locator);

        // Populate the selection type
        type = SelectType.stringToEnum(tokens.get("by").toString());

        // Populate the value
        switch (type) {
            case INDEX:
                index = Integer.parseInt(tokens.get("select").toString());
            case TEXT:
            case VALUE:
                value = tokens.get("select").toString();
        }

        // Populate wait
        wait = new Wait(driver, this).parse(Wait.WaitType.ELEMENT_EXISTS, locatorType, locator);

        return this;
    }
}
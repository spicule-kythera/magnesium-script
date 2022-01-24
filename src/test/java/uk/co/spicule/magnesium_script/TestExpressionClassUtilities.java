package uk.co.spicule.magnesium_script;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import uk.co.spicule.magnesium_script.expressions.Alert;
import uk.co.spicule.magnesium_script.expressions.Expression;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestExpressionClassUtilities {
    @Test
    void validByLocatorTypes() {
        HashMap<String, By> expected_entries = new HashMap<>();
        String locator = "locator-string";
        expected_entries.put("class", By.className(locator));
        expected_entries.put("css", By.cssSelector(locator));
        expected_entries.put("id", By.id(locator));
        expected_entries.put("link-text", By.linkText(locator));
        expected_entries.put("LINK_TEXT", By.linkText(locator));
        expected_entries.put("name", By.name(locator));
        expected_entries.put("partial-link-text", By.partialLinkText(locator));
        expected_entries.put("PARTIAL_LINK_TEXT", By.partialLinkText(locator));
        expected_entries.put("tag-name", By.tagName(locator));
        expected_entries.put("TAG_NAME", By.tagName(locator));
        expected_entries.put("xpath", By.xpath(locator));

        for(Map.Entry<String, By> e : expected_entries.entrySet()) {
            String key = e.getKey();
            By expected = e.getValue();

            Assertions.assertEquals((By) new Alert().by(key, locator), expected);
        }
    }

    @Test
    void invalidByLocatorType() {
        String locatorType = "invalid type";
        Assertions.assertThrows(InvalidArgumentException.class, () -> {
            new Alert().by(locatorType, "locator string");
        });
    }

    @Test
    void validClassPathToSlugName() {
        InvalidArgumentException e = new InvalidArgumentException("Test Object");
        String expected = "InvalidArgumentException";

        Assertions.assertEquals(Expression.classPathToSlugName(e).toLowerCase(),
                                expected.toLowerCase());
    }

    @Test
    void guardedSleepDoesNotInterrupt() {
        Assertions.assertDoesNotThrow(() -> {
            Expression.guardedSleep(100);
        });
    }

    @Test
    void enumerationValidatesWithGoodValues() {
        Class enumeration = DriverFactory.BrowserType.class;
        List<String> values = Arrays.asList("firefox", "FIREFOX");

        for(String value : values) {
            Assertions.assertDoesNotThrow(() -> {
                Expression.validateTypeClass(enumeration, value);
            });
        }
    }

    @Test
    void enumerationDoesNotValidateWithBadValues() {
        Class enumeration = DriverFactory.BrowserType.class;
        List<String> values = Arrays.asList("asdf", "ASDF", "{}213njwsd123^&@!#(");

        for(String value : values) {
            Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> {
                Expression.validateTypeClass(enumeration, value);
            });
        }
    }

    @Test
    void requiredFieldValidatesWithGoodType() {

    }
}

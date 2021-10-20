package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class Get extends Expression {
    URL url = null;

    public Get(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        LOG.debug("Sending browser to page: " + url.toString());

        // Get the URL
        driver.get(url.toString());
        return null;
    }

    public Get parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required field
        assertRequiredField("get", String.class, tokens);

        String urlToken = tokens.get("get").toString();

        try {
            url = new URL(urlToken);
        } catch (MalformedURLException e) {
            throw new InvalidExpressionSyntax(e);
        }
        return this;
    }
}
package uk.co.spicule.magnesium_script;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Arrays;
import java.util.List;

public class TestJsonLexing {
    List<String> options = Arrays.asList("--headless", "true");
    WebDriver driver = new FirefoxDriver();
    Lexer lexer = null;

    @BeforeAll
    void setUp() {
        // Set up the driver
        FirefoxOptions driverOptions = new FirefoxOptions();
        options.forEach(driverOptions::addArguments);
        driver = new FirefoxDriver(driverOptions);

        lexer = new Lexer();
    }

    @AfterAll
    void tearDown() {
        lexer = null;
    }

    @Test
    void testLexerNoErrorWithValidJson() {

    }
}

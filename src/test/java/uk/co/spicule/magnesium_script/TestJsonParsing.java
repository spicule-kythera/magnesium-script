package uk.co.spicule.magnesium_script;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TestJsonParsing {
    List<String> options = Arrays.asList("--headless", "true");
    WebDriver driver = new FirefoxDriver();
    Parser parser = null;

    @BeforeAll
    void setUp() {
        // Set up the driver
        FirefoxOptions driverOptions = new FirefoxOptions();
        options.forEach(driverOptions::addArguments);
        driver = new FirefoxDriver(driverOptions);

        // Set up the parser
        parser = new Parser(new HashMap<>());
    }

    @AfterAll
    void tearDown() {
        driver = null;
        parser = null;
    }
}

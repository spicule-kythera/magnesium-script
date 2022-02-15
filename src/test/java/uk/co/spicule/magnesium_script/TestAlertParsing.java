package uk.co.spicule.magnesium_script;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import uk.co.spicule.magnesium_script.DriverFactory.BrowserType;
import uk.co.spicule.magnesium_script.expressions.Alert;
import uk.co.spicule.magnesium_script.expressions.Expression;

import java.util.HashMap;

class TestAlertParsing {
  // Static things
  static WebDriver driver = null;

  // Instance Things
  Alert operation = null;
  HashMap<String, Object> tokens = null;

  @BeforeAll
  static void seUpForAll() {
    DriverFactory factory = new DriverFactory(true);
    driver = factory.build(BrowserType.FIREFOX);
  }

  @AfterAll
  static void tearDownForAll() {
    //driver.close(); // TODO: driver.close() is currently broken in the Selenium library, a patch will be needed before uncommenting this
    driver = null;
  }

  @BeforeEach
  void setUp() {
    operation = new Alert(driver, null);
    tokens = new HashMap<>();
  }

  @AfterEach
  void tearDown() {
    operation = null;
    tokens = null;
  }

  @Test
  void noAlert() {
    Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> operation.parse(tokens));
  }

  @Test
  void invalidAlertType() {
    tokens.put("alert", "non-existant alert-type");

    Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> operation.parse(tokens));
  }

  @Test
  void validAcceptAlert() {
    tokens.put("alert", "Accept");

    Assertions.assertDoesNotThrow(() -> {
//      operation.parse(tokens);
    });
  }

  @Test
  void invalidTimeout() {
    tokens.put("alert", "accept");
    tokens.put("timeout", 10.1);

    Assertions.assertThrows(Expression.InvalidExpressionSyntax.class, () -> operation.parse(tokens));
  }

  @Test
  void validTimeout() {
    tokens.put("alert", "accept");
    tokens.put("timeout", 10);

    Assertions.assertDoesNotThrow(() -> {
      operation.parse(tokens);
    });
  }
}

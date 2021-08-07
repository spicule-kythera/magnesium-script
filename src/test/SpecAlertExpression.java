import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import java.util.HashMap;
import junit.framework.TestCase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.kytheralabs.magnesium_script.expressions.Alert;
import com.kytheralabs.magnesium_script.expressions.Expression;

public class SpecAlertExpression extends TestCase {
    WebDriver driver;

    @Before
    public void setUp() {
        driver = new FirefoxDriver();
    }

    @After
    public void tearDown() {
        driver.close();
    }

//    @Test(expected = Expression.InvalidExpressionSyntax.class)
    @Test
    public void parseWrongTypeFails() throws Expression.InvalidExpressionSyntax {
        Alert alert = new Alert(null);

        HashMap<String, Object> tokens = new HashMap<>();
        tokens.put("alert", "");

        alert.parse(tokens);
    }
}
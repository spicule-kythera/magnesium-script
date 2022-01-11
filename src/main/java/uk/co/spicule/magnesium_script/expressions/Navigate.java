package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;

import java.util.Map;

public class Navigate extends Expression {
    enum NavigateAction {
        FORWARD,
        BACK,
        REFRESH;

        private static NavigateAction stringToEnum(String name) throws InvalidExpressionSyntax {
            return NavigateAction.valueOf(Expression.validateTypeClass(NavigateAction.class, name));
        }
    }

    NavigateAction action = null;
    int repeat = 1;

    public Navigate(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        for (int i = 0; i < repeat; ++i) {
            switch(action) {
                case FORWARD:
                    driver.navigate().forward();
                    break;
                case BACK:
                    driver.navigate().back();
                    break;
                case REFRESH:
                    driver.navigate().refresh();
                    break;
                default:
                    throw new RuntimeException("FATAL: Invalid navigate-action:: " + action);
            }
        }

        return null;
    }

    public Expression parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        assertRequiredField("navigate", String.class, tokens);

        // Asset the optional fields
        if(assertOptionalField("repeat", Integer.class, tokens)) {
            repeat = Integer.parseInt(tokens.get("repeat").toString());
        }

        action = NavigateAction.stringToEnum(tokens.get("navigate").toString());

        return this;
    }
}

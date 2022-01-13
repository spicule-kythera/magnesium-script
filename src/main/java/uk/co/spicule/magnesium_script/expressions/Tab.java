package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class Tab extends Expression {
    class NoSuchTab extends RuntimeException {
        public NoSuchTab(Set<String> tabHandles, Integer index) {
            super("No such tab exists! Tried to open tab #" + index + " in the set of tabs: " + tabHandles + " but none was found!");
        }
    }

    enum TabAction {
        NEW,
        CLOSE,
        SWITCH_TO;

        private static TabAction stringToEnum(String name) throws InvalidExpressionSyntax {
            return TabAction.valueOf(Expression.validateTypeClass(TabAction.class, name));
        }
    }

    TabAction action = null;
    URL url = null;
    Integer index = null;

    public Tab(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        String focusedTab = null;
        Set<String> tabHandles = driver.getWindowHandles();
        LOG.debug("Tab Handles at execution-time of this tab-expression: " + tabHandles);

        switch(action) {
            case NEW:
                driver.switchTo().newWindow(WindowType.TAB);
                driver.get(url.toString());
                LOG.debug("Opened a new tab with handle: " + driver.getWindowHandle());
                break;
            case CLOSE:
                if(index < 0 || index >= tabHandles.size()) {
                    throw new NoSuchTab(tabHandles, index);
                }

                focusedTab = (String) tabHandles.toArray()[index];
                driver.switchTo().window(focusedTab);
                driver.close();
                tabHandles = driver.getWindowHandles();
                String parentTab = (String) tabHandles.toArray()[0];
                LOG.debug("closed " + focusedTab + ", switching back to parent tab: " + parentTab);
                driver.switchTo().window(parentTab);
                break;
            case SWITCH_TO:
                if(index < 0 || index >= tabHandles.size()) {
                    throw new NoSuchTab(tabHandles, index);
                }

                focusedTab = (String) tabHandles.toArray()[index];
                driver.switchTo().window(focusedTab);
                break;
            default:
                throw new RuntimeException("FATAL: invalid tab-action: `" + action + "`");
        }

        return null;
    }

    public Expression parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        assertRequiredField("tab", String.class, tokens);

        action = TabAction.stringToEnum(tokens.get("tab").toString());

        switch (action) {
            case NEW:
                return parseNewTab(tokens);
            case CLOSE:
                return parseCloseTab(tokens);
            case SWITCH_TO:
                return parseSwitchToTab(tokens);
            default:
                throw new RuntimeException("FATAL: invalid tab-action: `" + action + "`");
        }
    }

    private Expression parseNewTab(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        assertRequiredField("tab", String.class, tokens);

        try {
            url = new URL(tokens.get("url").toString());
        } catch (MalformedURLException e) {
            throw new InvalidExpressionSyntax("bad URL: " + e);
        }
        return this;
    }

    private Expression parseCloseTab(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        assertRequiredField("index", Integer.class, tokens);

        index = Integer.parseInt(tokens.get("index").toString());
        if(index < 0) {
            throw new InvalidExpressionSyntax("tab-index must be a positive integer value!");
        }

        return this;
    }

    private Expression parseSwitchToTab(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required fields
        assertRequiredField("index", Integer.class, tokens);

        index = Integer.parseInt(tokens.get("index").toString());
        if(index < 0) {
            throw new InvalidExpressionSyntax("tab-index must be a positive integer value!");
        }
        return this;
    }
}

package uk.co.spicule.magnesium_script;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import uk.co.spicule.magnesium_script.expressions.Expression;

import java.util.ArrayList;

public class DriverFactory {
    public enum BrowserType {
        FIREFOX,
        CHROME,
        EDGE;

        public static BrowserType stringToEnum(String name) throws Expression.InvalidExpressionSyntax {
            return BrowserType.valueOf(Expression.validateTypeClass(BrowserType.class, name));
        }
    }

    // Instance things
    ArrayList<String> options = new ArrayList<String>() {
        {
            add("--no-sandbox");
            add("--disable-gpu");
            add("--disable-extensions");
            add("--ignore-certificate-errors");
            add("--incognito");
            add("--window-size=1920,1080");
            add("--proxy-server='direct://");
            add("--proxy-bypass-list=*");
            add("--disable-background-networking");
            add("--safebrowsing-disable-auto-update");
            add("--disable-sync");
            add("--metrics-recording-only");
            add("--disable-default-apps");
            add("--no-first-run");
            add("--disable-setuid-sandbox");
            add("--hide-scrollbars");
            add("--no-zygote");
            add("--disable-notifications");
            add("--disable-logging");
            add("--disable-permissions-api");
        }
    };

    public DriverFactory(boolean headless) {
        if(headless) {
            options.add("--headless");
        }
    }

    public ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        this.options.forEach(options::addArguments);
        return options;
    }

    public FirefoxOptions buildFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        this.options.forEach(options::addArguments);
        return options;
    }

    public EdgeOptions buildEdgeOptions() {
        return new EdgeOptions();
    }

    public WebDriver build(BrowserType type) {
        switch(type) {
            case CHROME:
                return new ChromeDriver(buildChromeOptions());
            case EDGE:
                return new EdgeDriver(buildEdgeOptions());
            case FIREFOX:
                return new FirefoxDriver(buildFirefoxOptions());
            default:
                throw new RuntimeException("FATAL: Invalid browser-type: " + type);
        }
    }
}

package sandbox;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import uk.co.spicule.magnesium_script.MagnesiumScript;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Sandbox {
    public static void main(String[] args) {
        List<String> options = Arrays.asList("", "");
        FirefoxOptions driverOptions = new FirefoxOptions();
        options.forEach(driverOptions::addArguments);
        FirefoxDriver driver = new FirefoxDriver(driverOptions);

        MagnesiumScript interpreter = new MagnesiumScript(driver);
        URL scriptPath = MagnesiumScript.class.getClassLoader().getResource("sandbox.yaml");
        try {
            interpreter.interpret(Paths.get(scriptPath.toURI()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}

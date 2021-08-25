package uk.co.spicule.magnesium_script;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.spicule.magnesium_script.expressions.Expression;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MagnesiumScript {
    enum BrowserType {
        FIREFOX, CHROME, EDGE
    }

    private static WebDriver driver = null;
    protected static Logger LOG = null;
    static ArgumentParser parser = ArgumentParsers.newFor("MagnesiumScript").build()
            .description("A Domain-Specific-Language for creating expressive and simple automation scripts " +
                         "for Selenium-based web-agents.")
            .defaultHelp(true);

    public MagnesiumScript(WebDriver driver, Logger LOG) {
        MagnesiumScript.driver = driver;
        MagnesiumScript.LOG = LOG;
    }

    public List<String> getSnapshots() {
        return new ArrayList<>();
    }

    /**
     * Opens a file and performs a lexical analysis depending on the file
     *      type, then proceeds parse the contents iff no lexical errors occur.
     * @param filePath Path to the file to read
     * @throws IOException Occurs when the filePath was not found or could not be opened
     * @throws ParseException Occurs when the file specified fails to parse to its primitive type (e.g. JSON or YAML syntax error)
     * @throws Parser.InvalidExpressionType Occurs when an unknown expression is detected
     * @throws Expression.InvalidExpressionSyntax Occurs when a syntactic error is detected
     */
    public void interpret(Path filePath) throws IOException,
                                                ParseException,
                                                Parser.InvalidExpressionType,
                                                Expression.InvalidExpressionSyntax {
        Map<String, Object> tokens;
        String fileName = filePath.toString().toLowerCase();

        if(fileName.endsWith("json")) {
            tokens = Lexer.jsonToTokenTree(filePath);
        } else if(fileName.endsWith("yaml") || fileName.endsWith("yml")) {
            tokens = Lexer.yamlToTokenTree(filePath);
        } else {
            throw new InvalidArgumentException("Unsupported parsing for file with type: " + fileName);
        }

        interpret(tokens);
    }

    /**
     * Parses the raw tokens and runs the resulting program iff there are no syntax errors.
     * @param script The raw tokens to be parsed
     * @throws Parser.InvalidExpressionType Occurs when an unknown expression is detected
     * @throws Expression.InvalidExpressionSyntax Occurs when a syntactic error is detected
     */
    public void interpret(Map<String, Object> script) throws Parser.InvalidExpressionType,
                                                             Expression.InvalidExpressionSyntax {
        // Parse program
        Parser parser = new Parser(script);
        Program program = parser.parse(driver);

        // Run parsed program
        program.run();
    }

    public static void main(String[] args) throws ArgumentParserException,
                                                  IOException,
                                                  Parser.InvalidExpressionType,
                                                  ParseException,
                                                  Expression.InvalidExpressionSyntax {
        parser.addArgument("-f", "--file")
              .dest("filePath")
              .type(String.class)
              .help("Specify a file to parse");
        parser.addArgument("--headless")
              .dest("headless")
              .type(Boolean.class)
              .action(Arguments.storeTrue())
              .setDefault(false)
              .help("Enable the Selenium `--headless` mode for hte driver");
        parser.addArgument("-d", "--driver")
              .dest("driver")
              .type(String.class)
              .setDefault("firefox")
              .choices(Arrays.asList("firefox", "chrome", "edge"))
              .help("Specify the Selenium driver type to use. (Default: firefox)");
        WebDriver driver = null;

        try {
            // Get the Args
            Namespace parsedArgs = parser.parseArgs(args);
            BrowserType type = BrowserType.valueOf(parsedArgs.getString("driver").toUpperCase());
            boolean isHeadless = parsedArgs.getBoolean("headless");

            // Set the driver headless mode

            // Set up the driver
            switch (type) {
                case FIREFOX:
                    driver = new FirefoxDriver();
                    break;
                case CHROME:
                    driver = new ChromeDriver();
                    break;
                case EDGE:
                    driver = new EdgeDriver();
                    break;
                default:
                    throw new InvalidArgumentException("Internal error: unknown browser type `" + type + "`");
            }

            // Set up the logger
            Logger LOG = LoggerFactory.getLogger(MagnesiumScript.class);

            // Set up the interpreter
            MagnesiumScript interpreter = new MagnesiumScript(driver, LOG);

            // Parse and run the script
            Path filePath = FileSystems.getDefault().getPath(parsedArgs.getString("filePath"));
            interpreter.interpret(filePath);
        } catch (HelpScreenException e) {
            // Do nothing for help menu
        } finally {
            if(driver != null) {
                driver.close();
            }
        }
    }
}
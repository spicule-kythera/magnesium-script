package uk.co.spicule.magnesium_script;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.spicule.magnesium_script.DriverFactory.BrowserType;
import uk.co.spicule.magnesium_script.expressions.Break;
import uk.co.spicule.magnesium_script.expressions.Expression;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

public class MagnesiumScript {
    // Static things
    private static WebDriver driver = null;
    public final static Logger LOG = LoggerFactory.getLogger(MagnesiumScript.class);
    static ArgumentParser parser = ArgumentParsers.newFor("MagnesiumScript").build()
            .description("A Domain-Specific-Language for creating expressive and simple automation scripts " +
                         "for Selenium-based web-agents.")
            .defaultHelp(true);
    private static final int MAJOR = 0;
    private static final int MINOR = 3;
    private static final int PATCH = 1;

    public MagnesiumScript(WebDriver driver) {
        LOG.info(version());
        MagnesiumScript.driver = driver;
    }

    public static String version() {
        return "MagnesiumScript v" + MAJOR + "." + MINOR + "." + PATCH;
    }

    /**
     * Opens a file and performs a lexical analysis depending on the file
     *      type, then proceeds parse the contents iff no lexical errors occur.
     * @param filePath Path to the file to read
     * @throws IOException Occurs when the filePath was not found or could not be opened
     * @throws Parser.InvalidExpressionType Occurs when an unknown expression is detected
     * @throws Expression.InvalidExpressionSyntax Occurs when a syntactic error is detected
     */
    public Program interpret(Path filePath) throws IOException,
                                                   Parser.InvalidExpressionType,
                                                   Expression.InvalidExpressionSyntax,
                                                   Break.StopIterationException {
        Map<String, Object> tokens;
        String fileName = filePath.toString().toLowerCase();

        if(fileName.endsWith("yaml") || fileName.endsWith("yml") || fileName.endsWith("json")) {
            tokens = Lexer.textToTokenTree(filePath);
        } else {
            throw new InvalidArgumentException("Unsupported parsing for file with type: " + fileName);
        }

        return interpret(tokens);
    }

    /**
     * Parses the raw tokens and runs the resulting program iff there are no syntax errors.
     * @param script The raw tokens to be parsed
     * @throws Parser.InvalidExpressionType Occurs when an unknown expression is detected
     * @throws Expression.InvalidExpressionSyntax Occurs when a syntactic error is detected
     */
    public Program interpret(Map<String, Object> script) throws Parser.InvalidExpressionType,
                                                                Expression.InvalidExpressionSyntax,
                                                                Break.StopIterationException {
        // Parse program
        Parser parser = new Parser(script);
        Program program = parser.parse(driver);

        // Run parsed program
        return program.run();
    }

    public static void main(String[] args) throws ArgumentParserException,
                                                  IOException,
                                                  Parser.InvalidExpressionType,
                                                  Expression.InvalidExpressionSyntax,
                                                  Break.StopIterationException {
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
        parser.addArgument("-v", "--version")
                .dest("version")
                .type(Boolean.class)
                .setDefault(false)
                .help("Print the interpreter version then exit");
        WebDriver driver = null;

        try {
            // Get the Args
            Namespace parsedArgs = parser.parseArgs(args);
            BrowserType type = BrowserType.stringToEnum(parsedArgs.getString("driver"));
            DriverFactory factory = new DriverFactory(parsedArgs.getBoolean("headless"));

            // Build the driver
            driver = factory.build(type);

            // Process version command
            if(parsedArgs.getBoolean("version")) {
                LOG.info(MagnesiumScript.version());
                System.exit(0);
            }

            // Set up the interpreter
            MagnesiumScript interpreter = new MagnesiumScript(driver);

            // Parse and run the script
            Path filePath = FileSystems.getDefault().getPath(parsedArgs.getString("filePath"));
            Program program = interpreter.interpret(filePath);
            program.run();
        } catch (HelpScreenException e) {
            // Do nothing for help menu
        } finally {
            if(driver != null) {
                driver.close();
            }
        }
    }
}
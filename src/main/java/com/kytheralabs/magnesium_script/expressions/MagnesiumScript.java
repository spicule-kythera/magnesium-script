package com.kytheralabs.magnesium_script.expressions;

import com.kytheralabs.magnesium_script.expressions.expressions.Expression;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.util.Map;

class MagnesiumScript {
    static ArgumentParser parser = ArgumentParsers.newFor("MagnesiumScript").build()
            .description("A Domain-Specific-Language for creating expressive and simple automation scripts " +
                         "for Selenium-based web-agents.")
            .defaultHelp(true);

    public static void compile(String[] raw_args) throws IOException,
                                                  Parser.InvalidExpressionType,
            Expression.InvalidExpressionSyntax {
        // CLI Argument stuff
        parser.addArgument("filepath")
              .type(String.class)
              .help("Ms YAML file to compile");
        Namespace args = null;
        try {
            args = parser.parseArgs(raw_args);
        } catch(ArgumentParserException e) {
            parser.handleError(e);
            System.exit(-1);
        }

        WebDriver driver = null;
        try {
            // Driver stuff
            driver = new FirefoxDriver();

            // Program stuff
            String filepath = args.get("filepath");
            Lexer lexicon = new Lexer(filepath);
            Map<String, Object> tokens = lexicon.analyze();
            Parser parser = new Parser(tokens);
            Program program = parser.parse(driver);

            // Run the damn thing
            program.run();
        } finally {
            if(driver != null)
                driver.close();
        }
    }
}
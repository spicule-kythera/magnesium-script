package com.kytheralabs.magnesium_script;

import java.net.URL;
import java.util.Map;
import java.io.IOException;

import com.kytheralabs.magnesium_script.expressions.Expression;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

class Main  {
    public static void main(String[] argv) throws IOException, Parser.InvalidExpressionType, Expression.InvalidExpressionSyntax {
        // Driver stuff
        WebDriver driver = new FirefoxDriver();

        // Program stuff
        URL filepath = Main.class.getClassLoader().getResource("script.yml");
        Lexer lexicon = new Lexer(filepath.getFile());
        Map<String, String> tokens = lexicon.analyze();
        Parser parser = new Parser(tokens);
        Program program = parser.parse(driver);

        try {
            program.run();
        }finally {
            driver.close();
        }
    }
}
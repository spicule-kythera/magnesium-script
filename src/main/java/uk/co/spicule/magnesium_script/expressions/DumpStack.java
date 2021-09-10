package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Stack;

public class DumpStack extends Expression {
    String output = null;
    String tagName = null;
    Stack<String> stack = new Stack<>();

    public DumpStack(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        // TODO: Pass through stack, somehow

        try {
            // Create the directory
            new File(output).mkdir();

            // Dump each stack item to its own html file
            for (int i = 0; i < stack.size(); ++i) {
                // File variables
                String item = stack.pop();
                String absolutePath = output + i + "-" + tagName + ".html";

                // Create the file
                File file = new File(absolutePath);
                file.createNewFile();

                // Write the file content
                FileWriter writer = new FileWriter(absolutePath);
                writer.write(item);
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DumpStack parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        if(!tokens.containsKey("output")) {
            throw new InvalidExpressionSyntax("dump-stack");
        }

        // Process output
        output = tokens.get("output").toString();
        if(!output.endsWith("/")) {
            output += "/";
        }

        // Process tag
        tagName = tokens.getOrDefault("tag", "stack").toString();

        return this;
    }
}

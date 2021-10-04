package uk.co.spicule.magnesium_script.expressions;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class DumpStack extends Expression {
    String outputDir = null;
    String fileName = "{SERIAL_NUMBER}-{TAG_NAME}.html";
    Stack<String> stack = new Stack<>();

    public DumpStack(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        // Set up the destination path
        Path outputDir = Paths.get(this.outputDir.replaceFirst("~", System.getProperty("user.home")));
        File destination = new File(outputDir.toAbsolutePath().toString());

        LOG.debug("Dumping stack with " + stack.size() + " snapshots!");

        // Guarantee that the output dir exists
        try{
            FileUtils.createParentDirectories(destination);
        } catch(IOException e) {
            LOG.error("Failed to create snapshot-output dir: " + destination.getAbsolutePath());
            return null;
        }

        // Dump the stack to file contents
        for(int i = 0; i <= stack.size(); ++i) {
            String htmlPage = stack.pop();
            String fileName = String.copyValueOf(this.fileName.toCharArray())
                                    .replace("{SERIAL_NUMBER}", String.valueOf(i));
            String absPath = destination.getAbsolutePath() + File.separator + fileName;
            LOG.debug("Dumping snapshot #" + i + ": " + absPath);

            File file = new File(absPath);
            try {
                FileUtils.writeStringToFile(file, htmlPage, "UTF-8");
            } catch (IOException e) {
                LOG.error("Failed to dump stack item #" + i + ":");
                e.printStackTrace();
            }
        }

        return null;
    }

    public DumpStack parse(Map<String, Object> tokens) throws InvalidExpressionSyntax {
        // Assert the required and optional fields
        assertRequiredField("dump-stack", String.class, tokens);
        boolean hasTag = assertOptionalField("tag-name", String.class, tokens);

        // Process output
        outputDir = tokens.get("dump-stack").toString();

        // Populate tag-name if it exists
        String tagName = "snapshot";
        if(hasTag) {
            tagName = tokens.get("tag-name").toString();
        }
        fileName = fileName.replace("{TAG_NAME}", tagName);

        return this;
    }

    public void setStack(List<String> stack) {
        for(String item : stack) {
            this.stack.push(item);
        }
    }

    public final Stack<String> getStack() {
        return stack;
    }
}
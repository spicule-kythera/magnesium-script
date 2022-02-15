package uk.co.spicule.magnesium_script.expressions;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import uk.co.spicule.magnesium_script.Parser;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@SuppressWarnings("unused")
public class Screenshot extends Expression {
    String outputDir = null;

    public Screenshot(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Screenshot(WebDriver driver, Expression parent, String outputDir, @Nullable String tagName) {
        super(driver, parent);

        this.outputDir = outputDir;
        if(tagName == null) {
            tagName = "magnesium-script";
        }
    }

    public Object execute() {
        // Take the screenshot
        TakesScreenshot camera = (TakesScreenshot) driver;
        File source = camera.getScreenshotAs(OutputType.FILE);
        if(!source.setReadable(true)) {
            throw new RuntimeException("Failed to make file readable:" + source);
        }

        // Set up the destination path
        Path outputDir = Paths.get(this.outputDir.replaceFirst("~", System.getProperty("user.home")));
        File destination = new File(outputDir.toAbsolutePath() + File.separator + source.getName());

        LOG.debug("Taking a screenshot of page: " + driver.getCurrentUrl() + " and saving it to: " + destination);

        // Move the screenshot to the specified location
        try{
            FileUtils.moveFile(source, destination);
        } catch(IOException e) {
            LOG.error("Failed to move snapshot from " + source.getAbsolutePath() + " to " + destination.getAbsolutePath());
            e.printStackTrace();
        }

        return null;
    }

    public Screenshot parse(Map<String, Object> tokens) throws InvalidExpressionSyntax, Parser.InvalidExpressionType {
        // Assert the required and optional fields
        assertRequiredField("screenshot", String.class, tokens);
        boolean hasTag = assertOptionalField("tag-name", String.class, tokens);

        // Process output
        outputDir = tokens.get("screenshot").toString();

        // Populate tag-name if it exists
        String tagName = "magnesium-script";
        if(hasTag) {
            tagName = tokens.get("tag-name").toString();
        }

        return this;
    }
}
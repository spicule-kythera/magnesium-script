package uk.co.spicule.magnesium_script.expressions;

import org.openqa.selenium.WebDriver;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.co.spicule.magnesium_script.Parser;

import java.util.Map;

public class Screenshot extends Expression {
    String output = null;
    String tagName = null;

    public Screenshot(WebDriver driver, Expression parent) {
        super(driver, parent);
    }

    public Object execute() {
        LOG.debug("Resolving expression: `" + this.getClass() + "`!");

        throw new NotImplementedException();
//        // Create the filepath
//        File f = new File(output);
//        f.mkdirs();
//        String absolutePath = output + getDateString() + "-" + tagName + ".png";
//
//        // Take the screenshot
//        com.spicule.ashot.Screenshot s = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(driver);
//
//        try {
//            //This write operation is convoluted due to Java being crap and not writing files properly in DBFS the ImageIO way:
//            File tempFile = Files.createTempFile(null, null).toFile();
//
//            ImageIO.write(s.getImage(), "PNG", tempFile);
//            File dest = new File(absolutePath);
//            FileUtils.copyFile(tempFile, dest);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public Screenshot parse(Map<String, Object> tokens) throws InvalidExpressionSyntax, Parser.InvalidExpressionType {
        if(!tokens.containsKey("output")) {
            throw new InvalidExpressionSyntax("screenshot", "`screenshot` operation must contain `output` field to specify the path to write the image to.");
        }

        // Process output
        output = tokens.get("output").toString();
        if(!output.endsWith("/")) {
            output += "/";
        }

        // Process tag
        tagName = tokens.getOrDefault("tag", "screenshot").toString();

        return this;
    }
}

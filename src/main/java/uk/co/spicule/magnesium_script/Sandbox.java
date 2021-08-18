package uk.co.spicule.magnesium_script;

import org.yaml.snakeyaml.Yaml;
import uk.co.spicule.magnesium_script.expressions.Expression;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Sandbox {
    public static void main(String[] args) throws Parser.InvalidExpressionType, IOException, Expression.InvalidExpressionSyntax {
        String path = System.getProperty("user.home") + "/cs/work/yaml-scripts/demo.yml";
        MagnesiumScript.compile(new String[]{path});
        Yaml yaml = new Yaml();
        InputStream input = new FileInputStream(path);
        Map<String, Object> data = yaml.load(input);
        System.out.println(data.get("run"));
    }
}

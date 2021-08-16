package com.kytheralabs.magnesium_script.expressions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.kytheralabs.magnesium_script.expressions.expressions.Expression;
import org.yaml.snakeyaml.Yaml;

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

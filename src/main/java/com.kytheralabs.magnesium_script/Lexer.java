package com.kytheralabs.magnesium_script;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.io.IOException;
import org.yaml.snakeyaml.Yaml;

public class Lexer {
    private final Map<String, String> script;

    public Lexer(Map<String, String> script) {
        this.script = script;
    }

    public Lexer(String filepath) throws IOException {
        this.script = loadYAMLFile(filepath);
    }
    
    private Map<String, String> loadYAMLFile(String filepath) throws IOException {
        Yaml yaml = new Yaml();
        InputStream input = new FileInputStream(filepath);
        return yaml.load(input);
    }

    public Map<String, String> analyze() {
        return script;
    }
}
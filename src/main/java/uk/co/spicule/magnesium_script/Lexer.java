package uk.co.spicule.magnesium_script;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Lexer {
    private final Map<String, Object> script;

    public Lexer(Map<String, Object> script) {
        this.script = script;
    }

    public Lexer(String filepath) throws IOException {
        this.script = loadYAMLFile(filepath);
    }
    
    private Map<String, Object> loadYAMLFile(String filepath) throws IOException {
        Yaml yaml = new Yaml();
        InputStream input = new FileInputStream(filepath);
        return yaml.load(input);
    }

    public Map<String, Object> analyze() {
        return script;
    }
}
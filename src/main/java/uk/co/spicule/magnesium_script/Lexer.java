package uk.co.spicule.magnesium_script;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class Lexer {
    protected static Map<String, Object> jsonToTokenTree(Path filePath) throws IOException, ParseException {
        FileReader reader = new FileReader(filePath.toAbsolutePath().toString());
        Map<String, Object> map = (Map<String, Object>) new JSONParser().parse(reader);
        return new TreeMap<>(map);
    }

    protected static Map<String, Object> yamlToTokenTree(Path filePath) throws FileNotFoundException {
        FileInputStream input = new FileInputStream(filePath.toAbsolutePath().toString());
        Map<String, Object> map = new Yaml().load(input);
        return new TreeMap<>(map);
    }
}
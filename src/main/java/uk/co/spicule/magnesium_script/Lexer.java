package uk.co.spicule.magnesium_script;

import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class Lexer {
    protected static Map<String, Object> textToTokenTree(Path filePath) throws FileNotFoundException {
        FileInputStream input = new FileInputStream(filePath.toAbsolutePath().toString());

        Yaml yaml = new Yaml();
        yaml.;

        Map<String, Object> map = yaml.load(input);
        return new TreeMap<>(map);
    }
}
package github.tuquanrong.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

/**
 * tutu
 * 2021/1/22
 */
public class PropertiesConfig {
    private static final PropertiesConfig PROPERTIES_CONFIG = new PropertiesConfig();
    private final String configPath = "tutuRpc.properties";
    private Properties properties;

    PropertiesConfig() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(configPath);
        properties = new Properties();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(url.getFile()));
            properties.load(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PropertiesConfig getInstance() {
        return PROPERTIES_CONFIG;
    }

    public String get(String key) {
        return properties.getProperty(key).trim();
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}

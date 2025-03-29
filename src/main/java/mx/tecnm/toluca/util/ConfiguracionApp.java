package mx.tecnm.toluca.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfiguracionApp {
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = ConfiguracionApp.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
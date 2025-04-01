package mx.tecnm.toluca.util;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfiguracionApp {
    private static final Logger LOGGER = Logger.getLogger(ConfiguracionApp.class.getName());
    private static final Properties configuracion;

    static {
        configuracion = new Properties();
        try {
            configuracion.load(ConfiguracionApp.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "No se pudo cargar el archivo de configuración", e);
        }
    }

    public static String getProperty(String key) {
        return configuracion.getProperty(key);
    }
}
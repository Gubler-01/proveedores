package mx.tecnm.toluca.util;

public class ConfigUtilTest {
    public static void main(String[] args) {
        System.out.println("Database Service URL: " + ConfigUtil.getProperty("database.service.url"));
        System.out.println("File Server URL: " + ConfigUtil.getProperty("file.server.url"));
        System.out.println("Auth Service URL: " + ConfigUtil.getProperty("auth.service.url"));
    }
}
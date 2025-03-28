package mx.tecnm.toluca.test;

import mx.tecnm.toluca.config.Config;
import mx.tecnm.toluca.util.DatabaseClient;
import mx.tecnm.toluca.util.TokenManager;

import java.util.Map;

public class AuthenticationTest {
    public static void main(String[] args) {
        try {
            // Inicializar DatabaseClient y TokenManager
            DatabaseClient databaseClient = new DatabaseClient(Config.DATABASE_SERVICE_URL, null);
            TokenManager tokenManager = new TokenManager(databaseClient, Config.DATABASE_EMAIL, Config.DATABASE_PASSWORD);

            // Realizar autenticación
            System.out.println("Intentando autenticar con email: " + Config.DATABASE_EMAIL);
            Map<String, Object> authResponse = databaseClient.authenticate(Config.DATABASE_EMAIL, Config.DATABASE_PASSWORD);

            // Mostrar la respuesta
            System.out.println("Respuesta de autenticación: " + authResponse);

            // Obtener el token
            String token = tokenManager.getToken();
            System.out.println("Token JWT obtenido: " + token);

        } catch (Exception e) {
            System.err.println("Error durante la autenticación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
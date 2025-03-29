package mx.tecnm.toluca.service;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mx.tecnm.toluca.model.Credenciales;
import mx.tecnm.toluca.model.RespuestaLogin;
import mx.tecnm.toluca.util.ConfiguracionApp;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    public RespuestaLogin obtenerToken(Credenciales credenciales) {
        String baseUrl = ConfiguracionApp.getProperty("app.base.url");
        String loginEndpoint = ConfiguracionApp.getProperty("app.login.endpoint");
        
        Client client = ClientBuilder.newClient()
            .property("jersey.config.client.connectTimeout", 10000)
            .property("jersey.config.client.readTimeout", 10000);
        
        Jsonb jsonb = JsonbBuilder.create();
        
        try {
            Response response = client.target(baseUrl + loginEndpoint)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(credenciales));
            
            String responseBody = response.readEntity(String.class);
            
            // Log de depuración
            LOGGER.log(Level.INFO, "Respuesta del servidor: {0}", responseBody);
            
            if (response.getStatus() == 200) {
                // Parsear la respuesta JSON
                RespuestaLogin respuesta = jsonb.fromJson(responseBody, RespuestaLogin.class);
                return respuesta;
            } else {
                // Log de error
                LOGGER.log(Level.SEVERE, "Error de autenticación. Código: {0}, Mensaje: {1}", 
                    new Object[]{response.getStatus(), responseBody});
                
                throw new RuntimeException("Error de autenticación: " + responseBody);
            }
        } catch (Exception e) {
            // Log de excepción
            LOGGER.log(Level.SEVERE, "Excepción en autenticación", e);
            
            throw new RuntimeException("Error de conexión: " + e.getMessage(), e);
        } finally {
            client.close();
            try {
                jsonb.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error cerrando Jsonb", e);
            }
        }
    }
}
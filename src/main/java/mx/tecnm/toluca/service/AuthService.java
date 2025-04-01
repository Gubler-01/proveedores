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
            
            LOGGER.log(Level.INFO, "Respuesta del servidor: {0}", responseBody);
            
            if (response.getStatus() == 200) {
                RespuestaLogin respuesta = jsonb.fromJson(responseBody, RespuestaLogin.class);
                return respuesta;
            } else {
                LOGGER.log(Level.SEVERE, "Error de autenticación. Código: {0}, Mensaje: {1}", 
                    new Object[]{response.getStatus(), responseBody});
                
                throw new RuntimeException("Error de autenticación: " + responseBody);
            }
        } catch (Exception e) {
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

    public String getAutomaticToken() {
        String correo = ConfiguracionApp.getProperty("app.api.auth.correo");
        String password = ConfiguracionApp.getProperty("app.api.auth.password");
        Credenciales credenciales = new Credenciales(correo, password);

        try {
            RespuestaLogin respuesta = obtenerToken(credenciales);
            LOGGER.log(Level.INFO, "Token automático obtenido para {0}", correo);
            return respuesta.getToken();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener token automático", e);
            throw new RuntimeException("No se pudo obtener el token automático: " + e.getMessage());
        }
    }
}
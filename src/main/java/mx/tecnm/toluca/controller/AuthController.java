package mx.tecnm.toluca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.tecnm.toluca.config.Config;
import mx.tecnm.toluca.util.DatabaseClient;
import mx.tecnm.toluca.util.TokenManager;

import java.io.IOException;
import java.util.Map;

public class AuthController extends HttpServlet {
    private final DatabaseClient databaseClient;
    private final TokenManager tokenManager;

    public AuthController() {
        this.databaseClient = new DatabaseClient(Config.DATABASE_SERVICE_URL, null);
        this.tokenManager = new TokenManager(databaseClient, null, null);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/logout".equals(path)) {
            // Limpiar el token y la sesión
            tokenManager.clearToken();
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/login".equals(path)) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            try {
                // Autenticar con el servicio de base de datos
                Map<String, Object> authResponse = databaseClient.authenticate(username, password);
                String token = (String) authResponse.get("token");

                // Actualizar el TokenManager con el nuevo token
                tokenManager.setToken(token);

                // Almacenar el token y el email en la sesión
                request.getSession().setAttribute("token", token);
                request.getSession().setAttribute("email", username);

                response.sendRedirect(request.getContextPath() + "/dashboard");
            } catch (Exception e) {
                // Parsear el mensaje de error si es un JSON
                String errorMessage = e.getMessage();
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> errorResponse = mapper.readValue(errorMessage, Map.class);
                    errorMessage = (String) errorResponse.getOrDefault("message", "Error de autenticación");
                } catch (Exception parseEx) {
                    // Si no se puede parsear, usar el mensaje original
                }
                request.setAttribute("error", errorMessage);
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        }
    }
}
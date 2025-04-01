package mx.tecnm.toluca.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.tecnm.toluca.model.Credenciales;
import mx.tecnm.toluca.model.RespuestaLogin;
import mx.tecnm.toluca.service.AuthService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
    private AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");

        Credenciales credenciales = new Credenciales(correo, password);

        try {
            RespuestaLogin respuestaLogin = authService.obtenerToken(credenciales);
            
            HttpSession session = request.getSession();
            session.setAttribute("token", respuestaLogin.getToken());
            session.setAttribute("roles", respuestaLogin.getRoles());
            session.setAttribute("correo", respuestaLogin.getCorreo());

            LOGGER.log(Level.INFO, "Inicio de sesión exitoso para: {0}", correo);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en inicio de sesión", e);
            String errorMessage;

            // Personalizar el mensaje de error según la causa
            String exceptionMessage = e.getMessage() != null ? e.getMessage() : "";
            if (exceptionMessage.contains("ERR_NGROK_3200")) {
                errorMessage = "El servidor está offline. Por favor, intenta de nuevo más tarde.";
            } else if (exceptionMessage.contains("httpCode\":401")) {
                if (exceptionMessage.contains("Intentos restantes")) {
                    String remainingAttempts = extractRemainingAttempts(exceptionMessage);
                    errorMessage = "Credenciales inválidas. Intentos restantes: " + remainingAttempts;
                } else {
                    errorMessage = "Credenciales inválidas.";
                }
            } else {
                errorMessage = "Error al intentar iniciar sesión. Por favor, intenta de nuevo.";
            }

            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private String extractRemainingAttempts(String message) {
        try {
            int startIndex = message.indexOf("Intentos restantes: ") + "Intentos restantes: ".length();
            int endIndex = message.indexOf(",", startIndex);
            if (endIndex == -1) endIndex = message.length();
            return message.substring(startIndex, endIndex).trim();
        } catch (Exception e) {
            return "desconocidos"; // Valor por defecto si no se puede extraer
        }
    }
}
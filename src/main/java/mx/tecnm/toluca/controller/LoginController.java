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

            // Log de inicio de sesión exitoso
            LOGGER.log(Level.INFO, "Inicio de sesión exitoso para: {0}", correo);

            response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
        } catch (Exception e) {
            // Log de error
            LOGGER.log(Level.SEVERE, "Error en inicio de sesión", e);
            
            request.setAttribute("errorMessage", "Error de inicio de sesión: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
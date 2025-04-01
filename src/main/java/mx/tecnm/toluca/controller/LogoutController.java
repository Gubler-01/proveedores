package mx.tecnm.toluca.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Obtener la sesión actual, sin crear una nueva si no existe
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Invalidar la sesión para eliminar todos los atributos (como el token)
            session.invalidate();
        }
        // Redirigir al usuario a la página de login
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si se accede por POST, delegar al método doGet
        doGet(request, response);
    }
}
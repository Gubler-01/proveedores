package mx.tecnm.toluca.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.tecnm.toluca.model.User;
import mx.tecnm.toluca.service.AuthService;

import java.io.IOException;

public class AuthController extends HttpServlet {
    private final AuthService authService;

    public AuthController() {
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/logout".equals(path)) {
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
                User user = authService.authenticate(username, password);
                request.getSession().setAttribute("user", user);
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } catch (Exception e) {
                request.setAttribute("error", e.getMessage());
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        }
    }
}
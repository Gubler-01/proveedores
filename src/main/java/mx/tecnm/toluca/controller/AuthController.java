package mx.tecnm.toluca.controller;

import mx.tecnm.toluca.util.ConfigUtil;
import mx.tecnm.toluca.util.HttpClientUtil;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import org.json.JSONArray;

@WebServlet(name = "AuthController", urlPatterns = {"/auth/*"})
public class AuthController extends HttpServlet {
    private final String authServiceUrl;

    public AuthController() {
        this.authServiceUrl = ConfigUtil.getProperty("auth.service.url");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ruta no válida");
            return;
        }

        switch (pathInfo) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Obtener las credenciales del formulario
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
                request.setAttribute("error", "Correo y contraseña son requeridos");
                request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
                return;
            }

            // Crear el cuerpo JSON para la solicitud de autenticación
            JSONObject credentials = new JSONObject();
            credentials.put("correo", email);
            credentials.put("password", password);

            // Enviar la solicitud al servicio de autenticación
            String authResponse = HttpClientUtil.sendPostRequest(authServiceUrl, null, credentials.toString());
            JSONObject jsonResponse = new JSONObject(authResponse);

            // Extraer el token y los roles
            String token = jsonResponse.getString("token");
            JSONArray rolesArray = jsonResponse.getJSONArray("roles");
            String[] roles = new String[rolesArray.length()];
            for (int i = 0; i < rolesArray.length(); i++) {
                roles[i] = rolesArray.getString(i);
            }

            // Almacenar el token y los roles en la sesión
            HttpSession session = request.getSession();
            session.setAttribute("token", token);
            session.setAttribute("roles", roles);
            session.setAttribute("email", email);

            // Redirigir al dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (Exception e) {
            request.setAttribute("error", "Error al iniciar sesión: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Invalidar la sesión
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Redirigir al login
        response.sendRedirect(request.getContextPath() + "/");
    }
}
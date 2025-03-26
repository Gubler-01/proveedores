package mx.tecnm.toluca.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/dashboard", "/products/*", "/orders/*"})
public class JwtFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Método de inicialización (puede estar vacío)
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Excluir rutas públicas como el login
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        if (requestURI.startsWith(contextPath + "/auth/") ||
            requestURI.equals(contextPath + "/") ||
            requestURI.equals(contextPath + "/index.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        // Verificar si hay un token en la sesión
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("token") == null) {
            // Si no hay token, redirigir al login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/");
            return;
        }

        // Si el token existe, continuar con la solicitud
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Método de destrucción (puede estar vacío)
    }
}
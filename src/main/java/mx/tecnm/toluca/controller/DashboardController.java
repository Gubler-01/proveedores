package mx.tecnm.toluca.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.tecnm.toluca.service.ProductService;
import mx.tecnm.toluca.model.Product;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/dashboard")
@MultipartConfig // Añadimos esta anotación para habilitar el manejo de multipart
public class DashboardController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());
    private ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String token = (String) session.getAttribute("token");
        String correo = (String) session.getAttribute("correo");

        LOGGER.log(Level.INFO, "Iniciando doGet del DashboardController. Token: {0}, Correo: {1}",
                new Object[]{token, correo});

        if (token == null || correo == null) {
            LOGGER.log(Level.WARNING, "Token o correo no encontrados en la sesión. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            LOGGER.log(Level.INFO, "Obteniendo lista de productos...");
            List<Product> products = productService.getAllProducts(token);
            LOGGER.log(Level.INFO, "Productos obtenidos: {0}", products.size());
            request.setAttribute("products", products);

            Integer currentPage = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
            Integer totalPages = 1;
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);

            LOGGER.log(Level.INFO, "Redirigiendo a dashboard.jsp");
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar productos", e);
            request.setAttribute("error", "Error al cargar productos: " + e.getMessage());
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String token = (String) session.getAttribute("token");

        if (token == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            String action = request.getPart("action").getSubmittedFileName() == null
                    ? new String(request.getPart("action").getInputStream().readAllBytes()) : null;
            LOGGER.log(Level.INFO, "Acción recibida: {0}", action);

            switch (action != null ? action : "") {
                case "add":
                    productService.addProduct(request, token);
                    session.setAttribute("message", "Producto agregado exitosamente");
                    break;
                case "update":
                    productService.updateProduct(request, token);
                    session.setAttribute("message", "Producto actualizado exitosamente");
                    break;
                case "delete":
                    String id = new String(request.getPart("id").getInputStream().readAllBytes());
                    productService.deleteProduct(id, token);
                    session.setAttribute("message", "Producto eliminado exitosamente");
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Acción no válida recibida: {0}", action);
                    session.setAttribute("error", "Acción no válida");
            }
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Error de validación: {0}", e.getMessage());
            session.setAttribute("error", e.getMessage()); // Mostrar mensaje específico al usuario
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en la acción del POST", e);
            session.setAttribute("error", "Error: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}

package mx.tecnm.toluca.controller;

import jakarta.servlet.ServletException;
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
public class DashboardController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(DashboardController.class.getName());
    private ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String token = (String) session.getAttribute("token");
        String correo = (String) session.getAttribute("correo");

        if (token == null || correo == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            List<Product> products = productService.getAllProducts(token);
            request.setAttribute("products", products);

            Integer currentPage = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
            Integer totalPages = 1; // Ajustar si la API soporta paginación
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);

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

        String action = request.getParameter("action");
        try {
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
                    String id = request.getParameter("id");
                    productService.deleteProduct(id, token);
                    session.setAttribute("message", "Producto eliminado exitosamente");
                    break;
                default:
                    session.setAttribute("error", "Acción no válida");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error en la acción: " + action, e);
            session.setAttribute("error", "Error: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
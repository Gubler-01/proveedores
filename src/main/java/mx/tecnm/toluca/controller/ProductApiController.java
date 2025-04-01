package mx.tecnm.toluca.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.service.AuthService;
import mx.tecnm.toluca.service.ProductService;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/products")
public class ProductApiController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ProductApiController.class.getName());
    private ProductService productService = new ProductService();
    private AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Obtener token automáticamente
            String token = authService.getAutomaticToken();
            LOGGER.log(Level.INFO, "Solicitando productos mediante API con token automático");

            List<Product> products = productService.getAllProducts(token);

            // Convertir la lista de productos a JSON
            Jsonb jsonb = JsonbBuilder.create();
            String jsonResponse = jsonb.toJson(products);
            LOGGER.log(Level.INFO, "Productos obtenidos para API: {0}", products.size());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse);

            jsonb.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener productos para la API", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            String errorMessage = String.format("{\"error\": \"Error al obtener productos: %s\"}", e.getMessage());
            response.getWriter().write(errorMessage);
        }
    }
}
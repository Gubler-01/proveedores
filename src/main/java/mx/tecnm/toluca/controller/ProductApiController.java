package mx.tecnm.toluca.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.service.ProductService;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/products")
public class ProductApiController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ProductApiController.class.getName());
    private ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Obtener el token del encabezado Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Extraer el token después de "Bearer "
        }

        // Validar que se proporcionó un token
        if (token == null || token.isEmpty()) {
            LOGGER.log(Level.WARNING, "Solicitud a /api/products sin token de autorización");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Se requiere token de autorización\"}");
            return;
        }

        try {
            LOGGER.log(Level.INFO, "Solicitando productos mediante API con token: {0}", token);
            List<Product> products = productService.getAllProducts(token);

            // Convertir la lista de productos a JSON
            Jsonb jsonb = JsonbBuilder.create();
            String jsonResponse = jsonb.toJson(products);
            LOGGER.log(Level.INFO, "Productos obtenidos para API: {0}", products.size());

            // Configurar la respuesta
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK); // 200
            response.getWriter().write(jsonResponse);

            jsonb.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener productos para la API", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            response.setContentType("application/json");
            String errorMessage = String.format("{\"error\": \"Error al obtener productos: %s\"}", e.getMessage());
            response.getWriter().write(errorMessage);
        }
    }
}
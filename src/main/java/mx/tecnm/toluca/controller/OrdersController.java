package mx.tecnm.toluca.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import mx.tecnm.toluca.model.Order;
import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.service.OrderService;
import mx.tecnm.toluca.service.ProductService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "OrdersController", urlPatterns = {"/orders"})
@MultipartConfig
public class OrdersController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(OrdersController.class.getName());
    private final OrderService orderService = new OrderService();
    private final ProductService productService = new ProductService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String token = (String) session.getAttribute("token");
        String correo = (String) session.getAttribute("correo");

        LOGGER.log(Level.INFO, "Iniciando doGet del OrdersController. Correo: {0}", correo);

        if (token == null || correo == null) {
            LOGGER.log(Level.WARNING, "Token o correo no encontrados en la sesión. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        List<Order> orders = Collections.emptyList();
        List<Product> products = Collections.emptyList();
        String errorMessage = null;

        try {
            LOGGER.log(Level.INFO, "Obteniendo lista de órdenes...");
            orders = orderService.getAllOrders(token);
            LOGGER.log(Level.INFO, "Órdenes obtenidas: {0}", String.valueOf(orders.size()));
            for (Order order : orders) {
                LOGGER.log(Level.INFO, "Orden ID: {0}", order.getId());
            }

            LOGGER.log(Level.INFO, "Obteniendo lista de productos...");
            products = productService.getAllProducts(token);
            LOGGER.log(Level.INFO, "Productos obtenidos: {0}", String.valueOf(products.size()));

            // Serializar ítems a JSON para cada orden
            try (Jsonb jsonb = JsonbBuilder.create()) {
                for (Order order : orders) {
                    String itemsJson = jsonb.toJson(order.getItems());
                    LOGGER.log(Level.INFO, "Items JSON para orden {0}: {1}", new Object[]{order.getId(), itemsJson});
                    order.setItemsJson(itemsJson);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar órdenes o productos", e);
            errorMessage = "No se pudo conectar con la base de datos. Por favor, intenta de nuevo más tarde.";
        }

        request.setAttribute("orders", orders);
        request.setAttribute("products", products);
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }

        LOGGER.log(Level.INFO, "Redirigiendo a orders.jsp");
        request.getRequestDispatcher("/orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String token = (String) session.getAttribute("token");

        if (token == null) {
            LOGGER.log(Level.WARNING, "Token no encontrado en la sesión. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            String action = new String(request.getPart("action").getInputStream().readAllBytes());
            LOGGER.log(Level.INFO, "Acción recibida: {0}", action);

            if ("update".equals(action)) {
                orderService.updateOrder(request, token);
                session.setAttribute("message", "Estado de la orden actualizado exitosamente");
            } else {
                LOGGER.log(Level.WARNING, "Acción no válida recibida: {0}", action);
                throw new IllegalArgumentException("Acción no válida: " + action);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "Error de validación: {0}", e.getMessage());
            session.setAttribute("error", "Error de validación: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al procesar la solicitud POST", e);
            session.setAttribute("error", "Error al procesar la orden: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/orders");
    }
}
package mx.tecnm.toluca.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.tecnm.toluca.model.Order;
import mx.tecnm.toluca.service.AuthService;
import mx.tecnm.toluca.service.OrderService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/orders")
public class OrderApiController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(OrderApiController.class.getName());
    private OrderService orderService = new OrderService();
    private AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Obtener token automáticamente
            String token = authService.getAutomaticToken();
            LOGGER.log(Level.INFO, "Procesando orden con token automático");

            // Leer el cuerpo JSON de la solicitud
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            Jsonb jsonb = JsonbBuilder.create();
            Order order = jsonb.fromJson(requestBody.toString(), Order.class);
            LOGGER.log(Level.INFO, "Orden recibida: {0}", requestBody.toString());

            Order processedOrder = orderService.createOrder(order, token);

            String jsonResponse = jsonb.toJson(processedOrder);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(jsonResponse);

            LOGGER.log(Level.INFO, "Orden procesada y enviada como respuesta: {0}", jsonResponse);
            jsonb.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al procesar la orden en la API", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            String errorMessage = String.format("{\"error\": \"Error al procesar la orden: %s\"}", e.getMessage());
            response.getWriter().write(errorMessage);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Obtener token automáticamente
            String token = authService.getAutomaticToken();
            LOGGER.log(Level.INFO, "Solicitando órdenes mediante API con token automático");

            // Obtener todas las órdenes usando OrderService
            List<Order> orders = orderService.getAllOrders(token);

            // Convertir la lista de órdenes a JSON
            Jsonb jsonb = JsonbBuilder.create();
            String jsonResponse = jsonb.toJson(orders);
            LOGGER.log(Level.INFO, "Órdenes obtenidas para API: {0}", orders.size());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse);

            jsonb.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al obtener órdenes para la API", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            String errorMessage = String.format("{\"error\": \"Error al obtener órdenes: %s\"}", e.getMessage());
            response.getWriter().write(errorMessage);
        }
    }
}
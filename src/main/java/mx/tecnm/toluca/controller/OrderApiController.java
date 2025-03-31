package mx.tecnm.toluca.controller;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.tecnm.toluca.model.Order;
import mx.tecnm.toluca.service.OrderService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/api/orders")
public class OrderApiController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(OrderApiController.class.getName());
    private OrderService orderService = new OrderService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Obtener el token del encabezado Authorization
        String authHeader = request.getHeader("Authorization");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null || token.isEmpty()) {
            LOGGER.log(Level.WARNING, "Solicitud a /api/orders sin token de autorización");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Se requiere token de autorización\"}");
            return;
        }

        try {
            // Leer el cuerpo JSON de la solicitud
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            // Convertir el JSON a un objeto Order
            Jsonb jsonb = JsonbBuilder.create();
            Order order = jsonb.fromJson(requestBody.toString(), Order.class);
            LOGGER.log(Level.INFO, "Orden recibida: {0}", requestBody.toString());

            // Procesar la orden
            Order processedOrder = orderService.createOrder(order, token);

            // Devolver la orden procesada como JSON (sin arreglo)
            String jsonResponse = jsonb.toJson(processedOrder); // Solo el objeto, no List.of()
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
}
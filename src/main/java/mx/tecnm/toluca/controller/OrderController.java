package mx.tecnm.toluca.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.tecnm.toluca.model.Order;
import mx.tecnm.toluca.service.OrderService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "OrderController", urlPatterns = {"/api/orders"})
public class OrderController extends HttpServlet {
    private OrderService orderService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        orderService = new OrderService();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Configurar la respuesta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Obtener todas las órdenes
        List<Order> orders = orderService.getOrders(1, Integer.MAX_VALUE); // Obtener todas las órdenes

        // Convertir a JSON
        String json = gson.toJson(orders);

        // Enviar la respuesta
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Leer el cuerpo de la solicitud (JSON)
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        // Convertir el JSON a un objeto Order
        Order order = gson.fromJson(sb.toString(), Order.class);

        // Validar los datos
        if (order.getProductId() == null || order.getQuantity() <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Faltan datos requeridos: productId, quantity\"}");
            return;
        }

        // Establecer un estado inicial para la orden
        order.setStatus("PENDING");

        // Guardar la orden
        orderService.addOrder(order);

        // Responder con éxito
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(order));
    }
}
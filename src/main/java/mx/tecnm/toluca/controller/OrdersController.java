package mx.tecnm.toluca.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.tecnm.toluca.model.Order;
import mx.tecnm.toluca.service.OrderService;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/orders")
public class OrdersController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(OrdersController.class.getName());
    private OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String token = (String) session.getAttribute("token");
        String correo = (String) session.getAttribute("correo");

        LOGGER.log(Level.INFO, "Iniciando doGet del OrdersController. Token: {0}, Correo: {1}", 
            new Object[]{token, correo});

        if (token == null || correo == null) {
            LOGGER.log(Level.WARNING, "Token o correo no encontrados en la sesión. Redirigiendo a login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            LOGGER.log(Level.INFO, "Obteniendo lista de órdenes...");
            List<Order> orders = orderService.getAllOrders(token);
            LOGGER.log(Level.INFO, "Órdenes obtenidas: {0}", orders.size());
            request.setAttribute("orders", orders);

            LOGGER.log(Level.INFO, "Redirigiendo a orders.jsp");
            request.getRequestDispatcher("/orders.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al cargar órdenes", e);
            request.setAttribute("error", "Error al cargar órdenes: " + e.getMessage());
            request.getRequestDispatcher("/orders.jsp").forward(request, response);
        }
    }
}
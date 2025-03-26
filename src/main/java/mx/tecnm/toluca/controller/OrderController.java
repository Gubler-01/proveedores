package mx.tecnm.toluca.controller;

import mx.tecnm.toluca.model.Order;
import mx.tecnm.toluca.model.OrderAudit;
import mx.tecnm.toluca.repository.OrderRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "OrderController", urlPatterns = {"/orders", "/orders/accept", "/orders/reject", "/orders/update", "/orders/audit"})
public class OrderController extends HttpServlet {
    private OrderRepository orderRepository;

    @Override
    public void init() throws ServletException {
        orderRepository = new OrderRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getRequestURI().substring(request.getContextPath().length());

        if (pathInfo.equals("/orders") || pathInfo.equals("/orders/")) {
            handleShowOrders(request, response);
        } else if (pathInfo.startsWith("/orders/audit")) {
            handleShowAudit(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getRequestURI().substring(request.getContextPath().length());

        switch (pathInfo) {
            case "/orders/accept":
                handleAcceptOrder(request, response);
                break;
            case "/orders/reject":
                handleRejectOrder(request, response);
                break;
            case "/orders/update":
                handleUpdateOrder(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
        }
    }

    private void handleShowOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Obtener el número de página desde los parámetros (por defecto, página 1)
            String pageParam = request.getParameter("page");
            int page = pageParam != null ? Integer.parseInt(pageParam) : 1;
            int pageSize = 10; // Número de órdenes por página

            // Cargar las órdenes para la página actual
            List<Order> orders = orderRepository.findAll(request, page, pageSize);

            // Determinar cuáles órdenes han sido procesadas (tienen auditoría)
            Map<String, Boolean> hasBeenProcessed = new HashMap<>();
            for (Order order : orders) {
                List<OrderAudit> auditEntries = orderRepository.findAuditByOrderId(request, order.getId());
                hasBeenProcessed.put(order.getId(), !auditEntries.isEmpty());
            }

            // Calcular el total de páginas
            long totalOrders = orderRepository.count(request);
            int totalPages = (int) Math.ceil((double) totalOrders / pageSize);
            if (totalPages == 0) totalPages = 1;

            // Establecer atributos para la JSP
            request.setAttribute("orders", orders);
            request.setAttribute("hasBeenProcessed", hasBeenProcessed);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            // Redirigir a orders.jsp
            request.getRequestDispatcher("/WEB-INF/orders.jsp").forward(request, response);
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error al cargar las órdenes: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    private void handleAcceptOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String id = request.getParameter("id");

            // Buscar la orden existente
            List<Order> orders = orderRepository.findAll(request, 1, Integer.MAX_VALUE);
            Order order = orders.stream()
                    .filter(o -> o.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + id));

            // Verificar si la orden está en estado PENDING
            if (!"PENDING".equals(order.getStatus())) {
                throw new IllegalStateException("La orden no está en estado PENDING y no puede ser aceptada");
            }

            // Actualizar el estado a PROCESSING
            String oldStatus = order.getStatus();
            order.setStatus("PROCESSING");
            orderRepository.update(request, order);

            // Guardar un registro de auditoría
            orderRepository.saveAudit(request, id, "ACCEPT_ORDER", "Orden aceptada. Estado cambiado de " + oldStatus + " a PROCESSING");

            request.getSession().setAttribute("message", "Orden aceptada exitosamente");
            response.sendRedirect(request.getContextPath() + "/orders");
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error al aceptar la orden: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/orders");
        }
    }

    private void handleRejectOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String id = request.getParameter("id");

            // Buscar la orden existente
            List<Order> orders = orderRepository.findAll(request, 1, Integer.MAX_VALUE);
            Order order = orders.stream()
                    .filter(o -> o.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + id));

            // Verificar si la orden está en estado PENDING
            if (!"PENDING".equals(order.getStatus())) {
                throw new IllegalStateException("La orden no está en estado PENDING y no puede ser rechazada");
            }

            // Actualizar el estado a CANCELLED
            String oldStatus = order.getStatus();
            order.setStatus("CANCELLED");
            orderRepository.update(request, order);

            // Guardar un registro de auditoría
            orderRepository.saveAudit(request, id, "REJECT_ORDER", "Orden rechazada. Estado cambiado de " + oldStatus + " a CANCELLED");

            request.getSession().setAttribute("message", "Orden rechazada exitosamente");
            response.sendRedirect(request.getContextPath() + "/orders");
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error al rechazar la orden: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/orders");
        }
    }

    private void handleUpdateOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String id = request.getParameter("id");
            String status = request.getParameter("status");

            // Buscar la orden existente
            List<Order> orders = orderRepository.findAll(request, 1, Integer.MAX_VALUE);
            Order order = orders.stream()
                    .filter(o -> o.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada: " + id));

            // Verificar si el estado es válido
            if (!List.of("PENDING", "PROCESSING", "COMPLETED", "CANCELLED").contains(status)) {
                throw new IllegalArgumentException("Estado no válido: " + status);
            }

            // Actualizar el estado
            String oldStatus = order.getStatus();
            order.setStatus(status);
            orderRepository.update(request, order);

            // Guardar un registro de auditoría
            orderRepository.saveAudit(request, id, "UPDATE_STATUS", "Estado cambiado de " + oldStatus + " a " + status);

            request.getSession().setAttribute("message", "Estado de la orden actualizado exitosamente");
            response.sendRedirect(request.getContextPath() + "/orders");
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error al actualizar la orden: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/orders");
        }
    }

    private void handleShowAudit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String orderId = request.getParameter("orderId");
            String customId = request.getParameter("customId");

            // Obtener el historial de auditoría
            List<OrderAudit> auditEntries = orderRepository.findAuditByOrderId(request, orderId);

            // Establecer atributos para la JSP
            request.setAttribute("orderId", orderId);
            request.setAttribute("customId", customId);
            request.setAttribute("auditEntries", auditEntries);

            // Redirigir a audit.jsp
            request.getRequestDispatcher("/WEB-INF/audit.jsp").forward(request, response);
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error al cargar el historial de auditoría: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/orders");
        }
    }
}
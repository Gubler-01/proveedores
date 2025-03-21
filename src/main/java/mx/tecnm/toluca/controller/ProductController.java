package mx.tecnm.toluca.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import mx.tecnm.toluca.model.Order;
import mx.tecnm.toluca.model.OrderAudit;
import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.model.User;
import mx.tecnm.toluca.service.OrderService;
import mx.tecnm.toluca.service.ProductService;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MultipartConfig
public class ProductController extends HttpServlet {

    private ProductService productService;
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        super.init();
        productService = new ProductService();
        orderService = new OrderService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getServletPath();
        System.out.println("Solicitud GET recibida para: " + pathInfo);

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("Usuario no autenticado, redirigiendo a /index.jsp");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        if ("/dashboard".equals(pathInfo)) {
            int page = 1;
            int pageSize = 5;
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }

            System.out.println("Cargando productos en doGet para página: " + page);
            var products = productService.getProducts(page, pageSize);
            long totalProducts = productService.getProductCount();
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
            totalPages = totalPages == 0 ? 1 : totalPages;

            System.out.println("Productos encontrados en página " + page + ": " + products.size());
            System.out.println("Productos cargados: " + products.size() + ", Total páginas: " + totalPages);

            request.setAttribute("products", products);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        } else if ("/orders".equals(pathInfo)) {
            int page = 1;
            int pageSize = 5;
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }

            System.out.println("Cargando órdenes en doGet para página: " + page);
            var orders = orderService.getOrders(page, pageSize);
            long totalOrders = orderService.getOrderCount();
            int totalPages = (int) Math.ceil((double) totalOrders / pageSize);
            totalPages = totalPages == 0 ? 1 : totalPages;

            // Mapa para almacenar si cada orden ha sido aceptada o rechazada
            Map<String, Boolean> hasBeenProcessed = new HashMap<>();
            for (Order order : orders) {
                List<OrderAudit> auditEntries = orderService.getAuditByOrderId(order.getId());
                boolean processed = auditEntries.stream()
                        .anyMatch(audit -> "ACCEPTED".equals(audit.getAction()) || "REJECTED".equals(audit.getAction()));
                hasBeenProcessed.put(order.getId(), processed);
            }

            System.out.println("Órdenes encontradas en página " + page + ": " + orders.size());
            System.out.println("Órdenes cargadas: " + orders.size() + ", Total páginas: " + totalPages);

            request.setAttribute("orders", orders);
            request.setAttribute("hasBeenProcessed", hasBeenProcessed);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("/orders.jsp").forward(request, response);
        } else if ("/orders/audit".equals(pathInfo)) {
            String orderId = request.getParameter("orderId");
            if (orderId == null) {
                session.setAttribute("error", "ID de orden no proporcionado");
                response.sendRedirect(request.getContextPath() + "/orders");
                return;
            }

            List<OrderAudit> auditEntries = orderService.getAuditByOrderId(orderId);
            request.setAttribute("auditEntries", auditEntries);
            request.setAttribute("orderId", orderId);
            request.getRequestDispatcher("/audit.jsp").forward(request, response);
        } else {
            System.out.println("Ruta no reconocida: " + pathInfo);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getServletPath();
        System.out.println("Solicitud POST recibida para: " + pathInfo);

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        if ("/products/add".equals(pathInfo)) {
            Product product = new Product();
            product.setName(request.getParameter("name"));
            product.setDescription(request.getParameter("description"));
            
            // Validar precio
            double price;
            try {
                price = Double.parseDouble(request.getParameter("price"));
                if (price < 0) {
                    session.setAttribute("error", "El precio no puede ser negativo");
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "El precio debe ser un número válido");
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }
            product.setPrice(price);

            // Validar stock
            int stock;
            try {
                stock = Integer.parseInt(request.getParameter("stock"));
                if (stock < 0) {
                    session.setAttribute("error", "El stock no puede ser negativo");
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "El stock debe ser un número entero válido");
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }
            product.setStock(stock);

            Part imagePart = request.getPart("image");
            InputStream imageStream = null;
            String imageName = null;
            if (imagePart != null && imagePart.getSize() > 0) {
                imageStream = imagePart.getInputStream();
                imageName = imagePart.getSubmittedFileName();
            }

            productService.addProduct(product, imageStream, imageName);
            session.setAttribute("message", "Producto agregado exitosamente");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else if ("/products/update".equals(pathInfo)) {
            Product product = new Product();
            product.setId(request.getParameter("id"));
            product.setName(request.getParameter("name"));
            product.setDescription(request.getParameter("description"));

            // Validar precio
            double price;
            try {
                price = Double.parseDouble(request.getParameter("price"));
                if (price < 0) {
                    session.setAttribute("error", "El precio no puede ser negativo");
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "El precio debe ser un número válido");
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }
            product.setPrice(price);

            // Validar stock
            int stock;
            try {
                stock = Integer.parseInt(request.getParameter("stock"));
                if (stock < 0) {
                    session.setAttribute("error", "El stock no puede ser negativo");
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "El stock debe ser un número entero válido");
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }
            product.setStock(stock);

            Product existingProduct = productService.getProductById(product.getId());
            product.setImageId(existingProduct.getImageId());
            product.setHasPendingOrders(existingProduct.isHasPendingOrders());

            Part imagePart = request.getPart("image");
            InputStream imageStream = null;
            String imageName = null;
            if (imagePart != null && imagePart.getSize() > 0) {
                imageStream = imagePart.getInputStream();
                imageName = imagePart.getSubmittedFileName();
            }

            productService.updateProduct(product, imageStream, imageName);
            session.setAttribute("message", "Producto actualizado exitosamente");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else if ("/products/delete".equals(pathInfo)) {
            String id = request.getParameter("id");
            Product product = productService.getProductById(id);
            if (product == null) {
                session.setAttribute("error", "Producto no encontrado");
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }
            if (product.isHasPendingOrders()) {
                session.setAttribute("error", "No se puede eliminar el producto porque tiene órdenes pendientes");
            } else {
                productService.deleteProduct(id);
                session.setAttribute("message", "Producto eliminado exitosamente");
            }
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else if ("/orders/update".equals(pathInfo)) {
            String id = request.getParameter("id");
            String status = request.getParameter("status");

            try {
                List<Order> existingOrders = orderService.getOrders(1, Integer.MAX_VALUE);
                Order existingOrder = existingOrders.stream()
                        .filter(o -> o.getId().equals(id))
                        .findFirst()
                        .orElse(null);

                if (existingOrder == null) {
                    session.setAttribute("error", "Orden no encontrada");
                    response.sendRedirect(request.getContextPath() + "/orders");
                    return;
                }

                Order order = new Order();
                order.setId(id);
                order.setStatus(status);
                order.setCustomerId(existingOrder.getCustomerId());
                order.setItems(existingOrder.getItems());
                order.setOrderDate(existingOrder.getOrderDate());
                order.setSubtotal(existingOrder.getSubtotal());
                order.setTotal(existingOrder.getTotal());

                orderService.updateOrder(order);
                session.setAttribute("message", "Estado de la orden actualizado exitosamente");
            } catch (IllegalArgumentException e) {
                session.setAttribute("error", e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/orders");
        } else if ("/orders/accept".equals(pathInfo)) {
            String id = request.getParameter("id");

            try {
                orderService.acceptOrder(id);
                session.setAttribute("message", "Orden aceptada exitosamente");
            } catch (IllegalArgumentException e) {
                session.setAttribute("error", e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/orders");
        } else if ("/orders/reject".equals(pathInfo)) {
            String id = request.getParameter("id");

            try {
                orderService.rejectOrder(id);
                session.setAttribute("message", "Orden rechazada y cancelada exitosamente");
            } catch (IllegalArgumentException e) {
                session.setAttribute("error", e.getMessage());
            }
            response.sendRedirect(request.getContextPath() + "/orders");
        }
    }
}
//package mx.tecnm.toluca.service;
//
//import mx.tecnm.toluca.model.Order;
//import mx.tecnm.toluca.model.OrderAudit;
//import mx.tecnm.toluca.model.OrderItem;
//import mx.tecnm.toluca.model.Product;
//import mx.tecnm.toluca.repository.OrderRepository;
//
//import java.util.List;
//
//public class OrderService {
//    private final OrderRepository orderRepository;
//    private final ProductService productService;
//
//    public OrderService() {
//        this.orderRepository = new OrderRepository();
//        this.productService = new ProductService();
//    }
//
//    public void addOrder(Order order) throws IllegalArgumentException {
//        // Validar stock para cada ítem
//        for (OrderItem item : order.getItems()) {
//            Product product = productService.getProductById(item.getProductId());
//            if (product == null) {
//                throw new IllegalArgumentException("Producto no encontrado: " + item.getProductId());
//            }
//            if (product.getStock() < item.getQuantity()) {
//                throw new IllegalArgumentException("Stock insuficiente para el producto " + item.getProductId() + ". Stock disponible: " + product.getStock());
//            }
//            // Actualizar el precio unitario con el precio actual del producto
//            item.setUnitPrice(product.getPrice());
//        }
//
//        // Calcular subtotal y total
//        order.calculateTotals();
//
//        // Guardar la orden
//        orderRepository.save(order);
//
//        // Registrar en auditoría
//        orderRepository.saveAudit(order.getId(), "CREATED", "Orden creada con " + order.getItems().size() + " productos");
//    }
//
//    public List<Order> getOrders(int page, int pageSize) {
//        return orderRepository.findAll(page, pageSize);
//    }
//
//    public long getOrderCount() {
//        return orderRepository.count();
//    }
//
//    public void updateOrder(Order order) throws IllegalArgumentException {
//        List<Order> existingOrders = orderRepository.findAll(1, Integer.MAX_VALUE);
//        Order existingOrder = existingOrders.stream()
//                .filter(o -> o.getId().equals(order.getId()))
//                .findFirst()
//                .orElse(null);
//
//        if (existingOrder == null) {
//            throw new IllegalArgumentException("Orden no encontrada: " + order.getId());
//        }
//
//        if ("COMPLETED".equals(existingOrder.getStatus()) || "CANCELLED".equals(existingOrder.getStatus())) {
//            throw new IllegalArgumentException("No se puede modificar una orden que está completada o cancelada");
//        }
//
//        String previousStatus = existingOrder.getStatus();
//        order.setItems(existingOrder.getItems());
//        order.setOrderDate(existingOrder.getOrderDate());
//        order.setSubtotal(existingOrder.getSubtotal());
//        order.setTotal(existingOrder.getTotal());
//        orderRepository.update(order);
//
//        // Registrar en auditoría
//        orderRepository.saveAudit(order.getId(), "UPDATED", "Estado cambiado de " + previousStatus + " a " + order.getStatus());
//    }
//
//    public void acceptOrder(String orderId) throws IllegalArgumentException {
//        List<Order> existingOrders = orderRepository.findAll(1, Integer.MAX_VALUE);
//        Order order = existingOrders.stream()
//                .filter(o -> o.getId().equals(orderId))
//                .findFirst()
//                .orElse(null);
//
//        if (order == null) {
//            throw new IllegalArgumentException("Orden no encontrada: " + orderId);
//        }
//
//        if (!"PENDING".equals(order.getStatus())) {
//            throw new IllegalArgumentException("Solo se pueden aceptar órdenes en estado PENDIENTE");
//        }
//
//        // Validar stock nuevamente
//        for (OrderItem item : order.getItems()) {
//            Product product = productService.getProductById(item.getProductId());
//            if (product == null) {
//                throw new IllegalArgumentException("Producto no encontrado: " + item.getProductId());
//            }
//            if (product.getStock() < item.getQuantity()) {
//                throw new IllegalArgumentException("Stock insuficiente para el producto " + item.getProductId() + ". Stock disponible: " + product.getStock());
//            }
//        }
//
//        // Reducir el stock de cada producto
//        for (OrderItem item : order.getItems()) {
//            Product product = productService.getProductById(item.getProductId());
//            product.setStock(product.getStock() - item.getQuantity());
//            productService.updateProduct(product, null, null);
//        }
//
//        order.setStatus("ACCEPTED");
//        orderRepository.update(order);
//
//        // Registrar en auditoría
//        orderRepository.saveAudit(orderId, "ACCEPTED", "Orden aceptada");
//    }
//
//    public void rejectOrder(String orderId) throws IllegalArgumentException {
//        List<Order> existingOrders = orderRepository.findAll(1, Integer.MAX_VALUE);
//        Order order = existingOrders.stream()
//                .filter(o -> o.getId().equals(orderId))
//                .findFirst()
//                .orElse(null);
//
//        if (order == null) {
//            throw new IllegalArgumentException("Orden no encontrada: " + orderId);
//        }
//
//        if (!"PENDING".equals(order.getStatus())) {
//            throw new IllegalArgumentException("Solo se pueden rechazar órdenes en estado PENDIENTE");
//        }
//
//        // Cambiar el estado a REJECTED temporalmente para auditoría
//        order.setStatus("REJECTED");
//        orderRepository.update(order);
//
//        // Registrar en auditoría
//        orderRepository.saveAudit(orderId, "REJECTED", "Orden rechazada");
//
//        // Cambiar el estado a CANCELLED
//        order.setStatus("CANCELLED");
//        orderRepository.update(order);
//
//        // Registrar en auditoría el cambio a CANCELLED
//        orderRepository.saveAudit(orderId, "CANCELLED", "Orden cancelada automáticamente tras ser rechazada");
//    }
//
//    public List<OrderAudit> getAuditByOrderId(String orderId) {
//        return orderRepository.findAuditByOrderId(orderId);
//    }
//}
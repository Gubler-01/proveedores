package mx.tecnm.toluca.model;

import java.util.List;

public class Order {
    private String id;           // Número de orden (generado automáticamente)
    private String customerId;   // ID del cliente
    private List<OrderItem> items; // Lista de ítems en la orden
    private double subtotal;     // Subtotal (suma de precio * cantidad)
    private double total;        // Total (podría incluir impuestos o descuentos, por ahora igual a subtotal)
    private String status;       // Estado de la orden (por ejemplo, "Pendiente")
    private String createdAt;    // Fecha de creación (opcional)

    // Constructores
    public Order() {}
    
    public Order(String customerId, List<OrderItem> items) {
        this.customerId = customerId;
        this.items = items;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
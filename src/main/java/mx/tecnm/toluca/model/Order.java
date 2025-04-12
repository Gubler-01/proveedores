package mx.tecnm.toluca.model;

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.List;

public class Order {
    @JsonbProperty("_id")
    private String id;

    @JsonbProperty("customerId")
    private String customerId;

    @JsonbProperty("items")
    private List<OrderItem> items;

    @JsonbProperty("subtotal")
    private double subtotal;

    @JsonbProperty("total")
    private double total;

    @JsonbProperty("status")
    private String status;

    @JsonbProperty("createdAt")
    private String createdAt;

    @JsonbProperty("paymentMethod")
    private String paymentMethod; // New field

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
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
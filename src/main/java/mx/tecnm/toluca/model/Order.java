package mx.tecnm.toluca.model;

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.List;

public class Order {
    @JsonbProperty("_id")
    private String id;           // Ahora mapeado a "_id" como en Product.java

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
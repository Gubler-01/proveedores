package mx.tecnm.toluca.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private String id; // ID de MongoDB (_id)
    private String customId; // ID personalizado (ejemplo: ORD-00001)
    private String customerId;
    private List<OrderItem> items;
    private String status;
    private LocalDateTime orderDate;
    private double subtotal;
    private double total;

    // Constructor
    public Order() {
        this.items = new ArrayList<>();
        this.status = "PENDING";
        this.orderDate = LocalDateTime.now();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        calculateTotals();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public Date getOrderDateAsDate() {
        return Date.from(orderDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Método para calcular subtotal y total
    public void calculateTotals() {
        this.subtotal = items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
        this.total = this.subtotal; // Por ahora, total = subtotal; puedes agregar impuestos o descuentos aquí
    }
}
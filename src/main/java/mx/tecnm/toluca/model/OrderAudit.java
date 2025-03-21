package mx.tecnm.toluca.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class OrderAudit {
    private String id;
    private String orderId;
    private String action; // Ejemplo: "CREATED", "UPDATED", "ACCEPTED", "REJECTED"
    private String details; // Detalles de la acción (por ejemplo, "Estado cambiado de PENDING a ACCEPTED")
    private LocalDateTime timestamp;

    // Constructor vacío
    public OrderAudit() {}

    // Constructor con parámetros
    public OrderAudit(String orderId, String action, String details) {
        this.orderId = orderId;
        this.action = action;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Date getTimestampAsDate() {
        return Date.from(timestamp.atZone(ZoneId.systemDefault()).toInstant());
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
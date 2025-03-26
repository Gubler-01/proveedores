package mx.tecnm.toluca.model;

public class OrderItem {
    private String productId;
    private int quantity;
    private Double unitPrice;

    // Constructor
    public OrderItem() {
    }

    // Getters y Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getSubtotal() {
        return (unitPrice != null ? unitPrice : 0.0) * quantity;
    }
}
package mx.tecnm.toluca.model;

public class OrderItem {
    private String productId;  // ID del producto
    private int quantity;      // Cantidad solicitada
    private double price;      // Precio unitario (obtenido de ProductService)

    // Constructores
    public OrderItem() {}
    
    public OrderItem(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters y Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
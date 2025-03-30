package mx.tecnm.toluca.model;

import jakarta.json.bind.annotation.JsonbProperty;

public class Product {
    @JsonbProperty("_id")
    private String id;

    @JsonbProperty("name")
    private String name;

    @JsonbProperty("description")
    private String description;

    @JsonbProperty("price")
    private Double price;

    @JsonbProperty("stock")
    private Integer stock;

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
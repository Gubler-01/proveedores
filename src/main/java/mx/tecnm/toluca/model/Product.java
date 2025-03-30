package mx.tecnm.toluca.model;

import jakarta.json.bind.annotation.JsonbProperty;

public class Product {
    @JsonbProperty("_id")
    private String id;

    @JsonbProperty("nombre")
    private String nombre;

    @JsonbProperty("descripcion")
    private String descripcion;

    @JsonbProperty("precio")
    private Double precio;

    @JsonbProperty("stock")
    private Integer stock;

    @JsonbProperty("categoria")
    private String categoria;

    @JsonbProperty("status")
    private String status; // Podría ser boolean si la API lo prefiere, pero usaremos String por ahora

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
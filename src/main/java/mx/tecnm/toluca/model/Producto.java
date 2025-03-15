/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tecnm.toluca.model;

/**
 *
 * @author guble
 */

public class Producto {
    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private String imagenId; // Nuevo campo para el ID del archivo en GridFS

    public Producto() {}

    public Producto(String id, String nombre, String descripcion, double precio, int stock, String imagenId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.imagenId = imagenId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getImagenId() { return imagenId; }
    public void setImagenId(String imagenId) { this.imagenId = imagenId; }
}
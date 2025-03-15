/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tecnm.toluca.model;

/**
 *
 * @author guble
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdenCompra {
    private String id;
    private String proveedorId;
    private String proveedorNombre; // Nombre del proveedor
    private Date fecha;
    private List<ProductoOrden> productos;
    private String estado; // "Pendiente", "Procesada", "Cancelada"
    private int tiempoEntregaDias; // Tiempo estimado de entrega en días
    private String notas; // Notas o comentarios sobre la orden
    private double precioTotal; // Precio total de la orden

    public OrdenCompra() {
        this.productos = new ArrayList<>();
        this.fecha = new Date();
        this.estado = "Pendiente";
        this.tiempoEntregaDias = 0;
        this.notas = "";
        this.precioTotal = 0.0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(String proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<ProductoOrden> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoOrden> productos) {
        this.productos = productos;
        // Calcular el precio total cuando se actualizan los productos
        this.precioTotal = 0.0;
        for (ProductoOrden prod : productos) {
            this.precioTotal += prod.getPrecio() * prod.getCantidad();
        }
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getTiempoEntregaDias() {
        return tiempoEntregaDias;
    }

    public void setTiempoEntregaDias(int tiempoEntregaDias) {
        this.tiempoEntregaDias = tiempoEntregaDias;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }
}
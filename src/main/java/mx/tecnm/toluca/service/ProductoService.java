/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tecnm.toluca.service;

/**
 *
 * @author guble
 */
import mx.tecnm.toluca.dao.ProductoDAO;
import mx.tecnm.toluca.model.Producto;
import java.io.InputStream;
import java.util.List;

public class ProductoService {
    private ProductoDAO productoDAO;

    public ProductoService() {
        this.productoDAO = new ProductoDAO();
    }

    public void registrarProducto(Producto producto, InputStream imagenStream, String fileName) throws Exception {
        if (producto.getPrecio() < 0 || producto.getStock() < 0) {
            throw new Exception("Precio y stock no pueden ser negativos.");
        }
        productoDAO.registrarProducto(producto, imagenStream, fileName);
    }

    public void actualizarProducto(Producto producto, InputStream imagenStream, String fileName) throws Exception {
        if (producto.getPrecio() < 0 || producto.getStock() < 0) {
            throw new Exception("Precio y stock no pueden ser negativos.");
        }
        if (productoDAO.buscarPorId(producto.getId()) == null) {
            throw new Exception("El producto no existe.");
        }
        productoDAO.actualizarProducto(producto, imagenStream, fileName);
    }

    public void eliminarProducto(String id) throws Exception {
        if (productoDAO.buscarPorId(id) == null) {
            throw new Exception("El producto no existe.");
        }
        productoDAO.eliminarProducto(id);
    }

    public List<Producto> listarProductos() {
        return productoDAO.listarProductos();
    }

    public InputStream obtenerImagen(String imagenId) {
        return productoDAO.obtenerImagen(imagenId);
    }
}
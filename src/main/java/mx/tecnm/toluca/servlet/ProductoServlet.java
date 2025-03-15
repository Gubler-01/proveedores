/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tecnm.toluca.servlet;

/**
 *
 * @author guble
 */
import mx.tecnm.toluca.model.Producto;
import mx.tecnm.toluca.service.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 5,  // 5MB
    maxRequestSize = 1024 * 1024 * 5 // 5MB
)
public class ProductoServlet extends HttpServlet {
    private ProductoService productoService;

    @Override
    public void init() throws ServletException {
        productoService = new ProductoService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        if ("listar".equals(accion)) {
            request.setAttribute("productos", productoService.listarProductos());
            request.getRequestDispatcher("/jsp/listarProductos.jsp").forward(request, response);
        } else if ("editar".equals(accion)) {
            String id = request.getParameter("id");
            Producto producto = productoService.listarProductos().stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            if (producto == null) {
                request.setAttribute("error", "Producto no encontrado con ID: " + id);
                request.setAttribute("productos", productoService.listarProductos());
                request.getRequestDispatcher("/jsp/listarProductos.jsp").forward(request, response);
            } else {
                request.setAttribute("producto", producto);
                request.getRequestDispatcher("/jsp/actualizarProducto.jsp").forward(request, response);
            }
        } else {
            request.getRequestDispatcher("/jsp/registrarProducto.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        try {
            if ("registrar".equals(accion)) {
                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");
                double precio = Double.parseDouble(request.getParameter("precio"));
                int stock = Integer.parseInt(request.getParameter("stock"));

                Part filePart = request.getPart("imagen");
                String fileName = filePart.getSubmittedFileName();
                InputStream imagenStream = filePart.getInputStream();

                Producto producto = new Producto(null, nombre, descripcion, precio, stock, null);
                productoService.registrarProducto(producto, imagenStream, fileName);
                request.setAttribute("mensaje", "Producto registrado con éxito.");
                request.setAttribute("showPopup", true);
                request.setAttribute("accion", "listar");
            } else if ("actualizar".equals(accion)) {
                String id = request.getParameter("id");
                String nombre = request.getParameter("nombre");
                String descripcion = request.getParameter("descripcion");
                double precio = Double.parseDouble(request.getParameter("precio"));
                int stock = Integer.parseInt(request.getParameter("stock"));
                String imagenId = request.getParameter("imagenActual");

                InputStream imagenStream = null;
                String newFileName = null;
                Part filePart = request.getPart("imagen");
                if (filePart != null && filePart.getSize() > 0) {
                    newFileName = filePart.getSubmittedFileName();
                    imagenStream = filePart.getInputStream();
                }

                Producto producto = new Producto(id, nombre, descripcion, precio, stock, imagenId);
                productoService.actualizarProducto(producto, imagenStream, newFileName);
                request.setAttribute("mensaje", "Producto actualizado con éxito.");
                request.setAttribute("showPopup", true);
                request.setAttribute("accion", "listar");
            } else if ("eliminar".equals(accion)) {
                String id = request.getParameter("id");
                productoService.eliminarProducto(id);
                request.setAttribute("mensaje", "Producto eliminado con éxito.");
                request.setAttribute("showPopup", true);
                // Redirigir directamente a listarProductos.jsp con los atributos
                request.setAttribute("productos", productoService.listarProductos());
                request.getRequestDispatcher("/jsp/listarProductos.jsp").forward(request, response);
                return; // Salir del método para evitar doGet
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
            request.setAttribute("accion", "listar");
        }
        doGet(request, response); // Solo para registrar y actualizar
    }
}
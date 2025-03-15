/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tecnm.toluca.servlet;

/**
 *
 * @author guble
 */
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import mx.tecnm.toluca.model.OrdenCompra;
import mx.tecnm.toluca.model.Producto;
import mx.tecnm.toluca.model.ProductoOrden;
import mx.tecnm.toluca.util.MongoDBConnection;
import org.bson.Document;
import org.bson.types.ObjectId;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdenCompraServlet extends HttpServlet {

    private MongoCollection<Document> ordenesCollection;
    private MongoCollection<Document> productosCollection;

    @Override
    public void init() throws ServletException {
        ordenesCollection = MongoDBConnection.getDatabase().getCollection("ordenes");
        productosCollection = MongoDBConnection.getDatabase().getCollection("productos");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                listarOrdenes(request, response);
                break;
            case "editar":
                cargarOrdenParaEditar(request, response);
                break;
            default:
                listarOrdenes(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        switch (accion) {
            case "registrar":
                registrarOrden(request, response);
                break;
            case "actualizar":
                actualizarOrden(request, response);
                break;
            case "eliminar":
                eliminarOrden(request, response);
                break;
        }
    }

    private void listarOrdenes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<OrdenCompra> ordenes = new ArrayList<>();
        for (Document doc : ordenesCollection.find()) {
            OrdenCompra orden = new OrdenCompra();
            orden.setId(doc.getString("id"));
            orden.setProveedorId(doc.getString("proveedorId"));
            orden.setProveedorNombre(doc.getString("proveedorNombre"));
            orden.setFecha(doc.getDate("fecha"));
            orden.setEstado(doc.getString("estado"));
            orden.setTiempoEntregaDias(doc.getInteger("tiempoEntregaDias"));
            orden.setNotas(doc.getString("notas"));
            orden.setPrecioTotal(doc.getDouble("precioTotal"));
            List<Document> productosDocs = (List<Document>) doc.get("productos");
            List<ProductoOrden> productos = new ArrayList<>();
            for (Document prodDoc : productosDocs) {
                ProductoOrden prod = new ProductoOrden();
                prod.setProductoId(prodDoc.getString("productoId"));
                prod.setNombre(prodDoc.getString("nombre"));
                prod.setPrecio(prodDoc.getDouble("precio"));
                prod.setCantidad(prodDoc.getInteger("cantidad"));
                productos.add(prod);
            }
            orden.setProductos(productos);
            ordenes.add(orden);
        }
        request.setAttribute("ordenes", ordenes);
        request.getRequestDispatcher("/jsp/listarOrdenes.jsp").forward(request, response);
    }

    private void registrarOrden(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        OrdenCompra orden = new OrdenCompra();
        orden.setProveedorId("proveedor1");
        orden.setProveedorNombre("Proveedor 1"); // Simulado, se integrará con ERP más adelante
        orden.setFecha(new Date());
        orden.setEstado("Pendiente");
        orden.setTiempoEntregaDias(Integer.parseInt(request.getParameter("tiempoEntregaDias")));
        orden.setNotas(request.getParameter("notas"));

        // Generar un ID personalizado (ord-#deorden)
        long numeroOrdenes = ordenesCollection.countDocuments();
        String nuevoId = "ord-" + (numeroOrdenes + 1);
        orden.setId(nuevoId);

        // Obtener productos seleccionados
        String[] productoIds = request.getParameterValues("productoId");
        String[] cantidades = request.getParameterValues("cantidad");

        List<ProductoOrden> productosOrden = new ArrayList<>();
        boolean stockSuficiente = true;
        String mensajeError = "";

        if (productoIds != null && cantidades != null) {
            for (int i = 0; i < productoIds.length; i++) {
                Document productoDoc = productosCollection.find(Filters.eq("_id", new ObjectId(productoIds[i]))).first();
                if (productoDoc != null) {
                    int stockDisponible = productoDoc.getInteger("stock", 0);
                    int cantidadSolicitada = Integer.parseInt(cantidades[i]);
                    if (stockDisponible < cantidadSolicitada) {
                        stockSuficiente = false;
                        mensajeError += "No hay suficiente stock para " + productoDoc.getString("nombre") + ". Disponible: " + stockDisponible + ", Solicitado: " + cantidadSolicitada + "<br>";
                    } else {
                        ProductoOrden prod = new ProductoOrden();
                        prod.setProductoId(productoIds[i]);
                        prod.setNombre(productoDoc.getString("nombre"));
                        prod.setPrecio(productoDoc.getDouble("precio"));
                        prod.setCantidad(cantidadSolicitada);
                        productosOrden.add(prod);
                    }
                }
            }
        }

        if (!stockSuficiente) {
            request.setAttribute("error", mensajeError);
            request.getRequestDispatcher("/jsp/registrarOrden.jsp").forward(request, response);
            return;
        }

        orden.setProductos(productosOrden);

        // Guardar la orden
        Document ordenDoc = new Document("id", orden.getId())
                .append("proveedorId", orden.getProveedorId())
                .append("proveedorNombre", orden.getProveedorNombre())
                .append("fecha", orden.getFecha())
                .append("estado", orden.getEstado())
                .append("tiempoEntregaDias", orden.getTiempoEntregaDias())
                .append("notas", orden.getNotas())
                .append("precioTotal", orden.getPrecioTotal());
        List<Document> productosDocs = new ArrayList<>();
        for (ProductoOrden prod : orden.getProductos()) {
            productosDocs.add(new Document("productoId", prod.getProductoId())
                    .append("nombre", prod.getNombre())
                    .append("precio", prod.getPrecio())
                    .append("cantidad", prod.getCantidad()));
            // Descontar del stock
            productosCollection.updateOne(
                Filters.eq("_id", new ObjectId(prod.getProductoId())),
                Updates.inc("stock", -prod.getCantidad())
            );
        }
        ordenDoc.append("productos", productosDocs);

        ordenesCollection.insertOne(ordenDoc);
        request.setAttribute("mensaje", "Orden registrada con éxito");
        request.setAttribute("showPopup", true);
        listarOrdenes(request, response);
    }

    private void cargarOrdenParaEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        Document doc = ordenesCollection.find(Filters.eq("id", id)).first();
        if (doc != null) {
            OrdenCompra orden = new OrdenCompra();
            orden.setId(doc.getString("id"));
            orden.setProveedorId(doc.getString("proveedorId"));
            orden.setProveedorNombre(doc.getString("proveedorNombre"));
            orden.setFecha(doc.getDate("fecha"));
            orden.setEstado(doc.getString("estado"));
            orden.setTiempoEntregaDias(doc.getInteger("tiempoEntregaDias"));
            orden.setNotas(doc.getString("notas"));
            orden.setPrecioTotal(doc.getDouble("precioTotal"));
            List<Document> productosDocs = (List<Document>) doc.get("productos");
            List<ProductoOrden> productos = new ArrayList<>();
            for (Document prodDoc : productosDocs) {
                ProductoOrden prod = new ProductoOrden();
                prod.setProductoId(prodDoc.getString("productoId"));
                prod.setNombre(prodDoc.getString("nombre"));
                prod.setPrecio(prodDoc.getDouble("precio"));
                prod.setCantidad(prodDoc.getInteger("cantidad"));
                productos.add(prod);
            }
            orden.setProductos(productos);
            request.setAttribute("orden", orden);

            // Cargar lista de productos disponibles (aunque no se usará para modificar)
            List<Producto> productosDisponibles = new ArrayList<>();
            for (Document prodDoc : productosCollection.find()) {
                Producto p = new Producto();
                p.setId(prodDoc.getObjectId("_id").toString());
                p.setNombre(prodDoc.getString("nombre"));
                p.setPrecio(prodDoc.getDouble("precio"));
                productosDisponibles.add(p);
            }
            request.setAttribute("productosDisponibles", productosDisponibles);
            request.getRequestDispatcher("/jsp/actualizarOrden.jsp").forward(request, response);
        }
    }

    private void actualizarOrden(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        String estado = request.getParameter("estado");

        // Recuperar la orden existente
        Document doc = ordenesCollection.find(Filters.eq("id", id)).first();
        if (doc == null) {
            request.setAttribute("error", "Orden no encontrada");
            listarOrdenes(request, response);
            return;
        }

        OrdenCompra orden = new OrdenCompra();
        orden.setId(doc.getString("id"));
        orden.setProveedorId(doc.getString("proveedorId"));
        orden.setProveedorNombre(doc.getString("proveedorNombre"));
        orden.setFecha(doc.getDate("fecha"));
        orden.setEstado(estado);
        orden.setTiempoEntregaDias(doc.getInteger("tiempoEntregaDias"));
        orden.setNotas(doc.getString("notas"));
        orden.setPrecioTotal(doc.getDouble("precioTotal"));

        List<Document> productosDocs = (List<Document>) doc.get("productos");
        List<ProductoOrden> productos = new ArrayList<>();
        for (Document prodDoc : productosDocs) {
            ProductoOrden prod = new ProductoOrden();
            prod.setProductoId(prodDoc.getString("productoId"));
            prod.setNombre(prodDoc.getString("nombre"));
            prod.setPrecio(prodDoc.getDouble("precio"));
            prod.setCantidad(prodDoc.getInteger("cantidad"));
            productos.add(prod);
        }
        orden.setProductos(productos);

        // Actualizar solo el estado
        Document ordenDoc = new Document("id", orden.getId())
                .append("proveedorId", orden.getProveedorId())
                .append("proveedorNombre", orden.getProveedorNombre())
                .append("fecha", orden.getFecha())
                .append("estado", orden.getEstado())
                .append("tiempoEntregaDias", orden.getTiempoEntregaDias())
                .append("notas", orden.getNotas())
                .append("precioTotal", orden.getPrecioTotal());
        productosDocs = new ArrayList<>();
        for (ProductoOrden prod : orden.getProductos()) {
            productosDocs.add(new Document("productoId", prod.getProductoId())
                    .append("nombre", prod.getNombre())
                    .append("precio", prod.getPrecio())
                    .append("cantidad", prod.getCantidad()));
        }
        ordenDoc.append("productos", productosDocs);

        ordenesCollection.replaceOne(Filters.eq("id", id), ordenDoc);
        request.setAttribute("mensaje", "Orden actualizada con éxito");
        request.setAttribute("showPopup", true);
        listarOrdenes(request, response);
    }

    private void eliminarOrden(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        ordenesCollection.deleteOne(Filters.eq("id", id));
        request.setAttribute("mensaje", "Orden eliminada con éxito");
        request.setAttribute("showPopup", true);
        listarOrdenes(request, response);
    }
}
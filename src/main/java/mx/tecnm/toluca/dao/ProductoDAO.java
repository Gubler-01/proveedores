/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tecnm.toluca.dao;

/**
 *
 * @author guble
 */
import com.mongodb.client.MongoCollection;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import mx.tecnm.toluca.model.Producto;
import mx.tecnm.toluca.util.MongoDBConnection;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    private MongoCollection<Document> collection;
    private GridFSBucket gridFSBucket;

    public ProductoDAO() {
        this.collection = MongoDBConnection.getDatabase().getCollection("productos");
        this.gridFSBucket = GridFSBuckets.create(MongoDBConnection.getDatabase());
    }

    public void registrarProducto(Producto producto, InputStream imagenStream, String fileName) {
        // Generar ID si no está presente
        if (producto.getId() == null || producto.getId().isEmpty()) {
            String newId = generarNuevoId();
            producto.setId(newId);
        }

        GridFSUploadOptions options = new GridFSUploadOptions()
                .metadata(new Document("type", "image"));
        ObjectId fileId = gridFSBucket.uploadFromStream(fileName, imagenStream, options);
        String imagenId = fileId.toHexString();

        Document doc = new Document("id", producto.getId())
                .append("nombre", producto.getNombre())
                .append("descripcion", producto.getDescripcion())
                .append("precio", producto.getPrecio())
                .append("stock", producto.getStock())
                .append("imagenId", imagenId);
        collection.insertOne(doc);
    }

    public void actualizarProducto(Producto producto, InputStream imagenStream, String fileName) {
        String imagenId = producto.getImagenId();
        if (imagenStream != null) {
            if (imagenId != null) {
                gridFSBucket.delete(new ObjectId(imagenId));
            }
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .metadata(new Document("type", "image"));
            ObjectId fileId = gridFSBucket.uploadFromStream(fileName, imagenStream, options);
            imagenId = fileId.toHexString();
        }

        Document doc = new Document("nombre", producto.getNombre())
                .append("descripcion", producto.getDescripcion())
                .append("precio", producto.getPrecio())
                .append("stock", producto.getStock())
                .append("imagenId", imagenId);
        collection.updateOne(Filters.eq("id", producto.getId()), new Document("$set", doc));
    }

    public void eliminarProducto(String id) {
        Document doc = collection.find(Filters.eq("id", id)).first();
        if (doc != null) {
            String imagenId = doc.getString("imagenId");
            if (imagenId != null) {
                gridFSBucket.delete(new ObjectId(imagenId));
            }
        }
        collection.deleteOne(Filters.eq("id", id));
    }

    public List<Producto> listarProductos() {
        List<Producto> productos = new ArrayList<>();
        for (Document doc : collection.find()) {
            Producto p = new Producto(
                doc.getString("id"),
                doc.getString("nombre"),
                doc.getString("descripcion"),
                doc.getDouble("precio"),
                doc.getInteger("stock"),
                doc.getString("imagenId")
            );
            productos.add(p);
        }
        return productos;
    }

    public Producto buscarPorId(String id) {
        Document doc = collection.find(Filters.eq("id", id)).first();
        if (doc != null) {
            return new Producto(
                doc.getString("id"),
                doc.getString("nombre"),
                doc.getString("descripcion"),
                doc.getDouble("precio"),
                doc.getInteger("stock"),
                doc.getString("imagenId")
            );
        }
        return null;
    }

    public InputStream obtenerImagen(String imagenId) {
        if (imagenId == null) return null;
        return gridFSBucket.openDownloadStream(new ObjectId(imagenId));
    }

    private String generarNuevoId() {
        // Contar el número de productos existentes
        long count = collection.countDocuments();
        // Generar el siguiente número (empezando desde 1)
        int nextNumber = (int) (count + 1);
        // Formatear con 3 dígitos
        String numberPart = String.format("%03d", nextNumber);
        return "wt-" + numberPart; // Ejemplo: wt-001, wt-002
    }
}
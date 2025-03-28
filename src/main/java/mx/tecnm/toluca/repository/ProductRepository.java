package mx.tecnm.toluca.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import mx.tecnm.toluca.model.Product;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private final MongoCollection<Document> productCollection;
    private final MongoCollection<Document> orderCollection;
    private final GridFSBucket gridFSBucket;

    public ProductRepository() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("supplier_db");
        this.productCollection = database.getCollection("products");
        this.orderCollection = database.getCollection("orders");
        this.gridFSBucket = GridFSBuckets.create(database);
    }

    public void save(Product product) {
        long productCount = productCollection.countDocuments();
        String customId = "PROD-BLANCO-" + (productCount + 1);

        Document doc = new Document()
                .append("_id", customId)
                .append("name", product.getName())
                .append("description", product.getDescription())
                .append("price", product.getPrice())
                .append("stock", product.getStock())
                .append("imageId", product.getImageId());
        productCollection.insertOne(doc);
        product.setId(customId);
    }

    public void update(Product product) {
        Product existingProduct = findById(product.getId());
        if (existingProduct == null) {
            throw new IllegalArgumentException("Producto no encontrado: " + product.getId());
        }

        // Verificar si hay órdenes pendientes
        long pendingOrders = orderCollection.countDocuments(
                Filters.and(
                        Filters.eq("items.productId", product.getId()),
                        Filters.eq("status", "PENDING")
                )
        );

        // Permitir actualización del stock incluso si hay órdenes pendientes
        // Pero restringir cambios en otros campos (nombre, descripción, precio, imagen)
        if (pendingOrders > 0) {
            boolean onlyStockChanged = product.getName().equals(existingProduct.getName()) &&
                    product.getDescription().equals(existingProduct.getDescription()) &&
                    product.getPrice() == existingProduct.getPrice() &&
                    (product.getImageId() == null || product.getImageId().equals(existingProduct.getImageId()));
            if (!onlyStockChanged) {
                throw new IllegalStateException("No se puede actualizar un producto con órdenes pendientes, excepto el stock");
            }
        }

        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (product.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        Document doc = new Document()
                .append("name", product.getName())
                .append("description", product.getDescription())
                .append("price", product.getPrice())
                .append("stock", product.getStock())
                .append("imageId", product.getImageId());
        productCollection.replaceOne(Filters.eq("_id", product.getId()), doc);
    }

    public void delete(String id) {
        long pendingOrders = orderCollection.countDocuments(
                Filters.and(
                        Filters.eq("items.productId", id),
                        Filters.eq("status", "PENDING")
                )
        );
        if (pendingOrders > 0) {
            throw new IllegalStateException("No se puede eliminar un producto con órdenes pendientes");
        }

        Product product = findById(id);
        if (product != null && product.getImageId() != null) {
            deleteImage(product.getImageId());
        }
        productCollection.deleteOne(Filters.eq("_id", id));
    }

    public Product findById(String id) {
        Document doc = productCollection.find(Filters.eq("_id", id)).first();
        if (doc == null) return null;
        Product product = new Product();
        Object idValue = doc.get("_id");
        product.setId(idValue instanceof ObjectId ? idValue.toString() : (String) idValue);
        product.setName(doc.getString("name"));
        product.setDescription(doc.getString("description"));
        product.setPrice(doc.getDouble("price"));
        product.setStock(doc.getInteger("stock"));
        product.setImageId(doc.getString("imageId"));
        return product;
    }

    public List<Product> findAll(int page, int pageSize) {
        List<Product> products = new ArrayList<>();
        int skip = (page - 1) * pageSize;

        for (Document doc : productCollection.find()
                .sort(Sorts.ascending("_id"))
                .skip(skip)
                .limit(pageSize)) {
            Product product = new Product();
            Object idValue = doc.get("_id");
            product.setId(idValue instanceof ObjectId ? idValue.toString() : (String) idValue);
            product.setName(doc.getString("name"));
            product.setDescription(doc.getString("description"));
            product.setPrice(doc.getDouble("price"));
            product.setStock(doc.getInteger("stock"));
            product.setImageId(doc.getString("imageId"));
            products.add(product);
        }
        return products;
    }

    public long count() {
        return productCollection.countDocuments();
    }

    public String saveImage(InputStream inputStream, String fileName) {
        ObjectId fileId = gridFSBucket.uploadFromStream(fileName, inputStream);
        return fileId.toString();
    }

    public byte[] getImage(String imageId) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        gridFSBucket.downloadToStream(new ObjectId(imageId), outputStream);
        return outputStream.toByteArray();
    }

    public void deleteImage(String imageId) {
        gridFSBucket.delete(new ObjectId(imageId));
    }
}
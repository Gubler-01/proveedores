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
    private final GridFSBucket gridFSBucket;

    public ProductRepository() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("supplier_db");
        this.productCollection = database.getCollection("products");
        this.gridFSBucket = GridFSBuckets.create(database);
    }

    public void save(Product product) {
        // Generar un ID personalizado: PROD-BLANCO-X
        long productCount = productCollection.countDocuments();
        String customId = "PROD-BLANCO-" + (productCount + 1);

        Document doc = new Document()
                .append("_id", customId) // Usar el ID personalizado
                .append("name", product.getName())
                .append("description", product.getDescription())
                .append("price", product.getPrice())
                .append("stock", product.getStock())
                .append("imageId", product.getImageId())
                .append("hasPendingOrders", product.isHasPendingOrders());
        productCollection.insertOne(doc);
        product.setId(customId); // Establecer el ID personalizado en el objeto Product
    }

    public void update(Product product) {
        Document doc = new Document()
                .append("name", product.getName())
                .append("description", product.getDescription())
                .append("price", product.getPrice())
                .append("stock", product.getStock())
                .append("imageId", product.getImageId())
                .append("hasPendingOrders", product.isHasPendingOrders());
        productCollection.replaceOne(Filters.eq("_id", product.getId()), doc);
    }

    public void delete(String id) {
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
        // Manejar el _id que puede ser String o ObjectId
        Object idValue = doc.get("_id");
        product.setId(idValue instanceof ObjectId ? idValue.toString() : (String) idValue);
        product.setName(doc.getString("name"));
        product.setDescription(doc.getString("description"));
        product.setPrice(doc.getDouble("price"));
        product.setStock(doc.getInteger("stock"));
        product.setImageId(doc.getString("imageId"));
        product.setHasPendingOrders(doc.getBoolean("hasPendingOrders", false));
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
            // Manejar el _id que puede ser String o ObjectId
            Object idValue = doc.get("_id");
            product.setId(idValue instanceof ObjectId ? idValue.toString() : (String) idValue);
            product.setName(doc.getString("name"));
            product.setDescription(doc.getString("description"));
            product.setPrice(doc.getDouble("price"));
            product.setStock(doc.getInteger("stock"));
            product.setImageId(doc.getString("imageId"));
            product.setHasPendingOrders(doc.getBoolean("hasPendingOrders", false));
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
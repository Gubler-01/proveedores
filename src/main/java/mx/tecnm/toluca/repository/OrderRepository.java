package mx.tecnm.toluca.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import mx.tecnm.toluca.model.Order;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    private final MongoCollection<Document> orderCollection;

    public OrderRepository() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("supplier_db");
        this.orderCollection = database.getCollection("orders");
    }

    public void save(Order order) {
        // Generar un ID personalizado: ORDER-X
        long orderCount = orderCollection.countDocuments();
        String customId = "ORDER-" + (orderCount + 1);

        Document doc = new Document()
                .append("_id", customId)
                .append("productId", order.getProductId())
                .append("quantity", order.getQuantity())
                .append("status", order.getStatus())
                .append("customerId", order.getCustomerId());
        orderCollection.insertOne(doc);
        order.setId(customId);
    }

    public List<Order> findAll(int page, int pageSize) {
        List<Order> orders = new ArrayList<>();
        int skip = (page - 1) * pageSize;

        for (Document doc : orderCollection.find()
                .sort(Sorts.ascending("_id"))
                .skip(skip)
                .limit(pageSize)) {
            Order order = new Order();
            Object idValue = doc.get("_id");
            order.setId(idValue instanceof org.bson.types.ObjectId ? idValue.toString() : (String) idValue);
            order.setProductId(doc.getString("productId"));
            order.setQuantity(doc.getInteger("quantity"));
            order.setStatus(doc.getString("status"));
            order.setCustomerId(doc.getString("customerId"));
            orders.add(order);
        }
        return orders;
    }

    public long count() {
        return orderCollection.countDocuments();
    }

    public void update(Order order) {
        Document doc = new Document()
                .append("productId", order.getProductId())
                .append("quantity", order.getQuantity())
                .append("status", order.getStatus())
                .append("customerId", order.getCustomerId());
        orderCollection.replaceOne(Filters.eq("_id", order.getId()), doc);
    }
}
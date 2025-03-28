package mx.tecnm.toluca.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import mx.tecnm.toluca.model.Order;
import mx.tecnm.toluca.model.OrderAudit;
import mx.tecnm.toluca.model.OrderItem;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrderRepository {

    private final MongoCollection<Document> orderCollection;
    private final MongoCollection<Document> auditCollection;

    public OrderRepository() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        var database = mongoClient.getDatabase("supplier_db");
        this.orderCollection = database.getCollection("orders");
        this.auditCollection = database.getCollection("order_audit");
    }

    public void save(Order order) {
        // Generar un customId (ejemplo: ORD-00001)
        String customId = generateCustomId();
        order.setCustomId(customId);

        Document doc = new Document();
        doc.append("_id", new ObjectId().toString());
        doc.append("customId", customId);
        doc.append("customerId", order.getCustomerId());
        List<Document> itemsDocs = order.getItems().stream().map(item -> new Document()
                .append("productId", item.getProductId())
                .append("quantity", item.getQuantity())
                .append("unitPrice", item.getUnitPrice())
        ).collect(Collectors.toList());
        doc.append("items", itemsDocs);
        doc.append("status", order.getStatus());
        doc.append("orderDate", order.getOrderDate().toString());
        doc.append("subtotal", order.getSubtotal());
        doc.append("total", order.getTotal());
        orderCollection.insertOne(doc);
        order.setId(doc.getString("_id"));
    }

    public void update(Order order) {
        Document doc = new Document();
        doc.append("customId", order.getCustomId());
        doc.append("customerId", order.getCustomerId());
        List<Document> itemsDocs = order.getItems().stream().map(item -> new Document()
                .append("productId", item.getProductId())
                .append("quantity", item.getQuantity())
                .append("unitPrice", item.getUnitPrice())
        ).collect(Collectors.toList());
        doc.append("items", itemsDocs);
        doc.append("status", order.getStatus());
        doc.append("orderDate", order.getOrderDate().toString());
        doc.append("subtotal", order.getSubtotal());
        doc.append("total", order.getTotal());
        orderCollection.updateOne(
                Filters.eq("_id", order.getId()),
                new Document("$set", doc)
        );
    }

    public List<Order> findAll(int page, int pageSize) {
        List<Order> orders = new ArrayList<>();
        orderCollection.find()
                .skip((page - 1) * pageSize)
                .limit(pageSize)
                .forEach(doc -> {
                    Order order = new Order();
                    order.setId(doc.getString("_id"));
                    // Asignar un customId si no existe
                    String customId = doc.getString("customId");
                    if (customId == null) {
                        customId = generateCustomId();
                        orderCollection.updateOne(
                                Filters.eq("_id", order.getId()),
                                new Document("$set", new Document("customId", customId))
                        );
                    }
                    order.setCustomId(customId);
                    order.setCustomerId(doc.getString("customerId"));

                    List<OrderItem> items;
                    if (doc.containsKey("items") && doc.get("items") != null) {
                        List<Document> itemsDocs = (List<Document>) doc.get("items");
                        items = itemsDocs.stream().map(itemDoc -> {
                            OrderItem item = new OrderItem();
                            item.setProductId(itemDoc.getString("productId"));
                            item.setQuantity(itemDoc.getInteger("quantity"));
                            item.setUnitPrice(itemDoc.getDouble("unitPrice"));
                            return item;
                        }).collect(Collectors.toList());
                    } else {
                        String productId = doc.getString("productId");
                        Integer quantity = doc.getInteger("quantity");
                        Double unitPrice = doc.containsKey("unitPrice") ? doc.getDouble("unitPrice") : 0.0;
                        OrderItem item = new OrderItem();
                        item.setProductId(productId);
                        item.setQuantity(quantity != null ? quantity : 0);
                        item.setUnitPrice(unitPrice != null ? unitPrice : 0.0);
                        items = Collections.singletonList(item);
                    }
                    order.setItems(items);

                    order.setStatus(doc.getString("status"));
                    order.setOrderDate(doc.containsKey("orderDate") ? LocalDateTime.parse(doc.getString("orderDate")) : LocalDateTime.now());
                    order.setSubtotal(doc.containsKey("subtotal") ? doc.getDouble("subtotal") : 0.0);
                    order.setTotal(doc.containsKey("total") ? doc.getDouble("total") : 0.0);
                    orders.add(order);
                });
        return orders;
    }

    public long count() {
        return orderCollection.countDocuments();
    }

    public void saveAudit(String orderId, String action, String details) {
        Document auditDoc = new Document();
        auditDoc.append("orderId", orderId);
        auditDoc.append("action", action);
        auditDoc.append("details", details);
        auditDoc.append("timestamp", LocalDateTime.now().toString());
        auditCollection.insertOne(auditDoc);
    }

    public List<OrderAudit> findAuditByOrderId(String orderId) {
        List<OrderAudit> audits = new ArrayList<>();
        auditCollection.find(Filters.eq("orderId", orderId))
                .forEach(doc -> {
                    OrderAudit audit = new OrderAudit();
                    audit.setOrderId(doc.getString("orderId"));
                    audit.setAction(doc.getString("action"));
                    audit.setDetails(doc.getString("details"));
                    audit.setTimestamp(LocalDateTime.parse(doc.getString("timestamp")));
                    audits.add(audit);
                });
        return audits;
    }

    // Método para generar un customId
    private String generateCustomId() {
        // Obtener el último customId para calcular el siguiente número
        Document lastOrder = orderCollection.find()
                .sort(Sorts.descending("customId"))
                .limit(1)
                .first();

        int nextNumber = 1; // Valor por defecto si no hay órdenes o customId
        if (lastOrder != null && lastOrder.containsKey("customId")) {
            String lastCustomId = lastOrder.getString("customId");
            if (lastCustomId != null && lastCustomId.contains("-")) {
                String numberPart = lastCustomId.split("-")[1];
                try {
                    nextNumber = Integer.parseInt(numberPart) + 1;
                } catch (NumberFormatException e) {
                    // Si el número no se puede parsear, usar el valor por defecto
                    nextNumber = 1;
                }
            }
        }

        return String.format("ORD-%05d", nextNumber);
    }
}
package mx.tecnm.toluca.repository;

import mx.tecnm.toluca.model.Order;
import mx.tecnm.toluca.model.OrderAudit;
import mx.tecnm.toluca.model.OrderItem;
import mx.tecnm.toluca.util.ConfigUtil;
import mx.tecnm.toluca.util.HttpClientUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private final String databaseServiceUrl;

    public OrderRepository() {
        this.databaseServiceUrl = ConfigUtil.getProperty("database.service.url") + ConfigUtil.getProperty("database.service.endpoint");
    }

    public void save(HttpServletRequest request, Order order) throws IOException, InterruptedException {
        String token = getTokenFromSession(request);

        String customId = generateCustomId(request);
        order.setCustomId(customId);

        JSONObject doc = new JSONObject();
        doc.put("_id", new JSONObject().toString());
        doc.put("customId", customId);
        doc.put("customerId", order.getCustomerId());
        JSONArray itemsArray = new JSONArray();
        for (OrderItem item : order.getItems()) {
            JSONObject itemDoc = new JSONObject();
            itemDoc.put("productId", item.getProductId());
            itemDoc.put("quantity", item.getQuantity());
            itemDoc.put("unitPrice", item.getUnitPrice());
            itemsArray.put(itemDoc);
        }
        doc.put("items", itemsArray);
        doc.put("status", order.getStatus());
        doc.put("orderDate", order.getOrderDate().toString());
        doc.put("subtotal", order.getSubtotal());
        doc.put("total", order.getTotal());

        String url = databaseServiceUrl + "/orders";
        String response = HttpClientUtil.sendPostRequest(url, token, doc.toString());
        JSONObject responseDoc = new JSONObject(response);
        order.setId(responseDoc.getString("_id"));
    }

    public void update(HttpServletRequest request, Order order) throws IOException, InterruptedException {
        String token = getTokenFromSession(request);

        JSONObject doc = new JSONObject();
        doc.put("customId", order.getCustomId());
        doc.put("customerId", order.getCustomerId());
        JSONArray itemsArray = new JSONArray();
        for (OrderItem item : order.getItems()) {
            JSONObject itemDoc = new JSONObject();
            itemDoc.put("productId", item.getProductId());
            itemDoc.put("quantity", item.getQuantity());
            itemDoc.put("unitPrice", item.getUnitPrice());
            itemsArray.put(itemDoc);
        }
        doc.put("items", itemsArray);
        doc.put("status", order.getStatus());
        doc.put("orderDate", order.getOrderDate().toString());
        doc.put("subtotal", order.getSubtotal());
        doc.put("total", order.getTotal());

        String url = databaseServiceUrl + "/orders/" + order.getId();
        HttpClientUtil.sendPutRequest(url, token, doc.toString());
    }

    public List<Order> findAll(HttpServletRequest request, int page, int pageSize) throws IOException, InterruptedException {
        String token = getTokenFromSession(request);

        String url = databaseServiceUrl + "/orders?page=" + page + "&size=" + pageSize;
        String response = HttpClientUtil.sendGetRequest(url, token);
        JSONArray docs = new JSONArray(response);

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
            Order order = new Order();
            order.setId(doc.getString("_id"));

            String customId = doc.optString("customId", null);
            if (customId == null) {
                customId = generateCustomId(request);
                JSONObject updateDoc = new JSONObject();
                updateDoc.put("customId", customId);
                HttpClientUtil.sendPutRequest(databaseServiceUrl + "/orders/" + order.getId(), token, updateDoc.toString());
            }
            order.setCustomId(customId);

            order.setCustomerId(doc.getString("customerId"));

            List<OrderItem> items = new ArrayList<>();
            if (doc.has("items") && !doc.isNull("items")) {
                JSONArray itemsArray = doc.getJSONArray("items");
                for (int j = 0; j < itemsArray.length(); j++) {
                    JSONObject itemDoc = itemsArray.getJSONObject(j);
                    OrderItem item = new OrderItem();
                    item.setProductId(itemDoc.getString("productId"));
                    item.setQuantity(itemDoc.getInt("quantity"));
                    item.setUnitPrice(itemDoc.getDouble("unitPrice"));
                    items.add(item);
                }
            }
            order.setItems(items);

            order.setStatus(doc.getString("status"));
            order.setOrderDate(doc.has("orderDate") ? LocalDateTime.parse(doc.getString("orderDate")) : LocalDateTime.now());
            order.setSubtotal(doc.optDouble("subtotal", 0.0));
            order.setTotal(doc.optDouble("total", 0.0));
            orders.add(order);
        }
        return orders;
    }

    public long count(HttpServletRequest request) throws IOException, InterruptedException {
        String token = getTokenFromSession(request);

        String url = databaseServiceUrl + "/orders";
        String response = HttpClientUtil.sendGetRequest(url, token);
        JSONArray docs = new JSONArray(response);
        return docs.length();
    }

    public void saveAudit(HttpServletRequest request, String orderId, String action, String details) throws IOException, InterruptedException {
        String token = getTokenFromSession(request);

        JSONObject auditDoc = new JSONObject();
        auditDoc.put("orderId", orderId);
        auditDoc.put("action", action);
        auditDoc.put("details", details);
        auditDoc.put("timestamp", LocalDateTime.now().toString());

        String url = databaseServiceUrl + "/order_audit";
        HttpClientUtil.sendPostRequest(url, token, auditDoc.toString());
    }

    public List<OrderAudit> findAuditByOrderId(HttpServletRequest request, String orderId) throws IOException, InterruptedException {
        String token = getTokenFromSession(request);

        String url = databaseServiceUrl + "/order_audit?orderId=" + orderId;
        String response = HttpClientUtil.sendGetRequest(url, token);
        JSONArray docs = new JSONArray(response);

        List<OrderAudit> audits = new ArrayList<>();
        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
            OrderAudit audit = new OrderAudit();
            audit.setOrderId(doc.getString("orderId"));
            audit.setAction(doc.getString("action"));
            audit.setDetails(doc.getString("details"));
            audit.setTimestamp(LocalDateTime.parse(doc.getString("timestamp")));
            audits.add(audit);
        }
        return audits;
    }

    private String generateCustomId(HttpServletRequest request) throws IOException, InterruptedException {
        String token = getTokenFromSession(request);

        String url = databaseServiceUrl + "/orders?sort=customId,desc&limit=1";
        String response = HttpClientUtil.sendGetRequest(url, token);
        JSONArray docs = new JSONArray(response);

        int nextNumber = 1;
        if (!docs.isEmpty()) {
            JSONObject lastOrder = docs.getJSONObject(0);
            String lastCustomId = lastOrder.optString("customId", null);
            if (lastCustomId != null && lastCustomId.contains("-")) {
                String numberPart = lastCustomId.split("-")[1];
                try {
                    nextNumber = Integer.parseInt(numberPart) + 1;
                } catch (NumberFormatException e) {
                    nextNumber = 1;
                }
            }
        }

        return String.format("ORD-%05d", nextNumber);
    }

    private String getTokenFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("token") == null) {
            throw new IllegalStateException("No se encontró un token de autenticación. Por favor, inicia sesión.");
        }
        return (String) session.getAttribute("token");
    }
}
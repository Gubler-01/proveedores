package mx.tecnm.toluca.repository;

import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.util.ConfigUtil;
import mx.tecnm.toluca.util.HttpClientUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private static final String FILE_SERVER_TOKEN = ConfigUtil.getProperty("file.server.token"); // Leer el token desde application.properties
    private final String databaseServiceUrl;
    private final String fileServerUrl;
    private final String fileServerUploadEndpoint;

    public ProductRepository() {
        this.databaseServiceUrl = ConfigUtil.getProperty("database.service.url") + ConfigUtil.getProperty("database.service.endpoint");
        this.fileServerUrl = ConfigUtil.getProperty("file.server.url");
        this.fileServerUploadEndpoint = ConfigUtil.getProperty("file.server.upload.endpoint");
    }

    public void save(HttpServletRequest request, Product product, String imageFilePath) throws IOException, InterruptedException {
        String token = getTokenFromSession(request);

        long productCount = count(request);
        String customId = "PROD-BLANCO-" + (productCount + 1);
        product.setId(customId);

        String imageUrl = null;
        if (imageFilePath != null && !imageFilePath.isEmpty()) {
            String fileName = customId + ".png";
            String response = HttpClientUtil.sendPostFormDataRequest(fileServerUploadEndpoint, FILE_SERVER_TOKEN, imageFilePath, fileName);
            JSONObject jsonResponse = new JSONObject(response);
            imageUrl = jsonResponse.getString("fileUrl");
            product.setImageUrl(imageUrl);
        }

        JSONObject doc = new JSONObject();
        doc.put("_id", customId);
        doc.put("name", product.getName());
        doc.put("description", product.getDescription());
        doc.put("price", product.getPrice());
        doc.put("stock", product.getStock());
        doc.put("imageUrl", product.getImageUrl());

        String url = databaseServiceUrl + "/products";
        HttpClientUtil.sendPostRequest(url, token, doc.toString());
    }

    public void update(HttpServletRequest request, Product product, String imageFilePath) throws IOException, InterruptedException {
        String token = getTokenFromSession(request);

        Product existingProduct = findById(request, product.getId());
        if (existingProduct == null) {
            throw new IllegalArgumentException("Producto no encontrado: " + product.getId());
        }

        String ordersUrl = databaseServiceUrl + "/orders/filtrar";
        JSONObject filter = new JSONObject();
        filter.put("items.productId", product.getId());
        filter.put("status", "PENDING");
        String ordersResponse = HttpClientUtil.sendPostRequest(ordersUrl, token, filter.toString());
        JSONArray ordersArray = new JSONArray(ordersResponse);
        if (ordersArray.length() > 0) {
            boolean onlyStockChanged = product.getName().equals(existingProduct.getName()) &&
                    product.getDescription().equals(existingProduct.getDescription()) &&
                    product.getPrice() == existingProduct.getPrice() &&
                    (product.getImageUrl() == null || product.getImageUrl().equals(existingProduct.getImageUrl()));
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

        if (imageFilePath != null && !imageFilePath.isEmpty()) {
            String fileName = product.getId() + ".png";
            String response = HttpClientUtil.sendPostFormDataRequest(fileServerUploadEndpoint, FILE_SERVER_TOKEN, imageFilePath, fileName);
            JSONObject jsonResponse = new JSONObject(response);
            String imageUrl = jsonResponse.getString("fileUrl");
            product.setImageUrl(imageUrl);
        }

        JSONObject doc = new JSONObject();
        doc.put("name", product.getName());
        doc.put("description", product.getDescription());
        doc.put("price", product.getPrice());
        doc.put("stock", product.getStock());
        doc.put("imageUrl", product.getImageUrl());

        String url = databaseServiceUrl + "/products/" + product.getId();
        HttpClientUtil.sendPutRequest(url, token, doc.toString());
    }

    public void delete(HttpServletRequest request, String id) throws IOException, InterruptedException {
        String token = getTokenFromSession(request);

        String ordersUrl = databaseServiceUrl + "/orders/filtrar";
        JSONObject filter = new JSONObject();
        filter.put("items.productId", id);
        filter.put("status", "PENDING");
        String ordersResponse = HttpClientUtil.sendPostRequest(ordersUrl, token, filter.toString());
        JSONArray ordersArray = new JSONArray(ordersResponse);
        if (ordersArray.length() > 0) {
            throw new IllegalStateException("No se puede eliminar un producto con órdenes pendientes");
        }

        String url = databaseServiceUrl + "/products/" + id;
        HttpClientUtil.sendDeleteRequest(url, token);
    }

    public Product findById(HttpServletRequest request, String id) throws IOException, InterruptedException {
        String token = getTokenFromSession(request);

        String url = databaseServiceUrl + "/products/" + id;
        String response = HttpClientUtil.sendGetRequest(url, token);
        JSONObject doc = new JSONObject(response);

        Product product = new Product();
        product.setId(doc.getString("_id"));
        product.setName(doc.getString("name"));
        product.setDescription(doc.getString("description"));
        product.setPrice(doc.getDouble("price"));
        product.setStock(doc.getInt("stock"));
        product.setImageUrl(doc.optString("imageUrl", null));
        return product;
    }

    public List<Product> findAll(HttpServletRequest request, int page, int pageSize) throws IOException, InterruptedException {
        String token = getTokenFromSession(request);

        String url = databaseServiceUrl + "/products?page=" + page + "&size=" + pageSize;
        String response = HttpClientUtil.sendGetRequest(url, token);
        JSONArray docs = new JSONArray(response);

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
            Product product = new Product();
            product.setId(doc.getString("_id"));
            product.setName(doc.getString("name"));
            product.setDescription(doc.getString("description"));
            product.setPrice(doc.getDouble("price"));
            product.setStock(doc.getInt("stock"));
            product.setImageUrl(doc.optString("imageUrl", null));
            products.add(product);
        }
        return products;
    }

    public long count(HttpServletRequest request) throws IOException, InterruptedException {
        String token = getTokenFromSession(request);

        String url = databaseServiceUrl + "/products";
        String response = HttpClientUtil.sendGetRequest(url, token);
        JSONArray docs = new JSONArray(response);
        return docs.length();
    }

    private String getTokenFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("token") == null) {
            throw new IllegalStateException("No se encontró un token de autenticación. Por favor, inicia sesión.");
        }
        return (String) session.getAttribute("token");
    }
}
package mx.tecnm.toluca.service;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.util.ConfiguracionApp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductService {
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());
    private final String baseUrl = ConfiguracionApp.getProperty("app.base.url");
    private final String serviceEndpoint = ConfiguracionApp.getProperty("app.service.endpoint");
    private final String collection = "productos1";

    public List<Product> getAllProducts(String token) {
        Client client = ClientBuilder.newClient();
        Jsonb jsonb = JsonbBuilder.create();

        try {
            String url = baseUrl + serviceEndpoint + "/" + collection;
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .get();

            String responseBody = response.readEntity(String.class);
            LOGGER.log(Level.INFO, "Respuesta de la API al obtener productos: {0}", responseBody);

            if (response.getStatus() == 200) {
                Product[] productsArray = jsonb.fromJson(responseBody, Product[].class);
                List<Product> products = Arrays.asList(productsArray);
                LOGGER.log(Level.INFO, "Productos obtenidos: {0}", products.size());
                return products.isEmpty() ? Collections.emptyList() : products;
            } else {
                LOGGER.log(Level.SEVERE, "Error al obtener productos: {0}", responseBody);
                throw new RuntimeException("Error al obtener productos: " + responseBody);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Excepción al obtener productos", e);
            throw new RuntimeException("Error al conectar con la API: " + e.getMessage());
        } finally {
            client.close();
            try {
                jsonb.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error cerrando Jsonb", e);
            }
        }
    }

    public void addProduct(HttpServletRequest request, String token) {
        Product product = new Product();
        product.setName(request.getParameter("name"));
        product.setDescription(request.getParameter("description"));
        product.setPrice(Double.parseDouble(request.getParameter("price")));
        product.setStock(Integer.parseInt(request.getParameter("stock")));

        List<Product> productList = Collections.singletonList(product);

        Client client = ClientBuilder.newClient();
        Jsonb jsonb = JsonbBuilder.create();

        try {
            String url = baseUrl + serviceEndpoint + "/" + collection;
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .post(Entity.json(productList));

            String responseBody = response.readEntity(String.class);
            if (response.getStatus() == 200) {
                ResponseMessage responseMessage = jsonb.fromJson(responseBody, ResponseMessage.class);
                if ("success".equals(responseMessage.getStatus()) && responseMessage.getHttpCode() == 200) {
                    LOGGER.log(Level.INFO, "Producto agregado correctamente: {0}", responseBody);
                } else {
                    LOGGER.log(Level.SEVERE, "Error en la respuesta de la API: {0}", responseBody);
                    throw new RuntimeException(responseMessage.getMessage());
                }
            } else {
                LOGGER.log(Level.SEVERE, "Error al agregar producto: {0}", responseBody);
                throw new RuntimeException(responseBody);
            }
        } finally {
            client.close();
            try {
                jsonb.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error cerrando Jsonb", e);
            }
        }
    }

    public void updateProduct(HttpServletRequest request, String token) {
        String id = request.getParameter("id");
        Product product = new Product();
        product.setName(request.getParameter("name"));
        product.setDescription(request.getParameter("description"));
        product.setPrice(Double.parseDouble(request.getParameter("price")));
        product.setStock(Integer.parseInt(request.getParameter("stock")));

        Client client = ClientBuilder.newClient();
        Jsonb jsonb = JsonbBuilder.create();

        try {
            String url = baseUrl + serviceEndpoint + "/" + collection + "/" + id;
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .put(Entity.json(product));

            String responseBody = response.readEntity(String.class);
            if (response.getStatus() == 200) {
                ResponseMessage responseMessage = jsonb.fromJson(responseBody, ResponseMessage.class);
                if ("succes".equals(responseMessage.getStatus()) && responseMessage.getHttpCode() == 200) {
                    LOGGER.log(Level.INFO, "Producto actualizado correctamente: {0}", responseBody);
                    return; // Éxito, no lanzamos excepción
                } else {
                    LOGGER.log(Level.SEVERE, "Error en la respuesta de la API: {0}", responseBody);
                    throw new RuntimeException(responseMessage.getMessage());
                }
            } else {
                LOGGER.log(Level.SEVERE, "Error al actualizar producto: {0}", responseBody);
                throw new RuntimeException(responseBody);
            }
        } finally {
            client.close();
            try {
                jsonb.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error cerrando Jsonb", e);
            }
        }
    }

    public void deleteProduct(String id, String token) {
        Client client = ClientBuilder.newClient();
        try {
            String url = baseUrl + serviceEndpoint + "/" + collection + "/" + id;
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .delete();

            if (response.getStatus() != 200) {
                String error = response.readEntity(String.class);
                LOGGER.log(Level.SEVERE, "Error al eliminar producto: {0}", error);
                throw new RuntimeException("Error al eliminar producto: " + error);
            }
            LOGGER.log(Level.INFO, "Producto eliminado correctamente en {0}", url);
        } finally {
            client.close();
        }
    }

    public static class ResponseMessage {
        private int httpCode;
        private String message;
        private String status;

        public int getHttpCode() { return httpCode; }
        public void setHttpCode(int httpCode) { this.httpCode = httpCode; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
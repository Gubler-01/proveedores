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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductService {
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());
    private final String baseUrl = ConfiguracionApp.getProperty("app.base.url");
    private final String serviceEndpoint = ConfiguracionApp.getProperty("app.service.endpoint");
    private final String collection = "productos1";
    private final Client client; // Cliente como campo de instancia

    public ProductService() {
        this.client = ClientBuilder.newBuilder()
                .property("jersey.config.client.connectTimeout", 10000)
                .property("jersey.config.client.readTimeout", 10000)
                .build();
    }

    public List<Product> getAllProducts(String token) {
        Jsonb jsonb = JsonbBuilder.create();
        try {
            String url = baseUrl + serviceEndpoint + "/" + collection;
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .get();

            if (response.getStatus() == 200) {
                String responseBody = response.readEntity(String.class);
                LOGGER.log(Level.INFO, "Productos obtenidos de {0}: {1}", new Object[]{url, responseBody});
                return Arrays.asList(jsonb.fromJson(responseBody, Product[].class));
            } else {
                String error = response.readEntity(String.class);
                LOGGER.log(Level.SEVERE, "Error al obtener productos: {0}", error);
                throw new RuntimeException("Error al obtener productos: " + error);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Excepción al obtener productos", e);
            throw new RuntimeException("Error al conectar con la API: " + e.getMessage());
        } finally {
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

        try {
            String url = baseUrl + serviceEndpoint + "/" + collection;
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .post(Entity.json(product));

            if (response.getStatus() != 200) {
                String error = response.readEntity(String.class);
                LOGGER.log(Level.SEVERE, "Error al agregar producto: {0}", error);
                throw new RuntimeException("Error al agregar producto: " + error);
            }
            LOGGER.log(Level.INFO, "Producto agregado correctamente en {0}", url);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Excepción al agregar producto", e);
            throw new RuntimeException("Error al conectar con la API: " + e.getMessage());
        }
    }

    public void updateProduct(HttpServletRequest request, String token) {
        String id = request.getParameter("id");
        Product product = new Product();
        product.setId(id);
        product.setName(request.getParameter("name"));
        product.setDescription(request.getParameter("description"));
        product.setPrice(Double.parseDouble(request.getParameter("price")));
        product.setStock(Integer.parseInt(request.getParameter("stock")));

        try {
            String url = baseUrl + serviceEndpoint + "/" + collection + "/" + id;
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .put(Entity.json(product));

            if (response.getStatus() != 200) {
                String error = response.readEntity(String.class);
                LOGGER.log(Level.SEVERE, "Error al actualizar producto: {0}", error);
                throw new RuntimeException("Error al actualizar producto: " + error);
            }
            LOGGER.log(Level.INFO, "Producto actualizado correctamente en {0}", url);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Excepción al actualizar producto", e);
            throw new RuntimeException("Error al conectar con la API: " + e.getMessage());
        }
    }

    public void deleteProduct(String id, String token) {
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
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Excepción al eliminar producto", e);
            throw new RuntimeException("Error al conectar con la API: " + e.getMessage());
        }
    }

    // Método para cerrar el cliente cuando sea necesario (opcional)
    public void close() {
        client.close();
    }
}
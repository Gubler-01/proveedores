package mx.tecnm.toluca.service;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mx.tecnm.toluca.model.Order;
import mx.tecnm.toluca.model.OrderItem;
import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.util.ConfiguracionApp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderService {
    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());
    private final String baseUrl = ConfiguracionApp.getProperty("app.base.url");
    private final String serviceEndpoint = ConfiguracionApp.getProperty("app.service.endpoint");
    private final String collection = "pedidos1";
    private final ProductService productService = new ProductService();

    public Order createOrder(Order order, String token) {
        Client client = ClientBuilder.newClient();
        Jsonb jsonb = JsonbBuilder.create();

        try {
            // Generar número de orden único y otros campos
            order.setId(UUID.randomUUID().toString());
            order.setStatus("Pendiente");
            order.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // Consultar productos para obtener precios y calcular totales
            List<Product> products = productService.getAllProducts(token);
            double subtotal = 0.0;

            for (OrderItem item : order.getItems()) {
                Product product = products.stream()
                        .filter(p -> p.getId().equals(item.getProductId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getProductId()));
                
                item.setPrice(product.getPrecio());
                subtotal += product.getPrecio() * item.getQuantity();
            }

            order.setSubtotal(subtotal);
            order.setTotal(subtotal); // Total = subtotal por ahora

            // Enviar la orden a la colección "ordenes1" como arreglo
            String url = baseUrl + serviceEndpoint + "/" + collection;
            LOGGER.log(Level.INFO, "Creando orden en la URL: {0}", url);
            String jsonOrder = jsonb.toJson(List.of(order)); // Enviar como [{...}]
            LOGGER.log(Level.INFO, "Orden enviada como JSON: {0}", jsonOrder);

            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .post(Entity.json(List.of(order)));

            String responseBody = response.readEntity(String.class);
            LOGGER.log(Level.INFO, "Código HTTP: {0}, Respuesta de la API: {1}", new Object[]{response.getStatus(), responseBody});

            if (response.getStatus() == 200) {
                ProductService.ResponseMessage responseMessage = jsonb.fromJson(responseBody, ProductService.ResponseMessage.class);
                if ("success".equals(responseMessage.getStatus()) && responseMessage.getHttpCode() == 200) {
                    LOGGER.log(Level.INFO, "Orden creada correctamente: {0}", responseBody);
                    return order; // Devolver la orden procesada
                } else {
                    LOGGER.log(Level.SEVERE, "Error en la respuesta de la API: {0}", responseBody);
                    throw new RuntimeException(responseMessage.getMessage());
                }
            } else {
                LOGGER.log(Level.SEVERE, "Error al crear orden: Código HTTP: {0}, Respuesta: {1}", 
                    new Object[]{response.getStatus(), responseBody});
                throw new RuntimeException("Error al crear orden: Código " + response.getStatus() + " - " + responseBody);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Excepción al crear orden", e);
            throw new RuntimeException("Error al procesar la orden: " + e.getMessage());
        } finally {
            client.close();
            try {
                jsonb.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error cerrando Jsonb", e);
            }
        }
    }
}
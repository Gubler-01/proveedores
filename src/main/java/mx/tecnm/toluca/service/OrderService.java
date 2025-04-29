package mx.tecnm.toluca.service;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mx.tecnm.toluca.model.Order;
import mx.tecnm.toluca.model.OrderItem;
import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.util.ConfiguracionApp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderService {

    private static final Logger LOGGER = Logger.getLogger(OrderService.class.getName());
    private final String baseUrl = ConfiguracionApp.getProperty("app.base.url");
    private final String serviceEndpoint = ConfiguracionApp.getProperty("app.service.endpoint");
    private final String collection = "pedidos1";
    private final ProductService productService = new ProductService();

    public Order createOrder(Order order, String token) {
        try (Client client = ClientBuilder.newClient(); Jsonb jsonb = JsonbBuilder.create()) {
            order.setStatus("Pendiente");
            order.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            if (order.getPaymentMethod() == null || order.getPaymentMethod().trim().isEmpty()) {
                LOGGER.log(Level.WARNING, "Intento de crear orden sin método de pago");
                throw new IllegalArgumentException("El método de pago es obligatorio.");
            }

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
            order.setTotal(subtotal);

            String url = baseUrl + serviceEndpoint + "/" + collection;
            LOGGER.log(Level.INFO, "Creando orden en la URL: {0}", url);
            String jsonOrder = jsonb.toJson(List.of(order));
            LOGGER.log(Level.INFO, "Orden enviada como JSON: {0}", jsonOrder);

            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .post(Entity.json(List.of(order)));

            String responseBody = response.readEntity(String.class);
            LOGGER.log(Level.INFO, "Código HTTP: {0}, Respuesta de la API: {1}", new Object[]{response.getStatus(), responseBody});

            if (response.getStatus() == 200) {
                ProductService.ResponseMessage responseMessage = jsonb.fromJson(responseBody, ProductService.ResponseMessage.class);
                if (("success".equals(responseMessage.getStatus()) || "succes".equals(responseMessage.getStatus())) && responseMessage.getHttpCode() == 200) {
                    LOGGER.log(Level.INFO, "Orden creada correctamente: {0}", responseBody);
                    return order;
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
        }
    }

    public List<Order> getAllOrders(String token) {
        try (Client client = ClientBuilder.newClient(); Jsonb jsonb = JsonbBuilder.create()) {
            String url = baseUrl + serviceEndpoint + "/" + collection;
            LOGGER.log(Level.INFO, "Solicitando órdenes a la URL: {0} con token: {1}", new Object[]{url, token});
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .get();

            String responseBody = response.readEntity(String.class);
            LOGGER.log(Level.INFO, "Código HTTP: {0}, Respuesta de la API: {1}", new Object[]{response.getStatus(), responseBody});

            if (response.getStatus() == 200) {
                Order[] ordersArray = jsonb.fromJson(responseBody, Order[].class);
                List<Order> orders = Arrays.asList(ordersArray);
                LOGGER.log(Level.INFO, "Número de órdenes obtenidas: {0}", orders.size());
                return orders.isEmpty() ? Collections.emptyList() : orders;
            } else if (response.getStatus() == 204) {
                LOGGER.log(Level.INFO, "No se encontraron órdenes en la base de datos (204 No Content).");
                return Collections.emptyList();
            } else {
                LOGGER.log(Level.SEVERE, "Error al obtener órdenes. Código HTTP: {0}, Respuesta: {1}",
                        new Object[]{response.getStatus(), responseBody});
                throw new RuntimeException("Error al obtener órdenes: Código " + response.getStatus() + " - " + responseBody);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Excepción al obtener órdenes", e);
            throw new RuntimeException("Error al conectar con la API: " + e.getMessage());
        }
    }

    public void updateOrder(HttpServletRequest request, String token) throws IOException, ServletException {
        try (Client client = ClientBuilder.newClient(); Jsonb jsonb = JsonbBuilder.create()) {
            Order order = new Order();

            // Leer los datos del formulario una sola vez
            String id = new String(request.getPart("id").getInputStream().readAllBytes()).trim();
            String customerId = new String(request.getPart("customerId").getInputStream().readAllBytes()).trim();
            String itemsJson = new String(request.getPart("items").getInputStream().readAllBytes()).trim();
            String subtotalStr = new String(request.getPart("subtotal").getInputStream().readAllBytes()).trim();
            String totalStr = new String(request.getPart("total").getInputStream().readAllBytes()).trim();
            String status = new String(request.getPart("status").getInputStream().readAllBytes()).trim();
            String createdAt = new String(request.getPart("createdAt").getInputStream().readAllBytes()).trim();
            String paymentMethod = new String(request.getPart("paymentMethod").getInputStream().readAllBytes()).trim();

            // Loguear los datos recibidos
            LOGGER.log(Level.INFO, "Datos recibidos en updateOrder:");
            LOGGER.log(Level.INFO, "ID: {0}", id);
            LOGGER.log(Level.INFO, "CustomerId: {0}", customerId);
            LOGGER.log(Level.INFO, "Items JSON: {0}", itemsJson);
            LOGGER.log(Level.INFO, "Subtotal: {0}", subtotalStr);
            LOGGER.log(Level.INFO, "Total: {0}", totalStr);
            LOGGER.log(Level.INFO, "Status: {0}", status);
            LOGGER.log(Level.INFO, "CreatedAt: {0}", createdAt);
            LOGGER.log(Level.INFO, "PaymentMethod: {0}", paymentMethod);

            // Construir el objeto Order en el orden exacto esperado por la API
            order.setCreatedAt(createdAt);
            order.setTotal(Double.parseDouble(totalStr));
            order.setSubtotal(Double.parseDouble(subtotalStr));
            order.setCustomerId(customerId);
            order.setPaymentMethod(paymentMethod);

            // Deserializar items
            List<OrderItem> items;
            try {
                if (itemsJson.isEmpty()) {
                    throw new IllegalArgumentException("El campo items está vacío.");
                }
                OrderItem[] itemsArray = jsonb.fromJson(itemsJson, OrderItem[].class);
                items = Arrays.asList(itemsArray);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al deserializar items JSON: {0}" + itemsJson, e);
                throw new IllegalArgumentException("Error al deserializar los ítems: " + e.getMessage());
            }
            order.setItems(items);

            order.setStatus(status);

            // Validaciones
            if (id.isEmpty()) {
                LOGGER.log(Level.WARNING, "Intento de actualizar orden sin ID");
                throw new IllegalArgumentException("El ID de la orden es obligatorio.");
            }
            if (customerId.isEmpty()) {
                LOGGER.log(Level.WARNING, "Intento de actualizar orden sin customerId");
                throw new IllegalArgumentException("El ID del cliente es obligatorio.");
            }
            if (items.isEmpty()) {
                LOGGER.log(Level.WARNING, "Intento de actualizar orden sin ítems");
                throw new IllegalArgumentException("La orden debe tener al menos un ítem.");
            }
            for (OrderItem item : items) {
                if (item.getProductId() == null || item.getProductId().isEmpty()) {
                    LOGGER.log(Level.WARNING, "Ítem con productId inválido");
                    throw new IllegalArgumentException("El ID del producto es obligatorio.");
                }
                if (item.getQuantity() <= 0) {
                    LOGGER.log(Level.WARNING, "Ítem con cantidad inválida: {0}", item.getQuantity());
                    throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
                }
                if (item.getPrice() < 0) {
                    LOGGER.log(Level.WARNING, "Ítem con precio inválido: {0}", item.getPrice());
                    throw new IllegalArgumentException("El precio no puede ser negativo.");
                }
            }
            if (order.getSubtotal() < 0) {
                LOGGER.log(Level.WARNING, "Intento de actualizar orden con subtotal negativo: {0}", order.getSubtotal());
                throw new IllegalArgumentException("El subtotal no puede ser negativo.");
            }
            if (order.getTotal() < 0) {
                LOGGER.log(Level.WARNING, "Intento de actualizar orden con total negativo: {0}", order.getTotal());
                throw new IllegalArgumentException("El total no puede ser negativo.");
            }
            if (!List.of("Pendiente", "Enviado", "Completado", "Cancelado").contains(status)) {
                LOGGER.log(Level.WARNING, "Estado de orden inválido: {0}", status);
                throw new IllegalArgumentException("El estado de la orden es inválido: " + status);
            }
            if (createdAt.isEmpty()) {
                LOGGER.log(Level.WARNING, "Intento de actualizar orden sin fecha de creación");
                throw new IllegalArgumentException("La fecha de creación es obligatoria.");
            }
            if (paymentMethod.isEmpty()) {
                LOGGER.log(Level.WARNING, "Intento de actualizar orden sin método de pago");
                throw new IllegalArgumentException("El método de pago es obligatorio.");
            }

            // Loguear el objeto Order antes de enviarlo a la API
            String orderJson = jsonb.toJson(order);
            LOGGER.log(Level.INFO, "Objeto Order enviado a la API: {0}", orderJson);

            // Enviar solicitud PUT
            String url = baseUrl + serviceEndpoint + "/" + collection + "/" + id;
            LOGGER.log(Level.INFO, "Actualizando orden en la URL: {0}", url);

            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .put(Entity.json(order));

            String responseBody = response.readEntity(String.class);
            LOGGER.log(Level.INFO, "Código HTTP: {0}, Respuesta de la API: {1}",
                    new Object[]{response.getStatus(), responseBody});

            if (response.getStatus() == 200) {
                ProductService.ResponseMessage responseMessage = jsonb.fromJson(
                        responseBody, ProductService.ResponseMessage.class);
                if (("success".equals(responseMessage.getStatus()) || "succes".equals(responseMessage.getStatus())) && responseMessage.getHttpCode() == 200) {
                    LOGGER.log(Level.INFO, "Orden actualizada correctamente: {0}", responseBody);
                } else {
                    LOGGER.log(Level.SEVERE, "Error en la respuesta de la API: {0}", responseBody);
                    throw new RuntimeException("Error al actualizar la orden: " + responseMessage.getMessage());
                }
            } else {
                LOGGER.log(Level.SEVERE, "Error al actualizar orden: Código HTTP: {0}, Respuesta: {1}",
                        new Object[]{response.getStatus(), responseBody});
                throw new RuntimeException("Error al actualizar orden: Código " + response.getStatus() + " - " + responseBody);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Excepción al actualizar orden", e);
            throw new RuntimeException("Error al procesar la actualización: " + e.getMessage());
        }
    }
}
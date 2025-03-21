package mx.tecnm.toluca.controller;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.tecnm.toluca.model.Order;
import mx.tecnm.toluca.model.OrderItem;
import mx.tecnm.toluca.service.OrderService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "OrderController", urlPatterns = {"/api/orders"})
public class OrderController extends HttpServlet {
    private OrderService orderService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        orderService = new OrderService();

        // Configurar Gson con un adaptador para LocalDateTime
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            @Override
            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(formatter.format(src));
            }
        });
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            @Override
            public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return LocalDateTime.parse(json.getAsString(), formatter);
            }
        });
        gson = gsonBuilder.create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Configurar la respuesta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Obtener todas las órdenes
        List<Order> orders = orderService.getOrders(1, Integer.MAX_VALUE); // Obtener todas las órdenes

        // Convertir a JSON
        String json = gson.toJson(orders);

        // Enviar la respuesta
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Leer el cuerpo de la solicitud (JSON)
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Error al leer el cuerpo de la solicitud: " + e.getMessage() + "\"}");
            return;
        }

        try {
            // Parsear el JSON
            String jsonBody = sb.toString();
            if (jsonBody.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"El cuerpo de la solicitud está vacío\"}");
                return;
            }

            JsonObject jsonObject = JsonParser.parseString(jsonBody).getAsJsonObject();

            // Validar campos requeridos
            if (!jsonObject.has("customerId") || jsonObject.get("customerId").isJsonNull()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Falta el campo customerId\"}");
                return;
            }
            if (!jsonObject.has("items") || !jsonObject.get("items").isJsonArray()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Falta el campo items o no es un arreglo\"}");
                return;
            }

            // Crear la orden
            Order order = new Order();
            order.setCustomerId(jsonObject.get("customerId").getAsString());

            // Obtener la lista de ítems
            JsonArray itemsArray = jsonObject.getAsJsonArray("items");
            if (itemsArray.size() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"La lista de ítems no puede estar vacía\"}");
                return;
            }

            List<OrderItem> items = new ArrayList<>();
            for (int i = 0; i < itemsArray.size(); i++) {
                JsonObject itemObject = itemsArray.get(i).getAsJsonObject();

                // Validar campos del ítem
                if (!itemObject.has("productId") || itemObject.get("productId").isJsonNull()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Falta el campo productId en el ítem " + i + "\"}");
                    return;
                }
                if (!itemObject.has("quantity") || itemObject.get("quantity").isJsonNull()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"Falta el campo quantity en el ítem " + i + "\"}");
                    return;
                }

                OrderItem item = new OrderItem();
                item.setProductId(itemObject.get("productId").getAsString());
                item.setQuantity(itemObject.get("quantity").getAsInt());
                if (item.getQuantity() <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\": \"La cantidad debe ser mayor que 0 en el ítem " + i + "\"}");
                    return;
                }
                items.add(item);
            }
            order.setItems(items);

            // Guardar la orden con validaciones
            orderService.addOrder(order);

            // Responder con éxito
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(order));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Formato de solicitud inválido: " + e.getMessage() + "\"}");
        }
    }
}
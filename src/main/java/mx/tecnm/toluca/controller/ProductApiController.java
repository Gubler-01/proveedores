package mx.tecnm.toluca.controller;
/*
import com.google.gson.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.service.ProductService;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "ProductApiController", urlPatterns = {"/api/products"})
public class ProductApiController extends HttpServlet {
    private final ProductService productService;
    private Gson gson;

    public ProductApiController() {
        this.productService = new ProductService();

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
        this.gson = gsonBuilder.create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Configurar la respuesta como JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Obtener todos los productos
        List<Product> products = productService.getProducts(1, Integer.MAX_VALUE); // Obtener todos los productos

        // Convertir a JSON
        String json = gson.toJson(products);

        // Enviar la respuesta
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}
*/
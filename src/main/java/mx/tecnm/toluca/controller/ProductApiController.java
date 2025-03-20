package mx.tecnm.toluca.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.service.ProductService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "ProductApiController", urlPatterns = {"/api/products"})
public class ProductApiController extends HttpServlet {
    private final ProductService productService;

    public ProductApiController() {
        this.productService = new ProductService();
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
        Gson gson = new Gson();
        String json = gson.toJson(products);

        // Enviar la respuesta
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}
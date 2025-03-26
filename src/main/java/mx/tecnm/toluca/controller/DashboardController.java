package mx.tecnm.toluca.controller;

import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.repository.ProductRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "DashboardController", urlPatterns = {"/dashboard"})
public class DashboardController extends HttpServlet {
    private ProductRepository productRepository;

    @Override
    public void init() throws ServletException {
        productRepository = new ProductRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String pageParam = request.getParameter("page");
            int page = pageParam != null ? Integer.parseInt(pageParam) : 1;
            int pageSize = 10;

            List<Product> products = productRepository.findAll(request, page, pageSize);

            long totalProducts = productRepository.count(request);
            int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
            if (totalPages == 0) totalPages = 1;

            request.setAttribute("products", products);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("/WEB-INF/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/dashboard.jsp").forward(request, response);
        }
    }
}
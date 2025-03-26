package mx.tecnm.toluca.controller;

import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.repository.ProductRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;

@WebServlet(name = "ProductController", urlPatterns = {"/products/*"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class ProductController extends HttpServlet {
    private ProductRepository productRepository;
    private static final String UPLOAD_DIR = "uploads";

    @Override
    public void init() throws ServletException {
        productRepository = new ProductRepository();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ruta no válida");
            return;
        }

        switch (pathInfo) {
            case "/add":
                handleAddProduct(request, response);
                break;
            case "/update":
                handleUpdateProduct(request, response);
                break;
            case "/delete":
                handleDeleteProduct(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
        }
    }

    private void handleAddProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadPath = applicationPath + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String name = request.getParameter("name");
            String description = request.getParameter("description");
            double price = Double.parseDouble(request.getParameter("price"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            Part imagePart = request.getPart("image");

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStock(stock);

            String imageFilePath = null;
            if (imagePart != null && imagePart.getSize() > 0) {
                String fileName = extractFileName(imagePart);
                imageFilePath = uploadPath + File.separator + fileName;
                imagePart.write(imageFilePath);
            }

            productRepository.save(request, product, imageFilePath);

            request.getSession().setAttribute("message", "Producto agregado exitosamente");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error al agregar el producto: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    private void handleUpdateProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String applicationPath = request.getServletContext().getRealPath("");
            String uploadPath = applicationPath + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String id = request.getParameter("id");
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            double price = Double.parseDouble(request.getParameter("price"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            Part imagePart = request.getPart("image");

            Product product = new Product();
            product.setId(id);
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStock(stock);

            String imageFilePath = null;
            if (imagePart != null && imagePart.getSize() > 0) {
                String fileName = extractFileName(imagePart);
                imageFilePath = uploadPath + File.separator + fileName;
                imagePart.write(imageFilePath);
            }

            productRepository.update(request, product, imageFilePath);

            request.getSession().setAttribute("message", "Producto actualizado exitosamente");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error al actualizar el producto: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    private void handleDeleteProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String id = request.getParameter("id");
            productRepository.delete(request, id);

            request.getSession().setAttribute("message", "Producto eliminado exitosamente");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error al eliminar el producto: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    private String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] items = contentDisposition.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return "";
    }
}
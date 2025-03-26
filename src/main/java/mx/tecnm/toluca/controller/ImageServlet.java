package mx.tecnm.toluca.controller;
/*
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.tecnm.toluca.service.ProductService;

import java.io.IOException;

public class ImageServlet extends HttpServlet {
    private final ProductService productService;

    public ImageServlet() {
        this.productService = new ProductService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String imageId = request.getParameter("imageId");
        if (imageId == null || imageId.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "imageId is required");
            return;
        }

        // Cambiar getImage por getProductImage
        byte[] imageData = productService.getProductImage(imageId);
        if (imageData == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Image not found");
            return;
        }

        response.setContentType("image/jpeg");
        response.setContentLength(imageData.length);
        response.getOutputStream().write(imageData);
    }
}
*/
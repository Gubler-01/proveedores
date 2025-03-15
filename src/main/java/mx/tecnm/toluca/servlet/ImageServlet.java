/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tecnm.toluca.servlet;

/**
 *
 * @author guble
 */
import mx.tecnm.toluca.service.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageServlet extends HttpServlet {
    private ProductoService productoService;

    @Override
    public void init() throws ServletException {
        productoService = new ProductoService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String imagenId = request.getParameter("imagenId");
        if (imagenId == null || imagenId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        InputStream imagenStream = productoService.obtenerImagen(imagenId);
        if (imagenStream == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("image/jpeg"); // Ajusta según el tipo de imagen
        OutputStream out = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = imagenStream.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        imagenStream.close();
        out.flush();
        out.close();
    }
}
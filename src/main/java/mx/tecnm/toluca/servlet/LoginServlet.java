/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tecnm.toluca.servlet;

/**
 *
 * @author guble
 */
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import mx.tecnm.toluca.util.MongoDBConnection;
import org.bson.Document;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        MongoCollection<Document> usuariosCollection = MongoDBConnection.getDatabase().getCollection("usuarios");

        Document usuario = usuariosCollection.find(
                Filters.and(
                        Filters.eq("username", username),
                        Filters.eq("password", password)
                )
        ).first();

        if (usuario != null) {
            HttpSession session = request.getSession();
            session.setAttribute("usuario", username);
            response.sendRedirect("ProductoServlet?accion=listar");
        } else {
            request.setAttribute("error", "Usuario o contraseña incorrectos");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
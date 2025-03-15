/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tecnm.toluca.util;

/**
 *
 * @author guble
 */
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Obtener la colección de usuarios
        MongoCollection<Document> usuariosCollection = MongoDBConnection.getDatabase().getCollection("usuarios");

        // Verificar si la colección usuarios tiene documentos
        long count = usuariosCollection.countDocuments();
        if (count == 0) {
            // La colección no existe o está vacía, creamos un usuario predeterminado
            Document usuarioAdmin = new Document("username", "admin")
                    .append("password", "admin123"); // Credenciales predeterminadas
            usuariosCollection.insertOne(usuarioAdmin);
            System.out.println("Colección 'usuarios' creada con usuario predeterminado: username=admin, password=admin123");
        } else {
            System.out.println("La colección 'usuarios' ya existe con " + count + " usuarios.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // No necesitamos hacer nada al destruir el contexto
    }
}
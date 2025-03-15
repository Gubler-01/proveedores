/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tecnm.toluca.util;

/**
 *
 * @author guble
 */
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static final String DATABASE_NAME = "proveedores_db"; // Nombre de tu base de datos

    static {
        try {
            // Conexión a MongoDB (ajusta la URI si es necesario)
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase(DATABASE_NAME);
            System.out.println("Conexión a MongoDB establecida con éxito.");
        } catch (Exception e) {
            System.err.println("Error al conectar con MongoDB: " + e.getMessage());
        }
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
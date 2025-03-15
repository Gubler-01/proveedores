/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.tecnm.toluca.dao;

/**
 *
 * @author guble
 */
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import mx.tecnm.toluca.model.Usuario;
import mx.tecnm.toluca.util.MongoDBConnection;
import org.bson.Document;

public class UsuarioDAO {
    private MongoCollection<Document> collection;

    public UsuarioDAO() {
        this.collection = MongoDBConnection.getDatabase().getCollection("usuarios");
    }

    public Usuario buscarPorUsername(String username) {
        Document doc = collection.find(Filters.eq("username", username)).first();
        if (doc != null) {
            return new Usuario(
                doc.getString("username"),
                doc.getString("password")
            );
        }
        return null;
    }
}
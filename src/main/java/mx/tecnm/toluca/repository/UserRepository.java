//package mx.tecnm.toluca.repository;
//
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.model.Filters;
//import mx.tecnm.toluca.config.MongoDBConfig;
//import mx.tecnm.toluca.model.User;
//import org.bson.Document;
//import org.bson.types.ObjectId;
//
//public class UserRepository {
//    private final MongoCollection<Document> collection;
//
//    public UserRepository() {
//        this.collection = MongoDBConfig.getDatabase().getCollection("users");
//    }
//
//    public User findByUsername(String username) {
//        Document doc = collection.find(Filters.eq("username", username)).first();
//        if (doc == null) return null;
//        User user = new User();
//        user.setId(doc.getObjectId("_id").toString());
//        user.setUsername(doc.getString("username"));
//        user.setPassword(doc.getString("password"));
//        user.setRole(doc.getString("role"));
//        user.setName(doc.getString("name"));
//        return user;
//    }
//
//    public void save(User user) {
//        Document doc = new Document("username", user.getUsername())
//                .append("password", user.getPassword())
//                .append("role", user.getRole())
//                .append("name", user.getName());
//        collection.insertOne(doc); // No necesitamos el resultado de InsertOneResult
//        user.setId(doc.getObjectId("_id").toString());
//    }
//
//    public long countUsers() {
//        return collection.countDocuments();
//    }
//}
package mx.tecnm.toluca.config;

import com.mongodb.client.MongoDatabase;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import mx.tecnm.toluca.model.User;
import mx.tecnm.toluca.repository.UserRepository;

@WebListener
public class DatabaseInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        MongoDatabase db = MongoDBConfig.getDatabase();
        UserRepository userRepository = new UserRepository();

        // Verificar si la colección "users" tiene datos, si no, crear usuarios
        if (userRepository.countUsers() == 0) {
            // Crear usuarios administradores
            User gustavo = new User("gustavo", "gustavo123", "Admin");
            gustavo.setName("Gustavo Salinas");
            userRepository.save(gustavo);

            User alejandro = new User("alejandro", "alejandro123", "Admin");
            alejandro.setName("Alejandro Linares");
            userRepository.save(alejandro);

            User daniel = new User("daniel", "daniel123", "Admin");
            daniel.setName("Daniel Ocadis");
            userRepository.save(daniel);

            System.out.println("Base de datos inicializada con 3 usuarios administradores.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Limpieza si es necesario
    }
}
package mx.tecnm.toluca.model;

public class User {
    private String id;
    private String username;
    private String password;
    private String role;
    private String name; // Nuevo campo para el nombre

    // Constructores
    public User() {}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getName() { return name; } // Getter para name
    public void setName(String name) { this.name = name; } // Setter para name
}
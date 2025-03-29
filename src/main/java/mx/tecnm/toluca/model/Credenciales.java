package mx.tecnm.toluca.model;

import jakarta.json.bind.annotation.JsonbProperty;

public class Credenciales {
    @JsonbProperty("correo")
    private String correo;
    
    @JsonbProperty("password")
    private String password;

    // Constructores
    public Credenciales() {}

    public Credenciales(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }

    // Getters y Setters
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
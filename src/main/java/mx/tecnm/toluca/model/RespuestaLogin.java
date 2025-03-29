package mx.tecnm.toluca.model;

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.List;

public class RespuestaLogin {
    @JsonbProperty("token")
    private String token;

    @JsonbProperty("roles")
    private List<String> roles;

    @JsonbProperty("correo")
    private String correo;

    // Constructores
    public RespuestaLogin() {}

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
package mx.tecnm.toluca.util;

public class TokenManager {
    private String token;
    private final DatabaseClient databaseClient;
    private final String email;
    private final String password;

    public TokenManager(DatabaseClient databaseClient, String email, String password) {
        this.databaseClient = databaseClient;
        this.email = email;
        this.password = password;
    }

    public String getToken() throws Exception {
        if (token == null) {
            refreshToken();
        }
        return token;
    }

    public void refreshToken() throws Exception {
        var response = databaseClient.authenticate(email, password);
        this.token = (String) response.get("token");
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void clearToken() {
        this.token = null;
    }
}
package mx.tecnm.toluca.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class DatabaseClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final TokenManager tokenManager;

    public DatabaseClient(String baseUrl, TokenManager tokenManager) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.tokenManager = tokenManager;
    }

    // Método para autenticación (obtener token)
    public Map<String, Object> authenticate(String email, String password) throws Exception {
        String url = baseUrl + "/api/auth/login";
        Map<String, String> credentials = Map.of("email", email, "password", password);
        String requestBody = objectMapper.writeValueAsString(credentials);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new DatabaseException("Error al autenticar: " + response.body());
        }

        return objectMapper.readValue(response.body(), Map.class);
    }

    // Crear un documento
    public void create(String collection, Map<String, Object> document) throws Exception {
        String url = baseUrl + "/api/service/" + collection;
        String requestBody = objectMapper.writeValueAsString(List.of(document));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenManager.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new DatabaseException("Error al crear documento: " + response.body());
        }
    }

    // Obtener documentos
    public List<Map<String, Object>> get(String collection, Map<String, String> filters) throws Exception {
        StringBuilder url = new StringBuilder(baseUrl + "/api/service/" + collection);
        if (filters != null && !filters.isEmpty()) {
            url.append("?");
            filters.forEach((key, value) -> url.append(key).append("=").append(value).append("&"));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString()))
                .header("Authorization", "Bearer " + tokenManager.getToken())
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new DatabaseException("Error al obtener documentos: " + response.body());
        }

        return objectMapper.readValue(response.body(), List.class);
    }

    // Actualizar un documento
    public void update(String collection, String id, Map<String, Object> document) throws Exception {
        String url = baseUrl + "/api/service/" + collection + "/" + id;
        String requestBody = objectMapper.writeValueAsString(document);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + tokenManager.getToken())
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new DatabaseException("Error al actualizar documento: " + response.body());
        }
    }

    // Eliminar un documento
    public void delete(String collection, String id) throws Exception {
        String url = baseUrl + "/api/service/" + collection + "/" + id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + tokenManager.getToken())
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new DatabaseException("Error al eliminar documento: " + response.body());
        }
    }
}
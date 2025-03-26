package mx.tecnm.toluca.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class HttpClientUtil {
    private static final HttpClient httpClient = HttpClient.newBuilder().build();

    // Método para realizar una solicitud GET
    public static String sendGetRequest(String url, String token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + (token != null ? token : ""))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    }

    // Método para realizar una solicitud POST con cuerpo JSON
    public static String sendPostRequest(String url, String token, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + (token != null ? token : ""))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    }

    // Método para realizar una solicitud POST con form-data (para subir archivos al file server)
    public static String sendPostFormDataRequest(String url, String token, String filePath, String fileName) throws IOException, InterruptedException {
        String boundary = "Boundary-" + System.currentTimeMillis();
        byte[] fileContent = Files.readAllBytes(Path.of(filePath));

        StringBuilder formDataBuilder = new StringBuilder();
        formDataBuilder.append("--").append(boundary).append("\r\n");
        formDataBuilder.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName).append("\"\r\n");
        formDataBuilder.append("Content-Type: application/octet-stream\r\n");
        formDataBuilder.append("\r\n");

        byte[] formDataStart = formDataBuilder.toString().getBytes(StandardCharsets.UTF_8);
        byte[] formDataEnd = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);

        // Combinar el cuerpo: inicio del form-data + contenido del archivo + fin del form-data
        byte[] body = new byte[formDataStart.length + fileContent.length + formDataEnd.length];
        System.arraycopy(formDataStart, 0, body, 0, formDataStart.length);
        System.arraycopy(fileContent, 0, body, formDataStart.length, fileContent.length);
        System.arraycopy(formDataEnd, 0, body, formDataStart.length + fileContent.length, formDataEnd.length);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + (token != null ? token : ""))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    }

    // Método para realizar una solicitud PUT con cuerpo JSON
    public static String sendPutRequest(String url, String token, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + (token != null ? token : ""))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    }

    // Método para realizar una solicitud DELETE
    public static String sendDeleteRequest(String url, String token) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + (token != null ? token : ""))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    }

    // Método auxiliar para manejar las respuestas y lanzar excepciones en caso de error
    private static String handleResponse(HttpResponse<String> response) throws IOException {
        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return response.body();
        } else {
            throw new IOException("Error en la solicitud HTTP: " + statusCode + " - " + response.body());
        }
    }
}
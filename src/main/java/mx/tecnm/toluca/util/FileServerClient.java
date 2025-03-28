package mx.tecnm.toluca.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class FileServerClient {
    private final String baseUrl;
    private final String fileServerToken;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public FileServerClient(String baseUrl, String fileServerToken) {
        this.baseUrl = baseUrl;
        this.fileServerToken = fileServerToken;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String uploadFile(InputStream fileStream, String fileName) throws Exception {
        String url = baseUrl + "/files/upload";
        Path tempFile = Files.createTempFile("upload-", fileName);
        Files.copy(fileStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofFile(tempFile);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + fileServerToken)
                .header("Content-Type", "multipart/form-data; boundary=---boundary")
                .POST(bodyPublisher)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Files.delete(tempFile);

        if (response.statusCode() != 200) {
            throw new FileServerException("Error al subir archivo: " + response.body());
        }

        Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
        return (String) responseBody.get("fileUrl");
    }
}
package mx.tecnm.toluca.service;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mx.tecnm.toluca.model.Product;
import mx.tecnm.toluca.util.ConfiguracionApp;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductService {
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());
    private final String baseUrl = ConfiguracionApp.getProperty("app.base.url");
    private final String serviceEndpoint = ConfiguracionApp.getProperty("app.service.endpoint");
    private final String fileServerUrl = ConfiguracionApp.getProperty("app.fileserver.url");
    private final String fileServerToken = ConfiguracionApp.getProperty("app.fileserver.token");
    private final String collection = "productos1";

    public List<Product> getAllProducts(String token) {
        Client client = ClientBuilder.newClient();
        Jsonb jsonb = JsonbBuilder.create();

        try {
            String url = baseUrl + serviceEndpoint + "/" + collection;
            LOGGER.log(Level.INFO, "Solicitando productos a la URL: {0} con token: {1}", new Object[]{url, token});
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .get();

            String responseBody = response.readEntity(String.class);
            LOGGER.log(Level.INFO, "Código HTTP: {0}, Respuesta de la API: {1}", new Object[]{response.getStatus(), responseBody});

            if (response.getStatus() == 200) {
                Product[] productsArray = jsonb.fromJson(responseBody, Product[].class);
                List<Product> products = Arrays.asList(productsArray);
                LOGGER.log(Level.INFO, "Número de productos obtenidos: {0}", products.size());
                return products.isEmpty() ? Collections.emptyList() : products;
            } else {
                LOGGER.log(Level.SEVERE, "Error al obtener productos. Código HTTP: {0}, Respuesta: {1}", 
                    new Object[]{response.getStatus(), responseBody});
                throw new RuntimeException("Error al obtener productos: Código " + response.getStatus() + " - " + responseBody);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Excepción al obtener productos", e);
            throw new RuntimeException("Error al conectar con la API: " + e.getMessage());
        } finally {
            client.close();
            try {
                jsonb.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error cerrando Jsonb", e);
            }
        }
    }

    private String uploadImage(Part filePart) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            LOGGER.log(Level.INFO, "No se proporcionó ninguna imagen para subir.");
            return null;
        }

        Client client = ClientBuilder.newClient();
        Jsonb jsonb = JsonbBuilder.create();

        try (InputStream fileInputStream = filePart.getInputStream()) {
            FormDataMultiPart formData = new FormDataMultiPart();
            formData.bodyPart(new StreamDataBodyPart("file", fileInputStream, filePart.getSubmittedFileName(), 
                    MediaType.APPLICATION_OCTET_STREAM_TYPE));

            LOGGER.log(Level.INFO, "Subiendo imagen al file server: {0}", fileServerUrl);
            Response response = client.target(fileServerUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + fileServerToken)
                    .post(Entity.entity(formData, MediaType.MULTIPART_FORM_DATA_TYPE));

            String responseBody = response.readEntity(String.class);
            LOGGER.log(Level.INFO, "Código HTTP: {0}, Respuesta del file server: {1}", new Object[]{response.getStatus(), responseBody});

            if (response.getStatus() == 200) {
                FileUploadResponse fileResponse = jsonb.fromJson(responseBody, FileUploadResponse.class);
                if (fileResponse.isSuccess()) {
                    LOGGER.log(Level.INFO, "Imagen subida exitosamente. URL: {0}", fileResponse.getFileUrl());
                    return fileResponse.getFileUrl();
                } else {
                    LOGGER.log(Level.SEVERE, "Error al subir la imagen: {0}", fileResponse.getMessage());
                    throw new RuntimeException("Error al subir la imagen: " + fileResponse.getMessage());
                }
            } else {
                LOGGER.log(Level.SEVERE, "Error al subir la imagen al file server. Código HTTP: {0}, Respuesta: {1}", 
                    new Object[]{response.getStatus(), responseBody});
                throw new RuntimeException("Error al subir la imagen: Código " + response.getStatus() + " - " + responseBody);
            }
        } finally {
            client.close();
            try {
                jsonb.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error cerrando Jsonb", e);
            }
        }
    }

    public void addProduct(HttpServletRequest request, String token) throws IOException, ServletException {
        Product product = new Product();
        product.setNombre(new String(request.getPart("nombre").getInputStream().readAllBytes()));
        product.setDescripcion(new String(request.getPart("descripcion").getInputStream().readAllBytes()));
        double precio = Double.parseDouble(new String(request.getPart("precio").getInputStream().readAllBytes()));
        int stock = Integer.parseInt(new String(request.getPart("stock").getInputStream().readAllBytes()));
        
        // Validaciones
        if (precio < 0) {
            LOGGER.log(Level.WARNING, "Intento de agregar producto con precio negativo: {0}", precio);
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        if (stock < 0) {
            LOGGER.log(Level.WARNING, "Intento de agregar producto con stock negativo: {0}", stock);
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }

        product.setPrecio(precio);
        product.setStock(stock);
        product.setCategoria("Blancos");
        product.setStatus(new String(request.getPart("status").getInputStream().readAllBytes()));

        Part filePart = request.getPart("imagen");
        String imageUrl = uploadImage(filePart);
        product.setImagen(imageUrl);

        List<Product> productList = Collections.singletonList(product);

        Client client = ClientBuilder.newClient();
        Jsonb jsonb = JsonbBuilder.create();

        try {
            String url = baseUrl + serviceEndpoint + "/" + collection;
            LOGGER.log(Level.INFO, "Agregando producto a la URL: {0}", url);
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .post(Entity.json(productList));

            String responseBody = response.readEntity(String.class);
            LOGGER.log(Level.INFO, "Código HTTP: {0}, Respuesta de la API: {1}", new Object[]{response.getStatus(), responseBody});

            if (response.getStatus() == 200) {
                ResponseMessage responseMessage = jsonb.fromJson(responseBody, ResponseMessage.class);
                if ("success".equals(responseMessage.getStatus()) && responseMessage.getHttpCode() == 200) {
                    LOGGER.log(Level.INFO, "Producto agregado correctamente: {0}", responseBody);
                } else {
                    LOGGER.log(Level.SEVERE, "Error en la respuesta de la API: {0}", responseBody);
                    throw new RuntimeException(responseMessage.getMessage());
                }
            } else {
                LOGGER.log(Level.SEVERE, "Error al agregar producto: Código HTTP: {0}, Respuesta: {1}", 
                    new Object[]{response.getStatus(), responseBody});
                throw new RuntimeException("Error al agregar producto: Código " + response.getStatus() + " - " + responseBody);
            }
        } finally {
            client.close();
            try {
                jsonb.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error cerrando Jsonb", e);
            }
        }
    }

    public void updateProduct(HttpServletRequest request, String token) throws IOException, ServletException {
        String id = new String(request.getPart("id").getInputStream().readAllBytes());
        Product product = new Product();
        product.setNombre(new String(request.getPart("nombre").getInputStream().readAllBytes()));
        product.setDescripcion(new String(request.getPart("descripcion").getInputStream().readAllBytes()));
        double precio = Double.parseDouble(new String(request.getPart("precio").getInputStream().readAllBytes()));
        int stock = Integer.parseInt(new String(request.getPart("stock").getInputStream().readAllBytes()));
        
        // Validaciones
        if (precio < 0) {
            LOGGER.log(Level.WARNING, "Intento de actualizar producto con precio negativo: {0}", precio);
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        if (stock < 0) {
            LOGGER.log(Level.WARNING, "Intento de actualizar producto con stock negativo: {0}", stock);
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }

        product.setPrecio(precio);
        product.setStock(stock);
        product.setCategoria("Blancos");
        product.setStatus(new String(request.getPart("status").getInputStream().readAllBytes()));

        Part filePart = request.getPart("imagen");
        String imageUrl = uploadImage(filePart);
        if (imageUrl != null) {
            product.setImagen(imageUrl);
        } else {
            String existingImagen = new String(request.getPart("existingImagen").getInputStream().readAllBytes());
            product.setImagen(existingImagen.isEmpty() ? null : existingImagen);
        }

        Client client = ClientBuilder.newClient();
        Jsonb jsonb = JsonbBuilder.create();

        try {
            String url = baseUrl + serviceEndpoint + "/" + collection + "/" + id;
            LOGGER.log(Level.INFO, "Actualizando producto en la URL: {0}", url);
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .put(Entity.json(product));

            String responseBody = response.readEntity(String.class);
            LOGGER.log(Level.INFO, "Código HTTP: {0}, Respuesta de la API: {1}", new Object[]{response.getStatus(), responseBody});

            if (response.getStatus() == 200) {
                ResponseMessage responseMessage = jsonb.fromJson(responseBody, ResponseMessage.class);
                if ("succes".equals(responseMessage.getStatus()) && responseMessage.getHttpCode() == 200) {
                    LOGGER.log(Level.INFO, "Producto actualizado correctamente: {0}", responseBody);
                } else {
                    LOGGER.log(Level.SEVERE, "Error en la respuesta de la API: {0}", responseBody);
                    throw new RuntimeException(responseMessage.getMessage());
                }
            } else {
                LOGGER.log(Level.SEVERE, "Error al actualizar producto: Código HTTP: {0}, Respuesta: {1}", 
                    new Object[]{response.getStatus(), responseBody});
                throw new RuntimeException("Error al actualizar producto: Código " + response.getStatus() + " - " + responseBody);
            }
        } finally {
            client.close();
            try {
                jsonb.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error cerrando Jsonb", e);
            }
        }
    }

    public void deleteProduct(String id, String token) {
        Client client = ClientBuilder.newClient();
        try {
            String url = baseUrl + serviceEndpoint + "/" + collection + "/" + id;
            LOGGER.log(Level.INFO, "Eliminando producto en la URL: {0}", url);
            Response response = client.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .header(ConfiguracionApp.getProperty("app.token.header"), "Bearer " + token)
                    .delete();

            String responseBody = response.readEntity(String.class);
            LOGGER.log(Level.INFO, "Código HTTP: {0}, Respuesta de la API: {1}", new Object[]{response.getStatus(), responseBody});

            if (response.getStatus() != 200) {
                LOGGER.log(Level.SEVERE, "Error al eliminar producto: {0}", responseBody);
                throw new RuntimeException("Error al eliminar producto: " + responseBody);
            }
            LOGGER.log(Level.INFO, "Producto eliminado correctamente en {0}", url);
        } finally {
            client.close();
        }
    }

    public static class ResponseMessage {
        private int httpCode;
        private String message;
        private String status;

        public int getHttpCode() { return httpCode; }
        public void setHttpCode(int httpCode) { this.httpCode = httpCode; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class FileUploadResponse {
        private String fileUrl;
        private boolean success;
        private String fileName;
        private String message;

        public String getFileUrl() { return fileUrl; }
        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
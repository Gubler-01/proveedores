package mx.tecnm.toluca.util;

public class HttpClientUtilTest {
    public static void main(String[] args) {
        try {
            // Obtener la URL del servicio de autenticación desde application.properties
            String authUrl = ConfigUtil.getProperty("auth.service.url");
            System.out.println("URL de autenticación: " + authUrl);

            // Cuerpo JSON con las credenciales (ajusta el correo y la contraseña según tus datos reales)
            String jsonBody = "{\"correo\": \"usuario3@ejemplo.com\", \"password\": \"contrasenia3\"}";

            // Realizar la solicitud POST al endpoint de autenticación
            // No enviamos token porque esta es la solicitud inicial para obtener el token
            String response = HttpClientUtil.sendPostRequest(authUrl, null, jsonBody);
            System.out.println("Respuesta del servicio de autenticación: " + response);

            // Opcional: Parsear la respuesta para extraer el token (esto dependerá del formato de la respuesta)
            // Por ejemplo, si la respuesta es: {"token": "jwt-token-aqui", "roles": ["ROLE_USER"], "correo": "test@example.com"}
            // Podrías usar una librería como Jackson o Gson para parsear el JSON y extraer el token.
        } catch (Exception e) {
            System.err.println("Error al realizar la solicitud: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
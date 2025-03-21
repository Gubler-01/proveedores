# Guía de Uso de la API de Proveedores

Bienvenido al proyecto de la API de Proveedores. Esta API permite gestionar órdenes y productos mediante operaciones HTTP simples. A continuación, encontrarás una guía detallada para configurar, ejecutar y utilizar esta API.

## Tecnologías Utilizadas

- **Lenguaje**: Java 21
- **Gestor de Dependencias**: Maven
- **Servidor de Aplicaciones**: Payara 6
- **IDE Recomendado**: NetBeans 24
- **Base de Datos**: MongoDB Atlas
- **Herramienta de Túnel (Opcional)**: Ngrok (para exponer el servidor local a internet)

## Requisitos Previos

Antes de comenzar, asegúrate de tener instalado lo siguiente:

1. **Java 21**: Descarga e instala el JDK 21 desde [el sitio oficial de Oracle](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) o usa un gestor como SDKMAN.
2. **Maven**: Instala Maven desde [el sitio oficial](https://maven.apache.org/download.cgi) o mediante tu gestor de paquetes favorito.
3. **Payara 6**: Descarga Payara Server 6 desde [el sitio oficial](https://www.payara.fish/page/downloads/). Recomendamos la versión completa (*Full Platform*).
4. **NetBeans 24**: Descarga e instala NetBeans 24 desde [el sitio oficial](https://netbeans.apache.org/download/index.html).
5. **MongoDB Atlas**: Crea una cuenta en [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) y configura un clúster gratuito para la base de datos.
6. **Ngrok (Opcional)**: Si deseas exponer tu servidor local a internet, descarga Ngrok desde [su sitio oficial](https://ngrok.com/download).

## Configuración del Proyecto

### 1. Clonar o Crear el Proyecto
Si ya tienes un repositorio, clónalo. De lo contrario, crea un nuevo proyecto Maven en NetBeans:

- Abre NetBeans 24.
- Selecciona `File > New Project > Maven > Java Application`.
- Asigna un nombre al proyecto (ejemplo: `proveedores-api`).
- Configura el `Group Id` (ejemplo: `com.miempresa`) y `Version` (ejemplo: `1.0-SNAPSHOT`).

### 2. Configurar el `pom.xml`
Asegúrate de que tu archivo `pom.xml` incluya las dependencias necesarias para Java 21, Payara 6 y MongoDB. Aquí tienes un ejemplo:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.miempresa</groupId>
    <artifactId>proveedores-api</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <payara.version>6.2023.10</payara.version>
    </properties>

    <dependencies>
        <!-- Jakarta EE para Payara -->
        <dependency>
            <groupId>jakarta.platform</groupId>
            <artifactId>jakarta.jakartaee-api</artifactId>
            <version>10.0.0</version>
            <scope>provided</scope>
        </dependency>
        <!-- Driver de MongoDB -->
        <dependency>
            <groupId>org.mongodb</groupId>
            <artifactId>mongodb-driver-sync</artifactId>
            <version>4.11.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.4.0</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### 3. Configurar MongoDB Atlas
- Inicia sesión en MongoDB Atlas y crea un clúster.
- Genera una URI de conexión (ejemplo: `mongodb+srv://<usuario>:<contraseña>@cluster0.mongodb.net/<dbname>?retryWrites=true&w=majority`).
- Configura la URI en tu proyecto, ya sea en un archivo de propiedades (`src/main/resources/application.properties`) o directamente en el código.

Ejemplo de `application.properties`:
```
mongodb.uri=mongodb+srv://<usuario>:<contraseña>@cluster0.mongodb.net/proveedores?retryWrites=true&w=majority
```

### 4. Configurar Payara 6
- Descomprime el archivo de Payara 6 descargado.
- Inicia el servidor ejecutando:
  ```bash
  ./payara6/bin/asadmin start-domain
  ```
- Accede al panel de administración en `http://localhost:4848` para verificar que el servidor esté corriendo.

### 5. Desplegar la Aplicación
- En NetBeans, haz clic derecho en el proyecto y selecciona `Build`.
- Copia el archivo `.war` generado en `target/` al directorio de despliegue de Payara:
  ```bash
  cp target/proveedores-api-1.0-SNAPSHOT.war payara6/glassfish/domains/domain1/autodeploy/
  ```
- Verifica en el log de Payara que la aplicación se haya desplegado correctamente.

## Endpoints de la API

La API ofrece dos recursos principales: **órdenes** y **productos**. A continuación, se detallan los endpoints disponibles y cómo usarlos.

### 1. Órdenes (`/proveedores/api/orders`)

#### Crear una Orden (POST)
- **URL**: `http://localhost:8080/proveedores/api/orders`
- **Método**: `POST`
- **Formato del cuerpo (JSON)**:
```json
{
    "customerId": "Juan Perez",
    "items": [
        {"productId": "PROD-BLANCO-2", "quantity": 1},
        {"productId": "PROD-BLANCO-3", "quantity": 1},
        {"productId": "PROD-BLANCO-7", "quantity": 1},
        {"productId": "PROD-BLANCO-8", "quantity": 10}
    ]
}
```
- **Descripción**: Crea una nueva orden con el ID del cliente y una lista de ítems (producto y cantidad).
- **Respuesta Exitosa**: Código `201 Created` con el ID de la orden generada.

#### Obtener Órdenes (GET)
- **URL**: `http://localhost:8080/proveedores/api/orders`
- **Método**: `GET`
- **Descripción**: Devuelve una lista de todas las órdenes registradas.
- **Respuesta Exitosa**: Código `200 OK` con una lista en formato JSON.

### 2. Productos (`/proveedores/api/products`)

#### Obtener Productos (GET)
- **URL**: `http://localhost:8080/proveedores/api/products`
- **Método**: `GET`
- **Descripción**: Devuelve una lista de todos los productos disponibles.
- **Respuesta Exitosa**: Código `200 OK` con una lista en formato JSON.

### Uso con Ngrok (Opcional)
Si deseas exponer tu servidor local a internet:
1. Inicia Ngrok:
   ```bash
   ngrok http 8080
   ```
2. Copia la URL generada por Ngrok (ejemplo: `https://abc123.ngrok.io`).
3. Reemplaza `http://localhost:8080` por la URL de Ngrok en los endpoints:
   - Órdenes: `https://abc123.ngrok.io/proveedores/api/orders`
   - Productos: `https://abc123.ngrok.io/proveedores/api/products`

## Ejemplo de Uso con cURL

### Crear una Orden
```bash
curl -X POST http://localhost:8080/proveedores/api/orders \
-H "Content-Type: application/json" \
-d '{
    "customerId": "Juan Perez",
    "items": [
        {"productId": "PROD-BLANCO-2", "quantity": 1},
        {"productId": "PROD-BLANCO-3", "quantity": 1}
    ]
}'
```

### Obtener Productos
```bash
curl -X GET http://localhost:8080/proveedores/api/products
```

## Resolución de Problemas

- **Error de conexión a MongoDB**: Verifica la URI en `application.properties` y asegúrate de que el clúster esté activo.
- **Payara no despliega la aplicación**: Revisa los logs en `payara6/glassfish/domains/domain1/logs/server.log`.
- **Ngrok no funciona**: Asegúrate de que el puerto 8080 esté libre y que Ngrok esté autenticado con tu token.

## Contribuciones

Si deseas contribuir, crea un *pull request* en el repositorio con una descripción clara de los cambios.

---

# Sistema de Gestión de Proveedores

Bienvenido(a) al **Sistema de Gestión de Proveedores**, una aplicación diseñada para facilitar la gestión de productos y órdenes para proveedores. Este sistema permite a los usuarios autenticados (proveedores) administrar sus productos, visualizar y gestionar órdenes de clientes, y mantener un registro de auditoría para cada orden. A continuación, encontrarás una descripción detallada del sistema, sus tecnologías, URLs de acceso, y cómo interactuar con la API para crear órdenes.

---

## Descripción General

El Sistema de Gestión de Proveedores es una aplicación web que permite a los proveedores:
- **Gestionar Productos**: Crear, actualizar y eliminar productos, incluyendo detalles como nombre, descripción, precio, stock e imágenes.
- **Administrar Órdenes**: Visualizar órdenes de clientes, aceptarlas o rechazarlas, actualizar su estado, y consultar un historial de auditoría para cada orden.
- **Seguridad**: Acceso restringido mediante autenticación de usuarios.
- **Escalabilidad**: Diseñado para manejar múltiples órdenes y productos con una base de datos en la nube.

La aplicación está diseñada con un enfoque en la usabilidad, permitiendo a los proveedores gestionar su inventario y órdenes de manera eficiente a través de una interfaz web amigable.

---

## Tecnologías Utilizadas

El sistema está construido con las siguientes tecnologías modernas:

- **Java 21**: Lenguaje de programación principal, utilizando las últimas características de Java para un código robusto y eficiente.
- **Maven**: Herramienta de gestión de dependencias y construcción del proyecto, asegurando una gestión sencilla de bibliotecas y compilación.
- **Payara Server 6.2025.1**: Servidor de aplicaciones Java EE, utilizado para desplegar la aplicación y manejar las solicitudes HTTP.
- **NetBeans 24**: Entorno de desarrollo integrado (IDE) utilizado para el desarrollo, depuración y prueba del sistema.
- **MongoDB Atlas**: Base de datos NoSQL en la nube, utilizada para almacenar productos, órdenes y registros de auditoría. MongoDB Atlas proporciona escalabilidad y alta disponibilidad.
- **Bootstrap 5.3.0**: Framework CSS para el diseño de la interfaz de usuario, asegurando una experiencia responsive y moderna.
- **JSP (JavaServer Pages)**: Tecnología para la generación de páginas web dinámicas en el lado del servidor.
- **GridFS (MongoDB)**: Utilizado para almacenar y recuperar imágenes de productos de manera eficiente.

---

## Requisitos del Sistema

Para ejecutar o desarrollar este sistema, necesitas:

- **Java 21**: Asegúrate de tener el JDK 21 instalado.
- **Maven**: Instala Maven para gestionar dependencias y compilar el proyecto.
- **Payara Server 6.2025.1**: Descarga e instala Payara Server para desplegar la aplicación.
- **NetBeans 24**: Opcional, pero recomendado para un desarrollo más sencillo.
- **MongoDB Atlas**: Una cuenta en MongoDB Atlas para la base de datos. Configura la conexión en `ProductRepository.java` y `OrderRepository.java` con tu URI de MongoDB Atlas.
- **Conexión a Internet**: Necesaria para acceder a MongoDB Atlas y, si usas Ngrok, para exponer tu servidor local.

---

## URLs de Acceso

El sistema proporciona varias URLs para interactuar con la API y la interfaz web. A continuación, se detallan las URLs principales:

### API para Órdenes
- **URL Local**: `http://localhost:8080/proveedores/api/orders`
- **Métodos Soportados**:
  - **GET**: Obtiene una lista de todas las órdenes existentes en formato JSON.
    - **Ejemplo de Respuesta**:
      ```json
      [
          {
              "id": "ORDER-BLANCO-1",
              "customerId": "Juan Perez",
              "orderDate": "2025-03-21T10:00:00",
              "items": [
                  {"productId": "PROD-BLANCO-2", "quantity": 1, "unitPrice": 50.0},
                  {"productId": "PROD-BLANCO-3", "quantity": 1, "unitPrice": 75.0}
              ],
              "subtotal": 125.0,
              "total": 137.5,
              "status": "PENDING"
          }
      ]
      ```
  - **POST**: Crea una nueva orden. Debes enviar un cuerpo JSON con los detalles de la orden.
    - **Formato del Cuerpo de la Solicitud**:
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
    - **Notas**:
      - `customerId`: Nombre o identificador del cliente que realiza la orden.
      - `items`: Lista de ítems de la orden, donde cada ítem incluye el `productId` (ID del producto) y la `quantity` (cantidad solicitada).
      - El sistema valida automáticamente el stock disponible para cada producto. Si no hay suficiente stock, la creación fallará con un error.
    - **Ejemplo de Respuesta Exitosa**:
      ```json
      {
          "id": "ORDER-BLANCO-2",
          "customerId": "Juan Perez",
          "orderDate": "2025-03-21T10:05:00",
          "items": [
              {"productId": "PROD-BLANCO-2", "quantity": 1, "unitPrice": 50.0},
              {"productId": "PROD-BLANCO-3", "quantity": 1, "unitPrice": 75.0}
          ],
          "subtotal": 125.0,
          "total": 137.5,
          "status": "PENDING"
      }
      ```
    - **Errores Posibles**:
      - `400 Bad Request`: Si el formato del cuerpo es inválido, falta el `customerId`, o los ítems son incorrectos.
        ```json
        {"error": "Falta el campo customerId"}
        ```
      - `400 Bad Request`: Si no hay suficiente stock para un producto.
        ```json
        {"error": "Stock insuficiente para el producto PROD-BLANCO-8. Stock disponible: 5"}
        ```

- **URL con Ngrok (si aplica)**:
  - Si estás usando Ngrok para exponer tu servidor local, la URL será algo como:
    ```
    https://<tu-id>.ngrok.io/proveedores/api/orders
    ```
  - Reemplaza `<tu-id>` con el ID proporcionado por Ngrok al iniciar el túnel.

### API para Productos
- **URL Local**: `http://localhost:8080/proveedores/api/products`
- **Métodos Soportados**:
  - **GET**: Obtiene una lista de todos los productos disponibles en formato JSON.
    - **Ejemplo de Respuesta**:
      ```json
      [
          {
              "id": "PROD-BLANCO-1",
              "name": "Producto 1",
              "description": "Descripción del producto 1",
              "price": 50.0,
              "stock": 10,
              "imageId": "12345"
          },
          {
              "id": "PROD-BLANCO-2",
              "name": "Producto 2",
              "description": "Descripción del producto 2",
              "price": 75.0,
              "stock": 20,
              "imageId": "67890"
          }
      ]
      ```

- **URL con Ngrok (si aplica)**:
  - Si estás usando Ngrok, la URL será:
    ```
    https://<tu-id>.ngrok.io/proveedores/api/products
    ```

### Interfaz Web para Proveedores
- **Dashboard de Productos**: `http://localhost:8080/proveedores/dashboard`
  - Permite a los proveedores autenticados gestionar sus productos (agregar, editar, eliminar).
- **Gestión de Órdenes**: `http://localhost:8080/proveedores/orders`
  - Permite a los proveedores visualizar órdenes, aceptarlas, rechazarlas, actualizar su estado y ver la auditoría.
- **Auditoría de Órdenes**: `http://localhost:8080/proveedores/orders/audit?orderId=<id>`
  - Muestra el historial de auditoría para una orden específica.

---

## Instrucciones de Uso

### 1. Configuración Inicial
1. **Clonar el Repositorio**:
   - Clona el repositorio del proyecto a tu máquina local.
     ```bash
     git clone <url-del-repositorio>
     ```
2. **Configurar MongoDB Atlas**:
   - Crea una cuenta en [MongoDB Atlas](https://www.mongodb.com/cloud/atlas).
   - Crea un clúster y obtén la URI de conexión (por ejemplo, `mongodb+srv://<usuario>:<contraseña>@cluster0.mongodb.net/supplier_db`).
   - Actualiza la URI de conexión en los archivos `ProductRepository.java` y `OrderRepository.java`:
     ```java
     MongoClient mongoClient = MongoClients.create("mongodb+srv://<usuario>:<contraseña>@cluster0.mongodb.net/supplier_db");
     ```
3. **Compilar el Proyecto**:
   - Abre el proyecto en NetBeans 24.
   - Usa Maven para compilar el proyecto:
     ```bash
     mvn clean install
     ```
4. **Desplegar en Payara Server**:
   - Configura Payara Server 6.2025.1 en NetBeans.
   - Despliega el archivo WAR generado (`target/proveedores.war`) en Payara Server.
5. **Iniciar el Servidor**:
   - Inicia Payara Server desde NetBeans o manualmente:
     ```bash
     <ruta-a-payara>/bin/asadmin start-domain
     ```
   - La aplicación estará disponible en `http://localhost:8080/proveedores`.

### 2. Acceso a la Interfaz Web
1. **Iniciar Sesión**:
   - Navega a `http://localhost:8080/proveedores/index.jsp`.
   - Ingresa las credenciales de un proveedor (por ejemplo, usuario: `proveedor`, contraseña: `1234`).
   - Si no tienes un usuario registrado, necesitarás crear uno (esto puede requerir ajustes en el código según tu implementación de autenticación).
2. **Gestionar Productos**:
   - Una vez autenticado, serás redirigido al dashboard (`/dashboard`).
   - Aquí puedes:
     - **Agregar un Producto**: Completa el formulario con nombre, descripción, precio, stock e imagen (opcional).
     - **Editar un Producto**: Haz clic en "Editar" para modificar los detalles de un producto existente.
     - **Eliminar un Producto**: Haz clic en "Eliminar" (solo disponible si el producto no tiene órdenes pendientes).
3. **Gestionar Órdenes**:
   - Navega a `/orders` desde el dashboard.
   - Verás una lista de órdenes con las siguientes acciones:
     - **Aceptar/Rechazar**: Disponible para órdenes en estado `PENDING` que no hayan sido procesadas.
     - **Editar Estado**: Cambia el estado de una orden (por ejemplo, de `ACCEPTED` a `COMPLETED`).
     - **Ver Auditoría**: Consulta el historial de acciones realizadas sobre una orden.

### 3. Uso de la API

#### Crear una Orden (POST `/proveedores/api/orders`)
- Usa una herramienta como Postman o cURL para enviar una solicitud POST a la API de órdenes.
- **Ejemplo con cURL**:
  ```bash
  curl -X POST http://localhost:8080/proveedores/api/orders \
  -H "Content-Type: application/json" \
  -d '{
      "customerId": "Juan Perez",
      "items": [
          {"productId": "PROD-BLANCO-2", "quantity": 1},
          {"productId": "PROD-BLANCO-3", "quantity": 1},
          {"productId": "PROD-BLANCO-7", "quantity": 1},
          {"productId": "PROD-BLANCO-8", "quantity": 10}
      ]
  }'
  ```
- **Notas**:
  - Asegúrate de que los `productId` correspondan a productos existentes en la base de datos.
  - Verifica que haya suficiente stock para cada producto. Si no, recibirás un error 400.

#### Obtener Órdenes (GET `/proveedores/api/orders`)
- Envía una solicitud GET para obtener todas las órdenes.
- **Ejemplo con cURL**:
  ```bash
  curl http://localhost:8080/proveedores/api/orders
  ```

#### Obtener Productos (GET `/proveedores/api/products`)
- Envía una solicitud GET para obtener todos los productos.
- **Ejemplo con cURL**:
  ```bash
  curl http://localhost:8080/proveedores/api/products
  ```

#### Uso con Ngrok
Si estás usando Ngrok para exponer tu servidor local:
1. Inicia Ngrok:
   ```bash
   ngrok http 8080
   ```
2. Obtendrás una URL pública (por ejemplo, `https://abcd1234.ngrok.io`).
3. Usa esta URL para acceder a la API:
   - Órdenes: `https://abcd1234.ngrok.io/proveedores/api/orders`
   - Productos: `https://abcd1234.ngrok.io/proveedores/api/products`

---

## Características del Sistema

### Gestión de Productos
- **Crear Productos**: Los proveedores pueden agregar nuevos productos con nombre, descripción, precio, stock e imagen.
- **Editar Productos**: Permite modificar los detalles de un producto existente, pero no si tiene órdenes pendientes (excepto el stock).
- **Eliminar Productos**: Los productos solo se pueden eliminar si no tienen órdenes pendientes.
- **Almacenamiento de Imágenes**: Las imágenes de los productos se almacenan en MongoDB usando GridFS.

### Gestión de Órdenes
- **Creación de Órdenes**: Los clientes (o sistemas externos) pueden crear órdenes a través de la API.
- **Aceptar/Rechazar Órdenes**: Los proveedores pueden aceptar o rechazar órdenes en estado `PENDING`.
  - Al aceptar una orden, el stock de los productos se reduce.
  - Al rechazar una orden, el estado cambia a `REJECTED` y luego a `CANCELLED`, sin afectar el stock.
- **Actualizar Estado**: Los proveedores pueden cambiar el estado de una orden (por ejemplo, de `ACCEPTED` a `COMPLETED`).
- **Auditoría**: Cada acción sobre una orden (creación, aceptación, rechazo, actualización) se registra en un historial de auditoría.

### Seguridad
- La interfaz web requiere autenticación para acceder al dashboard y a la gestión de órdenes.
- Las rutas de la API (`/api/orders`, `/api/products`) están disponibles públicamente para permitir la integración con sistemas externos, pero se recomienda agregar autenticación (por ejemplo, mediante tokens JWT) para un entorno de producción.

---

## Limitaciones y Consideraciones

- **Autenticación de la API**: Actualmente, las rutas `/api/orders` y `/api/products` no requieren autenticación. Para un entorno de producción, considera implementar autenticación (por ejemplo, con OAuth2 o JWT).
- **Rendimiento**: Las consultas a MongoDB Atlas no están optimizadas con índices. Para un sistema con muchas órdenes y productos, se recomienda agregar índices en los campos más utilizados (como `items.productId` y `status` en la colección de órdenes).
- **Escalabilidad**: Payara Server y MongoDB Atlas son escalables, pero asegúrate de configurar correctamente los recursos del clúster en MongoDB Atlas para manejar cargas altas.
- **Validación de Stock**: El sistema valida el stock al crear y aceptar órdenes, pero no implementa un mecanismo de bloqueo para evitar condiciones de carrera en entornos concurrentes. Esto podría resolverse con transacciones en MongoDB.

---

## Problemas Conocidos y Soluciones

1. **Error 500 al Aceptar/Rechazar Órdenes Posteriores**:
   - **Problema**: Al aceptar o rechazar órdenes posteriores a la primera, se producía un error 500 si el producto tenía otras órdenes pendientes.
   - **Solución**: Se modificó `ProductRepository.java` para permitir actualizaciones del stock incluso si hay órdenes pendientes, pero se mantuvo la restricción para otros campos (nombre, descripción, precio).

2. **Error 500 al Editar Productos con Órdenes Pendientes**:
   - **Problema**: Al intentar editar un producto con órdenes pendientes, se producía un error 500.
   - **Solución**: Se agregó manejo de excepciones en `ProductController.java` para mostrar un mensaje de error al usuario en lugar de un error 500.

---

## Contribuciones

Si deseas contribuir al desarrollo del sistema:
1. Clona el repositorio y crea una rama para tus cambios:
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
2. Realiza tus cambios y compila el proyecto con Maven.
3. Prueba tus cambios en un entorno local.
4. Envía un pull request con una descripción detallada de tus cambios.

---

## Contacto

Para soporte o preguntas, contacta al equipo de desarrollo:
- **Correo**: soporte@proveedores.com
- **Slack**: #canal-proveedores

Gracias por usar el Sistema de Gestión de Proveedores. ¡Esperamos que te sea útil! 🚀

--- 

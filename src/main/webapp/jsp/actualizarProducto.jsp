<%--
    Document   : actualizarProducto
    Created on : Mar 13, 2025, 6:51:19 PM
    Author     : guble
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="mx.tecnm.toluca.model.Producto" %>
<%@ page session="true" %>
<%
    if (session == null || session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<html>
<head>
    <title>Actualizar Producto</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <div class="container">
        <h1>Actualizar Producto</h1>
        <% Producto producto = (Producto) request.getAttribute("producto"); %>
        <form action="${pageContext.request.contextPath}/ProductoServlet" method="post" enctype="multipart/form-data">
            <input type="hidden" name="accion" value="actualizar">
            <input type="hidden" name="id" value="<%= producto.getId() %>">
            <input type="hidden" name="imagenActual" value="<%= producto.getImagenId() %>">
            <label>ID:</label><input type="text" name="id" value="<%= producto.getId() %>" readonly>
            <label>Nombre:</label><input type="text" name="nombre" value="<%= producto.getNombre() %>" required>
            <label>Descripción:</label><input type="text" name="descripcion" value="<%= producto.getDescripcion() %>" required>
            <label>Precio:</label><input type="number" name="precio" step="0.01" value="<%= producto.getPrecio() %>" required>
            <label>Stock:</label><input type="number" name="stock" value="<%= producto.getStock() %>" required>
            <label>Imagen Actual:</label>
            <img src="${pageContext.request.contextPath}/ImageServlet?imagenId=<%= producto.getImagenId() != null ? producto.getImagenId() : "" %>" alt="Producto Actual" style="max-width: 100px;" onerror="this.src='${pageContext.request.contextPath}/images/default.jpg';">
            <label>Nueva Imagen (opcional):</label><input type="file" name="imagen" accept="image/*">
            <input type="submit" value="Actualizar">
        </form>
        <% if (request.getAttribute("mensaje") != null) { %>
            <p class="mensaje-exito"><%= request.getAttribute("mensaje") %></p>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <p class="mensaje-error"><%= request.getAttribute("error") %></p>
        <% } %>
        <a href="${pageContext.request.contextPath}/ProductoServlet?accion=listar">Volver a la lista</a>
    </div>

    <!-- Modal -->
    <% if (request.getAttribute("showPopup") != null && (Boolean) request.getAttribute("showPopup")) { %>
        <div id="myModal" class="modal" style="display: flex;">
            <div class="modal-content">
                <p><%= request.getAttribute("mensaje") %></p>
                <button class="modal-button" onclick="closeModal()">Aceptar</button>
            </div>
        </div>
        <script type="text/javascript">
            function closeModal() {
                document.getElementById('myModal').style.display = 'none';
                window.location.href = "${pageContext.request.contextPath}/ProductoServlet?accion=listar";
            }
        </script>
    <% } %>
</body>
</html>
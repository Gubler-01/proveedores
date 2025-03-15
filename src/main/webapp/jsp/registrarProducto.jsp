<%--
    Document   : registrarProducto
    Created on : Mar 13, 2025, 6:44:20 PM
    Author     : guble
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%
    if (session == null || session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<html>
<head>
    <title>Registrar Producto</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <div class="container">
        <h1>Registrar Producto</h1>
        <form action="${pageContext.request.contextPath}/ProductoServlet" method="post" enctype="multipart/form-data">
            <input type="hidden" name="accion" value="registrar">
            <label>Nombre:</label><input type="text" name="nombre" required>
            <label>Descripción:</label><input type="text" name="descripcion" required>
            <label>Precio:</label><input type="number" name="precio" step="0.01" required>
            <label>Stock:</label><input type="number" name="stock" required>
            <label>Imagen:</label><input type="file" name="imagen" accept="image/*" required>
            <input type="submit" value="Registrar">
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
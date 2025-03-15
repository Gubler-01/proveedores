<%--
    Document   : registrarOrden
    Created on : Mar 15, 2025
    Author     : guble
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="mx.tecnm.toluca.model.Producto" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page session="true" %>
<%
    if (session == null || session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    List<Producto> productosDisponibles = new ArrayList<>();
    com.mongodb.client.MongoCollection<org.bson.Document> productosCollection = mx.tecnm.toluca.util.MongoDBConnection.getDatabase().getCollection("productos");
    for (org.bson.Document doc : productosCollection.find()) {
        Producto p = new Producto();
        p.setId(doc.getObjectId("_id").toString());
        p.setNombre(doc.getString("nombre"));
        p.setPrecio(doc.getDouble("precio"));
        productosDisponibles.add(p);
    }
%>
<html>
<head>
    <title>Registrar Orden de Compra</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <div class="container">
        <h1>Registrar Orden de Compra</h1>
        <form action="${pageContext.request.contextPath}/OrdenCompraServlet" method="post">
            <input type="hidden" name="accion" value="registrar">
            <label>Proveedor:</label>
            <input type="text" value="Proveedor 1" readonly>
            <label>Productos:</label>
            <div id="productos-container">
                <% if (productosDisponibles != null && !productosDisponibles.isEmpty()) { %>
                    <% for (Producto p : productosDisponibles) { %>
                        <div class="producto-item">
                            <input type="checkbox" name="productoId" value="<%= p.getId() %>">
                            <label><%= p.getNombre() %> - $<%= String.format("%.2f", p.getPrecio()) %></label>
                            <input type="number" name="cantidad" min="1" placeholder="Cantidad" disabled>
                        </div>
                    <% } %>
                <% } else { %>
                    <p>No hay productos disponibles.</p>
                <% } %>
            </div>
            <label>Tiempo de Entrega Estimado (días):</label>
            <input type="number" name="tiempoEntregaDias" min="1" required>
            <label>Notas:</label>
            <textarea name="notas" rows="3" placeholder="Escribe notas o comentarios (opcional)"></textarea>
            <input type="submit" value="Registrar Orden">
        </form>
        <a href="${pageContext.request.contextPath}/OrdenCompraServlet?accion=listar" class="back-button">Volver a la lista</a>
        <% if (request.getAttribute("mensaje") != null) { %>
            <p class="mensaje-exito"><%= request.getAttribute("mensaje") %></p>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <p class="mensaje-error"><%= request.getAttribute("error") %></p>
        <% } %>
    </div>

    <script type="text/javascript">
        document.querySelectorAll('input[name="productoId"]').forEach(checkbox => {
            checkbox.addEventListener('change', function() {
                const cantidadInput = this.parentElement.querySelector('input[name="cantidad"]');
                cantidadInput.disabled = !this.checked;
                if (!this.checked) {
                    cantidadInput.value = '';
                }
            });
        });
    </script>
</body>
</html>
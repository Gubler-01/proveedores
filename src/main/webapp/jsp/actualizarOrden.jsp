<%--
    Document   : actualizarOrden
    Created on : Mar 15, 2025
    Author     : guble
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="mx.tecnm.toluca.model.OrdenCompra" %>
<%@ page import="mx.tecnm.toluca.model.ProductoOrden" %>
<%@ page import="mx.tecnm.toluca.model.Producto" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page session="true" %>
<%
    if (session == null || session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
%>
<html>
<head>
    <title>Actualizar Orden de Compra</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <div class="container">
        <h1>Actualizar Orden de Compra</h1>
        <% OrdenCompra orden = (OrdenCompra) request.getAttribute("orden"); %>
        <form action="${pageContext.request.contextPath}/OrdenCompraServlet" method="post">
            <input type="hidden" name="accion" value="actualizar">
            <input type="hidden" name="id" value="<%= orden.getId() %>">
            <label>ID:</label><input type="text" value="<%= orden.getId() %>" readonly>
            <label>Proveedor:</label><input type="text" value="<%= orden.getProveedorNombre() %>" readonly>
            <label>Fecha:</label><input type="text" value="<%= dateFormat.format(orden.getFecha()) %>" readonly>
            <label>Estado:</label>
            <select name="estado">
                <option value="Pendiente" <%= "Pendiente".equals(orden.getEstado()) ? "selected" : "" %>>Pendiente</option>
                <option value="Procesada" <%= "Procesada".equals(orden.getEstado()) ? "selected" : "" %>>Procesada</option>
                <option value="Cancelada" <%= "Cancelada".equals(orden.getEstado()) ? "selected" : "" %>>Cancelada</option>
            </select>
            <label>Productos:</label>
            <div id="productos-container">
                <% for (ProductoOrden prod : orden.getProductos()) { %>
                    <div class="producto-item">
                        <label><%= prod.getNombre() %> - Cantidad: <%= prod.getCantidad() %>, Precio Unitario: $<%= String.format("%.2f", prod.getPrecio()) %></label>
                    </div>
                <% } %>
            </div>
            <label>Precio Total:</label><input type="text" value="$<%= String.format("%.2f", orden.getPrecioTotal()) %>" readonly>
            <label>Tiempo de Entrega Estimado:</label><input type="text" value="<%= orden.getTiempoEntregaDias() %> días" readonly>
            <label>Notas:</label><textarea readonly><%= orden.getNotas() != null ? orden.getNotas() : "" %></textarea>
            <input type="submit" value="Actualizar Orden">
        </form>
        <a href="${pageContext.request.contextPath}/OrdenCompraServlet?accion=listar" class="back-button">Volver a la lista</a>
        <% if (request.getAttribute("mensaje") != null) { %>
            <p class="mensaje-exito"><%= request.getAttribute("mensaje") %></p>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <p class="mensaje-error"><%= request.getAttribute("error") %></p>
        <% } %>
    </div>
</body>
</html>
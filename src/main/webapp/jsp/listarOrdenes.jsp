<%--
    Document   : listarOrdenes
    Created on : Mar 15, 2025
    Author     : guble
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="mx.tecnm.toluca.model.OrdenCompra" %>
<%@ page import="mx.tecnm.toluca.model.ProductoOrden" %>
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
    <title>Lista de Órdenes de Compra</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <div class="container">
        <h1>Lista de Órdenes de Compra</h1>
        <table>
            <tr>
                <th>ID</th>
                <th>Proveedor</th>
                <th>Fecha</th>
                <th>Estado</th>
                <th>Productos</th>
                <th>Precio Total</th>
                <th>Tiempo de Entrega</th>
                <th>Notas</th>
                <th>Acciones</th>
            </tr>
            <% List<OrdenCompra> ordenes = (List<OrdenCompra>) request.getAttribute("ordenes"); %>
            <% if (ordenes != null && !ordenes.isEmpty()) { %>
                <% for (OrdenCompra orden : ordenes) { %>
                    <tr>
                        <td><%= orden.getId() %></td>
                        <td><%= orden.getProveedorNombre() %></td>
                        <td><%= dateFormat.format(orden.getFecha()) %></td>
                        <td><%= orden.getEstado() %></td>
                        <td>
                            <ul>
                            <% for (ProductoOrden prod : orden.getProductos()) { %>
                                <li><%= prod.getNombre() %> - Cantidad: <%= prod.getCantidad() %>, Precio Unitario: $<%= String.format("%.2f", prod.getPrecio()) %></li>
                            <% } %>
                            </ul>
                        </td>
                        <td>$<%= String.format("%.2f", orden.getPrecioTotal()) %></td>
                        <td><%= orden.getTiempoEntregaDias() %> días</td>
                        <td><%= orden.getNotas() != null && !orden.getNotas().isEmpty() ? orden.getNotas() : "Sin notas" %></td>
                        <td class="acciones">
                            <a href="${pageContext.request.contextPath}/OrdenCompraServlet?accion=editar&id=<%= orden.getId() %>" class="update-button">Actualizar</a>
                            <form id="deleteForm-<%= orden.getId() %>" action="${pageContext.request.contextPath}/OrdenCompraServlet" method="post" style="display:inline;">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="<%= orden.getId() %>">
                                <button type="button" onclick="showConfirmModal('<%= orden.getId() %>')">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            <% } else { %>
                <tr>
                    <td colspan="9" style="text-align: center;">No hay órdenes de compra registradas.</td>
                </tr>
            <% } %>
        </table>
        <a href="${pageContext.request.contextPath}/jsp/registrarOrden.jsp" class="register-button">Registrar nueva orden</a>
        <a href="${pageContext.request.contextPath}/ProductoServlet?accion=listar" class="back-button">Volver a productos</a>
        <% if (request.getAttribute("mensaje") != null) { %>
            <p class="mensaje-exito"><%= request.getAttribute("mensaje") %></p>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <p class="mensaje-error"><%= request.getAttribute("error") %></p>
        <% } %>
    </div>

    <div id="confirmModal" class="modal-confirm">
        <div class="modal-confirm-content">
            <p>¿Estás seguro de que deseas eliminar esta orden?</p>
            <div class="modal-confirm-buttons">
                <button class="modal-confirm-button confirm" onclick="confirmDelete()">Sí</button>
                <button class="modal-confirm-button cancel" onclick="cancelDelete()">No</button>
            </div>
        </div>
    </div>

    <% if (request.getAttribute("showPopup") != null && (Boolean) request.getAttribute("showPopup")) { %>
        <div id="myModal" class="modal" style="display: flex;">
            <div class="modal-content">
                <p><%= request.getAttribute("mensaje") %></p>
                <button class="modal-button" onclick="closeModal()">Aceptar</button>
            </div>
        </div>
    <% } %>

    <script type="text/javascript">
        let formToDelete = null;

        function showConfirmModal(ordenId) {
            formToDelete = document.getElementById('deleteForm-' + ordenId);
            document.getElementById('confirmModal').style.display = 'flex';
        }

        function confirmDelete() {
            if (formToDelete) {
                formToDelete.submit();
            }
            document.getElementById('confirmModal').style.display = 'none';
        }

        function cancelDelete() {
            formToDelete = null;
            document.getElementById('confirmModal').style.display = 'none';
        }

        function closeModal() {
            document.getElementById('myModal').style.display = 'none';
            window.location.href = "${pageContext.request.contextPath}/OrdenCompraServlet?accion=listar";
        }

        window.onload = function() {
            <% if (request.getAttribute("showPopup") != null && (Boolean) request.getAttribute("showPopup")) { %>
                document.getElementById('myModal').style.display = 'flex';
            <% } %>
        };
    </script>
</body>
</html>
<%--
    Document   : listarProductos
    Created on : Mar 13, 2025, 6:44:50 PM
    Author     : guble
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="mx.tecnm.toluca.model.Producto" %>
<%@ page import="java.util.List" %>
<%@ page session="true" %>
<%
    if (session == null || session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<html>
<head>
    <title>Lista de Productos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
    <div class="container">
        <h1>Lista de Productos</h1>
        <table>
            <tr>
                <th>Imagen</th>
                <th>ID</th>
                <th>Nombre</th>
                <th>Descripción</th>
                <th>Precio</th>
                <th>Stock</th>
                <th>Acciones</th>
            </tr>
            <% List<Producto> productos = (List<Producto>) request.getAttribute("productos"); %>
            <% if (productos != null && !productos.isEmpty()) { %>
                <% for (Producto p : productos) { %>
                    <tr>
                        <td>
                            <img src="${pageContext.request.contextPath}/ImageServlet?imagenId=<%= p.getImagenId() != null ? p.getImagenId() : "" %>" alt="Producto" onerror="this.src='${pageContext.request.contextPath}/images/default.jpg';">
                        </td>
                        <td><%= p.getId() %></td>
                        <td><%= p.getNombre() %></td>
                        <td><%= p.getDescripcion() %></td>
                        <td>$<%= p.getPrecio() %></td>
                        <td><%= p.getStock() %>u</td>
                        <td class="acciones">
                            <a href="${pageContext.request.contextPath}/ProductoServlet?accion=editar&id=<%= p.getId() %>" class="update-button">Actualizar</a>
                            <form id="deleteForm-<%= p.getId() %>" action="${pageContext.request.contextPath}/ProductoServlet" method="post" style="display:inline;">
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="<%= p.getId() %>">
                                <button type="button" onclick="showConfirmModal('<%= p.getId() %>')">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            <% } else { %>
                <tr>
                    <td colspan="7" style="text-align: center;">No hay productos registrados.</td>
                </tr>
            <% } %>
        </table>
        <a href="${pageContext.request.contextPath}/ProductoServlet" class="register-button">Registrar nuevo producto</a>
        <a href="${pageContext.request.contextPath}/OrdenCompraServlet?accion=listar" class="orders-button">Ver órdenes de compra</a>
        <% if (request.getAttribute("mensaje") != null) { %>
            <p class="mensaje-exito"><%= request.getAttribute("mensaje") %></p>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <p class="mensaje-error"><%= request.getAttribute("error") %></p>
        <% } %>
    </div>

    <div id="confirmModal" class="modal-confirm">
        <div class="modal-confirm-content">
            <p>¿Estás seguro de que deseas eliminar este producto?</p>
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

        function showConfirmModal(productId) {
            formToDelete = document.getElementById('deleteForm-' + productId);
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
            window.location.href = "${pageContext.request.contextPath}/ProductoServlet?accion=listar";
        }

        window.onload = function() {
            <% if (request.getAttribute("showPopup") != null && (Boolean) request.getAttribute("showPopup")) { %>
                document.getElementById('myModal').style.display = 'flex';
            <% } %>
        };
    </script>
</body>
</html>
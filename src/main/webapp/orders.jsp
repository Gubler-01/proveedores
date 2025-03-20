<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="mx.tecnm.toluca.model.User" %>
<%@ page import="mx.tecnm.toluca.model.Order" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    List<Order> orders = (List<Order>) request.getAttribute("orders");
    if (orders == null) {
        orders = java.util.Collections.emptyList();
    }
    String message = (String) session.getAttribute("message");
    String error = (String) session.getAttribute("error");
    session.removeAttribute("message");
    session.removeAttribute("error");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
%>
<html>
<head>
    <title>Administrar Órdenes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
    <div class="container">
        <div class="dashboard-header">
            <h2>Administrar Órdenes - <%= user.getName() %></h2>
            <div class="dashboard-buttons">
                <a href="dashboard"><button class="btn btn-primary">Volver al Dashboard</button></a>
                <a href="logout"><button class="btn btn-danger">Cerrar Sesión</button></a>
            </div>
        </div>
        <div>
            <h3>Lista de Órdenes</h3>
            <table class="product-list table table-striped">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Producto ID</th>
                        <th>Cantidad</th>
                        <th>Estado</th>
                        <th>Cliente ID</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td><span class="custom-id">${order.id}</span></td>
                            <td>${order.productId}</td>
                            <td>${order.quantity}</td>
                            <td>${order.status}</td>
                            <td>${order.customerId}</td>
                            <td class="action-buttons">
                                <a href="#" class="btn btn-warning btn-sm edit-order" data-id="${order.id}" data-status="${order.status}">Cambiar Estado</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <!-- Controles de Paginación -->
            <nav aria-label="Paginación de órdenes">
                <ul class="pagination justify-content-center">
                    <li class="page-item <%= currentPage == 1 ? "disabled" : "" %>">
                        <a class="page-link" href="orders?page=<%= currentPage - 1 %>">Anterior</a>
                    </li>
                    <li class="page-item disabled">
                        <span class="page-link">Página <%= currentPage %> de <%= totalPages %></span>
                    </li>
                    <li class="page-item <%= currentPage == totalPages ? "disabled" : "" %>">
                        <a class="page-link" href="orders?page=<%= currentPage + 1 %>">Siguiente</a>
                    </li>
                </ul>
            </nav>
        </div>

        <!-- Modal para Cambiar Estado -->
        <div class="modal fade" id="editModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editModalLabel">Cambiar Estado de la Orden</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form action="orders/update" method="post" id="editOrderForm">
                            <input type="hidden" id="editId" name="id">
                            <div class="mb-3">
                                <label for="editStatus" class="form-label">Estado</label>
                                <select class="form-control" id="editStatus" name="status" required>
                                    <option value="PENDING">Pendiente</option>
                                    <option value="COMPLETED">Completada</option>
                                    <option value="CANCELLED">Cancelada</option>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-primary">Guardar Cambios</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal para Mensajes -->
        <div class="modal fade" id="messageModal" tabindex="-1" aria-labelledby="messageModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="messageModalLabel">Notificación</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body" id="messageModalBody">
                        <%= message != null ? message : (error != null ? error : "") %>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Aceptar</button>
                    </div>
                </div>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const editButtons = document.querySelectorAll('.edit-order');
                editButtons.forEach(button => {
                    button.addEventListener('click', function(e) {
                        e.preventDefault();
                        const id = this.getAttribute('data-id');
                        const status = this.getAttribute('data-status');

                        document.getElementById('editId').value = id;
                        document.getElementById('editStatus').value = status;
                        new bootstrap.Modal(document.getElementById('editModal')).show();
                    });
                });

                <% if (message != null || error != null) { %>
                    new bootstrap.Modal(document.getElementById('messageModal')).show();
                <% } %>
            });
        </script>
    </div>
</body>
</html>
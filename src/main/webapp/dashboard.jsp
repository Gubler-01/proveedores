<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="mx.tecnm.toluca.model.User" %>
<%@ page import="mx.tecnm.toluca.model.Product" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    List<Product> products = (List<Product>) request.getAttribute("products");
    if (products == null) {
        products = java.util.Collections.emptyList();
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
    <title>Supplier Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
    <div class="container">
        <div class="dashboard-header">
            <h2>Bienvenido Administrador, <%= user.getName() %>!</h2>
            <div class="dashboard-buttons">
                <a href="orders"><button class="btn btn-primary">Administrar Órdenes</button></a>
                <a href="logout"><button class="btn btn-danger">Cerrar Sesión</button></a>
            </div>
        </div>
        <div>
            <h3>Lista de Productos - Categoría Blancos</h3>
            <table class="product-list table table-striped">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Descripción</th>
                        <th>Precio</th>
                        <th>Stock</th>
                        <th>Imagen</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="product" items="${products}">
                        <tr>
                            <td><span class="custom-id">${product.id}</span></td>
                            <td>${product.name}</td>
                            <td>${product.description}</td>
                            <td>$${product.price}</td>
                            <td>${product.stock}</td>
                            <td>
                                <c:if test="${not empty product.imageId}">
                                    <img src="${pageContext.request.contextPath}/image?imageId=${product.imageId}" alt="Producto">
                                </c:if>
                                <c:if test="${empty product.imageId}">
                                    <span>Sin imagen</span>
                                </c:if>
                            </td>
                            <td class="action-buttons">
                                <a href="#" class="btn btn-warning btn-sm edit-product" data-id="${product.id}" data-name="${product.name}" data-description="${product.description}" data-price="${product.price}" data-stock="${product.stock}">Editar</a>
                                <button class="btn btn-danger btn-sm delete-product" data-id="${product.id}" data-bs-toggle="modal" data-bs-target="#deleteModal">Eliminar</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <!-- Controles de Paginación -->
            <nav aria-label="Paginación de productos">
                <ul class="pagination justify-content-center">
                    <li class="page-item <%= currentPage == 1 ? "disabled" : "" %>">
                        <a class="page-link" href="dashboard?page=<%= currentPage - 1 %>">Anterior</a>
                    </li>
                    <li class="page-item disabled">
                        <span class="page-link">Página <%= currentPage %> de <%= totalPages %></span>
                    </li>
                    <li class="page-item <%= currentPage == totalPages ? "disabled" : "" %>">
                        <a class="page-link" href="dashboard?page=<%= currentPage + 1 %>">Siguiente</a>
                    </li>
                </ul>
            </nav>

            <div class="text-center mt-4">
                <button class="btn btn-success" data-bs-toggle="modal" data-bs-target="#addModal">Agregar Producto</button>
            </div>
        </div>

        <!-- Modal para Agregar Producto -->
        <div class="modal fade" id="addModal" tabindex="-1" aria-labelledby="addModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addModalLabel">Agregar Nuevo Producto</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form action="products/add" method="post" enctype="multipart/form-data" id="addProductForm">
                            <div class="mb-3">
                                <label for="addName" class="form-label">Nombre</label>
                                <input type="text" class="form-control" id="addName" name="name" required placeholder="Ej. Sábana Blanca">
                            </div>
                            <div class="mb-3">
                                <label for="addDescription" class="form-label">Descripción</label>
                                <input type="text" class="form-control" id="addDescription" name="description" required placeholder="Ej. Sábana de algodón 100%">
                            </div>
                            <div class="mb-3">
                                <label for="addPrice" class="form-label">Precio</label>
                                <input type="number" class="form-control" id="addPrice" name="price" step="0.01" required placeholder="Ej. 50.00">
                            </div>
                            <div class="mb-3">
                                <label for="addStock" class="form-label">Stock</label>
                                <input type="number" class="form-control" id="addStock" name="stock" required placeholder="Ej. 100">
                            </div>
                            <div class="mb-3">
                                <label for="addImage" class="form-label">Imagen del Producto</label>
                                <input type="file" class="form-control" id="addImage" name="image" accept="image/*">
                            </div>
                            <button type="submit" class="btn btn-primary">Guardar</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal para Editar Producto -->
        <div class="modal fade" id="editModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editModalLabel">Editar Producto</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form action="products/update" method="post" enctype="multipart/form-data" id="editProductForm">
                            <input type="hidden" id="editId" name="id">
                            <div class="mb-3">
                                <label for="editName" class="form-label">Nombre</label>
                                <input type="text" class="form-control" id="editName" name="name" required>
                            </div>
                            <div class="mb-3">
                                <label for="editDescription" class="form-label">Descripción</label>
                                <input type="text" class="form-control" id="editDescription" name="description" required>
                            </div>
                            <div class="mb-3">
                                <label for="editPrice" class="form-label">Precio</label>
                                <input type="number" class="form-control" id="editPrice" name="price" step="0.01" required>
                            </div>
                            <div class="mb-3">
                                <label for="editStock" class="form-label">Stock</label>
                                <input type="number" class="form-control" id="editStock" name="stock" required>
                            </div>
                            <div class="mb-3">
                                <label for="editImage" class="form-label">Imagen del Producto (dejar en blanco para no cambiar)</label>
                                <input type="file" class="form-control" id="editImage" name="image" accept="image/*">
                            </div>
                            <button type="submit" class="btn btn-primary">Guardar Cambios</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal para Eliminar Producto -->
        <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="deleteModalLabel">Confirmar Eliminación</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        ¿Estás seguro de que deseas eliminar el producto con ID <span id="deleteProductId"></span>?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <form action="products/delete" method="post" id="deleteProductForm" style="display: inline;">
                            <input type="hidden" id="deleteId" name="id">
                            <button type="submit" class="btn btn-danger">Sí, Eliminar</button>
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
                console.log('DOM completamente cargado');
                const editButtons = document.querySelectorAll('.edit-product');
                console.log('Botones de edición encontrados: ', editButtons.length);
                editButtons.forEach(button => {
                    button.addEventListener('click', function(e) {
                        e.preventDefault();
                        console.log('Botón de edición clickeado');
                        const id = this.getAttribute('data-id');
                        const name = this.getAttribute('data-name');
                        const description = this.getAttribute('data-description');
                        const price = this.getAttribute('data-price');
                        const stock = this.getAttribute('data-stock');

                        document.getElementById('editId').value = id;
                        document.getElementById('editName').value = name;
                        document.getElementById('editDescription').value = description;
                        document.getElementById('editPrice').value = price;
                        document.getElementById('editStock').value = stock;
                        new bootstrap.Modal(document.getElementById('editModal')).show();
                    });
                });

                const deleteButtons = document.querySelectorAll('.delete-product');
                console.log('Botones de eliminación encontrados: ', deleteButtons.length);
                deleteButtons.forEach(button => {
                    button.addEventListener('click', function() {
                        console.log('Botón de eliminación clickeado');
                        const id = this.getAttribute('data-id');
                        document.getElementById('deleteId').value = id;
                        document.getElementById('deleteProductId').textContent = id;
                    });
                });

                <% if (message != null || error != null) { %>
                    console.log('Mostrando modal de mensaje');
                    new bootstrap.Modal(document.getElementById('messageModal')).show();
                <% } %>
            });
        </script>
    </div>
</body>
</html>
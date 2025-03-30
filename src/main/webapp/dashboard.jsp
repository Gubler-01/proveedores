<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    String token = (String) session.getAttribute("token");
    String correo = (String) session.getAttribute("correo");
    if (token == null || correo == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String message = (String) session.getAttribute("message");
    String error = (String) session.getAttribute("error");
    session.removeAttribute("message");
    session.removeAttribute("error");
%>
<html>
<head>
    <title>Supplier Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
    <div class="container">
        <div class="dashboard-header">
            <h2>Bienvenido, <%= correo %>!</h2>
            <div class="dashboard-buttons">
                <a href="logout"><button class="btn btn-danger">Cerrar Sesión</button></a>
            </div>
        </div>
        <div>
            <h3>Lista de Productos</h3>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Descripción</th>
                        <th>Precio</th>
                        <th>Stock</th>
                        <th>Categoría</th>
                        <th>Status</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="product" items="${products}">
                        <tr>
                            <td>${product.id}</td>
                            <td>${product.nombre}</td>
                            <td>${product.descripcion}</td>
                            <td>$<fmt:formatNumber value="${product.precio}" pattern="#,##0.00"/></td>
                            <td>${product.stock}</td>
                            <td>${product.categoria}</td>
                            <td>${product.status}</td>
                            <td>
                                <button class="btn btn-warning btn-sm edit-product" 
                                        data-id="${product.id}" 
                                        data-nombre="${product.nombre}" 
                                        data-descripcion="${product.descripcion}" 
                                        data-precio="${product.precio}" 
                                        data-stock="${product.stock}" 
                                        data-status="${product.status}" 
                                        data-bs-toggle="modal" 
                                        data-bs-target="#editModal">Editar</button>
                                <button class="btn btn-danger btn-sm delete-product" 
                                        data-id="${product.id}" 
                                        data-bs-toggle="modal" 
                                        data-bs-target="#deleteModal">Eliminar</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

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
                        <form action="dashboard" method="post" id="addProductForm">
                            <input type="hidden" name="action" value="add">
                            <div class="mb-3">
                                <label for="addNombre" class="form-label">Nombre</label>
                                <input type="text" class="form-control" id="addNombre" name="nombre" required>
                            </div>
                            <div class="mb-3">
                                <label for="addDescripcion" class="form-label">Descripción</label>
                                <input type="text" class="form-control" id="addDescripcion" name="descripcion" required>
                            </div>
                            <div class="mb-3">
                                <label for="addPrecio" class="form-label">Precio</label>
                                <input type="number" class="form-control" id="addPrecio" name="precio" step="0.01" required>
                            </div>
                            <div class="mb-3">
                                <label for="addStock" class="form-label">Stock</label>
                                <input type="number" class="form-control" id="addStock" name="stock" required>
                            </div>
                            <input type="hidden" name="categoria" value="Blancos">
                            <div class="mb-3">
                                <label for="addStatus" class="form-label">Status</label>
                                <select class="form-control" id="addStatus" name="status" required>
                                    <option value="Disponible">Disponible</option>
                                    <option value="No disponible">No disponible</option>
                                </select>
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
                        <form action="dashboard" method="post" id="editProductForm">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" id="editId" name="id">
                            <div class="mb-3">
                                <label for="editNombre" class="form-label">Nombre</label>
                                <input type="text" class="form-control" id="editNombre" name="nombre" required>
                            </div>
                            <div class="mb-3">
                                <label for="editDescripcion" class="form-label">Descripción</label>
                                <input type="text" class="form-control" id="editDescripcion" name="descripcion" required>
                            </div>
                            <div class="mb-3">
                                <label for="editPrecio" class="form-label">Precio</label>
                                <input type="number" class="form-control" id="editPrecio" name="precio" step="0.01" required>
                            </div>
                            <div class="mb-3">
                                <label for="editStock" class="form-label">Stock</label>
                                <input type="number" class="form-control" id="editStock" name="stock" required>
                            </div>
                            <input type="hidden" name="categoria" value="Blancos">
                            <div class="mb-3">
                                <label for="editStatus" class="form-label">Status</label>
                                <select class="form-control" id="editStatus" name="status" required>
                                    <option value="Disponible">Disponible</option>
                                    <option value="No disponible">No disponible</option>
                                </select>
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
                        <form action="dashboard" method="post" id="deleteProductForm">
                            <input type="hidden" name="action" value="delete">
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
                const editButtons = document.querySelectorAll('.edit-product');
                editButtons.forEach(button => {
                    button.addEventListener('click', function() {
                        const id = this.getAttribute('data-id');
                        const nombre = this.getAttribute('data-nombre');
                        const descripcion = this.getAttribute('data-descripcion');
                        const precio = this.getAttribute('data-precio');
                        const stock = this.getAttribute('data-stock');
                        const status = this.getAttribute('data-status');

                        document.getElementById('editId').value = id;
                        document.getElementById('editNombre').value = nombre;
                        document.getElementById('editDescripcion').value = descripcion;
                        document.getElementById('editPrecio').value = precio;
                        document.getElementById('editStock').value = stock;
                        document.getElementById('editStatus').value = status;
                    });
                });

                const deleteButtons = document.querySelectorAll('.delete-product');
                deleteButtons.forEach(button => {
                    button.addEventListener('click', function() {
                        const id = this.getAttribute('data-id');
                        document.getElementById('deleteId').value = id;
                        document.getElementById('deleteProductId').textContent = id;
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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    String token = (String) session.getAttribute("token");
    String correo = (String) session.getAttribute("correo");
    if (token == null || correo == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String message = (String) session.getAttribute("message");
    String error = (String) session.getAttribute("error");
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
            <h2>Bienvenido, Proveedor 1! <%= correo %></h2>
            <div class="dashboard-buttons">
                <a href="${pageContext.request.contextPath}/orders" class="btn btn-info me-2">Ver Órdenes</a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Cerrar Sesión</a>
            </div>
        </div>
        <div>
            <h3>Lista de Productos</h3>
            <c:choose>
                <c:when test="${not empty errorMessage}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${errorMessage}"/>
                    </div>
                </c:when>
                <c:when test="${empty products}">
                    <div class="alert alert-info" role="alert">
                        No hay productos disponibles.
                    </div>
                </c:when>
                <c:otherwise>
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
                                <th>Imagen</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="product" items="${products}">
                                <tr>
                                    <td>${fn:escapeXml(product.id)}</td>
                                    <td>${fn:escapeXml(product.nombre)}</td>
                                    <td>${fn:escapeXml(product.descripcion)}</td>
                                    <td>$<fmt:formatNumber value="${product.precio}" pattern="#,##0.00"/></td>
                                    <td>${fn:escapeXml(product.stock)}</td>
                                    <td>${fn:escapeXml(product.categoria)}</td>
                                    <td>${fn:escapeXml(product.status)}</td>
                                    <td>
                                        <c:if test="${not empty product.imagen}">
                                            <img src="${fn:escapeXml(product.imagen)}" alt="Imagen del producto" style="max-width: 100px;"/>
                                        </c:if>
                                    </td>
                                    <td>
                                        <button class="btn btn-warning btn-sm edit-product" 
                                                data-id="${fn:escapeXml(product.id)}" 
                                                data-nombre="${fn:escapeXml(product.nombre)}" 
                                                data-descripcion="${fn:escapeXml(product.descripcion)}" 
                                                data-precio="${product.precio}" 
                                                data-stock="${product.stock}" 
                                                data-status="${fn:escapeXml(product.status)}" 
                                                data-imagen="${fn:escapeXml(product.imagen)}" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#editModal">Editar</button>
                                        <button class="btn btn-danger btn-sm delete-product" 
                                                data-id="${fn:escapeXml(product.id)}" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#deleteModal">Eliminar</button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>

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
                        <form action="dashboard" method="post" enctype="multipart/form-data" id="addProductForm">
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
                            <div class="mb-3">
                                <label for="addImagen" class="form-label">Imagen</label>
                                <input type="file" class="form-control" id="addImagen" name="imagen" accept="image/*" required>
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
                        <form action="dashboard" method="post" enctype="multipart/form-data" id="editProductForm">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" id="editId" name="id">
                            <input type="hidden" id="editExistingImagen" name="existingImagen">
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
                            <div class="mb-3">
                                <label for="editImagen" class="form-label">Imagen</label>
                                <input type="file" class="form-control" id="editImagen" name="imagen" accept="image/*">
                                <small class="form-text text-muted">Dejar en blanco para mantener la imagen actual.</small>
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
                        <c:choose>
                            <c:when test="${not empty error}">
                                <p class="text-danger"><c:out value="${error}"/></p>
                            </c:when>
                            <c:when test="${not empty message}">
                                <p class="text-success"><c:out value="${message}"/></p>
                            </c:when>
                            <c:otherwise>
                                <p>No se recibió ningún mensaje.</p>
                            </c:otherwise>
                        </c:choose>
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
                        const imagen = this.getAttribute('data-imagen');

                        document.getElementById('editId').value = id;
                        document.getElementById('editNombre').value = nombre;
                        document.getElementById('editDescripcion').value = descripcion;
                        document.getElementById('editPrecio').value = precio;
                        document.getElementById('editStock').value = stock;
                        document.getElementById('editStatus').value = status;
                        document.getElementById('editExistingImagen').value = imagen;
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
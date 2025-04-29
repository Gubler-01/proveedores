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
    // No eliminamos los atributos de la sesión inmediatamente
    String message = (String) session.getAttribute("message");
    String error = (String) session.getAttribute("error");
%>
<html>
<head>
    <title>Orders</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
    <div class="container">
        <div class="dashboard-header">
            <h2>Órdenes - <%= correo %></h2>
            <div class="dashboard-buttons">
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary me-2">Volver al Dashboard</a>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger">Cerrar Sesión</a>
            </div>
        </div>
        <div>
            <h3>Lista de Órdenes</h3>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Cliente</th>
                        <th>Ítems</th>
                        <th>Subtotal</th>
                        <th>Total</th>
                        <th>Estado</th>
                        <th>Fecha Creación</th>
                        <th>Método de Pago</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td>${fn:escapeXml(order.id)}</td>
                            <td>${fn:escapeXml(order.customerId)}</td>
                            <td>
                                <c:forEach var="item" items="${order.items}">
                                    <c:set var="product" value="${products.stream().filter(p -> p.id == item.productId).findFirst().orElse(null)}"/>
                                    Producto: ${product != null ? fn:escapeXml(product.nombre) : fn:escapeXml(item.productId)},
                                    Cantidad: ${item.quantity},
                                    Precio: $<fmt:formatNumber value="${item.price}" pattern="#,##0.00"/><br>
                                </c:forEach>
                            </td>
                            <td>$<fmt:formatNumber value="${order.subtotal}" pattern="#,##0.00"/></td>
                            <td>$<fmt:formatNumber value="${order.total}" pattern="#,##0.00"/></td>
                            <td>${fn:escapeXml(order.status)}</td>
                            <td>${fn:escapeXml(order.createdAt)}</td>
                            <td>${fn:escapeXml(order.paymentMethod)}</td>
                            <td>
                                <button class="btn btn-warning btn-sm update-order"
                                        data-id="${fn:escapeXml(order.id)}"
                                        data-customer-id="${fn:escapeXml(order.customerId)}"
                                        data-items="${fn:escapeXml(order.itemsJson)}"
                                        data-subtotal="${order.subtotal}"
                                        data-total="${order.total}"
                                        data-status="${fn:escapeXml(order.status)}"
                                        data-created-at="${fn:escapeXml(order.createdAt)}"
                                        data-payment-method="${fn:escapeXml(order.paymentMethod)}"
                                        data-bs-toggle="modal"
                                        data-bs-target="#updateModal">Actualizar Estado</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Modal para Actualizar Estado -->
        <div class="modal fade" id="updateModal" tabindex="-1" aria-labelledby="updateModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="updateModalLabel">Actualizar Estado de Orden</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form action="orders" method="post" enctype="multipart/form-data" id="updateOrderForm">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" id="updateId" name="id">
                            <input type="hidden" id="updateCustomerId" name="customerId">
                            <input type="hidden" id="updateItems" name="items">
                            <input type="hidden" id="updateSubtotal" name="subtotal">
                            <input type="hidden" id="updateTotal" name="total">
                            <input type="hidden" id="updateCreatedAt" name="createdAt">
                            <input type="hidden" id="updatePaymentMethod" name="paymentMethod">
                            <div class="mb-3">
                                <label for="updateStatus" class="form-label">Estado</label>
                                <select class="form-control" id="updateStatus" name="status" required>
                                    <option value="Pendiente">Pendiente</option>
                                    <option value="Enviado">Enviado</option>
                                    <option value="Completado">Completado</option>
                                    <option value="Cancelado">Cancelado</option>
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
                        <button type="button" class="btn btn-primary" data-bs-dismiss="modal" onclick="clearSessionAttributes()">Aceptar</button>
                    </div>
                </div>
            </div>
        </div>

        <script>
            function clearSessionAttributes() {
                // Hacer una llamada AJAX para limpiar los atributos de la sesión
                fetch('${pageContext.request.contextPath}/clearMessages', {
                    method: 'POST'
                }).then(() => {
                    // No necesitamos hacer nada después de limpiar
                });
            }

            document.addEventListener('DOMContentLoaded', function() {
                const updateButtons = document.querySelectorAll('.update-order');
                updateButtons.forEach(button => {
                    button.addEventListener('click', function() {
                        const id = this.getAttribute('data-id');
                        console.log('ID del botón:', id); // Depuración
                        if (!id) {
                            alert('Error: El ID de la orden no está disponible.');
                            return;
                        }
                        document.getElementById('updateId').value = id;
                        document.getElementById('updateCustomerId').value = this.getAttribute('data-customer-id');
                        document.getElementById('updateItems').value = this.getAttribute('data-items');
                        document.getElementById('updateSubtotal').value = this.getAttribute('data-subtotal');
                        document.getElementById('updateTotal').value = this.getAttribute('data-total');
                        document.getElementById('updateStatus').value = this.getAttribute('data-status');
                        document.getElementById('updateCreatedAt').value = this.getAttribute('data-created-at');
                        document.getElementById('updatePaymentMethod').value = this.getAttribute('data-payment-method');
                    });
                });

                <% if (message != null || error != null) { %>
                    new bootstrap.Modal(document.getElementById('messageModal')).show();
                <% } %>

                document.getElementById('updateOrderForm').addEventListener('submit', function(e) {
                    const id = document.getElementById('updateId').value;
                    const status = document.getElementById('updateStatus').value;
                    console.log('ID antes de enviar:', id); // Depuración
                    if (!id) {
                        e.preventDefault();
                        alert('Error: El ID de la orden es obligatorio.');
                        return;
                    }
                    if (!['Pendiente', 'Enviado', 'Completado', 'Cancelado'].includes(status)) {
                        e.preventDefault();
                        alert('Estado inválido.');
                    }
                });
            });
</script>
    </div>
</body>
</html>
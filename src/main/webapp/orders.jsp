<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
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
<!DOCTYPE html>
<html>
    <head>
        <title>Órdenes - Supplier Module</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/styles.css">
    </head>
    <body>
        <div class="container mt-4">
            <h2>Lista de Órdenes</h2>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Número de Orden</th>
                        <th>Cliente</th>
                        <th>Estado</th>
                        <th>Fecha de Creación</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="order" items="${orders}">
                    <tr>
                        <td>${fn:escapeXml(order.id)}</td>
                        <td>${fn:escapeXml(order.customerId)}</td>
                        <td>${fn:escapeXml(order.status)}</td>
                        <td>
                    <c:catch var="parseException">
                        <fmt:parseDate value="${order.createdAt}" pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSSS" var="parsedDate" />
                        <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm:ss" />
                    </c:catch>
                    <c:if test="${not empty parseException}">
                        <c:catch var="parseExceptionNoMillis">
                            <fmt:parseDate value="${order.createdAt}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" />
                            <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm:ss" />
                        </c:catch>
                        <c:if test="${not empty parseExceptionNoMillis}">
                            ${fn:escapeXml(order.createdAt)}
                        </c:if>
                    </c:if>
                    </td>
                    <td>
                        <button type="button" class="btn btn-info btn-sm view-order" 
                                data-id="${fn:escapeXml(order.id)}" 
                                data-customer="${fn:escapeXml(order.customerId)}" 
                                data-status="${fn:escapeXml(order.status)}" 
                                data-created="${fn:escapeXml(order.createdAt)}" 
                                data-items='${jakarta.json.bind.JsonbBuilder.create().toJson(order.items)}' 
                                data-subtotal="${order.subtotal}" 
                                data-total="${order.total}" 
                                data-payment="${fn:escapeXml(order.paymentMethod)}" 
                                data-bs-toggle="modal" 
                                data-bs-target="#detailsModal">
                            Detalles
                        </button>
                        <button type="button" class="btn btn-warning btn-sm edit-order" 
                                data-id="${fn:escapeXml(order.id)}" 
                                data-customer="${fn:escapeXml(order.customerId)}" 
                                data-status="${fn:escapeXml(order.status)}" 
                                data-created="${fn:escapeXml(order.createdAt)}" 
                                data-items='${jakarta.json.bind.JsonbBuilder.create().toJson(order.items)}' 
                                data-subtotal="${order.subtotal}" 
                                data-total="${order.total}" 
                                data-payment="${fn:escapeXml(order.paymentMethod)}" 
                                data-bs-toggle="modal" 
                                data-bs-target="#editModal">
                            Editar
                        </button>
                    </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty orders}">
                    <tr>
                        <td colspan="5" class="text-center">No hay órdenes disponibles.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>

            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary mt-3">Volver al Dashboard</a>
        </div>

        <!-- Modal para Detalles de la Orden -->
        <div class="modal fade" id="detailsModal" tabindex="-1" aria-labelledby="detailsModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="detailsModalLabel">Detalles de la Orden</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p><strong>Número de Orden:</strong> <span id="detailOrderId"></span></p>
                        <p><strong>Cliente:</strong> <span id="detailCustomerId"></span></p>
                        <p><strong>Estado:</strong> <span id="detailStatus"></span></p>
                        <p><strong>Fecha de Creación:</strong> <span id="detailCreatedAt"></span></p>
                        <p><strong>Productos:</strong></p>
                        <ul id="detailItems"></ul>
                        <p><strong>Subtotal:</strong> $<span id="detailSubtotal"></span></p>
                        <p><strong>Total:</strong> $<span id="detailTotal"></span></p>
                        <p><strong>Método de Pago:</strong> <span id="detailPaymentMethod"></span></p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" data-bs-dismiss="modal">Aceptar</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal para Editar Orden -->
        <div class="modal fade" id="editModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editModalLabel">Editar Estado de la Orden</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <form id="editStatusForm" action="${pageContext.request.contextPath}/orders" method="post">
                            <input type="hidden" name="action" value="editStatus">
                            <input type="hidden" name="orderId" id="editOrderId">
                            <p><strong>Número de Orden:</strong> <span id="editOrderIdDisplay"></span></p>
                            <p><strong>Cliente:</strong> <span id="editCustomerId"></span></p>
                            <div class="mb-3">
                                <label for="statusSelect" class="form-label">Estado</label>
                                <select class="form-select" id="statusSelect" name="status" required>
                                    <option value="Completado">Completado</option>
                                    <option value="Cancelado">Cancelado</option>
                                </select>
                            </div>
                            <p><strong>Fecha de Creación:</strong> <span id="editCreatedAt"></span></p>
                            <p><strong>Productos:</strong></p>
                            <ul id="editItems"></ul>
                            <p><strong>Subtotal:</strong> $<span id="editSubtotal"></span></p>
                            <p><strong>Total:</strong> $<span id="editTotal"></span></p>
                            <p><strong>Método de Pago:</strong> <span id="editPaymentMethod"></span></p>
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

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            function formatDate(isoString) {
                try {
                    // Remove microsecond precision to make the date parseable
                    const cleanedIsoString = isoString.replace(/(\.\d{3})\d+/, '$1');
                    const date = new Date(cleanedIsoString);
                    if (isNaN(date.getTime())) {
                        return isoString; // Fallback if date parsing fails
                    }
                    const day = String(date.getDate()).padStart(2, '0');
                    const month = String(date.getMonth() + 1).padStart(2, '0');
                    const year = date.getFullYear();
                    const hours = String(date.getHours()).padStart(2, '0');
                    const minutes = String(date.getMinutes()).padStart(2, '0');
                    const seconds = String(date.getSeconds()).padStart(2, '0');
                    return `${day}/${month}/${year} ${hours}:${minutes}:${seconds}`;
                            } catch (e) {
                                console.error("Error formatting date:", e);
                                return isoString; // Fallback to raw string if parsing fails
                            }
                        }

                        document.addEventListener('DOMContentLoaded', function () {
                            const viewButtons = document.querySelectorAll('.view-order');
                            viewButtons.forEach(button => {
                                button.addEventListener('click', function () {
                                    try {
                                        const id = this.getAttribute('data-id');
                                        const customer = this.getAttribute('data-customer');
                                        const status = this.getAttribute('data-status');
                                        const createdAt = this.getAttribute('data-created');
                                        const items = JSON.parse(this.getAttribute('data-items') || '[]');
                                        const subtotal = this.getAttribute('data-subtotal');
                                        const total = this.getAttribute('data-total');
                                        const paymentMethod = this.getAttribute('data-payment');

                                        document.getElementById('detailOrderId').innerText = id || 'N/A';
                                        document.getElementById('detailCustomerId').innerText = customer || 'N/A';
                                        document.getElementById('detailStatus').innerText = status || 'N/A';
                                        document.getElementById('detailCreatedAt').innerText = formatDate(createdAt);

                                        const itemsList = document.getElementById('detailItems');
                                        itemsList.innerHTML = '';
                                        if (items && Array.isArray(items) && items.length > 0) {
                                            items.forEach(item => {
                                                const li = document.createElement('li');
                                                li.innerText = `Producto ID: ${item.productId || 'N/A'}, Cantidad: ${item.quantity || 'N/A'}, Precio: $${item.price || 'N/A'}`;
                                                itemsList.appendChild(li);
                                            });
                                        } else {
                                            const li = document.createElement('li');
                                            li.innerText = 'No hay productos disponibles.';
                                            itemsList.appendChild(li);
                                        }

                                        document.getElementById('detailSubtotal').innerText = subtotal || '0.0';
                                        document.getElementById('detailTotal').innerText = total || '0.0';
                                        document.getElementById('detailPaymentMethod').innerText = paymentMethod || 'No especificado';
                                    } catch (e) {
                                        console.error("Error populating details modal:", e);
                                    }
                                });
                            });

                            const editButtons = document.querySelectorAll('.edit-order');
                            editButtons.forEach(button => {
                                button.addEventListener('click', function () {
                                    try {
                                        const id = this.getAttribute('data-id');
                                        const customer = this.getAttribute('data-customer');
                                        const status = this.getAttribute('data-status');
                                        const createdAt = this.getAttribute('data-created');
                                        const items = JSON.parse(this.getAttribute('data-items') || '[]');
                                        const subtotal = this.getAttribute('data-subtotal');
                                        const total = this.getAttribute('data-total');
                                        const paymentMethod = this.getAttribute('data-payment');

                                        document.getElementById('editOrderId').value = id || '';
                                        document.getElementById('editOrderIdDisplay').innerText = id || 'N/A';
                                        document.getElementById('editCustomerId').innerText = customer || 'N/A';
                                        document.getElementById('statusSelect').value = status === 'Pendiente' ? 'Completado' : status || 'Completado';
                                        document.getElementById('editCreatedAt').innerText = formatDate(createdAt);

                                        const itemsList = document.getElementById('editItems');
                                        itemsList.innerHTML = '';
                                        if (items && Array.isArray(items) && items.length > 0) {
                                            items.forEach(item => {
                                                const li = document.createElement('li');
                                                li.innerText = `Producto ID: ${item.productId || 'N/A'}, Cantidad: ${item.quantity || 'N/A'}, Precio: $${item.price || 'N/A'}`;
                                                itemsList.appendChild(li);
                                            });
                                        } else {
                                            const li = document.createElement('li');
                                            li.innerText = 'No hay productos disponibles.';
                                            itemsList.appendChild(li);
                                        }

                                        document.getElementById('editSubtotal').innerText = subtotal || '0.0';
                                        document.getElementById('editTotal').innerText = total || '0.0';
                                        document.getElementById('editPaymentMethod').innerText = paymentMethod || 'No especificado';
                                    } catch (e) {
                                        console.error("Error populating edit modal:", e);
                                    }
                                });
                            });

            <% if (message != null || error != null) { %>
                            new bootstrap.Modal(document.getElementById('messageModal')).show();
            <% } %>
                        });
        </script>
    </body>
</html>
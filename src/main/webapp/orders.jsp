<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
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
                    <th>Productos</th>
                    <th>Subtotal</th>
                    <th>Total</th>
                    <th>Estado</th>
                    <th>Fecha de Creación</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="order" items="${orders}">
                    <tr>
                        <td>${order.id}</td>
                        <td>${order.customerId}</td>
                        <td>
                            <ul>
                                <c:forEach var="item" items="${order.items}">
                                    <li>Producto ID: ${item.productId}, Cantidad: ${item.quantity}, Precio: $${item.price}</li>
                                </c:forEach>
                            </ul>
                        </td>
                        <td>$${order.subtotal}</td>
                        <td>$${order.total}</td>
                        <td>${order.status}</td>
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
                                    ${order.createdAt}
                                </c:if>
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${order.status == 'Pendiente'}">
                                <button type="button" class="btn btn-success btn-sm" data-bs-toggle="modal" data-bs-target="#confirmModal" 
                                        onclick="setAction('Completada', '${order.id}')">Completar</button>
                                <button type="button" class="btn btn-danger btn-sm" data-bs-toggle="modal" data-bs-target="#confirmModal" 
                                        onclick="setAction('Cancelada', '${order.id}')">Cancelar</button>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty orders}">
                    <tr>
                        <td colspan="8" class="text-center">No hay órdenes disponibles.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>

        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary mt-3">Volver al Dashboard</a>
    </div>

    <!-- Modal de confirmación -->
    <div class="modal fade" id="confirmModal" tabindex="-1" aria-labelledby="confirmModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="confirmModalLabel">Confirmar Acción</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    ¿Estás seguro de que deseas marcar esta orden como <span id="actionText"></span>?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <form id="updateStatusForm" action="${pageContext.request.contextPath}/orders" method="post">
                        <input type="hidden" name="action" value="updateStatus">
                        <input type="hidden" name="orderId" id="orderIdInput">
                        <input type="hidden" name="status" id="statusInput">
                        <button type="submit" class="btn btn-primary">Confirmar</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal de éxito -->
    <div class="modal fade" id="successModal" tabindex="-1" aria-labelledby="successModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="successModalLabel">Acción Completada</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    La orden ha sido marcada como <span id="successActionText"></span>.
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary" data-bs-dismiss="modal" onclick="window.location.reload()">Cerrar</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function setAction(status, orderId) {
            document.getElementById('actionText').innerText = status;
            document.getElementById('orderIdInput').value = orderId;
            document.getElementById('statusInput').value = status;
        }

        // Mostrar el modal de éxito si hay un parámetro 'success' en la URL
        window.onload = function() {
            const urlParams = new URLSearchParams(window.location.search);
            const successStatus = urlParams.get('success');
            if (successStatus) {
                document.getElementById('successActionText').innerText = successStatus;
                const successModal = new bootstrap.Modal(document.getElementById('successModal'));
                successModal.show();
            }
        };
    </script>
</body>
</html>
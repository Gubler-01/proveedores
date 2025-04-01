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
                            <fmt:parseDate value="${order.createdAt}" pattern="yyyy-MM-dd'T'HH:mm:ss.SSSSSSS" var="parsedDate" />
                            <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm:ss" />
                        </td>
                        <td>
                            <c:if test="${order.status == 'Pendiente'}">
                                <form action="${pageContext.request.contextPath}/orders" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="updateStatus">
                                    <input type="hidden" name="orderId" value="${order.id}">
                                    <input type="hidden" name="status" value="Completada">
                                    <button type="submit" class="btn btn-success btn-sm">Completar</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/orders" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="updateStatus">
                                    <input type="hidden" name="orderId" value="${order.id}">
                                    <input type="hidden" name="status" value="Cancelada">
                                    <button type="submit" class="btn btn-danger btn-sm">Cancelar</button>
                                </form>
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

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
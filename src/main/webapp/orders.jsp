<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
                    <th>Subtotal</th>
                    <th>Total</th>
                    <th>Estado</th>
                    <th>Fecha de Creación</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="order" items="${orders}">
                    <tr>
                        <td>${order.id}</td>
                        <td>${order.customerId}</td>
                        <td>$${order.subtotal}</td>
                        <td>$${order.total}</td>
                        <td>${order.status}</td>
                        <td>${order.createdAt}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty orders}">
                    <tr>
                        <td colspan="6" class="text-center">No hay órdenes disponibles.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>

        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary mt-3">Volver al Dashboard</a>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="mx.tecnm.toluca.model.OrderAudit" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    String email = (String) session.getAttribute("email");
    if (email == null) {
        response.sendRedirect(request.getContextPath() + "/");
        return;
    }
    String orderId = (String) request.getAttribute("orderId");
    String customId = (String) request.getAttribute("customId");
    List<OrderAudit> auditEntries = (List<OrderAudit>) request.getAttribute("auditEntries");
    if (auditEntries == null) {
        auditEntries = java.util.Collections.emptyList();
    }
    String message = (String) session.getAttribute("message");
    String error = (String) session.getAttribute("error");
    session.removeAttribute("message");
    session.removeAttribute("error");
%>
<html>
<head>
    <title>Historial de Auditoría - Orden <%= customId != null ? customId : orderId %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
    <div class="container">
        <div class="dashboard-header">
            <h2>Historial de Auditoría - Orden <%= customId != null ? customId : orderId %></h2>
            <div class="dashboard-buttons">
                <a href="${pageContext.request.contextPath}/orders"><button class="btn btn-primary">Volver a Órdenes</button></a>
                <a href="${pageContext.request.contextPath}/auth/logout"><button class="btn btn-danger">Cerrar Sesión</button></a>
            </div>
        </div>
        <div>
            <h3>Entradas de Auditoría</h3>
            <table class="audit-list table table-striped">
                <thead>
                    <tr>
                        <th>Acción</th>
                        <th>Detalles</th>
                        <th>Fecha y Hora</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="audit" items="${auditEntries}">
                        <tr>
                            <td>${audit.action}</td>
                            <td>${audit.details}</td>
                            <td><fmt:formatDate value="${audit.timestampAsDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty auditEntries}">
                        <tr>
                            <td colspan="3" class="text-center">No hay registros de auditoría para esta orden.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
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
                <% if (message != null || error != null) { %>
                    new bootstrap.Modal(document.getElementById('messageModal')).show();
                <% } %>
            });
        </script>
    </div>
</body>
</html>
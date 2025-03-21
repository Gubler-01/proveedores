<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="mx.tecnm.toluca.model.OrderAudit" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    List<OrderAudit> auditEntries = (List<OrderAudit>) request.getAttribute("auditEntries");
    if (auditEntries == null) {
        auditEntries = java.util.Collections.emptyList();
    }
    String orderId = (String) request.getAttribute("orderId");
    String customId = request.getParameter("customId"); // Obtener el customId del parámetro
%>
<!DOCTYPE html>
<html>
<head>
    <title>Historial de Auditoría</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>
    <div class="modal fade" id="auditModal" tabindex="-1" aria-labelledby="auditModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="auditModalLabel">Historial de Auditoría - Orden <%= customId != null ? customId : orderId %></h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <table class="table table-striped audit-table">
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
                                    <td><fmt:formatDate value="${audit.timestampAsDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
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
                <div class="modal-footer">
                    <a href="${pageContext.request.contextPath}/orders" class="btn btn-secondary">Cerrar</a>
                </div>
            </div>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var auditModal = new bootstrap.Modal(document.getElementById('auditModal'), {
                backdrop: 'static',
                keyboard: false
            });
            auditModal.show();
        });
    </script>
</body>
</html>
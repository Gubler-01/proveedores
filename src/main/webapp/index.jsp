<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Supplier Module - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <div class="login-container">
        <h2>Supplier Module - Login</h2>
        <% if (request.getAttribute("error") != null) { %>
            <p class="error"><%= request.getAttribute("error") %></p>
        <% } %>
        <form action="${pageContext.request.contextPath}/auth/login" method="post">
            <div>
                <label for="email">Correo:</label>
                <input type="email" id="email" name="email" required placeholder="Ej. usuario@dominio.com">
            </div>
            <div>
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" required placeholder="Ingresa tu contraseña">
            </div>
            <div>
                <button type="submit">Iniciar Sesión</button>
            </div>
        </form>
    </div>
</body>
</html>
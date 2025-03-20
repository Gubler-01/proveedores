<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Supplier Module - Login</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <div class="login-container">
        <h2>Supplier Module - Login</h2>
        <% if (request.getAttribute("error") != null) { %>
            <p class="error"><%= request.getAttribute("error") %></p>
        <% } %>
        <form action="login" method="post">
            <div>
                <label for="username">Usuario:</label>
                <input type="text" id="username" name="username" required placeholder=" ">
            </div>
            <div>
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" required placeholder=" ">
            </div>
            <div>
                <button type="submit">Iniciar Sesión</button>
            </div>
        </form>
    </div>
</body>
</html>
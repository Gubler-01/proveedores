<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Supplier Module - Login </title>
    <link rel="stylesheet" href="css/styles.css"> 
</head>
<body>
    <div class="login-container">
        <h2>Proveedor 1 - Blancos</h2>
        <h2>Login</h2>
        
        <% 
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
        %>
            <p class="error"><%= errorMessage %></p>
        <% } %>
        
        <form action="login" method="post" id="loginForm">
            <div>
                <label for="correo">Correo:</label>
                <input type="email" id="correo" name="correo" required placeholder=" ">
            </div>
            
            <div>
                <label for="password">Contraseña:</label>
                <input type="password" id="password" name="password" required placeholder=" ">
            </div>
            
            <div class="loading">Iniciando sesión...</div>
            
            <div>
                <button type="submit" id="submitBtn">Iniciar Sesión</button>
            </div>
        </form>
    </div>

    <script>
        document.getElementById('loginForm').addEventListener('submit', function() {
            document.getElementById('submitBtn').disabled = true;
            document.querySelector('.loading').style.display = 'block';
        });
    </script>
</body>
</html>
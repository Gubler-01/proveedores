<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <style>
        .error { color: red; margin-bottom: 10px; }
        .loading { display: none; }
    </style>
</head>
<body>
    <h2>Iniciar Sesión</h2>
    
    <% 
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage != null) {
    %>
        <div class="error"><%= errorMessage %></div>
    <% } %>
    
    <form action="login" method="post" id="loginForm">
        <label for="correo">Correo:</label>
        <input type="email" id="correo" name="correo" required><br>
        
        <label for="password">Contraseña:</label>
        <input type="password" id="password" name="password" required><br>
        
        <div class="loading">Iniciando sesión...</div>
        
        <input type="submit" value="Iniciar Sesión" id="submitBtn">
    </form>

    <script>
        document.getElementById('loginForm').addEventListener('submit', function() {
            document.getElementById('submitBtn').disabled = true;
            document.querySelector('.loading').style.display = 'block';
        });
    </script>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Customer Login - SK Bank of India</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <h1>SK Bank of India - Secure Login</h1>
        <nav>
            <a href="${pageContext.request.contextPath}/ifsc-search">IFSC Search</a>
            <a href="${pageContext.request.contextPath}/register">Register</a>
        </nav>
    </header>
    <div class="container" style="max-width: 500px;">
        <div class="card">
            <h2>Customer Sign In</h2>
            <c:if test="${param.registered eq 'true'}">
                <div class="alert-success">Registration successful! Please login with your credentials.</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert-error">${error}</div>
            </c:if>
            <form action="login" method="post">
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" required placeholder="customer@example.com">
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required placeholder="••••••••">
                </div>
                <button type="submit" style="width: 100%;">Sign In</button>
            </form>
            <p style="text-align: center; margin-top: 1rem;">
                Don't have an account? <a href="register">Register Now</a>
            </p>
        </div>
    </div>
</body>
</html>

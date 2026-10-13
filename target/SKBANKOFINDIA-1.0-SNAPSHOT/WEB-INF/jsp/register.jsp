<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Customer Registration - SK Bank of India</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <h1>SK Bank of India - Account Opening</h1>
        <nav>
            <a href="${pageContext.request.contextPath}/ifsc-search">IFSC Search</a>
            <a href="${pageContext.request.contextPath}/login">Login</a>
        </nav>
    </header>
    <div class="container" style="max-width: 600px;">
        <div class="card">
            <h2>New Customer Registration</h2>
            <p>Open a Savings Account with zero opening balance instantly.</p>
            <c:if test="${not empty error}">
                <div class="alert-error">${error}</div>
            </c:if>
            <form action="register" method="post">
                <div class="form-group">
                    <label for="firstName">First Name</label>
                    <input type="text" id="firstName" name="firstName" required>
                </div>
                <div class="form-group">
                    <label for="lastName">Last Name</label>
                    <input type="text" id="lastName" name="lastName" required>
                </div>
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" required>
                </div>
                <div class="form-group">
                    <label for="phone">Mobile Number (10 digits)</label>
                    <input type="text" id="phone" name="phone" pattern="[6-9][0-9]{9}" required placeholder="9876543210">
                </div>
                <div class="form-group">
                    <label for="password">Secure Password</label>
                    <input type="password" id="password" name="password" required placeholder="••••••••">
                </div>
                <div class="form-group">
                    <label for="pan">PAN Number</label>
                    <input type="text" id="pan" name="pan" maxlength="10" placeholder="ABCDE1234F">
                </div>
                <div class="form-group">
                    <label for="address">Residential Address</label>
                    <input type="text" id="address" name="address">
                </div>
                <button type="submit" style="width: 100%;">Create Account</button>
            </form>
            <p style="text-align: center; margin-top: 1rem;">
                Already have an account? <a href="login">Sign In</a>
            </p>
        </div>
    </div>
</body>
</html>

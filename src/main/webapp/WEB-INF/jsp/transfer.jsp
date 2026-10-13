<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Fund Transfer - SK Bank of India</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <h1>SK Bank of India - Fund Transfer</h1>
        <nav>
            <a href="dashboard">Dashboard</a>
            <a href="transfer">Fund Transfer</a>
            <a href="ifsc-search">IFSC Search</a>
            <a href="login">Logout</a>
        </nav>
    </header>
    <div class="container" style="max-width: 600px;">
        <div class="card">
            <h2>NEFT / Internal Fund Transfer</h2>
            <c:if test="${not empty error}">
                <div class="alert-error">${error}</div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="alert-success">${success}</div>
            </c:if>
            <form action="transfer" method="post">
                <div class="form-group">
                    <label for="sourceAccountId">Source Account</label>
                    <select id="sourceAccountId" name="sourceAccountId" required>
                        <c:forEach var="acc" items="${accounts}">
                            <option value="${acc.id}">${acc.accountNumber} (Balance: ₹ ${acc.balance})</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="destinationAccountNumber">Destination Account Number</label>
                    <input type="text" id="destinationAccountNumber" name="destinationAccountNumber" required placeholder="Enter account number">
                </div>
                <div class="form-group">
                    <label for="destinationIfsc">Destination IFSC Code (11 characters)</label>
                    <input type="text" id="destinationIfsc" name="destinationIfsc" maxlength="11" required placeholder="ABCD0123456">
                </div>
                <div class="form-group">
                    <label for="amount">Transfer Amount (₹)</label>
                    <input type="number" step="0.01" min="1" id="amount" name="amount" required placeholder="0.00">
                </div>
                <button type="submit" style="width: 100%;">Transfer Funds Securely</button>
            </form>
        </div>
    </div>
</body>
</html>

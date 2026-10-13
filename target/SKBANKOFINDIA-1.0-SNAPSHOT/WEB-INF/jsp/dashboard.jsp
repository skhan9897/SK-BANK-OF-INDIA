<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Customer Dashboard - SK Bank of India</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <h1>Welcome, ${customer.firstName} ${customer.lastName}</h1>
        <nav>
            <a href="dashboard">Dashboard</a>
            <a href="transfer">Fund Transfer</a>
            <a href="ifsc-search">IFSC Search</a>
            <a href="login">Logout</a>
        </nav>
    </header>
    <div class="container">
        <div class="card">
            <h2>Your Bank Accounts</h2>
            <table>
                <thead>
                    <tr>
                        <th>Account Number</th>
                        <th>Type</th>
                        <th>Currency</th>
                        <th>Balance</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="acc" items="${accounts}">
                        <tr>
                            <td><strong>${acc.accountNumber}</strong></td>
                            <td>${acc.accountType}</td>
                            <td>${acc.currency}</td>
                            <td>₹ ${acc.balance}</td>
                            <td><span style="color: green;">${acc.status}</span></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="card">
            <h2>Recent Transactions & Passbook</h2>
            <c:if test="${empty transactions}">
                <p>No transactions found.</p>
            </c:if>
            <c:if test="${not empty transactions}">
                <table>
                    <thead>
                        <tr>
                            <th>Reference ID</th>
                            <th>Type</th>
                            <th>Amount</th>
                            <th>Balance After</th>
                            <th>Description</th>
                            <th>Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="tx" items="${transactions}">
                            <tr>
                                <td>${tx.transactionReference}</td>
                                <td><span style="color: ${tx.type eq 'TRANSFER_IN' or tx.type eq 'DEPOSIT' ? 'green' : 'red'};">${tx.type}</span></td>
                                <td>₹ ${tx.amount}</td>
                                <td>₹ ${tx.balanceAfter}</td>
                                <td>${tx.description}</td>
                                <td>${tx.createdAt}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
    </div>
</body>
</html>

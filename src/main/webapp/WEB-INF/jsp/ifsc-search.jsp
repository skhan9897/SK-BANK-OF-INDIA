<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>IFSC Code Search & Branch Directory - SK Bank of India</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <h1>SK Bank of India - IFSC & Branch Directory</h1>
        <nav>
            <a href="${pageContext.request.contextPath}/ifsc-search">IFSC Search</a>
            <a href="${pageContext.request.contextPath}/login">Login</a>
            <a href="${pageContext.request.contextPath}/register">Register</a>
        </nav>
    </header>
    <div class="container">
        <div class="card">
            <h2>Search by IFSC Code</h2>
            <form action="ifsc-search" method="get">
                <div class="form-group">
                    <label for="ifsc">Enter 11-Character IFSC (e.g. SKBI0000001)</label>
                    <input type="text" id="ifsc" name="ifsc" placeholder="ABCD0123456" maxlength="11" required value="${param.ifsc}">
                </div>
                <button type="submit">Verify & Search IFSC</button>
            </form>
        </div>

        <div class="card">
            <h2>Search by Branch, City, State or Bank Name</h2>
            <form action="ifsc-search" method="get">
                <div class="form-group">
                    <label for="query">Search Query</label>
                    <input type="text" id="query" name="query" placeholder="e.g. Mumbai, Nariman Point, SK Bank" required value="${param.query}">
                </div>
                <button type="submit">Search Directory</button>
            </form>
        </div>

        <c:if test="${not empty error}">
            <div class="alert-error">${error}</div>
        </c:if>

        <c:if test="${not empty branch}">
            <div class="card">
                <h3>Branch Details</h3>
                <p><strong>Branch Name:</strong> ${branch.branchName}</p>
                <p><strong>IFSC Code:</strong> ${branch.ifscCode}</p>
                <p><strong>MICR Code:</strong> ${branch.micrCode}</p>
                <p><strong>Address:</strong> ${branch.address}, ${branch.city}, ${branch.state} - ${branch.pinCode}</p>
                <p><strong>Verification Status:</strong> <span style="color: green; font-weight: bold;">${branch.verificationStatus}</span></p>
            </div>
        </c:if>

        <c:if test="${not empty branches}">
            <div class="card">
                <h3>Search Results (${branches.size()})</h3>
                <table>
                    <thead>
                        <tr>
                            <th>IFSC Code</th>
                            <th>Branch Name</th>
                            <th>City</th>
                            <th>State</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="b" items="${branches}">
                            <tr>
                                <td><strong>${b.ifscCode}</strong></td>
                                <td>${b.branchName}</td>
                                <td>${b.city}</td>
                                <td>${b.state}</td>
                                <td>${b.verificationStatus}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>
</body>
</html>

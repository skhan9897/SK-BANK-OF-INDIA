<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Centralized Master Admin Control - SK Bank Of India</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <h1>SK Bank Of India - Central Master Admin Control (All Linked Banks)</h1>
        <nav>
            <a href="${pageContext.request.contextPath}/home">Home</a>
            <a href="${pageContext.request.contextPath}/ifsc-search">IFSC Directory</a>
            <a href="${pageContext.request.contextPath}/login">Logout</a>
        </nav>
    </header>
    <div class="container">
        <!-- Register New Bank Form -->
        <div class="card">
            <h2>Register New Bank in Ecosystem</h2>
            <form action="dashboard" method="post">
                <input type="hidden" name="action" value="registerBank">
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
                    <div class="form-group">
                        <label>Bank Code</label>
                        <input type="text" name="bankCode" required placeholder="e.g. HDFC">
                    </div>
                    <div class="form-group">
                        <label>Bank Name</label>
                        <input type="text" name="bankName" required placeholder="e.g. HDFC Bank">
                    </div>
                    <div class="form-group">
                        <label>Sector</label>
                        <select name="sector">
                            <option value="PUBLIC">PUBLIC</option>
                            <option value="PRIVATE">PRIVATE</option>
                            <option value="FOREIGN">FOREIGN</option>
                            <option value="SMALL_FINANCE">SMALL_FINANCE</option>
                            <option value="COOPERATIVE">COOPERATIVE</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Headquarters</label>
                        <input type="text" name="headquarters" placeholder="e.g. Mumbai, India">
                    </div>
                    <div class="form-group">
                        <label>Website URL</label>
                        <input type="text" name="websiteUrl" placeholder="https://www.hdfcbank.com">
                    </div>
                </div>
                <button type="submit" style="margin-top: 1rem;">Register New Bank</button>
            </form>
        </div>

        <div class="card">
            <h2>Linked Banks & Admin Control (Ban / Unban)</h2>
            <table>
                <thead>
                    <tr>
                        <th>Bank Code</th>
                        <th>Bank Name</th>
                        <th>Sector</th>
                        <th>Headquarters</th>
                        <th>Status</th>
                        <th>Admin Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="bank" items="${banks}">
                        <tr>
                            <td><strong>${bank.bankCode}</strong></td>
                            <td>${bank.bankName}</td>
                            <td>${bank.sector}</td>
                            <td>${bank.headquarters}</td>
                            <td>
                                <span style="font-weight: bold; color: ${bank.active ? 'green' : 'red'};">
                                    ${bank.active ? 'ACTIVE' : 'SUSPENDED / BANNED'}
                                </span>
                            </td>
                            <td>
                                <form action="dashboard" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="toggleBankStatus">
                                    <input type="hidden" name="bankId" value="${bank.id}">
                                    <input type="hidden" name="active" value="${!bank.active}">
                                    <button type="submit" style="background-color: ${bank.active ? '#e76f51' : '#2a9d8f'}; padding: 0.4rem 0.8rem; font-size: 0.85rem;">
                                        ${bank.active ? 'Ban Bank' : 'Activate Bank'}
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="card">
            <h2>Centralized Branch & IFSC Verification Control</h2>
            <table>
                <thead>
                    <tr>
                        <th>Branch & Bank</th>
                        <th>IFSC Code</th>
                        <th>City</th>
                        <th>State</th>
                        <th>Verification Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="br" items="${branches}">
                        <tr>
                            <td>${br.branchName}</td>
                            <td><strong>${br.ifscCode}</strong></td>
                            <td>${br.city}</td>
                            <td>${br.state}</td>
                            <td><span style="font-weight: bold; color: ${br.verificationStatus eq 'VERIFIED' ? 'green' : 'orange'};">${br.verificationStatus}</span></td>
                            <td>
                                <form action="dashboard" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="verifyBranch">
                                    <input type="hidden" name="branchId" value="${br.id}">
                                    <select name="status" onchange="this.form.submit()" style="padding: 0.3rem;">
                                        <option value="VERIFIED" ${br.verificationStatus eq 'VERIFIED' ? 'selected' : ''}>VERIFIED</option>
                                        <option value="PENDING_REVIEW" ${br.verificationStatus eq 'PENDING_REVIEW' ? 'selected' : ''}>PENDING</option>
                                        <option value="REJECTED" ${br.verificationStatus eq 'REJECTED' ? 'selected' : ''}>REJECTED</option>
                                    </select>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="card">
            <h2>Registered Customers Across All Banks</h2>
            <table>
                <thead>
                    <tr>
                        <th>Customer ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="cust" items="${customers}">
                        <tr>
                            <td>${cust.customerId}</td>
                            <td>${cust.firstName} ${cust.lastName}</td>
                            <td>${cust.email}</td>
                            <td>${cust.phone}</td>
                            <td><span style="color: green;">${cust.status}</span></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>

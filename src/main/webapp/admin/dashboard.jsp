<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SK Bank of India - Admin Control Panel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="d-flex">
    <!-- Admin Sidebar -->
    <div class="bg-dark text-white p-3 min-vh-100" style="width: 260px;">
        <h4 class="fw-bold text-warning mb-4"><i class="fa-solid fa-user-shield me-2"></i>SKBI Admin</h4>
        <ul class="nav nav-pills flex-column mb-auto">
            <li class="nav-item mb-2"><a href="#" class="nav-link active text-white bg-primary"><i class="fa-solid fa-chart-line me-2"></i> Dashboard Analytics</a></li>
            <li class="nav-item mb-2"><a href="#" class="nav-link text-white-50"><i class="fa-solid fa-users me-2"></i> Customer Management</a></li>
            <li class="nav-item mb-2"><a href="#" class="nav-link text-white-50"><i class="fa-solid fa-file-signature me-2"></i> KYC Approvals</a></li>
            <li class="nav-item mb-2"><a href="#" class="nav-link text-white-50"><i class="fa-solid fa-hand-holding-dollar me-2"></i> Loan Applications</a></li>
            <li class="nav-item mb-2"><a href="#" class="nav-link text-white-50"><i class="fa-solid fa-triangle-exclamation me-2"></i> Fraud Alerts</a></li>
            <li class="nav-item mb-2"><a href="#" class="nav-link text-white-50"><i class="fa-solid fa-scroll me-2"></i> Audit Logs</a></li>
        </ul>
        <hr>
        <button onclick="logout()" class="btn btn-outline-danger btn-sm w-100"><i class="fa-solid fa-right-from-bracket me-1"></i> Admin Logout</button>
    </div>

    <!-- Main Content -->
    <div class="flex-grow-1 p-4">
        <h3 class="fw-bold mb-4">Bank System Analytics & Management</h3>

        <!-- Analytics Cards -->
        <div class="row g-4 mb-4">
            <div class="col-md-3">
                <div class="sk-card bg-white border-start border-4 border-primary">
                    <div class="text-muted small fw-bold">TOTAL CUSTOMERS</div>
                    <div class="fs-2 fw-bold text-dark" id="admTotalCustomers">1,240</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card bg-white border-start border-4 border-success">
                    <div class="text-muted small fw-bold">TOTAL ACCOUNTS</div>
                    <div class="fs-2 fw-bold text-dark" id="admTotalAccounts">1,890</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card bg-white border-start border-4 border-warning">
                    <div class="text-muted small fw-bold">TOTAL DEPOSITS</div>
                    <div class="fs-2 fw-bold text-dark" id="admTotalDeposits">₹ 18.4 Cr</div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card bg-white border-start border-4 border-info">
                    <div class="text-muted small fw-bold">PENDING KYCs</div>
                    <div class="fs-2 fw-bold text-dark" id="admPendingKycs">12</div>
                </div>
            </div>
        </div>

        <!-- Fraud & Security Alerts -->
        <div class="sk-card mb-4">
            <h5 class="fw-bold text-danger mb-3"><i class="fa-solid fa-shield-cat me-2"></i> System Fraud Alerts & Security Audit</h5>
            <div class="table-responsive">
                <table class="table sk-table mb-0">
                    <thead>
                        <tr>
                            <th>Alert ID</th>
                            <th>Trigger Rule</th>
                            <th>Risk Score</th>
                            <th>Target User</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>#ALT-9081</td>
                            <td>High Amount Off-hours IMPS Transfer</td>
                            <td><span class="badge bg-danger">HIGH (85)</span></td>
                            <td>cust_9011</td>
                            <td><span class="badge bg-warning text-dark">UNDER REVIEW</span></td>
                            <td><button class="btn btn-sm btn-outline-secondary" onclick="alert('Investigating fraud alert')">Review</button></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script src="../js/app.js"></script>
</body>
</html>

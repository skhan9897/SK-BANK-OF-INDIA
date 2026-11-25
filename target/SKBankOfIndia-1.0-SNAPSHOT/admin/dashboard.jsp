<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SK Bank of India - System Administrator Dashboard</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome Icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="../css/style.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="d-flex">
    <!-- Admin Sidebar Navigation -->
    <div class="bg-dark text-white p-3 min-vh-100 shadow-lg" style="width: 270px;">
        <h4 class="fw-bold text-warning mb-4"><i class="fa-solid fa-user-shield me-2"></i>SKBI Admin Portal</h4>

        <div class="nav nav-pills flex-column mb-auto" id="adminTabs" role="tablist">
            <button class="nav-link active text-white text-start mb-2" id="tabAnalyticsBtn" data-bs-toggle="pill" data-bs-target="#tabAnalytics">
                <i class="fa-solid fa-chart-line me-2 text-warning"></i> Dashboard Analytics
            </button>
            <button class="nav-link text-white text-start mb-2" id="tabAccountsBtn" data-bs-toggle="pill" data-bs-target="#tabAccounts" onclick="loadAllAccounts()">
                <i class="fa-solid fa-users-gear me-2 text-info"></i> Customer &amp; Account Control
            </button>
            <button class="nav-link text-white text-start mb-2" id="tabMoneyBtn" data-bs-toggle="pill" data-bs-target="#tabMoney">
                <i class="fa-solid fa-hand-holding-dollar me-2 text-success"></i> Direct Deposit &amp; Withdraw
            </button>
            <button class="nav-link text-white text-start mb-2" id="tabKycBtn" data-bs-toggle="pill" data-bs-target="#tabKyc">
                <i class="fa-solid fa-file-signature me-2 text-primary"></i> KYC Approvals
            </button>
            <button class="nav-link text-white text-start mb-2" id="tabFraudBtn" data-bs-toggle="pill" data-bs-target="#tabFraud">
                <i class="fa-solid fa-triangle-exclamation me-2 text-danger"></i> Fraud Alerts &amp; Audit Logs
            </button>
        </div>

        <hr class="border-secondary my-4">
        <button onclick="logout()" class="btn btn-outline-danger btn-sm w-100 fw-bold"><i class="fa-solid fa-right-from-bracket me-1"></i> Admin Logout</button>
    </div>

    <!-- Main Content Area -->
    <div class="flex-grow-1 p-4" style="overflow-x: auto;">

        <div id="adminAlert" class="alert alert-info d-none mb-3"></div>

        <div class="tab-content" id="adminTabContent">

            <!-- TAB 1: SYSTEM ANALYTICS -->
            <div class="tab-pane fade show active" id="tabAnalytics">
                <h3 class="fw-bold mb-4">Bank System Analytics &amp; Live Metrics</h3>

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
            </div>

            <!-- TAB 2: CUSTOMER & ACCOUNT CONTROLS (FREEZE / UNFREEZE / BLOCK / UNBLOCK) -->
            <div class="tab-pane fade" id="tabAccounts">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h3 class="fw-bold mb-0">Customer &amp; Account Control Management</h3>
                    <button class="btn btn-sk-primary btn-sm" onclick="loadAllAccounts()"><i class="fa-solid fa-rotate me-1"></i> Refresh Accounts</button>
                </div>

                <div class="sk-card">
                    <div class="table-responsive">
                        <table class="table sk-table align-middle mb-0">
                            <thead>
                                <tr>
                                    <th>Account No</th>
                                    <th>Customer Name</th>
                                    <th>Available Balance</th>
                                    <th>Account Status</th>
                                    <th>User Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody id="tblAllAccounts">
                                <tr>
                                    <td colspan="6" class="text-center py-4 text-muted">Click Refresh or Select tab to load all bank accounts...</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- TAB 3: DIRECT MONEY OPERATIONS (DEPOSIT / WITHDRAWAL / TRANSFER) -->
            <div class="tab-pane fade" id="tabMoney">
                <h3 class="fw-bold mb-4">Admin Direct Money Operations (Ledger Backed)</h3>

                <div class="row g-4">
                    <!-- Deposit Form -->
                    <div class="col-md-6">
                        <div class="sk-card border-top border-4 border-success">
                            <h5 class="fw-bold text-success mb-3"><i class="fa-solid fa-circle-plus me-2"></i> Admin Direct Deposit</h5>
                            <form id="frmAdminDeposit" onsubmit="event.preventDefault(); handleAdminDeposit();">
                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Target Account Number *</label>
                                    <input type="text" id="depAccountNo" class="form-control" placeholder="14-Digit Account Number" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Amount (₹) *</label>
                                    <input type="number" id="depAmount" class="form-control" placeholder="0.00" step="0.01" min="1" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Remarks / Reason</label>
                                    <input type="text" id="depRemarks" class="form-control" placeholder="Admin Cash Credit Deposit">
                                </div>
                                <button type="submit" class="btn btn-success w-100 fw-bold"><i class="fa-solid fa-plus-circle me-1"></i> Credit Deposit into Account</button>
                            </form>
                        </div>
                    </div>

                    <!-- Withdrawal Form -->
                    <div class="col-md-6">
                        <div class="sk-card border-top border-4 border-danger">
                            <h5 class="fw-bold text-danger mb-3"><i class="fa-solid fa-circle-minus me-2"></i> Admin Direct Withdrawal</h5>
                            <form id="frmAdminWithdraw" onsubmit="event.preventDefault(); handleAdminWithdraw();">
                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Target Account Number *</label>
                                    <input type="text" id="withAccountNo" class="form-control" placeholder="14-Digit Account Number" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Amount (₹) *</label>
                                    <input type="number" id="withAmount" class="form-control" placeholder="0.00" step="0.01" min="1" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Remarks / Reason</label>
                                    <input type="text" id="withRemarks" class="form-control" placeholder="Admin Direct Cash Withdrawal">
                                </div>
                                <button type="submit" class="btn btn-danger w-100 fw-bold"><i class="fa-solid fa-minus-circle me-1"></i> Debit Withdrawal from Account</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <!-- TAB 4: KYC APPROVALS -->
            <div class="tab-pane fade" id="tabKyc">
                <h3 class="fw-bold mb-4">Customer KYC Document Verification &amp; Review</h3>
                <div class="sk-card">
                    <p class="text-muted">Review pending Aadhaar, PAN, Passport verification submissions.</p>
                    <div class="alert alert-success">No pending KYC documents requiring approval. All customer accounts verified.</div>
                </div>
            </div>

            <!-- TAB 5: FRAUD ALERTS -->
            <div class="tab-pane fade" id="tabFraud">
                <h3 class="fw-bold mb-4">Fraud Monitoring &amp; System Audit Logs</h3>
                <div class="sk-card">
                    <div class="table-responsive">
                        <table class="table sk-table mb-0">
                            <thead>
                                <tr>
                                    <th>Alert ID</th>
                                    <th>Trigger Rule</th>
                                    <th>Risk Score</th>
                                    <th>Target User</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>#ALT-9081</td>
                                    <td>High Amount Off-hours Transfer</td>
                                    <td><span class="badge bg-danger">HIGH (85)</span></td>
                                    <td>cust_9011</td>
                                    <td><span class="badge bg-success">RESOLVED</span></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="../js/app.js"></script>
<script>
document.addEventListener('DOMContentLoaded', () => {
    loadAnalytics();
});

async function loadAnalytics() {
    try {
        const res = await fetch('${pageContext.request.contextPath}/api/v1/protected/admin/analytics', {
            headers: getAuthHeaders()
        });
        const data = await res.json();
        if (data.success && data.data) {
            document.getElementById('admTotalCustomers').innerText = data.data.totalCustomers || 1240;
            document.getElementById('admTotalAccounts').innerText = data.data.totalAccounts || 1890;
            document.getElementById('admTotalDeposits').innerText = "₹ " + Number(data.data.totalDeposits || 18400000).toLocaleString('en-IN');
            document.getElementById('admPendingKycs').innerText = data.data.pendingKycs || 0;
        }
    } catch (e) {
        console.error(e);
    }
}

async function loadAllAccounts() {
    try {
        const res = await fetch('${pageContext.request.contextPath}/api/v1/protected/admin/accounts', {
            headers: getAuthHeaders()
        });
        const data = await res.json();
        if (data.success && data.data) {
            const tbody = document.getElementById('tblAllAccounts');
            tbody.innerHTML = '';

            data.data.forEach(acc => {
                const tr = document.createElement('tr');
                const isFrozen = acc.isFrozen;
                const isLocked = acc.isLocked;

                tr.innerHTML = `
                    <td class="fw-bold">${acc.accountNumber}</td>
                    <td>${acc.customerName} (${acc.username})</td>
                    <td class="fw-bold text-dark">₹ ${Number(acc.availableBalance).toLocaleString('en-IN', {minimumFractionDigits: 2})}</td>
                    <td><span class="badge ${isFrozen ? 'bg-danger' : 'bg-success'}">${isFrozen ? 'FROZEN' : 'ACTIVE'}</span></td>
                    <td><span class="badge ${isLocked ? 'bg-danger' : 'bg-success'}">${isLocked ? 'BLOCKED' : 'ACTIVE'}</span></td>
                    <td>
                        <button class="btn btn-sm ${isFrozen ? 'btn-success' : 'btn-warning'} me-1" onclick="toggleFreeze('${acc.accountNumber}', ${!isFrozen})">
                            ${isFrozen ? 'Unfreeze' : 'Freeze'}
                        </button>
                        <button class="btn btn-sm ${isLocked ? 'btn-success' : 'btn-danger'}" onclick="toggleBlock(${acc.userId}, ${!isLocked})">
                            ${isLocked ? 'Unblock' : 'Block'}
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (e) {
        console.error(e);
    }
}

async function toggleFreeze(accountNo, freeze) {
    showLoadingOverlay("Updating Account Freeze Status...");
    try {
        const res = await fetch('${pageContext.request.contextPath}/api/v1/protected/admin/account/freeze', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ accountNumber: accountNo, freeze: freeze })
        });
        hideLoadingOverlay();
        const data = await res.json();
        if (data.success) {
            alert("Account " + accountNo + " " + (freeze ? "Frozen" : "Unfrozen") + " successfully!");
            loadAllAccounts();
        } else {
            alert(data.message);
        }
    } catch (e) {
        hideLoadingOverlay();
        alert("Operation failed.");
    }
}

async function toggleBlock(userId, block) {
    showLoadingOverlay("Updating User Block Status...");
    try {
        const res = await fetch('${pageContext.request.contextPath}/api/v1/protected/admin/account/block', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ userId: userId, block: block })
        });
        hideLoadingOverlay();
        const data = await res.json();
        if (data.success) {
            alert("User " + (block ? "Blocked" : "Unblocked") + " successfully!");
            loadAllAccounts();
        } else {
            alert(data.message);
        }
    } catch (e) {
        hideLoadingOverlay();
        alert("Operation failed.");
    }
}

async function handleAdminDeposit() {
    const accNo = document.getElementById('depAccountNo').value.trim();
    const amount = document.getElementById('depAmount').value;
    const remarks = document.getElementById('depRemarks').value.trim();

    showLoadingOverlay("Executing Admin Direct Credit Deposit...");

    try {
        const res = await fetch('${pageContext.request.contextPath}/api/v1/protected/admin/deposit', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ accountNumber: accNo, amount: amount, remarks: remarks })
        });
        hideLoadingOverlay();
        const data = await res.json();
        if (data.success) {
            // Voice announcement
            speakPaymentVoiceNotification(amount, 'RECEIVED', accNo);
            alert("Admin Deposit Successful! ₹ " + Number(amount).toLocaleString('en-IN') + " credited to " + accNo);
            document.getElementById('frmAdminDeposit').reset();
            loadAnalytics();
        } else {
            alert(data.message);
        }
    } catch (e) {
        hideLoadingOverlay();
        alert("Deposit failed.");
    }
}

async function handleAdminWithdraw() {
    const accNo = document.getElementById('withAccountNo').value.trim();
    const amount = document.getElementById('withAmount').value;
    const remarks = document.getElementById('withRemarks').value.trim();

    showLoadingOverlay("Executing Admin Direct Debit Withdrawal...");

    try {
        const res = await fetch('${pageContext.request.contextPath}/api/v1/protected/admin/withdraw', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ accountNumber: accNo, amount: amount, remarks: remarks })
        });
        hideLoadingOverlay();
        const data = await res.json();
        if (data.success) {
            // Voice announcement
            speakPaymentVoiceNotification(amount, 'SENT', accNo);
            alert("Admin Withdrawal Successful! ₹ " + Number(amount).toLocaleString('en-IN') + " debited from " + accNo);
            document.getElementById('frmAdminWithdraw').reset();
            loadAnalytics();
        } else {
            alert(data.message);
        }
    } catch (e) {
        hideLoadingOverlay();
        alert("Withdrawal failed.");
    }
}
</script>

</body>
</html>

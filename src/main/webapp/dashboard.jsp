<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="container py-4">
    <!-- Welcome Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-1" id="welcomeUserHeader">Welcome, Customer</h2>
            <p class="text-muted small mb-0"><i class="fa-solid fa-clock me-1 text-warning"></i> Last Login: Today, Online | Account Status: <span class="badge bg-success">ACTIVE & VERIFIED</span></p>
        </div>
        <div>
            <a href="transfers.jsp" class="btn btn-sk-primary"><i class="fa-solid fa-paper-plane me-1"></i> Quick Send Money</a>
            <button onclick="logout()" class="btn btn-outline-danger ms-2"><i class="fa-solid fa-power-off me-1"></i> Logout</button>
        </div>
    </div>

    <!-- Account Cards Grid -->
    <div class="row g-4 mb-4" id="accountCardsContainer">
        <div class="col-md-6 col-lg-4">
            <div class="sk-card">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span class="text-muted small fw-semibold">PRIMARY SAVINGS ACCOUNT</span>
                    <span class="badge bg-primary">SAVINGS</span>
                </div>
                <div class="fs-6 text-muted mb-2" id="dashAccountNo">1001••••••••9812</div>
                <div class="d-flex justify-content-between align-items-center mt-3">
                    <div>
                        <div class="small text-muted">Available Balance</div>
                        <div class="fs-3 fw-bold text-dark" id="dashBalance">₹ 10,000.00</div>
                    </div>
                    <button class="btn btn-light btn-sm" onclick="toggleBalanceVisibility('dashBalance')">
                        <i class="fa-regular fa-eye"></i>
                    </button>
                </div>
                <div class="mt-3 pt-3 border-top d-flex justify-content-between fs-7">
                    <span class="text-muted">IFSC: <strong id="dashIfsc">SKBI0000001</strong></span>
                    <a href="accounts.jsp" class="text-decoration-none fw-bold">View Statement <i class="fa-solid fa-arrow-right"></i></a>
                </div>
            </div>
        </div>

        <div class="col-md-6 col-lg-4">
            <div class="sk-card">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span class="text-muted small fw-semibold">UPI PAYMENT VPA</span>
                    <span class="badge bg-success"><i class="fa-solid fa-qrcode me-1"></i> ACTIVE</span>
                </div>
                <div class="fs-5 fw-bold text-dark mb-2" id="dashUpiVpa">user@skbank</div>
                <p class="small text-muted">Scan & Pay enabled across BHIM, Google Pay, PhonePe & Paytm</p>
                <div class="mt-3 pt-3 border-top d-flex justify-content-between">
                    <a href="transfers.jsp" class="btn btn-sm btn-outline-primary"><i class="fa-solid fa-qrcode me-1"></i> My QR Code</a>
                    <a href="transfers.jsp" class="btn btn-sm btn-sk-primary">Send UPI</a>
                </div>
            </div>
        </div>

        <div class="col-md-6 col-lg-4">
            <div class="sk-card">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span class="text-muted small fw-semibold">ACTIVE CARDS</span>
                    <span class="badge bg-warning text-dark">DEBIT CARD</span>
                </div>
                <div class="fw-bold">Platinum International Debit</div>
                <div class="small text-muted">Daily ATM Limit: ₹ 25,000</div>
                <div class="mt-3 pt-3 border-top d-flex justify-content-between align-items-center">
                    <span class="text-success small"><i class="fa-solid fa-lock-open me-1"></i> Card Unlocked</span>
                    <a href="cards.jsp" class="btn btn-sm btn-outline-secondary">Manage Cards</a>
                </div>
            </div>
        </div>
    </div>

    <!-- Recent Transactions & Quick Actions -->
    <div class="row g-4">
        <div class="col-lg-8">
            <div class="sk-card">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="fw-bold mb-0"><i class="fa-solid fa-list-check me-2 text-primary"></i> Recent Transactions</h5>
                    <a href="accounts.jsp" class="btn btn-sm btn-outline-primary">View All</a>
                </div>

                <div class="table-responsive">
                    <table class="table sk-table mb-0">
                        <thead>
                            <tr>
                                <th>Transaction Ref</th>
                                <th>Type / Method</th>
                                <th>Amount</th>
                                <th>Status</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody id="dashTxnTableBody">
                            <tr>
                                <td><span class="fw-bold">TXN20251125001</span></td>
                                <td>Welcome Credit Deposit</td>
                                <td><span class="badge-credit">+ ₹ 10,000.00</span></td>
                                <td><span class="badge bg-success">COMPLETED</span></td>
                                <td class="small text-muted">Today</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-lg-4">
            <div class="sk-card mb-4">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-bolt me-2 text-warning"></i> Quick Actions</h5>
                <div class="d-grid gap-2">
                    <a href="transfers.jsp" class="btn btn-outline-primary text-start py-2"><i class="fa-solid fa-money-bill-transfer me-2"></i> IMPS / NEFT Money Transfer</a>
                    <a href="loans.jsp" class="btn btn-outline-primary text-start py-2"><i class="fa-solid fa-calculator me-2"></i> Loan EMI Calculator & Apply</a>
                    <a href="services.jsp" class="btn btn-outline-primary text-start py-2"><i class="fa-solid fa-file-invoice-dollar me-2"></i> Pay Electricity & Utility Bills</a>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', () => {
    checkAuthAndRedirect();
    loadDashboardData();
});

async function loadDashboardData() {
    try {
        const res = await fetch('/SKBankOfIndia/api/v1/protected/accounts', {
            headers: getAuthHeaders()
        });

        if (res.status === 401) {
            logout();
            return;
        }

        const data = await res.json();
        if (data.success && data.data && data.data.length > 0) {
            const acc = data.data[0];
            document.getElementById('dashAccountNo').innerText = acc.accountNumber;
            document.getElementById('dashBalance').innerText = "₹ " + Number(acc.balance).toLocaleString('en-IN', { minimumFractionDigits: 2 });
            document.getElementById('dashIfsc').innerText = acc.branchIfsc || "SKBI0000001";
        }
    } catch (e) {
        console.error("Error loading dashboard", e);
    }
}
</script>

<%@ include file="/WEB-INF/views/footer.jspf" %>

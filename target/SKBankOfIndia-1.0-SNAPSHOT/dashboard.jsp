<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="container py-4">
    <!-- PhonePe-Style Dashboard Welcome Header -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div class="d-flex align-items-center">
            <div class="rounded-circle bg-warning text-dark fw-bold fs-4 d-flex justify-content-center align-items-center me-3" style="width: 50px; height: 50px;">
                SK
            </div>
            <div>
                <h2 class="fw-bold mb-0 text-white" id="welcomeUserHeader">SK Bank of India</h2>
                <p class="text-warning small mb-0"><i class="fa-solid fa-location-dot me-1"></i> Mumbai Main Branch • A/c: <span id="dashAccountNo">10018877665544</span></p>
            </div>
        </div>
        <div class="d-flex gap-2">
            <a href="services.jsp" class="btn btn-outline-light btn-sm"><i class="fa-solid fa-magnifying-glass me-1"></i> Search</a>
            <a href="download-app.jsp" class="btn btn-warning btn-sm fw-bold"><i class="fa-brands fa-android me-1"></i> Mobile App</a>
            <button onclick="logout()" class="btn btn-outline-danger btn-sm"><i class="fa-solid fa-power-off me-1"></i> Logout</button>
        </div>
    </div>

    <!-- Promotional Digital Loan Banner Card -->
    <div class="sk-card bg-navy text-white mb-4 p-4 border-top border-4 border-warning" style="background: linear-gradient(135deg, #281343 0%, #351A57 100%);">
        <div class="d-flex justify-content-between align-items-center">
            <div>
                <span class="badge bg-warning text-dark fw-bold mb-2">AD • 100% DIGITAL BANKING</span>
                <h3 class="fw-bold mb-1">Loans &amp; Investments Under 7 mins</h3>
                <p class="text-white-50 mb-3">Pre-approved instant personal loans &amp; fixed deposits up to ₹ 5,00,000</p>
                <a href="loans.jsp" class="btn btn-sk-primary btn-sm px-4 fw-bold">Check Eligibility <i class="fa-solid fa-arrow-right ms-1"></i></a>
            </div>
            <div class="d-none d-md-block text-warning fs-1">
                <i class="fa-solid fa-hand-holding-dollar"></i>
            </div>
        </div>
    </div>

    <!-- Section 1: Transfer Money (4 Circular Action Items Grid) -->
    <div class="mb-4">
        <h5 class="fw-bold text-white mb-3"><i class="fa-solid fa-money-bill-transfer text-warning me-2"></i> Transfer Money</h5>

        <div class="row g-3 text-center">
            <div class="col-6 col-md-3">
                <a href="transfers.jsp" class="text-decoration-none">
                    <div class="sk-card p-3 h-100 d-flex flex-column align-items-center justify-content-center hover-glow" style="background: #281343;">
                        <div class="rounded-circle bg-primary bg-opacity-20 text-white fs-3 d-flex align-items-center justify-content-center mb-2" style="width: 56px; height: 56px;">
                            <i class="fa-solid fa-user"></i>
                        </div>
                        <div class="fw-bold text-white fs-7">To Mobile Number</div>
                    </div>
                </a>
            </div>

            <div class="col-6 col-md-3">
                <a href="transfers.jsp" class="text-decoration-none">
                    <div class="sk-card p-3 h-100 d-flex flex-column align-items-center justify-content-center hover-glow" style="background: #281343;">
                        <div class="rounded-circle bg-success bg-opacity-20 text-white fs-3 d-flex align-items-center justify-content-center mb-2" style="width: 56px; height: 56px;">
                            <i class="fa-solid fa-building-columns"></i>
                        </div>
                        <div class="fw-bold text-white fs-7">To Bank / UPI ID</div>
                    </div>
                </a>
            </div>

            <div class="col-6 col-md-3">
                <a href="accounts.jsp" class="text-decoration-none">
                    <div class="sk-card p-3 h-100 d-flex flex-column align-items-center justify-content-center hover-glow" style="background: #281343;">
                        <div class="rounded-circle bg-warning bg-opacity-20 text-white fs-3 d-flex align-items-center justify-content-center mb-2" style="width: 56px; height: 56px;">
                            <i class="fa-solid fa-arrows-rotate"></i>
                        </div>
                        <div class="fw-bold text-white fs-7">To Self Account</div>
                    </div>
                </a>
            </div>

            <div class="col-6 col-md-3">
                <a href="javascript:void(0)" onclick="toggleBalanceVisibility('dashBalance')" class="text-decoration-none">
                    <div class="sk-card p-3 h-100 d-flex flex-column align-items-center justify-content-center hover-glow" style="background: #281343;">
                        <div class="rounded-circle bg-info bg-opacity-20 text-white fs-3 d-flex align-items-center justify-content-center mb-2" style="width: 56px; height: 56px;">
                            <i class="fa-solid fa-wallet"></i>
                        </div>
                        <div class="fw-bold text-white fs-7">Check Bank Balance</div>
                    </div>
                </a>
            </div>
        </div>
    </div>

    <!-- Section 2: UPI ID Strip & Balance Visibility -->
    <div class="sk-card mb-4 p-3 d-flex flex-wrap justify-content-between align-items-center" style="background: #281343;">
        <div class="d-flex align-items-center gap-2">
            <span class="text-white-50 small">UPI ID:</span>
            <strong class="text-warning fs-6" id="dashUpiVpa">customer@skbank</strong>
        </div>
        <div class="d-flex align-items-center gap-3">
            <span class="text-white-50 small">Available Balance:</span>
            <strong class="fs-4 text-white" id="dashBalance">₹ 10,000.00</strong>
            <button class="btn btn-outline-light btn-sm" onclick="toggleBalanceVisibility('dashBalance')">
                <i class="fa-regular fa-eye"></i>
            </button>
        </div>
    </div>

    <!-- Section 3: Quick Features Cards (SK Wallet, My QR Code, Chat & Send) -->
    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <a href="services.jsp" class="text-decoration-none">
                <div class="sk-card p-3 text-center" style="background: #281343;">
                    <i class="fa-solid fa-briefcase text-warning fs-2 mb-2"></i>
                    <h6 class="fw-bold text-white mb-0">SK Wallet</h6>
                </div>
            </a>
        </div>
        <div class="col-md-4">
            <a href="services.jsp" class="text-decoration-none">
                <div class="sk-card p-3 text-center" style="background: #281343;">
                    <i class="fa-solid fa-qrcode text-info fs-2 mb-2"></i>
                    <h6 class="fw-bold text-white mb-0">My Payment QR</h6>
                </div>
            </a>
        </div>
        <div class="col-md-4">
            <a href="transfers.jsp" class="text-decoration-none">
                <div class="sk-card p-3 text-center" style="background: #281343;">
                    <i class="fa-solid fa-comments text-success fs-2 mb-2"></i>
                    <h6 class="fw-bold text-white mb-0">Chat &amp; Send Money</h6>
                </div>
            </a>
        </div>
    </div>

    <!-- Section 4: Recharge & Pay Bills Grid -->
    <div class="mb-4">
        <h5 class="fw-bold text-white mb-3"><i class="fa-solid fa-bolt-lightning text-warning me-2"></i> Recharge &amp; Pay Bills</h5>

        <div class="row g-3 text-center">
            <div class="col-6 col-md-3">
                <a href="services.jsp" class="text-decoration-none">
                    <div class="sk-card p-3 h-100" style="background: #281343;">
                        <i class="fa-solid fa-mobile-screen text-warning fs-2 mb-2"></i>
                        <div class="fw-bold text-white fs-7">Mobile Recharge</div>
                    </div>
                </a>
            </div>

            <div class="col-6 col-md-3">
                <a href="services.jsp" class="text-decoration-none">
                    <div class="sk-card p-3 h-100" style="background: #281343;">
                        <i class="fa-solid fa-car text-info fs-2 mb-2"></i>
                        <div class="fw-bold text-white fs-7">Buy FASTag</div>
                    </div>
                </a>
            </div>

            <div class="col-6 col-md-3">
                <a href="services.jsp" class="text-decoration-none">
                    <div class="sk-card p-3 h-100" style="background: #281343;">
                        <i class="fa-solid fa-satellite-dish text-success fs-2 mb-2"></i>
                        <div class="fw-bold text-white fs-7">DTH Recharge</div>
                    </div>
                </a>
            </div>

            <div class="col-6 col-md-3">
                <a href="services.jsp" class="text-decoration-none">
                    <div class="sk-card p-3 h-100" style="background: #281343;">
                        <i class="fa-solid fa-lightbulb text-warning fs-2 mb-2"></i>
                        <div class="fw-bold text-white fs-7">Electricity Bill</div>
                    </div>
                </a>
            </div>
        </div>
    </div>

    <!-- Recent Passbook Transactions Table -->
    <div class="sk-card" style="background: #281343;">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="fw-bold text-white mb-0"><i class="fa-solid fa-receipt me-2 text-warning"></i> Recent Passbook Transactions</h5>
            <a href="accounts.jsp" class="btn btn-sm btn-outline-light">View All</a>
        </div>

        <div class="table-responsive">
            <table class="table table-dark table-hover align-middle mb-0">
                <thead>
                    <tr>
                        <th>Transaction Ref</th>
                        <th>Type / Method</th>
                        <th>Amount</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody id="dashTxnTableBody">
                    <tr>
                        <td class="fw-bold">TXN20251125001</td>
                        <td>Welcome Credit Deposit</td>
                        <td class="text-success fw-bold">+ ₹ 10,000.00</td>
                        <td><span class="badge bg-success">COMPLETED</span></td>
                    </tr>
                </tbody>
            </table>
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
        const res = await fetch('${pageContext.request.contextPath}/api/v1/protected/accounts', {
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
        }
    } catch (e) {
        console.error("Error loading dashboard", e);
    }
}
</script>

<%@ include file="/WEB-INF/views/footer.jspf" %>

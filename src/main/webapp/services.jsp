<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="container py-4">
    <h2 class="fw-bold mb-4">Banking Services & Utility Bill Payments</h2>

    <div class="row g-4">
        <!-- Utility Bills -->
        <div class="col-md-6 col-lg-4">
            <div class="sk-card">
                <div class="fs-1 text-warning mb-2"><i class="fa-solid fa-bolt-lightning"></i></div>
                <h5 class="fw-bold">Electricity Bill Payment</h5>
                <p class="small text-muted">Pay Mahadiscom, BSES, TATA Power, and state electricity bills instantly.</p>
                <div class="mb-3">
                    <input type="text" class="form-control" placeholder="Consumer No. / Account ID">
                </div>
                <button class="btn btn-sk-primary w-100" onclick="alert('Fetching bill from Bharat BillPay BBPS sandbox...')">Fetch & Pay Bill</button>
            </div>
        </div>

        <div class="col-md-6 col-lg-4">
            <div class="sk-card">
                <div class="fs-1 text-primary mb-2"><i class="fa-solid fa-mobile-screen"></i></div>
                <h5 class="fw-bold">Mobile & DTH Recharge</h5>
                <p class="small text-muted">Instant prepaid & postpaid recharge for Jio, Airtel, Vi, and Tata Play.</p>
                <div class="mb-3">
                    <input type="tel" class="form-control" placeholder="10-digit Mobile Number">
                </div>
                <button class="btn btn-sk-primary w-100" onclick="alert('Recharge order placed successfully!')">Recharge Now</button>
            </div>
        </div>

        <!-- Cheque Book Request -->
        <div class="col-md-6 col-lg-4">
            <div class="sk-card">
                <div class="fs-1 text-success mb-2"><i class="fa-solid fa-money-check"></i></div>
                <h5 class="fw-bold">Cheque Book Request</h5>
                <p class="small text-muted">Request a personalized 25-leaf or 50-leaf cheque book delivered to home.</p>
                <div class="mb-3">
                    <select class="form-select">
                        <option>25 Leaves (Free)</option>
                        <option>50 Leaves (₹ 100 fee)</option>
                    </select>
                </div>
                <button class="btn btn-sk-primary w-100" onclick="alert('Cheque book dispatch order placed!')">Request Cheque Book</button>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>

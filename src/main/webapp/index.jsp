<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/header.jspf" %>

<!-- Hero Banner -->
<section class="sk-hero">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-lg-7">
                <span class="badge bg-warning text-dark px-3 py-2 mb-3 fw-bold rounded-pill"><i class="fa-solid fa-shield-halved me-1"></i> RBI Regulated Digital Bank</span>
                <h1 class="mb-3">Next-Generation Banking for India.</h1>
                <p class="lead text-white-50 mb-4">Experience instant zero-fee UPI transfers, high-yield savings accounts, virtual debit cards, and seamless digital banking at your fingertips.</p>
                <div class="d-flex flex-wrap gap-3">
                    <a href="register.jsp" class="btn btn-sk-accent btn-lg"><i class="fa-solid fa-user-plus me-2"></i> Open Savings Account</a>
                    <a href="login.jsp" class="btn btn-outline-light btn-lg"><i class="fa-solid fa-lock me-2"></i> Internet Banking Login</a>
                    <a href="download-app.jsp" class="btn btn-warning btn-lg fw-bold text-dark"><i class="fa-brands fa-android me-2"></i> Download / Update Mobile App</a>
                </div>
            </div>
            <div class="col-lg-5 text-center mt-5 mt-lg-0">
                <div class="sk-credit-card mx-auto" style="max-width: 380px;">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="fw-bold">SK BANK OF INDIA</span>
                        <i class="fa-solid fa-wifi text-warning fs-5"></i>
                    </div>
                    <div class="chip"></div>
                    <div class="fs-4 fw-bold tracking-wider mb-2">4532 •••• •••• 9812</div>
                    <div class="d-flex justify-content-between align-items-end mt-4">
                        <div>
                            <div class="small text-white-50">CARD HOLDER</div>
                            <div class="fw-bold">PRIMARY ACCOUNT</div>
                        </div>
                        <div>
                            <div class="small text-white-50">VALID THRU</div>
                            <div class="fw-bold">12/28</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Mobile Banking App Showcase Banner -->
<section class="py-5 bg-light border-bottom border-top">
    <div class="container">
        <div class="row align-items-center g-4">
            <div class="col-md-3 text-center">
                <div class="bg-navy text-warning p-4 rounded-4 d-inline-block shadow-sm">
                    <i class="fa-brands fa-android display-1"></i>
                </div>
            </div>
            <div class="col-md-6">
                <span class="badge bg-primary mb-2">OFFICIAL MOBILE APP v1.0.0</span>
                <h3 class="fw-bold text-dark mb-2">Banking in Your Pocket - SK Bank Android App</h3>
                <p class="text-muted mb-0">Get zero-fee instant UPI payments, biometric login, card lock controls, and real-time passbook statements. Download or update to the latest version directly from our secure website.</p>
            </div>
            <div class="col-md-3 text-md-end text-center">
                <a href="download-app.jsp" class="btn btn-sk-primary btn-lg fw-bold w-100 py-3">
                    <i class="fa-solid fa-download me-2"></i> Download / Update App
                </a>
            </div>
        </div>
    </div>
</section>

<!-- Features Grid -->
<section class="py-5 container">
    <div class="text-center mb-5">
        <h2 class="fw-bold">Why Choose SK Bank of India?</h2>
        <p class="text-muted">Built with double-entry accounting integrity, 256-bit encryption, and instant settlement.</p>
    </div>

    <div class="row g-4">
        <div class="col-md-4">
            <div class="sk-card h-100">
                <div class="fs-1 text-primary mb-3"><i class="fa-solid fa-bolt"></i></div>
                <h5 class="fw-bold">Instant Money Transfer</h5>
                <p class="text-muted small">Perform zero-cost instant UPI, IMPS, NEFT, and RTGS money transfers 24x7 with complete transaction receipts.</p>
            </div>
        </div>
        <div class="col-md-4">
            <div class="sk-card h-100">
                <div class="fs-1 text-success mb-3"><i class="fa-solid fa-vault"></i></div>
                <h5 class="fw-bold">High Interest Deposits</h5>
                <p class="text-muted small">Earn up to 7.50% p.a. interest on Fixed Deposits and Recurring Deposits with flexible tenure options.</p>
            </div>
        </div>
        <div class="col-md-4">
            <div class="sk-card h-100">
                <div class="fs-1 text-warning mb-3"><i class="fa-solid fa-shield-virus"></i></div>
                <h5 class="fw-bold">Bank-Grade Security</h5>
                <p class="text-muted small">Protected by multi-factor authentication, BCrypt encryption, JWT session management, and automated fraud alerts.</p>
            </div>
        </div>
    </div>
</section>

<%@ include file="/WEB-INF/views/footer.jspf" %>

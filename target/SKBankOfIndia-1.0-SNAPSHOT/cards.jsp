<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="container py-4">
    <h2 class="fw-bold mb-4">Debit & Credit Cards</h2>

    <div class="row g-4">
        <!-- Platinum Debit Card -->
        <div class="col-md-6 col-lg-4">
            <div class="sk-credit-card mb-3">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <span class="fw-bold">SK BANK OF INDIA</span>
                    <span class="badge bg-warning text-dark">DEBIT</span>
                </div>
                <div class="chip"></div>
                <div class="fs-4 fw-bold tracking-wider mb-2">4532 9012 3456 9812</div>
                <div class="d-flex justify-content-between align-items-end mt-4">
                    <div>
                        <div class="small text-white-50">CARD HOLDER</div>
                        <div class="fw-bold">PRIMARY CUSTOMER</div>
                    </div>
                    <div>
                        <div class="small text-white-50">EXPIRES</div>
                        <div class="fw-bold">12/28</div>
                    </div>
                </div>
            </div>

            <div class="sk-card">
                <h6 class="fw-bold mb-3">Card Controls</h6>
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" id="cardLockToggle" checked>
                    <label class="form-check-label fw-semibold" for="cardLockToggle">Card Active (Unlocked)</label>
                </div>
                <div class="form-check form-switch mb-3">
                    <input class="form-check-input" type="checkbox" id="cardIntlToggle">
                    <label class="form-check-label" for="cardIntlToggle">International Transactions</label>
                </div>
                <button class="btn btn-sm btn-outline-danger w-100" onclick="alert('Card PIN reset link sent via SMS')">
                    <i class="fa-solid fa-key me-1"></i> Reset Card PIN
                </button>
            </div>
        </div>

        <!-- Virtual Card -->
        <div class="col-md-6 col-lg-4">
            <div class="sk-card text-center h-100 d-flex flex-column justify-content-center py-5">
                <div class="fs-1 text-primary mb-3"><i class="fa-solid fa-mobile-screen-button"></i></div>
                <h5 class="fw-bold">Instant Virtual Card</h5>
                <p class="text-muted small">Generate a single-use or recurring virtual card for safe online shopping without revealing physical card numbers.</p>
                <div>
                    <button class="btn btn-sk-primary" onclick="alert('Virtual Debit Card Generated: 4912 •••• •••• 7721')">
                        <i class="fa-solid fa-wand-magic-sparkles me-1"></i> Issue Virtual Card
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>

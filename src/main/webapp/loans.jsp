<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="container py-4">
    <h2 class="fw-bold mb-4">Loan Products & Instant EMI Calculator</h2>

    <div class="row g-4">
        <!-- EMI Calculator -->
        <div class="col-lg-6">
            <div class="sk-card">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-calculator me-2 text-primary"></i> Loan EMI Calculator</h5>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Loan Amount (₹): <span id="lblAmount" class="text-primary fw-bold">₹ 5,000,00</span></label>
                    <input type="range" class="form-range" id="rngAmount" min="50000" max="5000000" step="10000" value="500000" oninput="calculateEmi()">
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Interest Rate (% p.a.): <span id="lblRate" class="text-primary fw-bold">10.5 %</span></label>
                    <input type="range" class="form-range" id="rngRate" min="7" max="18" step="0.25" value="10.5" oninput="calculateEmi()">
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Tenure (Months): <span id="lblTenure" class="text-primary fw-bold">36 Months</span></label>
                    <input type="range" class="form-range" id="rngTenure" min="6" max="240" step="6" value="36" oninput="calculateEmi()">
                </div>

                <div class="p-3 bg-light rounded-3 text-center my-4">
                    <div class="small text-muted text-uppercase fw-bold">Estimated Monthly EMI</div>
                    <div class="fs-2 fw-bold text-success" id="resEmi">₹ 16,253 / month</div>
                </div>

                <button class="btn btn-sk-primary w-100" onclick="alert('Loan Application Form opened. Instant Pre-approval active!')">
                    <i class="fa-solid fa-paper-plane me-1"></i> Apply for Pre-Approved Loan
                </button>
            </div>
        </div>

        <!-- Available Loan Schemes -->
        <div class="col-lg-6">
            <div class="sk-card mb-3">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="fw-bold mb-1">Personal Loan</h6>
                        <p class="small text-muted mb-0">Collateral-free instant loan up to ₹ 10 Lakhs @ 10.5% p.a.</p>
                    </div>
                    <span class="badge bg-success">INSTANT APPROVAL</span>
                </div>
            </div>

            <div class="sk-card mb-3">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="fw-bold mb-1">Home Loan</h6>
                        <p class="small text-muted mb-0">Lowest interest rates starting @ 8.40% p.a. with up to 30 years tenure.</p>
                    </div>
                    <span class="badge bg-primary">LOWEST EMI</span>
                </div>
            </div>

            <div class="sk-card">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="fw-bold mb-1">Vehicle Loan</h6>
                        <p class="small text-muted mb-0">100% on-road financing for 2-wheelers & 4-wheelers @ 8.90% p.a.</p>
                    </div>
                    <span class="badge bg-warning text-dark">100% FINANCING</span>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
function calculateEmi() {
    const P = parseFloat(document.getElementById('rngAmount').value);
    const r = parseFloat(document.getElementById('rngRate').value) / 12 / 100;
    const n = parseInt(document.getElementById('rngTenure').value);

    document.getElementById('lblAmount').innerText = "₹ " + P.toLocaleString('en-IN');
    document.getElementById('lblRate').innerText = document.getElementById('rngRate').value + " %";
    document.getElementById('lblTenure').innerText = n + " Months";

    const emi = (P * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
    document.getElementById('resEmi').innerText = "₹ " + Math.round(emi).toLocaleString('en-IN') + " / month";
}
</script>

<%@ include file="/WEB-INF/views/footer.jspf" %>

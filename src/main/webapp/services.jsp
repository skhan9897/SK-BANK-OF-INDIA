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

        <!-- Generate Transaction PIN -->
        <div class="col-md-6 col-lg-6">
            <div class="sk-card border-top border-4 border-warning">
                <div class="fs-1 text-warning mb-2"><i class="fa-solid fa-key"></i></div>
                <h5 class="fw-bold">Generate Transaction PIN</h5>
                <p class="small text-muted">Set or reset your 4-digit transaction PIN for instant secure payments and transfers.</p>
                <div class="mb-3">
                    <input type="password" id="txtNewPin" class="form-control" placeholder="Enter 4-digit New PIN" maxlength="6" required>
                </div>
                <button class="btn btn-warning w-100 fw-bold" onclick="handleGeneratePin()"><i class="fa-solid fa-lock me-1"></i> Generate Transaction PIN</button>
            </div>
        </div>

        <!-- My Payment QR Code -->
        <div class="col-md-6 col-lg-6">
            <div class="sk-card border-top border-4 border-primary">
                <div class="fs-1 text-primary mb-2"><i class="fa-solid fa-qrcode"></i></div>
                <h5 class="fw-bold">My Payment QR Code</h5>
                <p class="small text-muted">Display your official SK Bank UPI payment VPA and share QR Code for instant credits.</p>
                <div class="mb-3 p-2 bg-light text-center rounded border">
                    <div id="webQrVpa" class="fw-bold text-primary mb-1">customer@skbank</div>
                    <img id="webQrImg" src="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=upi://pay?pa=customer@skbank&pn=SK%20Customer" alt="Payment QR" class="img-fluid my-2 border p-2 bg-white">
                </div>
                <button class="btn btn-sk-primary w-100" onclick="loadWebPaymentQr()"><i class="fa-solid fa-rotate me-1"></i> Refresh Payment QR Code</button>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', () => {
    checkAuthAndRedirect();
    loadWebPaymentQr();
});

async function handleGeneratePin() {
    const pin = document.getElementById('txtNewPin').value.trim();
    if (!pin || pin.length < 4) {
        alert("Please enter a valid 4 to 6 digit transaction PIN.");
        return;
    }

    showLoadingOverlay("Updating Transaction PIN...");

    try {
        const res = await fetch('${pageContext.request.contextPath}/api/v1/protected/accounts/pin/generate', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ pin: pin })
        });

        hideLoadingOverlay();
        const data = await res.json();
        if (data.success) {
            alert("Transaction PIN Generated & Saved Successfully!");
            document.getElementById('txtNewPin').value = "";
        } else {
            alert(data.message);
        }
    } catch (e) {
        hideLoadingOverlay();
        alert("Failed to generate PIN.");
    }
}

async function loadWebPaymentQr() {
    try {
        const res = await fetch('${pageContext.request.contextPath}/api/v1/protected/upi/qr/me', {
            headers: getAuthHeaders()
        });
        const data = await res.json();
        if (data.success && data.data) {
            document.getElementById('webQrVpa').innerText = data.data.vpa;
            const qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + encodeURIComponent(data.data.qrPayload);
            document.getElementById('webQrImg').src = qrUrl;
        }
    } catch (e) {
        console.error(e);
    }
}
</script>

<%@ include file="/WEB-INF/views/footer.jspf" %>

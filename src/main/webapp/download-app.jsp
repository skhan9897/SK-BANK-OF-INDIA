<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/header.jspf" %>

<section class="py-5 bg-navy text-white text-center" style="background: linear-gradient(135deg, #0A192F 0%, #0052CC 100%);">
    <div class="container">
        <span class="badge bg-warning text-dark px-3 py-2 mb-3 fw-bold rounded-pill">
            <i class="fa-brands fa-android me-1"></i> OFFICIAL ANDROID MOBILE APP
        </span>
        <h1 class="fw-bold mb-3">Download &amp; Update SK Bank Mobile App</h1>
        <p class="lead text-white-50 max-w-600 mx-auto">Get instant access to zero-fee UPI transfers, biometric authentication, card locks, and real-time account statements on your Android phone.</p>

        <div class="mt-4">
            <a href="${pageContext.request.contextPath}/downloads/SKBankOfIndia.apk" download="SKBankOfIndia.apk" type="application/vnd.android.package-archive" class="btn btn-sk-accent btn-lg px-4 py-3 fs-5 me-3 fw-bold shadow-lg">
                <i class="fa-solid fa-download me-2"></i> Download Official APK (v1.0.0 - 18.5 MB)
            </a>
            <button onclick="checkAppUpdate()" class="btn btn-outline-light btn-lg px-4 py-3 fs-5">
                <i class="fa-solid fa-rotate me-2"></i> Check Latest Version
            </button>
        </div>
    </div>
</section>

<div class="container py-5">
    <div class="row g-4 align-items-center">
        <!-- App Details Card -->
        <div class="col-lg-7">
            <div class="sk-card">
                <h3 class="fw-bold mb-3"><i class="fa-brands fa-android text-success me-2"></i> Official App Release Information (v1.0.0)</h3>

                <table class="table table-borderless">
                    <tbody>
                        <tr>
                            <td class="fw-semibold text-muted" style="width: 180px;">App Name:</td>
                            <td class="fw-bold">SK Bank of India - Mobile Banking</td>
                        </tr>
                        <tr>
                            <td class="fw-semibold text-muted">Latest Version:</td>
                            <td><span class="badge bg-primary fs-6">v1.0.0 (Build 1 - Official)</span></td>
                        </tr>
                        <tr>
                            <td class="fw-semibold text-muted">Package Name:</td>
                            <td><code>com.bank.skbankofindia</code></td>
                        </tr>
                        <tr>
                            <td class="fw-semibold text-muted">File Name:</td>
                            <td><code class="text-success fw-bold">SKBankOfIndia.apk</code></td>
                        </tr>
                        <tr>
                            <td class="fw-semibold text-muted">Requires Android:</td>
                            <td>Android 7.0 (API level 24) or higher</td>
                        </tr>
                        <tr>
                            <td class="fw-semibold text-muted">Release Date:</td>
                            <td>November 25, 2025</td>
                        </tr>
                    </tbody>
                </table>

                <hr class="my-4">

                <h5 class="fw-bold mb-3"><i class="fa-solid fa-sparkles text-warning me-2"></i> What's New in v1.0.0</h5>
                <ul class="list-unstyled mb-0">
                    <li class="mb-2"><i class="fa-solid fa-circle-check text-success me-2"></i> <strong>Instant UPI Money Transfer:</strong> Pay via VPA or QR code scanning 24x7.</li>
                    <li class="mb-2"><i class="fa-solid fa-circle-check text-success me-2"></i> <strong>Double-Entry Ledger Statements:</strong> Real-time passbook &amp; transaction history updates.</li>
                    <li class="mb-2"><i class="fa-solid fa-circle-check text-success me-2"></i> <strong>Biometric Authentication:</strong> Fast &amp; secure Fingerprint / Face Unlock login.</li>
                    <li class="mb-2"><i class="fa-solid fa-circle-check text-success me-2"></i> <strong>Card Controls:</strong> Instant lock/unlock and international usage toggles.</li>
                </ul>
            </div>
        </div>

        <!-- Installation / Update Guide -->
        <div class="col-lg-5">
            <div class="sk-card bg-light">
                <h5 class="fw-bold mb-3"><i class="fa-solid fa-circle-info me-2 text-primary"></i> How to Update / Install APK</h5>

                <ol class="ps-3 mb-4">
                    <li class="mb-3">
                        <strong>Download APK:</strong> Click the <span class="text-primary fw-bold">Download Official APK</span> button above to receive <code>SKBankOfIndia.apk</code>.
                    </li>
                    <li class="mb-3">
                        <strong>Allow Unknown Sources:</strong> Go to Phone Settings &gt; Security &gt; Enable <em>"Install Unknown Apps"</em> for your browser.
                    </li>
                    <li class="mb-3">
                        <strong>Install/Update:</strong> Open <code>SKBankOfIndia.apk</code> in your downloads folder and tap <strong>Install / Update</strong>.
                    </li>
                    <li>
                        <strong>Login:</strong> Launch SK Bank of India and login with your existing Internet Banking credentials.
                    </li>
                </ol>

                <div class="alert alert-info mb-0 small">
                    <i class="fa-solid fa-shield-halved me-1"></i> <strong>Security Guarantee:</strong> This official APK is cryptographically signed by SK Bank of India and safe to install on all Android devices.
                </div>
            </div>
        </div>
    </div>
</div>

<script>
async function checkAppUpdate() {
    try {
        const res = await fetch('${pageContext.request.contextPath}/api/v1/app/version');
        const data = await res.json();
        if (data.success) {
            alert("App Status: You have the latest version (v" + data.data.latestVersionName + ").\n\nRelease Notes: " + data.data.releaseNotes);
        } else {
            alert("Latest App Version: v1.0.0 available for download.");
        }
    } catch (e) {
        alert("App version: v1.0.0 (Latest version)");
    }
}
</script>

<%@ include file="/WEB-INF/views/footer.jspf" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/header.jspf" %>

<section class="py-5 bg-navy text-white text-center" style="background: linear-gradient(135deg, #0A192F 0%, #0052CC 100%);">
    <div class="container">
        <span class="badge bg-warning text-dark px-3 py-2 mb-3 fw-bold rounded-pill">
            <i class="fa-brands fa-android me-1"></i> OFFICIAL MOBILE BANKING INSTALLATION
        </span>
        <h1 class="fw-bold mb-3">Install &amp; Update SK Bank Mobile App</h1>
        <p class="lead text-white-50 max-w-600 mx-auto">Choose your preferred installation method: Instant 1-Click Home Screen App or Direct Android APK Package.</p>

        <div class="mt-4 d-flex flex-wrap justify-content-center gap-3">
            <!-- 1-Click Instant Web App Install -->
            <button id="btnPwaInstall" onclick="triggerPwaInstall()" class="btn btn-sk-accent btn-lg px-4 py-3 fs-5 fw-bold shadow-lg">
                <i class="fa-solid fa-mobile-screen-button me-2"></i> 1-Click Instant App Install
            </button>

            <!-- Direct APK Package Download -->
            <a href="${pageContext.request.contextPath}/downloads/SKBankOfIndia.apk" download="SKBankOfIndia.apk" type="application/vnd.android.package-archive" class="btn btn-outline-light btn-lg px-4 py-3 fs-5 fw-bold">
                <i class="fa-solid fa-download me-2"></i> Download APK Package (v1.0.0)
            </a>
        </div>
    </div>
</section>

<div class="container py-5">
    <div class="row g-4">
        <!-- Option 1: PWA Instant Installation Guide -->
        <div class="col-lg-6">
            <div class="sk-card h-100 border-start border-4 border-warning">
                <h4 class="fw-bold text-dark mb-3"><i class="fa-solid fa-bolt text-warning me-2"></i> Method 1: Instant Web App (Recommended)</h4>
                <p class="text-muted small">No security warnings, no file downloads required! Adds official SK Bank app directly to your phone's home screen.</p>

                <ol class="ps-3 mb-4">
                    <li class="mb-2">Click <strong>"1-Click Instant App Install"</strong> button above.</li>
                    <li class="mb-2">Tap <strong>"Install / Add to Home Screen"</strong> when Chrome prompts.</li>
                    <li class="mb-2">Open <strong>SK Bank of India</strong> icon directly from your phone app drawer.</li>
                </ol>

                <button onclick="triggerPwaInstall()" class="btn btn-sk-primary w-100 py-2">
                    <i class="fa-solid fa-plus-circle me-1"></i> Add SK Bank to Phone Home Screen
                </button>
            </div>
        </div>

        <!-- Option 2: APK Installation Guide -->
        <div class="col-lg-6">
            <div class="sk-card h-100 border-start border-4 border-primary">
                <h4 class="fw-bold text-dark mb-3"><i class="fa-brands fa-android text-success me-2"></i> Method 2: Android APK Package</h4>
                <p class="text-muted small">For installing full standalone Android APK binary file.</p>

                <ol class="ps-3 mb-4">
                    <li class="mb-2">Click <strong>"Download APK Package"</strong> to download <code>SKBankOfIndia.apk</code>.</li>
                    <li class="mb-2">If Chrome shows <em>"File might be harmful"</em>, tap <strong>"Download Anyway"</strong>.</li>
                    <li class="mb-2">Tap <strong>Open</strong> &gt; Enable <em>"Allow from this source"</em> in Phone Settings if prompted.</li>
                    <li class="mb-2">Tap <strong>Install</strong> to complete installation.</li>
                </ol>

                <a href="${pageContext.request.contextPath}/downloads/SKBankOfIndia.apk" download="SKBankOfIndia.apk" type="application/vnd.android.package-archive" class="btn btn-outline-primary w-100 py-2">
                    <i class="fa-solid fa-file-arrow-down me-1"></i> Download SKBankOfIndia.apk
                </a>
            </div>
        </div>
    </div>
</div>

<script>
let deferredPrompt;

window.addEventListener('beforeinstallprompt', (e) => {
    e.preventDefault();
    deferredPrompt = e;
    const btn = document.getElementById('btnPwaInstall');
    if (btn) btn.classList.add('pulse-glow');
});

function triggerPwaInstall() {
    if (deferredPrompt) {
        deferredPrompt.prompt();
        deferredPrompt.userChoice.then((choiceResult) => {
            if (choiceResult.outcome === 'accepted') {
                alert('SK Bank Mobile App successfully added to Home Screen!');
            }
            deferredPrompt = null;
        });
    } else {
        alert('To Install: Tap your browser menu (3 dots) and select "Add to Home screen" or "Install app".');
    }
}
</script>

<%@ include file="/WEB-INF/views/footer.jspf" %>

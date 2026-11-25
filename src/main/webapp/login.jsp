<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="sk-card">
                <div class="text-center mb-4">
                    <div class="fs-1 text-primary"><i class="fa-solid fa-lock"></i></div>
                    <h3 class="fw-bold">Secure Login</h3>
                    <p class="text-muted small">Access Internet Banking & Account Dashboard</p>
                </div>

                <div id="loginAlert" class="alert alert-danger d-none"></div>

                <form id="loginForm" onsubmit="event.preventDefault(); handleLoginSubmit();">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Username / Email / Mobile</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fa-solid fa-user"></i></span>
                            <input type="text" id="loginIdentifier" class="form-control" placeholder="Enter username or email" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Password</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fa-solid fa-key"></i></span>
                            <input type="password" id="loginPassword" class="form-control" placeholder="Enter your password" required>
                        </div>
                    </div>

                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="rememberMe">
                            <label class="form-check-label small" for="rememberMe">Remember Username</label>
                        </div>
                        <a href="#" class="small text-decoration-none" onclick="showForgotPasswordModal(); return false;">Forgot Password?</a>
                    </div>

                    <button type="submit" id="btnLoginSubmit" class="btn btn-sk-primary w-100 py-2 fs-6">
                        <i class="fa-solid fa-right-to-bracket me-2"></i> Login to Banking Portal
                    </button>
                </form>

                <hr class="my-4">
                <div class="text-center">
                    <span class="text-muted small">Don't have an account? </span>
                    <a href="register.jsp" class="fw-bold text-decoration-none">Register Now</a>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
async function handleLoginSubmit() {
    const identifier = document.getElementById('loginIdentifier').value.trim();
    const password = document.getElementById('loginPassword').value;
    const alertDiv = document.getElementById('loginAlert');
    alertDiv.classList.add('d-none');

    try {
        const response = await fetch('/SKBankOfIndia/api/v1/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ identifier, password })
        });

        const res = await response.json();
        if (res.success) {
            setToken(res.data.token);
            localStorage.setItem('sk_user_data', JSON.stringify(res.data));
            window.location.href = 'dashboard.jsp';
        } else {
            alertDiv.innerText = res.message;
            alertDiv.classList.remove('d-none');
        }
    } catch (err) {
        alertDiv.innerText = "Network connection failed. Please check backend service.";
        alertDiv.classList.remove('d-none');
    }
}
</script>

<%@ include file="/WEB-INF/views/footer.jspf" %>

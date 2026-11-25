/* ==============================================================================
   SK BANK OF INDIA - CLIENT APPLICATION ENGINE
   ============================================================================== */

const API_BASE_URL = '/SKBankOfIndia/api/v1';

function getToken() {
    return localStorage.getItem('sk_auth_token');
}

function setToken(token) {
    localStorage.setItem('sk_auth_token', token);
}

function removeToken() {
    localStorage.removeItem('sk_auth_token');
    localStorage.removeItem('sk_user_data');
}

function getAuthHeaders() {
    const token = getToken();
    const headers = { 'Content-Type': 'application/json' };
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }
    return headers;
}

function checkAuthAndRedirect() {
    const token = getToken();
    if (!token) {
        window.location.href = 'login.jsp';
    }
}

function logout() {
    removeToken();
    window.location.href = 'login.jsp';
}

function showToast(message, isError = false) {
    alert((isError ? "Error: " : "Success: ") + message);
}

// Balance Mask / Unmask Toggle
function toggleBalanceVisibility(elementId) {
    const el = document.getElementById(elementId);
    if (!el) return;
    if (el.dataset.hidden === "true") {
        el.innerText = el.dataset.realValue;
        el.dataset.hidden = "false";
    } else {
        el.dataset.realValue = el.innerText;
        el.innerText = "••••••••";
        el.dataset.hidden = "true";
    }
}

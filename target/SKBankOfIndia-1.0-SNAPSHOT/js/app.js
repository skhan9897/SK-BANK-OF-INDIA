/* ==============================================================================
   SK BANK OF INDIA - CLIENT APPLICATION ENGINE
   Includes Professional Loading Overlay & Real-Time Voice Feedback
   ============================================================================== */

function getApiBaseUrl() {
    const path = window.location.pathname;
    if (path.startsWith('/SKBankOfIndia')) {
        return '/SKBankOfIndia/api/v1';
    }
    return '/api/v1';
}

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

/* ==============================================================================
   PROFESSIONAL LOADING OVERLAY UI
   ============================================================================== */
function showLoadingOverlay(message = "Processing Transaction...") {
    let overlay = document.getElementById('skLoadingOverlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'skLoadingOverlay';
        overlay.className = 'sk-loading-overlay';
        overlay.innerHTML = `
            <div class="tricolor-spinner mb-3"></div>
            <h4 class="fw-bold text-white mb-2" id="skLoadingTitle">Processing Transaction</h4>
            <p class="text-warning small mb-0" id="skLoadingSub"><i class="fa-solid fa-lock me-1"></i> Securing Double-Entry Ledger Entry...</p>
        `;
        document.body.appendChild(overlay);
    } else {
        document.getElementById('skLoadingTitle').innerText = message;
        overlay.style.display = 'flex';
    }

    // Safety timeout: Auto-hide after 5 seconds max if backend responds slowly
    setTimeout(() => {
        hideLoadingOverlay();
    }, 5000);
}

function hideLoadingOverlay() {
    const overlay = document.getElementById('skLoadingOverlay');
    if (overlay) {
        overlay.style.display = 'none';
    }
    const tricolorOverlay = document.getElementById('skTricolorLoadingOverlay');
    if (tricolorOverlay) {
        tricolorOverlay.style.display = 'none';
    }
}

/* ==============================================================================
   TEXT-TO-SPEECH (TTS) REAL-TIME VOICE ANNOUNCEMENT ENGINE
   ============================================================================== */
function speakPaymentVoiceNotification(amount, type = 'SENT', payee = '') {
    if (!('speechSynthesis' in window)) {
        console.warn('Speech synthesis not supported on this browser.');
        return;
    }

    // Cancel any ongoing speech
    window.speechSynthesis.cancel();

    let textToSpeak = "";
    const formattedAmount = Number(amount).toLocaleString('en-IN');

    if (type === 'SENT') {
        textToSpeak = `SK Bank of India: Payment of ${formattedAmount} Rupees successfully sent!`;
    } else if (type === 'RECEIVED') {
        textToSpeak = `SK Bank of India: Payment of ${formattedAmount} Rupees successfully received in your account!`;
    } else {
        textToSpeak = `SK Bank of India: Transaction of ${formattedAmount} Rupees completed successfully!`;
    }

    const utterance = new SpeechSynthesisUtterance(textToSpeak);
    utterance.rate = 0.95; // Natural speed
    utterance.pitch = 1.0;
    utterance.volume = 1.0;

    // Try finding an English or Hindi voice
    const voices = window.speechSynthesis.getVoices();
    const preferredVoice = voices.find(v => v.lang.includes('en-IN') || v.lang.includes('hi-IN') || v.lang.includes('en-US'));
    if (preferredVoice) {
        utterance.voice = preferredVoice;
    }

    window.speechSynthesis.speak(utterance);
}

// Pre-load voices for browser compatibility
if ('speechSynthesis' in window) {
    window.speechSynthesis.onvoiceschanged = () => {
        window.speechSynthesis.getVoices();
    };
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

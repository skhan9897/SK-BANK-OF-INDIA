<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="tricolor-wave-bg py-5">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="sk-card shadow-lg border-0">

                    <!-- Title Header -->
                    <div class="text-center mb-4">
                        <span class="badge bg-warning text-dark px-3 py-2 fw-bold rounded-pill mb-2">
                            <i class="fa-solid fa-shield-halved me-1"></i> RBI REGULATED DIGITAL ONBOARDING
                        </span>
                        <h2 class="fw-bold text-dark">Open Savings Account</h2>
                        <p class="text-muted small mb-0">Instant Account Number &amp; Digital Passbook Generation</p>
                    </div>

                    <!-- Step Wizard Indicator Dots -->
                    <div class="step-wizard-indicator px-4">
                        <div class="step-dot active" id="dotStep1">1</div>
                        <div class="step-dot" id="dotStep2">2</div>
                        <div class="step-dot" id="dotStep3">3</div>
                        <div class="step-dot" id="dotStep4">4</div>
                    </div>

                    <div id="registerAlert" class="alert alert-danger d-none"></div>

                    <form id="wizardForm">
                        <!-- ================= STEP 1: PERSONAL INFORMATION ================= -->
                        <div id="step1" class="wizard-step">
                            <h5 class="fw-bold mb-3 text-primary"><i class="fa-solid fa-user me-2"></i> Step 1: Personal Details</h5>
                            <div class="row g-3">
                                <div class="col-md-4">
                                    <label class="form-label fw-semibold">First Name *</label>
                                    <input type="text" id="regFirstName" class="form-control form-control-lg fs-6" placeholder="John" required>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-semibold">Middle Name</label>
                                    <input type="text" id="regMiddleName" class="form-control form-control-lg fs-6" placeholder="M.">
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-semibold">Last Name *</label>
                                    <input type="text" id="regLastName" class="form-control form-control-lg fs-6" placeholder="Doe" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Date of Birth *</label>
                                    <input type="date" id="regDob" class="form-control form-control-lg fs-6" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Gender *</label>
                                    <select id="regGender" class="form-select form-select-lg fs-6">
                                        <option value="MALE">Male</option>
                                        <option value="FEMALE">Female</option>
                                        <option value="OTHER">Other</option>
                                    </select>
                                </div>
                            </div>
                            <div class="d-flex justify-content-end mt-4">
                                <button type="button" class="btn btn-sk-primary px-4" onclick="goToStep(2)">
                                    Next: Account Credentials <i class="fa-solid fa-arrow-right ms-2"></i>
                                </button>
                            </div>
                        </div>

                        <!-- ================= STEP 2: CREDENTIALS & CONTACT ================= -->
                        <div id="step2" class="wizard-step d-none">
                            <h5 class="fw-bold mb-3 text-primary"><i class="fa-solid fa-key me-2"></i> Step 2: Credentials &amp; Contact Details</h5>
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Username *</label>
                                    <input type="text" id="regUsername" class="form-control form-control-lg fs-6" placeholder="johndoe99" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Email Address *</label>
                                    <input type="email" id="regEmail" class="form-control form-control-lg fs-6" placeholder="john@example.com" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Mobile Number *</label>
                                    <input type="tel" id="regPhone" class="form-control form-control-lg fs-6" placeholder="9876543210" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Password *</label>
                                    <input type="password" id="regPassword" class="form-control form-control-lg fs-6" placeholder="Min 8 characters" required>
                                </div>
                            </div>
                            <div class="d-flex justify-content-between mt-4">
                                <button type="button" class="btn btn-outline-secondary px-4" onclick="goToStep(1)">
                                    <i class="fa-solid fa-arrow-left me-2"></i> Back
                                </button>
                                <button type="button" class="btn btn-sk-primary px-4" onclick="goToStep(3)">
                                    Next: Identity Verification <i class="fa-solid fa-arrow-right ms-2"></i>
                                </button>
                            </div>
                        </div>

                        <!-- ================= STEP 3: IDENTITY & ADDRESS ================= -->
                        <div id="step3" class="wizard-step d-none">
                            <h5 class="fw-bold mb-3 text-primary"><i class="fa-solid fa-address-card me-2"></i> Step 3: Identity &amp; Address Verification</h5>
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">PAN Number</label>
                                    <input type="text" id="regPan" class="form-control form-control-lg fs-6" placeholder="ABCDE1234F" style="text-transform:uppercase;">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-semibold">Aadhaar Number *</label>
                                    <input type="text" id="regAadhaar" class="form-control form-control-lg fs-6" placeholder="12-digit Aadhaar" required>
                                </div>
                                <div class="col-md-12">
                                    <label class="form-label fw-semibold">Address Line 1 *</label>
                                    <input type="text" id="regAddress1" class="form-control form-control-lg fs-6" placeholder="Flat No., Building, Street Name" required>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-semibold">City *</label>
                                    <input type="text" id="regCity" class="form-control form-control-lg fs-6" placeholder="Mumbai" required>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-semibold">State *</label>
                                    <input type="text" id="regState" class="form-control form-control-lg fs-6" placeholder="Maharashtra" required>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-semibold">Pincode *</label>
                                    <input type="text" id="regPincode" class="form-control form-control-lg fs-6" placeholder="400001" required>
                                </div>
                            </div>
                            <div class="d-flex justify-content-between mt-4">
                                <button type="button" class="btn btn-outline-secondary px-4" onclick="goToStep(2)">
                                    <i class="fa-solid fa-arrow-left me-2"></i> Back
                                </button>
                                <button type="button" class="btn btn-sk-primary px-4" onclick="sendOtpAndProceed()">
                                    Send Verification OTP <i class="fa-solid fa-paper-plane ms-2"></i>
                                </button>
                            </div>
                        </div>

                        <!-- ================= STEP 4: OTP VERIFICATION & CONFIRMATION ================= -->
                        <div id="step4" class="wizard-step d-none">
                            <h5 class="fw-bold mb-3 text-success"><i class="fa-solid fa-mobile-retro me-2"></i> Step 4: OTP Verification &amp; Account Confirmation</h5>
                            <p class="text-muted small mb-3">Enter the 6-digit OTP code sent to your registered Mobile Number and Email.</p>

                            <div class="row justify-content-center mb-4">
                                <div class="col-md-6">
                                    <div class="input-group input-group-lg">
                                        <span class="input-group-text bg-light"><i class="fa-solid fa-lock"></i></span>
                                        <input type="text" id="regOtpCode" class="form-control text-center fw-bold letter-spacing-2" placeholder="Enter 6-Digit OTP" maxlength="6" required>
                                    </div>
                                    <div class="text-center mt-2">
                                        <button type="button" class="btn btn-link btn-sm text-decoration-none" onclick="sendOtpAndProceed()">Resend OTP Code</button>
                                    </div>
                                </div>
                            </div>

                            <div class="form-check my-3 text-start">
                                <input class="form-check-input" type="checkbox" id="termsCheck" required checked>
                                <label class="form-check-label small text-muted" for="termsCheck">
                                    I declare that all information provided is accurate and I agree to SK Bank of India T&amp;C and RBI Digital Banking Rules.
                                </label>
                            </div>

                            <div class="d-flex justify-content-between mt-4">
                                <button type="button" class="btn btn-outline-secondary px-4" onclick="goToStep(3)">
                                    <i class="fa-solid fa-arrow-left me-2"></i> Back
                                </button>
                                <button type="button" class="btn btn-sk-accent px-5 py-2 fs-6 fw-bold" onclick="handleFinalAccountOpening()">
                                    <i class="fa-solid fa-circle-check me-2"></i> Verify OTP &amp; Open Account
                                </button>
                            </div>
                        </div>

                    </form>

                    <hr class="my-4">
                    <div class="text-center">
                        <span class="text-muted small">Already have an SK Bank account? </span>
                        <a href="login.jsp" class="fw-bold text-decoration-none">Login to Internet Banking</a>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<script>
let currentStep = 1;

function goToStep(stepNumber) {
    if (stepNumber > currentStep) {
        if (!validateStep(currentStep)) return;
    }

    document.querySelectorAll('.wizard-step').forEach(step => step.classList.add('d-none'));
    document.getElementById('step' + stepNumber).classList.remove('d-none');

    // Update Step Indicator Dots
    for (let i = 1; i <= 4; i++) {
        const dot = document.getElementById('dotStep' + i);
        dot.classList.remove('active', 'completed');
        if (i === stepNumber) {
            dot.classList.add('active');
        } else if (i < stepNumber) {
            dot.classList.add('completed');
            dot.innerHTML = '<i class="fa-solid fa-check"></i>';
        } else {
            dot.innerHTML = i;
        }
    }

    currentStep = stepNumber;
}

function validateStep(step) {
    const alertDiv = document.getElementById('registerAlert');
    alertDiv.classList.add('d-none');

    if (step === 1) {
        const fn = document.getElementById('regFirstName').value.trim();
        const ln = document.getElementById('regLastName').value.trim();
        const dob = document.getElementById('regDob').value;
        if (!fn || !ln || !dob) {
            alertDiv.innerText = "Please complete all required fields in Step 1.";
            alertDiv.classList.remove('d-none');
            return false;
        }
    } else if (step === 2) {
        const un = document.getElementById('regUsername').value.trim();
        const em = document.getElementById('regEmail').value.trim();
        const ph = document.getElementById('regPhone').value.trim();
        const pw = document.getElementById('regPassword').value;

        if (!un || !em || !ph || !pw) {
            alertDiv.innerText = "Please complete all required fields in Step 2.";
            alertDiv.classList.remove('d-none');
            return false;
        }
        if (pw.length < 8) {
            alertDiv.innerText = "Password must be at least 8 characters long.";
            alertDiv.classList.remove('d-none');
            return false;
        }
    } else if (step === 3) {
        const aadhaar = document.getElementById('regAadhaar').value.trim();
        const address = document.getElementById('regAddress1').value.trim();
        const city = document.getElementById('regCity').value.trim();

        if (!aadhaar || !address || !city) {
            alertDiv.innerText = "Please enter Aadhaar number and address details.";
            alertDiv.classList.remove('d-none');
            return false;
        }
    }
    return true;
}

async function sendOtpAndProceed() {
    if (!validateStep(3)) return;

    const email = document.getElementById('regEmail').value.trim();
    showTricolorLoadingOverlay("Sending Verification OTP...");

    try {
        const response = await fetch('/SKBankOfIndia/api/v1/auth/send-otp', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ recipient: email, purpose: 'REGISTER' })
        });
        hideLoadingOverlay();
        const data = await response.json();
        if (data.success) {
            goToStep(4);
            alert("Verification OTP sent to " + email + ". (Sandbox Test OTP: " + (data.data ? data.data.otp_sandbox : "123456") + ")");
        } else {
            alert(data.message);
        }
    } catch (e) {
        hideLoadingOverlay();
        goToStep(4); // Fallback to step 4 in sandbox
    }
}

async function handleFinalAccountOpening() {
    const alertDiv = document.getElementById('registerAlert');
    alertDiv.classList.add('d-none');

    const payload = {
        username: document.getElementById('regUsername').value.trim(),
        email: document.getElementById('regEmail').value.trim(),
        phone: document.getElementById('regPhone').value.trim(),
        password: document.getElementById('regPassword').value,
        firstName: document.getElementById('regFirstName').value.trim(),
        middleName: document.getElementById('regMiddleName').value.trim(),
        lastName: document.getElementById('regLastName').value.trim(),
        dob: document.getElementById('regDob').value,
        gender: document.getElementById('regGender').value,
        pan: document.getElementById('regPan').value.trim(),
        aadhaar: document.getElementById('regAadhaar').value.trim(),
        address1: document.getElementById('regAddress1').value.trim(),
        city: document.getElementById('regCity').value.trim(),
        state: document.getElementById('regState').value.trim(),
        pincode: document.getElementById('regPincode').value.trim()
    };

    showTricolorLoadingOverlay("Verifying OTP & Generating Savings Account...");

    try {
        const response = await fetch('/SKBankOfIndia/api/v1/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        hideLoadingOverlay();
        const res = await response.json();

        if (res.success) {
            setToken(res.data.token);

            // Play Speech Announcement!
            if ('speechSynthesis' in window) {
                const utterance = new SpeechSynthesisUtterance("Welcome to SK Bank of India! Your Savings Account has been successfully opened.");
                window.speechSynthesis.speak(utterance);
            }

            alert("Congratulations! Savings Account Successfully Opened.\nAccount Number: " + res.data.account.accountNumber + "\nBranch IFSC: SKBI0000001");
            window.location.href = 'dashboard.jsp';
        } else {
            alertDiv.innerText = res.message;
            alertDiv.classList.remove('d-none');
        }
    } catch (err) {
        hideLoadingOverlay();
        alertDiv.innerText = "Network failure while opening account. Please try again.";
        alertDiv.classList.remove('d-none');
    }
}

function showTricolorLoadingOverlay(msg) {
    let overlay = document.getElementById('skTricolorLoadingOverlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'skTricolorLoadingOverlay';
        overlay.className = 'sk-loading-overlay';
        overlay.innerHTML = `
            <div class="tricolor-spinner mb-3"></div>
            <h4 class="fw-bold text-white mb-2" id="skTricolorTitle">${msg}</h4>
            <p class="text-warning small mb-0"><i class="fa-solid fa-shield-halved me-1"></i> SK Bank Digital Ledger Processing...</p>
        `;
        document.body.appendChild(overlay);
    } else {
        document.getElementById('skTricolorTitle').innerText = msg;
        overlay.style.display = 'flex';
    }
}
</script>

<%@ include file="/WEB-INF/views/footer.jspf" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="sk-card">
                <div class="text-center mb-4">
                    <div class="fs-1 text-primary"><i class="fa-solid fa-user-plus"></i></div>
                    <h3 class="fw-bold">Open Savings Account & Register</h3>
                    <p class="text-muted small">Instant Digital Onboarding with Instant Account Number Generation</p>
                </div>

                <div id="registerAlert" class="alert alert-danger d-none"></div>

                <form id="registerForm" onsubmit="event.preventDefault(); handleRegisterSubmit();">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">First Name *</label>
                            <input type="text" id="regFirstName" class="form-control" placeholder="John" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Middle Name</label>
                            <input type="text" id="regMiddleName" class="form-control" placeholder="M.">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Last Name *</label>
                            <input type="text" id="regLastName" class="form-control" placeholder="Doe" required>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Username *</label>
                            <input type="text" id="regUsername" class="form-control" placeholder="johndoe99" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Email Address *</label>
                            <input type="email" id="regEmail" class="form-control" placeholder="john@example.com" required>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Mobile Number *</label>
                            <input type="tel" id="regPhone" class="form-control" placeholder="9876543210" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Password *</label>
                            <input type="password" id="regPassword" class="form-control" placeholder="Min 8 characters" required>
                        </div>

                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Date of Birth *</label>
                            <input type="date" id="regDob" class="form-control" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Gender *</label>
                            <select id="regGender" class="form-select">
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                                <option value="OTHER">Other</option>
                            </select>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">PAN Number</label>
                            <input type="text" id="regPan" class="form-control" placeholder="ABCDE1234F" style="text-transform:uppercase;">
                        </div>

                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Aadhaar Number *</label>
                            <input type="text" id="regAadhaar" class="form-control" placeholder="12-digit Aadhaar" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Address Line 1 *</label>
                            <input type="text" id="regAddress1" class="form-control" placeholder="Flat No., Street Name" required>
                        </div>

                        <div class="col-md-4">
                            <label class="form-label fw-semibold">City *</label>
                            <input type="text" id="regCity" class="form-control" placeholder="Mumbai" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">State *</label>
                            <input type="text" id="regState" class="form-control" placeholder="Maharashtra" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Pincode *</label>
                            <input type="text" id="regPincode" class="form-control" placeholder="400001" required>
                        </div>
                    </div>

                    <div class="form-check my-4">
                        <input class="form-check-input" type="checkbox" id="termsCheck" required>
                        <label class="form-check-label small" for="termsCheck">
                            I accept SK Bank of India's Terms & Conditions and RBI Banking Guidelines.
                        </label>
                    </div>

                    <button type="submit" class="btn btn-sk-primary w-100 py-2 fs-6">
                        <i class="fa-solid fa-check-circle me-2"></i> Submit & Open Account
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
async function handleRegisterSubmit() {
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

    const alertDiv = document.getElementById('registerAlert');
    alertDiv.classList.add('d-none');

    try {
        const response = await fetch('/SKBankOfIndia/api/v1/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const res = await response.json();
        if (res.success) {
            setToken(res.data.token);
            alert("Registration Successful! Account Number: " + res.data.account.accountNumber);
            window.location.href = 'dashboard.jsp';
        } else {
            alertDiv.innerText = res.message;
            alertDiv.classList.remove('d-none');
        }
    } catch (err) {
        alertDiv.innerText = "Network failure while connecting to SK Bank Backend.";
        alertDiv.classList.remove('d-none');
    }
}
</script>

<%@ include file="/WEB-INF/views/footer.jspf" %>

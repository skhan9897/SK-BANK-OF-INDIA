<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="container py-4">
    <div class="row g-4">
        <!-- Transfer Form -->
        <div class="col-lg-7">
            <div class="sk-card">
                <h4 class="fw-bold mb-3"><i class="fa-solid fa-paper-plane me-2 text-primary"></i> Initiate Instant Fund Transfer</h4>

                <div id="transferAlert" class="alert alert-danger d-none"></div>

                <form id="transferForm" onsubmit="event.preventDefault(); handleFundTransfer();">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">From Account *</label>
                        <select id="transferSourceAccount" class="form-select" required>
                            <option value="">Loading accounts...</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Payment Method *</label>
                        <div class="btn-group w-100" role="group">
                            <input type="radio" class="btn-check" name="payMethod" id="pmInternal" value="INTERNAL" checked>
                            <label class="btn btn-outline-primary" for="pmInternal">Internal Bank</label>

                            <input type="radio" class="btn-check" name="payMethod" id="pmUPI" value="UPI">
                            <label class="btn btn-outline-primary" for="pmUPI">UPI / QR</label>

                            <input type="radio" class="btn-check" name="payMethod" id="pmIMPS" value="IMPS">
                            <label class="btn btn-outline-primary" for="pmIMPS">IMPS Instant</label>

                            <input type="radio" class="btn-check" name="payMethod" id="pmNEFT" value="NEFT">
                            <label class="btn btn-outline-primary" for="pmNEFT">NEFT / RTGS</label>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Destination Account / VPA / Account Number *</label>
                        <input type="text" id="transferDestinationAccount" class="form-control" placeholder="14-digit Account Number or user@skbank" required>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Amount (₹) *</label>
                            <input type="number" id="transferAmount" class="form-control" placeholder="0.00" step="0.01" min="1" required>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-semibold">Transaction PIN *</label>
                            <input type="password" id="transferPin" class="form-control" placeholder="4-digit PIN" maxlength="6" required>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-semibold">Remarks / Purpose</label>
                        <input type="text" id="transferRemarks" class="form-control" placeholder="e.g. Rent, Bill payment, Family support">
                    </div>

                    <button type="submit" id="btnTransferSubmit" class="btn btn-sk-primary w-100 py-2 fs-6">
                        <i class="fa-solid fa-bolt me-2 text-warning"></i> Instant Confirm &amp; Send Money
                    </button>
                </form>
            </div>
        </div>

        <!-- Beneficiary Sidebar -->
        <div class="col-lg-5">
            <div class="sk-card mb-4">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="fw-bold mb-0"><i class="fa-solid fa-users me-2 text-warning"></i> Saved Payees</h5>
                    <button class="btn btn-sm btn-outline-primary" onclick="alert('Add beneficiary modal')">+ Add Payee</button>
                </div>
                <div class="list-group list-group-flush">
                    <a href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center" onclick="populatePayee('10018877665544')">
                        <div>
                            <div class="fw-bold">Ramesh Sharma</div>
                            <div class="small text-muted">Acc: 10018877665544</div>
                        </div>
                        <span class="badge bg-light text-dark">INTERNAL</span>
                    </a>
                    <a href="#" class="list-group-item list-group-item-action d-flex justify-content-between align-items-center" onclick="populatePayee('priya@skbank')">
                        <div>
                            <div class="fw-bold">Priya Patel</div>
                            <div class="small text-muted">VPA: priya@skbank</div>
                        </div>
                        <span class="badge bg-light text-dark">UPI</span>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Success Modal -->
<div class="modal fade" id="paymentSuccessModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content text-center p-4">
            <div class="mb-3">
                <div class="sound-wave-icon d-inline-block p-3 rounded-circle bg-success bg-opacity-10 text-success">
                    <i class="fa-solid fa-circle-check fs-1"></i>
                </div>
            </div>
            <h3 class="fw-bold text-success mb-1">Payment Successful!</h3>
            <p class="text-muted mb-3" id="succTxnRef">Ref: TXN202511250001</p>
            <div class="p-3 bg-light rounded-3 mb-4 text-start">
                <div class="d-flex justify-content-between mb-2">
                    <span class="text-muted">Amount Paid:</span>
                    <strong class="fs-5 text-dark" id="succAmount">₹ 0.00</strong>
                </div>
                <div class="d-flex justify-content-between">
                    <span class="text-muted">Paid To:</span>
                    <strong id="succPayee">Account Number</strong>
                </div>
            </div>
            <button class="btn btn-sk-primary w-100" data-bs-dismiss="modal">Done &amp; Close</button>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', () => {
    checkAuthAndRedirect();
    loadSourceAccounts();
});

async function loadSourceAccounts() {
    try {
        const res = await fetch('${pageContext.request.contextPath}/api/v1/protected/accounts', {
            headers: getAuthHeaders()
        });
        const data = await res.json();
        if (data.success && data.data) {
            const sel = document.getElementById('transferSourceAccount');
            sel.innerHTML = '';
            data.data.forEach(acc => {
                const opt = document.createElement('option');
                opt.value = acc.accountId;
                opt.innerText = acc.accountNumber + " (Bal: ₹ " + Number(acc.availableBalance).toLocaleString('en-IN') + ")";
                sel.appendChild(opt);
            });
        }
    } catch (e) {
        console.error(e);
    }
}

function populatePayee(accountNo) {
    document.getElementById('transferDestinationAccount').value = accountNo;
}

async function handleFundTransfer() {
    const sourceAccountId = document.getElementById('transferSourceAccount').value;
    const destinationAccount = document.getElementById('transferDestinationAccount').value.trim();
    const amount = document.getElementById('transferAmount').value;
    const remarks = document.getElementById('transferRemarks').value.trim();
    const alertDiv = document.getElementById('transferAlert');
    alertDiv.classList.add('d-none');

    let method = "INTERNAL";
    const selectedMethod = document.querySelector('input[name="payMethod"]:checked');
    if (selectedMethod) method = selectedMethod.value;

    showLoadingOverlay("Executing Ultra-Fast Payment...");

    try {
        const payload = {
            sourceAccountId: sourceAccountId,
            destinationAccountId: 1, // Default receiver in sandbox
            amount: amount,
            paymentMethod: method,
            remarks: remarks || "Instant Transfer"
        };

        const response = await fetch('${pageContext.request.contextPath}/api/v1/protected/transfers/internal', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });

        hideLoadingOverlay();
        const data = await response.json();

        if (data.success) {
            // Trigger Voice Announcement!
            speakPaymentVoiceNotification(amount, 'SENT', destinationAccount);

            document.getElementById('succTxnRef').innerText = "Txn Ref: " + (data.data.transactionRef || "TXN_SUCCESS");
            document.getElementById('succAmount').innerText = "₹ " + Number(amount).toLocaleString('en-IN', { minimumFractionDigits: 2 });
            document.getElementById('succPayee').innerText = destinationAccount;

            const modal = new bootstrap.Modal(document.getElementById('paymentSuccessModal'));
            modal.show();

            document.getElementById('transferForm').reset();
            loadSourceAccounts();
        } else {
            alertDiv.innerText = data.message;
            alertDiv.classList.remove('d-none');
        }
    } catch (e) {
        hideLoadingOverlay();
        alertDiv.innerText = "Network connection timeout during transaction.";
        alertDiv.classList.remove('d-none');
    }
}
</script>

<%@ include file="/WEB-INF/views/footer.jspf" %>

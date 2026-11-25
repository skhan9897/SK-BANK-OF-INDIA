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

<!-- Digital Bank Payment Receipt Modal -->
<div class="modal fade" id="paymentSuccessModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content shadow-lg border-0" id="receiptPrintArea">

            <!-- Receipt Header -->
            <div class="modal-header bg-navy text-white p-4" style="background: #0A192F;">
                <div>
                    <h4 class="fw-bold text-warning mb-1"><i class="fa-solid fa-building-columns me-2"></i>SK Bank of India</h4>
                    <p class="small text-white-50 mb-0">Official Electronic Banking Transaction Receipt</p>
                </div>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>

            <!-- Receipt Body -->
            <div class="modal-body p-4 text-start">

                <div class="text-center mb-4">
                    <span class="badge bg-success bg-opacity-10 text-success px-4 py-2 fs-6 rounded-pill fw-bold border border-success">
                        <i class="fa-solid fa-circle-check me-1"></i> PAYMENT COMPLETED &amp; LEDGER VERIFIED
                    </span>
                    <h2 class="fw-bold text-dark mt-3 mb-1" id="succAmount">₹ 0.00</h2>
                    <p class="text-muted small" id="succTxnRef">Txn Ref: TXN202511250001</p>
                </div>

                <div class="table-responsive bg-light p-3 rounded-3 border mb-4">
                    <table class="table table-borderless mb-0">
                        <tbody>
                            <tr>
                                <td class="text-muted fw-semibold">Payment Status:</td>
                                <td class="text-end fw-bold text-success"><i class="fa-solid fa-check-double me-1"></i> SUCCESS / DEBITED</td>
                            </tr>
                            <tr>
                                <td class="text-muted fw-semibold">Date &amp; Timestamp:</td>
                                <td class="text-end fw-bold text-dark" id="succTimestamp">25-Nov-2026 17:45:10</td>
                            </tr>
                            <tr>
                                <td class="text-muted fw-semibold">Transferred To:</td>
                                <td class="text-end fw-bold text-primary" id="succPayee">Account Number</td>
                            </tr>
                            <tr>
                                <td class="text-muted fw-semibold">Payment Method:</td>
                                <td class="text-end fw-bold text-dark" id="succMethod">INTERNAL_TRANSFER</td>
                            </tr>
                            <tr>
                                <td class="text-muted fw-semibold">Remarks / Note:</td>
                                <td class="text-end text-muted" id="succRemarks">Instant Fund Transfer</td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <div class="alert alert-info small mb-0 text-center">
                    <i class="fa-solid fa-shield-halved me-1"></i> This is a computer-generated bank payment receipt and does not require a physical signature.
                </div>

            </div>

            <!-- Receipt Footer / Print, Download PDF & Share Buttons -->
            <div class="modal-footer bg-light p-3 d-flex flex-wrap justify-content-between gap-2">
                <div class="d-flex gap-2">
                    <button class="btn btn-outline-dark fw-bold btn-sm" onclick="window.print()"><i class="fa-solid fa-print me-1"></i> Print Receipt</button>
                    <button class="btn btn-outline-success fw-bold btn-sm" onclick="downloadPdfReceipt()"><i class="fa-solid fa-file-pdf me-1"></i> Download PDF Receipt</button>
                    <button class="btn btn-outline-info fw-bold btn-sm" onclick="shareWebReceipt()"><i class="fa-solid fa-share-nodes me-1"></i> Share Receipt</button>
                </div>
                <button class="btn btn-sk-primary px-4 fw-bold" data-bs-dismiss="modal">Done &amp; Close</button>
            </div>

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

function downloadPdfReceipt() {
    const txnRef = document.getElementById('succTxnRef').innerText;
    const amount = document.getElementById('succAmount').innerText;
    const payee = document.getElementById('succPayee').innerText;
    const timestamp = document.getElementById('succTimestamp').innerText;
    const method = document.getElementById('succMethod').innerText;
    const remarks = document.getElementById('succRemarks').innerText;

    const receiptContent = `====================================================
SK BANK OF INDIA - OFFICIAL ELECTRONIC RECEIPT
====================================================
Status: PAYMENT COMPLETED & LEDGER VERIFIED
${txnRef}
Amount Paid: ${amount}
Transferred To: ${payee}
Date & Time: ${timestamp}
Payment Method: ${method}
Remarks: ${remarks}
====================================================
This is an official computer-generated receipt.
====================================================`;

    const blob = new Blob([receiptContent], { type: 'text/plain;charset=utf-8' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `SKBank_Receipt_${Date.now()}.txt`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

function shareWebReceipt() {
    const txnRef = document.getElementById('succTxnRef').innerText;
    const amount = document.getElementById('succAmount').innerText;
    const payee = document.getElementById('succPayee').innerText;

    const shareText = `SK Bank Payment Receipt\nStatus: COMPLETED\n${txnRef}\nAmount: ${amount}\nPaid To: ${payee}`;

    if (navigator.share) {
        navigator.share({
            title: 'SK Bank Payment Receipt',
            text: shareText
        }).catch(() => {});
    } else {
        navigator.clipboard.writeText(shareText);
        alert("Receipt details copied to clipboard!");
    }
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
            destinationAccount: destinationAccount,
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

            document.getElementById('succTxnRef').innerText = "Txn Ref: " + (data.data.transactionRef || "TXN" + System.currentTimeMillis());
            document.getElementById('succAmount').innerText = "₹ " + Number(amount).toLocaleString('en-IN', { minimumFractionDigits: 2 });
            document.getElementById('succPayee').innerText = destinationAccount;
            document.getElementById('succTimestamp').innerText = new Date().toLocaleString('en-IN');
            document.getElementById('succMethod').innerText = method;
            document.getElementById('succRemarks').innerText = remarks || "Instant Fund Transfer";

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

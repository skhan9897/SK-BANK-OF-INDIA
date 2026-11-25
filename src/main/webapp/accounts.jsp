<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-1">Bank Accounts & Statements</h2>
            <p class="text-muted small mb-0">Overview of all active Savings, Current, and Salary accounts</p>
        </div>
        <button class="btn btn-sk-primary" data-bs-toggle="modal" data-bs-target="#newAccountModal">
            <i class="fa-solid fa-plus-circle me-1"></i> Open Additional Account
        </button>
    </div>

    <!-- Statement Filter & Accounts List -->
    <div class="sk-card mb-4">
        <h5 class="fw-bold mb-3"><i class="fa-solid fa-receipt me-2 text-primary"></i> Detailed Account Statement</h5>

        <div class="table-responsive">
            <table class="table sk-table mb-0">
                <thead>
                    <tr>
                        <th>Account Number</th>
                        <th>Account Type</th>
                        <th>Available Balance</th>
                        <th>Ledger Balance</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody id="accountsTableBody">
                    <tr>
                        <td colspan="6" class="text-center py-4 text-muted">Loading customer account details...</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modal: Open New Account -->
<div class="modal fade" id="newAccountModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-navy text-white">
                <h5 class="modal-title fw-bold">Open Additional Bank Account</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="createAccountForm" onsubmit="event.preventDefault(); handleCreateAccount();">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Select Account Type</label>
                        <select id="selAccountType" class="form-select">
                            <option value="SAVINGS">Savings Account (3.5% Interest)</option>
                            <option value="CURRENT">Corporate Current Account (Zero Min Balance)</option>
                            <option value="SALARY">Premium Salary Account</option>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-sk-primary w-100">Submit Application</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', () => {
    checkAuthAndRedirect();
    loadAccounts();
});

async function loadAccounts() {
    try {
        const res = await fetch('/SKBankOfIndia/api/v1/protected/accounts', {
            headers: getAuthHeaders()
        });

        const data = await res.json();
        if (data.success && data.data) {
            const tbody = document.getElementById('accountsTableBody');
            tbody.innerHTML = '';

            data.data.forEach(acc => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="fw-bold">${acc.accountNumber}</td>
                    <td><span class="badge bg-primary">${acc.accountTypeCode}</span></td>
                    <td class="fw-bold text-dark">₹ ${Number(acc.availableBalance).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                    <td class="text-muted">₹ ${Number(acc.ledgerBalance).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                    <td><span class="badge bg-success">${acc.status}</span></td>
                    <td><button class="btn btn-sm btn-outline-primary" onclick="alert('Downloading statement PDF for ${acc.accountNumber}')"><i class="fa-solid fa-download me-1"></i> Statement</button></td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (e) {
        console.error(e);
    }
}

async function handleCreateAccount() {
    const type = document.getElementById('selAccountType').value;
    try {
        const res = await fetch('/SKBankOfIndia/api/v1/protected/accounts/create', {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify({ accountType: type })
        });
        const data = await res.json();
        if (data.success) {
            alert("New " + type + " Account Opened! Account No: " + data.data.accountNumber);
            location.reload();
        } else {
            alert(data.message);
        }
    } catch (e) {
        alert("Account opening failed.");
    }
}
</script>

<%@ include file="/WEB-INF/views/footer.jspf" %>

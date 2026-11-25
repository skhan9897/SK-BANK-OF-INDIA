package com.bank.skbankofindia.model;

public class TransferRequest {
    private long sourceAccountId;
    private long destinationAccountId;
    private String amount;
    private String paymentMethod;
    private String remarks;

    public TransferRequest(long sourceAccountId, long destinationAccountId, String amount, String paymentMethod, String remarks) {
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.remarks = remarks;
    }

    public long getSourceAccountId() { return sourceAccountId; }
    public long getDestinationAccountId() { return destinationAccountId; }
    public String getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getRemarks() { return remarks; }
}

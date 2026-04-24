package com.demo.dispute.model;

public class DisputeRequest {
    private String transactionId;
    private String merchantName;
    private String disputeType;
    private String description;
    private String customerNote;
    private double amount;

    public DisputeRequest() {
    }

    public DisputeRequest(String transactionId, String merchantName, String disputeType,
                          String description, String customerNote, double amount) {
        this.transactionId = transactionId;
        this.merchantName = merchantName;
        this.disputeType = disputeType;
        this.description = description;
        this.customerNote = customerNote;
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getDisputeType() {
        return disputeType;
    }

    public void setDisputeType(String disputeType) {
        this.disputeType = disputeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
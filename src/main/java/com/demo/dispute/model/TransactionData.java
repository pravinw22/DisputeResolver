package com.demo.dispute.model;

import java.time.LocalDateTime;

public class TransactionData {
    private String transactionId;
    private double amount;
    private String currency;
    private String merchantName;
    private String merchantCategory;
    private LocalDateTime transactionDate;
    private String cardLast4;
    private String location;

    public TransactionData() {
    }

    public TransactionData(String transactionId, double amount, String currency, 
                           String merchantName, String merchantCategory, LocalDateTime transactionDate,
                           String cardLast4, String location) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.merchantName = merchantName;
        this.merchantCategory = merchantCategory;
        this.transactionDate = transactionDate;
        this.cardLast4 = cardLast4;
        this.location = location;
    }

    public static TransactionDataBuilder builder() {
        return new TransactionDataBuilder();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantCategory() {
        return merchantCategory;
    }

    public void setMerchantCategory(String merchantCategory) {
        this.merchantCategory = merchantCategory;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public static class TransactionDataBuilder {
        private String transactionId;
        private double amount;
        private String currency;
        private String merchantName;
        private String merchantCategory;
        private LocalDateTime transactionDate;
        private String cardLast4;
        private String location;

        public TransactionDataBuilder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public TransactionDataBuilder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public TransactionDataBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public TransactionDataBuilder merchantName(String merchantName) {
            this.merchantName = merchantName;
            return this;
        }

        public TransactionDataBuilder merchantCategory(String merchantCategory) {
            this.merchantCategory = merchantCategory;
            return this;
        }

        public TransactionDataBuilder transactionDate(LocalDateTime transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public TransactionDataBuilder cardLast4(String cardLast4) {
            this.cardLast4 = cardLast4;
            return this;
        }

        public TransactionDataBuilder location(String location) {
            this.location = location;
            return this;
        }

        public TransactionDataBuilder timestamp(LocalDateTime timestamp) {
            this.transactionDate = timestamp;
            return this;
        }

        public TransactionDataBuilder hasTravelHistory(boolean hasTravelHistory) {
            // Placeholder for compatibility - not stored in the model
            return this;
        }

        public TransactionDataBuilder isFirstTimeLocation(boolean isFirstTimeLocation) {
            // Placeholder for compatibility - not stored in the model
            return this;
        }

        public TransactionDataBuilder cardType(String cardType) {
            // Placeholder for compatibility - not stored in the model
            return this;
        }

        public TransactionDataBuilder deviceInfo(String deviceInfo) {
            // Placeholder for compatibility - not stored in the model
            return this;
        }

        public TransactionData build() {
            return new TransactionData(transactionId, amount, currency, merchantName, 
                                       merchantCategory, transactionDate, cardLast4, location);
        }
    }
}
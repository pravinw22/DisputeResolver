package com.demo.dispute.model;

import java.util.List;

public class MerchantContext {
    private String merchantName;
    private String merchantCategory;
    private double disputeRate;
    private List<String> disputeHistory;
    private String riskLevel;

    public MerchantContext() {
    }

    public MerchantContext(String merchantName, String merchantCategory, double disputeRate,
                           List<String> disputeHistory, String riskLevel) {
        this.merchantName = merchantName;
        this.merchantCategory = merchantCategory;
        this.disputeRate = disputeRate;
        this.disputeHistory = disputeHistory;
        this.riskLevel = riskLevel;
    }

    public static MerchantContextBuilder builder() {
        return new MerchantContextBuilder();
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

    public double getDisputeRate() {
        return disputeRate;
    }

    public void setDisputeRate(double disputeRate) {
        this.disputeRate = disputeRate;
    }

    public List<String> getDisputeHistory() {
        return disputeHistory;
    }

    public void setDisputeHistory(List<String> disputeHistory) {
        this.disputeHistory = disputeHistory;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public static class MerchantContextBuilder {
        private String merchantName;
        private String merchantCategory;
        private double disputeRate;
        private List<String> disputeHistory;
        private String riskLevel;

        public MerchantContextBuilder merchantName(String merchantName) {
            this.merchantName = merchantName;
            return this;
        }

        public MerchantContextBuilder merchantCategory(String merchantCategory) {
            this.merchantCategory = merchantCategory;
            return this;
        }

        public MerchantContextBuilder disputeRate(double disputeRate) {
            this.disputeRate = disputeRate;
            return this;
        }

        public MerchantContextBuilder disputeHistory(List<String> disputeHistory) {
            this.disputeHistory = disputeHistory;
            return this;
        }

        public MerchantContextBuilder riskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }

        public MerchantContextBuilder deliveryStatus(String deliveryStatus) {
            // This is a placeholder for compatibility - not stored in the model
            return this;
        }

        public MerchantContextBuilder lastKnownDeliveryAttempt(String lastKnownDeliveryAttempt) {
            // Placeholder for compatibility - not stored in the model
            return this;
        }

        public MerchantContext build() {
            return new MerchantContext(merchantName, merchantCategory, disputeRate, 
                                       disputeHistory, riskLevel);
        }
    }
}
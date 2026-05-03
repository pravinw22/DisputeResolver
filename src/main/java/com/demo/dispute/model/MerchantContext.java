package com.demo.dispute.model;

import java.util.List;

public class MerchantContext {
    private String merchantName;
    private String merchantCategory;
    private double disputeRate;
    private List<String> disputeHistory;
    private String riskLevel;
    private String deliveryStatus;
    private String lastKnownDeliveryAttempt;
    private String ragContext;

    public MerchantContext() {
    }

    public MerchantContext(String merchantName, String merchantCategory, double disputeRate,
                           List<String> disputeHistory, String riskLevel, String deliveryStatus,
                           String lastKnownDeliveryAttempt, String ragContext) {
        this.merchantName = merchantName;
        this.merchantCategory = merchantCategory;
        this.disputeRate = disputeRate;
        this.disputeHistory = disputeHistory;
        this.riskLevel = riskLevel;
        this.deliveryStatus = deliveryStatus;
        this.lastKnownDeliveryAttempt = lastKnownDeliveryAttempt;
        this.ragContext = ragContext;
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

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getLastKnownDeliveryAttempt() {
        return lastKnownDeliveryAttempt;
    }

    public void setLastKnownDeliveryAttempt(String lastKnownDeliveryAttempt) {
        this.lastKnownDeliveryAttempt = lastKnownDeliveryAttempt;
    }

    public String getRagContext() {
        return ragContext;
    }

    public void setRagContext(String ragContext) {
        this.ragContext = ragContext;
    }

    public static class MerchantContextBuilder {
        private String merchantName;
        private String merchantCategory;
        private double disputeRate;
        private List<String> disputeHistory;
        private String riskLevel;
        private String deliveryStatus;
        private String lastKnownDeliveryAttempt;
        private String ragContext;

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
            this.deliveryStatus = deliveryStatus;
            return this;
        }

        public MerchantContextBuilder lastKnownDeliveryAttempt(String lastKnownDeliveryAttempt) {
            this.lastKnownDeliveryAttempt = lastKnownDeliveryAttempt;
            return this;
        }

        public MerchantContextBuilder ragContext(String ragContext) {
            this.ragContext = ragContext;
            return this;
        }

        public MerchantContext build() {
            return new MerchantContext(merchantName, merchantCategory, disputeRate, 
                                       disputeHistory, riskLevel, deliveryStatus,
                                       lastKnownDeliveryAttempt, ragContext);
        }
    }
}
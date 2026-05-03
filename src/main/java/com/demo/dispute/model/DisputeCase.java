package com.demo.dispute.model;

import java.time.LocalDateTime;

public class DisputeCase {
    private String caseId;
    private DisputeRequest request;
    private DisputeStatus status;
    private String finalDecision;
    private String explanation;
    private AuditTrail auditTrail;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private FraudSignals fraudSignals;
    private MerchantContext merchantContext;
    private java.util.List<String> citedPolicies;
    private java.util.List<String> similarCases;
    private java.util.List<String> fraudPatterns;

    public DisputeCase() {
        this.auditTrail = new AuditTrail();
    }

    public DisputeCase(String caseId, DisputeRequest request, DisputeStatus status, 
                       String finalDecision, String explanation, AuditTrail auditTrail,
                       LocalDateTime createdAt, LocalDateTime resolvedAt) {
        this.caseId = caseId;
        this.request = request;
        this.status = status;
        this.finalDecision = finalDecision;
        this.explanation = explanation;
        this.auditTrail = auditTrail != null ? auditTrail : new AuditTrail();
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    public static DisputeCaseBuilder builder() {
        return new DisputeCaseBuilder();
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public DisputeRequest getRequest() {
        return request;
    }

    public void setRequest(DisputeRequest request) {
        this.request = request;
    }

    public DisputeStatus getStatus() {
        return status;
    }

    public void setStatus(DisputeStatus status) {
        this.status = status;
    }

    public String getFinalDecision() {
        return finalDecision;
    }

    public void setFinalDecision(String finalDecision) {
        this.finalDecision = finalDecision;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public AuditTrail getAuditTrail() {
        return auditTrail;
    }

    public void setAuditTrail(AuditTrail auditTrail) {
        this.auditTrail = auditTrail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public FraudSignals getFraudSignals() {
        return fraudSignals;
    }

    public void setFraudSignals(FraudSignals fraudSignals) {
        this.fraudSignals = fraudSignals;
    }

    public MerchantContext getMerchantContext() {
        return merchantContext;
    }

    public void setMerchantContext(MerchantContext merchantContext) {
        this.merchantContext = merchantContext;
    }

    public java.util.List<String> getCitedPolicies() {
        return citedPolicies;
    }

    public void setCitedPolicies(java.util.List<String> citedPolicies) {
        this.citedPolicies = citedPolicies;
    }

    public java.util.List<String> getSimilarCases() {
        return similarCases;
    }

    public void setSimilarCases(java.util.List<String> similarCases) {
        this.similarCases = similarCases;
    }

    public java.util.List<String> getFraudPatterns() {
        return fraudPatterns;
    }

    public void setFraudPatterns(java.util.List<String> fraudPatterns) {
        this.fraudPatterns = fraudPatterns;
    }

    public static class DisputeCaseBuilder {
        private String caseId;
        private DisputeRequest request;
        private DisputeStatus status;
        private String finalDecision;
        private String explanation;
        private AuditTrail auditTrail = new AuditTrail();
        private LocalDateTime createdAt;
        private LocalDateTime resolvedAt;
        private FraudSignals fraudSignals;
        private MerchantContext merchantContext;
        private java.util.List<String> citedPolicies;
        private java.util.List<String> similarCases;
        private java.util.List<String> fraudPatterns;

        public DisputeCaseBuilder caseId(String caseId) {
            this.caseId = caseId;
            return this;
        }

        public DisputeCaseBuilder request(DisputeRequest request) {
            this.request = request;
            return this;
        }

        public DisputeCaseBuilder status(DisputeStatus status) {
            this.status = status;
            return this;
        }

        public DisputeCaseBuilder finalDecision(String finalDecision) {
            this.finalDecision = finalDecision;
            return this;
        }

        public DisputeCaseBuilder explanation(String explanation) {
            this.explanation = explanation;
            return this;
        }

        public DisputeCaseBuilder auditTrail(AuditTrail auditTrail) {
            this.auditTrail = auditTrail;
            return this;
        }

        public DisputeCaseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DisputeCaseBuilder resolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }

        public DisputeCaseBuilder fraudSignals(FraudSignals fraudSignals) {
            this.fraudSignals = fraudSignals;
            return this;
        }

        public DisputeCaseBuilder merchantContext(MerchantContext merchantContext) {
            this.merchantContext = merchantContext;
            return this;
        }

        public DisputeCaseBuilder citedPolicies(java.util.List<String> citedPolicies) {
            this.citedPolicies = citedPolicies;
            return this;
        }

        public DisputeCaseBuilder similarCases(java.util.List<String> similarCases) {
            this.similarCases = similarCases;
            return this;
        }

        public DisputeCaseBuilder fraudPatterns(java.util.List<String> fraudPatterns) {
            this.fraudPatterns = fraudPatterns;
            return this;
        }

        public DisputeCase build() {
            DisputeCase disputeCase = new DisputeCase(caseId, request, status, finalDecision, explanation, 
                                   auditTrail, createdAt, resolvedAt);
            disputeCase.setFraudSignals(fraudSignals);
            disputeCase.setMerchantContext(merchantContext);
            disputeCase.setCitedPolicies(citedPolicies);
            disputeCase.setSimilarCases(similarCases);
            disputeCase.setFraudPatterns(fraudPatterns);
            return disputeCase;
        }
    }
}
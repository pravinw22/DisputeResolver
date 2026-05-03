package com.demo.dispute.agent;

import com.demo.dispute.model.*;
import com.demo.dispute.service.LlmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.demo.dispute.config.Prompts.*;

@Component
public class DisputeOrchestratorAgent {

    private final LlmService llmService;
    private final FraudDetectionAgent fraudDetectionAgent;
    private final TransactionDataAgent transactionDataAgent;
    private final MerchantContextAgent merchantContextAgent;
    private final ComplianceAgent complianceAgent;
    private final ObjectMapper objectMapper;

    public DisputeOrchestratorAgent(
            LlmService llmService,
            FraudDetectionAgent fraudDetectionAgent,
            TransactionDataAgent transactionDataAgent,
            MerchantContextAgent merchantContextAgent,
            ComplianceAgent complianceAgent,
            ObjectMapper objectMapper) {
        this.llmService = llmService;
        this.fraudDetectionAgent = fraudDetectionAgent;
        this.transactionDataAgent = transactionDataAgent;
        this.merchantContextAgent = merchantContextAgent;
        this.complianceAgent = complianceAgent;
        this.objectMapper = objectMapper;
    }

    public DisputeCase process(DisputeCase disputeCase) {
        try {
            DisputeRequest request = disputeCase.getRequest();
            AuditTrail trail = disputeCase.getAuditTrail();

            // === STEP 1: THINK — Understand the dispute ===
            AgentStep step1 = AgentStep.builder()
                    .agentName("OrchestratorAgent")
                    .phase("THINK")
                    .timestamp(LocalDateTime.now())
                    .build();
            
            String understanding = llmService.chat(
                    ORCHESTRATOR_SYSTEM_PROMPT,
                    "Analyze this dispute: " + toJson(request)
            );
            step1.setThought(understanding);
            trail.addStep(step1);

        // === STEP 2: ACT — Fetch transaction data ===
        AgentStep step2 = AgentStep.builder()
                .agentName("TransactionDataAgent")
                .phase("ACT")
                .action("Calling TransactionDataAgent for txn " + request.getTransactionId())
                .timestamp(LocalDateTime.now())
                .build();
        
        TransactionData txnData = transactionDataAgent.fetch(request.getTransactionId());
        step2.setObservation(toJson(txnData));
        trail.addStep(step2);

        // Branch based on dispute type
        if ("FRAUD".equalsIgnoreCase(request.getDisputeType())) {
            processFraudDispute(disputeCase, request, txnData, trail);
        } else {
            processMerchantDispute(disputeCase, request, txnData, trail);
        }

        // === FINAL STEP: Compliance check ===
        AgentStep complianceStep = AgentStep.builder()
                .agentName("ComplianceAgent")
                .phase("ACT")
                .action("Validating decision against compliance policies")
                .timestamp(LocalDateTime.now())
                .build();
        
        String complianceResult = complianceAgent.validate(disputeCase);
        complianceStep.setObservation(complianceResult);
        trail.addStep(complianceStep);

            disputeCase.setAuditTrail(trail);
            return disputeCase;
        } catch (Exception e) {
            e.printStackTrace();
            // Set error state
            disputeCase.setStatus(DisputeStatus.ESCALATED_TO_HUMAN);
            disputeCase.setFinalDecision("ERROR");
            disputeCase.setExplanation("Error processing dispute: " + e.getMessage() + ". Escalated to human review.");
            return disputeCase;
        }
    }

    private void processFraudDispute(DisputeCase disputeCase, DisputeRequest request,
                                     TransactionData txnData, AuditTrail trail) {
        // === STEP 3: ACT — Run fraud detection ===
        AgentStep step3 = AgentStep.builder()
                .agentName("FraudDetectionAgent")
                .phase("ACT")
                .action("Calling FraudDetectionAgent to analyze fraud signals")
                .timestamp(LocalDateTime.now())
                .build();
        
        FraudSignals signals = fraudDetectionAgent.analyze(request, txnData);
        step3.setObservation("Fraud score: " + signals.getScore() + "/100 | Signals: " + 
                            signals.getSignals() + " | Recommendation: " + signals.getRecommendation());
        trail.addStep(step3);
        
        // Store fraud signals on the case for UI display
        disputeCase.setFraudSignals(signals);

        // === STEP 4: THINK — Make decision ===
        AgentStep step4 = AgentStep.builder()
                .agentName("OrchestratorAgent")
                .phase("THINK")
                .timestamp(LocalDateTime.now())
                .build();
        
        String decisionPrompt = String.format("""
            Given fraud signals: %s
            Transaction data: %s
            
            Should this dispute be auto-approved? Respond with:
            DECISION: <AUTO_APPROVED or ESCALATED>
            REASON: <one clear sentence explaining why>
            CONFIDENCE: <HIGH, MEDIUM, or LOW>
            """,
            toJson(signals),
            toJson(txnData)
        );
        
        String decision = llmService.chat(DECISION_SYSTEM_PROMPT, decisionPrompt);
        step4.setThought(decision);
        trail.addStep(step4);

        // Parse decision and set on case
        parseAndSetDecision(disputeCase, decision, signals.getScore());
    }

    private void processMerchantDispute(DisputeCase disputeCase, DisputeRequest request,
                                       TransactionData txnData, AuditTrail trail) {
        // === STEP 3: ACT — Fetch merchant context ===
        AgentStep step3 = AgentStep.builder()
                .agentName("MerchantContextAgent")
                .phase("ACT")
                .action("Calling MerchantContextAgent for merchant " + request.getMerchantName())
                .timestamp(LocalDateTime.now())
                .build();
        
        MerchantContext merchantContext = merchantContextAgent.fetch(request.getMerchantName());
        step3.setObservation(toJson(merchantContext));
        trail.addStep(step3);
        
        // Store merchant context on the case for UI display
        disputeCase.setMerchantContext(merchantContext);

        // === STEP 4: THINK — Analyze merchant dispute ===
        AgentStep step4 = AgentStep.builder()
                .agentName("OrchestratorAgent")
                .phase("THINK")
                .timestamp(LocalDateTime.now())
                .build();
        
        String analysisPrompt = String.format("""
            Analyze this merchant dispute:
            Transaction: %s
            Merchant Context: %s
            Customer Note: %s
            
            Can this be auto-resolved or does it need human review?
            """,
            toJson(txnData),
            toJson(merchantContext),
            request.getCustomerNote()
        );
        
        String analysis = llmService.chat(ORCHESTRATOR_SYSTEM_PROMPT, analysisPrompt);
        step4.setThought(analysis);
        trail.addStep(step4);

        // Merchant disputes with delivery issues always escalate
        disputeCase.setStatus(DisputeStatus.ESCALATED_TO_HUMAN);
        disputeCase.setFinalDecision("ESCALATED_TO_HUMAN");
        disputeCase.setExplanation("Delivery not confirmed. Merchant history shows " + 
                                  merchantContext.getDisputeHistory() + 
                                  " prior disputes. Cannot auto-resolve. Human review required.");
    }

    private void parseAndSetDecision(DisputeCase disputeCase, String decision, int fraudScore) {
        if (decision.toUpperCase().contains("AUTO_APPROVED") || fraudScore >= 80) {
            disputeCase.setStatus(DisputeStatus.AUTO_RESOLVED);
            disputeCase.setFinalDecision("AUTO_APPROVED");
            disputeCase.setResolvedAt(LocalDateTime.now());
            
            // Extract reason from decision text
            String reason = extractReason(decision);
            disputeCase.setExplanation(reason != null ? reason : 
                "Fraud score " + fraudScore + "/100. Strong fraud signals detected. Auto-approved for customer protection.");
        } else {
            disputeCase.setStatus(DisputeStatus.ESCALATED_TO_HUMAN);
            disputeCase.setFinalDecision("ESCALATED");
            
            String reason = extractReason(decision);
            disputeCase.setExplanation(reason != null ? reason : 
                "Insufficient confidence for auto-decision. Escalated to human review.");
        }
    }

    private String extractReason(String decision) {
        if (decision.contains("REASON:")) {
            String[] parts = decision.split("REASON:");
            if (parts.length > 1) {
                String reason = parts[1].split("CONFIDENCE:")[0].trim();
                return reason;
            }
        }
        return null;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }
}

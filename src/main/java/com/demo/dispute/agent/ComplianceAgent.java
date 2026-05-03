package com.demo.dispute.agent;

import com.demo.dispute.model.DisputeCase;
import com.demo.dispute.rag.RagService;
import com.demo.dispute.service.LlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.demo.dispute.config.Prompts.COMPLIANCE_SYSTEM_PROMPT;

@Component
public class ComplianceAgent {

    private static final Logger logger = LoggerFactory.getLogger(ComplianceAgent.class);

    private final LlmService llmService;
    private final RagService ragService;

    public ComplianceAgent(LlmService llmService, RagService ragService) {
        this.llmService = llmService;
        this.ragService = ragService;
    }

    /**
     * Validates the decision against policy rules and regulatory requirements using RAG.
     */
    public String validate(DisputeCase disputeCase) {
        logger.info("Validating compliance with RAG enhancement for case: {}", disputeCase.getCaseId());
        
        // Build RAG-enhanced context with relevant regulations
        String ragContext = ragService.buildComplianceContext(
                disputeCase.getFinalDecision(),
                disputeCase.getExplanation()
        );
        
        String prompt = String.format("""
            You are a banking compliance officer. Review this dispute decision for policy compliance.
            
            Decision: %s
            Reason: %s
            Dispute Type: %s
            
            %s
            
            Based on the above regulations and policies, validate this decision.
            Cite specific regulation IDs or policy IDs in your validation.
            
            Check: 
            (1) Is the auto-decision threshold appropriate per regulations? 
            (2) Is audit trail complete per compliance requirements? 
            (3) Any regulatory concerns?
            
            Respond in 2-3 sentences with specific citations to regulations.
            """,
            disputeCase.getFinalDecision(),
            disputeCase.getExplanation(),
            disputeCase.getRequest().getDisputeType(),
            ragContext
        );

        String validation = llmService.chat(COMPLIANCE_SYSTEM_PROMPT, prompt);
        
        logger.info("Compliance validation complete for case: {}", disputeCase.getCaseId());
        
        return validation;
    }
}
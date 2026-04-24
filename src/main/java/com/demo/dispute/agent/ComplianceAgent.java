package com.demo.dispute.agent;

import com.demo.dispute.model.DisputeCase;
import com.demo.dispute.service.LlmService;
import org.springframework.stereotype.Component;

import static com.demo.dispute.config.Prompts.COMPLIANCE_SYSTEM_PROMPT;

@Component
public class ComplianceAgent {

    private final LlmService llmService;

    public ComplianceAgent(LlmService llmService) {
        this.llmService = llmService;
    }

    /**
     * Validates the decision against policy rules and regulatory requirements.
     */
    public String validate(DisputeCase disputeCase) {
        String prompt = String.format("""
            You are a banking compliance officer. Review this dispute decision for policy compliance.
            Decision: %s
            Reason: %s
            Dispute Type: %s
            
            Check: (1) Is the auto-decision threshold appropriate? (2) Is audit trail complete? (3) Any regulatory concerns?
            Respond briefly in 2-3 sentences.
            """,
            disputeCase.getFinalDecision(),
            disputeCase.getExplanation(),
            disputeCase.getRequest().getDisputeType()
        );

        return llmService.chat(COMPLIANCE_SYSTEM_PROMPT, prompt);
    }
}

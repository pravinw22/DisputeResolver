package com.demo.dispute.service;

import com.demo.dispute.agent.DisputeOrchestratorAgent;
import com.demo.dispute.model.AgentStep;
import com.demo.dispute.model.DisputeCase;
import com.demo.dispute.model.DisputeRequest;
import com.demo.dispute.model.DisputeStatus;
import com.demo.dispute.store.InMemoryDisputeStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DisputeService {

    private final DisputeOrchestratorAgent orchestratorAgent;
    private final InMemoryDisputeStore store;

    public DisputeService(DisputeOrchestratorAgent orchestratorAgent, InMemoryDisputeStore store) {
        this.orchestratorAgent = orchestratorAgent;
        this.store = store;
    }

    public DisputeCase submit(DisputeRequest request) {
        // 1. Generate case ID
        String caseId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 2. Create initial case
        DisputeCase disputeCase = DisputeCase.builder()
                .caseId(caseId)
                .request(request)
                .status(DisputeStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .build();

        // 3. Run orchestrator (this calls Claude + sub-agents)
        disputeCase = orchestratorAgent.process(disputeCase);

        // 4. Store in memory
        store.save(disputeCase);

        return disputeCase;
    }

    public DisputeCase getCase(String caseId) {
        return store.findById(caseId);
    }

    public List<DisputeCase> getAllCases() {
        return store.findAll();
    }

    public List<DisputeCase> getEscalatedCases() {
        return store.findByStatus(DisputeStatus.ESCALATED_TO_HUMAN);
    }

    public DisputeCase humanDecide(String caseId, String decision, String note) {
        DisputeCase disputeCase = store.findById(caseId);
        if (disputeCase == null) {
            throw new IllegalArgumentException("Case not found: " + caseId);
        }

        disputeCase.setStatus(DisputeStatus.HUMAN_RESOLVED);
        disputeCase.setFinalDecision(decision);
        disputeCase.setExplanation("Human reviewer decision: " + note);
        disputeCase.setResolvedAt(LocalDateTime.now());

        // Add a final audit step
        AgentStep humanStep = AgentStep.builder()
                .agentName("HumanReviewer")
                .phase("ACT")
                .action("Human reviewer submitted decision: " + decision)
                .observation(note)
                .timestamp(LocalDateTime.now())
                .build();
        
        disputeCase.getAuditTrail().addStep(humanStep);

        store.save(disputeCase);
        return disputeCase;
    }
}

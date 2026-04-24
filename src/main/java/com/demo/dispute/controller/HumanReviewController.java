package com.demo.dispute.controller;

import com.demo.dispute.model.DisputeCase;
import com.demo.dispute.service.DisputeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
public class HumanReviewController {

    private final DisputeService disputeService;

    public HumanReviewController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @GetMapping("/queue")
    public ResponseEntity<List<DisputeCase>> getReviewQueue() {
        List<DisputeCase> escalatedCases = disputeService.getEscalatedCases();
        return ResponseEntity.ok(escalatedCases);
    }

    @GetMapping("/{caseId}")
    public ResponseEntity<DisputeCase> getCaseDetail(@PathVariable String caseId) {
        DisputeCase disputeCase = disputeService.getCase(caseId);
        if (disputeCase == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(disputeCase);
    }

    @PostMapping("/{caseId}/decision")
    public ResponseEntity<DisputeCase> submitDecision(
            @PathVariable String caseId,
            @RequestBody Map<String, String> decisionRequest) {
        
        String decision = decisionRequest.get("decision");
        String note = decisionRequest.get("note");
        
        DisputeCase updatedCase = disputeService.humanDecide(caseId, decision, note);
        return ResponseEntity.ok(updatedCase);
    }
}

package com.demo.dispute.controller;

import com.demo.dispute.model.DisputeCase;
import com.demo.dispute.model.DisputeRequest;
import com.demo.dispute.service.DisputeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/disputes")
public class DisputeController {

    private final DisputeService disputeService;

    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @PostMapping
    public ResponseEntity<DisputeCase> submitDispute(@RequestBody DisputeRequest request) {
        try {
            DisputeCase disputeCase = disputeService.submit(request);
            if (disputeCase == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            return ResponseEntity.ok(disputeCase);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{caseId}")
    public ResponseEntity<DisputeCase> getDispute(@PathVariable String caseId) {
        DisputeCase disputeCase = disputeService.getCase(caseId);
        if (disputeCase == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(disputeCase);
    }

    @GetMapping
    public ResponseEntity<?> getAllDisputes() {
        return ResponseEntity.ok(disputeService.getAllCases());
    }
}

package com.demo.dispute.controller;

import com.demo.dispute.model.DisputeCase;
import com.demo.dispute.service.DisputeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UiController {

    private final DisputeService disputeService;

    public UiController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/result")
    public String result(@RequestParam String caseId, Model model) {
        DisputeCase disputeCase = disputeService.getCase(caseId);
        if (disputeCase == null) {
            model.addAttribute("error", "Case not found: " + caseId);
            return "error";
        }
        model.addAttribute("case", disputeCase);
        return "result";
    }

    @GetMapping("/review")
    public String reviewQueue(Model model) {
        List<DisputeCase> cases = disputeService.getEscalatedCases();
        model.addAttribute("cases", cases);
        return "review-queue";
    }
}

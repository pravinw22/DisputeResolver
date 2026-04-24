package com.demo.dispute.model;

import java.util.ArrayList;
import java.util.List;

public class AuditTrail {
    private List<AgentStep> steps;

    public AuditTrail() {
        this.steps = new ArrayList<>();
    }

    public AuditTrail(List<AgentStep> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
    }

    public List<AgentStep> getSteps() {
        return steps;
    }

    public void setSteps(List<AgentStep> steps) {
        this.steps = steps;
    }

    public void addStep(AgentStep step) {
        step.setStepNumber(steps.size() + 1);
        steps.add(step);
    }
}
package com.demo.dispute.model;

import java.time.LocalDateTime;

public class AgentStep {
    private int stepNumber;
    private String agentName;
    private String thought;
    private String action;
    private String observation;
    private LocalDateTime timestamp;
    private String phase;

    public AgentStep() {
    }

    public AgentStep(int stepNumber, String agentName, String thought, String action, 
                     String observation, LocalDateTime timestamp, String phase) {
        this.stepNumber = stepNumber;
        this.agentName = agentName;
        this.thought = thought;
        this.action = action;
        this.observation = observation;
        this.timestamp = timestamp;
        this.phase = phase;
    }

    public static AgentStepBuilder builder() {
        return new AgentStepBuilder();
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public static class AgentStepBuilder {
        private int stepNumber;
        private String agentName;
        private String thought;
        private String action;
        private String observation;
        private LocalDateTime timestamp;
        private String phase;

        public AgentStepBuilder stepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
            return this;
        }

        public AgentStepBuilder agentName(String agentName) {
            this.agentName = agentName;
            return this;
        }

        public AgentStepBuilder thought(String thought) {
            this.thought = thought;
            return this;
        }

        public AgentStepBuilder action(String action) {
            this.action = action;
            return this;
        }

        public AgentStepBuilder observation(String observation) {
            this.observation = observation;
            return this;
        }

        public AgentStepBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public AgentStepBuilder phase(String phase) {
            this.phase = phase;
            this.action = phase;
            return this;
        }

        public AgentStep build() {
            return new AgentStep(stepNumber, agentName, thought, action, observation, timestamp, phase);
        }
    }
}
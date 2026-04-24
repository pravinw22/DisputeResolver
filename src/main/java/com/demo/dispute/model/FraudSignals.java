package com.demo.dispute.model;

import java.util.List;

public class FraudSignals {
    private int score;
    private List<String> signals;
    private String recommendation;
    private String reasoning;

    public FraudSignals() {
    }

    public FraudSignals(int score, List<String> signals, String recommendation, String reasoning) {
        this.score = score;
        this.signals = signals;
        this.recommendation = recommendation;
        this.reasoning = reasoning;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getSignals() {
        return signals;
    }

    public void setSignals(List<String> signals) {
        this.signals = signals;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
}
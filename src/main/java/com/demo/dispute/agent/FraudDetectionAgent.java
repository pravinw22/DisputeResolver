package com.demo.dispute.agent;

import com.demo.dispute.model.DisputeRequest;
import com.demo.dispute.model.FraudSignals;
import com.demo.dispute.model.TransactionData;
import com.demo.dispute.service.LlmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.demo.dispute.config.Prompts.FRAUD_SYSTEM_PROMPT;

@Component
public class FraudDetectionAgent {

    private final LlmService llmService;
    private final ObjectMapper objectMapper;

    public FraudDetectionAgent(LlmService llmService, ObjectMapper objectMapper) {
        this.llmService = llmService;
        this.objectMapper = objectMapper;
    }

    public FraudSignals analyze(DisputeRequest request, TransactionData txnData) {
        String prompt = String.format("""
            You are a fraud detection engine. Analyze the following transaction data and return fraud signals.
            Transaction: %s
            Customer description: %s
            
            Respond in JSON format only: { "score": 0-100, "signals": ["signal1", "signal2"], "recommendation": "AUTO_APPROVE or ESCALATE", "reasoning": "brief explanation" }
            """,
            toJson(txnData),
            request.getDescription()
        );

        String response = llmService.chat(FRAUD_SYSTEM_PROMPT, prompt);
        return parseFraudSignals(response);
    }

    private FraudSignals parseFraudSignals(String response) {
        try {
            // Try to parse JSON response
            return objectMapper.readValue(response, FraudSignals.class);
        } catch (Exception e) {
            // Fallback: create signals based on keywords in response
            FraudSignals signals = new FraudSignals();
            
            if (response.toLowerCase().contains("foreign") || response.toLowerCase().contains("unknown location")) {
                signals.setScore(92);
                signals.setSignals(Arrays.asList("foreign_location", "no_travel_history", "unusual_amount"));
                signals.setRecommendation("AUTO_APPROVE");
                signals.setReasoning("High fraud score detected: foreign location with no travel history");
            } else {
                signals.setScore(45);
                signals.setSignals(Arrays.asList("normal_pattern"));
                signals.setRecommendation("ESCALATE");
                signals.setReasoning("Moderate risk, requires human review");
            }
            
            return signals;
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }
}

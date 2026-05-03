package com.demo.dispute.agent;

import com.demo.dispute.model.DisputeRequest;
import com.demo.dispute.model.FraudSignals;
import com.demo.dispute.model.TransactionData;
import com.demo.dispute.rag.RagService;
import com.demo.dispute.service.LlmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.demo.dispute.config.Prompts.FRAUD_SYSTEM_PROMPT;

@Component
public class FraudDetectionAgent {

    private static final Logger logger = LoggerFactory.getLogger(FraudDetectionAgent.class);

    private final LlmService llmService;
    private final ObjectMapper objectMapper;
    private final RagService ragService;

    public FraudDetectionAgent(LlmService llmService, ObjectMapper objectMapper, RagService ragService) {
        this.llmService = llmService;
        this.objectMapper = objectMapper;
        this.ragService = ragService;
    }

    public FraudSignals analyze(DisputeRequest request, TransactionData txnData) {
        logger.info("Analyzing fraud signals with RAG enhancement for transaction: {}", request.getTransactionId());
        
        // Build RAG-enhanced context
        String ragContext = ragService.buildFraudDetectionContext(request, txnData);
        
        String prompt = String.format("""
            You are a fraud detection engine. Analyze the following transaction data and return fraud signals.
            
            Transaction Data:
            %s
            
            Customer Description: %s
            
            %s
            
            Based on the above historical cases, fraud patterns, and policies, analyze this transaction.
            Cite specific case IDs, pattern IDs, or policy IDs in your reasoning.
            
            Respond in JSON format only: 
            { 
              "score": 0-100, 
              "signals": ["signal1", "signal2"], 
              "recommendation": "AUTO_APPROVE or ESCALATE", 
              "reasoning": "brief explanation with citations to similar cases, patterns, or policies"
            }
            """,
            toJson(txnData),
            request.getDescription(),
            ragContext
        );

        String response = llmService.chat(FRAUD_SYSTEM_PROMPT, prompt);
        FraudSignals signals = parseFraudSignals(response);
        
        logger.info("Fraud analysis complete. Score: {}, Recommendation: {}", 
                signals.getScore(), signals.getRecommendation());
        
        return signals;
    }

    private FraudSignals parseFraudSignals(String response) {
        try {
            // Try to parse JSON response
            return objectMapper.readValue(response, FraudSignals.class);
        } catch (Exception e) {
            logger.warn("Failed to parse LLM response as JSON, using fallback logic", e);
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
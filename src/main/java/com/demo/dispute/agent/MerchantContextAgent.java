package com.demo.dispute.agent;

import com.demo.dispute.model.DisputeRequest;
import com.demo.dispute.model.MerchantContext;
import com.demo.dispute.rag.RagSearchResult;
import com.demo.dispute.rag.RagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MerchantContextAgent {

    private static final Logger logger = LoggerFactory.getLogger(MerchantContextAgent.class);

    private final RagService ragService;

    public MerchantContextAgent(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * Fetches merchant context using RAG to retrieve historical data.
     * Returns merchant reputation, dispute history, and delivery performance.
     */
    public MerchantContext fetch(String merchantName) {
        logger.info("Fetching merchant context with RAG for: {}", merchantName);
        
        // Retrieve merchant data from RAG
        List<RagSearchResult> merchantResults = ragService.retrieveMerchantContext(merchantName);
        
        if (merchantResults.isEmpty()) {
            logger.warn("No merchant data found for: {}. Using fallback data.", merchantName);
            return createFallbackContext(merchantName);
        }
        
        // Parse merchant data from RAG result
        RagSearchResult result = merchantResults.get(0);
        String content = result.getContent();
        
        // Extract information from content
        String deliveryStatus = extractDeliveryStatus(content);
        double disputeRate = extractDisputeRate(content);
        String riskLevel = extractRiskLevel(content);
        
        logger.info("Merchant context retrieved. Dispute Rate: {}, Risk Level: {}", disputeRate, riskLevel);
        
        return MerchantContext.builder()
            .merchantName(merchantName)
            .deliveryStatus(deliveryStatus)
            .lastKnownDeliveryAttempt("Based on historical data")
            .disputeHistory(Arrays.asList(
                "Historical dispute data retrieved from RAG",
                String.format("Merchant has %.1f%% dispute rate", disputeRate * 100),
                String.format("Risk Level: %s", riskLevel)
            ))
            .riskLevel(riskLevel)
            .disputeRate(disputeRate)
            .ragContext(content)  // Store full RAG context for reference
            .build();
    }

    /**
     * Fetch merchant context with additional dispute request context.
     */
    public MerchantContext fetchWithContext(String merchantName, DisputeRequest request) {
        logger.info("Fetching merchant context with dispute context for: {}", merchantName);
        
        MerchantContext context = fetch(merchantName);
        
        // Build enhanced context using RAG
        String enhancedContext = ragService.buildMerchantDisputeContext(request, merchantName);
        context.setRagContext(enhancedContext);
        
        return context;
    }

    private MerchantContext createFallbackContext(String merchantName) {
        // Fallback for merchants not in RAG database
        return MerchantContext.builder()
            .merchantName(merchantName)
            .deliveryStatus("NOT_DELIVERED")
            .lastKnownDeliveryAttempt("3 days ago")
            .disputeHistory(Arrays.asList("No historical data available"))
            .riskLevel("MEDIUM")
            .disputeRate(0.05)
            .build();
    }

    private String extractDeliveryStatus(String content) {
        if (content.contains("NOT_DELIVERED") || content.contains("not delivered")) {
            return "NOT_DELIVERED";
        } else if (content.contains("DELIVERED")) {
            return "DELIVERED";
        }
        return "UNKNOWN";
    }

    private double extractDisputeRate(String content) {
        try {
            // Look for "Dispute Rate: X%" pattern
            String[] lines = content.split("\n");
            for (String line : lines) {
                if (line.contains("Dispute Rate:")) {
                    String rateStr = line.replaceAll("[^0-9.]", "");
                    return Double.parseDouble(rateStr) / 100.0;
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to extract dispute rate from content", e);
        }
        return 0.05; // Default 5%
    }

    private String extractRiskLevel(String content) {
        if (content.contains("Risk Level: HIGH") || content.contains("HIGH RISK")) {
            return "HIGH";
        } else if (content.contains("Risk Level: LOW")) {
            return "LOW";
        }
        return "MEDIUM";
    }
}
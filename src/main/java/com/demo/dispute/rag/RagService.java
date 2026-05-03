package com.demo.dispute.rag;

import com.demo.dispute.model.DisputeRequest;
import com.demo.dispute.model.TransactionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Main RAG service that provides retrieval-augmented generation capabilities.
 * Orchestrates semantic search across different knowledge bases.
 */
@Service
public class RagService {

    private static final Logger logger = LoggerFactory.getLogger(RagService.class);

    private final InMemoryVectorStore vectorStore;

    public RagService(InMemoryVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Retrieve similar historical dispute cases.
     */
    public List<RagSearchResult> retrieveSimilarCases(String disputeDescription, int topK) {
        logger.info("Retrieving similar cases for: {}", disputeDescription);
        return vectorStore.search(disputeDescription, topK, "CASE", 0.5);
    }

    /**
     * Retrieve relevant policies based on dispute type and context.
     */
    public List<RagSearchResult> retrievePolicies(String disputeType, String context) {
        String query = String.format("Dispute type: %s. Context: %s", disputeType, context);
        logger.info("Retrieving policies for: {}", query);
        return vectorStore.search(query, 3, "POLICY", 0.4);
    }

    /**
     * Retrieve relevant regulations based on context.
     */
    public List<RagSearchResult> retrieveRegulations(String context) {
        logger.info("Retrieving regulations for: {}", context);
        return vectorStore.search(context, 3, "REGULATION", 0.4);
    }

    /**
     * Retrieve fraud patterns matching transaction characteristics.
     */
    public List<RagSearchResult> retrieveFraudPatterns(TransactionData txnData) {
        String query = String.format("Transaction in %s, amount %.2f, unusual pattern detection",
                txnData.getLocation(), txnData.getAmount());
        logger.info("Retrieving fraud patterns for: {}", query);
        return vectorStore.search(query, 3, "FRAUD_PATTERN", 0.5);
    }

    /**
     * Retrieve merchant history and reputation.
     */
    public List<RagSearchResult> retrieveMerchantContext(String merchantName) {
        logger.info("Retrieving merchant context for: {}", merchantName);
        return vectorStore.search(merchantName, 1, "MERCHANT", 0.6);
    }

    /**
     * Build enhanced context for fraud detection with RAG.
     */
    public String buildFraudDetectionContext(DisputeRequest request, TransactionData txnData) {
        StringBuilder context = new StringBuilder();

        // Retrieve similar cases
        List<RagSearchResult> similarCases = retrieveSimilarCases(request.getDescription(), 3);
        if (!similarCases.isEmpty()) {
            context.append("\n=== SIMILAR HISTORICAL CASES ===\n");
            for (int i = 0; i < similarCases.size(); i++) {
                RagSearchResult result = similarCases.get(i);
                context.append(String.format("%d. %s (Similarity: %.2f)\n%s\n\n",
                        i + 1,
                        result.getId(),
                        result.getSimilarityScore(),
                        result.getContent()));
            }
        }

        // Retrieve fraud patterns
        List<RagSearchResult> patterns = retrieveFraudPatterns(txnData);
        if (!patterns.isEmpty()) {
            context.append("\n=== RELEVANT FRAUD PATTERNS ===\n");
            for (int i = 0; i < patterns.size(); i++) {
                RagSearchResult result = patterns.get(i);
                context.append(String.format("%d. %s (Similarity: %.2f)\n%s\n\n",
                        i + 1,
                        result.getId(),
                        result.getSimilarityScore(),
                        result.getContent()));
            }
        }

        // Retrieve relevant policies
        List<RagSearchResult> policies = retrievePolicies("FRAUD", request.getDescription());
        if (!policies.isEmpty()) {
            context.append("\n=== RELEVANT POLICIES ===\n");
            for (int i = 0; i < policies.size(); i++) {
                RagSearchResult result = policies.get(i);
                context.append(String.format("%d. %s (Similarity: %.2f)\n%s\n\n",
                        i + 1,
                        result.getId(),
                        result.getSimilarityScore(),
                        result.getContent()));
            }
        }

        return context.toString();
    }

    /**
     * Build enhanced context for merchant disputes with RAG.
     */
    public String buildMerchantDisputeContext(DisputeRequest request, String merchantName) {
        StringBuilder context = new StringBuilder();

        // Retrieve merchant history
        List<RagSearchResult> merchantInfo = retrieveMerchantContext(merchantName);
        if (!merchantInfo.isEmpty()) {
            context.append("\n=== MERCHANT HISTORY & REPUTATION ===\n");
            RagSearchResult result = merchantInfo.get(0);
            context.append(String.format("%s (Similarity: %.2f)\n%s\n\n",
                    result.getId(),
                    result.getSimilarityScore(),
                    result.getContent()));
        }

        // Retrieve similar merchant dispute cases
        List<RagSearchResult> similarCases = retrieveSimilarCases(
                "Merchant dispute: " + request.getDescription(), 3);
        if (!similarCases.isEmpty()) {
            context.append("\n=== SIMILAR MERCHANT DISPUTE CASES ===\n");
            for (int i = 0; i < similarCases.size(); i++) {
                RagSearchResult result = similarCases.get(i);
                context.append(String.format("%d. %s (Similarity: %.2f)\n%s\n\n",
                        i + 1,
                        result.getId(),
                        result.getSimilarityScore(),
                        result.getContent()));
            }
        }

        // Retrieve relevant policies
        List<RagSearchResult> policies = retrievePolicies("MERCHANT", request.getDescription());
        if (!policies.isEmpty()) {
            context.append("\n=== RELEVANT MERCHANT POLICIES ===\n");
            for (int i = 0; i < policies.size(); i++) {
                RagSearchResult result = policies.get(i);
                context.append(String.format("%d. %s (Similarity: %.2f)\n%s\n\n",
                        i + 1,
                        result.getId(),
                        result.getSimilarityScore(),
                        result.getContent()));
            }
        }

        return context.toString();
    }

    /**
     * Build enhanced context for compliance validation with RAG.
     */
    public String buildComplianceContext(String decision, String reasoning) {
        StringBuilder context = new StringBuilder();

        // Retrieve relevant regulations
        String query = String.format("Decision: %s. Reasoning: %s", decision, reasoning);
        List<RagSearchResult> regulations = retrieveRegulations(query);
        if (!regulations.isEmpty()) {
            context.append("\n=== RELEVANT REGULATIONS ===\n");
            for (int i = 0; i < regulations.size(); i++) {
                RagSearchResult result = regulations.get(i);
                context.append(String.format("%d. %s (Similarity: %.2f)\n%s\n\n",
                        i + 1,
                        result.getId(),
                        result.getSimilarityScore(),
                        result.getContent()));
            }
        }

        return context.toString();
    }

    /**
     * Format RAG results as citations for audit trail.
     */
    public String formatCitations(List<RagSearchResult> results) {
        if (results.isEmpty()) {
            return "No relevant documents found.";
        }

        return results.stream()
                .map(result -> String.format("- %s (Similarity: %.2f)",
                        result.getId(),
                        result.getSimilarityScore()))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Get vector store statistics.
     */
    public String getVectorStoreStats() {
        var stats = vectorStore.getStats();
        return String.format("Vector Store: %d documents loaded. Categories: %s",
                stats.get("totalDocuments"),
                stats.get("categoryCounts"));
    }
}

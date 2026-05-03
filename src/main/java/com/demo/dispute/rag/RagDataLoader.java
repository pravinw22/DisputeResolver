package com.demo.dispute.rag;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads RAG data from JSON files on application startup.
 * Converts JSON documents to embeddings and stores in vector database.
 */
@Component
public class RagDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RagDataLoader.class);

    private final InMemoryVectorStore vectorStore;
    private final EmbeddingService embeddingService;
    private final ObjectMapper objectMapper;

    public RagDataLoader(InMemoryVectorStore vectorStore,
                         EmbeddingService embeddingService,
                         ObjectMapper objectMapper) {
        this.vectorStore = vectorStore;
        this.embeddingService = embeddingService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting RAG data loading...");
        
        try {
            loadPolicies();
            loadRegulations();
            loadHistoricalCases();
            loadFraudPatterns();
            loadMerchants();
            
            logger.info("RAG data loading completed successfully!");
            logger.info(vectorStore.getStats().toString());
        } catch (Exception e) {
            logger.error("Error loading RAG data", e);
        }
    }

    private void loadPolicies() throws IOException {
        logger.info("Loading policies...");
        InputStream is = new ClassPathResource("rag-data/policies.json").getInputStream();
        List<Map<String, Object>> policies = objectMapper.readValue(is, new TypeReference<>() {});
        
        for (Map<String, Object> policy : policies) {
            String id = (String) policy.get("id");
            String title = (String) policy.get("title");
            String content = (String) policy.get("content");
            String category = (String) policy.get("category");
            
            // Create searchable text
            String searchableText = String.format("%s\n%s\n%s", id, title, content);
            
            // Generate embedding
            float[] embedding = embeddingService.embed(searchableText);
            
            // Create metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("title", title);
            metadata.put("policyCategory", category);
            metadata.put("version", policy.get("version"));
            metadata.put("effectiveDate", policy.get("effectiveDate"));
            
            // Store document
            RagDocument doc = new RagDocument(id, content, embedding, "POLICY", metadata);
            vectorStore.addDocument(doc);
        }
        
        logger.info("Loaded {} policies", policies.size());
    }

    private void loadRegulations() throws IOException {
        logger.info("Loading regulations...");
        InputStream is = new ClassPathResource("rag-data/regulations.json").getInputStream();
        List<Map<String, Object>> regulations = objectMapper.readValue(is, new TypeReference<>() {});
        
        for (Map<String, Object> regulation : regulations) {
            String id = (String) regulation.get("id");
            String title = (String) regulation.get("title");
            String content = (String) regulation.get("content");
            String authority = (String) regulation.get("authority");
            
            // Create searchable text
            String searchableText = String.format("%s\n%s\n%s\n%s", id, title, authority, content);
            
            // Generate embedding
            float[] embedding = embeddingService.embed(searchableText);
            
            // Create metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("title", title);
            metadata.put("authority", authority);
            metadata.put("regulationCategory", regulation.get("category"));
            metadata.put("reference", regulation.get("reference"));
            
            // Store document
            RagDocument doc = new RagDocument(id, content, embedding, "REGULATION", metadata);
            vectorStore.addDocument(doc);
        }
        
        logger.info("Loaded {} regulations", regulations.size());
    }

    private void loadHistoricalCases() throws IOException {
        logger.info("Loading historical cases...");
        InputStream is = new ClassPathResource("rag-data/historical-cases.json").getInputStream();
        List<Map<String, Object>> cases = objectMapper.readValue(is, new TypeReference<>() {});
        
        for (Map<String, Object> caseData : cases) {
            String caseId = (String) caseData.get("caseId");
            String disputeType = (String) caseData.get("disputeType");
            String customerNote = (String) caseData.get("customerNote");
            String reasoning = (String) caseData.get("reasoning");
            String decision = (String) caseData.get("decision");
            
            // Create searchable text
            String searchableText = String.format(
                    "Case: %s\nType: %s\nCustomer: %s\nReasoning: %s\nDecision: %s",
                    caseId, disputeType, customerNote, reasoning, decision);
            
            // Generate embedding
            float[] embedding = embeddingService.embed(searchableText);
            
            // Create content summary
            String content = String.format(
                    "Case ID: %s\nDispute Type: %s\nAmount: ₹%.2f\nLocation: %s\n" +
                    "Customer Note: %s\nDecision: %s\nReasoning: %s\nOutcome: %s\nResolution Time: %s",
                    caseId,
                    disputeType,
                    caseData.get("amount"),
                    caseData.get("location"),
                    customerNote,
                    decision,
                    reasoning,
                    caseData.get("outcome"),
                    caseData.get("resolutionTime"));
            
            // Create metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("disputeType", disputeType);
            metadata.put("amount", caseData.get("amount"));
            metadata.put("decision", decision);
            metadata.put("outcome", caseData.get("outcome"));
            metadata.put("fraudScore", caseData.get("fraudScore"));
            
            // Store document
            RagDocument doc = new RagDocument(caseId, content, embedding, "CASE", metadata);
            vectorStore.addDocument(doc);
        }
        
        logger.info("Loaded {} historical cases", cases.size());
    }

    private void loadFraudPatterns() throws IOException {
        logger.info("Loading fraud patterns...");
        InputStream is = new ClassPathResource("rag-data/fraud-patterns.json").getInputStream();
        List<Map<String, Object>> patterns = objectMapper.readValue(is, new TypeReference<>() {});
        
        for (Map<String, Object> pattern : patterns) {
            String patternId = (String) pattern.get("patternId");
            String name = (String) pattern.get("name");
            String description = (String) pattern.get("description");
            @SuppressWarnings("unchecked")
            List<String> indicators = (List<String>) pattern.get("indicators");
            
            // Create searchable text
            String searchableText = String.format(
                    "%s\n%s\n%s\nIndicators: %s",
                    patternId, name, description, String.join(", ", indicators));
            
            // Generate embedding
            float[] embedding = embeddingService.embed(searchableText);
            
            // Create content summary
            String content = String.format(
                    "Pattern ID: %s\nName: %s\nDescription: %s\nRisk Level: %s\n" +
                    "Recommended Action: %s\nHistorical Accuracy: %.1f%%\nFalse Positive Rate: %.1f%%\n" +
                    "Indicators:\n- %s",
                    patternId,
                    name,
                    description,
                    pattern.get("riskLevel"),
                    pattern.get("recommendedAction"),
                    pattern.get("historicalAccuracy"),
                    pattern.get("falsePositiveRate"),
                    String.join("\n- ", indicators));
            
            // Create metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("name", name);
            metadata.put("riskLevel", pattern.get("riskLevel"));
            metadata.put("recommendedAction", pattern.get("recommendedAction"));
            metadata.put("accuracy", pattern.get("historicalAccuracy"));
            
            // Store document
            RagDocument doc = new RagDocument(patternId, content, embedding, "FRAUD_PATTERN", metadata);
            vectorStore.addDocument(doc);
        }
        
        logger.info("Loaded {} fraud patterns", patterns.size());
    }

    private void loadMerchants() throws IOException {
        logger.info("Loading merchant data...");
        InputStream is = new ClassPathResource("rag-data/merchants.json").getInputStream();
        List<Map<String, Object>> merchants = objectMapper.readValue(is, new TypeReference<>() {});
        
        for (Map<String, Object> merchant : merchants) {
            String merchantId = (String) merchant.get("merchantId");
            String name = (String) merchant.get("name");
            String category = (String) merchant.get("category");
            String notes = (String) merchant.get("notes");
            
            // Create searchable text
            String searchableText = String.format(
                    "%s\n%s\nCategory: %s\n%s",
                    merchantId, name, category, notes);
            
            // Generate embedding
            float[] embedding = embeddingService.embed(searchableText);
            
            // Create content summary
            @SuppressWarnings("unchecked")
            Map<String, Object> deliveryPerf = (Map<String, Object>) merchant.get("deliveryPerformance");
            String content = String.format(
                    "Merchant ID: %s\nName: %s\nCategory: %s\nTotal Transactions: %d\n" +
                    "Dispute Rate: %.1f%%\nRisk Level: %s\n" +
                    "Delivery Performance:\n- On-Time Rate: %.1f%%\n- Average Delay: %s\n" +
                    "Notes: %s",
                    merchantId,
                    name,
                    category,
                    merchant.get("totalTransactions"),
                    ((Number) merchant.get("disputeRate")).doubleValue() * 100,
                    merchant.get("riskLevel"),
                    ((Number) deliveryPerf.get("onTimeRate")).doubleValue() * 100,
                    deliveryPerf.get("averageDelay"),
                    notes);
            
            // Create metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("name", name);
            metadata.put("merchantCategory", category);
            metadata.put("disputeRate", merchant.get("disputeRate"));
            metadata.put("riskLevel", merchant.get("riskLevel"));
            
            // Store document
            RagDocument doc = new RagDocument(merchantId, content, embedding, "MERCHANT", metadata);
            vectorStore.addDocument(doc);
        }
        
        logger.info("Loaded {} merchants", merchants.size());
    }
}

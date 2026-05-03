package com.demo.dispute.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory vector store for RAG documents.
 * Stores documents with their embeddings and provides semantic search.
 */
@Component
public class InMemoryVectorStore {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryVectorStore.class);

    private final Map<String, RagDocument> documents = new ConcurrentHashMap<>();
    private final EmbeddingService embeddingService;

    public InMemoryVectorStore(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    /**
     * Add a document to the vector store.
     */
    public void addDocument(RagDocument document) {
        documents.put(document.getId(), document);
        logger.debug("Added document: {} (category: {})", document.getId(), document.getCategory());
    }

    /**
     * Add multiple documents to the vector store.
     */
    public void addDocuments(List<RagDocument> docs) {
        docs.forEach(this::addDocument);
        logger.info("Added {} documents to vector store", docs.size());
    }

    /**
     * Search for similar documents using semantic similarity.
     * 
     * @param query The search query text
     * @param topK Number of top results to return
     * @param category Optional category filter (null for all categories)
     * @param minSimilarity Minimum similarity threshold (0.0 to 1.0)
     * @return List of search results sorted by similarity (highest first)
     */
    public List<RagSearchResult> search(String query, int topK, String category, double minSimilarity) {
        if (documents.isEmpty()) {
            logger.warn("Vector store is empty. No documents to search.");
            return Collections.emptyList();
        }

        // Generate embedding for query
        float[] queryEmbedding = embeddingService.embed(query);

        // Filter by category if specified
        Collection<RagDocument> searchDocs = documents.values();
        if (category != null && !category.isEmpty()) {
            searchDocs = documents.values().stream()
                    .filter(doc -> category.equals(doc.getCategory()))
                    .collect(Collectors.toList());
            logger.debug("Filtered to {} documents in category: {}", searchDocs.size(), category);
        }

        // Calculate similarity scores
        List<RagSearchResult> results = searchDocs.stream()
                .map(doc -> {
                    double similarity = embeddingService.cosineSimilarity(queryEmbedding, doc.getEmbedding());
                    return new RagSearchResult(doc, similarity);
                })
                .filter(result -> result.getSimilarityScore() >= minSimilarity)
                .sorted(Comparator.comparingDouble(RagSearchResult::getSimilarityScore).reversed())
                .limit(topK)
                .collect(Collectors.toList());

        logger.debug("Found {} results for query (topK={}, minSimilarity={})", 
                results.size(), topK, minSimilarity);

        return results;
    }

    /**
     * Search with default parameters (topK=5, no category filter, minSimilarity=0.0).
     */
    public List<RagSearchResult> search(String query) {
        return search(query, 5, null, 0.0);
    }

    /**
     * Search with topK parameter.
     */
    public List<RagSearchResult> search(String query, int topK) {
        return search(query, topK, null, 0.0);
    }

    /**
     * Get document by ID.
     */
    public Optional<RagDocument> getDocument(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    /**
     * Get all documents in a category.
     */
    public List<RagDocument> getDocumentsByCategory(String category) {
        return documents.values().stream()
                .filter(doc -> category.equals(doc.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * Get total number of documents.
     */
    public int size() {
        return documents.size();
    }

    /**
     * Clear all documents.
     */
    public void clear() {
        documents.clear();
        logger.info("Cleared vector store");
    }

    /**
     * Get statistics about the vector store.
     */
    public Map<String, Object> getStats() {
        Map<String, Long> categoryCount = documents.values().stream()
                .collect(Collectors.groupingBy(RagDocument::getCategory, Collectors.counting()));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDocuments", documents.size());
        stats.put("categoryCounts", categoryCount);
        return stats;
    }
}

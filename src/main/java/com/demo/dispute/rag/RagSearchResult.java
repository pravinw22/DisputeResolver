package com.demo.dispute.rag;

/**
 * Represents a search result from the RAG system with similarity score.
 */
public class RagSearchResult {
    private final RagDocument document;
    private final double similarityScore;

    public RagSearchResult(RagDocument document, double similarityScore) {
        this.document = document;
        this.similarityScore = similarityScore;
    }

    public RagDocument getDocument() {
        return document;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }

    public String getId() {
        return document.getId();
    }

    public String getContent() {
        return document.getContent();
    }

    public String getCategory() {
        return document.getCategory();
    }

    @Override
    public String toString() {
        return "RagSearchResult{" +
                "id='" + document.getId() + '\'' +
                ", category='" + document.getCategory() + '\'' +
                ", similarityScore=" + String.format("%.4f", similarityScore) +
                '}';
    }
}

package com.demo.dispute.rag;

import java.util.Map;

/**
 * Represents a document in the RAG system with its embedding and metadata.
 */
public class RagDocument {
    private final String id;
    private final String content;
    private final float[] embedding;
    private final String category;
    private final Map<String, Object> metadata;

    public RagDocument(String id, String content, float[] embedding, String category, Map<String, Object> metadata) {
        this.id = id;
        this.content = content;
        this.embedding = embedding;
        this.category = category;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public String getCategory() {
        return category;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Object getMetadata(String key) {
        return metadata != null ? metadata.get(key) : null;
    }
}

package com.demo.dispute.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for generating text embeddings using local embedding model.
 * Uses all-MiniLM-L6-v2 model (384 dimensions) for semantic similarity.
 */
@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingService() {
        // Initialize local embedding model (no API key needed)
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    }

    /**
     * Generate embedding for a single text.
     */
    public float[] embed(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }
        
        Embedding embedding = embeddingModel.embed(text).content();
        return embedding.vector();
    }

    /**
     * Generate embeddings for multiple texts.
     */
    public List<float[]> embedAll(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            throw new IllegalArgumentException("Texts list cannot be null or empty");
        }
        
        return texts.stream()
                .map(this::embed)
                .toList();
    }

    /**
     * Calculate cosine similarity between two embeddings.
     */
    public double cosineSimilarity(float[] embedding1, float[] embedding2) {
        if (embedding1.length != embedding2.length) {
            throw new IllegalArgumentException("Embeddings must have same dimensions");
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
            norm1 += embedding1[i] * embedding1[i];
            norm2 += embedding2[i] * embedding2[i];
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}

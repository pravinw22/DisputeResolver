package com.demo.dispute.service;

/**
 * Unified interface for LLM services (Anthropic Claude or Ollama).
 * Implementations provide chat completion functionality.
 */
public interface LlmService {
    
    /**
     * Send a chat request with system prompt and user message.
     * 
     * @param systemPrompt The system-level instructions for the LLM
     * @param userMessage The user's message/query
     * @return The LLM's response text
     */
    String chat(String systemPrompt, String userMessage);
    
    /**
     * Get the name of the LLM provider.
     * 
     * @return Provider name (e.g., "Anthropic Claude", "Ollama")
     */
    String getProviderName();
}

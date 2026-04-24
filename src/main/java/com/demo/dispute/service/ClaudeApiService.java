package com.demo.dispute.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "llm.provider", havingValue = "anthropic", matchIfMissing = true)
public class ClaudeApiService implements LlmService {

    @Value("${anthropic.api.key:}")
    private String apiKey;

    @Value("${anthropic.api.url:https://api.anthropic.com/v1/messages}")
    private String apiUrl;

    @Value("${anthropic.model:claude-sonnet-4-20250514}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ClaudeApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String chat(String systemPrompt, String userMessage) {
        // Check if API key is configured
        if (apiKey == null || apiKey.isEmpty()) {
            return "API key not configured. Please set ANTHROPIC_API_KEY environment variable.";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("max_tokens", 1024);
        body.put("system", systemPrompt);
        body.put("messages", List.of(Map.of("role", "user", "content", userMessage)));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
            return (String) content.get(0).get("text");
        } catch (Exception e) {
            // For demo: return a fallback string so flow continues
            return "Agent response unavailable: " + e.getMessage();
        }
    }

    @Override
    public String getProviderName() {
        return "Anthropic Claude (" + model + ")";
    }
}

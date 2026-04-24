package com.demo.dispute.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "llm.provider", havingValue = "ollama")
public class OllamaApiService implements LlmService {

    @Value("${ollama.api.url:http://localhost:11434/api/generate}")
    private String apiUrl;

    @Value("${ollama.model:llama2}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OllamaApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String chat(String systemPrompt, String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Combine system prompt and user message for Ollama
        String combinedPrompt = systemPrompt + "\n\n" + userMessage;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("prompt", combinedPrompt);
        body.put("stream", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            return (String) response.getBody().get("response");
        } catch (Exception e) {
            // For demo: return a fallback string so flow continues
            return "Ollama response unavailable: " + e.getMessage();
        }
    }

    @Override
    public String getProviderName() {
        return "Ollama (" + model + ")";
    }
}

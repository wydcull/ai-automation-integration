package com.firstaiAutomationSystem.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstaiAutomationSystem.project.model.AiTriageResult;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAiTriageService {

    private final OpenAIClient openAIClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public OpenAiTriageService(
            OpenAIClient openAIClient,
            @Value("${groq.model}") String model,
            ObjectMapper objectMapper) {
        this.openAIClient = openAIClient;
        this.model = model;
        this.objectMapper = objectMapper;
    }

    public AiTriageResult triage(String subject, String body, String candidateName) {
        // Build personalized greeting if name is available
        String nameContext = (candidateName != null && !candidateName.isBlank())
                ? "\n\nCandidate name from document: " + candidateName
                : "";

        String prompt = """
            You are a business email triage assistant.
            Analyze the email and respond ONLY with valid JSON (no markdown, no extra text):
            {
              "category": "BILLING|TECHNICAL|JOB_APPLICATION|COMPLAINT|ORDER_STATUS|GENERAL|ENQUIRY",
              "priority": "LOW|MEDIUM|HIGH",
              "summary": "one sentence summary",
              "draftReply": "professional reply draft",
              "extractedData": {
                "key": "value"
              }
            }
            
            IMPORTANT: If a candidate name is provided below, use it in the draft reply greeting.
            For example: "Dear [Name]," instead of "Dear applicant,"%s

            Subject: %s
            Body: %s
            """.formatted(nameContext, subject, body);

        // Rest stays the same...
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(model)
                .addUserMessage(prompt)
                .temperature(0.3)
                .build();

        ChatCompletion completion = openAIClient.chat().completions().create(params);

        String content = completion.choices().stream()
                .findFirst()
                .flatMap(choice -> choice.message().content())
                .orElseThrow(() -> new IllegalStateException("OpenAI returned empty response"));

        return parseAiResponse(content);
    }

    private AiTriageResult parseAiResponse(String content) {
        try {
            String json = content.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("(?s)^```(?:json)?\\s*", "")
                        .replaceAll("```\\s*$", "")
                        .trim();
            }
            return objectMapper.readValue(json, AiTriageResult.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse OpenAI response: " + content, e);
        }
    }
}
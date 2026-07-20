package com.firstaiAutomationSystem.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstaiAutomationSystem.project.model.DocumentExtractionResult;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiDocumentExtractionService {

    private final OpenAIClient openAIClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public AiDocumentExtractionService(
            OpenAIClient openAIClient,
            @Value("${groq.model}") String model,
            ObjectMapper objectMapper) {
        this.openAIClient = openAIClient;
        this.model = model;
        this.objectMapper = objectMapper;
    }

    public DocumentExtractionResult extractStructuredData(String documentText) {
        if (documentText == null || documentText.isBlank()) {
            return null;
        }

        // Limit text to avoid token limits (adjust as needed)
        String truncatedText = documentText.length() > 6000
                ? documentText.substring(0, 6000)
                : documentText;

        String prompt = """
                You are a document analysis AI. Analyze the following document and extract relevant information.
                
                First, identify the document type (INVOICE, RESUME, LETTER, CONTRACT, REPORT, FORM, CERTIFICATE, or OTHER).
                Then extract the most relevant fields based on the document type.
                
                Respond ONLY with valid JSON (no markdown, no extra text):
                {
                  "documentType": "INVOICE|RESUME|LETTER|CONTRACT|REPORT|FORM|CERTIFICATE|OTHER",
                  "summary": "brief 1-2 sentence summary",
                  "extractedFields": {
                    ... relevant fields based on document type ...
                  }
                }
                
                Examples of extractedFields by type:
                
                INVOICE: {"invoiceNumber": "...", "vendorName": "...", "totalAmount": "...", "invoiceDate": "...", "dueDate": "..."}
                
                RESUME: {"candidateName": "...", "email": "...", "phone": "...", "experience": "X years", "currentRole": "...", "skills": ["skill1", "skill2"], "education": "..."}
                
                LETTER: {"sender": "...", "recipient": "...", "date": "...", "subject": "...", "purpose": "..."}
                
                CONTRACT: {"contractNumber": "...", "parties": "...", "effectiveDate": "...", "expiryDate": "...", "value": "..."}
                
                REPORT: {"reportTitle": "...", "author": "...", "date": "...", "keyFindings": "...", "recommendations": "..."}
                
                FORM: {"formType": "...", "applicantName": "...", "date": "...", relevant form fields...}
                
                CERTIFICATE: {"certificateType": "...", "recipientName": "...", "issueDate": "...", "issuingAuthority": "...", "validUntil": "..."}
                
                Extract all relevant fields you can find. If a field is not present, omit it from extractedFields.
                
                Document text:
                %s
                """.formatted(truncatedText);

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(model)
                .addUserMessage(prompt)
                .temperature(0.2)
                .build();

        ChatCompletion completion = openAIClient.chat().completions().create(params);

        String content = completion.choices().stream()
                .findFirst()
                .flatMap(choice -> choice.message().content())
                .orElseThrow(() -> new IllegalStateException("AI returned empty response"));

        return parseDocumentResponse(content);
    }

    private DocumentExtractionResult parseDocumentResponse(String content) {
        try {
            String json = content.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("(?s)^```(?:json)?\\s*", "")
                        .replaceAll("```\\s*$", "")
                        .trim();
            }
            return objectMapper.readValue(json, DocumentExtractionResult.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse document extraction response: " + content, e);
        }
    }
}
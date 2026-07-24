package com.firstaiAutomationSystem.project.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GmailService {

    private final GmailAuthService gmailAuthService;
    private final EmailTriageService emailTriageService;

    @Value("${gmail.user:me}")
    private String user;

    @Value("${gmail.processed.label:AI_PROCESSED}")
    private String processedLabel;

    @Value("${gmail.auto.reply.enabled:false}")
    private boolean autoReplyEnabled;

    @Value("${gmail.auto.reply.categories:}")
    private String autoReplyCategories;

    public GmailService(GmailAuthService gmailAuthService, EmailTriageService emailTriageService) {
        this.gmailAuthService = gmailAuthService;
        this.emailTriageService = emailTriageService;
    }

    public List<String> fetchAndProcessUnreadEmails() throws IOException, GeneralSecurityException {
        Gmail service = gmailAuthService.getGmailService();
        List<String> processedIds = new ArrayList<>();

        // List unread messages
        ListMessagesResponse response = service.users().messages()
                .list(user)
                .setQ("is:unread")
                .setMaxResults(10L)
                .execute();

        List<Message> messages = response.getMessages();
        if (messages == null || messages.isEmpty()) {
            return processedIds;
        }

        for (Message message : messages) {
            try {
                processEmail(service, message.getId());
                processedIds.add(message.getId());
            } catch (Exception e) {
                System.err.println("Error processing message " + message.getId() + ": " + e.getMessage());
            }
        }

        return processedIds;
    }

    private void processEmail(Gmail service, String messageId) throws IOException {
        // Get full message
        Message message = service.users().messages()
                .get(user, messageId)
                .setFormat("full")
                .execute();

        // Extract email details
        String senderEmail = getHeader(message, "From");
        String subject = getHeader(message, "Subject");
        String body = getEmailBody(message);
        String threadId = message.getThreadId();

        // Check for PDF attachments
        MultipartFile pdfAttachment = null;
        if (message.getPayload() != null && message.getPayload().getParts() != null) {
            for (MessagePart part : message.getPayload().getParts()) {
                if (isPdfAttachment(part)) {
                    pdfAttachment = downloadAttachment(service, messageId, part);
                    break; // Process only first PDF for now
                }
            }
        }

        // Process with existing EmailTriageService
        com.firstaiAutomationSystem.project.model.EmailTriageRequest request =
                new com.firstaiAutomationSystem.project.model.EmailTriageRequest(
                        senderEmail, subject, body
                );

        com.firstaiAutomationSystem.project.model.EmailTriageResponse response =
                emailTriageService.processFromGmail(request, pdfAttachment, messageId, threadId);

        // AUTO-REPLY LOGIC - Add this section
        if (autoReplyEnabled && shouldAutoReply(response.category())) {
            try {
                sendReply(
                        senderEmail,
                        subject,
                        response.draftReply(),
                        threadId
                );

                // Update database to mark reply as sent
                emailTriageService.markReplySent(response.id());

                System.out.println("Auto-reply sent to: " + senderEmail +
                        " (Category: " + response.category() + ")");
            } catch (Exception e) {
                System.err.println("Failed to send auto-reply: " + e.getMessage());
            }
        }

        // Mark as read and apply label
        markAsProcessed(service, messageId);

        System.out.println("Processed email: " + messageId + " - Subject: " + subject);
    }

    // Add this helper method
    private boolean shouldAutoReply(String category) {
        if (autoReplyCategories == null || autoReplyCategories.isBlank()) {
            return false; // No categories configured = no auto-reply
        }

        String[] allowedCategories = autoReplyCategories.split(",");
        for (String allowed : allowedCategories) {
            if (allowed.trim().equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    private String getHeader(Message message, String headerName) {
        if (message.getPayload() != null && message.getPayload().getHeaders() != null) {
            for (MessagePartHeader header : message.getPayload().getHeaders()) {
                if (headerName.equalsIgnoreCase(header.getName())) {
                    return header.getValue();
                }
            }
        }
        return "";
    }

    private String getEmailBody(Message message) {
        if (message.getPayload() == null) return "";

        MessagePart payload = message.getPayload();
        String mimeType = payload.getMimeType();

        // Handle plain text
        if ("text/plain".equals(mimeType) && payload.getBody() != null && payload.getBody().getData() != null) {
            return new String(Base64.decodeBase64(payload.getBody().getData()));
        }

        // Handle multipart
        if (payload.getParts() != null) {
            for (MessagePart part : payload.getParts()) {
                if ("text/plain".equals(part.getMimeType()) && part.getBody() != null && part.getBody().getData() != null) {
                    return new String(Base64.decodeBase64(part.getBody().getData()));
                }
            }
        }

        return "";
    }

    private boolean isPdfAttachment(MessagePart part) {
        return part.getFilename() != null &&
                !part.getFilename().isEmpty() &&
                part.getFilename().toLowerCase().endsWith(".pdf") &&
                part.getBody() != null &&
                part.getBody().getAttachmentId() != null;
    }

    private MultipartFile downloadAttachment(Gmail service, String messageId, MessagePart part) throws IOException {
        String attachmentId = part.getBody().getAttachmentId();
        MessagePartBody attachmentBody = service.users().messages().attachments()
                .get(user, messageId, attachmentId)
                .execute();

        byte[] data = Base64.decodeBase64(attachmentBody.getData());
        return new InMemoryMultipartFile(part.getFilename(), data);
    }

    private void markAsProcessed(Gmail service, String messageId) throws IOException {
        // Mark as read
        ModifyMessageRequest modifyRequest = new ModifyMessageRequest()
                .setRemoveLabelIds(List.of("UNREAD"));
        service.users().messages().modify(user, messageId, modifyRequest).execute();
    }

    public void sendReply(String toEmail, String subject, String body, String threadId) throws IOException, GeneralSecurityException {
        Gmail service = gmailAuthService.getGmailService();

        String rawMessage = createEmail(toEmail, subject, body, threadId);
        Message message = new Message().setRaw(rawMessage).setThreadId(threadId);

        service.users().messages().send(user, message).execute();
    }

    private String createEmail(String to, String subject, String bodyText, String threadId) {
        String email = "To: " + to + "\r\n" +
                "Subject: Re: " + subject + "\r\n" +
                (threadId != null ? "In-Reply-To: " + threadId + "\r\n" : "") +
                "\r\n" + bodyText;

        return Base64.encodeBase64URLSafeString(email.getBytes());
    }

    // Inner class for in-memory multipart file
    private static class InMemoryMultipartFile implements MultipartFile {
        private final String name;
        private final byte[] content;

        public InMemoryMultipartFile(String name, byte[] content) {
            this.name = name;
            this.content = content;
        }

        @Override
        public String getName() { return "file"; }

        @Override
        public String getOriginalFilename() { return name; }

        @Override
        public String getContentType() { return "application/pdf"; }

        @Override
        public boolean isEmpty() { return content == null || content.length == 0; }

        @Override
        public long getSize() { return content.length; }

        @Override
        public byte[] getBytes() { return content; }

        @Override
        public java.io.InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(java.io.File dest) throws IOException {
            java.nio.file.Files.write(dest.toPath(), content);
        }
    }
}
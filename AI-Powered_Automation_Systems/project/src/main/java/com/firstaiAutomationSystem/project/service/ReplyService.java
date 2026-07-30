package com.firstaiAutomationSystem.project.service;

import com.firstaiAutomationSystem.project.model.EmailTriageRecord;
import com.firstaiAutomationSystem.project.repository.EmailTriageRepository;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReplyService {

    private final EmailTriageRepository repository;
    private final GmailAuthService gmailAuthService;
    private final AuditService auditService;
    private static final Logger log = LoggerFactory.getLogger(ReplyService.class);

    public ReplyService(EmailTriageRepository repository,
                        GmailAuthService gmailAuthService,
                        AuditService auditService) {
        this.repository = repository;
        this.gmailAuthService = gmailAuthService;
        this.auditService = auditService;
    }

    @Transactional
    public EmailTriageRecord approveReply(Long id, String approvedBy) {
        EmailTriageRecord record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Email not found: " + id));

        if (Boolean.TRUE.equals(record.getReplySent())) {
            throw new IllegalStateException("Reply already sent");
        }

        record.setApproved(true);
        record.setApprovedBy(approvedBy);
        record.setApprovedAt(LocalDateTime.now());
        record.setRejected(false);

        EmailTriageRecord saved = repository.save(record);
        auditService.logEmailApproval(id, approvedBy);
        log.info("Reply approved: emailId={}, approvedBy={}", id, approvedBy);
        return saved;
    }

    @Transactional
    public EmailTriageRecord rejectReply(Long id, String rejectedBy, String reason) {
        EmailTriageRecord record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Email not found: " + id));

        if (Boolean.TRUE.equals(record.getReplySent())) {
            throw new IllegalStateException("Reply already sent");
        }

        record.setRejected(true);
        record.setRejectedBy(rejectedBy);
        record.setRejectionReason(reason);
        record.setApproved(false);

        EmailTriageRecord saved = repository.save(record);
        auditService.logEmailRejection(id, rejectedBy, reason);
        log.warn("Reply rejected: emailId={}, rejectedBy={}, reason={}", id, rejectedBy, reason);

        return saved;
    }

    @Transactional
    public EmailTriageRecord sendReply(Long id) throws Exception {
        EmailTriageRecord record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Email not found: " + id));

        if (Boolean.TRUE.equals(record.getReplySent())) {
            throw new IllegalStateException("Reply already sent");
        }

        if (!Boolean.TRUE.equals(record.getApproved())) {
            throw new IllegalStateException("Reply must be approved before sending");
        }

        Gmail gmail = gmailAuthService.getGmailService();

        MimeMessage email = createEmail(
                record.getSenderEmail(),
                "me",
                record.getSubject(),
                record.getDraftReply()
        );

        Message message = sendMessage(gmail, "me", email);

        record.setReplySent(true);
        record.setReplyAt(LocalDateTime.now());
        record.setReplyMessageId(message.getId());

        EmailTriageRecord saved = repository.save(record);
        auditService.logReplySent(id, record.getApprovedBy(), message.getId());
        log.info("Reply sent: emailId={}, gmailMessageId={}", id, message.getId());

        return saved;
    }

    @Transactional(readOnly = true)
    public List<EmailTriageRecord> getPendingApprovals() {
        return repository.findAll().stream()
                .filter(r -> !Boolean.TRUE.equals(r.getApproved())
                        && !Boolean.TRUE.equals(r.getRejected())
                        && !Boolean.TRUE.equals(r.getReplySent()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmailTriageRecord> getApprovedNotSent() {
        return repository.findAll().stream()
                .filter(r -> Boolean.TRUE.equals(r.getApproved())
                        && !Boolean.TRUE.equals(r.getReplySent()))
                .toList();
    }

    @Transactional
    public EmailTriageRecord editDraftReply(Long id, String newDraftReply) {
        EmailTriageRecord record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Email not found: " + id));

        if (Boolean.TRUE.equals(record.getReplySent())) {
            throw new IllegalStateException("Cannot edit - reply already sent");
        }

        record.setDraftReply(newDraftReply);
        log.info("Draft reply edited: emailId={}", id);
        return repository.save(record);
    }

    // Helper methods
    private MimeMessage createEmail(String to, String from, String subject, String bodyText)
            throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject("Re: " + subject);
        email.setText(bodyText);
        return email;
    }

    private Message sendMessage(Gmail service, String userId, MimeMessage email)
            throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return service.users().messages().send(userId, message).execute();
    }
}
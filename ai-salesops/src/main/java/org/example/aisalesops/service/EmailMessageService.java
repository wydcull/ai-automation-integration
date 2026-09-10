package org.example.aisalesops.service;

import org.example.aisalesops.entity.EmailMessage;
import org.example.aisalesops.repository.EmailMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmailMessageService {
    private final EmailMessageRepository emailMessageRepository;


    // =========================================
// CONSTRUCTOR
// =========================================
    public EmailMessageService(
            EmailMessageRepository emailMessageRepository
    ) {
        this.emailMessageRepository = emailMessageRepository;
    }


    // =========================================
// CREATE EMAIL MESSAGE - POST
// =========================================
    public EmailMessage createEmailMessage(
            EmailMessage emailMessage
    ) {

        return emailMessageRepository.save(emailMessage);
    }


    // =========================================
// GET ALL EMAIL MESSAGES - GET
// =========================================
    public List<EmailMessage> getAllEmailMessages() {

        return emailMessageRepository.findAll();
    }


    // =========================================
// GET EMAIL MESSAGE BY ID - GET
// =========================================
    public Optional<EmailMessage> getEmailMessageById(
            Long id
    ) {

        return emailMessageRepository.findById(id);
    }


    // =========================================
// FULL UPDATE - PUT
// =========================================
    @Transactional
    public EmailMessage updateEmailMessage(
            Long id,
            EmailMessage updatedEmailMessage
    ) {

        // Find existing record
        EmailMessage existingEmailMessage =
                emailMessageRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Email Message not found with id: " + id
                                )
                        );


        // PUT = Full Update
        existingEmailMessage.setExternalMessageId(
                updatedEmailMessage.getExternalMessageId()
        );

        existingEmailMessage.setSender(
                updatedEmailMessage.getSender()
        );

        existingEmailMessage.setRecipient(
                updatedEmailMessage.getRecipient()
        );

        existingEmailMessage.setSubject(
                updatedEmailMessage.getSubject()
        );

        existingEmailMessage.setBodyText(
                updatedEmailMessage.getBodyText()
        );

        existingEmailMessage.setReceivedAt(
                updatedEmailMessage.getReceivedAt()
        );

        existingEmailMessage.setProcessStatus(
                updatedEmailMessage.getProcessStatus()
        );

        existingEmailMessage.setErrorMessage(
                updatedEmailMessage.getErrorMessage()
        );

        existingEmailMessage.setRetryCount(
                updatedEmailMessage.getRetryCount()
        );

        existingEmailMessage.setProcessedAt(
                updatedEmailMessage.getProcessedAt()
        );


        // createdAt is not changed

        return emailMessageRepository.save(
                existingEmailMessage
        );
    }


    // =========================================
// PARTIAL UPDATE - PATCH
// =========================================
    @Transactional
    public EmailMessage partialUpdateEmailMessage(
            Long id,
            EmailMessage updatedEmailMessage
    ) {

        // Find existing record
        EmailMessage existingEmailMessage =
                emailMessageRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Email Message not found with id: " + id
                                )
                        );


        // Update External Message ID
        if (updatedEmailMessage.getExternalMessageId() != null) {

            existingEmailMessage.setExternalMessageId(
                    updatedEmailMessage.getExternalMessageId()
            );
        }


        // Update Sender
        if (updatedEmailMessage.getSender() != null) {

            existingEmailMessage.setSender(
                    updatedEmailMessage.getSender()
            );
        }


        // Update Recipient
        if (updatedEmailMessage.getRecipient() != null) {

            existingEmailMessage.setRecipient(
                    updatedEmailMessage.getRecipient()
            );
        }


        // Update Subject
        if (updatedEmailMessage.getSubject() != null) {

            existingEmailMessage.setSubject(
                    updatedEmailMessage.getSubject()
            );
        }


        // Update Body Text
        if (updatedEmailMessage.getBodyText() != null) {

            existingEmailMessage.setBodyText(
                    updatedEmailMessage.getBodyText()
            );
        }


        // Update Received At
        if (updatedEmailMessage.getReceivedAt() != null) {

            existingEmailMessage.setReceivedAt(
                    updatedEmailMessage.getReceivedAt()
            );
        }


        // Update Process Status
        if (updatedEmailMessage.getProcessStatus() != null) {

            existingEmailMessage.setProcessStatus(
                    updatedEmailMessage.getProcessStatus()
            );
        }


        // Update Error Message
        if (updatedEmailMessage.getErrorMessage() != null) {

            existingEmailMessage.setErrorMessage(
                    updatedEmailMessage.getErrorMessage()
            );
        }


        // Update Retry Count
        if (updatedEmailMessage.getRetryCount() != null) {

            existingEmailMessage.setRetryCount(
                    updatedEmailMessage.getRetryCount()
            );
        }


        // Update Processed At
        if (updatedEmailMessage.getProcessedAt() != null) {

            existingEmailMessage.setProcessedAt(
                    updatedEmailMessage.getProcessedAt()
            );
        }


        // createdAt is intentionally not updated

        return emailMessageRepository.save(
                existingEmailMessage
        );
    }


    // =========================================
// DELETE EMAIL MESSAGE - DELETE
// =========================================
    public void deleteEmailMessage(
            Long id
    ) {

        EmailMessage existingEmailMessage =
                emailMessageRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Email Message not found with id: " + id
                                )
                        );

        emailMessageRepository.delete(
                existingEmailMessage
        );
    }


}

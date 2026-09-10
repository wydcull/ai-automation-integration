package org.example.aisalesops.service;

import org.example.aisalesops.entity.Notification;
import org.example.aisalesops.repository.NotificationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;


    // =========================================
    // CONSTRUCTOR
    // =========================================
    public NotificationService(
            NotificationRepository notificationRepository
    ) {

        this.notificationRepository = notificationRepository;
    }


    // =========================================
    // CREATE NOTIFICATION - POST
    // =========================================
    public Notification createNotification(
            Notification notification
    ) {

        return notificationRepository.save(
                notification
        );
    }


    // =========================================
    // GET ALL NOTIFICATIONS - GET
    // =========================================
    public List<Notification> getAllNotifications() {

        return notificationRepository.findAll();
    }


    // =========================================
    // GET NOTIFICATION BY ID - GET
    // =========================================
    public Optional<Notification> getNotificationById(
            Long id
    ) {

        return notificationRepository.findById(
                id
        );
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================
    @Transactional
    public Notification updateNotification(
            Long id,
            Notification updatedNotification
    ) {

        Notification existingNotification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found with id: " + id
                                )
                        );


        existingNotification.setUserId(
                updatedNotification.getUserId()
        );

        existingNotification.setType(
                updatedNotification.getType()
        );

        existingNotification.setTitle(
                updatedNotification.getTitle()
        );

        existingNotification.setBody(
                updatedNotification.getBody()
        );

        existingNotification.setLeadId(
                updatedNotification.getLeadId()
        );

        existingNotification.setReadAt(
                updatedNotification.getReadAt()
        );


        // createdAt is not changed

        return notificationRepository.save(
                existingNotification
        );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================
    @Transactional
    public Notification partialUpdateNotification(
            Long id,
            Notification updatedNotification
    ) {

        Notification existingNotification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found with id: " + id
                                )
                        );


        // Update User ID
        if (updatedNotification.getUserId() != null) {

            existingNotification.setUserId(
                    updatedNotification.getUserId()
            );
        }


        // Update Type
        if (updatedNotification.getType() != null) {

            existingNotification.setType(
                    updatedNotification.getType()
            );
        }


        // Update Title
        if (updatedNotification.getTitle() != null) {

            existingNotification.setTitle(
                    updatedNotification.getTitle()
            );
        }


        // Update Body
        if (updatedNotification.getBody() != null) {

            existingNotification.setBody(
                    updatedNotification.getBody()
            );
        }


        // Update Lead ID
        if (updatedNotification.getLeadId() != null) {

            existingNotification.setLeadId(
                    updatedNotification.getLeadId()
            );
        }


        // Update Read At
        if (updatedNotification.getReadAt() != null) {

            existingNotification.setReadAt(
                    updatedNotification.getReadAt()
            );
        }


        // createdAt is intentionally not updated

        return notificationRepository.save(
                existingNotification
        );
    }


    // =========================================
    // DELETE NOTIFICATION - DELETE
    // =========================================
    public void deleteNotification(
            Long id
    ) {

        Notification existingNotification =
                notificationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found with id: " + id
                                )
                        );


        notificationRepository.delete(
                existingNotification
        );
    }

}
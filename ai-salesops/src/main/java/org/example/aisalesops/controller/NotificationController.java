package org.example.aisalesops.controller;

import org.example.aisalesops.entity.Notification;
import org.example.aisalesops.service.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;


    // =========================================
    // CONSTRUCTOR
    // =========================================
    public NotificationController(
            NotificationService notificationService
    ) {

        this.notificationService = notificationService;
    }


    // =========================================
    // CREATE - POST
    // =========================================
    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestBody Notification notification
    ) {

        Notification savedNotification =
                notificationService.createNotification(
                        notification
                );

        return ResponseEntity.ok(
                savedNotification
        );
    }


    // =========================================
    // GET ALL
    // =========================================
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {

        List<Notification> notifications =
                notificationService.getAllNotifications();

        return ResponseEntity.ok(
                notifications
        );
    }


    // =========================================
    // GET BY ID
    // =========================================
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(
            @PathVariable Long id
    ) {

        Optional<Notification> notification =
                notificationService.getNotificationById(id);

        return notification
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }


    // =========================================
    // FULL UPDATE - PUT
    // =========================================
    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotification(
            @PathVariable Long id,
            @RequestBody Notification notification
    ) {

        Notification updatedNotification =
                notificationService.updateNotification(
                        id,
                        notification
                );

        return ResponseEntity.ok(
                updatedNotification
        );
    }


    // =========================================
    // PARTIAL UPDATE - PATCH
    // =========================================
    @PatchMapping("/{id}")
    public ResponseEntity<Notification> partialUpdateNotification(
            @PathVariable Long id,
            @RequestBody Notification notification
    ) {

        Notification updatedNotification =
                notificationService.partialUpdateNotification(
                        id,
                        notification
                );

        return ResponseEntity.ok(
                updatedNotification
        );
    }


    // =========================================
    // DELETE
    // =========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long id
    ) {

        notificationService.deleteNotification(
                id
        );

        return ResponseEntity.noContent().build();
    }

}
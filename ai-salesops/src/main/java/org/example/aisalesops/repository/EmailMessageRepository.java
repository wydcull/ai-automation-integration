package org.example.aisalesops.repository;

import org.example.aisalesops.entity.EmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailMessageRepository
        extends JpaRepository<EmailMessage, Long> {

}
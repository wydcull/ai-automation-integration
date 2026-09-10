package org.example.aisalesops.repository;

import org.example.aisalesops.entity.EmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DashboardEmailRepository
        extends JpaRepository<EmailMessage, Long> {

    @Query("""
        SELECT COUNT(e)
        FROM EmailMessage e
        WHERE e.processStatus = 'PROCESSED'
    """)
    long countProcessedEmails();

    @Query("""
        SELECT COUNT(e)
        FROM EmailMessage e
        WHERE e.processStatus = 'FAILED'
    """)
    long countFailedEmails();

    @Query("""
        SELECT COUNT(e)
        FROM EmailMessage e
        WHERE e.processStatus = 'RECEIVED'
    """)
    long countReceivedEmails();

    @Query("""
        SELECT COUNT(e)
        FROM EmailMessage e
        WHERE e.processStatus = 'PROCESSING'
    """)
    long countProcessingEmails();
}
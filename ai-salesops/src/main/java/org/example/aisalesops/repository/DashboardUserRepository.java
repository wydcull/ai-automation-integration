package org.example.aisalesops.repository;

import org.example.aisalesops.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DashboardUserRepository extends JpaRepository<User, Long> {

    // Count all active users
    @Query("""
        SELECT COUNT(u)
        FROM User u
        WHERE u.active = true
    """)
    long countActiveUsers();


    // Count active managers
    @Query("""
        SELECT COUNT(u)
        FROM User u
        JOIN u.role r
        WHERE u.active = true
        AND r.code = 'MANAGER'
    """)
    long countActiveManagers();


    // Count active sales representatives
    @Query("""
        SELECT COUNT(u)
        FROM User u
        JOIN u.role r
        WHERE u.active = true
        AND r.code = 'REP'
    """)
    long countActiveRepresentatives();


    // Count active admins
    @Query("""
        SELECT COUNT(u)
        FROM User u
        JOIN u.role r
        WHERE u.active = true
        AND r.code = 'ADMIN'
    """)
    long countActiveAdmins();

}
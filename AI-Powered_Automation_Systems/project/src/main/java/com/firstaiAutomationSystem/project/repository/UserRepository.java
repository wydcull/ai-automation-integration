package com.firstaiAutomationSystem.project.repository;

import com.firstaiAutomationSystem.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> {
    Optional<Object> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
    
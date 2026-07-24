package com.firstaiAutomationSystem.project.service;

import com.firstaiAutomationSystem.project.model.User;
import com.firstaiAutomationSystem.project.model.UserRole;
import com.firstaiAutomationSystem.project.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Transactional
    public User register(String username, String email, String password,
                         String fullName, String role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setRole(UserRole.valueOf(role));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        auditService.log("USER_REGISTERED", "USER", saved.getId(), null);
        return saved;
    }

    @Transactional
    public User authenticate(String username, String password) {
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!user.getEnabled()) {
            throw new IllegalArgumentException("Account disabled");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        auditService.log("USER_LOGIN", "USER", user.getId(), null);
        return user;
    }

    public User findByUsername(String username) {
        return (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
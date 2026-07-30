package com.firstaiAutomationSystem.project.controller;

import com.firstaiAutomationSystem.project.model.User;
import com.firstaiAutomationSystem.project.service.AuthService;
import com.firstaiAutomationSystem.project.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = authService.register(
                request.username(),
                request.email(),
                request.password(),
                request.fullName(),
                request.role()
        );
        log.info("Register request: username={}, role={}", request.username(), request.role());

        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "userId", user.getId()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = authService.authenticate(request.username(), request.password());
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        log.info("Login request: username={}", request.username());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", user.getUsername(),
                "role", user.getRole(),
                "fullName", user.getFullName()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // Get from SecurityContextHolder
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = authService.findByUsername(username);

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "fullName", user.getFullName()
        ));
    }

    record LoginRequest(String username, String password) {}
    record RegisterRequest(String username, String email, String password,
                           String fullName, String role) {}
}
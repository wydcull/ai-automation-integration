package org.example.aisalesops.controller;

import org.example.aisalesops.entity.LoginRequest;
import org.example.aisalesops.entity.LoginResponse;
import org.example.aisalesops.entity.User;
import org.example.aisalesops.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (isBlank(request.getUsername())
                || isBlank(request.getPassword())
                || isBlank(request.getRole())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "username, password and role are required"));
        }

        // username = email for now
        User user = userRepository.findByEmail(request.getUsername())
                .orElse(null);

        if (user == null || !Boolean.TRUE.equals(user.getActive())) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid credentials"));
        }

        // Phase 1 simple: plain compare OR BCrypt if you already hash
        // Prefer: passwordEncoder.matches(request.getPassword(), user.getPasswordHash())
        if (!user.getPasswordHash().equals(request.getPassword())) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid credentials"));
        }

        String dbRole = user.getRole().getCode(); // e.g. ADMIN / MANAGER / REP

        if (!dbRole.equalsIgnoreCase(request.getRole().trim())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Role does not match this user"));
        }

        LoginResponse res = new LoginResponse();
        res.setUserId(user.getId());
        res.setUsername(user.getEmail());
        res.setFullName(user.getFullName());
        res.setRole(dbRole);
        res.setMessage("Login successful");

        return ResponseEntity.ok(res);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

package org.example.aisalesops.entity;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;  // use email for now
    private String password;
    private String role;      // ADMIN | MANAGER | REP

    // getters/setters
}

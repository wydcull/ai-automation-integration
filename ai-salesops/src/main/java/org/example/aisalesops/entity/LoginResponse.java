package org.example.aisalesops.entity;

import lombok.Data;

@Data
public class LoginResponse {
    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private String message;

    // getters/setters + constructor
}
package com.example.dto;

import lombok.Data;

@Data
public class BaseLoginRequest {
    private String username;
    private String password;
    private String userType; // ADMIN, STUDENT, TEACHER
} 
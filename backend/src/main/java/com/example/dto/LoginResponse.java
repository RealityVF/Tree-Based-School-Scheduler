package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private String message;
    private LoginData loginData;
    private UserInfo userInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginData {
        private String token;
        private String role;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String id;
        private String username;
        private String name;
        private String role;
        private String classId;
        private String majorId;
    }

    public static LoginResponse success(String token, String username, String role) {
        return LoginResponse.builder()
                .token(token)
                .username(username)
                .role(role)
                .message("登录成功")
                .build();
    }
    
    public static LoginResponse error(String message) {
        return LoginResponse.builder()
                .message(message)
                .build();
    }
} 
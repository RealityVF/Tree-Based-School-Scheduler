package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.dto.LoginResponse;
import com.example.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private com.example.util.JwtUtil jwtUtil;

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", System.currentTimeMillis()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest.getUsername(), loginRequest.getPassword())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String bearerToken) {
        try {
            // 从 Authorization 头中提取令牌
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "无效的Authorization头"
                ));
            }
            
            String token = bearerToken.substring(7);
            
            // 检查令牌是否需要刷新
            if (!jwtUtil.shouldRefreshToken(token)) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "令牌仍然有效",
                    "token", token
                ));
            }
            
            // 刷新令牌
            String newToken = jwtUtil.refreshToken(token);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "令牌刷新成功",
                "token", newToken
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "令牌刷新失败：" + e.getMessage()
            ));
        }
    }
} 
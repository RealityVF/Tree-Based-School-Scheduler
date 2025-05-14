package com.example.service;

import com.example.dto.BaseLoginRequest;
import com.example.dto.LoginResponse;
import java.util.Optional;

public interface AuthService {
    LoginResponse login(BaseLoginRequest request);
    Optional<LoginResponse> login(String username, String password);
} 
package com.example.admr.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123"; // 这里输入您想要的密码
        String encodedPassword = encoder.encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("加密后的密码: " + encodedPassword);
    }
} 
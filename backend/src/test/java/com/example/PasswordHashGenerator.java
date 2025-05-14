package com.example;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 测试教师工号后6位
        String teacherId = "20231001001";
        String password = teacherId.substring(teacherId.length() - 6);
        String hash = encoder.encode(password);
        
        System.out.println("Teacher ID: " + teacherId);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
    }
} 
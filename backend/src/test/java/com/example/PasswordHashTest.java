package com.example;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 数据库中的哈希
        String dbHash = "$2a$10$7O2MNNp8oQXbO7sC1tMG3egDg1WVNeVnhL5lhaVTQ.oTew18zbzAW";
        
        // 测试各种可能的密码
        String[] possiblePasswords = {
            "100001",    // 直接的6位数字
            "010012",    // 特定教师ID的后6位
            "000000",    // 默认密码可能是全0
            "123456",    // 常见的简单密码
            "111111"     // 另一个可能的默认密码
        };
        
        System.out.println("验证密码与数据库哈希的匹配情况：");
        System.out.println("数据库哈希: " + dbHash);
        
        for (String password : possiblePasswords) {
            boolean matches = encoder.matches(password, dbHash);
            System.out.println("密码 '" + password + "' 匹配结果: " + matches);
            
            // 生成新的哈希，以便比较
            String newHash = encoder.encode(password);
            System.out.println("密码 '" + password + "' 生成的新哈希: " + newHash);
            System.out.println("------------------------------");
        }
    }
} 
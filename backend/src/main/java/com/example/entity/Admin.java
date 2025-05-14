package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Admin {
    private String id;              // 管理员ID
    private String passwordHash;    // 密码哈希
    private String email;          // 邮箱
    private String role;           // 角色
    private String departmentId;   // 部门ID
    private Boolean isActive;      // 是否激活
    private LocalDateTime lastLogin;         // 最后登录时间
    private LocalDateTime passwordUpdatedAt; // 密码更新时间
    private LocalDateTime createdAt;         // 创建时间
    private LocalDateTime updatedAt;         // 更新时间
}
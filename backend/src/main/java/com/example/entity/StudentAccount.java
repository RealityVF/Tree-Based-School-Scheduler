package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student_accounts")
public class StudentAccount {
    @TableId(type = IdType.INPUT)
    private String studentId;
    private String username;
    private String name;
    private String password;
    private String passwordHash;
    private String email;
    private LocalDateTime lastLogin;
    private LocalDateTime lastLoginAt;
    private Boolean isActive;
    private Boolean enabled = true;
    private LocalDateTime passwordUpdatedAt;
    private String className;
    private String counselor;
    private String ethnicity;
    private String faculty;
    private String gender;
    private String grade;
    private String idCard;
    private String major;
    private String phone;
    private String politicalStatus;
} 
package com.example.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "teacher_accounts")
public class TeacherAccount {
    @Id
    @Pattern(regexp = "^\\d{11}$", message = "教师工号必须是11位数字")  // 格式：入职年份4位+部门号4位+序列号3位
    @Column(name = "teacher_id", length = 11)
    private String teacherId;

    @Column(name = "password_hash", length = 60, nullable = false)
    private String passwordHash;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "password_updated_at")
    private LocalDateTime passwordUpdatedAt;

    @PrePersist
    public void prePersist() {
        if (passwordUpdatedAt == null) {
            passwordUpdatedAt = LocalDateTime.now();
        }
    }
} 
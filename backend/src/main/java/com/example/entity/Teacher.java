package com.example.entity;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @Column(name = "id", length = 11)
    @Pattern(regexp = "^\\d{11}$", message = "教师工号必须是11位数字")  // 格式：入职年份4位+部门号4位+序列号3位
    private String id;                // 教师工号

    @Column(name = "name", length = 50, nullable = false)
    private String name;              // 姓名

    @Column(name = "english_name", length = 50)
    private String englishName;       // 英文姓名

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;            // 性别

    @Column(name = "ethnicity", length = 20)
    private String ethnicity;         // 民族

    @Column(name = "title", length = 20)
    private String title;             // 职称

    @Column(name = "department_id", length = 20, nullable = false)
    private String departmentId;      // 所属部门

    @Column(name = "is_external")
    private Boolean isExternal = false;  // 是否外聘

    @Column(name = "teacher_type", length = 20)
    private String teacherType;       // 教师类型

    @Column(name = "satisfaction")
    private Boolean satisfaction = true;  // 教室位置满意度

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 创建时间

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 更新时间

    @Column(name = "phone", length = 20)
    private String phone;             // 联系电话

    @Column(name = "email", length = 100)
    private String email;             // 电子邮箱

    @Column(name = "max_weekly_hours")
    private Integer maxWeeklyHours = 16;  // 最大周课时

    @Column(name = "research_direction", length = 200)
    private String researchDirection;  // 研究方向

    @Column(name = "is_active")
    private Integer isActive;          // 是否激活（0-禁用，1-启用）

    @Column(name = "last_login")
    private LocalDateTime lastLogin;   // 最后登录时间

    @Column(name = "password_hash", length = 60)
    private String passwordHash;       // 密码哈希

    @Column(name = "password_updated_at")
    private LocalDateTime passwordUpdatedAt; // 密码更新时间

    @Transient
    private String departmentName;     // 部门名称（非数据库字段）

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (maxWeeklyHours == null) {
            maxWeeklyHours = 16;
        }
        if (satisfaction == null) {
            satisfaction = true;
        }
        if (isExternal == null) {
            isExternal = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 性别枚举
    public enum Gender {
        男, 女
    }

    // 用于登录的用户名，返回教师工号
    public String getUsername() {
        return this.id;
    }

    // 用于显示的教师姓名
    public String getTeacherName() {
        return this.name;
    }

    // 用于获取教师工号
    public String getTeacherId() {
        return this.id;
    }
}
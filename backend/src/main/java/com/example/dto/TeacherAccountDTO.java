package com.example.dto;

import lombok.Data;

@Data
public class TeacherAccountDTO {
    // 基本信息
    private String teacherId;      // 工号
    private String teacherName;    // 姓名
    private String loginAccount;   // 登录账号
    private String password;       // 初始密码
    private String departmentId;   // 单位/部门ID
    private String title;          // 教职工类别
    private Boolean isActive;      // 账号状态

    // 扩展信息
    private String gender;         // 性别（男/女）
    private String ethnicity;      // 民族
    private String academicTitle;  // 职称
    private Boolean isForeigner;   // 是否外籍
    private String englishName;    // 英文姓名
    private String email;          // 邮箱
    private String phone;          // 电话
} 
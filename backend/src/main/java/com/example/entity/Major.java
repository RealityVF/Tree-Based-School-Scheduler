package com.example.entity;

import lombok.Data;

@Data
public class Major {
    private String majorId;      // 专业编号
    private String majorName;    // 专业名称
    private String departmentId; // 所属学院
} 
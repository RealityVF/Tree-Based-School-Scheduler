package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseSemesterRelation {
    private Integer relationId;    // 关系ID
    private String courseId;       // 课程编号
    private Integer semesterId;    // 学期ID
    private Boolean isActive;      // 是否有效
    private LocalDateTime createdAt; // 创建时间
} 
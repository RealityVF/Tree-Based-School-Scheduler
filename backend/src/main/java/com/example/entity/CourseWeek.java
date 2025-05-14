package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseWeek {
    private Integer weekId;        // 周表ID
    private String courseId;       // 课程编号
    private Integer weekNumber;    // 周次
    private Boolean isActive;      // 是否有效
    private LocalDateTime createdAt; // 创建时间
} 
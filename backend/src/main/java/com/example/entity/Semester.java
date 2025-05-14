package com.example.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Semester {
    private Integer semesterId;    // 学期ID
    private String semesterName;   // 学期名称
    private LocalDate startDate;   // 开始日期
    private LocalDate endDate;     // 结束日期
    private Boolean isCurrent;     // 是否当前学期
    private LocalDateTime createdAt; // 创建时间
} 
package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("courses")
public class Course {
    private String id;                  // 课程编号
    private String name;                // 课程名称
    private String category;            // 课程类别(A-纯理论课,B-理论+实践课,C-纯实践课,D-实验课,E-其他)
    private String attribute;           // 课程属性
    private String courseType;          // 课程类型(必修/选修/公共)
    private String nature;              // 课程性质
    private String departmentName;      // 开课院系
    private Boolean isActive;           // 是否启用
    private Integer totalHours;         // 总学时
    private Integer theoryHours;        // 理论学时
    private Integer experimentHours;    // 实验学时
    private Integer computerHours;      // 上机学时
    private Integer practiceHours;      // 实践学时
    private Double credit;              // 学分
    private Integer weeklyHours;        // 周学时
    private Boolean isPractical;        // 是否纯实践环节
    private LocalDateTime createdAt;    // 创建时间
} 
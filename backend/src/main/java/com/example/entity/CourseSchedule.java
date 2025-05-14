package com.example.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseSchedule {
    private Long id;                // 主键
    private String courseId;        // 课程编号
    private String classroomId;     // 教室编号
    private Integer weekday;        // 星期几(1-7)
    private Integer startSection;   // 开始节次
    private Integer endSection;     // 结束节次
    private String academicYear;    // 学年
    private Integer semester;       // 学期(1或2)
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间

    // 关联字段 - 不在数据库表中，由查询结果填充
    private String courseName;      // 课程名称
    private String courseType;      // 课程类型
    private String classroomName;   // 教室名称
    private String building;        // 教学楼
} 
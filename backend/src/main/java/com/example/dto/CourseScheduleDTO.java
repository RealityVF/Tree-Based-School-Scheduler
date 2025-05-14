package com.example.dto;

import lombok.Data;

@Data
public class CourseScheduleDTO {
    private Integer id;           // 主键
    private String courseId;      // 课程ID
    private String courseName;    // 课程名称
    private String courseType;    // 课程类型
    private String classroomId;   // 教室ID
    private String classroomName; // 教室名称
    private String buildingId;    // 教学楼
    private String teacherId;     // 教师ID
    private String teacherName;   // 教师姓名
    private String classId;       // 班级ID
    private String className;     // 班级名称
    private Integer weekday;      // 星期几(1-7)
    private Integer startSection; // 开始节次
    private Integer endSection;   // 结束节次
    private String startTime;     // 开始时间
    private String endTime;       // 结束时间
    private String academicYear;  // 学年
    private Integer semester;     // 学期(1或2)
    private Integer weekNumber;   // 周次
} 
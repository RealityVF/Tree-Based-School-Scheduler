package com.example.entity;

import lombok.Data;

@Data
public class Classroom {
    private String classroomId;    // 教室编号
    private String building;       // 教学楼
    private String roomNumber;     // 房间号
    private Integer capacity;      // 容纳人数
    private String equipmentInfo;  // 设备信息
} 
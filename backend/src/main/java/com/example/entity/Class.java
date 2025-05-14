package com.example.entity;

import lombok.Data;
import java.time.Year;

@Data
public class Class {
    private String classId;    // 班级编号
    private String className;  // 班级名称
    private String majorId;    // 所属专业
    private Year startYear;    // 入学年份
} 
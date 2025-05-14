package com.example.dto;

import com.example.entity.Teacher;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeacherDTO {
    private String id;
    private String name;
    private String englishName;
    private Teacher.Gender gender;
    private String ethnicity;
    private String title;
    private String departmentId;
    private Boolean isExternal;
    private String teacherType;
    private Boolean satisfaction;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String phone;
    private String email;
    private Integer maxWeeklyHours;
    private String researchDirection;
} 
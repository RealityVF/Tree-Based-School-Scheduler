package com.example.entity;

import lombok.Data;

@Data
public class Department {
    private String id;
    private String name;
    private String description;
    private String parentId;
    private Integer level;
    private Boolean isActive;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
} 
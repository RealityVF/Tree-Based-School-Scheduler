package com.example.service;

import com.example.entity.Class;
import java.util.List;

public interface ClassService {
    List<Class> getAllClasses();
    Class getClassById(String classId);
    List<Class> getClassesByMajorId(String majorId);
} 
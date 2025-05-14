package com.example.service;

import com.example.entity.Major;
import java.util.List;

public interface MajorService {
    List<Major> getAllMajors();
    Major getMajorById(String majorId);
    List<Major> getMajorsByDepartmentId(String departmentId);
} 
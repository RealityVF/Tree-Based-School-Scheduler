package com.example.mapper;

import com.example.entity.Department;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    
    /**
     * 获取所有部门列表
     */
    List<Department> getAllDepartments();
    
    /**
     * 根据ID获取部门
     */
    Department getDepartmentById(String id);
} 
package com.example.service;

import com.example.entity.Course;
import java.util.List;

public interface CourseService {
    
    // 获取课程列表
    List<Course> getCourseList(String keyword, String courseType, String departmentName, int page, int size);
    
    // 获取课程总数
    int countCourses(String keyword, String courseType, String departmentName);
    
    // 根据ID获取课程
    Course getCourseById(String id);
    
    // 根据课程编号列表批量获取课程
    List<Course> getCoursesByIds(List<String> ids);
    
    // 根据开课院系获取课程
    List<Course> getCoursesByDepartment(String departmentName);

    // 创建课程
    Course createCourse(Course course);

    // 更新课程
    Course updateCourse(Course course);

    // 删除课程
    boolean deleteCourse(String id);

    // 批量导入课程
    List<Course> batchImportCourses(List<Course> courses);
} 
package com.example.service.impl;

import com.example.entity.Course;
import com.example.mapper.CourseMapper;
import com.example.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class CourseServiceImpl implements CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<Course> getCourseList(String keyword, String courseType, String departmentName, int page, int size) {
        int offset = (page - 1) * size;
        return courseMapper.getCourseList(keyword, courseType, departmentName, offset, size);
    }

    @Override
    public int countCourses(String keyword, String courseType, String departmentName) {
        return courseMapper.countCourses(keyword, courseType, departmentName);
    }

    @Override
    public Course getCourseById(String id) {
        return courseMapper.getCourseById(id);
    }

    @Override
    public List<Course> getCoursesByIds(List<String> ids) {
        return courseMapper.getCoursesByIds(ids);
    }

    @Override
    public List<Course> getCoursesByDepartment(String departmentName) {
        return courseMapper.getCoursesByDepartment(departmentName);
    }

    @Override
    @Transactional
    public Course createCourse(Course course) {
        course.setCreatedAt(LocalDateTime.now());
        course.setIsActive(true);
        courseMapper.insert(course);
        return course;
    }

    @Override
    @Transactional
    public Course updateCourse(Course course) {
        logger.info("[课程服务] 开始更新课程, ID: {}", course.getId());
        
        Course existing = courseMapper.getCourseById(course.getId());
        if (existing == null) {
            logger.error("[课程服务] 课程不存在, ID: {}", course.getId());
            throw new RuntimeException("课程不存在");
        }
        
        logger.info("[课程服务] 原课程数据: {}", existing);
        logger.info("[课程服务] 更新的数据: {}", course);
        
        int result = courseMapper.update(course);
        logger.info("[课程服务] 数据库更新结果: {}", result > 0 ? "成功" : "失败");
        
        Course updated = courseMapper.getCourseById(course.getId());
        logger.info("[课程服务] 更新后的数据: {}", updated);
        
        return updated;
    }

    @Override
    @Transactional
    public boolean deleteCourse(String id) {
        Course existing = courseMapper.getCourseById(id);
        if (existing == null) {
            return false;
        }
        return courseMapper.delete(id) > 0;
    }

    @Override
    @Transactional
    public List<Course> batchImportCourses(List<Course> courses) {
        LocalDateTime now = LocalDateTime.now();
        for (Course course : courses) {
            course.setCreatedAt(now);
            course.setIsActive(true);
            courseMapper.insert(course);
        }
        return courses;
    }
} 
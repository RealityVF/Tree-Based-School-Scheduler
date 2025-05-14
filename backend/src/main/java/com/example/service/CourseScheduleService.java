package com.example.service;

import com.example.entity.CourseSchedule;
import com.example.dto.CourseScheduleDTO;
import java.util.List;

public interface CourseScheduleService {
    
    // 获取学生课表
    List<CourseScheduleDTO> getStudentSemesterSchedule(
        String classId, 
        String academicYear,
        Integer semester
    );
    
    // 获取教师课表
    List<CourseScheduleDTO> getTeacherSemesterSchedule(
        String teacherId, 
        String academicYear,
        Integer semester
    );
    
    // 获取课程详情
    CourseSchedule getCourseDetail(
        String courseId, 
        String academicYear,
        Integer semester
    );
    
    // 根据ID获取课程安排
    CourseSchedule getScheduleById(Integer id);
    
    // 根据教室和时间查询课程安排
    List<CourseSchedule> getSchedulesByClassroomAndTime(
        String classroomId, 
        String academicYear,
        Integer semester,
        Integer weekday, 
        Integer startSection, 
        Integer endSection
    );
    
    // 创建课程安排
    CourseSchedule createSchedule(CourseSchedule schedule);
    
    // 更新课程安排
    boolean updateSchedule(CourseSchedule schedule);
    
    // 删除课程安排
    boolean deleteSchedule(Integer id);
} 
package com.example.mapper;

import com.example.entity.CourseSchedule;
import com.example.dto.CourseScheduleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CourseScheduleMapper {
    CourseSchedule findById(@Param("id") Integer id);
    
    List<CourseSchedule> findByTeacherAndSemester(
        @Param("teacherId") String teacherId,
        @Param("academicYear") String academicYear,
        @Param("semester") Integer semester
    );
    
    List<CourseSchedule> findByClassAndSemester(
        @Param("classId") String classId,
        @Param("academicYear") String academicYear,
        @Param("semester") Integer semester
    );
    
    List<CourseSchedule> findByClassroomAndSemester(
        @Param("classroomId") String classroomId,
        @Param("academicYear") String academicYear,
        @Param("semester") Integer semester
    );

    /**
     * 查询学生课表
     */
    List<CourseScheduleDTO> findStudentSchedule(
        @Param("classId") String classId,
        @Param("academicYear") String academicYear,
        @Param("semester") Integer semester,
        @Param("weekNumber") Integer weekNumber
    );

    /**
     * 查询学生整个学期的课程表
     */
    List<CourseScheduleDTO> findStudentSemesterSchedule(
        @Param("classId") String classId,
        @Param("academicYear") String academicYear,
        @Param("semester") Integer semester
    );

    /**
     * 查询教师课表
     */
    List<CourseScheduleDTO> findTeacherSchedule(
        @Param("teacherId") String teacherId,
        @Param("academicYear") String academicYear,
        @Param("semester") Integer semester,
        @Param("weekNumber") Integer weekNumber
    );

    /**
     * 查询教师整个学期的课程表
     */
    List<CourseScheduleDTO> findTeacherSemesterSchedule(
        @Param("teacherId") String teacherId,
        @Param("academicYear") String academicYear,
        @Param("semester") Integer semester
    );
    
    /**
     * 获取课程详情
     */
    CourseSchedule getCourseDetail(
        @Param("courseId") String courseId,
        @Param("academicYear") String academicYear,
        @Param("semester") Integer semester
    );
    
    /**
     * 根据教室和时间查询课程安排
     */
    List<CourseSchedule> findByClassroomAndTime(
        @Param("classroomId") String classroomId,
        @Param("academicYear") String academicYear,
        @Param("semester") Integer semester,
        @Param("weekday") Integer weekday,
        @Param("startSection") Integer startSection,
        @Param("endSection") Integer endSection
    );
    
    /**
     * 插入课程安排
     */
    int insert(CourseSchedule schedule);
    
    /**
     * 更新课程安排
     */
    int update(CourseSchedule schedule);
    
    /**
     * 删除课程安排
     */
    int delete(@Param("id") Integer id);
} 
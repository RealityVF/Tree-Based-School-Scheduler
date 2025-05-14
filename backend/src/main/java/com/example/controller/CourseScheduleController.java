package com.example.controller;

import com.example.entity.CourseSchedule;
import com.example.dto.CourseScheduleDTO;
import com.example.service.CourseScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin
public class CourseScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(CourseScheduleController.class);

    @Autowired
    private CourseScheduleService courseScheduleService;

    /**
     * 获取学生课表
     */
    @GetMapping("/student")
    public ResponseEntity<?> getStudentSchedule(
            @RequestParam String classId,
            @RequestParam String academicYear,
            @RequestParam Integer semester) {
        logger.info("接收到获取学生课表请求 - 班级: {}, 学年: {}, 学期: {}", classId, academicYear, semester);
        try {
            List<CourseScheduleDTO> schedule = courseScheduleService.getStudentSemesterSchedule(classId, academicYear, semester);
            logger.info("成功获取学生课表 - 班级: {}, 记录数: {}", classId, schedule.size());
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            logger.error("获取学生课表失败 - 班级: " + classId, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * 获取教师课表
     */
    @GetMapping("/teacher")
    public ResponseEntity<?> getTeacherSchedule(
            @RequestParam String teacherId,
            @RequestParam String academicYear,
            @RequestParam Integer semester) {
        logger.info("接收到获取教师课表请求 - 教师: {}, 学年: {}, 学期: {}", teacherId, academicYear, semester);
        try {
            List<CourseScheduleDTO> schedule = courseScheduleService.getTeacherSemesterSchedule(teacherId, academicYear, semester);
            logger.info("成功获取教师课表 - 教师: {}, 记录数: {}", teacherId, schedule.size());
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            logger.error("获取教师课表失败 - 教师: " + teacherId, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCourseDetail(
            @PathVariable String courseId,
            @RequestParam String academicYear,
            @RequestParam Integer semester) {
        logger.info("接收到获取课程详情请求 - 课程: {}, 学年: {}, 学期: {}", courseId, academicYear, semester);
        try {
            CourseSchedule detail = courseScheduleService.getCourseDetail(courseId, academicYear, semester);
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            logger.error("获取课程详情失败 - 课程: " + courseId, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * 根据教室和时间查询课程安排
     */
    @GetMapping("/classroom")
    public ResponseEntity<?> getSchedulesByClassroomAndTime(
            @RequestParam String classroomId,
            @RequestParam String academicYear,
            @RequestParam Integer semester,
            @RequestParam Integer weekday,
            @RequestParam Integer startSection,
            @RequestParam Integer endSection) {
        logger.info("接收到查询教室课程安排请求 - 教室: {}, 学年: {}, 学期: {}, 星期: {}, 节次: {}-{}", 
                classroomId, academicYear, semester, weekday, startSection, endSection);
        try {
            List<CourseSchedule> schedules = courseScheduleService.getSchedulesByClassroomAndTime(
                    classroomId, academicYear, semester, weekday, startSection, endSection);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            logger.error("查询教室课程安排失败 - 教室: " + classroomId, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * 创建课程安排
     */
    @PostMapping
    public ResponseEntity<?> createSchedule(@RequestBody CourseSchedule schedule) {
        try {
            CourseSchedule created = courseScheduleService.createSchedule(schedule);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("创建课程安排失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * 更新课程安排
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Integer id,
            @RequestBody CourseSchedule schedule) {
        schedule.setId(Long.valueOf(id));
        try {
            boolean updated = courseScheduleService.updateSchedule(schedule);
            return ResponseEntity.ok(Map.of("success", updated));
        } catch (Exception e) {
            logger.error("更新课程安排失败 - ID: " + id, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * 删除课程安排
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Integer id) {
        try {
            boolean deleted = courseScheduleService.deleteSchedule(id);
            return ResponseEntity.ok(Map.of("success", deleted));
        } catch (Exception e) {
            logger.error("删除课程安排失败 - ID: " + id, e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }
}
package com.example.controller;

import com.example.entity.Course;
import com.example.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseService courseService;

    /**
     * 获取课程列表（分页）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCourseList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String courseType,
            @RequestParam(required = false) String departmentName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            List<Course> courses = courseService.getCourseList(keyword, courseType, departmentName, page, size);
            int total = courseService.countCourses(keyword, courseType, departmentName);

            Map<String, Object> response = Map.of(
                "success", true,
                "data", Map.of(
                    "courses", courses,
                    "total", total,
                    "page", page,
                    "size", size
                )
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 创建新课程
     */
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        try {
            Course created = courseService.createCourse(course);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", created
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 更新课程信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable String id, @RequestBody Course course) {
        try {
            logger.info("[课程更新] 开始更新课程, ID: {}", id);
            logger.info("[课程更新] 接收到的更新数据: {}", course);
            
            course.setId(id);
            Course updated = courseService.updateCourse(course);
            
            logger.info("[课程更新] 更新成功, 更新后的数据: {}", updated);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", updated
            ));
        } catch (Exception e) {
            logger.error("[课程更新] 更新失败, ID: {}, 错误: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable String id) {
        try {
            boolean deleted = courseService.deleteCourse(id);
            return ResponseEntity.ok(Map.of("success", deleted));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 根据ID获取课程
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable String id) {
        Course course = courseService.getCourseById(id);
        if (course != null) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", course
            ));
        }
        return ResponseEntity.ok(Map.of(
            "success", false,
            "message", "课程不存在"
        ));
    }

    /**
     * 根据开课院系获取课程
     */
    @GetMapping("/department/{departmentName}")
    public ResponseEntity<?> getCoursesByDepartment(@PathVariable String departmentName) {
        List<Course> courses = courseService.getCoursesByDepartment(departmentName);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", courses
        ));
    }

    /**
     * 批量导入课程
     */
    @PostMapping("/batch")
    public ResponseEntity<?> batchImportCourses(@RequestBody List<Course> courses) {
        try {
            List<Course> imported = courseService.batchImportCourses(courses);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", imported
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
} 
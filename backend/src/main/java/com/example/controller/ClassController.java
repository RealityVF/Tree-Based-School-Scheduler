package com.example.controller;

import com.example.entity.Class;
import com.example.service.ClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin
public class ClassController {
    private static final Logger logger = LoggerFactory.getLogger(ClassController.class);

    @Autowired
    private ClassService classService;

    @GetMapping
    public ResponseEntity<?> getAllClasses() {
        try {
            logger.info("开始获取所有班级信息");
            List<Class> classes = classService.getAllClasses();
            logger.info("成功获取班级信息，数量: {}", classes.size());
            classes.forEach(clazz -> logger.debug("班级信息: {}", clazz));
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", classes
            );
            logger.info("返回响应数据: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取班级信息时发生错误", e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", e.getMessage(),
                "data", null));
        }
    }

    @GetMapping("/{classId}")
    public ResponseEntity<?> getClassById(@PathVariable String classId) {
        try {
            logger.info("开始获取班级信息，班级ID: {}", classId);
            Class clazz = classService.getClassById(classId);
            if (clazz != null) {
                logger.info("成功获取班级信息: {}", clazz);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", clazz));
            }
            logger.warn("未找到班级信息，班级ID: {}", classId);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "班级不存在",
                "data", null));
        } catch (Exception e) {
            logger.error("获取班级信息时发生错误，班级ID: " + classId, e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", e.getMessage(),
                "data", null));
        }
    }

    @GetMapping("/major/{majorId}")
    public ResponseEntity<?> getClassesByMajorId(@PathVariable String majorId) {
        try {
            logger.info("开始获取专业班级信息，专业ID: {}", majorId);
            List<Class> classes = classService.getClassesByMajorId(majorId);
            logger.info("成功获取专业班级信息，数量: {}", classes.size());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", classes));
        } catch (Exception e) {
            logger.error("获取专业班级信息时发生错误，专业ID: " + majorId, e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", e.getMessage(),
                "data", null));
        }
    }
} 
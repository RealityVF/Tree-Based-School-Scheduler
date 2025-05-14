package com.example.controller;

import com.example.entity.Major;
import com.example.service.MajorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/majors")
@CrossOrigin
public class MajorController {
    private static final Logger logger = LoggerFactory.getLogger(MajorController.class);

    @Autowired
    private MajorService majorService;

    @GetMapping
    public ResponseEntity<?> getAllMajors() {
        try {
            logger.info("开始获取所有专业信息");
            List<Major> majors = majorService.getAllMajors();
            logger.info("成功获取专业信息，数量: {}", majors.size());
            majors.forEach(major -> logger.debug("专业信息: {}", major));
            
            Map<String, Object> response = Map.of(
                "success", true,
                "data", majors
            );
            logger.info("返回响应数据: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取专业信息时发生错误", e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", e.getMessage(),
                "data", null));
        }
    }

    @GetMapping("/{majorId}")
    public ResponseEntity<?> getMajorById(@PathVariable String majorId) {
        try {
            logger.info("开始获取专业信息，专业ID: {}", majorId);
            Major major = majorService.getMajorById(majorId);
            if (major != null) {
                logger.info("成功获取专业信息: {}", major);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", major));
            }
            logger.warn("未找到专业信息，专业ID: {}", majorId);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "专业不存在",
                "data", null));
        } catch (Exception e) {
            logger.error("获取专业信息时发生错误，专业ID: " + majorId, e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", e.getMessage(),
                "data", null));
        }
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<?> getMajorsByDepartmentId(@PathVariable String departmentId) {
        try {
            logger.info("开始获取院系专业信息，院系ID: {}", departmentId);
            List<Major> majors = majorService.getMajorsByDepartmentId(departmentId);
            logger.info("成功获取院系专业信息，数量: {}", majors.size());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", majors));
        } catch (Exception e) {
            logger.error("获取院系专业信息时发生错误，院系ID: " + departmentId, e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", e.getMessage(),
                "data", null));
        }
    }
} 
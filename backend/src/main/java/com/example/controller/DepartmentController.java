package com.example.controller;

import com.example.entity.Department;
import com.example.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/departments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
public class DepartmentController {
    
    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);
    private final DepartmentMapper departmentMapper;

    @GetMapping
    public ResponseEntity<?> getAllDepartments() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("开始获取院系列表");
        logger.info("当前用户: {}", auth.getName());
        logger.info("用户权限: {}", auth.getAuthorities());
        logger.info("用户详情: {}", auth.getPrincipal());
            
        try {
            logger.info("正在从数据库查询院系列表...");
            List<Department> departments = departmentMapper.getAllDepartments();
            logger.info("成功获取院系列表，数量: {}", departments.size());
            
            if (departments.isEmpty()) {
                logger.warn("院系列表为空");
            } else {
                logger.info("院系列表第一条数据: {}", departments.get(0));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取部门列表成功");
            response.put("data", departments);
            
            logger.info("返回响应数据: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取院系列表失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取部门列表失败：" + e.getMessage());
            response.put("data", null);
            return ResponseEntity.ok(response);
        }
    }
} 
package com.example.controller;

import com.example.common.ApiResponse;
import com.example.dto.TeacherCreateDTO;
import com.example.entity.Teacher;
import com.example.exception.BusinessException;
import com.example.service.TeacherAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 教师账号管理Controller
 */
@RestController
@Slf4j
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/admin/accounts")
public class TeacherAccountController {

    private final TeacherAccountService teacherAccountService;

    /**
     * 创建教师账号
     * 注意：教师号即为登录账号，初始密码为教师号后六位
     * @param dto 教师账号创建DTO
     * @return API响应
     */
    @PostMapping("/teacher")
    public ResponseEntity<?> createTeacherAccount(@RequestBody TeacherCreateDTO dto) {
        try {
            Teacher teacher = teacherAccountService.createTeacherAccount(dto);
            return ResponseEntity.ok(teacher);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/teacher/{teacherId}")
    public ResponseEntity<?> updateTeacherAccount(
            @PathVariable String teacherId,
            @RequestBody TeacherCreateDTO dto) {
        try {
            Teacher teacher = teacherAccountService.updateTeacherAccount(teacherId, dto);
            return ResponseEntity.ok(teacher);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 查询教师账号列表
     * @param keyword 关键词(教师号或姓名)
     * @param departmentId 部门ID
     * @param teacherType 教师类型
     * @param page 页码
     * @param size 每页大小
     * @return API响应
     */
    @GetMapping("/teacher")
    public ApiResponse<Map<String, Object>> getTeacherAccounts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String teacherType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("收到查询教师账号请求 - 关键词: {}, 部门: {}, 教师类型: {}, 页码: {}, 每页大小: {}", 
                keyword, departmentId, teacherType, page, size);

            List<Teacher> teachers = teacherAccountService.getTeacherAccounts(keyword, departmentId, teacherType, page, size);
            int total = teacherAccountService.countTeacherAccounts(keyword, departmentId, teacherType);

            // 处理教师列表数据，确保所有必要字段都有值
            List<Map<String, Object>> teacherList = teachers.stream()
                .map(teacher -> {
                    Map<String, Object> teacherMap = new HashMap<>();
                    teacherMap.put("id", teacher.getId());
                    teacherMap.put("name", teacher.getName());
                    teacherMap.put("englishName", teacher.getEnglishName() != null ? teacher.getEnglishName() : "");
                    teacherMap.put("gender", teacher.getGender() != null ? teacher.getGender().toString() : "");
                    teacherMap.put("ethnicity", teacher.getEthnicity() != null ? teacher.getEthnicity() : "");
                    teacherMap.put("title", teacher.getTitle() != null ? teacher.getTitle() : "");
                    teacherMap.put("departmentId", teacher.getDepartmentId());
                    teacherMap.put("departmentName", teacher.getDepartmentName() != null ? teacher.getDepartmentName() : "");
                    teacherMap.put("isExternal", teacher.getIsExternal() != null ? teacher.getIsExternal() : false);
                    teacherMap.put("teacherType", teacher.getTeacherType() != null ? teacher.getTeacherType() : "");
                    teacherMap.put("satisfaction", teacher.getSatisfaction() != null ? teacher.getSatisfaction() : true);
                    teacherMap.put("phone", teacher.getPhone() != null ? teacher.getPhone() : "");
                    teacherMap.put("email", teacher.getEmail() != null ? teacher.getEmail() : "");
                    teacherMap.put("maxWeeklyHours", teacher.getMaxWeeklyHours() != null ? teacher.getMaxWeeklyHours() : 16);
                    teacherMap.put("researchDirection", teacher.getResearchDirection() != null ? teacher.getResearchDirection() : "");
                    teacherMap.put("isActive", teacher.getIsActive() != null ? teacher.getIsActive() : true);
                    return teacherMap;
                })
                .collect(Collectors.toList());

            Map<String, Object> data = new HashMap<>();
            data.put("teachers", teacherList);
            data.put("total", total);
            data.put("page", page);
            data.put("size", size);

            log.info("查询教师账号成功，返回 {} 条记录", teachers.size());
            return ApiResponse.success("查询成功", data);
        } catch (Exception e) {
            log.error("查询教师账号失败: {}", e.getMessage(), e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 切换教师账号状态
     * @param teacherId 教师ID
     * @param request 请求体，包含enable字段
     * @return API响应
     */
    @PatchMapping("/teacher/{teacherId}/status")
    public ApiResponse<?> toggleTeacherAccountStatus(
            @PathVariable String teacherId,
            @RequestBody Map<String, Object> request) {
        try {
            if (!request.containsKey("enable")) {
                return ApiResponse.error("请求体中缺少enable字段");
            }

            boolean enable = Boolean.TRUE.equals(request.get("enable"));
            boolean success = teacherAccountService.toggleTeacherAccountStatus(teacherId, enable);
            if (success) {
                return ApiResponse.success(enable ? "启用成功" : "禁用成功");
            }
            return ApiResponse.error("操作失败");
        } catch (Exception e) {
            log.error("切换教师账号状态失败: {}", e.getMessage(), e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 重置教师账号密码
     * @param teacherId 教师工号
     * @return API响应
     */
    @PostMapping("/teacher/{teacherId}/reset-password")
    public ResponseEntity<?> resetTeacherPassword(@PathVariable String teacherId) {
        try {
            // 使用教师号后6位作为新密码
            String newPassword = teacherId.substring(teacherId.length() - 6);
            // 重置密码
            teacherAccountService.resetTeacherPassword(teacherId, newPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "密码重置成功");
            response.put("newPassword", newPassword);
            
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 设置教师账号密码
     * @param teacherId 教师工号
     * @param request 包含新密码的请求体
     * @return API响应
     */
    @PostMapping("/teacher/{teacherId}/set-password")
    public ResponseEntity<?> setTeacherPassword(
            @PathVariable String teacherId,
            @RequestBody Map<String, String> request) {
        try {
            String newPassword = request.get("password");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("密码不能为空");
            }
            
            // 设置密码
            teacherAccountService.resetTeacherPassword(teacherId, newPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "密码设置成功");
            
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 
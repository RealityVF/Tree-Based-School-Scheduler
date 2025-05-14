package com.example.controller;

import com.example.entity.Student;
import com.example.entity.StudentAccount;
import com.example.service.StudentAccountService;
import com.example.dto.PasswordResetRequest;
import com.example.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/accounts/student")
public class StudentAccountController {

    @Autowired
    private StudentAccountService studentAccountService;

    @PostMapping
    public ResponseEntity<?> createStudentAccount(@RequestBody Student student) {
        try {
            StudentAccount account = studentAccountService.createStudentAccount(student);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "创建学生账号成功");
            response.put("data", account);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<?> getStudentAccount(@PathVariable String studentId) {
        StudentAccount account = studentAccountService.findByStudentId(studentId);
        
        Map<String, Object> response = new HashMap<>();
        if (account == null) {
            response.put("success", false);
            response.put("message", "学生账号不存在");
            return ResponseEntity.ok(response);
        }
        
        response.put("success", true);
        response.put("data", account);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{studentId}/status")
    public ResponseEntity<?> updateAccountStatus(
            @PathVariable String studentId,
            @RequestParam boolean isActive) {
        try {
            boolean success = studentAccountService.updateAccountStatus(studentId, isActive);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? (isActive ? "账号已启用" : "账号已禁用") : "操作失败");
            
            if (success) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{studentId}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable String studentId) {
        try {
            // 获取学生详情以获取身份证号
            StudentAccount student = studentAccountService.getStudentDetail(studentId);
            if (student == null) {
                throw new RuntimeException("学生账号不存在");
            }

            // 如果有身份证号，使用身份证后6位作为新密码，否则使用学号后6位
            String newPassword;
            if (student.getIdCard() != null && !student.getIdCard().trim().isEmpty()) {
                newPassword = student.getIdCard().substring(Math.max(0, student.getIdCard().length() - 6));
                log.info("Using last 6 digits of ID card as new password for student: {}", studentId);
            } else {
                newPassword = studentId.substring(Math.max(0, studentId.length() - 6));
                log.info("ID card not found, using last 6 digits of student ID as new password for student: {}", studentId);
            }
            
            // 重置密码
            boolean success = studentAccountService.resetPassword(studentId, newPassword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "密码重置成功" : "密码重置失败");
            if (success) {
                response.put("newPassword", newPassword);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to reset password for student: {}", studentId, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{studentId}/password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetPassword(
            @PathVariable String studentId,
            @RequestBody PasswordResetRequest request) {
        
        log.info("Received password reset request for student: {}", studentId);
        
        try {
            // 获取学生详情以验证学生是否存在
            studentAccountService.getStudentDetail(studentId);
            
            // 重置密码
            boolean success = studentAccountService.resetPassword(studentId, request.getNewPassword());
            
            if (success) {
                log.info("Successfully reset password for student: {}", studentId);
                return ResponseEntity.ok(new ApiResponse<>(true, "密码重置成功"));
            } else {
                log.warn("Failed to reset password for student: {}", studentId);
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "密码重置失败"));
            }
        } catch (Exception e) {
            log.error("Error resetting password for student: {}", studentId, e);
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "密码重置失败：" + e.getMessage()));
        }
    }

    /**
     * 获取学生账号列表
     */
    @GetMapping
    public ResponseEntity<?> getStudentAccounts(
            @RequestParam(required = false) String searchKey,
            @RequestParam(required = false) String faculty,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String major,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            List<StudentAccount> accounts = studentAccountService.getStudentAccounts(
                searchKey, faculty, className, grade, major, page, size);
            
            int total = studentAccountService.countStudentAccounts(
                searchKey, faculty, className, grade, major);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", accounts);
            response.put("total", total);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
} 
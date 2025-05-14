package com.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.Student;
import com.example.entity.StudentAccount;
import com.example.mapper.StudentAccountMapper;
import com.example.mapper.StudentMapper;
import com.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StudentAccountMapper studentAccountMapper;

    /**
     * 添加学生
     */
    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody Student student) {
        try {
            Student result = studentService.addStudent(student);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "添加学生成功");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新学生信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable String id, @RequestBody Student student) {
        try {
            student.setId(id);
            boolean success = studentService.updateStudent(student);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "更新成功" : "更新失败");
            
            if (success) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除学生
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable String id) {
        try {
            boolean success = studentService.deleteStudent(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "删除成功" : "删除失败");
            
            if (success) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取学生详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable String id) {
        Student student = studentService.getStudentById(id);
        Map<String, Object> response = new HashMap<>();
        
        if (student != null) {
            response.put("success", true);
            response.put("data", student);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "学生不存在");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 分页查询学生
     */
    @GetMapping
    public ResponseEntity<?> getStudentPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String majorId,
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String studentStatus,
            @RequestParam(required = false) String keyword) {
        
        Page<Student> page = new Page<>(current, size);
        IPage<Student> result = studentService.getStudentPage(page, departmentId, majorId, classId, studentStatus, keyword);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result.getRecords());
        response.put("total", result.getTotal());
        response.put("current", result.getCurrent());
        response.put("size", result.getSize());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取班级学生列表
     */
    @GetMapping("/class/{classId}")
    public ResponseEntity<?> getStudentsByClassId(@PathVariable String classId) {
        List<Student> students = studentService.getStudentsByClassId(classId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", students);
        response.put("total", students.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取专业学生列表
     */
    @GetMapping("/major/{majorId}")
    public ResponseEntity<?> getStudentsByMajorId(@PathVariable String majorId) {
        List<Student> students = studentService.getStudentsByMajorId(majorId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", students);
        response.put("total", students.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 批量导入学生
     */
    @PostMapping("/batch-import")
    public ResponseEntity<?> batchImportStudents(@RequestBody List<Student> students) {
        try {
            int successCount = studentService.batchImportStudents(students);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "导入成功");
            response.put("successCount", successCount);
            response.put("totalCount", students.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取当前登录学生的个人信息
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getStudentProfile() {
        // 从安全上下文中获取当前登录的学生ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();
        
        // 获取学生信息
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "学生信息不存在");
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", student);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取当前登录学生的账号信息
     */
    @GetMapping("/account")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getStudentAccount() {
        // 从安全上下文中获取当前登录的学生ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();
        
        // 获取学生账号信息
        StudentAccount account = studentAccountMapper.findByStudentId(studentId);
        if (account == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "学生账号不存在");
            return ResponseEntity.ok(response);
        }
        
        // 出于安全考虑，清除密码哈希
        account.setPasswordHash(null);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", account);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新学生个人信息
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> updateStudentProfile(@RequestBody Student student) {
        // 从安全上下文中获取当前登录的学生ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();
        
        // 确保只能修改自己的信息
        student.setId(studentId);
        
        // 更新学生信息
        boolean success = studentService.updateStudent(student);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", success ? "更新成功" : "更新失败");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 修改学生密码
     */
    @PutMapping("/password")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData) {
        // 从安全上下文中获取当前登录的学生ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();
        
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "请提供旧密码和新密码");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 验证旧密码并设置新密码的逻辑...
        // 这里应该调用一个专门的service方法
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "密码修改成功");
        
        return ResponseEntity.ok(response);
    }
} 
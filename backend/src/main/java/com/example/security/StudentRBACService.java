package com.example.security;

import com.example.entity.Student;
import com.example.entity.StudentAccount;
import com.example.mapper.StudentAccountMapper;
import com.example.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 学生角色权限服务
 */
@Service
public class StudentRBACService {

    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private StudentAccountMapper studentAccountMapper;
    
    /**
     * 检查学生是否有权限访问特定资源
     * @param studentId 学生ID
     * @param resource 资源路径
     * @return 是否有权限
     */
    public boolean hasPermission(String studentId, String resource) {
        // 检查学生账号状态
        StudentAccount account = studentAccountMapper.findByStudentId(studentId);
        if (account == null || !account.getIsActive()) {
            return false;
        }
        
        // 获取学生信息
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            return false;
        }
        
        // 默认学生只能访问自己的资源
        if (resource.startsWith("/api/student")) {
            return true;
        }
        
        // 其他权限规则可以在这里添加
        
        return false;
    }
    
    /**
     * 检查是否是学生自己的资源
     * @param studentId 学生ID
     * @param resourceStudentId 资源关联的学生ID
     * @return 是否是自己的资源
     */
    public boolean isOwnResource(String studentId, String resourceStudentId) {
        return studentId != null && studentId.equals(resourceStudentId);
    }
} 
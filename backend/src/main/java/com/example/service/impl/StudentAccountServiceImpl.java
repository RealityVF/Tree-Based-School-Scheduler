package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.entity.Student;
import com.example.entity.StudentAccount;
import com.example.mapper.StudentAccountMapper;
import com.example.mapper.StudentMapper;
import com.example.service.StudentAccountService;
import com.example.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Slf4j
@Service
public class StudentAccountServiceImpl implements StudentAccountService {

    @Autowired
    private StudentAccountMapper studentAccountMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public StudentAccount createStudentAccount(Student student) {
        // 检查学生是否存在
        Student existingStudent = studentMapper.selectById(student.getId());
        if (existingStudent == null) {
            throw new RuntimeException("学生信息不存在");
        }

        // 检查账号是否已存在
        StudentAccount existingAccount = studentAccountMapper.selectById(student.getId());
        if (existingAccount != null) {
            throw new RuntimeException("该学生账号已存在");
        }

        // 初始密码：身份证后6位
        String initialPassword = student.getIdCard().substring(Math.max(0, student.getIdCard().length() - 6));
        
        // 创建学生账号
        StudentAccount account = new StudentAccount();
        account.setStudentId(student.getId());
        account.setUsername(student.getId()); // 使用学号作为用户名
        account.setName(student.getName()); // 设置学生姓名
        account.setPassword(initialPassword); // 设置明文密码
        account.setPasswordHash(passwordEncoder.encode(initialPassword)); // 使用身份证后6位作为初始密码
        account.setIsActive(true);
        account.setEnabled(true);
        account.setLastLogin(null);
        account.setLastLoginAt(null);
        account.setPasswordUpdatedAt(LocalDateTime.now());
        
        // 从学生对象复制信息
        account.setEmail(student.getEmail());
        // 如果email为空，则生成一个基于学号的临时邮箱
        if (account.getEmail() == null || account.getEmail().trim().isEmpty()) {
            account.setEmail(student.getId() + "@temp.edu.cn");
        }
        account.setGender(student.getGender());
        account.setEthnicity(student.getEthnicity());
        account.setPoliticalStatus(student.getPoliticalStatus());
        account.setIdCard(student.getIdCard());
        account.setPhone(student.getPhone());
        
        // 获取班级、专业等信息
        try {
            // 使用班级ID查询班级名称
            String classId = student.getClassId();
            if (classId != null && !classId.isEmpty()) {
                // TODO: 理想情况下应该从班级服务中获取班级名称
                account.setClassName(classId); // 暂时使用班级ID
                
                // 根据班级ID提取年级信息 (格式：年份4位+部门号4位+专业号2位+班级号2位)
                if (classId.length() >= 4) {
                    account.setGrade(classId.substring(0, 4)); // 使用班级ID前4位作为年级
                }
            }
            
            // 设置专业名称
            String majorId = student.getMajorId();
            if (majorId != null && !majorId.isEmpty()) {
                // TODO: 理想情况下应该从专业服务中获取专业名称
                account.setMajor(majorId); // 暂时使用专业ID
            }
            
            // 设置院系
            String departmentId = student.getDepartmentId();
            if (departmentId != null && !departmentId.isEmpty()) {
                // TODO: 理想情况下应该从院系服务中获取院系名称
                account.setFaculty(departmentId); // 暂时使用院系ID
            }
        } catch (Exception e) {
            // 忽略获取额外信息的错误，仍然可以创建基本账号
            System.out.println("获取学生关联信息时出错: " + e.getMessage());
        }

        studentAccountMapper.insert(account);
        return account;
    }

    @Override
    public StudentAccount findByStudentId(String studentId) {
        return studentAccountMapper.selectById(studentId);
    }

    @Override
    @Transactional
    public boolean updateAccountStatus(String studentId, boolean isActive) {
        StudentAccount account = studentAccountMapper.selectById(studentId);
        if (account == null) {
            throw new RuntimeException("学生账号不存在");
        }

        account.setIsActive(isActive);
        return studentAccountMapper.updateById(account) > 0;
    }

    @Override
    @Transactional
    public boolean resetPassword(String studentId, String newPassword) {
        log.info("Attempting to reset password for student: {}", studentId);
        
        StudentAccount student = studentAccountMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException("学生账号不存在");
        }

        try {
            // 加密新密码
            String encodedPassword = passwordEncoder.encode(newPassword);
            student.setPassword(encodedPassword);
            student.setPasswordHash(encodedPassword);
            student.setPasswordUpdatedAt(LocalDateTime.now());
            
            int result = studentAccountMapper.updateById(student);
            
            if (result > 0) {
                log.info("Successfully reset password for student: {}", studentId);
                return true;
            } else {
                log.error("Failed to update password in database for student: {}", studentId);
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to reset password for student: {}", studentId, e);
            throw new BusinessException("密码重置失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean setPassword(String studentId, String newPassword) {
        StudentAccount account = studentAccountMapper.selectById(studentId);
        if (account == null) {
            throw new RuntimeException("学生账号不存在");
        }

        account.setPasswordHash(passwordEncoder.encode(newPassword));
        account.setPasswordUpdatedAt(LocalDateTime.now());

        return studentAccountMapper.updateById(account) > 0;
    }

    @Override
    public List<StudentAccount> getStudentAccounts(
            String searchKey,
            String faculty,
            String className,
            String grade,
            String major,
            int page,
            int size
    ) {
        int offset = Math.max(0, (page - 1) * size);
        System.out.println("Pagination params - page: " + page + ", size: " + size + ", offset: " + offset);
        
        List<StudentAccount> accounts = studentAccountMapper.selectStudentAccounts(
                searchKey,
                faculty,
                className,
                grade,
                major,
                offset,
                size
        );
        
        System.out.println("Query returned " + accounts.size() + " accounts");
        if (!accounts.isEmpty()) {
            System.out.println("First account ID: " + accounts.get(0).getStudentId());
            System.out.println("Last account ID: " + accounts.get(accounts.size() - 1).getStudentId());
        }
        
        return accounts;
    }

    @Override
    public int countStudentAccounts(
            String searchKey,
            String faculty,
            String className,
            String grade,
            String major
    ) {
        int total = studentAccountMapper.countStudentAccounts(
                searchKey,
                faculty,
                className,
                grade,
                major
        );
        System.out.println("Total accounts count: " + total);
        return total;
    }

    @Override
    public StudentAccount getStudentDetail(String studentId) {
        log.info("Fetching student details for ID: {}", studentId);
        
        StudentAccount student = studentAccountMapper.selectById(studentId);
        if (student == null) {
            log.error("Student not found with ID: {}", studentId);
            throw new BusinessException("学生账号不存在");
        }
        return student;
    }
} 
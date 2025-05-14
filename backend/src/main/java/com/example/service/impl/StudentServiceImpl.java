package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.Student;
import com.example.entity.StudentAccount;
import com.example.mapper.StudentAccountMapper;
import com.example.mapper.StudentMapper;
import com.example.service.StudentAccountService;
import com.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StudentAccountMapper studentAccountMapper;

    @Autowired
    private StudentAccountService studentAccountService;

    @Override
    @Transactional
    public Student addStudent(Student student) {
        // 检查学号是否已存在
        if (studentMapper.selectById(student.getId()) != null) {
            throw new RuntimeException("该学号已存在");
        }

        // 设置默认值
        if (student.getIsActive() == null) {
            student.setIsActive(true);
        }
        if (student.getStudentStatus() == null) {
            student.setStudentStatus("在读");
        }
        student.setCreatedAt(new Date());
        student.setUpdatedAt(new Date());

        try {
            // 保存学生信息
            studentMapper.insert(student);

            // 创建学生账号
            studentAccountService.createStudentAccount(student);

            return student;
        } catch (Exception e) {
            // 记录错误并重新抛出异常，确保事务回滚
            System.err.println("创建学生失败: " + student.getId() + ", 原因: " + e.getMessage());
            throw new RuntimeException("创建学生账号失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean updateStudent(Student student) {
        // 检查学生是否存在
        Student existingStudent = studentMapper.selectById(student.getId());
        if (existingStudent == null) {
            throw new RuntimeException("学生信息不存在");
        }

        student.setUpdatedAt(new Date());
        int result = studentMapper.updateById(student);

        // 如果修改了学生状态，同步更新账号状态
        if (student.getIsActive() != null && !student.getIsActive().equals(existingStudent.getIsActive())) {
            StudentAccount account = studentAccountMapper.selectById(student.getId());
            if (account != null) {
                account.setIsActive(student.getIsActive());
                studentAccountMapper.updateById(account);
            }
        }

        return result > 0;
    }

    @Override
    @Transactional
    public boolean deleteStudent(String id) {
        // 检查学生是否存在
        if (studentMapper.selectById(id) == null) {
            throw new RuntimeException("学生信息不存在");
        }

        // 删除学生信息
        int result = studentMapper.deleteById(id);

        // 删除学生账号
        studentAccountMapper.deleteById(id);

        return result > 0;
    }

    @Override
    public Student getStudentById(String id) {
        return studentMapper.selectById(id);
    }

    @Override
    public IPage<Student> getStudentPage(Page<Student> page, String departmentId, String majorId, 
                                      String classId, String studentStatus, String keyword) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();

        // 条件查询
        if (StringUtils.hasText(departmentId)) {
            queryWrapper.eq(Student::getDepartmentId, departmentId);
        }
        if (StringUtils.hasText(majorId)) {
            queryWrapper.eq(Student::getMajorId, majorId);
        }
        if (StringUtils.hasText(classId)) {
            queryWrapper.eq(Student::getClassId, classId);
        }
        if (StringUtils.hasText(studentStatus)) {
            queryWrapper.eq(Student::getStudentStatus, studentStatus);
        }
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> 
                wrapper.like(Student::getName, keyword)
                       .or()
                       .like(Student::getId, keyword)
                       .or()
                       .like(Student::getIdCard, keyword)
                       .or()
                       .like(Student::getPhone, keyword)
                       .or()
                       .like(Student::getEmail, keyword)
            );
        }

        // 只查询在校学生
        queryWrapper.eq(Student::getIsActive, true);

        // 排序
        queryWrapper.orderByDesc(Student::getCreatedAt)
                   .orderByAsc(Student::getId);

        // 执行查询
        return studentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Student> getStudentsByClassId(String classId) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getClassId, classId);
        queryWrapper.orderByAsc(Student::getId);
        return studentMapper.selectList(queryWrapper);
    }

    @Override
    public List<Student> getStudentsByMajorId(String majorId) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getMajorId, majorId);
        queryWrapper.orderByAsc(Student::getId);
        return studentMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchImportStudents(List<Student> students) {
        int successCount = 0;
        StringBuilder errors = new StringBuilder();
        
        for (Student student : students) {
            try {
                // 设置默认值
                if (student.getIsActive() == null) {
                    student.setIsActive(true);
                }
                if (student.getStudentStatus() == null) {
                    student.setStudentStatus("在读");
                }
                student.setCreatedAt(new Date());
                student.setUpdatedAt(new Date());

                // 检查学号是否已存在
                if (studentMapper.selectById(student.getId()) != null) {
                    throw new RuntimeException("该学号已存在: " + student.getId());
                }

                // 保存学生信息
                studentMapper.insert(student);

                // 创建学生账号
                studentAccountService.createStudentAccount(student);

                successCount++;
            } catch (Exception e) {
                // 记录错误信息
                errors.append("导入学生[").append(student.getId()).append("]失败: ").append(e.getMessage()).append("\n");
            }
        }
        
        // 如果有任何失败，抛出异常以回滚整个事务
        if (successCount < students.size()) {
            throw new RuntimeException("部分学生导入失败:\n" + errors.toString());
        }
        
        return successCount;
    }
} 
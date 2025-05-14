package com.example.service;

import com.example.entity.Student;
import com.example.entity.StudentAccount;
import java.util.List;

public interface StudentAccountService {
    /**
     * 创建学生账号
     * @param student 学生信息
     * @return 创建的学生账号
     */
    StudentAccount createStudentAccount(Student student);

    /**
     * 根据学号查找学生账号
     * @param studentId 学号
     * @return 学生账号
     */
    StudentAccount findByStudentId(String studentId);

    /**
     * 更新学生账号状态
     * @param studentId 学号
     * @param isActive 是否激活
     * @return 是否成功
     */
    boolean updateAccountStatus(String studentId, boolean isActive);

    /**
     * 重置学生密码
     * @param studentId 学生ID
     * @param newPassword 新密码
     * @return 是否重置成功
     */
    boolean resetPassword(String studentId, String newPassword);

    /**
     * 设置学生密码
     * @param studentId 学号
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean setPassword(String studentId, String newPassword);

    /**
     * 获取学生账号列表
     * @param searchKey 搜索关键词
     * @param faculty 院系
     * @param className 班级
     * @param grade 年级
     * @param major 专业
     * @param page 页码
     * @param size 每页大小
     * @return 学生账号列表
     */
    List<StudentAccount> getStudentAccounts(
            String searchKey,
            String faculty,
            String className,
            String grade,
            String major,
            int page,
            int size
    );

    /**
     * 统计学生账号数量
     * @param searchKey 搜索关键词
     * @param faculty 院系
     * @param className 班级
     * @param grade 年级
     * @param major 专业
     * @return 学生账号数量
     */
    int countStudentAccounts(
            String searchKey,
            String faculty,
            String className,
            String grade,
            String major
    );

    /**
     * 获取学生账号详情
     * @param studentId 学生ID
     * @return 学生账号信息
     */
    StudentAccount getStudentDetail(String studentId);
} 
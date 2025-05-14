package com.example.service;

import com.example.dto.TeacherCreateDTO;
import com.example.entity.Teacher;
import java.util.List;

/**
 * 教师账号服务接口
 */
public interface TeacherAccountService {
    
    /**
     * 创建单个教师账号
     * @param dto 教师账号创建信息
     * @return 创建的教师信息
     */
    Teacher createTeacherAccount(TeacherCreateDTO dto);

    /**
     * 获取教师账号列表
     * @param keyword 关键词
     * @param departmentId 部门ID
     * @param teacherType 教师类型
     * @param page 页码
     * @param size 每页大小
     * @return 教师列表
     */
    List<Teacher> getTeacherAccounts(String keyword, String departmentId, String teacherType, int page, int size);

    /**
     * 统计教师账号总数
     * @param keyword 关键词
     * @param departmentId 部门ID
     * @param teacherType 教师类型
     * @return 总数
     */
    int countTeacherAccounts(String keyword, String departmentId, String teacherType);

    /**
     * 更新教师账号
     * @param teacherId 教师ID
     * @param dto 教师更新DTO
     * @return 更新后的教师实体
     */
    Teacher updateTeacherAccount(String teacherId, TeacherCreateDTO dto);

    /**
     * 切换教师账号状态
     * @param teacherId 教师ID
     * @param enable 是否启用
     * @return 是否操作成功
     */
    boolean toggleTeacherAccountStatus(String teacherId, boolean enable);

    /**
     * 重置教师账号密码
     * @param teacherId 教师工号
     * @param newPassword 新密码
     */
    void resetTeacherPassword(String teacherId, String newPassword);

    /**
     * 生成初始密码
     * 使用教师工号的后六位作为密码
     * @param teacherId 教师工号
     * @return 生成的密码
     */
    String generateSecurePassword(String teacherId);
} 
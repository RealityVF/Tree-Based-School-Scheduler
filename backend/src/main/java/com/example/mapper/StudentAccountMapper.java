package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.StudentAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StudentAccountMapper extends BaseMapper<StudentAccount> {
    /**
     * 根据用户名查找学生账号
     * @param username 用户名
     * @return 学生账号
     */
    @Select("SELECT * FROM student_accounts WHERE username = #{username}")
    StudentAccount findByUsername(String username);
    
    /**
     * 根据学号查找学生账号
     * @param studentId 学号
     * @return 学生账号
     */
    @Select("SELECT * FROM student_accounts WHERE student_id = #{studentId}")
    StudentAccount findByStudentId(String studentId);
    
    /**
     * 更新最后登录时间
     * @param studentId 学号
     * @param lastLogin 最后登录时间
     */
    @Update("UPDATE student_accounts SET last_login = #{lastLogin} WHERE student_id = #{studentId}")
    void updateLastLogin(@Param("studentId") String studentId, @Param("lastLogin") LocalDateTime lastLogin);
    
    /**
     * 获取密码哈希
     * @param studentId 学号
     * @return 密码哈希
     */
    @Select("SELECT password_hash FROM student_accounts WHERE student_id = #{studentId}")
    String getPasswordHash(String studentId);
    
    /**
     * 获取学生账号状态
     * @param studentId 学号
     * @return 是否激活
     */
    @Select("SELECT is_active FROM student_accounts WHERE student_id = #{studentId}")
    Boolean getIsActive(String studentId);

    /**
     * 获取学生账号列表
     * @param searchKey 搜索关键字
     * @param faculty 院系
     * @param className 班级
     * @param grade 年级
     * @param major 专业
     * @param offset 偏移量
     * @param size 每页大小
     * @return 学生账号列表
     */
    List<StudentAccount> selectStudentAccounts(
            @Param("searchKey") String searchKey,
            @Param("faculty") String faculty,
            @Param("className") String className,
            @Param("grade") String grade,
            @Param("major") String major,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 统计学生账号数量
     * @param searchKey 搜索关键字
     * @param faculty 院系
     * @param className 班级
     * @param grade 年级
     * @param major 专业
     * @return 学生账号数量
     */
    int countStudentAccounts(
            @Param("searchKey") String searchKey,
            @Param("faculty") String faculty,
            @Param("className") String className,
            @Param("grade") String grade,
            @Param("major") String major
    );
} 
package com.example.mapper;

import com.example.entity.Teacher;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TeacherMapper {
    // 搜索教师
    List<Teacher> searchTeachers(@Param("query") String query);

    // 根据ID查找教师
    Teacher findById(@Param("id") String id);

    // 创建教师
    void insert(Teacher teacher);

    // 更新教师信息
    void update(Teacher teacher);

    // 删除教师
    void delete(@Param("id") String id);

    // 检查教师ID是否存在
    @Select("SELECT COUNT(*) > 0 FROM teachers WHERE id = #{id}")
    boolean existsById(@Param("id") String id);

    // 根据用户名（教师工号）查找教师
    Teacher findByUsername(@Param("username") String username);
    
    // 获取密码哈希
    @Select("SELECT password_hash FROM teacher_accounts WHERE teacher_id = #{username}")
    String getPasswordHash(@Param("username") String username);

    // 获取账号状态
    @Select("SELECT is_active FROM teacher_accounts WHERE teacher_id = #{username}")
    Boolean getIsActive(@Param("username") String username);
    
    // 更新最后登录时间
    @Update("UPDATE teacher_accounts SET last_login = #{lastLogin} WHERE teacher_id = #{teacherId}")
    void updateLastLogin(@Param("teacherId") String teacherId, @Param("lastLogin") LocalDateTime lastLogin);
    
    // 更新教师密码
    @Update("UPDATE teacher_accounts SET password_hash = #{passwordHash}, password_updated_at = #{updatedAt} WHERE teacher_id = #{teacherId}")
    void updatePassword(@Param("teacherId") String teacherId, @Param("passwordHash") String passwordHash, @Param("updatedAt") LocalDateTime updatedAt);

    // 获取教师列表（分页）
    List<Teacher> getTeacherList(@Param("keyword") String keyword, 
                                @Param("departmentId") String departmentId,
                                @Param("teacherType") String teacherType,
                                @Param("offset") int offset,
                                @Param("size") int size);

    // 统计教师总数
    int countTeachers(@Param("keyword") String keyword,
                     @Param("departmentId") String departmentId,
                     @Param("teacherType") String teacherType);

    // 创建教师账号
    @Insert("INSERT IGNORE INTO teacher_accounts (teacher_id, password_hash, email, last_login, is_active, password_updated_at) VALUES (#{teacherId}, #{passwordHash}, #{email}, #{lastLogin}, #{isActive}, #{passwordUpdatedAt})")
    int createTeacherAccount(@Param("teacherId") String teacherId,
                            @Param("passwordHash") String passwordHash,
                            @Param("email") String email,
                            @Param("lastLogin") LocalDateTime lastLogin,
                            @Param("isActive") Boolean isActive,
                            @Param("passwordUpdatedAt") LocalDateTime passwordUpdatedAt);

    @Select("SELECT COUNT(*) > 0 FROM teacher_accounts WHERE teacher_id = #{teacherId}")
    boolean existsTeacherAccount(@Param("teacherId") String teacherId);

    @Select("SELECT COUNT(*) > 0 FROM teachers WHERE id = #{teacherId} FOR UPDATE")
    boolean existsByIdWithLock(@Param("teacherId") String teacherId);

    @Select("SELECT COUNT(*) > 0 FROM teacher_accounts WHERE teacher_id = #{teacherId} FOR UPDATE")
    boolean existsTeacherAccountWithLock(@Param("teacherId") String teacherId);
} 
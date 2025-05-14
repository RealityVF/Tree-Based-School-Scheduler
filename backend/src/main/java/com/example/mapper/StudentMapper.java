package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    /**
     * 根据学号查找学生
     * @param id 学号
     * @return 学生信息
     */
    @Select("SELECT * FROM students WHERE id = #{id}")
    Student findById(String id);
    
    /**
     * 根据班级查找学生
     * @param classId 班级ID
     * @return 学生列表
     */
    @Select("SELECT * FROM students WHERE class_id = #{classId} AND is_active = true ORDER BY id")
    List<Student> findByClassId(String classId);
    
    /**
     * 根据专业查找学生
     * @param majorId 专业ID
     * @return 学生列表
     */
    @Select("SELECT * FROM students WHERE major_id = #{majorId} AND is_active = true ORDER BY id")
    List<Student> findByMajorId(String majorId);
    
    /**
     * 根据院系查找学生
     * @param departmentId 院系ID
     * @return 学生列表
     */
    @Select("SELECT * FROM students WHERE department_id = #{departmentId} AND is_active = true ORDER BY id")
    List<Student> findByDepartmentId(String departmentId);

    /**
     * 更新学生最后登录时间
     */
    @Update("UPDATE student_accounts SET last_login = #{lastLogin} WHERE student_id = #{studentId}")
    void updateLastLogin(@Param("studentId") String studentId, @Param("lastLogin") LocalDateTime lastLogin);

    /**
     * 统计学生数量
     */
    @Select("SELECT COUNT(*) FROM students WHERE is_active = true")
    int countActiveStudents();

    /**
     * 根据条件统计学生数量
     */
    @Select({
        "<script>",
        "SELECT COUNT(*) FROM students s",
        "WHERE s.is_active = true",
        "<if test='departmentId != null and departmentId != \"\"'>",
        "AND s.department_id = #{departmentId}",
        "</if>",
        "<if test='majorId != null and majorId != \"\"'>",
        "AND s.major_id = #{majorId}",
        "</if>",
        "<if test='classId != null and classId != \"\"'>",
        "AND s.class_id = #{classId}",
        "</if>",
        "<if test='studentStatus != null and studentStatus != \"\"'>",
        "AND s.student_status = #{studentStatus}",
        "</if>",
        "<if test='keyword != null and keyword != \"\"'>",
        "AND (s.name LIKE CONCAT('%', #{keyword}, '%')",
        "OR s.id LIKE CONCAT('%', #{keyword}, '%')",
        "OR s.id_card LIKE CONCAT('%', #{keyword}, '%')",
        "OR s.phone LIKE CONCAT('%', #{keyword}, '%')",
        "OR s.email LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "</script>"
    })
    int countStudentsByCondition(
        @Param("departmentId") String departmentId,
        @Param("majorId") String majorId,
        @Param("classId") String classId,
        @Param("studentStatus") String studentStatus,
        @Param("keyword") String keyword
    );
} 
package com.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.Student;

import java.util.List;

public interface StudentService {
    /**
     * 添加学生信息
     * @param student 学生信息
     * @return 添加后的学生信息
     */
    Student addStudent(Student student);

    /**
     * 修改学生信息
     * @param student 学生信息
     * @return 是否成功
     */
    boolean updateStudent(Student student);

    /**
     * 删除学生信息
     * @param id 学号
     * @return 是否成功
     */
    boolean deleteStudent(String id);

    /**
     * 根据学号查询学生信息
     * @param id 学号
     * @return 学生信息
     */
    Student getStudentById(String id);

    /**
     * 分页查询学生信息
     * @param page 分页参数
     * @param departmentId 院系ID
     * @param majorId 专业ID
     * @param classId 班级ID
     * @param keyword 关键字（姓名/学号）
     * @return 分页结果
     */
    IPage<Student> getStudentPage(Page<Student> page, String departmentId, String majorId, 
                                String classId, String studentStatus, String keyword);

    /**
     * 根据班级ID查询学生列表
     * @param classId 班级ID
     * @return 学生列表
     */
    List<Student> getStudentsByClassId(String classId);

    /**
     * 根据专业ID查询学生列表
     * @param majorId 专业ID
     * @return 学生列表
     */
    List<Student> getStudentsByMajorId(String majorId);

    /**
     * 批量导入学生信息
     * @param students 学生信息列表
     * @return 导入成功的学生数量
     */
    int batchImportStudents(List<Student> students);
} 
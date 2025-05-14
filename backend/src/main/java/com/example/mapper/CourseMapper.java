package com.example.mapper;

import com.example.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CourseMapper {
    
    // 根据条件查询课程列表
    List<Course> getCourseList(@Param("keyword") String keyword,
                             @Param("courseType") String courseType,
                             @Param("departmentName") String departmentName,
                             @Param("offset") int offset,
                             @Param("size") int size);
    
    // 统计符合条件的课程总数
    int countCourses(@Param("keyword") String keyword,
                    @Param("courseType") String courseType,
                    @Param("departmentName") String departmentName);
    
    // 根据ID获取课程
    Course getCourseById(@Param("id") String id);
    
    // 根据课程编号列表批量获取课程
    List<Course> getCoursesByIds(@Param("ids") List<String> ids);
    
    // 根据开课院系获取课程
    List<Course> getCoursesByDepartment(@Param("departmentName") String departmentName);

    // 插入新课程
    int insert(Course course);

    // 更新课程信息
    int update(Course course);

    // 删除课程
    int delete(@Param("id") String id);
} 
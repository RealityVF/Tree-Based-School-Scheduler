package com.example.mapper;

import com.example.entity.CourseWeek;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CourseWeekMapper {
    CourseWeek findById(Integer weekId);
    List<CourseWeek> findByCourseId(String courseId);
    List<CourseWeek> findByCourseIdAndWeekNumber(String courseId, Integer weekNumber);
    int insert(CourseWeek courseWeek);
    int update(CourseWeek courseWeek);
    int delete(Integer weekId);
} 
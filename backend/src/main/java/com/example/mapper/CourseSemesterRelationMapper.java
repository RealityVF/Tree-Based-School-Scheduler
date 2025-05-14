package com.example.mapper;

import com.example.entity.CourseSemesterRelation;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CourseSemesterRelationMapper {
    CourseSemesterRelation findById(Integer relationId);
    List<CourseSemesterRelation> findByCourseId(String courseId);
    List<CourseSemesterRelation> findBySemesterId(Integer semesterId);
    CourseSemesterRelation findByCourseAndSemester(String courseId, Integer semesterId);
    int insert(CourseSemesterRelation relation);
    int update(CourseSemesterRelation relation);
    int delete(Integer relationId);
} 
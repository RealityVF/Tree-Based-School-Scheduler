package com.example.mapper;

import com.example.entity.Semester;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface SemesterMapper {
    Semester findById(Integer semesterId);
    Semester findByName(String semesterName);
    Semester findCurrentSemester();
    List<Semester> findAll();
    int insert(Semester semester);
    int update(Semester semester);
    int delete(Integer semesterId);
} 
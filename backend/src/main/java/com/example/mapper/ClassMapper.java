package com.example.mapper;

import com.example.entity.Class;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import java.util.List;

@Mapper
public interface ClassMapper {
    
    @Select("SELECT * FROM classes")
    @Results({
        @Result(property = "classId", column = "id"),
        @Result(property = "className", column = "name"),
        @Result(property = "majorId", column = "major_id"),
        @Result(property = "startYear", column = "start_year")
    })
    List<Class> findAll();

    @Select("SELECT * FROM classes WHERE id = #{classId}")
    @Results({
        @Result(property = "classId", column = "id"),
        @Result(property = "className", column = "name"),
        @Result(property = "majorId", column = "major_id"),
        @Result(property = "startYear", column = "start_year")
    })
    Class findById(String classId);

    @Select("SELECT * FROM classes WHERE major_id = #{majorId}")
    @Results({
        @Result(property = "classId", column = "id"),
        @Result(property = "className", column = "name"),
        @Result(property = "majorId", column = "major_id"),
        @Result(property = "startYear", column = "start_year")
    })
    List<Class> findByMajorId(String majorId);
} 
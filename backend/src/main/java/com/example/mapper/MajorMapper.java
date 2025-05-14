package com.example.mapper;

import com.example.entity.Major;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import java.util.List;

@Mapper
public interface MajorMapper {
    
    @Select("SELECT * FROM majors")
    @Results({
        @Result(property = "majorId", column = "id"),
        @Result(property = "majorName", column = "name"),
        @Result(property = "departmentId", column = "department_id")
    })
    List<Major> findAll();

    @Select("SELECT * FROM majors WHERE id = #{majorId}")
    @Results({
        @Result(property = "majorId", column = "id"),
        @Result(property = "majorName", column = "name"),
        @Result(property = "departmentId", column = "department_id")
    })
    Major findById(String majorId);

    @Select("SELECT * FROM majors WHERE department_id = #{departmentId}")
    @Results({
        @Result(property = "majorId", column = "id"),
        @Result(property = "majorName", column = "name"),
        @Result(property = "departmentId", column = "department_id")
    })
    List<Major> findByDepartmentId(String departmentId);
} 
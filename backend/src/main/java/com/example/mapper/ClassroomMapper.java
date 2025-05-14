package com.example.mapper;

import com.example.entity.Classroom;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ClassroomMapper {
    Classroom findById(String classroomId);
    List<Classroom> findByBuilding(String building);
    List<Classroom> findByMinCapacity(Integer minCapacity);
    Classroom findByBuildingAndRoom(String building, String roomNumber);
} 
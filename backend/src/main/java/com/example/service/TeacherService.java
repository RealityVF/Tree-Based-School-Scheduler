package com.example.service;

import com.example.entity.Teacher;
import com.example.dto.TeacherDTO;
import com.example.mapper.TeacherMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeacherService {
    private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);
    
    @Autowired
    private TeacherMapper teacherMapper;

    /**
     * 搜索教师
     * @param query 搜索关键词
     * @return 教师列表
     */
    public List<Teacher> searchTeachers(String query) {
        return teacherMapper.searchTeachers(query);
    }

    /**
     * 根据ID获取教师
     * @param id 教师ID
     * @return 教师DTO
     */
    public Optional<TeacherDTO> getTeacherById(String id) {
        Teacher teacher = teacherMapper.findById(id);
        return Optional.ofNullable(convertToDTO(teacher));
    }

    /**
     * 创建新教师
     * @param teacherDTO 教师DTO
     * @return 创建的教师DTO
     */
    @Transactional
    public Optional<TeacherDTO> createTeacher(TeacherDTO teacherDTO) {
        Teacher teacher = convertToEntity(teacherDTO);
        if (teacher != null) {
            teacherMapper.insert(teacher);
            return Optional.of(convertToDTO(teacher));
        }
        return Optional.empty();
    }

    /**
     * 更新教师信息
     * @param id 教师ID
     * @param teacherDTO 教师DTO
     * @return 更新后的教师DTO
     */
    @Transactional
    public Optional<TeacherDTO> updateTeacher(String id, TeacherDTO teacherDTO) {
        if (!teacherMapper.existsById(id)) {
            return Optional.empty();
        }
        Teacher teacher = convertToEntity(teacherDTO);
        teacher.setId(id);
        teacherMapper.update(teacher);
        return Optional.of(convertToDTO(teacher));
    }

    /**
     * 删除教师
     * @param id 教师ID
     * @return 是否删除成功
     */
    @Transactional
    public boolean deleteTeacher(String id) {
        if (!teacherMapper.existsById(id)) {
            return false;
        }
        teacherMapper.delete(id);
        return true;
    }

    private TeacherDTO convertToDTO(Teacher teacher) {
        if (teacher == null) {
            return null;
        }
        TeacherDTO dto = new TeacherDTO();
        dto.setId(teacher.getId());
        dto.setName(teacher.getName());
        dto.setEnglishName(teacher.getEnglishName());
        dto.setGender(teacher.getGender());
        dto.setEthnicity(teacher.getEthnicity());
        dto.setTitle(teacher.getTitle());
        dto.setDepartmentId(teacher.getDepartmentId());
        dto.setIsExternal(teacher.getIsExternal());
        dto.setTeacherType(teacher.getTeacherType());
        dto.setSatisfaction(teacher.getSatisfaction());
        return dto;
    }

    private Teacher convertToEntity(TeacherDTO dto) {
        if (dto == null) {
            return null;
        }
        Teacher teacher = new Teacher();
        teacher.setId(dto.getId());
        teacher.setName(dto.getName());
        teacher.setEnglishName(dto.getEnglishName());
        teacher.setGender(dto.getGender());
        teacher.setEthnicity(dto.getEthnicity());
        teacher.setTitle(dto.getTitle());
        teacher.setDepartmentId(dto.getDepartmentId());
        teacher.setIsExternal(dto.getIsExternal());
        teacher.setTeacherType(dto.getTeacherType());
        teacher.setSatisfaction(dto.getSatisfaction());
        return teacher;
    }

    /**
     * 获取教师列表
     * @param keyword 关键词
     * @param departmentId 部门ID
     * @param teacherType 教师类型
     * @param page 页码
     * @param size 每页大小
     * @return 教师列表
     */
    public List<Teacher> getTeacherList(String keyword, String departmentId, String teacherType, int page, int size) {
        int offset = (page - 1) * size;
        return teacherMapper.getTeacherList(keyword, departmentId, teacherType, offset, size);
    }

    /**
     * 统计教师总数
     * @param keyword 关键词
     * @param departmentId 部门ID
     * @param teacherType 教师类型
     * @return 总数
     */
    public int countTeachers(String keyword, String departmentId, String teacherType) {
        return teacherMapper.countTeachers(keyword, departmentId, teacherType);
    }
} 
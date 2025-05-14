package com.example.controller;

import com.example.dto.TeacherDTO;
import com.example.entity.Teacher;
import com.example.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashMap;
import java.util.Arrays;

@RestController
@RequestMapping("/api/teachers")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, allowCredentials = "true")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    private TeacherDTO convertToDTO(Teacher teacher) {
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
        dto.setCreatedAt(teacher.getCreatedAt());
        dto.setUpdatedAt(teacher.getUpdatedAt());
        dto.setPhone(teacher.getPhone());
        dto.setEmail(teacher.getEmail());
        dto.setMaxWeeklyHours(teacher.getMaxWeeklyHours());
        dto.setResearchDirection(teacher.getResearchDirection());
        return dto;
    }

    /**
     * 搜索教师
     * @param query 搜索关键词
     * @return 教师列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<TeacherDTO>> searchTeachers(@RequestParam String query) {
        List<TeacherDTO> teachers = teacherService.searchTeachers(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teachers);
    }

    /**
     * 根据ID获取教师
     * @param id 教师ID
     * @return 教师信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable String id) {
        return teacherService.getTeacherById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 创建新教师
     * @param teacherDTO 教师信息
     * @return 创建的教师信息
     */
    @PostMapping
    public ResponseEntity<TeacherDTO> createTeacher(@RequestBody TeacherDTO teacherDTO) {
        return teacherService.createTeacher(teacherDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * 更新教师信息
     * @param id 教师ID
     * @param teacherDTO 教师信息
     * @return 更新后的教师信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<TeacherDTO> updateTeacher(@PathVariable String id, @RequestBody TeacherDTO teacherDTO) {
        return teacherService.updateTeacher(id, teacherDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 删除教师
     * @param id 教师ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable String id) {
        return teacherService.deleteTeacher(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    /**
     * 获取教师列表
     * @param keyword 关键词
     * @param departmentId 部门ID
     * @param teacherType 教师类型
     * @param page 页码
     * @param size 每页大小
     * @return 教师列表和总数
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getTeacherList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String teacherType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<TeacherDTO> teachers = teacherService.getTeacherList(keyword, departmentId, teacherType, page, size)
            .stream()
            .map(teacher -> convertToDTO(teacher))
            .collect(Collectors.toList());
        
        int total = teacherService.countTeachers(keyword, departmentId, teacherType);
        
        Map<String, Object> response = new HashMap<>();
        response.put("teachers", teachers);
        response.put("total", total);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/options")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getTeacherOptions() {
        Map<String, List<Map<String, String>>> options = new HashMap<>();
        
        // 职称选项
        List<Map<String, String>> titles = Arrays.asList(
            createOption("教授", "教授"),
            createOption("副教授", "副教授"),
            createOption("讲师", "讲师"),
            createOption("助教", "助教"),
            createOption("技工学校教师", "技工学校教师"),
            createOption("其他", "其他")
        );
        options.put("titles", titles);
        
        // 教职工类别选项
        List<Map<String, String>> types = Arrays.asList(
            createOption("校本部教职工", "校本部教职工"),
            createOption("外聘", "外聘"),
            createOption("援疆教师", "援疆教师"),
            createOption("其他", "其他")
        );
        options.put("types", types);
        
        // 民族选项
        List<Map<String, String>> ethnicities = Arrays.asList(
            createOption("汉族", "汉族"),
            createOption("回族", "回族"),
            createOption("维吾尔族", "维吾尔族"),
            createOption("哈萨克族", "哈萨克族"),
            createOption("其他", "其他")
        );
        options.put("ethnicities", ethnicities);
        
        return ResponseEntity.ok(options);
    }
    
    private Map<String, String> createOption(String value, String label) {
        Map<String, String> option = new HashMap<>();
        option.put("value", value);
        option.put("label", label);
        return option;
    }
} 
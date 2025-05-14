package com.example.service.impl;

import com.example.dto.TeacherCreateDTO;
import com.example.entity.Teacher;
import com.example.exception.BusinessException;
import com.example.mapper.TeacherMapper;
import com.example.service.TeacherAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 教师账号服务实现类
 */
@Service
public class TeacherAccountServiceImpl implements TeacherAccountService {

    private static final Logger logger = LoggerFactory.getLogger(TeacherAccountServiceImpl.class);

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 创建单个教师账号
     * 注意：教师号同时作为登录账号，初始密码为教师号后六位
     * @param dto 教师账号创建信息
     * @return 创建的教师信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public Teacher createTeacherAccount(TeacherCreateDTO dto) {
        logger.info("开始创建教师账号，接收到的参数: {}", dto);
        
        // 生成安全的初始密码
        String initialPassword = generateSecurePassword(dto.getTeacherId());
        logger.info("教师号: {}, 生成的初始密码: {}", dto.getTeacherId(), initialPassword);
        String passwordHash = passwordEncoder.encode(initialPassword);
        logger.info("生成的密码哈希: {}", passwordHash);
        
        // 验证密码哈希
        boolean matches = passwordEncoder.matches(initialPassword, passwordHash);
        logger.info("密码验证结果: {}", matches);
        
        // 测试数据库中已有的哈希
        String dbHash = "$2a$10$7O2MNNp8oQXbO7sC1tMG3egDg1WVNeVnhL5lhaVTQ.oTew18zbzAW";
        boolean dbMatches = passwordEncoder.matches(initialPassword, dbHash);
        logger.info("数据库哈希验证结果: {} (密码: {}, 哈希: {})", dbMatches, initialPassword, dbHash);
        
        // 如果是教师ID为100001，特别测试
        if ("100001".equals(initialPassword)) {
            boolean specialMatches = passwordEncoder.matches("100001", dbHash);
            logger.info("特殊测试 - 100001密码与数据库哈希匹配结果: {}", specialMatches);
        }
        
        // 创建教师基本信息
        logger.info("开始创建教师基本信息，教师号: {}, 姓名: {}", dto.getTeacherId(), dto.getName());
        Teacher teacher = new Teacher();
        teacher.setId(dto.getTeacherId());
        teacher.setName(dto.getName());
        teacher.setEnglishName(dto.getEnglishName());
        teacher.setGender(dto.getGenderEnum());
        teacher.setEthnicity(dto.getEthnicity());
        teacher.setTitle(dto.getTitle());
        teacher.setDepartmentId(dto.getDepartment());
        teacher.setIsExternal(dto.getIsOutsourced());
        teacher.setTeacherType(dto.getTeacherType());
        teacher.setSatisfaction(dto.getSatisfaction());
        teacher.setIsActive(dto.getIsActive() ? 1 : 0);
        teacher.setPhone(dto.getPhone());
        teacher.setEmail(dto.getEmail());
        teacher.setMaxWeeklyHours(dto.getMaxWeeklyHours());
        teacher.setResearchDirection(dto.getResearchDirection());
        teacher.setPasswordHash(passwordHash);
        
        // 设置时间戳
        LocalDateTime now = LocalDateTime.now();
        teacher.setCreatedAt(now);
        teacher.setUpdatedAt(now);
        teacher.setPasswordUpdatedAt(now);
        
        try {
            // 先创建教师基本信息
            teacherMapper.insert(teacher);
            
            // 创建教师账号记录
            logger.info("开始创建教师账号记录，教师号: {}", dto.getTeacherId());
            int result = teacherMapper.createTeacherAccount(
                dto.getTeacherId(),
                passwordHash,  // 使用相同的密码哈希
                dto.getEmail(),
                null,  // last_login
                dto.getIsActive(),
                now  // password_updated_at
            );
            
            if (result == 0) {
                logger.warn("教师账号已存在，跳过创建: {}", dto.getTeacherId());
            } else {
                logger.info("教师账号创建成功，教师号: {}", dto.getTeacherId());
            }
            
            return teacher;
        } catch (Exception e) {
            logger.error("创建教师账号失败: {}", e.getMessage(), e);
            throw new BusinessException("创建教师账号失败: " + e.getMessage());
        }
    }

    /**
     * 生成初始密码
     * 使用教师工号的后六位作为密码
     */
    @Override
    public String generateSecurePassword(String teacherId) {
        if (teacherId == null || teacherId.length() < 6) {
            throw new BusinessException("教师工号长度不足6位");
        }
        return teacherId.substring(teacherId.length() - 6);
    }

    @Override
    public List<Teacher> getTeacherAccounts(String keyword, String departmentId, String teacherType, int page, int size) {
        try {
            logger.info("开始查询教师账号列表 - 关键词: {}, 部门: {}, 教师类型: {}, 页码: {}, 每页大小: {}", 
                keyword, departmentId, teacherType, page, size);
            
            int offset = (page - 1) * size;
            List<Teacher> teachers = teacherMapper.getTeacherList(keyword, departmentId, teacherType, offset, size);
            
            logger.info("查询到 {} 条教师账号记录", teachers.size());
            return teachers;
        } catch (Exception e) {
            logger.error("查询教师账号列表失败: {}", e.getMessage());
            throw new BusinessException("查询教师账号列表失败: " + e.getMessage());
        }
    }

    @Override
    public int countTeacherAccounts(String keyword, String departmentId, String teacherType) {
        try {
            logger.info("开始统计教师账号总数 - 关键词: {}, 部门: {}, 教师类型: {}", keyword, departmentId, teacherType);
            
            int count = teacherMapper.countTeachers(keyword, departmentId, teacherType);
            
            logger.info("统计到 {} 条教师账号记录", count);
            return count;
        } catch (Exception e) {
            logger.error("统计教师账号总数失败: {}", e.getMessage());
            throw new BusinessException("统计教师账号总数失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Teacher updateTeacherAccount(String teacherId, TeacherCreateDTO dto) {
        try {
            logger.info("开始更新教师账号，教师号: {}", teacherId);
            
            // 检查教师是否存在
            Teacher existingTeacher = teacherMapper.findById(teacherId);
            if (existingTeacher == null) {
                throw new BusinessException("教师账号不存在: " + teacherId);
            }

            // 更新教师信息
            existingTeacher.setName(dto.getName());
            existingTeacher.setEnglishName(dto.getEnglishName());
            existingTeacher.setGender(dto.getGenderEnum());
            existingTeacher.setEthnicity(dto.getEthnicity());
            existingTeacher.setTitle(dto.getTitle());
            existingTeacher.setDepartmentId(dto.getDepartment());
            existingTeacher.setIsExternal(dto.getIsOutsourced());
            existingTeacher.setTeacherType(dto.getTeacherType());
            existingTeacher.setSatisfaction(dto.getSatisfaction());
            existingTeacher.setPhone(dto.getPhone());
            existingTeacher.setEmail(dto.getEmail());
            existingTeacher.setMaxWeeklyHours(dto.getMaxWeeklyHours());
            existingTeacher.setResearchDirection(dto.getResearchDirection());
            existingTeacher.setIsActive(dto.getIsActive() ? 1 : 0);
            existingTeacher.setUpdatedAt(LocalDateTime.now());

            // 更新数据库
            teacherMapper.update(existingTeacher);
            
            logger.info("教师账号更新成功: {}", teacherId);
            return existingTeacher;
        } catch (Exception e) {
            logger.error("更新教师账号失败: {}", e.getMessage(), e);
            throw new BusinessException("更新教师账号失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean toggleTeacherAccountStatus(String teacherId, boolean enable) {
        try {
            // 查找教师
            Teacher teacher = teacherMapper.findById(teacherId);
            if (teacher == null) {
                logger.error("教师不存在: {}", teacherId);
                return false;
            }

            // 更新状态
            teacher.setIsActive(enable ? 1 : 0);
            teacher.setUpdatedAt(LocalDateTime.now());

            // 保存更新
            teacherMapper.update(teacher);
            logger.info("{}教师账号成功: {}", enable ? "启用" : "禁用", teacherId);
            return true;
        } catch (Exception e) {
            logger.error("{}教师账号时发生错误: {}", enable ? "启用" : "禁用", teacherId, e);
            throw new RuntimeException(enable ? "启用教师账号失败" : "禁用教师账号失败", e);
        }
    }

    @Override
    @Transactional
    public void resetTeacherPassword(String teacherId, String newPassword) {
        // 检查教师是否存在
        Teacher teacher = teacherMapper.findById(teacherId);
        if (teacher == null) {
            throw new BusinessException("教师账号不存在: " + teacherId);
        }

        // 检查账号是否被禁用
        Boolean isActive = teacherMapper.getIsActive(teacherId);
        if (Boolean.FALSE.equals(isActive)) {
            throw new BusinessException("教师账号已被禁用，无法重置密码");
        }

        // 加密新密码
        String passwordHash = passwordEncoder.encode(newPassword);
        LocalDateTime now = LocalDateTime.now();
        
        // 更新 teacher_accounts 表中的密码
        teacherMapper.updatePassword(teacherId, passwordHash, now);
        
        // 更新 teachers 表中的密码
        teacher.setPasswordHash(passwordHash);
        teacher.setPasswordUpdatedAt(now);
        teacher.setUpdatedAt(now);
        teacherMapper.update(teacher);
        
        logger.info("教师密码重置成功，教师号: {}", teacherId);
    }
} 
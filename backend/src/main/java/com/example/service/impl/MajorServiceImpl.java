package com.example.service.impl;

import com.example.entity.Major;
import com.example.mapper.MajorMapper;
import com.example.service.MajorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MajorServiceImpl implements MajorService {
    private static final Logger logger = LoggerFactory.getLogger(MajorServiceImpl.class);

    @Autowired
    private MajorMapper majorMapper;

    @Override
    public List<Major> getAllMajors() {
        logger.info("Service层: 开始获取所有专业信息");
        List<Major> majors = majorMapper.findAll();
        logger.info("Service层: 获取到专业数量: {}", majors.size());
        majors.forEach(major -> logger.debug("Service层: 专业信息: {}", major));
        return majors;
    }

    @Override
    public Major getMajorById(String majorId) {
        logger.info("Service层: 开始获取专业信息，专业ID: {}", majorId);
        Major major = majorMapper.findById(majorId);
        if (major != null) {
            logger.info("Service层: 成功获取专业信息: {}", major);
        } else {
            logger.warn("Service层: 未找到专业信息，专业ID: {}", majorId);
        }
        return major;
    }

    @Override
    public List<Major> getMajorsByDepartmentId(String departmentId) {
        logger.info("Service层: 开始获取院系专业信息，院系ID: {}", departmentId);
        List<Major> majors = majorMapper.findByDepartmentId(departmentId);
        logger.info("Service层: 获取到院系专业数量: {}", majors.size());
        return majors;
    }
} 
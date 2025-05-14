package com.example.service.impl;

import com.example.entity.Class;
import com.example.mapper.ClassMapper;
import com.example.service.ClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {

    private static final Logger logger = LoggerFactory.getLogger(ClassServiceImpl.class);

    @Autowired
    private ClassMapper classMapper;

    @Override
    public List<Class> getAllClasses() {
        logger.info("Service层: 开始获取所有班级信息");
        List<Class> classes = classMapper.findAll();
        logger.info("Service层: 获取到班级数量: {}", classes.size());
        classes.forEach(clazz -> logger.debug("Service层: 班级信息: {}", clazz));
        return classes;
    }

    @Override
    public Class getClassById(String classId) {
        logger.info("Service层: 开始根据ID获取班级信息, ID: {}", classId);
        Class clazz = classMapper.findById(classId);
        if (clazz != null) {
            logger.info("Service层: 成功获取班级信息: {}", clazz);
        } else {
            logger.warn("Service层: 未找到ID为{}的班级", classId);
        }
        return clazz;
    }

    @Override
    public List<Class> getClassesByMajorId(String majorId) {
        logger.info("Service层: 开始获取专业下的班级信息, 专业ID: {}", majorId);
        List<Class> classes = classMapper.findByMajorId(majorId);
        logger.info("Service层: 获取到专业{}下的班级数量: {}", majorId, classes.size());
        classes.forEach(clazz -> logger.debug("Service层: 班级信息: {}", clazz));
        return classes;
    }
} 
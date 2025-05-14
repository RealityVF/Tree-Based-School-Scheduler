package com.example.service.impl;

import com.example.entity.CourseSchedule;
import com.example.dto.CourseScheduleDTO;
import com.example.mapper.CourseScheduleMapper;
import com.example.service.CourseScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
public class CourseScheduleServiceImpl implements CourseScheduleService {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseScheduleServiceImpl.class);
    
    @Autowired
    private CourseScheduleMapper courseScheduleMapper;
    
    @Override
    public CourseSchedule getCourseDetail(String courseId, String academicYear, Integer semester) {
        logger.debug("开始查询课程详情 - 课程: {}, 学年: {}, 学期: {}", courseId, academicYear, semester);
        try {
            CourseSchedule courseDetail = courseScheduleMapper.getCourseDetail(courseId, academicYear, semester);
            if (courseDetail == null) {
                logger.warn("未找到课程详情 - 课程: {}, 学年: {}, 学期: {}", courseId, academicYear, semester);
            } else {
                logger.debug("课程详情查询完成 - 课程: {}", courseId);
            }
            return courseDetail;
        } catch (Exception e) {
            logger.error("查询课程详情时发生错误 - 课程: " + courseId, e);
            throw e;
        }
    }
    
    @Override
    public List<CourseScheduleDTO> getStudentSemesterSchedule(String classId, String academicYear, Integer semester) {
        logger.debug("开始查询学生课表 - 班级: {}, 学年: {}, 学期: {}", classId, academicYear, semester);
        try {
            // 参数验证
            if (classId == null || classId.trim().isEmpty()) {
                logger.error("班级ID为空");
                throw new IllegalArgumentException("班级ID不能为空");
            }
            if (academicYear == null || academicYear.trim().isEmpty()) {
                logger.error("学年为空");
                throw new IllegalArgumentException("学年不能为空");
            }
            if (semester == null || semester < 1 || semester > 2) {
                logger.error("无效的学期: {}", semester);
                throw new IllegalArgumentException("无效的学期");
            }

            logger.debug("开始执行数据库查询...");
            List<CourseScheduleDTO> schedules = courseScheduleMapper.findStudentSemesterSchedule(classId, academicYear, semester);
            
            if (schedules == null) {
                logger.warn("查询结果为 null");
                return new ArrayList<>();
            }
            
            logger.debug("查询完成 - 获取到 {} 条记录", schedules.size());
            if (schedules.isEmpty()) {
                logger.warn("未找到课表记录 - 班级: {}, 学年: {}, 学期: {}", classId, academicYear, semester);
            } else {
                // 记录第一条数据的详细信息用于调试
                CourseScheduleDTO firstSchedule = schedules.get(0);
                logger.debug("第一条记录详情: courseId={}, courseName={}, weekday={}, startSection={}, endSection={}, classroomId={}, teacherId={}",
                    firstSchedule.getCourseId(),
                    firstSchedule.getCourseName(),
                    firstSchedule.getWeekday(),
                    firstSchedule.getStartSection(),
                    firstSchedule.getEndSection(),
                    firstSchedule.getClassroomId(),
                    firstSchedule.getTeacherId()
                );
            }
            
            return schedules;
        } catch (Exception e) {
            logger.error("查询学生课表时发生错误 - 班级: " + classId, e);
            throw e;
        }
    }
    
    @Override
    public List<CourseScheduleDTO> getTeacherSemesterSchedule(String teacherId, String academicYear, Integer semester) {
        logger.debug("开始查询教师课表 - 教师: {}, 学年: {}, 学期: {}", teacherId, academicYear, semester);
        try {
            List<CourseScheduleDTO> schedules = courseScheduleMapper.findTeacherSemesterSchedule(teacherId, academicYear, semester);
            logger.debug("教师课表查询完成 - 教师: {}, 记录数: {}", teacherId, schedules.size());
            if (schedules.isEmpty()) {
                logger.warn("未找到教师课表记录 - 教师: {}, 学年: {}, 学期: {}", teacherId, academicYear, semester);
            }
            return schedules;
        } catch (Exception e) {
            logger.error("查询教师课表时发生错误 - 教师: " + teacherId, e);
            throw e;
        }
    }
    
    @Override
    public CourseSchedule getScheduleById(Integer id) {
        logger.debug("开始查询课程安排 - ID: {}", id);
        try {
            CourseSchedule schedule = courseScheduleMapper.findById(id);
            if (schedule == null) {
                logger.warn("未找到课程安排 - ID: {}", id);
            } else {
                logger.debug("课程安排查询完成 - ID: {}", id);
            }
            return schedule;
        } catch (Exception e) {
            logger.error("查询课程安排时发生错误 - ID: " + id, e);
            throw e;
        }
    }
    
    @Override
    public List<CourseSchedule> getSchedulesByClassroomAndTime(
            String classroomId, 
            String academicYear,
            Integer semester,
            Integer weekday, 
            Integer startSection, 
            Integer endSection) {
        logger.debug("开始查询教室课程安排 - 教室: {}, 学年: {}, 学期: {}, 星期: {}, 节次: {}-{}", 
            classroomId, academicYear, semester, weekday, startSection, endSection);
        try {
            List<CourseSchedule> schedules = courseScheduleMapper.findByClassroomAndTime(
                classroomId, academicYear, semester, weekday, startSection, endSection);
            logger.debug("教室课程安排查询完成 - 教室: {}, 记录数: {}", classroomId, schedules.size());
            if (schedules.isEmpty()) {
                logger.warn("未找到教室课程安排记录 - 教室: {}, 学年: {}, 学期: {}, 星期: {}, 节次: {}-{}", 
                    classroomId, academicYear, semester, weekday, startSection, endSection);
            }
            return schedules;
        } catch (Exception e) {
            logger.error("查询教室课程安排时发生错误 - 教室: " + classroomId, e);
            throw e;
        }
    }
    
    @Override
    @Transactional
    public CourseSchedule createSchedule(CourseSchedule schedule) {
        logger.debug("开始创建课程安排 - 课程: {}, 教室: {}", schedule.getCourseId(), schedule.getClassroomId());
        try {
            int result = courseScheduleMapper.insert(schedule);
            if (result > 0) {
                logger.debug("课程安排创建成功 - ID: {}", schedule.getId());
                return schedule;
            } else {
                logger.error("课程安排创建失败 - 课程: {}, 教室: {}", schedule.getCourseId(), schedule.getClassroomId());
                return null;
            }
        } catch (Exception e) {
            logger.error("创建课程安排时发生错误 - 课程: " + schedule.getCourseId(), e);
            throw e;
        }
    }
    
    @Override
    @Transactional
    public boolean updateSchedule(CourseSchedule schedule) {
        logger.debug("开始更新课程安排 - ID: {}", schedule.getId());
        try {
            int result = courseScheduleMapper.update(schedule);
            if (result > 0) {
                logger.debug("课程安排更新成功 - ID: {}", schedule.getId());
                return true;
            } else {
                logger.error("课程安排更新失败 - ID: {}", schedule.getId());
                return false;
            }
        } catch (Exception e) {
            logger.error("更新课程安排时发生错误 - ID: " + schedule.getId(), e);
            throw e;
        }
    }
    
    @Override
    @Transactional
    public boolean deleteSchedule(Integer id) {
        logger.debug("开始删除课程安排 - ID: {}", id);
        try {
            int result = courseScheduleMapper.delete(id);
            if (result > 0) {
                logger.debug("课程安排删除成功 - ID: {}", id);
                return true;
            } else {
                logger.error("课程安排删除失败 - ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("删除课程安排时发生错误 - ID: " + id, e);
            throw e;
        }
    }
} 
package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@TableName("students")
public class Student {
    @TableId(type = IdType.INPUT)
    @Pattern(regexp = "^\\d{14}$", message = "学号必须是14位数字")  // 格式：年份4位+部门号4位+专业号2位+班级号2位+学生编号2位
    private String id; // 学号

    private String name; // 姓名
    private String gender; // 性别
    private String idCard; // 身份证号
    private Date birthday; // 出生日期
    private String ethnicity; // 民族
    private String politicalStatus; // 政治面貌
    private String classId; // 所属班级
    private String majorId; // 所属专业
    private String departmentId; // 所属院系
    private String educationLevel; // 培养层次
    private Date enrollmentDate; // 入学日期
    private Date expectedGraduation; // 预计毕业日期
    private String studentStatus; // 学籍状态
    private Boolean isActive; // 是否在校
    private String phone; // 联系电话
    private String email; // 电子邮箱
    private String address; // 家庭住址
    private String notes; // 备注
    private Date createdAt; // 创建时间
    private Date updatedAt; // 更新时间
}
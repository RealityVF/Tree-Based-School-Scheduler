package com.example.dto;

import com.example.entity.Teacher;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 教师账号创建DTO
 * 注意：教师号同时也是登录账号
 */
@Data
public class TeacherCreateDTO {
    @NotBlank(message = "教师号不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "教师号必须是11位数字")
    private String teacherId;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不应超过50个字符")
    private String name;

    @NotBlank(message = "部门不能为空")
    @Size(max = 20, message = "部门ID长度不应超过20个字符")
    private String department;

    @NotBlank(message = "教师类型不能为空")
    @Size(max = 20, message = "教师类型长度不应超过20个字符")
    @Pattern(regexp = "^(校本部教职工|外聘|援疆教师|其他)$", message = "无效的教师类型")
    private String teacherType;

    @NotBlank(message = "性别不能为空")
    @Pattern(regexp = "^(男|女)$", message = "性别必须是'男'或'女'")
    private String gender;

    @Size(max = 20, message = "民族长度不应超过20个字符")
    private String ethnicity;

    @Size(max = 20, message = "职称长度不应超过20个字符")
    private String title;

    private Boolean isOutsourced = false;

    @Size(max = 50, message = "英文姓名长度不应超过50个字符")
    private String englishName;

    private Boolean isActive = true;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不应超过100个字符")
    private String email;

    @Pattern(regexp = "^(1[3-9]\\d{9})?$", message = "手机号格式不正确")
    @Size(max = 20, message = "手机号长度不应超过20个字符")
    private String phone;

    @Size(max = 200, message = "研究方向长度不应超过200个字符")
    private String researchDirection;

    @Min(value = 0, message = "最大周课时不能为负数")
    @Max(value = 40, message = "最大周课时不能超过40")
    private Integer maxWeeklyHours = 16;

    private Boolean satisfaction = true;

    // 获取性别枚举值
    public Teacher.Gender getGenderEnum() {
        return "男".equals(gender) ? Teacher.Gender.男 : Teacher.Gender.女;
    }

    // 用于日志输出的toString方法
    @Override
    public String toString() {
        return "TeacherCreateDTO{" +
                "teacherId='" + teacherId + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", teacherType='" + teacherType + '\'' +
                ", gender='" + gender + '\'' +
                ", ethnicity='" + ethnicity + '\'' +
                ", title='" + title + '\'' +
                ", isOutsourced=" + isOutsourced +
                ", englishName='" + englishName + '\'' +
                ", isActive=" + isActive +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", maxWeeklyHours=" + maxWeeklyHours +
                ", satisfaction=" + satisfaction +
                '}';
    }
} 
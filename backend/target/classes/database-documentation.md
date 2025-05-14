# 排课管理系统数据库文档

## 概述

本文档提供了排课管理系统数据库的详细设计说明，包括表结构、字段定义、关系约束等信息，供开发和维护人员参考。

## 数据库表结构

### 1. 管理员账户表 (admin_accounts)

管理系统管理员的账户信息。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | char(8) | 管理员ID(年份4位+类型1位+序号3位) | 主键 |
| password_hash | char(60) | 密码哈希 | 非空 |
| email | varchar(100) | 邮箱 | 唯一 |
| role | enum | 角色类型('系统管理员','院系管理员') | 非空 |
| department_id | varchar(20) | 所属院系 | 外键 -> departments(id) |
| last_login | datetime | 最后登录时间 | |
| is_active | tinyint(1) | 是否激活 | 默认1 |
| password_updated_at | datetime | 密码更新时间 | 默认当前时间 |
| created_at | timestamp | 创建时间 | 默认当前时间 |
| updated_at | timestamp | 更新时间 | 自动更新 |

### 2. 教学楼表 (buildings)

管理校内各教学楼信息。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | varchar(20) | 教学楼编号 | 主键 |
| name | varchar(50) | 教学楼名称 | 非空 |
| campus | varchar(20) | 校区名称 | 非空 |
| is_active | tinyint(1) | 是否可用 | 默认1 |
| created_at | timestamp | 创建时间 | 默认当前时间 |
| updated_at | timestamp | 更新时间 | 自动更新 |

### 3. 班级表 (classes)

管理学生班级信息。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | char(12) | 班级编号(年份4位+部门号4位+专业号2位+班级号2位) | 主键 |
| name | varchar(50) | 班级名称 | 非空 |
| short_name | varchar(20) | 班级简称 | |
| major_id | varchar(20) | 所属专业 | 外键 -> majors(id) |
| direction_id | varchar(20) | 专业方向 | 外键 -> major_directions(id) |
| grade | int | 年级 | 非空 |
| education_level | varchar(20) | 培养层次 | 非空 |
| class_type | varchar(20) | 班级类别 | 非空 |
| counselor_id | varchar(20) | 辅导员工号 | 外键 -> teachers(id) |
| head_teacher_id | varchar(20) | 班主任工号 | 外键 -> teachers(id) |
| expected_graduation | int | 预计毕业年度 | |
| is_graduated | tinyint(1) | 是否毕业 | 默认0 |
| student_count | int | 班级人数 | 默认0 |
| male_count | int | 男生人数 | 默认0 |
| female_count | int | 女生人数 | 默认0 |
| max_student_count | int | 班级最大人数 | |
| admission_year | int | 入学年份 | 非空 |
| head_teacher_phone | varchar(20) | 班主任联系电话 | |
| graduation_term | varchar(20) | 毕业学年学期 | |
| is_expanded | tinyint(1) | 是否扩招 | 默认0 |
| academic_advisor | varchar(50) | 学业导师 | |
| created_at | timestamp | 创建时间 | 默认当前时间 |
| updated_at | timestamp | 更新时间 | 自动更新 |

### 4. 教室时段可用表 (classroom_time_slots)

记录教室在特定时段是否可用。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | bigint | 主键ID | 自增主键 |
| classroom_id | varchar(20) | 教室ID | 非空 |
| week_day | int | 星期几(1-7) | 非空 |
| time_slot | int | 时段(1-12) | 非空 |
| is_available | tinyint(1) | 是否可用(1:可用,0:不可用) | 默认1 |
| semester_id | varchar(20) | 学期ID | 非空 |
| created_at | datetime | 创建时间 | 默认当前时间 |
| updated_at | datetime | 更新时间 | 自动更新 |

### 5. 教室表 (classrooms)

管理教学场地信息。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | varchar(20) | 教室编号 | 主键 |
| name | varchar(50) | 教室名称 | 非空 |
| building_id | varchar(20) | 所属教学楼 | 外键 -> buildings(id) |
| campus | varchar(20) | 所在校区 | 非空 |
| floor | int | 所在楼层 | 非空 |
| type | varchar(20) | 教室类型 | 非空 |
| exam_capacity | int | 考场容纳人数 | 非空 |
| class_capacity | int | 最大上课容纳人数 | 非空 |
| has_ac | tinyint(1) | 是否有空调 | 默认0 |
| is_active | tinyint(1) | 是否启用 | 默认1 |
| description | text | 教室描述 | |
| department_name | varchar(50) | 管理部门 | |
| weekly_hours | int | 周安排学时 | 默认0 |
| area | decimal(10,2) | 教室面积 | |
| furniture_type | varchar(20) | 桌椅类型 | |
| created_at | timestamp | 创建时间 | 默认当前时间 |
| updated_at | timestamp | 更新时间 | 自动更新 |
| department_id | varchar(20) | 部门ID | |

### 6. 课程班级关系表 (course_class_relations)

记录课程与班级的关联关系。

### 7. 课程评价表 (course_evaluations)

存储学生对课程的评价信息。

### 8. 课程表 (courses)

管理课程基本信息。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | varchar(20) | 课程编号 | 主键 |
| name | varchar(100) | 课程名称 | 非空 |
| category | enum | 课程类别(A-纯理论课,B-理论+实践课,C-纯实践课,D-实验课,E-其他) | 非空 |
| attribute | varchar(20) | 课程属性 | |
| course_type | enum | 课程类型('必修','选修','公共') | 非空，默认'必修' |
| nature | varchar(20) | 课程性质 | |
| department_name | varchar(50) | 开课院系 | 非空 |
| is_active | tinyint(1) | 是否启用 | 默认1 |
| total_hours | int | 总学时 | 非空 |
| theory_hours | int | 理论学时 | 默认0 |
| experiment_hours | int | 实验学时 | 默认0 |
| computer_hours | int | 上机学时 | 默认0 |
| practice_hours | int | 实践学时 | 默认0 |
| credit | decimal(4,2) | 学分 | 非空 |
| weekly_hours | int | 周学时 | 非空 |
| is_practical | tinyint(1) | 是否纯实践环节 | 默认0 |
| created_at | datetime | 创建时间 | |

### 9. 部门表 (departments)

管理学校各部门信息。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | varchar(20) | 部门代码 | 主键 |
| name | varchar(50) | 部门名称 | 非空 |
| is_entity | tinyint(1) | 是否实体 | 默认1 |
| establish_date | date | 建立年月 | |
| expire_date | date | 失效日期 | |
| unit_type | varchar(20) | 单位类别 | |
| unit_category | varchar(20) | 单位办别 | |
| parent_dept_id | varchar(20) | 上级部门 | |
| building_id | varchar(20) | 固定教学楼 | |
| is_teaching_dept | tinyint(1) | 是否开课院系 | 默认0 |
| phone | varchar(20) | 固定电话 | |
| notes | text | 备注说明 | |
| is_active | tinyint(1) | 是否启用 | 默认1 |
| created_at | timestamp | 创建时间 | 默认当前时间 |
| updated_at | timestamp | 更新时间 | 自动更新 |
| description | varchar(200) | 部门描述 | |

### 10. 专业表 (majors)

管理学校各专业信息。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | varchar(20) | 专业编号 | 主键 |
| name | varchar(50) | 专业名称 | 非空 |
| department_id | varchar(20) | 所属院系 | 外键 -> departments(id) |
| education_level | varchar(20) | 培养层次 | 非空 |
| study_years | int | 学制 | 非空 |
| description | text | 专业描述 | |
| created_at | timestamp | 创建时间 | 默认当前时间 |
| updated_at | timestamp | 更新时间 | 自动更新 |

### 11. 专业方向表 (major_directions)

管理专业下的不同培养方向。

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | varchar(20) | 方向编号 | 主键 |
| major_id | varchar(20) | 所属专业 | 外键 -> majors(id) |
| name | varchar(50) | 方向名称 | 非空 |
| description | text | 方向描述 | |
| created_at | timestamp | 创建时间 | 默认当前时间 |
| updated_at | timestamp | 更新时间 | 自动更新 |

### 12. 排课任务表 (scheduling_tasks)

管理排课任务信息。

### 13. 学生账户表 (student_accounts)

管理学生账户信息。

### 14. 教师账户表 (teacher_accounts)

管理教师账户信息。

### 15. 教师表 (teachers)

管理教师基本信息。

## 数据库关系图

```
admin_accounts -----> departments
classes ---------> majors
classes ---------> major_directions
classes ---------> teachers (counselor_id)
classes ---------> teachers (head_teacher_id)
classrooms ------> buildings
course_class_relations -> courses
course_class_relations -> classes
major_directions -> majors
majors ----------> departments
student_accounts -> students
teacher_accounts -> teachers
```

## 附录：重要业务逻辑约束

1. 课程类别与学时约束:
   - A类(纯理论课): 理论学时>0, 实践学时=0
   - B类(理论+实践课): 理论学时>0, 实践学时>0
   - C类(纯实践课): 理论学时=0, 实践学时>0

2. 课程编号规则:
   - 学校通用课程: 学科代码+序号
   - 专业课程: 专业代码+序号

3. 教室编号规则:
   - 校区代码+楼宇代码+楼层+序号

4. ID编号规则:
   - 管理员ID: 年份4位+类型1位+序号3位
   - 班级编号: 年份4位+部门号4位+专业号2位+班级号2位
``` 
# 教务管理系统后端开发文档

## 1. 系统概述

本系统是一个完整的教务管理系统，主要功能包括用户认证、账号管理、课程管理、教室管理、排课管理、调课申请等模块。系统采用前后端分离架构，后端基于Spring Boot框架开发。

## 2. 技术架构

### 2.1 技术栈

- **编程语言**：Java 8+
- **框架**：Spring Boot 2.x
- **ORM框架**：MyBatis-Plus 3.x
- **数据库**：MySQL 8.0
- **安全框架**：Spring Security + JWT
- **API文档**：Swagger 3.0
- **构建工具**：Maven

### 2.2 项目结构

```
com.example
├── config                # 配置类，存放全局配置
├── controller            # 控制器层 - 处理HTTP请求
├── service               # 服务层 - 业务逻辑
│   └── impl              # 服务实现类
├── mapper                # MyBatis映射接口
├── entity                # 实体类 - 数据库映射对象
├── dto                   # 数据传输对象 - 请求/响应数据结构
├── repository            # 数据仓库 - JPA接口
├── exception             # 异常类定义
├── security              # 安全相关类
├── common                # 通用组件
│   ├── ApiResponse.java  # 统一响应格式
│   └── config            # 通用配置
└── util                  # 工具类
```

## 3. 通用API规范

### 3.1 基础URL

所有API请求的基础URL为: `/api`

### 3.2 统一响应格式

所有API响应遵循以下统一格式:

```json
{
  "success": true,           
  "message": "操作成功",      
  "data": "{ ... }(数据内容)",          
  "total": 100,              
  "code": 200                
}
```

### 3.3 错误处理

当请求失败时，响应格式如下:

```json
{
  "success": false,
  "message": "具体错误信息",
  "code": 400                 
}
```

错误代码说明:

| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 (未登录或Token无效) |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 3.4 认证机制

除了登录接口外，所有接口都需要在请求头中携带token进行认证:

```
Authorization: Bearer {token}
```

## 4. 核心模块API

### 4.1 用户认证

#### 4.1.1 登录

- **URL**: `/api/user/login`
- **方法**: POST
- **描述**: 用户登录系统
- **请求体**:

```json
{
  "username": "用户名",
  "password": "密码",
  "userType": "ADMIN/TEACHER/STUDENT"
}
```

- **响应**:

```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "jwt-token-here",
    "userInfo": {
      "userId": "用户ID",
      "name": "用户姓名",
      "role": "用户角色",
      "userType": "用户类型",
      "isActive": true,
      "email": "用户邮箱",
      "departmentId": "部门ID",
      "majorId": "专业ID",
      "gender": "性别",
      "title": "职称"
    }
  }
}
```

#### 4.1.2 修改密码

- **URL**: `/api/user/password`
- **方法**: PUT
- **描述**: 修改当前登录用户的密码
- **请求体**:

```json
{
  "oldPassword": "旧密码",
  "newPassword": "新密码"
}
```

- **响应**:

```json
{
  "success": true,
  "message": "密码修改成功"
}
```

### 4.2 教师账号管理

#### 4.2.1 创建教师账号

- **URL**: `/api/admin/accounts/teacher`
- **方法**: POST
- **描述**: 创建单个教师账号
- **权限**: 仅超级管理员可访问
- **请求体**:

```json
{
  "teacherId": "教师工号",
  "username": "登录用户名",
  "password": "登录密码",
  "name": "姓名",
  "department": "部门",
  "teacherType": "教师类型",
  "roleType": "角色类型 TEACHER/ADMIN/SUPER_ADMIN",
  "gender": "性别",
  "ethnicity": "民族",
  "title": "职称",
  "isOutsourced": false,
  "englishName": "英文姓名",
  "status": "ENABLED",
  "email": "邮箱",
  "phone": "电话"
}
```

- **响应**:

```json
{
  "success": true,
  "message": "教师账号创建成功",
  "data": {
    "id": 1,
    "teacherId": "教师工号",
    "username": "登录用户名",
    "name": "姓名"
  }
}
```

#### 4.2.2 获取教师账号列表

- **URL**: `/api/admin/accounts/teacher`
- **方法**: GET
- **描述**: 获取教师账号列表，支持分页和筛选
- **权限**: 仅超级管理员可访问
- **参数**:
  - page: 页码，从1开始
  - size: 每页记录数
  - searchKey: 搜索关键词（工号/姓名/账号）
  - department: 部门筛选

- **响应**:

```json
{
  "success": true,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "teacherId": "20230001",
      "username": "teacher1",
      "name": "张三",
      "department": "计算机科学与技术学院",
      "teacherType": "专任教师",
      "roleType": "TEACHER",
      "gender": "male",
      "ethnicity": "汉族",
      "title": "教授",
      "isOutsourced": false,
      "englishName": "Zhang San",
      "status": "ENABLED",
      "email": "zhangsan@example.com",
      "phone": "13800138000",
      "createdAt": "2023-06-01T12:00:00Z",
      "updatedAt": "2023-06-01T12:00:00Z"
    }
    
  ],
  "total": 100  
}
```

### 4.3 学生账号管理

#### 4.3.1 创建学生账号

- **URL**: `/api/admin/accounts/student`
- **方法**: POST
- **描述**: 创建单个学生账号
- **权限**: 仅超级管理员可访问
- **请求体**:

```json
{
  "studentId": "学号",
  "username": "登录用户名",
  "password": "登录密码",
  "name": "姓名",
  "gender": "性别",
  "ethnicity": "民族",
  "className": "班级",
  "faculty": "院系",
  "grade": "年级",
  "major": "专业",
  "counselor": "辅导员",
  "politicalStatus": "政治面貌",
  "status": "ENABLED",
  "email": "邮箱",
  "phone": "电话"
}
```

- **响应**:

```json
{
  "success": true,
  "message": "学生账号创建成功",
  "data": {
    "id": 1,
    "studentId": "学号",
    "username": "登录用户名",
    "name": "姓名"
   
  }
}
```

### 4.4 课程管理

#### 4.4.1 获取课程列表

- **URL**: `/api/courses`
- **方法**: GET
- **描述**: 获取课程列表，支持分页和条件筛选
- **参数**:
  - keyword: 搜索关键词(课程名称/编号)
  - courseType: 课程类型
  - departmentId: 开课院系ID
  - page: 页码(默认1)
  - size: 每页记录数(默认20)

- **响应**:

```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "courses": [
      {
        "id": "C001",
        "name": "高等数学",
        "category": "A",
        "attribute": "必修",
        "courseType": "理论课",
        "nature": "基础课",
        "departmentId": "D001",
        "departmentName": "数学学院",
        "isActive": true,
        "totalHours": 64,
        "theoryHours": 48,
        "experimentHours": 16,
        "computerHours": 0,
        "practiceHours": 0,
        "credit": 4.0,
        "weeklyHours": 4,
        "isPractical": false
      }
     
    ],
    "total": 100
  }
}
```

#### 4.4.2 创建课程

- **URL**: `/api/courses`
- **方法**: POST
- **描述**: 创建新课程
- **权限**: 需要管理员权限
- **请求体**:

```json
{
  "name": "课程名称",
  "category": "课程类别",
  "attribute": "课程属性",
  "course_type": "课程类型",
  "nature": "课程性质",
  "department_id": "开课院系ID",
  "department_name": "开课院系名称",
  "is_active": true,
  "total_hours": 64,
  "theory_hours": 48,
  "experiment_hours": 16,
  "computer_hours": 0,
  "practice_hours": 0,
  "credit": 4.0,
  "weekly_hours": 4,
  "is_practical": false
}
```

- **响应**:

```json
{
  "success": true,
  "message": "课程创建成功",
  "data": {
    "id": "C002",
    "name": "课程名称"
    
  }
}
```

#### 4.4.3 更新课程

- **URL**: `/api/courses/{id}`
- **方法**: PUT
- **描述**: 更新课程信息
- **权限**: 需要管理员权限
- **请求体**: 同创建课程，字段可选

- **响应**:

```json
{
  "success": true,
  "message": "课程更新成功",
  "data": {
    "id": "C002",
    "name": "课程名称"
    
  }
}
```

#### 4.4.4 删除课程

- **URL**: `/api/courses/{id}`
- **方法**: DELETE
- **描述**: 删除课程
- **权限**: 需要管理员权限

- **响应**:

```json
{
  "success": true,
  "message": "课程删除成功"
}
```

### 4.5 调课申请

#### 4.5.1 提交调课申请

- **URL**: `/api/schedule/change`
- **方法**: POST
- **描述**: 教师提交调课申请
- **权限**: 需要教师权限
- **请求体**:

```json
{
  "courseId": "课程ID",
  "courseName": "课程名称",
  "classId": "班级ID",
  "className": "班级名称",
  "originalDate": "2023-09-01",
  "originalSection": 1,
  "targetDate": "2023-09-08",
  "targetSection": 2,
  "reason": "调课原因"
}
```

- **响应**:

```json
{
  "success": true,
  "message": "调课申请提交成功",
  "data": {
    "id": "A001",
    "status": "PENDING",
    "applyTime": "2023-06-01T12:00:00Z"
    
  }
}
```

#### 4.5.2 审批调课申请

- **URL**: `/api/schedule/change/{id}/review`
- **方法**: POST
- **描述**: 管理员审批调课申请
- **权限**: 需要管理员权限
- **请求体**:

```json
{
  "status": "APPROVED",  
  "rejectReason": "拒绝原因"  
}
```

- **响应**:

```json
{
  "success": true,
  "message": "审核已完成"
}
```

## 5. 数据库设计

### 5.1 教师账号表(teacher_account)

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键ID |
| teacher_id | varchar(50) | 教师工号(唯一) |
| username | varchar(50) | 用户名(唯一) |
| password | varchar(100) | 密码(加密存储) |
| name | varchar(50) | 姓名 |
| department | varchar(50) | 部门 |
| teacher_type | varchar(20) | 教师类型 |
| role_type | varchar(20) | 角色类型 |
| gender | varchar(10) | 性别 |
| ethnicity | varchar(20) | 民族 |
| title | varchar(20) | 职称 |
| is_outsourced | tinyint(1) | 是否外聘 |
| english_name | varchar(50) | 英文姓名 |
| status | varchar(20) | 账号状态 |
| email | varchar(100) | 邮箱 |
| phone | varchar(20) | 电话 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

### 5.2 学生账号表(student_account)

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | bigint | 主键ID |
| student_id | varchar(50) | 学号(唯一) |
| username | varchar(50) | 用户名(唯一) |
| password | varchar(100) | 密码(加密存储) |
| name | varchar(50) | 姓名 |
| gender | varchar(10) | 性别 |
| ethnicity | varchar(20) | 民族 |
| class_id | varchar(50) | 班级ID |
| class_name | varchar(50) | 班级名称 |
| department_id | varchar(50) | 院系ID |
| department_name | varchar(50) | 院系名称 |
| grade | varchar(10) | 年级 |
| major_id | varchar(50) | 专业ID |
| major_name | varchar(50) | 专业名称 |
| counselor | varchar(50) | 辅导员 |
| political_status | varchar(20) | 政治面貌 |
| status | varchar(20) | 账号状态 |
| email | varchar(100) | 邮箱 |
| phone | varchar(20) | 电话 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

### 5.3 课程表(course)

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | varchar(50) | 课程ID |
| name | varchar(100) | 课程名称 |
| category | varchar(20) | 课程类别 |
| attribute | varchar(20) | 课程属性 |
| course_type | varchar(20) | 课程类型 |
| nature | varchar(20) | 课程性质 |
| department_id | varchar(50) | 开课院系ID |
| department_name | varchar(50) | 开课院系名称 |
| is_active | tinyint(1) | 是否启用 |
| total_hours | int | 总学时 |
| theory_hours | int | 理论学时 |
| experiment_hours | int | 实验学时 |
| computer_hours | int | 上机学时 |
| practice_hours | int | 实践学时 |
| credit | decimal(3,1) | 学分 |
| weekly_hours | int | 周学时 |
| is_practical | tinyint(1) | 是否纯实践 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

### 5.4 课程排课表(course_schedule)

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | varchar(50) | 排课ID |
| course_id | varchar(50) | 课程ID |
| classroom_id | varchar(50) | 教室ID |
| classroom_name | varchar(50) | 教室名称 |
| building_id | varchar(50) | 教学楼ID |
| building_name | varchar(50) | 教学楼名称 |
| teacher_id | varchar(50) | 教师ID |
| teacher_name | varchar(50) | 教师姓名 |
| class_id | varchar(50) | 班级ID |
| class_name | varchar(50) | 班级名称 |
| semester | varchar(20) | 学期 |
| weekday | int | 星期几(1-7) |
| start_section | int | 开始节次 |
| end_section | int | 结束节次 |
| week_numbers | varchar(100) | 周次 |
| created_at | datetime | 创建时间 |
| updated_at | datetime | 更新时间 |

### 5.5 调课申请表(schedule_change)

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | varchar(50) | 调课申请ID |
| teacher_id | varchar(50) | 教师ID |
| teacher_name | varchar(50) | 教师姓名 |
| course_id | varchar(50) | 课程ID |
| course_name | varchar(100) | 课程名称 |
| class_id | varchar(50) | 班级ID |
| class_name | varchar(50) | 班级名称 |
| original_date | date | 原课程日期 |
| original_section | int | 原课程节数 |
| target_date | date | 调整后日期 |
| target_section | int | 调整后节数 |
| reason | varchar(500) | 申请原因 |
| status | varchar(20) | 申请状态 |
| apply_time | datetime | 申请时间 |
| review_time | datetime | 审核时间 |
| reviewer_id | varchar(50) | 审核人ID |
| reviewer_name | varchar(50) | 审核人姓名 |
| reject_reason | varchar(500) | 拒绝原因 |

## 6. 开发规范

### 6.1 命名规范

1. **类命名**: 使用大驼峰命名法，如`UserController`
2. **方法命名**: 使用小驼峰命名法，如`getUserById`
3. **变量命名**: 使用小驼峰命名法，如`userList`
4. **常量命名**: 全大写，单词间用下划线分隔，如`MAX_PAGE_SIZE`
5. **包命名**: 小写字母，如`com.example.controller`

### 6.2 接口规范

1. **URL设计**:
   - 使用资源名词而非动词，如`/users`而非`/getUsers`
   - 使用复数表示资源集合，如`/users`而非`/user`
   - 使用嵌套表示资源层级关系，如`/departments/{id}/majors`

2. **HTTP方法**:
   - GET: 查询资源
   - POST: 创建资源
   - PUT: 更新资源(整体)
   - PATCH: 更新资源(部分)
   - DELETE: 删除资源

3. **状态码**:
   - 200: OK - 请求成功
   - 201: Created - 资源创建成功
   - 400: Bad Request - 请求参数错误
   - 401: Unauthorized - 未授权
   - 403: Forbidden - 权限不足
   - 404: Not Found - 资源不存在
   - 500: Internal Server Error - 服务器内部错误

### 6.3 代码规范

1. 使用`@Slf4j`注解进行日志记录，关键操作必须记录日志
2. 使用统一异常处理机制，避免直接返回异常信息给客户端
3. 敏感信息(如密码)必须加密存储，禁止明文传输
4. 接口参数必须进行校验，使用JSR-303注解进行约束
5. 禁止硬编码，配置信息应放在配置文件中
6. 代码中添加必要的注释，特别是复杂逻辑和业务规则

## 7. 安全规范

1. 使用BCrypt等安全算法加密存储密码
2. 实现适当的权限控制，确保用户只能访问权限范围内的资源
3. 防止SQL注入，使用参数化查询或ORM框架
4. 防止XSS攻击，对输入输出进行适当的转义
5. 实现请求频率限制和验证码机制，防止暴力破解
6. 敏感操作生成审计日志，记录操作人、操作时间和操作内容

## 8. 部署说明

### 8.1 环境要求

- JDK 1.8+
- MySQL 8.0+
- Maven 3.6+

### 8.2 部署步骤

1. 配置数据库连接信息(application.yml)
2. 修改JWT密钥和过期时间
3. 编译打包: `mvn clean package -DskipTests`
4. 运行: `java -jar target/application.jar`

## 9. 附录

### 9.1 参考文档

- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [MyBatis-Plus文档](https://baomidou.com/)
- [Spring Security文档](https://spring.io/projects/spring-security)

### 9.2 开发团队

- 后端开发: 技术团队
- 前端开发: 前端团队
- 产品经理: 产品团队
- 测试: 测试团队

---

文档版本: v1.0  
最后更新: 2023-12-20 
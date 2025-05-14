# 调课申请API接口文档

本文档描述了教师调课申请系统的后端API接口规范，供前后端开发人员参考。

## 基础信息

* 基础URL: `/api`
* 请求/响应数据格式: JSON
* 认证方式: JWT Token (在请求头中添加 `Authorization: Bearer <token>`)

## 通用响应格式

所有接口都使用统一的响应格式:

```json
{
  "success": true,  // 请求是否成功
  "message": "操作成功",  // 提示信息 (成功或错误消息)
  "data": { ... },  // 响应数据 (可选，根据接口返回不同的数据结构)
  "total": 100  // 分页查询时的总记录数 (仅适用于分页查询接口)
}
```

## 错误码说明

| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 (未登录或Token无效) |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 调课申请API接口

### 1. 获取调课申请列表

获取调课申请列表，支持分页和多种筛选条件。

* **URL**: `/schedule/change`
* **方法**: `GET`
* **权限要求**: 教师只能查看自己的申请；管理员可查看所有申请

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| teacherId | string | 否 | 教师ID，筛选指定教师的申请 |
| courseId | string | 否 | 课程ID，筛选指定课程的申请 |
| classId | string | 否 | 班级ID，筛选指定班级的申请 |
| status | string | 否 | 申请状态：`PENDING`(待审核)、`APPROVED`(已通过)、`REJECTED`(已拒绝) |
| startDate | string | 否 | 开始日期，格式：YYYY-MM-DD |
| endDate | string | 否 | 结束日期，格式：YYYY-MM-DD |
| page | number | 是 | 当前页码，从1开始 |
| pageSize | number | 是 | 每页记录数 |

**成功响应**:

```json
{
  "success": true,
  "message": "获取调课申请列表成功",
  "data": [
    {
      "id": "1001",
      "teacherId": "T10001",
      "teacherName": "张老师",
      "courseId": "C2001",
      "courseName": "高等数学",
      "classId": "CL3001",
      "className": "计算机科学2班",
      "originalDate": "2023-10-15",
      "originalSection": 2,
      "targetDate": "2023-10-18",
      "targetSection": 4,
      "reason": "有重要会议需要参加",
      "status": "PENDING",
      "applyTime": "2023-10-10T08:30:00",
      "reviewTime": null,
      "reviewerId": null,
      "reviewerName": null,
      "rejectReason": null
    },
    // 更多记录...
  ],
  "total": 45
}
```

### 2. 获取调课申请详情

获取单个调课申请的详细信息。

* **URL**: `/schedule/change/{id}`
* **方法**: `GET`
* **权限要求**: 教师只能查看自己的申请；管理员可查看所有申请

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | string | 是 | 调课申请ID |

**成功响应**:

```json
{
  "success": true,
  "message": "获取调课申请详情成功",
  "data": {
    "id": "1001",
    "teacherId": "T10001",
    "teacherName": "张老师",
    "courseId": "C2001",
    "courseName": "高等数学",
    "classId": "CL3001",
    "className": "计算机科学2班",
    "originalDate": "2023-10-15",
    "originalSection": 2,
    "targetDate": "2023-10-18",
    "targetSection": 4,
    "reason": "有重要会议需要参加",
    "status": "PENDING",
    "applyTime": "2023-10-10T08:30:00",
    "reviewTime": null,
    "reviewerId": null,
    "reviewerName": null,
    "rejectReason": null
  }
}
```

### 3. 提交调课申请

教师提交新的调课申请。

* **URL**: `/schedule/change`
* **方法**: `POST`
* **权限要求**: 需要教师权限

**请求参数**:

```json
{
  "courseId": "C2001",
  "classId": "CL3001",
  "originalDate": "2023-10-15",
  "originalSection": 2,
  "targetDate": "2023-10-18",
  "targetSection": 4,
  "reason": "有重要会议需要参加"
}
```

**请求字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| courseId | string | 是 | 课程ID |
| classId | string | 否 | 班级ID (可选，如果课程关联多个班级，则需要指定) |
| originalDate | string | 是 | 原课程日期，格式：YYYY-MM-DD |
| originalSection | number | 是 | 原课程节数 (1-5) |
| targetDate | string | 是 | 调整后日期，格式：YYYY-MM-DD |
| targetSection | number | 是 | 调整后节数 (1-5) |
| reason | string | 是 | 申请原因 (5-200字) |

**成功响应**:

```json
{
  "success": true,
  "message": "调课申请提交成功",
  "data": {
    "id": "1001",
    "teacherId": "T10001",
    "teacherName": "张老师",
    "courseId": "C2001",
    "courseName": "高等数学",
    "classId": "CL3001",
    "className": "计算机科学2班",
    "originalDate": "2023-10-15",
    "originalSection": 2,
    "targetDate": "2023-10-18",
    "targetSection": 4,
    "reason": "有重要会议需要参加",
    "status": "PENDING",
    "applyTime": "2023-10-10T08:30:00"
  }
}
```

### 4. 取消调课申请

教师取消自己提交的待审核调课申请。

* **URL**: `/schedule/change/{id}/cancel`
* **方法**: `POST`
* **权限要求**: 教师只能取消自己的申请，且必须是待审核状态

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | string | 是 | 调课申请ID |

**成功响应**:

```json
{
  "success": true,
  "message": "调课申请已取消"
}
```

### 5. 审核调课申请

管理员审核教师提交的调课申请。

* **URL**: `/schedule/change/{id}/review`
* **方法**: `POST`
* **权限要求**: 需要管理员权限

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | string | 是 | 调课申请ID |

**请求参数**:

```json
{
  "status": "APPROVED",  // 或 "REJECTED"
  "rejectReason": "与其他课程冲突"  // 当status为REJECTED时必填
}
```

**请求字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| status | string | 是 | 审核结果：`APPROVED`(通过)或`REJECTED`(拒绝) |
| rejectReason | string | 条件必填 | 拒绝原因 (当status为REJECTED时必填，5-200字) |

**成功响应**:

```json
{
  "success": true,
  "message": "审核已完成",
}
```

### 6. 获取教师课程列表

获取当前登录教师所教授的课程列表，用于调课申请时选择课程。

* **URL**: `/teacher/courses`
* **方法**: `GET`
* **权限要求**: 需要教师权限

**成功响应**:

```json
{
  "success": true,
  "message": "获取教师课程列表成功",
  "data": [
    {
      "id": "C2001",
      "name": "高等数学"
    },
    {
      "id": "C2002",
      "name": "线性代数"
    },
    // 更多课程...
  ]
}
```

### 7. 获取课程班级列表

获取指定课程关联的班级列表，用于调课申请时选择班级。

* **URL**: `/courses/{courseId}/classes`
* **方法**: `GET`
* **权限要求**: 需要教师权限

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| courseId | string | 是 | 课程ID |

**成功响应**:

```json
{
  "success": true,
  "message": "获取课程班级列表成功",
  "data": [
    {
      "id": "CL3001",
      "name": "计算机科学2班"
    },
    {
      "id": "CL3002",
      "name": "软件工程1班"
    },
    // 更多班级...
  ]
}
```

## 数据模型

### 调课申请(ScheduleChange)

| 字段名 | 类型 | 说明 |
|-------|------|------|
| id | string | 调课申请ID |
| teacherId | string | 教师ID |
| teacherName | string | 教师姓名 |
| courseId | string | 课程ID |
| courseName | string | 课程名称 |
| classId | string | 班级ID (可选) |
| className | string | 班级名称 (可选) |
| originalDate | string | 原课程日期，格式：YYYY-MM-DD |
| originalSection | number | 原课程节数 (1-5) |
| targetDate | string | 调整后日期，格式：YYYY-MM-DD |
| targetSection | number | 调整后节数 (1-5) |
| reason | string | 申请原因 |
| status | string | 申请状态：`PENDING`(待审核)、`APPROVED`(已通过)、`REJECTED`(已拒绝) |
| applyTime | string | 申请时间，格式：ISO 8601 |
| reviewTime | string | 审核时间，格式：ISO 8601 (可选) |
| reviewerId | string | 审核人ID (可选) |
| reviewerName | string | 审核人姓名 (可选) |
| rejectReason | string | 拒绝原因 (可选) |

## 节数对应的时间段

| 节数 | 时间段 |
|-----|--------|
| 1 | 08:00 - 09:40 |
| 2 | 10:00 - 11:40 |
| 3 | 13:30 - 15:10 |
| 4 | 15:30 - 17:10 |
| 5 | 18:30 - 20:10 |

## 部署与测试

1. 接口应支持跨域请求（已在前端配置了代理）
2. 建议提供Swagger或类似的API文档系统，方便前端开发人员查阅和测试
3. 每个接口都应有适当的权限验证
4. 建议增加参数验证，包括必填检查、类型检查、范围检查等 
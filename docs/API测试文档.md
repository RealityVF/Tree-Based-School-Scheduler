# 调课申请API测试文档

本文档提供调课申请功能的API测试指南，帮助开发人员和测试人员进行接口测试。

## 测试环境

- 开发环境: http://localhost:8080
- 测试环境: http://test-api.example.com
- 生产环境: https://api.example.com

## 认证方式

所有API请求需要在Header中添加Token进行认证：

```
Authorization: Bearer {your_jwt_token}
```

## Postman测试集合

以下是Postman测试集合的JSON格式，可导入Postman进行测试：

```json
{
  "info": {
    "_postman_id": "a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6",
    "name": "课程调课申请API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "获取调课申请列表",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}",
            "type": "text"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/api/schedule/change?page=1&pageSize=10&status=PENDING",
          "host": ["{{baseUrl}}"],
          "path": ["api", "schedule", "change"],
          "query": [
            {
              "key": "page",
              "value": "1"
            },
            {
              "key": "pageSize",
              "value": "10"
            },
            {
              "key": "status",
              "value": "PENDING"
            }
          ]
        }
      },
      "response": []
    },
    {
      "name": "获取调课申请详情",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}",
            "type": "text"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/api/schedule/change/1",
          "host": ["{{baseUrl}}"],
          "path": ["api", "schedule", "change", "1"]
        }
      },
      "response": []
    },
    {
      "name": "提交调课申请",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json",
            "type": "text"
          },
          {
            "key": "Authorization",
            "value": "Bearer {{token}}",
            "type": "text"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"courseId\": \"c001\",\n  \"classId\": \"cl001\",\n  \"originalDate\": \"2023-09-25\",\n  \"originalSection\": 2,\n  \"targetDate\": \"2023-09-27\",\n  \"targetSection\": 4,\n  \"reason\": \"因参加学校活动，需要调整课程时间\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/schedule/change",
          "host": ["{{baseUrl}}"],
          "path": ["api", "schedule", "change"]
        }
      },
      "response": []
    },
    {
      "name": "取消调课申请",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}",
            "type": "text"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/api/schedule/change/1/cancel",
          "host": ["{{baseUrl}}"],
          "path": ["api", "schedule", "change", "1", "cancel"]
        }
      },
      "response": []
    },
    {
      "name": "审核调课申请-通过",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json",
            "type": "text"
          },
          {
            "key": "Authorization",
            "value": "Bearer {{token}}",
            "type": "text"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"status\": \"APPROVED\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/schedule/change/1/review",
          "host": ["{{baseUrl}}"],
          "path": ["api", "schedule", "change", "1", "review"]
        }
      },
      "response": []
    },
    {
      "name": "审核调课申请-拒绝",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json",
            "type": "text"
          },
          {
            "key": "Authorization",
            "value": "Bearer {{token}}",
            "type": "text"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"status\": \"REJECTED\",\n  \"rejectReason\": \"所选时间段已有其他课程安排，无法调整\"\n}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/schedule/change/1/review",
          "host": ["{{baseUrl}}"],
          "path": ["api", "schedule", "change", "1", "review"]
        }
      },
      "response": []
    },
    {
      "name": "获取教师课程列表",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}",
            "type": "text"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/api/teacher/courses",
          "host": ["{{baseUrl}}"],
          "path": ["api", "teacher", "courses"]
        }
      },
      "response": []
    },
    {
      "name": "获取课程班级列表",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}",
            "type": "text"
          }
        ],
        "url": {
          "raw": "{{baseUrl}}/api/courses/c001/classes",
          "host": ["{{baseUrl}}"],
          "path": ["api", "courses", "c001", "classes"]
        }
      },
      "response": []
    }
  ],
  "event": [
    {
      "listen": "prerequest",
      "script": {
        "type": "text/javascript",
        "exec": [""]
      }
    },
    {
      "listen": "test",
      "script": {
        "type": "text/javascript",
        "exec": [""]
      }
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080",
      "type": "string"
    },
    {
      "key": "token",
      "value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "type": "string"
    }
  ]
}
```

## 测试建议

### 1. 基础测试用例

| 测试场景                   | 测试用例                                 | 预期结果                         |
|--------------------------|----------------------------------------|----------------------------------|
| 获取调课申请列表            | 教师角色获取自己的申请列表                | 返回列表数据，只包含自己的申请     |
| 获取调课申请列表            | 管理员角色获取所有申请列表                | 返回所有申请列表数据              |
| 获取调课申请列表            | 使用不同筛选条件查询                      | 返回符合条件的申请数据             |
| 获取调课申请详情            | 教师查看自己的申请                        | 返回详情数据                      |
| 获取调课申请详情            | 教师查看别人的申请                        | 返回权限不足错误                  |
| 获取调课申请详情            | 管理员查看任意申请                        | 返回详情数据                      |
| 提交调课申请                | 使用有效数据提交                          | 提交成功，返回申请ID               |
| 提交调课申请                | 使用无效数据提交（如缺少必填字段）          | 提交失败，返回错误信息             |
| 取消调课申请                | 教师取消自己的待审核申请                   | 取消成功                         |
| 取消调课申请                | 教师取消已审核申请                        | 返回操作不允许错误                |
| 取消调课申请                | 教师取消别人的申请                        | 返回权限不足错误                  |
| 审核调课申请                | 管理员通过有效申请                        | 审核成功，状态变为已通过           |
| 审核调课申请                | 管理员拒绝申请并提供原因                   | 审核成功，状态变为已拒绝           |
| 审核调课申请                | 管理员拒绝申请但不提供原因                 | 返回参数错误信息                  |
| 审核调课申请                | 教师尝试审核申请                          | 返回权限不足错误                  |

### 2. 边界测试用例

| 测试场景                   | 测试用例                                 | 预期结果                         |
|--------------------------|----------------------------------------|----------------------------------|
| 提交调课申请                | 原课程节数设为0或6                        | 返回参数错误信息                  |
| 提交调课申请                | 目标课程节数设为0或6                      | 返回参数错误信息                  |
| 提交调课申请                | 申请原因长度为4个字符                     | 返回参数错误信息                  |
| 提交调课申请                | 申请原因长度为201个字符                   | 返回参数错误信息                  |
| 提交调课申请                | 原日期晚于目标日期                        | 正常提交（系统不限制日期先后）      |
| 获取调课申请列表            | page=0, pageSize=10                    | 使用默认值page=1处理              |
| 获取调课申请列表            | page=1, pageSize=0                     | 使用默认值pageSize=10处理         |
| 获取调课申请列表            | page=1, pageSize=1000                  | 限制为最大允许页大小（如100）       |

### 3. 压力测试建议

1. 并发提交多个调课申请，测试系统稳定性
2. 频繁查询申请列表，测试查询性能
3. 批量审核申请，测试系统处理能力

## 自动化测试脚本示例

使用 JUnit 和 RestAssured 进行自动化测试：

```java
@Test
public void testSubmitScheduleChange() {
    ScheduleChangeForm form = new ScheduleChangeForm();
    form.setCourseId("c001");
    form.setOriginalDate(LocalDate.now().plusDays(5));
    form.setOriginalSection(2);
    form.setTargetDate(LocalDate.now().plusDays(7));
    form.setTargetSection(4);
    form.setReason("自动化测试调课申请");

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + getToken())
        .body(form)
    .when()
        .post("/api/schedule/change")
    .then()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("data.id", notNullValue())
        .body("data.status", equalTo("PENDING"));
}
```

## 常见问题及解决方案

1. **问题**: 无法获取Token或Token无效
   **解决方案**: 检查用户登录状态，刷新Token或重新登录

2. **问题**: 提交调课申请返回"课程不存在"错误
   **解决方案**: 检查courseId是否正确，确保教师有权限访问该课程

3. **问题**: 审核调课申请失败，提示"申请已处理"
   **解决方案**: 确认申请当前状态，只有PENDING状态的申请可以审核 
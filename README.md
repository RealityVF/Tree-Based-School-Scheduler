# 基于知识二叉树的排课系统

这是一个合并了登录认证和课程表功能的教务管理系统应用。

## 功能特点

- 用户认证（学生、教师、管理员登录）
- 登录状态管理
- 课程表查询和显示
- 响应式设计

## 技术栈

- Vue 3
- TypeScript
- Vue Router
- Pinia
- Element Plus
- Axios

## 开发环境设置

### 安装依赖

```bash
npm install
```

### 开发服务器启动

```bash
npm run dev
```

### 编译生产版本

```bash
npm run build
```

### 代码检查和修复

```bash
npm run lint
```

## 应用结构

- `/src/views` - 视图组件
  - `LoginView.vue` - 登录页面
  - `DashboardView.vue` - 仪表板页面
  - `TimetableView.vue` - 课程表页面
- `/src/api` - API请求
  - `auth.ts` - 认证相关API
  - `timetable.ts` - 课程表相关API
- `/src/router` - 路由配置
- `/src/utils` - 工具函数
  - `request.ts` - Axios请求配置

## 使用流程

1. 用户访问应用，首先会被重定向到登录页
2. 登录成功后，根据用户类型显示相应功能
3. 用户可以查看课程表、个人信息等
4. 用户可以通过右上角的下拉菜单退出登录

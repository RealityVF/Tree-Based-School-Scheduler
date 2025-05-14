<template>
  <div class="dashboard-container">
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical"
          :router="true"
          background-color="#263445"
          text-color="#a7b1c2"
          active-text-color="#409eff"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <!-- 根据用户类型显示不同的菜单项 -->
          <!-- 学生菜单 -->
          <template v-if="userStore.isStudent">
            <el-menu-item index="/timetable">
              <el-icon><Calendar /></el-icon>
              <span>班级课表</span>
            </el-menu-item>
            <el-menu-item index="/resources">
              <el-icon><Folder /></el-icon>
              <span>课程资源</span>
            </el-menu-item>
          </template>

          <!-- 教师菜单 -->
          <template v-if="userStore.isTeacher">
            <el-menu-item index="/timetable">
              <el-icon><Calendar /></el-icon>
              <span>我的课表</span>
            </el-menu-item>
            <el-menu-item index="/resources">
              <el-icon><Folder /></el-icon>
              <span>课程资源</span>
            </el-menu-item>
            <el-menu-item index="/schedule/change">
              <el-icon><EditPen /></el-icon>
              <span>调课申请</span>
            </el-menu-item>
          </template>

          <!-- 管理员菜单 -->
          <template v-if="userStore.isAdmin">
            <el-menu-item index="/timetable">
              <el-icon><Calendar /></el-icon>
              <span>课表查询</span>
            </el-menu-item>
            <el-menu-item index="/resources">
              <el-icon><Folder /></el-icon>
              <span>课程资源</span>
            </el-menu-item>
            <el-menu-item index="/schedule/management">
              <el-icon><Management /></el-icon>
              <span>调课管理</span>
            </el-menu-item>
            <el-sub-menu
              v-if="userStore.isAdmin"
              index="/school"
            >
              <template #title>
                <el-icon><School /></el-icon>
                <span>学校管理</span>
              </template>
              <el-menu-item-group title="校区管理">
                <el-menu-item index="/school/building">
                  <el-icon><OfficeBuilding /></el-icon>
                  <span>校区管理</span>
                </el-menu-item>
                <el-menu-item index="/school/class">
                  <el-icon><User /></el-icon>
                  <span>班级管理</span>
                </el-menu-item>
                <el-menu-item index="/school/course">
                  <el-icon><Reading /></el-icon>
                  <span>排课管理</span>
                </el-menu-item>
                <el-menu-item index="/school/course-library">
                  <el-icon><Reading /></el-icon>
                  <span>课程库</span>
                </el-menu-item>
                <el-menu-item index="/school/major">
                  <el-icon><Reading /></el-icon>
                  <span>专业管理</span>
                </el-menu-item>
                <el-menu-item index="/school/major-direction">
                  <el-icon><Reading /></el-icon>
                  <span>专业方向管理</span>
                </el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>
            <el-sub-menu
              v-if="userStore.isAdmin && isSuperAdmin"
              index="/account"
            >
              <template #title>
                <el-icon><UserFilled /></el-icon>
                <span>账号管理</span>
              </template>
              <el-menu-item-group title="教师账号">
                <el-menu-item index="/account/create">
                  <el-icon><Plus /></el-icon>
                  <span>创建教师账号</span>
                </el-menu-item>
                <el-menu-item index="/account/list">
                  <el-icon><Document /></el-icon>
                  <span>教师账号列表</span>
                </el-menu-item>
              </el-menu-item-group>
              <el-menu-item-group title="学生账号">
                <el-menu-item index="/account/student-create">
                  <el-icon><Plus /></el-icon>
                  <span>创建学生账号</span>
                </el-menu-item>
                <el-menu-item index="/account/student-list">
                  <el-icon><Document /></el-icon>
                  <span>学生账号列表</span>
                </el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>
            <el-menu-item index="/server-test">
              <el-icon><Monitor /></el-icon>
              <span>服务器测试</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-content">
            <h2>教务管理系统</h2>
            <div class="user-info">
              <span>{{ userStore.userName }} ({{ getUserTypeText }})</span>
              <el-dropdown @command="handleCommand">
                <span class="el-dropdown-link">
                  <el-icon><User /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                    <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { HomeFilled, User, Calendar, UserFilled, Plus, Document, Monitor, Folder, EditPen, Management, OfficeBuilding, School, Reading } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/counter'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const activeMenu = ref('/')

// 根据用户类型返回对应的文本
const getUserTypeText = computed(() => {
  if (userStore.isAdmin) return '管理员'
  if (userStore.isTeacher) return '教师'
  if (userStore.isStudent) return '学生'
  return '未知'
})

// 判断当前用户是否为超级管理员
const isSuperAdmin = computed(() => {
  console.log('当前用户信息:', userStore.userInfo);

  // 直接基于用户ID判断，工号20230001固定为超级管理员
  if (userStore.userInfo?.userId === '20230001') {
    console.log('用户 20230001 被识别为超级管理员');
    return true;
  }

  // 还需保留role字段检查，以便后续实现
  if (userStore.userInfo?.role === 'SUPER_ADMIN') {
    console.log('基于role识别为超级管理员');
    return true;
  }

  return false;
})

onMounted(() => {
  // 加载存储的用户信息
  userStore.loadUserFromStorage()

  // 设置当前激活的菜单项
  activeMenu.value = route.path
})

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
    ElMessage({
      message: '已退出登录',
      type: 'success',
      duration: 2000
    })
  } else if (command === 'profile') {
    // 跳转到个人信息页面
    router.push('/profile')
  }
}
</script>

<style scoped>
.dashboard-container {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  overflow: hidden;
}

.el-container {
  width: 100%;
  height: 100%;
}

.el-aside {
  height: 100%;
  background-color: #263445;
  color: #fff;
  overflow-y: auto;
  overflow-x: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  transition: all 0.3s;
}

.el-menu-vertical {
  border-right: none;
  width: 100%;
  background-color: #263445;
}

.el-menu-vertical :deep(.el-menu-item),
.el-menu-vertical :deep(.el-sub-menu__title) {
  display: flex;
  align-items: center;
  height: 56px;
  line-height: 56px;
  color: #a7b1c2;
  padding: 0 20px;
  transition: all 0.3s;
}

.el-menu-vertical :deep(.el-menu-item:hover),
.el-menu-vertical :deep(.el-sub-menu__title:hover) {
  color: #fff;
  background-color: #1f2d3d;
}

.el-menu-vertical :deep(.el-menu-item.is-active) {
  color: #409eff;
  background-color: #1f2d3d;
  border-left: 4px solid #409eff;
}

.el-menu-vertical :deep(.el-menu-item) .el-icon,
.el-menu-vertical :deep(.el-sub-menu__title) .el-icon {
  margin-right: 12px;
  font-size: 18px;
  color: inherit;
}

.el-menu-vertical :deep(.el-sub-menu .el-menu) {
  background-color: #1f2d3d;
}

.el-menu-vertical :deep(.el-sub-menu .el-menu-item) {
  background-color: #1f2d3d;
  color: #a7b1c2;
  padding-left: 50px !important;
}

.el-menu-vertical :deep(.el-sub-menu .el-menu-item:hover) {
  color: #fff;
  background-color: #1a2638;
}

.el-menu-vertical :deep(.el-sub-menu .el-menu-item.is-active) {
  color: #409eff;
  background-color: #1a2638;
}

.el-menu-vertical :deep(.el-sub-menu.is-active .el-sub-menu__title) {
  color: #fff;
}

.el-menu-vertical :deep(.el-menu-item-group__title) {
  color: #7a8599;
  padding-left: 20px;
}

.el-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #606266;
  transition: color 0.3s;
}

.el-dropdown-link:hover {
  color: #409eff;
}

.el-main {
  padding: 20px;
  height: calc(100% - 60px);
  overflow-y: auto;
  background-color: #f0f2f5;
  position: relative;
}

.welcome-container {
  padding: 20px;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.welcome-container h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #303133;
}

.info-card {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;
}

.system-info {
  margin-top: 30px;
}

.system-info h3 {
  margin-bottom: 15px;
  color: #303133;
}

.system-info ul {
  padding-left: 20px;
}
</style>

<template>
  <div class="welcome-container">
    <h2>欢迎使用教务管理系统</h2>
    <div class="info-card">
      <p><strong>用户姓名：</strong>{{ userStore.userName }}</p>
      <p><strong>用户类型：</strong>{{ getUserTypeText }}</p>
      <p><strong>用户ID：</strong>{{ userStore.userInfo?.userId }}</p>
      <template v-if="userStore.isStudent">
        <p v-if="userStore.userInfo?.majorId"><strong>专业ID：</strong>{{ userStore.userInfo.majorId }}</p>
        <p v-if="userStore.userInfo?.classId"><strong>班级ID：</strong>{{ userStore.userInfo.classId }}</p>
      </template>
      <template v-if="userStore.isTeacher">
        <p v-if="userStore.userInfo?.departmentId"><strong>部门ID：</strong>{{ userStore.userInfo.departmentId }}</p>
        <p v-if="userStore.userInfo?.title"><strong>职称：</strong>{{ userStore.userInfo.title }}</p>
      </template>
    </div>
    
    <div class="system-info">
      <h3>系统功能</h3>
      <el-card>
        <div v-if="userStore.isStudent">
          <p>作为学生，您可以：</p>
          <ul>
            <li>查看班级课表</li>
            <li>查看课程详情</li>
          </ul>
        </div>
        <div v-else-if="userStore.isTeacher">
          <p>作为教师，您可以：</p>
          <ul>
            <li>查看个人教学课表</li>
            <li>查看课程详情</li>
          </ul>
        </div>
        <div v-else-if="userStore.isAdmin">
          <p>作为管理员，您可以：</p>
          <ul>
            <li>查询任何班级课表</li>
            <li>查询任何教师课表</li>
            <li>查看课程详情</li>
          </ul>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/counter'

const userStore = useUserStore()

// 根据用户类型返回对应的文本
const getUserTypeText = computed(() => {
  if (userStore.isAdmin) return '管理员'
  if (userStore.isTeacher) return '教师'
  if (userStore.isStudent) return '学生'
  return '未知'
})
</script>

<style scoped>
.welcome-container {
  padding: 20px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  overflow: auto;
}

.welcome-container h2 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #303133;
  font-size: 24px;
}

.info-card {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.system-info {
  margin-top: 30px;
}

.system-info h3 {
  margin-bottom: 15px;
  color: #303133;
  font-size: 18px;
}

.system-info ul {
  padding-left: 20px;
  line-height: 1.8;
}

@media (max-width: 768px) {
  .welcome-container {
    padding: 15px;
  }
  
  .info-card {
    padding: 15px;
  }
}
</style> 
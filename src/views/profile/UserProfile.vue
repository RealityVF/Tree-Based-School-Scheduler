<template>
  <div class="user-profile-container">
    <div class="profile-header">
      <h1>个人信息</h1>
    </div>
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
          <el-button type="primary" plain>编辑</el-button>
        </div>
      </template>
      <div class="profile-content">
        <div class="profile-item">
          <span class="label">工号：</span>
          <span class="value">{{ userInfo.teacherId || userInfo.studentId || '--' }}</span>
        </div>
        <div class="profile-item">
          <span class="label">姓名：</span>
          <span class="value">{{ userInfo.name || '--' }}</span>
        </div>
        <div class="profile-item">
          <span class="label">账号：</span>
          <span class="value">{{ userInfo.username || '--' }}</span>
        </div>
        <div class="profile-item" v-if="userInfo.department">
          <span class="label">单位/部门：</span>
          <span class="value">{{ userInfo.department }}</span>
        </div>
        <div class="profile-item" v-if="userInfo.teacherType">
          <span class="label">教职工类别：</span>
          <span class="value">{{ userInfo.teacherType }}</span>
        </div>
        <div class="profile-item" v-if="userInfo.major">
          <span class="label">专业：</span>
          <span class="value">{{ userInfo.major }}</span>
        </div>
        <div class="profile-item" v-if="userInfo.class">
          <span class="label">班级：</span>
          <span class="value">{{ userInfo.class }}</span>
        </div>
      </div>
    </el-card>

    <el-card class="profile-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>安全设置</span>
        </div>
      </template>
      <div class="security-settings">
        <div class="setting-item">
          <div class="setting-info">
            <h3>密码修改</h3>
            <p>定期修改密码可以提高账号安全性</p>
          </div>
          <el-button>修改密码</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const userInfo = ref({
  name: '',
  username: '',
  teacherId: '',
  studentId: '',
  department: '',
  teacherType: '',
  major: '',
  class: '',
});

onMounted(() => {
  // 假数据，实际实现应该从API获取用户信息
  userInfo.value = {
    name: '张三',
    username: 'zhangsan',
    teacherId: '12345',
    department: '计算机科学与技术学院',
    teacherType: '专任教师',
    major: '',
    class: '',
    studentId: '',
  };
});
</script>

<style scoped>
.user-profile-container {
  padding: 20px;
}

.profile-header {
  margin-bottom: 20px;
}

.profile-card {
  width: 100%;
  max-width: 800px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.profile-item {
  display: flex;
  line-height: 24px;
}

.label {
  color: #606266;
  margin-right: 10px;
  min-width: 90px;
}

.value {
  color: #303133;
  font-weight: 500;
}

.security-settings {
  margin-top: 10px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #EBEEF5;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-info h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: normal;
}

.setting-info p {
  color: #909399;
  margin: 0;
  font-size: 13px;
}
</style>

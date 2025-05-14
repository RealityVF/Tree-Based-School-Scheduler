<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="header-with-debug">
          <h2>系统登录</h2>
          <el-button
            v-if="serverStatus !== 'checking'"
            type="primary"
            size="small"
            @click="openDiagnosticDialog"
            :icon="Monitor"
            circle
            class="debug-btn"
            title="服务器诊断"
          />
        </div>
      </template>
      <!-- 服务器状态提示 -->
      <div v-if="serverStatus === 'checking'" class="server-status checking">
        正在检查服务器状态...
      </div>
      <div v-else-if="serverStatus === 'offline'" class="server-status offline">
        警告: 后端服务器未连接，请确认服务器已启动
      </div>
      <div v-else class="server-status online">
        服务器连接正常
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-width="80px"
      >
        <el-form-item label="账号" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入账号"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="loginForm.userType" placeholder="请选择用户类型" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>

  <!-- 服务器诊断对话框 -->
  <el-dialog
    v-model="showDiagnosticDialog"
    title="服务器连接诊断"
    width="80%"
  >
    <el-descriptions
      v-if="diagnosticReport && !isDiagnosticLoading"
      border
      title="服务器连接状态"
      :column="1"
    >
      <el-descriptions-item label="基础URL状态">
        <el-tag
          :type="diagnosticReport.baseUrlStatus.isOnline ? 'success' : 'danger'"
        >
          {{ diagnosticReport.baseUrlStatus.message }}
        </el-tag>
        <div v-if="diagnosticReport.baseUrlStatus.latency">
          延迟: {{ diagnosticReport.baseUrlStatus.latency }}ms
        </div>
      </el-descriptions-item>
    </el-descriptions>

    <el-descriptions
      v-if="diagnosticReport && !isDiagnosticLoading"
      border
      title="CORS配置状态"
      :column="1"
    >
      <el-descriptions-item label="CORS 状态">
        <el-tag
          :type="diagnosticReport.corsStatus.corsHeadersPresent ? 'success' : 'danger'"
        >
          {{ diagnosticReport.corsStatus.message }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="CORS 头信息" v-if="diagnosticReport.corsStatus.details">
        <pre>{{ JSON.stringify(diagnosticReport.corsStatus.details, null, 2) }}</pre>
      </el-descriptions-item>
    </el-descriptions>

    <el-descriptions
      v-if="diagnosticReport && !isDiagnosticLoading"
      border
      title="API端点状态"
      :column="1"
    >
      <el-descriptions-item
        v-for="(status, endpoint) in diagnosticReport.apiEndpointsStatus"
        :key="endpoint"
        :label="endpoint"
      >
        <el-tag
          :type="status.isOnline ? 'success' : 'danger'"
        >
          {{ status.message }}
        </el-tag>
      </el-descriptions-item>
    </el-descriptions>

    <el-descriptions
      v-if="diagnosticReport && !isDiagnosticLoading"
      border
      title="环境变量"
      :column="1"
    >
      <el-descriptions-item
        v-for="(value, key) in diagnosticReport.environmentVariables"
        :key="key"
        :label="key"
      >
        {{ value }}
      </el-descriptions-item>
    </el-descriptions>

    <div v-if="isDiagnosticLoading" class="loading-container">
      <el-icon class="is-loading"><svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"><path fill="currentColor" d="M512 64a32 32 0 0 1 32 32v192a32 32 0 0 1-64 0V96a32 32 0 0 1 32-32zm0 640a32 32 0 0 1 32 32v192a32 32 0 1 1-64 0V736a32 32 0 0 1 32-32zm448-192a32 32 0 0 1-32 32H736a32 32 0 1 1 0-64h192a32 32 0 0 1 32 32zm-640 0a32 32 0 0 1-32 32H96a32 32 0 0 1 0-64h192a32 32 0 0 1 32 32zM195.2 195.2a32 32 0 0 1 45.3 0l128 128a32 32 0 0 1-45.3 45.3l-128-128a32 32 0 0 1 0-45.3zm707.3 707.3a32 32 0 0 1-45.3 0l-128-128a32 32 0 0 1 45.3-45.3l128 128a32 32 0 0 1 0 45.3zm-707.3 0a32 32 0 0 1 0-45.3l128-128a32 32 0 0 1 45.3 45.3l-128 128a32 32 0 0 1-45.3 0zm707.3-707.3a32 32 0 0 1 0 45.3l-128 128a32 32 0 0 1-45.3-45.3l128-128a32 32 0 0 1 45.3 0z"></path></svg></el-icon>
      <p>正在加载诊断信息...</p>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="showDiagnosticDialog = false">
          关闭
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElDialog, ElDescriptions, ElDescriptionsItem } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { Monitor } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { login, checkServerConnection, getServerDiagnostics } from '@/api/auth'
import type { LoginParams, DiagnosticReport } from '@/api/auth'
import { useUserStore } from '@/stores/counter'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref<FormInstance>()
const loading = ref(false)
// 服务器状态
const serverStatus = ref<'checking' | 'online' | 'offline'>('checking')
// 服务器诊断对话框
const showDiagnosticDialog = ref(false)

const diagnosticReport = ref<DiagnosticReport | null>(null)
const isDiagnosticLoading = ref(false)

const loginForm = reactive<LoginParams>({
  username: '',
  password: '',
  userType: 'ADMIN'
})

const loginRules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    {
      validator: (rule: unknown, value: string, callback: (error?: Error) => void) => {
        if (!value) {
          callback()
          return
        }
        if (loginForm.userType === 'TEACHER' && !/^\d{11}$/.test(value)) {
          callback(new Error('教师工号必须是11位数字'))
        } else if (loginForm.userType === 'STUDENT' && !/^\d{14}$/.test(value)) {
          callback(new Error('学号必须是14位数字'))
        } else if (loginForm.userType === 'ADMIN' && (value.length < 3 || value.length > 20)) {
          callback(new Error('管理员账号长度应为3-20个字符'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ]
}

// 打开服务器诊断对话框
const openDiagnosticDialog = async () => {
  try {
    showDiagnosticDialog.value = true;
    isDiagnosticLoading.value = true;
    diagnosticReport.value = await getServerDiagnostics();
  } catch (error) {
    console.error('获取诊断报告失败:', error);
    ElMessage({
      message: '获取服务器诊断信息失败',
      type: 'error',
      duration: 3000
    });
  } finally {
    isDiagnosticLoading.value = false;
  }
};

// 组件加载时检查服务器状态
onMounted(async () => {
  try {
    serverStatus.value = 'checking';
    const isOnline = await checkServerConnection();
    serverStatus.value = isOnline ? 'online' : 'offline';

    if (!isOnline) {
      ElMessage({
        message: '警告：后端服务器未连接，请确认服务器已启动',
        type: 'warning',
        duration: 5000,
        showClose: true,
        center: true
      });
    }
  } catch (error) {
    serverStatus.value = 'offline';
    console.error('服务器状态检查失败:', error);
  }
});

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    loading.value = true

    console.log('登录请求参数:', loginForm)
    const response = await login(loginForm)

    if (response.message === '登录成功') {
      ElMessage.success('登录成功')
      // 创建符合 UserInfo 接口的对象
      const userInfo = {
        userId: response.userInfo.id,
        name: response.userInfo.name,
        role: response.userInfo.role,
        userType: loginForm.userType,
        isActive: true,
        // 添加学生相关信息
        classId: response.userInfo.classId,
        majorId: response.userInfo.majorId
      }
      userStore.setUserInfo(userInfo, response.loginData.token)
      await router.push('/')
    } else {
      throw new Error(response.message)
    }
  } catch (error) {
    console.error('登录错误:', error)
    ElMessage.error(error instanceof Error ? error.message : '登录失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  background-color: #f0f2f5;
  background-image: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-card {
  width: 400px;
  max-width: 90%;
  background-color: #ffffff;
  margin: auto;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.login-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.el-card__header {
  padding: 20px 20px 10px;
  border-bottom: 1px solid #f0f0f0;
}

.el-card__header h2 {
  margin: 0;
  text-align: center;
  font-size: 24px;
  color: #303133;
}

.el-form {
  padding: 20px;
}

.el-button {
  width: 100%;
  padding: 12px 0;
  font-size: 16px;
}

.header-with-debug {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.debug-btn {
  font-size: 14px;
}

.server-status {
  margin: -10px 20px 10px;
  padding: 8px;
  border-radius: 4px;
  text-align: center;
  font-size: 14px;
}

.server-status.checking {
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.server-status.online {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

.server-status.offline {
  background-color: #fff2f0;
  color: #ff4d4f;
  border: 1px solid #ffccc7;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.loading-container .el-icon {
  font-size: 40px;
  margin-bottom: 16px;
  color: #409eff;
}

@media (max-width: 768px) {
  .login-card {
    width: 350px;
  }
}

@media (max-width: 480px) {
  .login-card {
    width: 90%;
  }
}
</style>

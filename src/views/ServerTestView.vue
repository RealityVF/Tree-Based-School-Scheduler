<template>
  <div class="server-test-container">
    <h1>后端服务器连接测试</h1>
    
    <el-card class="test-card">
      <template #header>
        <div class="card-header">
          <h2>服务器基本信息</h2>
          <el-button type="primary" @click="refreshTests" :loading="loading" size="small">
            刷新测试
          </el-button>
        </div>
      </template>
      
      <el-descriptions :column="1" border>
        <el-descriptions-item label="API 基础 URL">
          {{ config.apiBaseUrl }}
        </el-descriptions-item>
        <el-descriptions-item label="超时设置">
          {{ config.apiTimeout }}ms
        </el-descriptions-item>
        <el-descriptions-item label="重试次数">
          {{ config.apiRetryCount }}
        </el-descriptions-item>
      </el-descriptions>
      
      <div class="test-section">
        <h3>服务器状态</h3>
        <el-tag :type="serverStatus.isOnline ? 'success' : 'danger'" class="status-tag">
          {{ serverStatus.message }}
        </el-tag>
        <div class="detail-info" v-if="serverStatus.latency">
          响应延迟: {{ serverStatus.latency }}ms
        </div>
      </div>
      
      <div class="test-section">
        <h3>API 端点测试结果</h3>
        <el-table :data="apiEndpointTests" v-loading="loading">
          <el-table-column prop="endpoint" label="API 端点" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag 
                :type="scope.row.result?.isOnline ? 'success' : 'danger'"
              >
                {{ scope.row.result?.isOnline ? '可访问' : '不可访问' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="message" label="信息">
            <template #default="scope">
              {{ scope.row.result?.message }}
            </template>
          </el-table-column>
          <el-table-column prop="latency" label="延迟">
            <template #default="scope">
              {{ scope.row.result?.latency ? scope.row.result.latency + 'ms' : '---' }}
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div class="test-section">
        <h3>网络诊断信息</h3>
        <el-collapse>
          <el-collapse-item title="详细信息" name="1">
            <pre class="diagnostic-info">{{ JSON.stringify(diagnosticInfo, null, 2) }}</pre>
          </el-collapse-item>
        </el-collapse>
      </div>
      
      <div class="test-section">
        <h3>登录测试</h3>
        <el-form :model="loginForm" label-width="100px">
          <el-form-item label="账号">
            <el-input v-model="loginForm.username" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item label="用户类型">
            <el-select v-model="loginForm.userType" placeholder="请选择用户类型">
              <el-option label="管理员" value="ADMIN" />
              <el-option label="教师" value="TEACHER" />
              <el-option label="学生" value="STUDENT" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="testLogin('normal')" :loading="loginTestLoading.normal">
              测试登录
            </el-button>
            <el-button type="info" @click="testLogin('alternative')" :loading="loginTestLoading.alternative">
              测试备选登录
            </el-button>
            <el-button type="warning" @click="testLogin('direct')" :loading="loginTestLoading.direct">
              测试直接URL登录
            </el-button>
          </el-form-item>
        </el-form>
        
        <div v-if="loginResult">
          <h4>登录测试结果:</h4>
          <el-alert
            :title="loginResult.success ? '登录成功' : '登录失败'"
            :type="loginResult.success ? 'success' : 'error'"
            :description="loginResult.message"
            show-icon
          />
          <pre v-if="loginResult.data" class="result-json">{{ JSON.stringify(loginResult.data, null, 2) }}</pre>
        </div>
      </div>
      
      <div class="test-section">
        <h3>设置超级管理员</h3>
        <div class="test-content">
          <p>此功能用于直接将指定工号的用户设置为超级管理员。</p>
          <el-alert
            type="info"
            title="使用说明"
            description="1. 输入需设置为超级管理员的工号（默认为20230001）
2. 点击按钮设置该账号为超级管理员
3. 设置成功后请退出并重新登录系统以生效
4. 如果后端不支持此接口，将使用本地模拟实现，仅对当前登录用户有效"
            :closable="false"
            show-icon
            style="margin-bottom: 15px;"
          />
          <el-input
            v-model="superAdminId"
            placeholder="请输入要设置为超级管理员的工号"
            class="mb-10"
          />
          <div class="admin-buttons">
            <el-button type="primary" @click="handleSetSuperAdmin" :loading="settingSuperAdmin">
              设置为超级管理员
            </el-button>
            <el-button type="warning" @click="handleLogout">
              退出登录
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { 
  login, 
  loginAlternative, 
  loginAlternative2, 
  getServerDiagnostics,
  type LoginParams,
  type LoginResponse,
  type DiagnosticReport
} from '@/api/auth';
import { setSuperAdmin } from '@/api/account';
import { ServerDiagnosticTools } from '@/utils/debugTools';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/counter';

// 环境配置
const config = reactive({
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL || '-',
  apiTimeout: import.meta.env.VITE_API_TIMEOUT || '5000',
  apiRetryCount: import.meta.env.VITE_API_RETRY_COUNT || '2',
  apiDebug: import.meta.env.VITE_API_DEBUG || 'false'
});

// 定义连接检查结果类型
interface ConnectionStatus {
  isOnline: boolean;
  message: string;
  latency?: number;
  details?: unknown;
}

// 状态变量
const loading = ref(false);
const serverStatus = ref<ConnectionStatus>({ isOnline: false, message: '未测试' });
const apiEndpointTests = ref<Array<{ endpoint: string, result: ConnectionStatus }>>([]);
const diagnosticInfo = ref<DiagnosticReport | null>(null);
const loginTestLoading = reactive({
  normal: false,
  alternative: false,
  direct: false
});

interface LoginTestResult {
  success: boolean;
  message: string;
  data?: LoginResponse;
  error?: {
    name?: string;
    code?: string | number;
    stack?: string;
  };
}

const loginResult = ref<LoginTestResult | null>(null);

// 测试用登录表单
const loginForm = reactive<LoginParams>({
  username: 'admin',
  password: '123456',
  userType: 'ADMIN'
});

// 超级管理员设置相关
const superAdminId = ref('20230001') // 默认值为20230001
const settingSuperAdmin = ref(false)

const router = useRouter();
const userStore = useUserStore();

// 设置超级管理员
const handleSetSuperAdmin = async () => {
  if (!superAdminId.value) {
    ElMessage.warning('请输入要设置为超级管理员的工号')
    return
  }
  
  try {
    settingSuperAdmin.value = true
    const response = await setSuperAdmin(superAdminId.value)
    
    if (response.success) {
      ElMessage.success(`已成功将工号 ${superAdminId.value} 设置为超级管理员`)
    } else {
      ElMessage.error(response.message || '设置超级管理员失败')
    }
  } catch (error) {
    console.error('设置超级管理员失败:', error)
    ElMessage.error('设置超级管理员失败，请稍后重试')
  } finally {
    settingSuperAdmin.value = false
  }
}

// 处理退出登录
const handleLogout = () => {
  ElMessage.info('正在退出登录...');
  userStore.logout();
  setTimeout(() => {
    router.push('/login');
  }, 500);
};

// 刷新所有测试
const refreshTests = async () => {
  loading.value = true;
  loginResult.value = null;
  
  try {
    // 测试服务器连接
    serverStatus.value = await ServerDiagnosticTools.checkConnection(config.apiBaseUrl);
    
    // 测试API端点
    const endpoints = [
      '/user/login',
      '/auth/login'
    ];
    
    apiEndpointTests.value = endpoints.map(endpoint => ({
      endpoint,
      result: { isOnline: false, message: '测试中...' }
    }));
    
    // 依次测试每个端点
    for (let i = 0; i < endpoints.length; i++) {
      const endpoint = endpoints[i];
      apiEndpointTests.value[i].result = await ServerDiagnosticTools.checkConnection(
        `${config.apiBaseUrl}${endpoint}`,
        parseInt(config.apiTimeout)
      );
    }
    
    // 获取完整诊断信息
    diagnosticInfo.value = await getServerDiagnostics();
    
  } catch (error) {
    console.error('测试过程出错:', error);
    ElMessage.error('测试过程中发生错误');
  } finally {
    loading.value = false;
  }
};

// 测试登录函数
const testLogin = async (type: 'normal' | 'alternative' | 'direct') => {
  loginTestLoading[type] = true;
  loginResult.value = null;
  
  try {
    let response;
    
    switch (type) {
      case 'normal':
        response = await login(loginForm);
        break;
      case 'alternative':
        response = await loginAlternative(loginForm);
        break;
      case 'direct':
        response = await loginAlternative2(loginForm);
        break;
    }
    
    loginResult.value = {
      success: response && response.code === 0,
      message: response ? response.message || '请求成功但没有返回消息' : '未收到响应',
      data: response
    };
    
  } catch (error: unknown) {
    console.error('登录测试失败:', error);
    loginResult.value = {
      success: false,
      message: error instanceof Error ? error.message : '未知错误',
      error: error instanceof Error ? {
        name: error.name,
        code: (error as {code?: string | number}).code,
        stack: error.stack
      } : {
        name: '未知错误类型'
      }
    };
  } finally {
    loginTestLoading[type] = false;
  }
};

// 组件加载时运行测试
onMounted(() => {
  refreshTests();
});
</script>

<style scoped>
.server-test-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.test-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
}

.test-section {
  margin-top: 30px;
}

.status-tag {
  margin-bottom: 10px;
  padding: 8px 16px;
  font-size: 14px;
}

.detail-info {
  font-size: 14px;
  color: #606266;
  margin-top: 5px;
}

.diagnostic-info, .result-json {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  overflow: auto;
  max-height: 300px;
  font-family: monospace;
  font-size: 12px;
  white-space: pre-wrap;
}
</style> 
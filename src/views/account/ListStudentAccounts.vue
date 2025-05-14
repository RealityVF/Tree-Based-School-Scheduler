<template>
  <div class="account-list-container">
    <div class="page-header">
      <h2>学生账号管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="router.push('/account/student-create')">
          <el-icon><Plus /></el-icon>
          创建账号
        </el-button>
        <el-button type="success" @click="router.push('/account/student-create?tab=batch')">
          <el-icon><Upload /></el-icon>
          批量导入
        </el-button>
      </div>
    </div>

    <div class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键字">
          <el-input
            v-model="searchForm.searchKey"
            placeholder="学号/姓名/账号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="院系">
          <el-select
            v-model="searchForm.faculty"
            placeholder="请选择"
            clearable
            style="width: 180px"
            @change="handleDepartmentChange"
          >
            <el-option
              v-for="item in departments"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="年级">
          <el-select
            v-model="searchForm.grade"
            placeholder="请选择"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in GRADE_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="专业">
          <el-select
            v-model="searchForm.major"
            placeholder="专业"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in majors"
              :key="item.majorId"
              :label="item.majorName"
              :value="item.majorId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table
        v-loading="loading"
        :data="accountList"
        style="width: 100%"
        border
        stripe
        row-key="studentId"
      >
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column label="院系" width="180">
          <template #default="scope">
            {{ scope.row.faculty }}
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="educationLevel" label="培养层次" width="100" />
        <el-table-column prop="major" label="专业" width="150" />
        <el-table-column label="政治面貌" width="100">
          <template #default="scope">
            {{ scope.row.politicalStatus || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="学籍状态" width="100">
          <template #default="scope">
            <el-tag type="info" size="small">
              {{ scope.row.studentStatus || '在读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag
              :type="scope.row.isActive ? 'success' : 'danger'"
              size="small"
            >
              {{ scope.row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleViewDetails(scope.row)"
            >
              详情
            </el-button>
            <el-button
              link
              type="primary"
              size="small"
              @click="handleEdit(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              link
              :type="scope.row.isActive ? 'danger' : 'success'"
              size="small"
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.isActive ? '禁用' : '启用' }}
            </el-button>
            <el-dropdown>
              <el-button link type="primary" size="small">
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleResetPassword(scope.row)">
                    重置密码
                  </el-dropdown-item>
                  <el-dropdown-item @click="handleSetPassword(scope.row)">
                    设置密码
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 账号详情对话框 -->
    <el-dialog
      v-model="detailsDialogVisible"
      title="账号详情"
      width="600px"
    >
      <template v-if="selectedAccount">
        <div class="account-details">
          <div class="detail-section">
            <h3>基本信息</h3>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="学号">{{ selectedAccount.studentId }}</el-descriptions-item>
              <el-descriptions-item label="登录账号">{{ selectedAccount.username }}</el-descriptions-item>
              <el-descriptions-item label="姓名">{{ selectedAccount.name }}</el-descriptions-item>
              <el-descriptions-item label="院系">{{ getDepartmentLabel(selectedAccount.faculty) }}</el-descriptions-item>
              <el-descriptions-item label="专业">{{ selectedAccount.major }}</el-descriptions-item>
              <el-descriptions-item label="年级">{{ selectedAccount.grade }}</el-descriptions-item>
              <el-descriptions-item label="班级">{{ selectedAccount.className }}</el-descriptions-item>
              <el-descriptions-item label="账号状态">
                <el-tag :type="selectedAccount.isActive ? 'success' : 'danger'">
                  {{ selectedAccount.isActive ? '启用' : '禁用' }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="detail-section">
            <h3>扩展信息</h3>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="性别">{{ selectedAccount.gender === 'male' ? '男' : (selectedAccount.gender === 'female' ? '女' : '-') }}</el-descriptions-item>
              <el-descriptions-item label="民族">{{ selectedAccount.ethnicity ? getEthnicityLabel(selectedAccount.ethnicity) : '-' }}</el-descriptions-item>
              <el-descriptions-item label="政治面貌">{{ selectedAccount.politicalStatus ? getPoliticalStatusLabel(selectedAccount.politicalStatus) : '-' }}</el-descriptions-item>
              <el-descriptions-item label="辅导员">{{ selectedAccount.counselor || '-' }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ selectedAccount.email || '-' }}</el-descriptions-item>
              <el-descriptions-item label="电话">{{ selectedAccount.phone || '-' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </div>
      </template>
    </el-dialog>

    <!-- 重置密码确认对话框 -->
    <el-dialog
      v-model="resetPasswordDialogVisible"
      title="重置密码"
      width="400px"
    >
      <div class="reset-password-confirm">
        <p>您确定要重置 <strong>{{ selectedAccount?.name }}</strong> 的密码吗？</p>
        <p class="warning">重置后将生成一个新的随机密码，请及时通知用户。</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmResetPassword" :loading="resetPasswordLoading">
            确认重置
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 重置密码结果对话框 -->
    <el-dialog
      v-model="resetPasswordResultDialogVisible"
      title="重置密码成功"
      width="400px"
    >
      <div class="reset-password-result">
        <el-alert
          title="密码重置成功"
          type="success"
          :closable="false"
          show-icon
        />
        <div class="new-password">
          <p>新密码：</p>
          <div class="password-display">
            <span>{{ newPassword }}</span>
            <el-button size="small" @click="copyPassword">
              <el-icon><DocumentCopy /></el-icon>复制
            </el-button>
          </div>
          <p class="password-tip">请保存此密码并告知用户，关闭此窗口后将无法再次查看。</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="resetPasswordResultDialogVisible = false">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 设置密码对话框 -->
    <el-dialog
      v-model="setPasswordDialogVisible"
      title="设置密码"
      width="400px"
    >
      <div class="set-password-form">
        <el-form ref="setPasswordFormRef" :model="setPasswordForm" :rules="setPasswordRules">
          <el-form-item label="新密码" prop="password">
            <el-input
              v-model="setPasswordForm.password"
              type="password"
              placeholder="请输入新密码"
              show-password
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="setPasswordForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="setPasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSetPassword" :loading="setPasswordLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, ArrowDown, DocumentCopy, Upload } from '@element-plus/icons-vue'
import {
  getStudentAccounts,
  getStudentAccountDetail,
  toggleStudentAccountStatus,
  resetStudentPassword,
  setStudentPassword
} from '@/api/account'
import { getDepartments } from '@/api/department'
import { getMajorsByDepartmentId } from '@/api/major'
import {
  POLITICAL_STATUS_OPTIONS,
  GENDER_OPTIONS,
  GRADE_OPTIONS,
  type StudentAccount
} from '@/types/account'
import type { Department } from '@/types/department'
import type { Major } from '@/types/major'

const router = useRouter()
const loading = ref(false)
const accountList = ref<StudentAccount[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 院系和专业数据
const departments = ref<Department[]>([])
const majors = ref<Major[]>([])
const loadingDepartments = ref(false)
const loadingMajors = ref(false)

// 搜索表单
const searchForm = reactive({
  searchKey: '',
  faculty: '',
  grade: '',
  major: '',
  className: ''
})

// 账号详情对话框
const detailsDialogVisible = ref(false)
const selectedAccount = ref<StudentAccount | null>(null)

// 重置密码相关
const resetPasswordDialogVisible = ref(false)
const resetPasswordResultDialogVisible = ref(false)
const resetPasswordLoading = ref(false)
const newPassword = ref('')

// 设置密码相关的响应式变量
const setPasswordDialogVisible = ref(false)
const setPasswordLoading = ref(false)
const setPasswordForm = reactive({
  password: '',
  confirmPassword: ''
})
const setPasswordFormRef = ref()
const setPasswordRules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== setPasswordForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取标签显示文本的辅助函数
const getDepartmentLabel = (value: string) => {
  const option = departments.value.find(item => item.id === value)
  return option ? option.name : value
}

const getEthnicityLabel = (value: string) => {
  const option = GENDER_OPTIONS.find(item => item.value === value)
  return option ? option.label : value
}

const getPoliticalStatusLabel = (value: string) => {
  const option = POLITICAL_STATUS_OPTIONS.find(item => item.value === value)
  return option ? option.label : value
}

// 加载院系列表
const loadDepartments = async () => {
  loadingDepartments.value = true
  try {
    const response = await getDepartments()
    if (response.success) {
      // 只显示教学单位（ID以1开头的部门）
      departments.value = response.data.filter(dept => dept.id.startsWith('1'))
      console.log('过滤后的院系列表:', departments.value)
    } else {
      ElMessage.error(response.message || '获取院系列表失败')
    }
  } catch (error) {
    console.error('获取院系列表失败:', error)
    ElMessage.error('获取院系列表失败')
  } finally {
    loadingDepartments.value = false
  }
}

// 加载专业列表
const loadMajors = async (departmentId: string) => {
  if (!departmentId) {
    majors.value = []
    return
  }

  loadingMajors.value = true
  try {
    const response = await getMajorsByDepartmentId(departmentId)
    if (response.success) {
      majors.value = response.data
    } else {
      ElMessage.error(response.message || '获取专业列表失败')
    }
  } catch (error) {
    console.error('获取专业列表失败:', error)
    ElMessage.error('获取专业列表失败')
  } finally {
    loadingMajors.value = false
  }
}

// 监听院系变化
const handleDepartmentChange = (value: string) => {
  searchForm.major = '' // 清空专业选择
  if (value) {
    loadMajors(value)
  }
}

// 加载账号列表数据
const loadAccounts = async () => {
  loading.value = true
  try {
    const offset = (currentPage.value - 1) * pageSize.value
    const response = await getStudentAccounts({
      searchKey: searchForm.searchKey.trim(),
      faculty: searchForm.faculty,
      grade: searchForm.grade,
      major: searchForm.major,
      offset,
      size: pageSize.value
    })
    if (response.success) {
      accountList.value = response.data
      total.value = response.total
    } else {
      ElMessage.error(response.message || '获取学生账号列表失败')
    }
  } catch (error) {
    ElMessage.error('获取学生账号列表失败')
    console.error('Failed to load student accounts:', error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  currentPage.value = 1
  loadAccounts()
}

// 重置搜索条件
const handleReset = () => {
  searchForm.searchKey = ''
  searchForm.faculty = ''
  searchForm.grade = ''
  searchForm.major = ''
  searchForm.className = ''
  currentPage.value = 1
  loadAccounts()
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1 // 重置到第一页
  loadAccounts()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadAccounts()
}

// 查看账号详情
const handleViewDetails = async (account: StudentAccount) => {
  try {
    const response = await getStudentAccountDetail(account.studentId)
    if (response.success && response.data) {
      selectedAccount.value = response.data
      detailsDialogVisible.value = true
    } else {
      ElMessage.error(response.message || '获取账号详情失败')
    }
  } catch (error) {
    console.error('获取账号详情失败:', error)
    ElMessage.error('获取账号详情失败，请稍后重试')
  }
}

// 编辑账号
const handleEdit = (account: StudentAccount) => {
  router.push(`/account/student-edit/${account.studentId}`)
}

// 启用/禁用账号
const handleToggleStatus = async (row: StudentAccount) => {
  const enable = !row.isActive;
  const action = enable ? '启用' : '禁用';

  ElMessageBox.confirm(
    `确定要${action}该学生账号吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    loading.value = true;
    try {
      const result = await toggleStudentAccountStatus(row.studentId, enable);
      if (result.success) {
        ElMessage.success(`${action}成功`);
        row.isActive = enable;
        await loadAccounts();
      } else {
        ElMessage.error(result.message || `${action}失败`);
      }
    } catch (error) {
      console.error(`${action}账号失败:`, error);
      ElMessage.error(`${action}失败，请稍后重试`);
    } finally {
      loading.value = false;
    }
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

// 重置密码
const handleResetPassword = (account: StudentAccount) => {
  selectedAccount.value = account
  resetPasswordDialogVisible.value = true
}

// 设置密码
const handleSetPassword = (account: StudentAccount) => {
  selectedAccount.value = account
  setPasswordForm.password = ''
  setPasswordForm.confirmPassword = ''
  setPasswordDialogVisible.value = true
}

// 确认设置密码
const confirmSetPassword = async () => {
  if (!setPasswordFormRef.value) return

  await setPasswordFormRef.value.validate(async (valid: boolean) => {
    if (valid && selectedAccount.value) {
      setPasswordLoading.value = true
      try {
        const response = await setStudentPassword(selectedAccount.value.studentId, setPasswordForm.password)
        if (response.success) {
          ElMessage.success('密码设置成功')
          setPasswordDialogVisible.value = false
        } else {
          ElMessage.error(response.message || '密码设置失败')
        }
      } catch (error) {
        console.error('设置密码失败:', error)
        ElMessage.error('设置密码失败，请稍后重试')
      } finally {
        setPasswordLoading.value = false
      }
    }
  })
}

// 确认重置密码
const confirmResetPassword = async () => {
  if (!selectedAccount.value) return

  resetPasswordLoading.value = true
  try {
    const response = await resetStudentPassword(selectedAccount.value.studentId)
    if (response.success && response.newPassword) {
      resetPasswordDialogVisible.value = false
      newPassword.value = response.newPassword
      resetPasswordResultDialogVisible.value = true
    } else {
      ElMessage.error(response.message || '重置密码失败')
    }
  } catch (error) {
    console.error('重置密码失败:', error)
    ElMessage.error('重置密码失败，请稍后重试')
  } finally {
    resetPasswordLoading.value = false
  }
}

// 复制新密码到剪贴板
const copyPassword = () => {
  navigator.clipboard.writeText(newPassword.value)
    .then(() => {
      ElMessage.success('密码已复制到剪贴板')
    })
    .catch(() => {
      ElMessage.error('复制失败，请手动复制')
    })
}

// 页面加载时获取账号列表和院系列表
onMounted(() => {
  loadAccounts()
  loadDepartments()
})
</script>

<style scoped>
.account-list-container {
  width: 100%;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.search-card {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 24px;
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.table-card {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.account-details {
  padding: 10px;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h3 {
  margin: 0 0 16px;
  font-size: 18px;
  color: #333;
  font-weight: 500;
}

.reset-password-confirm {
  text-align: center;
  padding: 10px 0;
}

.reset-password-confirm .warning {
  color: #e6a23c;
  margin-top: 10px;
}

.reset-password-result {
  padding: 20px 0;
}

.new-password {
  margin-top: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
  background-color: #f9f9f9;
}

.password-display {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 8px 15px;
  margin: 10px 0;
}

.password-tip {
  font-size: 12px;
  color: #f56c6c;
  margin-top: 10px;
  margin-bottom: 0;
}

.set-password-form {
  padding: 20px;
}
</style>

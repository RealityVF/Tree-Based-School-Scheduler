<template>
  <div class="account-list-container">
    <div class="page-header">
      <h2>教师账号管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="router.push('/account/create')">
          <el-icon><Plus /></el-icon>
          创建账号
        </el-button>
        <el-button type="success" @click="router.push('/account/create?tab=batch')">
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
            placeholder="工号/姓名/账号"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="单位/部门">
          <el-select
            v-model="searchForm.department"
            placeholder="请选择"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in departments"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教职工类别">
          <el-select
            v-model="searchForm.teacherType"
            placeholder="请选择"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
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
        row-key="teacherId"
      >
        <el-table-column prop="teacherId" label="工号" width="100" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column label="单位/部门" width="180">
          <template #default="scope">
            {{ getDepartmentLabel(scope.row.departmentId) }}
          </template>
        </el-table-column>
        <el-table-column label="教职工类别" width="120">
          <template #default="scope">
            {{ scope.row.teacherType || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="职称" width="120">
          <template #default="scope">
            {{ scope.row.title || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="外聘" width="80">
          <template #default="scope">
            <el-tag
              :type="scope.row.isExternal ? 'success' : 'info'"
              size="small"
            >
              {{ scope.row.isExternal ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'danger'" size="small">
              {{ row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              @click="handleViewDetails(row)"
            >
              详情
            </el-button>
            <el-button
              link
              type="primary"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              link
              :type="row.isActive ? 'danger' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.isActive ? '禁用' : '启用' }}
            </el-button>
            <el-dropdown>
              <el-button link type="primary">
                更多<el-icon><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleResetPassword(row)">
                    重置密码
                  </el-dropdown-item>
                  <el-dropdown-item @click="handleSetPassword(row)">
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
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 重置密码结果对话框 -->
    <el-dialog
      v-model="resetPasswordResultDialogVisible"
      title="重置密码成功"
      width="400px"
    >
      <div class="reset-password-result">
        <p>新密码已生成：</p>
        <div class="new-password">
          <span>{{ newPassword }}</span>
          <el-button type="primary" size="small" @click="copyPassword">
            复制密码
          </el-button>
        </div>
        <p class="warning">请及时将新密码告知教师本人，并提醒其及时修改密码。</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetPasswordResultDialogVisible = false">
            关闭
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

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailsVisible"
      title="教师账号详情"
      width="600px"
    >
      <el-descriptions
        :column="2"
        border
      >
        <el-descriptions-item label="工号">
          {{ selectedAccount?.teacherId }}
        </el-descriptions-item>
        <el-descriptions-item label="姓名">
          {{ selectedAccount?.name }}
        </el-descriptions-item>
        <el-descriptions-item label="英文名">
          {{ selectedAccount?.englishName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ selectedAccount?.gender || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="民族">
          {{ selectedAccount?.ethnicity || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="职称">
          {{ selectedAccount?.title || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="所属部门">
          {{ selectedAccount?.departmentId ? getDepartmentLabel(selectedAccount.departmentId) : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="教师类型">
          {{ selectedAccount?.teacherType || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ selectedAccount?.phone || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="电子邮箱">
          {{ selectedAccount?.email || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="最大周课时">
          {{ selectedAccount?.maxWeeklyHours || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="账号状态">
          <el-tag :type="selectedAccount?.isActive ? 'success' : 'danger'">
            {{ selectedAccount?.isActive ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="研究方向" :span="2">
          {{ selectedAccount?.researchDirection || '-' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailsVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑教师信息对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="'编辑教师信息'"
      width="600px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="formRules"
        label-width="100px"
        label-position="right"
      >
        <el-form-item label="工号" prop="teacherId">
          <el-input v-model="editForm.teacherId" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="英文名">
          <el-input v-model="editForm.englishName" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="editForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="民族">
          <el-input v-model="editForm.ethnicity" />
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="所属部门" prop="departmentId">
          <el-select v-model="editForm.departmentId" placeholder="请选择部门">
            <el-option
              v-for="item in departments"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教师类型">
          <el-select v-model="editForm.teacherType" placeholder="请选择教师类型">
            <el-option
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="电子邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="最大周课时">
          <el-input-number v-model="editForm.maxWeeklyHours" :min="0" />
        </el-form-item>
        <el-form-item label="是否外聘">
          <el-switch v-model="editForm.isExternal" />
        </el-form-item>
        <el-form-item label="研究方向">
          <el-input v-model="editForm.researchDirection" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditForm" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Upload, ArrowDown } from '@element-plus/icons-vue'
import type { FormItemRule } from 'element-plus'
import {
  getTeacherAccounts,
  toggleTeacherAccountStatus,
  resetTeacherPassword,
  setTeacherPassword,
  getTeacherOptions,
  updateTeacherAccount
} from '@/api/account'
import type { TeacherAccount } from '@/types/account'
import { Gender } from '@/types/account'
import { getDepartments } from '@/api/department'
import type { Department } from '@/types/department'

const router = useRouter()

const loading = ref(false)
const accountList = ref<TeacherAccount[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const departments = ref<Department[]>([])
const typeOptions = ref<{ value: string; label: string }[]>([])
const newPassword = ref('')
const resetPasswordResultDialogVisible = ref(false)
const selectedAccount = ref<TeacherAccount | null>(null)
const detailsVisible = ref(false)

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
      validator: (_rule: FormItemRule, value: string, callback: (error?: Error) => void) => {
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

// 搜索表单
const searchForm = reactive({
  searchKey: '',
  department: '',
  teacherType: ''
})

// 编辑表单相关
const editDialogVisible = ref(false)
const editFormRef = ref()
const submitLoading = ref(false)
const editForm = reactive({
  teacherId: '',
  name: '',
  englishName: '',
  gender: '男',
  ethnicity: '',
  title: '',
  departmentId: '',
  isExternal: false,
  teacherType: '',
  satisfaction: true,
  phone: '',
  email: '',
  maxWeeklyHours: 16,
  researchDirection: '',
  isActive: true
})

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  departmentId: [{ required: true, message: '请选择部门', trigger: 'change' }]
}

// 获取部门列表
const fetchDepartments = async () => {
  try {
    const response = await getDepartments()
    if (response.success) {
      departments.value = response.data
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
  }
}

// 获取教师类型选项
const fetchOptions = async () => {
  try {
    const response = await getTeacherOptions()
    if (response.data) {
      typeOptions.value = response.data.types
    }
  } catch (error) {
    console.error('获取教师类型选项失败:', error)
  }
}

// 获取部门名称
const getDepartmentLabel = (departmentId: string) => {
  const department = departments.value.find(d => d.id === departmentId)
  return department?.name || '-'
}

// 加载账号列表数据
const loadAccounts = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      department: searchForm.department,
      teacherType: searchForm.teacherType,
      searchKey: searchForm.searchKey
    }
    console.log('Fetching accounts with params:', params)

    const response = await getTeacherAccounts(params)
    console.log('Raw API Response:', response)

    if (response.success && response.data?.teachers) {
      console.log('Transformed teachers:', response.data.teachers)
      accountList.value = response.data.teachers
      total.value = response.data.total

      // 如果当前页没有数据且不是第一页，回到上一页
      if (accountList.value.length === 0 && currentPage.value > 1) {
        currentPage.value--
        await loadAccounts()
      }
    } else {
      console.error('Failed to load accounts:', response)
      ElMessage.error(response.message || '获取账号列表失败')
      accountList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('加载账号列表失败:', error)
    ElMessage.error('加载账号列表失败，请稍后重试')
    accountList.value = []
    total.value = 0
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
  searchForm.department = ''
  searchForm.teacherType = ''
  currentPage.value = 1
  loadAccounts()
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadAccounts()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadAccounts()
}

// 编辑账号
const handleEdit = (account: TeacherAccount) => {
  // 复制账号信息到编辑表单
  Object.assign(editForm, {
    teacherId: account.teacherId,
    name: account.name,
    englishName: account.englishName || '',
    gender: account.gender || '男',
    ethnicity: account.ethnicity || '',
    title: account.title || '',
    departmentId: account.departmentId || '',
    isExternal: account.isExternal || false,
    teacherType: account.teacherType || '',
    satisfaction: account.satisfaction !== undefined ? account.satisfaction : true,
    phone: account.phone || '',
    email: account.email || '',
    maxWeeklyHours: account.maxWeeklyHours || 16,
    researchDirection: account.researchDirection || '',
    isActive: account.isActive
  })

  // 打开编辑对话框
  editDialogVisible.value = true
}

// 启用/禁用账号
const handleToggleStatus = async (account: TeacherAccount) => {
  const enable = !account.isActive
  const actionText = enable ? '启用' : '禁用'

  try {
    await ElMessageBox.confirm(
      `确定要${actionText}账号 ${account.name}（${account.teacherId}）吗？`,
      `${actionText}账号`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await toggleTeacherAccountStatus(account.teacherId, enable)
    if (response?.success) {
      ElMessage.success(`${actionText}账号成功`)
      // 立即更新本地状态
      account.isActive = enable
      // 重新加载列表
      await loadAccounts()
    } else {
      ElMessage.error(response?.message || `${actionText}账号失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${actionText}账号失败:`, error)
      ElMessage.error(`${actionText}账号失败，请稍后重试`)
    }
  }
}

// 重置密码
const handleResetPassword = async (account: TeacherAccount) => {
  try {
    const response = await resetTeacherPassword(account.teacherId)
    if (response?.success && response?.newPassword) {
      ElMessage.success('密码重置成功')
      // 显示新密码
      newPassword.value = response.newPassword
      resetPasswordResultDialogVisible.value = true
    } else {
      ElMessage.error('密码重置失败')
    }
  } catch (error) {
    console.error('重置密码失败:', error)
    ElMessage.error('重置密码失败，请稍后重试')
  }
}

// 设置密码
const handleSetPassword = (account: TeacherAccount) => {
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
        const response = await setTeacherPassword(selectedAccount.value.teacherId, setPasswordForm.password)
        if (response?.success) {
          ElMessage.success('密码设置成功')
          setPasswordDialogVisible.value = false
        } else {
          ElMessage.error(response?.message || '密码设置失败')
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

// 查看详情
const handleViewDetails = (row: TeacherAccount) => {
  detailsVisible.value = true;
  selectedAccount.value = row;
};

// 提交编辑表单
const submitEditForm = async () => {
  if (!editFormRef.value) return

  await editFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        // 转换Gender类型
        const genderValue = editForm.gender === '男' ? Gender.男 : Gender.女;

        const response = await updateTeacherAccount(editForm.teacherId, {
          ...editForm,
          gender: genderValue
        })

        if (response.success) {
          ElMessage.success('教师信息更新成功')
          editDialogVisible.value = false
          // 刷新数据
          await loadAccounts()
        } else {
          ElMessage.error(response.message || '更新失败')
        }
      } catch (error) {
        console.error('更新教师信息失败:', error)
        ElMessage.error('更新教师信息失败，请稍后重试')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 组件挂载时加载数据
onMounted(async () => {
  await Promise.all([
    fetchDepartments(),
    fetchOptions(),
    loadAccounts()
  ])
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

.change-role-content {
  padding: 10px 0;
}

.role-select {
  margin: 20px 0;
}

.role-description {
  margin-top: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
  padding: 15px;
  font-size: 14px;
}

.role-description .warning {
  color: #f56c6c;
  margin-top: 5px;
}

.el-dropdown {
  margin-left: 8px;
}
</style>

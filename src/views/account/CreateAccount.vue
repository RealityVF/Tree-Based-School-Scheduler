<template>
  <div class="create-account-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑教师账号' : '创建教师账号' }}</h2>
      <div class="header-actions">
        <el-button @click="router.back()">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="create-tabs">
      <el-tab-pane label="单个创建" name="single">
        <div class="form-card">
          <el-form
            ref="formRef"
            :model="formData"
            :rules="formRules"
            label-width="120px"
            status-icon
          >
            <el-tabs v-model="formTabActive">
              <el-tab-pane label="基本信息" name="basic">
                <div class="form-section">
                  <h3>账号基本信息</h3>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="教师号" prop="teacherId">
                        <el-input
                          v-model="formData.teacherId"
                          placeholder="请输入教师号（11位数字）"
                          clearable
                          @input="handleTeacherIdInput"
                        />
                        <span class="role-tips">注意：教师号同时也是登录账号</span>
                      </el-form-item>
                    </el-col>

                    <el-col :span="12">
                      <el-form-item label="姓名" prop="name">
                        <el-input
                          v-model="formData.name"
                          placeholder="请输入姓名"
                          clearable
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="初始密码" prop="password">
                        <el-input
                          v-model="formData.password"
                          placeholder="将自动使用教师号后六位作为初始密码"
                          :disabled="true"
                          show-password
                        />
                        <span class="role-tips">注意：初始密码为教师号后六位</span>
                      </el-form-item>
                    </el-col>

                    <el-col :span="12">
                      <el-form-item label="单位/部门" prop="departmentId">
                        <el-select
                          v-model="formData.departmentId"
                          placeholder="请选择单位/部门"
                          clearable
                          filterable
                          style="width: 100%"
                        >
                          <el-option
                            v-for="item in departmentOptions"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id"
                          />
                        </el-select>
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="教职工类别" prop="teacherType">
                        <el-select
                          v-model="formData.teacherType"
                          placeholder="请选择教职工类别"
                          clearable
                          filterable
                          style="width: 100%"
                        >
                          <el-option
                            v-for="item in TEACHER_TYPES"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </el-col>

                    <el-col :span="12">
                      <el-form-item label="手机号" prop="phone">
                        <el-input v-model="formData.phone" placeholder="请输入手机号" />
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row :gutter="20" v-if="isSuperAdmin">
                    <el-col :span="12">
                      <el-form-item label="账号权限" prop="roleType">
                        <el-select
                          v-model="formData.roleType"
                          placeholder="请选择账号权限"
                          style="width: 100%"
                        >
                          <el-option
                            v-for="item in ROLE_TYPE_OPTIONS"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                        <span class="role-tips">注意：拥有管理员权限的教师账号将可以登录管理后台</span>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </div>
              </el-tab-pane>

              <el-tab-pane label="扩展信息" name="extension">
                <div class="form-section">
                  <h3>账号扩展信息（选填）</h3>

                  <el-row :gutter="20">
                    <el-col :span="8">
                      <el-form-item label="性别" prop="gender">
                        <el-radio-group v-model="formData.gender">
                          <el-radio label="男">男</el-radio>
                          <el-radio label="女">女</el-radio>
                        </el-radio-group>
                      </el-form-item>
                    </el-col>

                    <el-col :span="8">
                      <el-form-item label="民族" prop="ethnicity">
                        <el-select
                          v-model="formData.ethnicity"
                          placeholder="请选择民族"
                          clearable
                          filterable
                          style="width: 100%"
                        >
                          <el-option
                            v-for="item in ETHNICITY_OPTIONS"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </el-col>

                    <el-col :span="8">
                      <el-form-item label="职称" prop="title">
                        <el-select
                          v-model="formData.title"
                          placeholder="请选择职称"
                          clearable
                          filterable
                          style="width: 100%"
                        >
                          <el-option
                            v-for="item in TEACHER_TITLES"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="是否外聘" prop="isExternal">
                        <el-switch
                          v-model="formData.isExternal"
                          active-text="是"
                          inactive-text="否"
                        />
                      </el-form-item>
                    </el-col>

                    <el-col :span="12">
                      <el-form-item label="英文姓名" prop="englishName">
                        <el-input
                          v-model="formData.englishName"
                          placeholder="请输入英文姓名（可选）"
                          clearable
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="邮箱" prop="email">
                        <el-input
                          v-model="formData.email"
                          placeholder="请输入邮箱地址"
                          clearable
                          type="email"
                        />
                      </el-form-item>
                    </el-col>

                    <el-col :span="12">
                      <el-form-item label="每周最大课时" prop="maxWeeklyHours">
                        <el-input-number
                          v-model="formData.maxWeeklyHours"
                          :min="0"
                          :max="40"
                          placeholder="请输入每周最大课时"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="研究方向" prop="researchDirection">
                        <el-input
                          v-model="formData.researchDirection"
                          type="textarea"
                          :rows="3"
                          placeholder="请输入研究方向"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                </div>
              </el-tab-pane>
            </el-tabs>

            <div class="form-actions">
              <el-button @click="resetForm">重置</el-button>
              <el-button type="primary" @click="submitForm" :loading="loading">
                {{ isEdit ? '保存修改' : '创建账号' }}
              </el-button>
            </div>
          </el-form>
        </div>
      </el-tab-pane>

      <el-tab-pane label="批量导入" name="batch">
        <div class="form-card">
          <div class="batch-upload-section">
            <div class="upload-info">
              <h3>批量导入教师账号</h3>
              <p>请上传含有教师账号信息的Excel文件(.xlsx)进行批量导入</p>
              <div class="template-download">
                <span>请确保文件格式正确。可以下载模板:</span>
                <el-button type="primary" link @click="downloadTemplate">下载模板</el-button>
              </div>
            </div>

            <el-upload
              class="batch-upload"
              drag
              :action="uploadAction"
              :auto-upload="false"
              :show-file-list="true"
              :limit="1"
              :on-change="handleFileChange"
              :before-upload="beforeUpload"
              :on-exceed="handleExceed"
              accept=".xlsx,.xls"
              ref="uploadRef"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将文件拖到此处，或<em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  支持.xlsx、.xls格式，不超过2MB
                </div>
              </template>
            </el-upload>

            <div class="upload-actions">
              <el-button type="primary" @click="handleBatchUpload" :loading="batchLoading">
                开始导入
              </el-button>
            </div>
          </div>

          <!-- 导入结果对话框 -->
          <el-dialog
            v-model="importResultVisible"
            title="导入结果"
            width="500px"
            :close-on-click-modal="false"
          >
            <div class="import-result">
              <el-result
                :icon="importSuccess ? 'success' : 'warning'"
                :title="importSuccess ? '导入成功' : '导入部分完成'"
                :sub-title="getImportResultSummary()"
              >
                <template #extra>
                  <div class="result-details" v-if="importResult && importResult.errors && importResult.errors.length > 0">
                    <h4>导入失败明细：</h4>
                    <el-table :data="importResult.errors" style="width: 100%" border size="small">
                      <el-table-column prop="teacherId" label="教师号" width="100" />
                      <el-table-column prop="reason" label="失败原因" />
                    </el-table>
                  </div>
                </template>
              </el-result>
              <div class="dialog-footer">
                <el-button @click="importResultVisible = false">关闭</el-button>
                <el-button type="primary" @click="handleAfterImport">
                  {{ importSuccess ? '查看账号列表' : '继续导入' }}
                </el-button>
              </div>
            </div>
          </el-dialog>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules, UploadInstance, UploadProps } from 'element-plus'
import { ArrowLeft, UploadFilled } from '@element-plus/icons-vue'
import { createTeacherAccount, batchCreateTeacherAccounts, getTeacherAccountDetail, updateTeacherAccount, getDepartments } from '@/api/account'
import type { TeacherAccount } from '@/types/account'
import {
  AccountStatus,
  TEACHER_TYPES,
  ETHNICITY_OPTIONS,
  TEACHER_TITLES,
  DEPARTMENT_OPTIONS,
  RoleType,
  ROLE_TYPE_OPTIONS,
  Gender
} from '@/types/account'
import { useUserStore } from '@/stores/counter'

const router = useRouter()
const route = useRoute()
const formRef = ref<FormInstance>()
const uploadRef = ref<UploadInstance>()
const loading = ref(false)
const batchLoading = ref(false)
const activeTab = ref('single')
const formTabActive = ref('basic')
const uploadAction = '#' // 阻止自动上传

// 导入结果相关
const importResultVisible = ref(false)
const importSuccess = ref(false)
const importResult = ref<{
  total: number;
  success: number;
  failed: number;
  errors: Array<{teacherId: string; reason: string}>;
} | null>(null)

// 文件变量
const selectedFile = ref<File | null>(null);

// 表单数据
const formData = ref({
  teacherId: '',           // 教师号
  name: '',               // 姓名
  gender: '男',           // 性别
  departmentId: '',       // 部门ID
  teacherType: '',        // 教职工类别
  englishName: '',        // 英文姓名
  ethnicity: '',          // 民族
  title: '',             // 职称
  isExternal: false,      // 是否外聘
  email: '',             // 邮箱
  phone: '',             // 电话
  maxWeeklyHours: 16,    // 最大周课时
  researchDirection: '',  // 研究方向
  satisfaction: true,     // 教室位置满意度
  isActive: true,        // 是否激活
  password: ''           // 密码
})

// 表单验证规则
const formRules = reactive<FormRules>({
  teacherId: [
    { required: true, message: '请输入教师号', trigger: 'blur' },
    { pattern: /^\d{11}$/, message: '教师号必须是11位数字', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度应为2-50个字符', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请选择单位/部门', trigger: 'change' }
  ],
  teacherType: [
    { required: true, message: '请选择教职工类别', trigger: 'change' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  maxWeeklyHours: [
    { type: 'number', min: 0, max: 40, message: '最大周课时应在0-40之间', trigger: 'blur' }
  ]
})

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  formData.value.gender = '男'
  formData.value.isExternal = false
  formData.value.maxWeeklyHours = 16
  formData.value.satisfaction = true
  formData.value.isActive = true
  formData.value.password = ''
}

// 页面模式（创建/编辑）
const isEdit = computed(() => !!route.params.id);
const teacherId = computed(() => route.params.id as string);

// 加载账号详情
const loadAccountDetail = async () => {
  if (!isEdit.value) return;

  try {
    loading.value = true;

    const response = await getTeacherAccountDetail(teacherId.value);

    if (response.success && response.data) {
      // 填充表单数据
      const accountData = response.data;

      // 使用类型安全的方式更新formData
      if (accountData.teacherId) formData.value.teacherId = accountData.teacherId;
      if (accountData.name) formData.value.name = accountData.name;
      if (accountData.departmentId) formData.value.departmentId = accountData.departmentId;
      if (accountData.teacherType) formData.value.teacherType = accountData.teacherType;
      if (accountData.gender) formData.value.gender = accountData.gender;
      if (accountData.ethnicity) formData.value.ethnicity = accountData.ethnicity;
      if (accountData.title) formData.value.title = accountData.title;
      if (accountData.isExternal !== undefined) formData.value.isExternal = accountData.isExternal;
      if (accountData.englishName) formData.value.englishName = accountData.englishName;
      if (accountData.email) formData.value.email = accountData.email;
      if (accountData.phone) formData.value.phone = accountData.phone;
      if (accountData.status) formData.value.status = accountData.status;
      if (accountData.roleType) formData.value.roleType = accountData.roleType;
    } else {
      ElMessage.error(response.message || '获取账号详情失败');
      router.push('/account/list');
    }
  } catch (error) {
    console.error('获取账号详情失败:', error);
    ElMessage.error('获取账号详情失败，请稍后重试');
    router.push('/account/list');
  } finally {
    loading.value = false;
  }
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();

    loading.value = true;

    // 准备提交数据
    const submitData = {
      teacherId: formData.value.teacherId,
      name: formData.value.name,
      gender: formData.value.gender,
      departmentId: formData.value.departmentId, // 确保使用部门ID
      teacherType: formData.value.teacherType,
      englishName: formData.value.englishName,
      ethnicity: formData.value.ethnicity,
      title: formData.value.title,
      isExternal: formData.value.isExternal,
      email: formData.value.email,
      phone: formData.value.phone,
      maxWeeklyHours: formData.value.maxWeeklyHours,
      researchDirection: formData.value.researchDirection,
      satisfaction: formData.value.satisfaction,
      isActive: formData.value.isActive,
      password: formData.value.password
    };

    // 创建新账号
    const response = await createTeacherAccount(submitData);

    if (response.success) {
      ElMessage.success('创建教师账号成功');
      resetForm();
      router.push('/account/list');
    } else {
      ElMessage.error(response.message || '创建教师账号失败');
    }
  } catch (error) {
    console.error('表单验证失败:', error);
  } finally {
    loading.value = false;
  }
}

// 文件上传前的校验
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  // 检查文件类型
  const fileTypes = [
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  ];

  const isValidType = fileTypes.includes(file.type);
  if (!isValidType) {
    ElMessage.error('只能上传Excel文件(.xlsx, .xls)!');
    return false;
  }

  // 检查文件大小
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error('文件大小不能超过2MB!');
    return false;
  }

  return true;
}

// 文件change事件 - 修改为只记录文件选择
const handleFileChange: UploadProps['onChange'] = (file) => {
  if (file.raw) {
    // 存储选择的文件，以便后续上传使用
    selectedFile.value = file.raw;
    console.log('文件已选择:', file.name);
  }
}

// 超出文件数量限制
const handleExceed = () => {
  ElMessage.warning('最多只能上传1个文件');
}

// 处理批量上传
const handleBatchUpload = async () => {
  // 检查是否已选择文件
  if (!selectedFile.value) {
    ElMessage.warning('请先选择要上传的文件');
    return;
  }

  try {
    batchLoading.value = true;

    const response = await batchCreateTeacherAccounts(selectedFile.value);

    if (response.success) {
      importResult.value = response.data || null;
      importSuccess.value = importResult.value ?
        importResult.value.failed === 0 :
        false;
      importResultVisible.value = true;
    } else {
      ElMessage.error(response.message || '批量导入账号失败');
    }
  } catch (error) {
    console.error('批量导入账号失败:', error);
    ElMessage.error('批量导入账号失败，请稍后重试');
  } finally {
    batchLoading.value = false;
  }
}

// 获取导入结果摘要
const getImportResultSummary = () => {
  if (!importResult.value) return '';

  const { total, success, failed } = importResult.value;
  return `共处理 ${total} 条记录，成功 ${success} 条，失败 ${failed} 条`;
}

// 导入后操作
const handleAfterImport = () => {
  if (importSuccess.value) {
    importResultVisible.value = false;
    router.push('/account/list');
  } else {
    importResultVisible.value = false;
  }
}

// 下载模板
const downloadTemplate = () => {
  // 实际应该从后端获取模板文件
  ElMessage.info('模板下载功能将连接到后端API');

  // 模拟下载链接
  const templateUrl = '/api/admin/accounts/teacher/template';

  // 创建a标签并触发下载
  const link = document.createElement('a');
  link.href = templateUrl;
  link.setAttribute('download', '教师账号导入模板.xlsx');
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

// 根据URL参数设置默认tab，加载账号详情
onMounted(() => {
  if (route.query.tab === 'batch') {
    activeTab.value = 'batch';
  }

  if (isEdit.value) {
    loadAccountDetail();
  }

  fetchDepartments();
});

const userStore = useUserStore()

// 判断当前用户是否为超级管理员
const isSuperAdmin = computed(() => userStore.isSuperAdmin)

const departmentOptions = ref<Array<{ id: string; name: string }>>([])

// 获取部门列表
const fetchDepartments = async () => {
  try {
    const response = await getDepartments()
    if (response.success) {
      departmentOptions.value = response.data.map(dept => ({
        id: dept.id,
        name: dept.name
      }))
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
    ElMessage.error('获取部门列表失败，请稍后重试')
  }
}

// 处理教师号输入
const handleTeacherIdInput = (value: string) => {
  console.log('Teacher ID input:', value); // 添加日志
  if (value && value.length >= 6) {
    const lastSix = value.slice(-6);
    console.log('Setting password to:', lastSix); // 添加日志
    formData.value.password = lastSix;
  } else {
    formData.value.password = '';
  }
}
</script>

<style scoped>
.create-account-container {
  width: 100%;
  max-width: 1000px;
  margin: 0 auto;
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

.create-tabs {
  margin-top: 20px;
}

.form-card {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.form-section {
  margin-bottom: 20px;
}

.form-section h3 {
  margin: 0 0 16px;
  font-size: 18px;
  color: #333;
  font-weight: 500;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.batch-upload-section {
  padding: 20px 0;
}

.upload-info {
  margin-bottom: 24px;
}

.upload-info h3 {
  margin: 0 0 16px;
  font-size: 18px;
  color: #333;
  font-weight: 500;
}

.upload-info p {
  color: #606266;
  margin-bottom: 16px;
}

.template-download {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.batch-upload {
  width: 100%;
  margin-bottom: 24px;
}

.upload-actions {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.import-result {
  text-align: center;
}

.result-details {
  text-align: left;
  margin: 20px 0;
}

.result-details h4 {
  margin-bottom: 10px;
  color: #606266;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 20px;
}

.role-tips {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>

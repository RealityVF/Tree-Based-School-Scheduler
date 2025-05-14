<template>
  <div class="create-account-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑学生信息' : '创建学生账号' }}</h2>
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
                  <h3>学生基本信息</h3>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="学号" prop="id">
                        <el-input
                          v-model="formData.id"
                          placeholder="请输入学号(14位)"
                          :disabled="isEdit"
                          clearable
                        />
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
                      <el-form-item label="性别" prop="gender">
                        <el-select
                          v-model="formData.gender"
                          placeholder="请选择性别"
                          style="width: 100%"
                        >
                          <el-option label="男" value="男" />
                          <el-option label="女" value="女" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="身份证号" prop="idCard">
                        <el-input
                          v-model="formData.idCard"
                          placeholder="请输入身份证号"
                          clearable
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="出生日期" prop="birthday">
                        <el-date-picker
                          v-model="formData.birthday"
                          type="date"
                          placeholder="选择出生日期"
                          style="width: 100%"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="民族" prop="ethnicity">
                        <el-select
                          v-model="formData.ethnicity"
                          placeholder="请选择民族"
                          clearable
                          filterable
                          style="width: 100%"
                        >
                          <el-option
                            v-for="item in ethnicityOptions"
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
                      <el-form-item label="政治面貌" prop="politicalStatus">
                        <el-select
                          v-model="formData.politicalStatus"
                          placeholder="请选择政治面貌"
                          clearable
                          filterable
                          style="width: 100%"
                        >
                          <el-option
                            v-for="item in politicalStatusOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                          />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="账号状态" prop="isActive">
                        <el-switch
                          v-model="formData.isActive"
                          active-text="启用"
                          inactive-text="禁用"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                </div>
              </el-tab-pane>

              <el-tab-pane label="学业信息" name="academic">
                <div class="form-section">
                  <h3>学生学业信息</h3>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="所属院系" prop="departmentId">
                        <el-select
                          v-model="formData.departmentId"
                          placeholder="请选择院系"
                          clearable
                          filterable
                          style="width: 100%"
                          @change="handleDepartmentChange"
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
                    <el-col :span="12">
                      <el-form-item label="所属专业" prop="majorId">
                        <el-select
                          v-model="formData.majorId"
                          placeholder="请选择专业"
                          clearable
                          filterable
                          style="width: 100%"
                          @change="handleMajorChange"
                        >
                          <el-option
                            v-for="item in majorOptions"
                            :key="item.id || item.majorId"
                            :label="item.name || item.majorName"
                            :value="item.id || item.majorId"
                          />
                        </el-select>
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="所属班级" prop="classId">
                        <el-select
                          v-model="formData.classId"
                          placeholder="请选择班级"
                          clearable
                          filterable
                          style="width: 100%"
                        >
                          <el-option
                            v-for="item in classOptions"
                            :key="item.id || item.classId"
                            :label="item.name || item.className"
                            :value="item.id || item.classId"
                          />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="培养层次" prop="educationLevel">
                        <el-select
                          v-model="formData.educationLevel"
                          placeholder="请选择培养层次"
                          clearable
                          style="width: 100%"
                        >
                          <el-option label="本科" value="本科" />
                          <el-option label="硕士研究生" value="硕士研究生" />
                          <el-option label="博士研究生" value="博士研究生" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="入学日期" prop="enrollmentDate">
                        <el-date-picker
                          v-model="formData.enrollmentDate"
                          type="date"
                          placeholder="选择入学日期"
                          style="width: 100%"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="预计毕业日期" prop="expectedGraduation">
                        <el-date-picker
                          v-model="formData.expectedGraduation"
                          type="date"
                          placeholder="选择预计毕业日期"
                          style="width: 100%"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="学籍状态" prop="studentStatus">
                        <el-select
                          v-model="formData.studentStatus"
                          placeholder="请选择学籍状态"
                          clearable
                          style="width: 100%"
                        >
                          <el-option label="在读" value="在读" />
                          <el-option label="休学" value="休学" />
                          <el-option label="退学" value="退学" />
                          <el-option label="毕业" value="毕业" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </div>
              </el-tab-pane>

              <el-tab-pane label="联系方式" name="contact">
                <div class="form-section">
                  <h3>联系方式</h3>

                  <el-row :gutter="20">
                    <el-col :span="12">
                      <el-form-item label="联系电话" prop="phone">
                        <el-input
                          v-model="formData.phone"
                          placeholder="请输入联系电话"
                          clearable
                        />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="电子邮箱" prop="email">
                        <el-input
                          v-model="formData.email"
                          placeholder="请输入电子邮箱"
                          clearable
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row>
                    <el-col :span="24">
                      <el-form-item label="家庭住址" prop="address">
                        <el-input
                          v-model="formData.address"
                          placeholder="请输入家庭住址"
                          clearable
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>

                  <el-row>
                    <el-col :span="24">
                      <el-form-item label="备注" prop="notes">
                        <el-input
                          v-model="formData.notes"
                          type="textarea"
                          :rows="3"
                          placeholder="请输入备注信息"
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
          <div class="batch-import-section">
            <h3>批量导入学生信息</h3>
            <p class="import-tips">
              您可以下载模板文件，填写学生信息后上传，系统将批量创建学生账号。
            </p>

            <div class="import-actions">
              <el-button type="primary" plain @click="downloadTemplate">
                <el-icon><Download /></el-icon>
                下载模板
              </el-button>

              <el-upload
                class="import-upload"
                action="#"
                :auto-upload="false"
                :on-change="handleFileChange"
                :limit="1"
                accept=".xlsx,.xls"
              >
                <el-button type="primary">
                  <el-icon><Upload /></el-icon>
                  选择文件
                </el-button>
              </el-upload>

              <el-button
                type="success"
                @click="importStudents"
                :disabled="!importFile"
                :loading="importLoading"
              >
                <el-icon><Check /></el-icon>
                开始导入
              </el-button>
            </div>

            <div v-if="importResult" class="import-result">
              <h4>导入结果</h4>
              <el-alert
                :title="`总计 ${importResult.total} 条数据，成功 ${importResult.success} 条，失败 ${importResult.failed} 条`"
                :type="importResult.failed > 0 ? 'warning' : 'success'"
                show-icon
              />

              <el-collapse v-if="importResult.failed > 0">
                <el-collapse-item title="查看失败详情">
                  <el-table :data="importResult.errors" border stripe>
                    <el-table-column prop="studentId" label="学号" width="180" />
                    <el-table-column prop="reason" label="失败原因" />
                  </el-table>
                </el-collapse-item>
              </el-collapse>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { ArrowLeft, Download, Upload, Check } from '@element-plus/icons-vue'
import { addStudent, getStudentById, updateStudent, batchImportStudents } from '@/api/student'
import { getDepartments } from '@/api/department'
import { getMajorsByDepartment } from '@/api/major'
import { getClassesByMajorId } from '@/api/class'
import { ETHNICITY_OPTIONS, POLITICAL_STATUS_OPTIONS } from '@/constants/options'
import type { Department } from '@/types/department'
import type { Major } from '@/types/major'
import type { Class } from '@/types/class'

// 路由
const router = useRouter()
const route = useRoute()

// 标签页
const activeTab = ref('single')
const formTabActive = ref('basic')

// 加载状态
const loading = ref(false)
const importLoading = ref(false)

// 表单引用
const formRef = ref<FormInstance>()

// 批量导入相关
const importFile = ref<File | null>(null)
const importResult = ref<{
  total: number;
  success: number;
  failed: number;
  errors: Array<{studentId: string; reason: string}>;
} | null>(null)

// 页面模式（创建/编辑）
const isEdit = computed(() => !!route.params.id)
const studentId = computed(() => route.params.id as string)

// 选项数据
const departmentOptions = ref<Department[]>([])
const majorOptions = ref<Major[]>([])
const classOptions = ref<Class[]>([])
const ethnicityOptions = ref(ETHNICITY_OPTIONS)
const politicalStatusOptions = ref(POLITICAL_STATUS_OPTIONS)

// 表单数据
const formData = reactive({
  id: '',
  name: '',
  gender: '',
  idCard: '',
  birthday: null,
  ethnicity: '',
  politicalStatus: '',
  classId: '',
  majorId: '',
  departmentId: '',
  educationLevel: '',
  enrollmentDate: null,
  expectedGraduation: null,
  studentStatus: '在读',
  isActive: true,
  phone: '',
  email: '',
  address: '',
  notes: ''
})

// 表单验证规则
const formRules = reactive<FormRules>({
  id: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^\d{14}$/, message: '学号必须是14位数字（年份4位+部门号4位+专业号2位+班级号2位+学生编号2位）', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度应为2-50个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号码', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ],
  majorId: [
    { required: true, message: '请选择专业', trigger: 'change' }
  ],
  classId: [
    { required: true, message: '请输入班级', trigger: 'blur' }
  ],
  educationLevel: [
    { required: true, message: '请选择培养层次', trigger: 'change' }
  ],
  enrollmentDate: [
    { required: true, message: '请选择入学日期', trigger: 'change' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
})

// 加载院系列表
const loadDepartments = async () => {
  try {
    const res = await getDepartments()
    if (res.success && res.data) {
      departmentOptions.value = res.data
    }
  } catch (error) {
    console.error('获取院系列表失败:', error)
    ElMessage.error('获取院系列表失败')
  }
}

// 根据院系加载专业列表
const loadMajors = async (departmentId: string) => {
  if (!departmentId) {
    majorOptions.value = []
    formData.majorId = ''
    classOptions.value = []
    formData.classId = ''
    return
  }

  try {
    const res = await getMajorsByDepartment(departmentId)
    if (res.success && res.data) {
      majorOptions.value = res.data
    }
  } catch (error) {
    console.error('获取专业列表失败:', error)
    ElMessage.error('获取专业列表失败')
  }
}

// 根据专业加载班级列表
const loadClasses = async (majorId: string) => {
  if (!majorId) {
    classOptions.value = []
    formData.classId = ''
    return
  }

  try {
    console.log('开始获取班级列表，专业ID:', majorId)
    const res = await getClassesByMajorId(majorId)
    console.log('获取班级列表响应:', res)

    if (res.success && res.data) {
      // 处理数据，确保有统一的id和name字段
      classOptions.value = res.data.map(clazz => ({
        ...clazz,
        id: clazz.id || clazz.classId,
        name: clazz.name || clazz.className
      }))
      console.log('处理后的班级选项:', classOptions.value)
    }
  } catch (error) {
    console.error('获取班级列表失败:', error)
    ElMessage.error('获取班级列表失败')
  }
}

// 院系变更处理
const handleDepartmentChange = (value: string) => {
  formData.majorId = ''
  formData.classId = ''
  classOptions.value = []
  loadMajors(value)
}

// 专业变更处理
const handleMajorChange = (value: string) => {
  formData.classId = ''
  loadClasses(value)
}

// 监听专业变化
watch(() => formData.majorId, (newVal) => {
  if (newVal) {
    console.log('专业变化，新值：', newVal)
    loadClasses(newVal)
  } else {
    classOptions.value = []
    formData.classId = ''
  }
}, { immediate: true })

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  formTabActive.value = 'basic'
}

// 加载学生详情
const loadStudentDetail = async () => {
  if (!isEdit.value) return

  try {
    loading.value = true
    const response = await getStudentById(studentId.value)

    if (response.success && response.data) {
      // 加载专业列表
      await loadMajors(response.data.departmentId)

      // 填充表单数据
      Object.keys(formData).forEach(key => {
        if (key in response.data) {
          // @ts-ignore
          formData[key] = response.data[key]
        }
      })
    } else {
      ElMessage.error(response.message || '获取学生详情失败')
      router.push('/account/student-list')
    }
  } catch (error) {
    console.error('获取学生详情失败:', error)
    ElMessage.error('获取学生详情失败，请稍后重试')
    router.push('/account/student-list')
  } finally {
    loading.value = false
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        if (isEdit.value) {
          // 编辑模式
          const response = await updateStudent(studentId.value, formData)
          if (response.success) {
            ElMessage.success('更新学生信息成功')
            router.push('account/student-list') // 修改路径
          } else {
            ElMessage.error(response.message || '更新学生信息失败')
          }
        } else {
          // 创建模式
          const response = await addStudent(formData)
          if (response.success) {
            ElMessage.success('创建学生账号成功')
            router.push('account/student-list') // 修改路径
          } else {
            ElMessage.error(response.message || '创建学生账号失败')
          }
        }
      } catch (error) {
        console.error('提交表单失败:', error)
        ElMessage.error('操作失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

// 取消编辑
const handleCancel = () => {
  router.push('account/student-list') // 修改路径
}

// 处理文件选择
const handleFileChange = (file: any) => {
  importFile.value = file.raw
}

// 下载模板
const downloadTemplate = () => {
  // 实际应用中，这里应该是一个真实的模板下载链接
  const templateUrl = '/api/admin/students/template'
  window.open(templateUrl, '_blank')
}

// 批量导入学生
const importStudents = async () => {
  if (!importFile.value) {
    ElMessage.warning('请先选择要导入的Excel文件')
    return
  }

  importLoading.value = true
  importResult.value = null

  try {
    const formData = new FormData()
    formData.append('file', importFile.value)

    const response = await batchImportStudents(formData)

    if (response.success) {
      importResult.value = {
        total: response.totalCount || 0,
        success: response.successCount || 0,
        failed: (response.totalCount || 0) - (response.successCount || 0),
        errors: response.errors || []
      }

      if (importResult.value.failed === 0) {
        ElMessage.success(`成功导入 ${importResult.value.success} 条学生信息`)
      } else {
        ElMessage.warning(`导入完成，成功 ${importResult.value.success} 条，失败 ${importResult.value.failed} 条，请查看详情`)
      }
    } else {
      ElMessage.error(response.message || '批量导入失败')
    }
  } catch (error) {
    console.error('批量导入失败:', error)
    ElMessage.error('批量导入失败，请稍后重试')
  } finally {
    importLoading.value = false
  }
}

// 组件挂载时执行
onMounted(async () => {
  console.log('组件挂载，开始加载数据...')
  await loadDepartments()

  if (isEdit.value) {
    console.log('编辑模式，加载学生详情...')
    await loadStudentDetail()
  } else if (formData.majorId) {
    // 如果有专业ID但没有班级列表，加载班级列表
    console.log('初始化专业已选择，加载班级列表...')
    loadClasses(formData.majorId)
  }

  console.log('组件挂载完成，当前表单数据:', formData)
})
</script>

<style scoped>
.create-account-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.form-card {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.form-section {
  margin-bottom: 20px;
}

.form-section h3 {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.form-actions {
  margin-top: 30px;
  text-align: right;
}

.import-tips {
  margin-bottom: 20px;
  color: #666;
}

.import-actions {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 30px;
}

.import-result {
  margin-top: 30px;
}

.import-result h4 {
  margin-bottom: 15px;
}
</style>

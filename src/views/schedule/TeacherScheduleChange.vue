<template>
  <div class="schedule-change-container">
    <div class="schedule-change-header">
      <h2 class="title">调课申请</h2>
    </div>

    <!-- 操作栏 -->
    <div class="operation-section">
      <el-button type="primary" @click="handleCreateRequest">
        <el-icon><Plus /></el-icon>
        新建调课申请
      </el-button>
    </div>

    <!-- 查询栏 -->
    <div class="filter-section">
      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="课程">
          <el-select 
            v-model="queryForm.courseId" 
            placeholder="请选择课程"
            clearable
            filterable
            style="width: 220px"
          >
            <el-option
              v-for="item in courseOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select 
            v-model="queryForm.status" 
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 申请列表 -->
    <div class="request-list">
      <el-table
        v-loading="loading"
        :data="requestList"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column label="原课程时间" min-width="180">
          <template #default="scope">
            {{ scope.row.originalDate }} 第{{ scope.row.originalSection }}节
          </template>
        </el-table-column>
        <el-table-column label="调整后时间" min-width="180">
          <template #default="scope">
            {{ scope.row.targetDate }} 第{{ scope.row.targetSection }}节
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="申请原因" min-width="150" show-overflow-tooltip />
        <el-table-column prop="applyTime" label="申请时间" min-width="150" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleViewDetails(scope.row)"
            >
              查看
            </el-button>
            <el-button
              v-if="scope.row.status === 'PENDING'"
              link
              type="danger"
              size="small"
              @click="handleCancel(scope.row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 申请详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="调课申请详情"
      width="600px"
    >
      <template v-if="selectedRequest">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="课程名称">{{ selectedRequest.courseName }}</el-descriptions-item>
          <el-descriptions-item label="班级" v-if="selectedRequest.className">{{ selectedRequest.className }}</el-descriptions-item>
          <el-descriptions-item label="原课程时间">
            {{ selectedRequest.originalDate }} 第{{ selectedRequest.originalSection }}节
          </el-descriptions-item>
          <el-descriptions-item label="调整后时间">
            {{ selectedRequest.targetDate }} 第{{ selectedRequest.targetSection }}节
          </el-descriptions-item>
          <el-descriptions-item label="申请原因">{{ selectedRequest.reason }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ selectedRequest.applyTime }}</el-descriptions-item>
          <el-descriptions-item label="申请状态">
            <el-tag :type="getStatusType(selectedRequest.status)">
              {{ getStatusText(selectedRequest.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedRequest.reviewTime" label="审核时间">{{ selectedRequest.reviewTime }}</el-descriptions-item>
          <el-descriptions-item v-if="selectedRequest.reviewerName" label="审核人">{{ selectedRequest.reviewerName }}</el-descriptions-item>
          <el-descriptions-item v-if="selectedRequest.rejectReason && selectedRequest.status === 'REJECTED'" label="拒绝原因">
            {{ selectedRequest.rejectReason }}
          </el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>

    <!-- 新建申请对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="新建调课申请"
      width="600px"
    >
      <el-form
        ref="scheduleChangeFormRef"
        :model="scheduleChangeForm"
        :rules="formRules"
        label-width="120px"
        status-icon
      >
        <el-form-item label="课程" prop="courseId">
          <el-select 
            v-model="scheduleChangeForm.courseId" 
            placeholder="请选择课程"
            filterable
            style="width: 100%"
            @change="handleCourseChange"
          >
            <el-option
              v-for="item in courseOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级" prop="classId" v-if="classOptions.length > 0">
          <el-select 
            v-model="scheduleChangeForm.classId" 
            placeholder="请选择班级"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in classOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="原课程日期" prop="originalDate">
          <el-date-picker
            v-model="scheduleChangeForm.originalDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="原课程节数" prop="originalSection">
          <el-select
            v-model="scheduleChangeForm.originalSection"
            placeholder="请选择节数"
            style="width: 100%"
          >
            <el-option
              v-for="item in sectionOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="调整后日期" prop="targetDate">
          <el-date-picker
            v-model="scheduleChangeForm.targetDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="调整后节数" prop="targetSection">
          <el-select
            v-model="scheduleChangeForm.targetSection"
            placeholder="请选择节数"
            style="width: 100%"
          >
            <el-option
              v-for="item in sectionOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="申请原因" prop="reason">
          <el-input
            v-model="scheduleChangeForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入调课原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitScheduleChangeRequest" :loading="submitting">
            提交申请
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/counter'
import { 
  getScheduleChangeList, 
  getScheduleChangeDetail, 
  submitScheduleChange, 
  cancelScheduleChange,
  getTeacherCourses,
  getCourseClasses
} from '@/api/schedule'
import type { 
  ScheduleChangeRequest, 
  ScheduleChangeQueryParams,
  ScheduleChangeForm
} from '@/types/schedule'

// 用户信息
const userStore = useUserStore()

// 查询参数
const queryForm = reactive<ScheduleChangeQueryParams>({
  teacherId: userStore.userInfo?.userId,
  courseId: '',
  status: undefined,
  page: 1,
  pageSize: 10
})

// 日期范围
const dateRange = ref<[string, string] | null>(null)

// 状态选项
const statusOptions = [
  { label: '待审核', value: 'PENDING' },
  { label: '已通过', value: 'APPROVED' },
  { label: '已拒绝', value: 'REJECTED' }
]

// 获取状态文本
const getStatusText = (status: string) => {
  switch (status) {
    case 'PENDING': return '待审核'
    case 'APPROVED': return '已通过'
    case 'REJECTED': return '已拒绝'
    default: return '未知'
  }
}

// 获取状态类型
const getStatusType = (status: string) => {
  switch (status) {
    case 'PENDING': return 'warning'
    case 'APPROVED': return 'success'
    case 'REJECTED': return 'danger'
    default: return 'info'
  }
}

// 列表数据
const loading = ref(false)
const requestList = ref<ScheduleChangeRequest[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 课程选项
const courseOptions = ref<Array<{ id: string; name: string }>>([])

// 班级选项
const classOptions = ref<Array<{ id: string; name: string }>>([])

// 详情对话框
const detailDialogVisible = ref(false)
const selectedRequest = ref<ScheduleChangeRequest | null>(null)

// 新建申请对话框
const createDialogVisible = ref(false)
const scheduleChangeFormRef = ref<FormInstance>()
const submitting = ref(false)

// 节数选项
const sectionOptions = [
  { label: '第1节 (8:00-9:40)', value: 1 },
  { label: '第2节 (10:00-11:40)', value: 2 },
  { label: '第3节 (13:30-15:10)', value: 3 },
  { label: '第4节 (15:30-17:10)', value: 4 },
  { label: '第5节 (18:30-20:10)', value: 5 }
]

// 调课申请表单
const scheduleChangeForm = reactive<ScheduleChangeForm>({
  courseId: '',
  classId: undefined,
  originalDate: '',
  originalSection: 0,
  targetDate: '',
  targetSection: 0,
  reason: ''
})

// 表单验证规则
const formRules = {
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  originalDate: [
    { required: true, message: '请选择原课程日期', trigger: 'change' }
  ],
  originalSection: [
    { required: true, message: '请选择原课程节数', trigger: 'change' },
    { type: 'number', min: 1, message: '请选择有效的节数', trigger: 'change' }
  ],
  targetDate: [
    { required: true, message: '请选择调整后日期', trigger: 'change' }
  ],
  targetSection: [
    { required: true, message: '请选择调整后节数', trigger: 'change' },
    { type: 'number', min: 1, message: '请选择有效的节数', trigger: 'change' }
  ],
  reason: [
    { required: true, message: '请输入申请原因', trigger: 'blur' },
    { min: 5, max: 200, message: '原因长度在5到200个字符之间', trigger: 'blur' }
  ]
}

// 监听日期范围变化
watch(dateRange, (newVal) => {
  if (newVal) {
    queryForm.startDate = newVal[0]
    queryForm.endDate = newVal[1]
  } else {
    queryForm.startDate = undefined
    queryForm.endDate = undefined
  }
})

// 加载调课申请列表
const loadRequestList = async () => {
  try {
    loading.value = true
    const params: ScheduleChangeQueryParams = {
      ...queryForm,
      page: currentPage.value,
      pageSize: pageSize.value
    }
    
    const response = await getScheduleChangeList(params)
    
    if (response.success) {
      requestList.value = response.data
      total.value = response.total
    } else {
      ElMessage.error(response.message || '获取调课申请列表失败')
    }
  } catch (error) {
    console.error('加载调课申请列表失败:', error)
    ElMessage.error('加载调课申请列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 加载课程选项
const loadCourseOptions = async () => {
  try {
    const response = await getTeacherCourses()
    
    if (response.success) {
      courseOptions.value = response.data
    } else {
      ElMessage.warning(response.message || '获取课程列表失败')
    }
  } catch (error) {
    console.error('加载课程选项失败:', error)
    ElMessage.warning('获取课程列表失败，请稍后重试')
  }
}

// 处理课程变化
const handleCourseChange = async (courseId: string) => {
  if (!courseId) {
    classOptions.value = []
    scheduleChangeForm.classId = undefined
    return
  }
  
  try {
    const response = await getCourseClasses(courseId)
    
    if (response.success) {
      classOptions.value = response.data
      if (response.data.length > 0) {
        scheduleChangeForm.classId = response.data[0].id
      }
    } else {
      ElMessage.warning(response.message || '获取班级列表失败')
      classOptions.value = []
    }
  } catch (error) {
    console.error('加载班级选项失败:', error)
    ElMessage.warning('获取班级列表失败，请稍后重试')
    classOptions.value = []
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  loadRequestList()
}

// 重置查询条件
const resetQuery = () => {
  queryForm.courseId = ''
  queryForm.status = undefined
  dateRange.value = null
  currentPage.value = 1
  loadRequestList()
}

// 处理页码变化
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadRequestList()
}

// 处理每页条数变化
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadRequestList()
}

// 查看申请详情
const handleViewDetails = async (request: ScheduleChangeRequest) => {
  try {
    const response = await getScheduleChangeDetail(request.id)
    
    if (response.success && response.data) {
      selectedRequest.value = response.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(response.message || '获取调课申请详情失败')
    }
  } catch (error) {
    console.error('获取调课申请详情失败:', error)
    ElMessage.error('获取调课申请详情失败，请稍后重试')
  }
}

// 取消申请
const handleCancel = (request: ScheduleChangeRequest) => {
  ElMessageBox.confirm(
    '确定要取消该调课申请吗？',
    '取消确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await cancelScheduleChange(request.id)
      
      if (response.success) {
        ElMessage.success('取消成功')
        loadRequestList()
      } else {
        ElMessage.error(response.message || '取消失败')
      }
    } catch (error) {
      console.error('取消调课申请失败:', error)
      ElMessage.error('取消调课申请失败，请稍后重试')
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 创建调课申请
const handleCreateRequest = async () => {
  // 重置表单
  scheduleChangeForm.courseId = ''
  scheduleChangeForm.classId = undefined
  scheduleChangeForm.originalDate = ''
  scheduleChangeForm.originalSection = 0
  scheduleChangeForm.targetDate = ''
  scheduleChangeForm.targetSection = 0
  scheduleChangeForm.reason = ''
  
  // 加载课程选项
  await loadCourseOptions()
  
  // 打开对话框
  createDialogVisible.value = true
}

// 提交调课申请
const submitScheduleChangeRequest = async () => {
  if (!scheduleChangeFormRef.value) return
  
  await scheduleChangeFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      submitting.value = true
      
      // 检查节数是否有效
      if (scheduleChangeForm.originalSection <= 0) {
        ElMessage.error('请选择有效的原课程节数')
        submitting.value = false
        return
      }
      
      if (scheduleChangeForm.targetSection <= 0) {
        ElMessage.error('请选择有效的调整后节数')
        submitting.value = false
        return
      }
      
      const response = await submitScheduleChange(scheduleChangeForm)
      
      if (response.success) {
        ElMessage.success('调课申请提交成功')
        createDialogVisible.value = false
        loadRequestList()
      } else {
        ElMessage.error(response.message || '提交失败')
      }
    } catch (error) {
      console.error('提交调课申请失败:', error)
      ElMessage.error('提交调课申请失败，请稍后重试')
    } finally {
      submitting.value = false
    }
  })
}

// 初始化
onMounted(() => {
  loadRequestList()
})
</script>

<style scoped>
.schedule-change-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.schedule-change-header {
  background-color: #fff;
  padding: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  margin-bottom: 16px;
}

.title {
  margin: 0;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
  position: relative;
  padding-left: 12px;
  line-height: 1;
  border-left: 4px solid #409eff;
}

.operation-section {
  background-color: #fff;
  padding: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-end;
}

.filter-section {
  background-color: #fff;
  padding: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  margin-bottom: 16px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.request-list {
  flex: 1;
  background-color: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.text-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.time-separator {
  color: #606266;
}

/* 移动设备适配 */
@media (max-width: 768px) {
  .filter-form {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .filter-form :deep(.el-form-item) {
    margin-right: 0;
    margin-bottom: 12px;
    width: 100%;
  }
  
  .filter-form :deep(.el-select),
  .filter-form :deep(.el-input),
  .filter-form :deep(.el-date-editor) {
    width: 100% !important;
  }
}
</style> 
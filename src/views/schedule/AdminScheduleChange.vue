<template>
  <div class="schedule-change-container">
    <div class="schedule-change-header">
      <h2 class="title">调课申请管理</h2>
    </div>

    <!-- 查询栏 -->
    <div class="filter-section">
      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="教师">
          <el-input
            v-model="queryForm.teacherName"
            placeholder="请输入教师姓名"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="课程">
          <el-input
            v-model="queryForm.courseName"
            placeholder="请输入课程名称"
            clearable
            style="width: 180px"
          />
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
        <el-table-column prop="teacherName" label="申请教师" min-width="100" />
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="className" label="班级" min-width="120" show-overflow-tooltip />
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
              type="success"
              size="small"
              @click="handleReview(scope.row, 'APPROVED')"
            >
              通过
            </el-button>
            <el-button
              v-if="scope.row.status === 'PENDING'"
              link
              type="danger"
              size="small"
              @click="handleReview(scope.row, 'REJECTED')"
            >
              拒绝
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
          <el-descriptions-item label="申请教师">{{ selectedRequest.teacherName }}</el-descriptions-item>
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

        <div v-if="selectedRequest.status === 'PENDING'" class="action-buttons">
          <el-button type="success" @click="handleReview(selectedRequest, 'APPROVED')">
            通过申请
          </el-button>
          <el-button type="danger" @click="handleReview(selectedRequest, 'REJECTED')">
            拒绝申请
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 拒绝原因对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="填写拒绝原因"
      width="500px"
    >
      <el-form
        ref="rejectFormRef"
        :model="rejectForm"
        :rules="rejectRules"
        label-width="100px"
      >
        <el-form-item label="拒绝原因" prop="reason">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝原因（将通知给申请教师）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReject" :loading="submitting">
            确认拒绝
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/counter'
import {
  getScheduleChangeList,
  getScheduleChangeDetail,
  reviewScheduleChange
} from '@/api/schedule'
import type {
  ScheduleChangeRequest,
  ScheduleChangeQueryParams,
  ScheduleChangeReviewParams
} from '@/types/schedule'

// 用户信息
const userStore = useUserStore()

// 查询表单
const queryForm = reactive({
  teacherName: '',
  courseName: '',
  status: undefined as 'PENDING' | 'APPROVED' | 'REJECTED' | undefined,
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

// 详情对话框
const detailDialogVisible = ref(false)
const selectedRequest = ref<ScheduleChangeRequest | null>(null)

// 拒绝对话框
const rejectDialogVisible = ref(false)
const rejectFormRef = ref<FormInstance>()
const submitting = ref(false)
const rejectForm = reactive({
  requestId: '',
  reason: ''
})

// 拒绝表单验证规则
const rejectRules = {
  reason: [
    { required: true, message: '请输入拒绝原因', trigger: 'blur' },
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

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  loadRequestList()
}

// 重置查询条件
const resetQuery = () => {
  queryForm.teacherName = ''
  queryForm.courseName = ''
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

// 处理审核
const handleReview = (request: ScheduleChangeRequest, status: 'APPROVED' | 'REJECTED') => {
  if (status === 'APPROVED') {
    // 直接通过申请
    ElMessageBox.confirm(
      `确定通过该调课申请吗？`,
      '审核确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    ).then(async () => {
      try {
        await submitReviewResult(request.id, status)
      } catch (error) {
        console.error('审核调课申请失败:', error)
        ElMessage.error('审核调课申请失败，请稍后重试')
      }
    }).catch(() => {
      // 用户取消操作
    })
  } else {
    // 打开拒绝原因对话框
    rejectForm.requestId = request.id
    rejectForm.reason = ''
    rejectDialogVisible.value = true
  }
}

// 提交拒绝
const submitReject = async () => {
  if (!rejectFormRef.value) return

  await rejectFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      submitting.value = true
      await submitReviewResult(rejectForm.requestId, 'REJECTED', rejectForm.reason)
      rejectDialogVisible.value = false
    } catch (error) {
      console.error('拒绝调课申请失败:', error)
      ElMessage.error('拒绝调课申请失败，请稍后重试')
    } finally {
      submitting.value = false
    }
  })
}

// 提交审核结果
const submitReviewResult = async (requestId: string, status: 'APPROVED' | 'REJECTED', rejectReason?: string) => {
  try {
    const params: ScheduleChangeReviewParams = {
      id: requestId,
      status,
      rejectReason
    }

    const response = await reviewScheduleChange(params)

    if (response.success) {
      ElMessage.success(status === 'APPROVED' ? '已通过该调课申请' : '已拒绝该调课申请')
      loadRequestList()
      detailDialogVisible.value = false
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('提交审核结果失败:', error)
    throw error
  }
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

.action-buttons {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
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

  .action-buttons {
    flex-direction: column;
    gap: 10px;
  }
}
</style>

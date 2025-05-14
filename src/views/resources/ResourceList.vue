<template>
  <div class="resource-container">
    <div class="resource-header">
      <h2 class="title">课程资源管理</h2>
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
          <el-form-item label="资源类型">
            <el-select 
              v-model="queryForm.resourceType" 
              placeholder="请选择类型"
              clearable
              style="width: 150px"
            >
              <el-option
                v-for="item in resourceTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input
              v-model="queryForm.keyword"
              placeholder="输入标题或描述关键词"
              clearable
              style="width: 200px"
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
    </div>

    <div class="resource-content">
      <div class="operation-bar">
        <el-button type="primary" @click="handleUpload">
          <el-icon><Upload /></el-icon>
          上传资源
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="resourceList"
        border
        style="width: 100%"
        stripe
      >
        <el-table-column label="资源标题" min-width="180">
          <template #default="scope">
            <div class="resource-title">
              <el-icon :class="getResourceTypeIcon(scope.row.resourceType)">
                <Document v-if="scope.row.resourceType === 'DOCUMENT'" />
                <VideoPlay v-else-if="scope.row.resourceType === 'VIDEO'" />
                <Picture v-else-if="scope.row.resourceType === 'IMAGE'" />
                <Headset v-else-if="scope.row.resourceType === 'AUDIO'" />
                <Files v-else />
              </el-icon>
              <span>{{ scope.row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="fileName" label="文件名" min-width="180" />
        <el-table-column prop="courseName" label="所属课程" min-width="150" />
        <el-table-column label="文件大小" width="120">
          <template #default="scope">
            {{ formatFileSize(scope.row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="uploaderName" label="上传者" width="120" />
        <el-table-column label="上传时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.uploadTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleDownload(scope.row)"
            >
              下载
            </el-button>
            <el-button
              link
              type="primary"
              size="small"
              @click="handleViewDetails(scope.row)"
            >
              详情
            </el-button>
            <el-button
              v-if="canDelete(scope.row)"
              link
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
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

    <!-- 资源详情对话框 -->
    <el-dialog
      v-model="detailsDialogVisible"
      title="资源详情"
      width="600px"
    >
      <template v-if="selectedResource">
        <div class="resource-details">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="资源标题">{{ selectedResource.title }}</el-descriptions-item>
            <el-descriptions-item label="所属课程">{{ selectedResource.courseName }}</el-descriptions-item>
            <el-descriptions-item label="资源描述">{{ selectedResource.description || '暂无描述' }}</el-descriptions-item>
            <el-descriptions-item label="文件名">{{ selectedResource.fileName }}</el-descriptions-item>
            <el-descriptions-item label="文件类型">{{ selectedResource.fileType }}</el-descriptions-item>
            <el-descriptions-item label="文件大小">{{ formatFileSize(selectedResource.fileSize) }}</el-descriptions-item>
            <el-descriptions-item label="上传者">{{ selectedResource.uploaderName }}</el-descriptions-item>
            <el-descriptions-item label="上传时间">{{ formatDate(selectedResource.uploadTime) }}</el-descriptions-item>
          </el-descriptions>

          <div class="resource-actions">
            <el-button type="primary" @click="handleDownload(selectedResource)">
              <el-icon><Download /></el-icon>
              下载资源
            </el-button>
            <el-button v-if="canDelete(selectedResource)" type="danger" @click="handleDelete(selectedResource)">
              <el-icon><Delete /></el-icon>
              删除资源
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>

    <!-- 上传资源对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传课程资源"
      width="600px"
    >
      <el-form
        ref="uploadFormRef"
        :model="uploadForm"
        :rules="uploadRules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="所属课程" prop="courseId">
          <el-select 
            v-model="uploadForm.courseId" 
            placeholder="请选择课程"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in courseOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="资源标题" prop="title">
          <el-input v-model="uploadForm.title" placeholder="请输入资源标题" />
        </el-form-item>
        <el-form-item label="资源描述" prop="description">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资源描述（选填）"
          />
        </el-form-item>
        <el-form-item label="资源类型" prop="resourceType">
          <el-select 
            v-model="uploadForm.resourceType" 
            placeholder="请选择资源类型"
            style="width: 100%"
          >
            <el-option
              v-for="item in resourceTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上传文件" prop="file">
          <el-upload
            ref="uploadRef"
            class="resource-upload"
            action="#"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-exceed="handleExceed"
            :file-list="fileList"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            drag
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                请上传文件，大小不超过100MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload" :loading="uploading">
            开始上传
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type UploadInstance, type FormInstance } from 'element-plus'
import { Search, Refresh, Upload, Document, VideoPlay, Picture, Headset, Files, Download, Delete, UploadFilled } from '@element-plus/icons-vue'
import { getCourseResources, uploadCourseResource, deleteCourseResource, getUserCourses, type CourseResource, type ResourceListParams } from '@/api/resources'
import { useUserStore } from '@/stores/counter'

// 用户信息
const userStore = useUserStore()

// 资源列表数据
const loading = ref(false)
const resourceList = ref<CourseResource[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 查询表单
const queryForm = reactive<ResourceListParams>({
  courseId: '',
  resourceType: '',
  keyword: '',
  page: 1,
  pageSize: 10
})

// 课程选项
const courseOptions = ref<Array<{ id: string; name: string }>>([])

// 资源类型选项
const resourceTypeOptions = [
  { label: '文档', value: 'DOCUMENT' },
  { label: '视频', value: 'VIDEO' },
  { label: '图片', value: 'IMAGE' },
  { label: '音频', value: 'AUDIO' },
  { label: '其他', value: 'OTHER' }
]

// 资源详情对话框
const detailsDialogVisible = ref(false)
const selectedResource = ref<CourseResource | null>(null)

// 上传资源对话框
const uploadDialogVisible = ref(false)
const uploadFormRef = ref<FormInstance>()
const uploadRef = ref<UploadInstance>()
const uploading = ref(false)
const fileList = ref<any[]>([])

// 上传表单
const uploadForm = reactive({
  courseId: '',
  title: '',
  description: '',
  resourceType: 'DOCUMENT',
  file: null as File | null
})

// 上传表单验证规则
const uploadRules = {
  courseId: [
    { required: true, message: '请选择所属课程', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入资源标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度在2到50个字符之间', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
  ],
  resourceType: [
    { required: true, message: '请选择资源类型', trigger: 'change' }
  ],
  file: [
    { required: true, message: '请选择要上传的文件', trigger: 'change' }
  ]
}

// 检查是否有删除权限
const canDelete = (resource: CourseResource) => {
  // 管理员可以删除任何资源
  if (userStore.isAdmin) {
    return true
  }
  // 教师只能删除自己上传的资源
  if (userStore.isTeacher) {
    return resource.uploaderId === userStore.userInfo?.userId
  }
  return false
}

// 根据资源类型获取图标类名
const getResourceTypeIcon = (type: string) => {
  const iconMap: Record<string, string> = {
    'DOCUMENT': 'document-icon',
    'VIDEO': 'video-icon',
    'IMAGE': 'image-icon',
    'AUDIO': 'audio-icon',
    'OTHER': 'other-icon'
  }
  return iconMap[type] || 'other-icon'
}

// 格式化文件大小
const formatFileSize = (size: number) => {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB'
  } else if (size < 1024 * 1024 * 1024) {
    return (size / (1024 * 1024)).toFixed(2) + ' MB'
  } else {
    return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
  }
}

// 格式化日期
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载资源列表
const loadResourceList = async () => {
  try {
    loading.value = true
    const params: ResourceListParams = {
      ...queryForm,
      page: currentPage.value,
      pageSize: pageSize.value
    }
    
    const response = await getCourseResources(params)
    
    if (response.success) {
      resourceList.value = response.data
      total.value = response.total
    } else {
      ElMessage.error(response.message || '获取资源列表失败')
    }
  } catch (error) {
    console.error('加载资源列表失败:', error)
    ElMessage.error('加载资源列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 加载课程选项
const loadCourseOptions = async () => {
  try {
    const response = await getUserCourses()
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

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  loadResourceList()
}

// 重置查询条件
const resetQuery = () => {
  queryForm.courseId = ''
  queryForm.resourceType = ''
  queryForm.keyword = ''
  currentPage.value = 1
  loadResourceList()
}

// 处理页码变化
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadResourceList()
}

// 处理每页条数变化
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadResourceList()
}

// 查看资源详情
const handleViewDetails = (resource: CourseResource) => {
  selectedResource.value = resource
  detailsDialogVisible.value = true
}

// 下载资源
const handleDownload = (resource: CourseResource) => {
  // 创建一个隐藏的a标签用于下载
  const link = document.createElement('a')
  link.href = resource.fileUrl
  link.download = resource.fileName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 删除资源
const handleDelete = (resource: CourseResource) => {
  ElMessageBox.confirm(
    `确定要删除资源"${resource.title}"吗？此操作不可恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await deleteCourseResource(resource.id)
      
      if (response.success) {
        ElMessage.success('删除成功')
        loadResourceList()
        if (detailsDialogVisible.value && selectedResource.value?.id === resource.id) {
          detailsDialogVisible.value = false
        }
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      console.error('删除资源失败:', error)
      ElMessage.error('删除资源失败，请稍后重试')
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 打开上传对话框
const handleUpload = async () => {
  // 重置上传表单
  uploadForm.courseId = ''
  uploadForm.title = ''
  uploadForm.description = ''
  uploadForm.resourceType = 'DOCUMENT'
  uploadForm.file = null
  fileList.value = []
  
  // 打开对话框
  uploadDialogVisible.value = true
  
  // 打开对话框后加载课程选项
  await loadCourseOptions()
  
  // 如果加载后没有课程选项，提示用户但不关闭对话框
  if (courseOptions.value.length === 0) {
    ElMessage.warning('没有可用的课程，请联系管理员添加课程')
  } else {
    // 默认选择第一个课程
    uploadForm.courseId = courseOptions.value[0]?.id || ''
  }
}

// 处理文件选择变化
const handleFileChange = (file: any) => {
  if (file.raw) {
    uploadForm.file = file.raw
    // 自动填充标题（如果未填写）
    if (!uploadForm.title && file.name) {
      // 去掉文件扩展名作为标题
      const titleWithoutExt = file.name.split('.').slice(0, -1).join('.')
      uploadForm.title = titleWithoutExt || file.name
    }
  }
}

// 上传前验证文件
const beforeUpload = (file: File) => {
  // 文件大小限制为100MB
  const maxSize = 100 * 1024 * 1024 // 100MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过100MB！')
    return false
  }
  return true
}

// 处理上传成功（虽然我们使用手动上传，但为完整起见添加此函数）
const handleUploadSuccess = () => {
  ElMessage.success('文件上传成功')
}

// 处理上传错误
const handleUploadError = () => {
  ElMessage.error('文件上传失败，请重试')
}

// 处理超出文件数量限制
const handleExceed = () => {
  ElMessage.warning('只能上传一个文件，请先删除已选择的文件')
}

// 提交上传
const submitUpload = async () => {
  if (!uploadFormRef.value) return
  
  await uploadFormRef.value.validate(async (valid) => {
    if (!valid || !uploadForm.file) {
      return
    }
    
    try {
      uploading.value = true
      
      const uploadParams = {
        courseId: uploadForm.courseId,
        title: uploadForm.title,
        description: uploadForm.description,
        resourceType: uploadForm.resourceType as 'DOCUMENT' | 'VIDEO' | 'IMAGE' | 'AUDIO' | 'OTHER',
        file: uploadForm.file
      }
      
      const response = await uploadCourseResource(uploadParams)
      
      if (response.success) {
        ElMessage.success('上传成功')
        uploadDialogVisible.value = false
        loadResourceList()
      } else {
        ElMessage.error(response.message || '上传失败')
      }
    } catch (error) {
      console.error('上传资源失败:', error)
      ElMessage.error('上传资源失败，请稍后重试')
    } finally {
      uploading.value = false
    }
  })
}

// 初始化
onMounted(() => {
  loadResourceList()
})
</script>

<style scoped>
.resource-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.resource-header {
  background-color: #fff;
  padding: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  margin-bottom: 16px;
}

.title {
  margin: 0 0 16px;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
  position: relative;
  padding-left: 12px;
  line-height: 1;
  border-left: 4px solid #409eff;
}

.filter-section {
  margin-top: 16px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
}

.resource-content {
  flex: 1;
  background-color: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.operation-bar {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-end;
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.resource-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.document-icon {
  color: #3963bc;
}

.video-icon {
  color: #e6a23c;
}

.image-icon {
  color: #67c23a;
}

.audio-icon {
  color: #f56c6c;
}

.other-icon {
  color: #909399;
}

.resource-details {
  margin: 0;
}

.resource-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.resource-upload {
  width: 100%;
}

.resource-upload :deep(.el-upload-dragger) {
  width: 100%;
  height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.resource-upload :deep(.el-icon--upload) {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.resource-upload :deep(.el-upload__text) {
  color: #606266;
  font-size: 14px;
  text-align: center;
}

.resource-upload :deep(.el-upload__text em) {
  color: #409eff;
  font-style: normal;
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
  .filter-form :deep(.el-input) {
    width: 100% !important;
  }
}
</style> 
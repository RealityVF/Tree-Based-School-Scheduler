<template>
  <div class="major-management-container">
    <div class="page-header">
      <h2>专业管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>新增专业
        </el-button>
        <el-button type="success" @click="importDialogVisible = true">
          <el-icon><Upload /></el-icon> 批量导入
        </el-button>
      </div>
    </div>

    <div class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="专业名称">
          <el-input v-model="searchForm.name" placeholder="输入专业名称" clearable />
        </el-form-item>
        <el-form-item label="院系">
          <el-select v-model="searchForm.department" placeholder="选择院系" clearable>
            <el-option
              v-for="item in departmentOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table
        v-loading="loading"
        :data="majorList"
        border
        style="width: 100%"
      >
        <el-table-column prop="duration" label="学制" width="100" />
        <el-table-column prop="department" label="所属院系" width="180" />
        <el-table-column prop="name" label="专业名称" width="200" />
        <el-table-column prop="code" label="专业编号" width="120" />
        <el-table-column prop="level" label="培养层次" width="120" />
        <el-table-column fixed="right" label="操作" width="180">
          <template #default="scope">
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
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 添加/编辑专业对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑专业' : '添加专业'"
      width="500px"
    >
      <el-form
        ref="majorFormRef"
        :model="majorForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="学制" prop="duration">
          <el-select v-model="majorForm.duration" placeholder="请选择学制">
            <el-option label="四年制" value="4" />
            <el-option label="五年制" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属院系" prop="department">
          <el-select v-model="majorForm.department" placeholder="请选择院系">
            <el-option
              v-for="item in departmentOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="专业名称" prop="name">
          <el-input v-model="majorForm.name" placeholder="请输入专业名称" />
        </el-form-item>
        <el-form-item label="专业编号" prop="code">
          <el-input v-model="majorForm.code" placeholder="请输入专业编号" />
        </el-form-item>
        <el-form-item label="培养层次" prop="level">
          <el-select v-model="majorForm.level" placeholder="请选择培养层次">
            <el-option label="本科" value="undergraduate" />
            <el-option label="硕士" value="master" />
            <el-option label="博士" value="doctor" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="批量导入专业"
      width="500px"
    >
      <div class="import-area">
        <el-upload
          class="upload-demo"
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :limit="1"
          accept=".xlsx,.xls,.csv"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              请上传Excel或CSV格式文件，文件大小不超过5MB
            </div>
          </template>
        </el-upload>

        <div class="template-download">
          <el-button type="text" size="small" @click="downloadTemplate">
            <el-icon><Download /></el-icon> 下载导入模板
          </el-button>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitImport" :loading="importLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, UploadFilled, Upload, Download } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import { getDepartments, getMajors } from '@/api/school'

interface OptionItem {
  id: string
  name: string
  value?: string
  label?: string
}

// 专业数据接口
interface Major {
  id: string
  duration: string      // 学制
  department: string    // 所属院系
  name: string         // 专业名称
  code: string         // 专业编号
  level: string        // 培养层次
}

// 院系选项
const departmentOptions = ref<{ value: string; label: string }[]>([])

// 专业数据
const loading = ref(false)
const majorList = ref<Major[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索表单
const searchForm = reactive({
  name: '',
  department: ''
})

// 专业表单
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const majorFormRef = ref<FormInstance>()
const majorForm = reactive({
  id: '',
  duration: '',
  department: '',
  name: '',
  code: '',
  level: ''
})

// 表单验证规则
const formRules = {
  duration: [
    { required: true, message: '请输入学制', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请选择所属院系', trigger: 'change' }
  ],
  name: [
    { required: true, message: '请输入专业名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入专业编号', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择培养层次', trigger: 'change' }
  ]
}

// 导入相关
const importDialogVisible = ref(false)
const importLoading = ref(false)
const importFile = ref<File | null>(null)

// 获取院系列表
const fetchDepartments = async () => {
  try {
    const response = await getDepartments()
    if (response.success) {
      departmentOptions.value = (response.data as OptionItem[]).map(item => ({
        value: item.id,
        label: item.name
      }))
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('获取院系列表失败:', error)
    ElMessage.error('获取院系列表失败，请稍后重试')
  }
}

// 获取专业列表
const fetchMajorList = async () => {
  loading.value = true
  try {
    const response = await getMajors()
    if (response.success) {
      // Transform the response data to match our Major interface
      majorList.value = []
      total.value = 0
    } else {
      ElMessage.error(response.message || '获取专业列表失败')
    }
  } catch (error) {
    console.error('获取专业列表失败:', error)
    ElMessage.error('获取专业列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 搜索专业
const handleSearch = () => {
  const filteredList = majorList.value.filter(item => {
    const nameMatch = !searchForm.name || item.name.includes(searchForm.name)
    const departmentMatch = !searchForm.department || item.department === searchForm.department
    return nameMatch && departmentMatch
  })
  majorList.value = filteredList
}

// 重置搜索
const handleReset = () => {
  searchForm.name = ''
  searchForm.department = ''
  fetchMajorList()
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  majorForm.id = ''
  majorForm.duration = ''
  majorForm.department = ''
  majorForm.name = ''
  majorForm.code = ''
  majorForm.level = ''
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row: Major) => {
  isEdit.value = true
  majorForm.id = row.id
  majorForm.duration = row.duration
  majorForm.department = row.department
  majorForm.name = row.name
  majorForm.code = row.code
  majorForm.level = row.level
  dialogVisible.value = true
}

// 处理删除
const handleDelete = async (row: Major) => {
  try {
    await ElMessageBox.confirm('确定要删除该专业吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // TODO: 待后端提供删除API后实现
    console.log('删除专业:', row.id)
    ElMessage.success('删除成功')
    await fetchMajorList()
  } catch (error) {
    console.error('删除专业失败:', error)
    ElMessage.error('删除失败')
  }
}

// 提交表单
const submitForm = async () => {
  if (!majorFormRef.value) return

  await majorFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        // TODO: 实现创建/更新API调用
        ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
        dialogVisible.value = false
        fetchMajorList()
      } catch (error) {
        console.error(isEdit.value ? '更新专业失败:' : '创建专业失败:', error)
        ElMessage.error(isEdit.value ? '更新失败，请稍后重试' : '创建失败，请稍后重试')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 处理文件变更
const handleFileChange = (file: { raw: File }) => {
  importFile.value = file.raw
}

// 下载导入模板
const downloadTemplate = () => {
  // TODO: 实现下载模板功能
  ElMessage.info('下载导入模板功能待实现')
}

// 提交导入
const submitImport = async () => {
  if (!importFile.value) {
    ElMessage.warning('请选择要导入的文件')
    return
  }

  importLoading.value = true
  try {
    // TODO: 调用导入API
    ElMessage.success('导入成功')
    importDialogVisible.value = false
    fetchMajorList()
  } catch (error) {
    console.error('导入失败:', error)
    ElMessage.error('导入失败，请稍后重试')
  } finally {
    importLoading.value = false
  }
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchMajorList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchMajorList()
}

// 组件挂载时初始化
onMounted(() => {
  fetchMajorList()
  fetchDepartments()
})
</script>

<style scoped>
.major-management-container {
  padding: 0;
  height: 100vh;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  background-color: #f5f7fa;
  overflow: hidden;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
  margin-bottom: 0;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-card {
  background-color: #fff;
  padding: 12px 24px;
  width: 100%;
  flex-shrink: 0;
  border-bottom: 1px solid #ebeef5;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  width: 100%;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.search-form :deep(.el-form-item__content) {
  margin-bottom: 0 !important;
}

.table-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  padding: 0;
  overflow: hidden;
  min-height: 0;
}

.table-card :deep(.el-table) {
  flex: 1;
  height: 100% !important;
}

.table-card :deep(.el-table__inner-wrapper) {
  height: 100%;
}

.table-card :deep(.el-table__body-wrapper) {
  height: calc(100% - 40px) !important;
  overflow-y: auto;
}

.table-card :deep(.el-table__header-wrapper) {
  background-color: #f5f7fa;
}

.table-card :deep(.el-table__header) th {
  background-color: #f5f7fa;
  height: 40px;
  padding: 8px 0;
}

.table-card :deep(.el-table__body) td {
  padding: 6px 0;
}

.pagination-container {
  padding: 12px 24px;
  background-color: #fff;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;
  flex-shrink: 0;
  margin-top: 0;
}

.template-download {
  margin-top: 10px;
  text-align: right;
}

.import-area {
  margin-bottom: 20px;
}
</style>

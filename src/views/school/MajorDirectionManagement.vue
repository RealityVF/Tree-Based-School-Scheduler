<template>
  <div class="major-direction-management-container">
    <div class="page-header">
      <h2>专业方向管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>新增专业方向
        </el-button>
        <el-button type="success" @click="importDialogVisible = true">
          <el-icon><Upload /></el-icon> 批量导入
        </el-button>
      </div>
    </div>

    <div class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="专业方向名称">
          <el-input v-model="searchForm.name" placeholder="输入专业方向名称" clearable />
        </el-form-item>
        <el-form-item label="所属专业">
          <el-select v-model="searchForm.major" placeholder="选择所属专业" clearable>
            <el-option
              v-for="item in majorOptions"
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
        :data="directionList"
        border
        style="width: 100%"
      >
        <el-table-column prop="code" label="专业方向编号" width="120" />
        <el-table-column prop="name" label="专业方向名称" width="150" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="department" label="院系" width="150" />
        <el-table-column prop="majorCode" label="专业编号" width="120" />
        <el-table-column prop="majorName" label="专业名称" width="150" />
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

    <!-- 添加/编辑专业方向对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑专业方向' : '添加专业方向'"
      width="500px"
    >
      <el-form
        ref="directionFormRef"
        :model="directionForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="专业方向编号" prop="code">
          <el-input v-model="directionForm.code" placeholder="请输入专业方向编号" />
        </el-form-item>
        <el-form-item label="专业方向名称" prop="name">
          <el-input v-model="directionForm.name" placeholder="请输入专业方向名称" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-input v-model="directionForm.grade" placeholder="请输入年级" />
        </el-form-item>
        <el-form-item label="院系" prop="department">
          <el-select v-model="directionForm.department" placeholder="请选择院系">
            <el-option
              v-for="item in departmentOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="专业编号" prop="majorCode">
          <el-input v-model="directionForm.majorCode" placeholder="请输入专业编号" />
        </el-form-item>
        <el-form-item label="专业名称" prop="majorName">
          <el-input v-model="directionForm.majorName" placeholder="请输入专业名称" />
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
      title="批量导入专业方向"
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
import { getMajors, getDepartments } from '@/api/school'

interface OptionItem {
  id: string
  name: string
  value?: string
  label?: string
}

// 专业方向数据接口
interface MajorDirection {
  id: string
  code: string          // 专业方向编号
  name: string          // 专业方向名称
  grade: string         // 年级
  department: string    // 院系
  majorCode: string     // 专业编号
  majorName: string     // 专业名称
}

// 院系选项
const departmentOptions = ref<{ value: string; label: string }[]>([])

// 专业选项
const majorOptions = ref<{ value: string; label: string }[]>([])

// 专业方向数据
const loading = ref(false)
const directionList = ref<MajorDirection[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索表单
const searchForm = reactive({
  name: '',
  major: ''
})

// 专业方向表单
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const directionFormRef = ref<FormInstance>()
const directionForm = reactive({
  id: '',
  code: '',
  name: '',
  grade: '',
  department: '',
  majorCode: '',
  majorName: ''
})

// 表单验证规则
const formRules = {
  code: [
    { required: true, message: '请输入专业方向编号', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入专业方向名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  grade: [
    { required: true, message: '请输入年级', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ],
  majorCode: [
    { required: true, message: '请输入专业编号', trigger: 'blur' }
  ],
  majorName: [
    { required: true, message: '请输入专业名称', trigger: 'blur' }
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
const fetchMajors = async () => {
  try {
    const response = await getMajors()
    if (response.success) {
      majorOptions.value = (response.data as OptionItem[]).map(item => ({
        value: item.id,
        label: item.name
      }))
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('获取专业列表失败:', error)
    ElMessage.error('获取专业列表失败，请稍后重试')
  }
}

// 获取专业方向列表
const fetchDirectionList = async () => {
  loading.value = true
  try {
    // Mock data for testing
    const mockData: MajorDirection[] = [
      {
        id: 'D001',
        code: 'CS001-1',
        name: '人工智能',
        grade: '2023',
        department: '计算机学院',
        majorCode: 'CS001',
        majorName: '计算机科学与技术'
      },
      {
        id: 'D002',
        code: 'CS001-2',
        name: '大数据',
        grade: '2023',
        department: '计算机学院',
        majorCode: 'CS001',
        majorName: '计算机科学与技术'
      }
    ]

    directionList.value = mockData
    total.value = mockData.length
  } catch (error) {
    console.error('获取专业方向列表失败:', error)
    ElMessage.error('获取专业方向列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 搜索专业方向
const handleSearch = () => {
  const filteredList = directionList.value.filter(item => {
    const nameMatch = !searchForm.name || item.name.includes(searchForm.name)
    const majorMatch = !searchForm.major || item.majorName === searchForm.major
    return nameMatch && majorMatch
  })
  directionList.value = filteredList
}

// 重置搜索
const handleReset = () => {
  searchForm.name = ''
  searchForm.major = ''
  fetchDirectionList()
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  directionForm.id = ''
  directionForm.code = ''
  directionForm.name = ''
  directionForm.grade = ''
  directionForm.department = ''
  directionForm.majorCode = ''
  directionForm.majorName = ''
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row: MajorDirection) => {
  isEdit.value = true
  directionForm.id = row.id
  directionForm.code = row.code
  directionForm.name = row.name
  directionForm.grade = row.grade
  directionForm.department = row.department
  directionForm.majorCode = row.majorCode
  directionForm.majorName = row.majorName
  dialogVisible.value = true
}

// 处理删除
const handleDelete = async (row: MajorDirection) => {
  try {
    await ElMessageBox.confirm('确定要删除该专业方向吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // TODO: 调用删除API
    ElMessage.success('删除成功')
    await fetchDirectionList()
  } catch (error) {
    console.error('删除专业方向失败:', error)
    ElMessage.error('删除失败')
  }
}

// 提交表单
const submitForm = async () => {
  if (!directionFormRef.value) return

  await directionFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        // TODO: 调用创建/更新API
        ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
        dialogVisible.value = false
        fetchDirectionList()
      } catch (error) {
        console.error(isEdit.value ? '更新专业方向失败:' : '创建专业方向失败:', error)
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
    fetchDirectionList()
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
  fetchDirectionList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchDirectionList()
}

// 组件挂载时初始化
onMounted(() => {
  fetchDirectionList()
  fetchMajors()
  fetchDepartments()
})
</script>

<style scoped>
.major-direction-management-container {
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

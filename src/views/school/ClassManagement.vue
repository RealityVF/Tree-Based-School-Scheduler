<template>
  <div class="class-management-container">
    <div class="page-header">
      <h2>班级管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">新增班级</el-button>
        <el-button @click="handleImport">导入</el-button>
        <el-button @click="handleExport">导出</el-button>
      </div>
    </div>

    <div class="search-card">
      <el-form :model="searchForm" class="search-form" inline>
        <el-form-item label="班级编号">
          <el-input v-model="searchForm.classCode" placeholder="请输入班级编号" clearable />
        </el-form-item>
        <el-form-item label="班级名称">
          <el-input v-model="searchForm.className" placeholder="请输入班级名称" clearable />
        </el-form-item>
        <el-form-item label="所属院系">
          <el-select v-model="searchForm.department" placeholder="请选择院系" clearable>
            <el-option v-for="dept in departmentOptions" :key="dept.value" :label="dept.label" :value="dept.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业">
          <el-select v-model="searchForm.major" placeholder="请选择专业" clearable>
            <el-option v-for="major in majorOptions" :key="major.value" :label="major.label" :value="major.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table v-loading="loading" :data="classList" border style="width: 100%">
        <el-table-column prop="classCode" label="班级编号" width="150" />
        <el-table-column prop="className" label="班级名称" width="200" />
        <el-table-column prop="classShortName" label="班级简称" width="150" />
        <el-table-column prop="educationSystem" label="学制" width="100" />
        <el-table-column prop="educationLevel" label="培养层次" width="120" />
        <el-table-column prop="classType" label="班级类别" width="120" />
        <el-table-column prop="counselor" label="辅导员" width="120" />
        <el-table-column prop="headTeacher" label="班主任" width="120" />
        <el-table-column prop="graduationYear" label="预计毕业年度" width="120" />
        <el-table-column prop="studentCount" label="班级人数" width="100" />
        <el-table-column prop="maxStudentCount" label="班级最大人数" width="120" />
        <el-table-column prop="enrollmentYear" label="入学年份" width="100" />
        <el-table-column prop="department" label="所属院系" width="180" />
        <el-table-column prop="major" label="专业" width="180" />
        <el-table-column prop="majorDirection" label="专业方向" width="180" />
        <el-table-column prop="campus" label="校区" width="120" />
        <el-table-column prop="headTeacherPhone" label="班主任联系电话" width="150" />
        <el-table-column prop="graduationTerm" label="毕业学年学期" width="150" />
        <el-table-column prop="academicAdvisor" label="学业导师" width="120" />
        <el-table-column fixed="right" label="操作" width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface SearchForm {
  classCode: string
  className: string
  department: string
  major: string
}

interface ClassInfo {
  classCode: string
  className: string
  classShortName: string
  educationSystem: string
  educationLevel: string
  classType: string
  counselor: string
  headTeacher: string
  graduationYear: string
  studentCount: number
  maxStudentCount: number
  enrollmentYear: string
  department: string
  major: string
  majorDirection: string
  campus: string
  headTeacherPhone: string
  graduationTerm: string
  academicAdvisor: string
}

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const classList = ref<ClassInfo[]>([])

const searchForm = ref<SearchForm>({
  classCode: '',
  className: '',
  department: '',
  major: ''
})

const departmentOptions = ref([
  { value: '化学与材料工程学院', label: '化学与材料工程学院' },
  { value: '计算机科学与技术学院', label: '计算机科学与技术学院' }
])

const majorOptions = ref([
  { value: '分析检验技术', label: '分析检验技术' },
  { value: '计算机科学与技术', label: '计算机科学与技术' }
])

// 获取班级列表
const fetchClassList = async () => {
  loading.value = true
  try {
    // 模拟数据
    const mockData: ClassInfo[] = [
      {
        classCode: '2025200701011',
        className: '24分析检验技术2班',
        classShortName: '分析2班',
        educationSystem: '3年',
        educationLevel: '专科',
        classType: '普通班级',
        counselor: '张三',
        headTeacher: '李四',
        graduationYear: '2024',
        studentCount: 39,
        maxStudentCount: 60,
        enrollmentYear: '2024',
        department: '化学与材料工程学院',
        major: '分析检验技术',
        majorDirection: '',
        campus: '铁门关校区',
        headTeacherPhone: '13800138000',
        graduationTerm: '2024-2025-1',
        academicAdvisor: '王五'
      }
    ]
    classList.value = mockData
    total.value = mockData.length
  } catch (error) {
    console.error('获取班级列表失败:', error)
    ElMessage.error('获取班级列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchClassList()
}

const handleReset = () => {
  searchForm.value = {
    classCode: '',
    className: '',
    department: '',
    major: ''
  }
  handleSearch()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchClassList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchClassList()
}

const handleAdd = () => {
  ElMessage.info('新增班级功能开发中')
}

const handleEdit = (row: ClassInfo) => {
  ElMessage.info('编辑班级功能开发中')
}

const handleDelete = (row: ClassInfo) => {
  ElMessageBox.confirm('确定要删除该班级吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    fetchClassList()
  })
}

const handleImport = () => {
  ElMessage.info('导入功能开发中')
}

const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

onMounted(() => {
  fetchClassList()
})
</script>

<style scoped>
.class-management-container {
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
</style>

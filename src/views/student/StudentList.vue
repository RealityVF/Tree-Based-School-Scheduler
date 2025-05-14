<template>
  <div class="student-list-container">
    <div class="page-header">
      <h2>学生信息管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="router.push('/student/create')">
          <el-icon><Plus /></el-icon>
          创建学生
        </el-button>
        <el-button type="success" @click="router.push('/student/create?tab=batch')">
          <el-icon><Upload /></el-icon>
          批量导入
        </el-button>
      </div>
    </div>

    <div class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键字">
          <el-input
            v-model="searchForm.keyword"
            placeholder="学号/姓名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="院系">
          <el-select
            v-model="searchForm.departmentId"
            placeholder="请选择院系"
            clearable
            style="width: 180px"
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
        <el-form-item label="专业">
          <el-select
            v-model="searchForm.majorId"
            placeholder="请选择专业"
            clearable
            style="width: 180px"
            @change="handleMajorChange"
          >
            <el-option
              v-for="item in majorOptions"
              :key="item.majorId"
              :label="item.majorName"
              :value="item.majorId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-select
            v-model="searchForm.classId"
            placeholder="请选择班级"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in classOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学籍状态">
          <el-select
            v-model="searchForm.studentStatus"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="在读" value="在读" />
            <el-option label="休学" value="休学" />
            <el-option label="退学" value="退学" />
            <el-option label="毕业" value="毕业" />
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
        :data="studentList"
        style="width: 100%"
        border
        stripe
        row-key="id"
      >
        <el-table-column prop="id" label="学号" width="140" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column label="院系" width="180">
          <template #default="scope">
            {{ getDepartmentName(scope.row.departmentId) }}
          </template>
        </el-table-column>
        <el-table-column label="专业" width="150">
          <template #default="scope">
            {{ getMajorName(scope.row.majorId) }}
          </template>
        </el-table-column>
        <el-table-column label="班级" width="120">
          <template #default="scope">
            {{ getClassName(scope.row.classId) }}
          </template>
        </el-table-column>
        <el-table-column prop="educationLevel" label="培养层次" width="100" />
        <el-table-column prop="studentStatus" label="学籍状态" width="80">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.studentStatus)">
              {{ scope.row.studentStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isActive" label="账号状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.isActive ? 'success' : 'danger'">
              {{ scope.row.isActive ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button-group>
              <el-button
                type="primary"
                size="small"
                @click="handleEdit(scope.row)"
              >
                编辑
              </el-button>
              <el-button
                type="success"
                size="small"
                @click="handleView(scope.row)"
              >
                查看
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="handleDelete(scope.row)"
              >
                删除
              </el-button>
            </el-button-group>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload, Search, Refresh } from '@element-plus/icons-vue'
import { getStudents } from '@/api/student'
import { getDepartments } from '@/api/department'
import { getMajorsByDepartment } from '@/api/major'
import { getClassesByMajorId } from '@/api/class'
import type { Student } from '@/types/student'
import type { Department } from '@/types/department'
import type { Major } from '@/types/major'
import type { Class } from '@/types/class'

const router = useRouter()

// 加载状态
const loading = ref(false)

// 分页参数
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  departmentId: '',
  majorId: '',
  classId: '',
  studentStatus: ''
})

// 选项数据
const departmentOptions = ref<Department[]>([])
const majorOptions = ref<Major[]>([])
const classOptions = ref<Class[]>([])

// 学生列表数据
const studentList = ref<Student[]>([])

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
    searchForm.majorId = ''
    classOptions.value = []
    searchForm.classId = ''
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
    searchForm.classId = ''
    return
  }

  try {
    const res = await getClassesByMajorId(majorId)
    if (res.success && res.data) {
      classOptions.value = res.data
    }
  } catch (error) {
    console.error('获取班级列表失败:', error)
    ElMessage.error('获取班级列表失败')
  }
}

// 院系变更处理
const handleDepartmentChange = (value: string) => {
  searchForm.majorId = ''
  searchForm.classId = ''
  classOptions.value = []
  loadMajors(value)
}

// 专业变更处理
const handleMajorChange = (value: string) => {
  searchForm.classId = ''
  loadClasses(value)
}

// 获取院系名称
const getDepartmentName = (departmentId: string) => {
  const department = departmentOptions.value.find(d => d.id === departmentId)
  return department ? department.name : departmentId
}

// 获取专业名称
const getMajorName = (majorId: string) => {
  const major = majorOptions.value.find(m => m.majorId === majorId)
  return major ? major.majorName : majorId
}

// 获取班级名称
const getClassName = (classId: string) => {
  const clazz = classOptions.value.find(c => c.id === classId)
  return clazz ? clazz.name : classId
}

// 获取状态标签类型
const getStatusType = (status: string) => {
  switch (status) {
    case '在读':
      return 'success'
    case '休学':
      return 'warning'
    case '退学':
      return 'danger'
    case '毕业':
      return 'info'
    default:
      return ''
  }
}

// 加载学生列表数据
const loadStudents = async () => {
  loading.value = true
  try {
    const response = await getStudents({
      page: currentPage.value,
      size: pageSize.value,
      departmentId: searchForm.departmentId,
      majorId: searchForm.majorId,
      classId: searchForm.classId,
      studentStatus: searchForm.studentStatus,
      keyword: searchForm.keyword
    })

    if (response.success) {
      studentList.value = response.data || []
      total.value = response.total || 0
    } else {
      ElMessage.error(response.message || '获取学生列表失败')
    }
  } catch (error) {
    console.error('加载学生列表失败:', error)
    ElMessage.error('加载学生列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  currentPage.value = 1
  loadStudents()
}

// 重置搜索条件
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.departmentId = ''
  searchForm.majorId = ''
  searchForm.classId = ''
  searchForm.studentStatus = ''
  currentPage.value = 1
  loadStudents()
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadStudents()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadStudents()
}

// 编辑学生
const handleEdit = (row: Student) => {
  router.push(`/student/edit/${row.id}`)
}

// 查看学生详情
const handleView = (row: Student) => {
  router.push(`/student/view/${row.id}`)
}

// 删除学生
const handleDelete = async (row: Student) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除学生 ${row.name}（${row.id}）吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // TODO: 调用删除API
    ElMessage.success('删除成功')
    loadStudents()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

// 组件挂载时执行
onMounted(async () => {
  await loadDepartments()
  loadStudents()
})
</script>

<style scoped>
.student-list-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-card {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.table-card {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

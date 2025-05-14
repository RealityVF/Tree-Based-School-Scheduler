<template>
  <div class="course-library">
    <!-- 搜索表单 -->
    <el-form :model="searchForm" class="search-form" inline>
      <el-form-item label="课程名称">
        <el-input v-model="searchForm.name" placeholder="请输入课程名称" clearable />
      </el-form-item>
      
      <el-form-item label="开课院系">
        <el-select 
          v-model="searchForm.department_name" 
          placeholder="请选择开课院系" 
          clearable
          :loading="loading"
          @change="handleDepartmentChange"
        >
          <el-option
            v-for="item in departmentOptions"
            :key="item.id"
            :label="item.name"
            :value="item.name"
          >
            {{ item.name }}
          </el-option>
          <template #empty>
            <el-empty 
              v-if="!loading"
              description="暂无院系数据" 
              :image-size="60"
            />
            <div v-else class="loading-text">
              加载中...
            </div>
          </template>
        </el-select>
      </el-form-item>

      <el-form-item label="课程类别">
        <el-select v-model="searchForm.category" placeholder="请选择课程类别" clearable>
          <el-option label="纯理论课" value="A" />
          <el-option label="理论+实践课" value="B" />
          <el-option label="纯实践课" value="C" />
          <el-option label="实验课" value="D" />
          <el-option label="其他" value="E" />
        </el-select>
      </el-form-item>

      <el-form-item label="课程类型">
        <el-select v-model="searchForm.course_type" placeholder="请选择课程类型" clearable>
          <el-option label="必修" value="必修" />
          <el-option label="选修" value="选修" />
          <el-option label="公共" value="公共" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>重置
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 课程列表表格 -->
    <div class="table-container">
      <div class="table-header">
        <div class="table-title">课程列表</div>
        <div class="table-actions">
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增课程
          </el-button>
          <el-button type="success" @click="handleImport">
            <el-icon><Upload /></el-icon>批量导入
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="courseList"
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="课程编号" width="120" />
        <el-table-column prop="name" label="课程名称" width="180" />
        <el-table-column prop="category" label="课程类别" width="120">
          <template #default="scope">
            {{ getCategoryText(scope.row.category) }}
          </template>
        </el-table-column>
        <el-table-column prop="attribute" label="课程属性" width="120" />
        <el-table-column prop="course_type" label="课程类型" width="120" />
        <el-table-column prop="nature" label="课程性质" width="120" />
        <el-table-column prop="department_name" label="开课院系" width="150" />
        <el-table-column prop="is_active" label="是否启用" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.is_active ? 'success' : 'danger'">
              {{ scope.row.is_active ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="total_hours" label="总学时" width="100" />
        <el-table-column prop="theory_hours" label="理论学时" width="100" />
        <el-table-column prop="experiment_hours" label="实验学时" width="100" />
        <el-table-column prop="computer_hours" label="上机学时" width="100" />
        <el-table-column prop="practice_hours" label="实践学时" width="100" />
        <el-table-column prop="credit" label="学分" width="100" />
        <el-table-column prop="weekly_hours" label="周学时" width="100" />
        <el-table-column prop="is_practical" label="是否纯实践" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.is_practical ? 'warning' : 'info'">
              {{ scope.row.is_practical ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="150">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 调试信息 -->
    <div v-if="import.meta.env.DEV" class="debug-info">
      <h3>调试信息：</h3>
      <p>院系选项数量：{{ departmentOptions.length }}</p>
      <p>当前选中院系：{{ searchForm.department_name }}</p>
      <p>加载状态：{{ loading }}</p>
      <p>课程列表数量：{{ courseList.length }}</p>
      <p>总记录数：{{ total }}</p>
      <p>当前页：{{ currentPage }}</p>
      <p>每页大小：{{ pageSize }}</p>
      <div v-if="departmentOptions.length > 0">
        <h4>院系列表：</h4>
        <ul>
          <li v-for="dept in departmentOptions" :key="dept.id">
            ID: {{ dept.id }} - 名称: {{ dept.name }}
          </li>
        </ul>
      </div>
      <div v-if="courseList.length > 0">
        <h4>课程列表：</h4>
        <pre>{{ JSON.stringify(courseList, null, 2) }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Upload } from '@element-plus/icons-vue'
import { getDepartments } from '@/api/department'
import { getCourses, createCourse, updateCourse, deleteCourse } from '@/api/school'
import type { Course } from '@/api/school'

// 搜索表单
const searchForm = reactive({
  name: '',
  department_name: '',
  category: '',
  course_type: ''
})

// 加载状态
const loading = ref(false)

// 院系选项
const departmentOptions = ref<Array<{
  id: string
  name: string
  value: string
  label: string
}>>([])

// 课程列表数据
const courseList = ref<Course[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 获取课程列表
const fetchCourseList = async () => {
  loading.value = true
  try {
    console.log('开始获取课程列表，参数:', {
      ...searchForm,
      page: currentPage.value,
      pageSize: pageSize.value
    });
    
    const response = await getCourses({
      ...searchForm,
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    console.log('获取课程列表原始响应:', response);

    if (response.success) {
      if (Array.isArray(response.data)) {
        console.log('响应数据是数组格式:', response.data);
        courseList.value = response.data
        total.value = response.data.length
        console.log('更新后的课程列表:', courseList.value);
      } else if (response.data && typeof response.data === 'object') {
        console.log('响应数据是分页对象格式:', response.data);
        const { list, total: totalCount } = response.data as { list: Course[], total: number }
        courseList.value = list || []
        total.value = totalCount || 0
        console.log('更新后的课程列表(分页):', courseList.value);
      } else {
        console.error('未知的响应数据格式:', response.data);
        ElMessage.error('获取课程列表失败：数据格式错误')
        courseList.value = []
        total.value = 0
      }
    } else {
      console.error('获取课程列表失败:', response);
      ElMessage.error(response.message || '获取课程列表失败')
      courseList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败，请稍后重试')
    courseList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 获取院系列表
const fetchDepartments = async () => {
  try {
    const response = await getDepartments()
    if (response.success) {
      departmentOptions.value = response.data.map(dept => ({
        id: dept.id,
        name: dept.name,
        value: dept.name,
        label: dept.name
      }))
    } else {
      ElMessage.error(response.message || '获取院系列表失败')
    }
  } catch (error) {
    console.error('获取院系列表失败:', error)
    ElMessage.error('获取院系列表失败，请稍后重试')
  }
}

// 课程类别文本映射
const getCategoryText = (category: string) => {
  const categoryMap: Record<string, string> = {
    'A': '纯理论课',
    'B': '理论+实践课',
    'C': '纯实践课',
    'D': '实验课',
    'E': '其他'
  }
  return categoryMap[category] || category
}

// 监听院系变化
const handleDepartmentChange = (value: string) => {
  console.log('选择的院系:', value);
  searchForm.department_name = value;
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  fetchCourseList()
}

// 重置处理
const handleReset = () => {
  searchForm.name = ''
  searchForm.department_name = ''
  searchForm.category = ''
  searchForm.course_type = ''
  currentPage.value = 1
  fetchCourseList()
}

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchCourseList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchCourseList()
}

// 新增课程
const handleAdd = () => {
  // TODO: 实现新增课程功能
  console.log('新增课程');
}

// 导入课程
const handleImport = () => {
  // TODO: 实现导入课程功能
  console.log('导入课程');
}

// 编辑课程
const handleEdit = (row: Course) => {
  // TODO: 实现编辑课程功能
  console.log('编辑课程:', row);
}

// 删除课程
const handleDelete = async (row: Course) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该课程吗？此操作不可恢复。',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await deleteCourse(row.id)
    if (response.success) {
      ElMessage.success('删除成功')
      fetchCourseList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除课程失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

// 在组件挂载时获取数据
onMounted(() => {
  fetchDepartments()
  fetchCourseList()
})
</script>

<style scoped>
.course-library {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.table-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.table-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.table-actions {
  display: flex;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.loading-text {
  text-align: center;
  padding: 10px;
  color: #909399;
}

.debug-info {
  margin-top: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
}

.debug-info h3 {
  margin-top: 0;
  color: #666;
}

.debug-info ul {
  margin: 0;
  padding-left: 20px;
}

.debug-info li {
  margin: 5px 0;
}

.debug-info pre {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}
</style> 
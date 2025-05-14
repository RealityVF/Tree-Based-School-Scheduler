<template>
  <div class="course-library-container">
    <div class="page-header">
      <h2>课程库</h2>
      <div class="header-actions">
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>新增课程
        </el-button>
        <el-button type="success" @click="importDialogVisible = true">
          <el-icon><Upload /></el-icon>批量导入
        </el-button>
      </div>
    </div>

    <div class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.name" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item label="开课院系">
          <el-select v-model="searchForm.departmentName" placeholder="请选择开课院系" clearable>
            <el-option
              v-for="item in departmentOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
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
          <el-select v-model="searchForm.courseType" placeholder="请选择课程类型" clearable>
            <el-option v-for="item in departmentOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table
        v-loading="loading"
        :data="courseList"
        border
        style="width: 100%"
      >
        <el-table-column prop="id" label="课程编号" width="120">
          <template #default="scope">
            {{ scope.row.id || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="课程名称" width="180">
          <template #default="scope">
            {{ scope.row.name || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="category" label="课程类别" width="120">
          <template #default="scope">
            {{ getCategoryText(scope.row.category) || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="attribute" label="课程属性" width="120">
          <template #default="scope">
            {{ scope.row.attribute || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="courseType" label="课程类型" width="120">
          <template #default="scope">
            {{ scope.row.courseType || scope.row.course_type || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="nature" label="课程性质" width="120">
          <template #default="scope">
            {{ scope.row.nature || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="开课院系" width="180">
          <template #default="scope">
            {{ scope.row.departmentName || scope.row.department_name || getDepartmentName(scope.row.departmentId) || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="isActive" label="是否启用" width="100">
          <template #default="scope">
            <el-tag :type="(scope.row.isActive ?? scope.row.is_active) ? 'success' : 'danger'">
              {{ (scope.row.isActive ?? scope.row.is_active) ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalHours" label="总学时" width="100">
          <template #default="scope">
            {{ (scope.row.totalHours ?? scope.row.total_hours) !== undefined ? (scope.row.totalHours ?? scope.row.total_hours) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="theoryHours" label="理论学时" width="100">
          <template #default="scope">
            {{ (scope.row.theoryHours ?? scope.row.theory_hours) !== undefined ? (scope.row.theoryHours ?? scope.row.theory_hours) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="experimentHours" label="实验学时" width="100">
          <template #default="scope">
            {{ (scope.row.experimentHours ?? scope.row.experiment_hours) !== undefined ? (scope.row.experimentHours ?? scope.row.experiment_hours) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="computerHours" label="上机学时" width="100">
          <template #default="scope">
            {{ (scope.row.computerHours ?? scope.row.computer_hours) !== undefined ? (scope.row.computerHours ?? scope.row.computer_hours) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="practiceHours" label="实践学时" width="100">
          <template #default="scope">
            {{ (scope.row.practiceHours ?? scope.row.practice_hours) !== undefined ? (scope.row.practiceHours ?? scope.row.practice_hours) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="credit" label="学分" width="80">
          <template #default="scope">
            {{ scope.row.credit !== undefined ? scope.row.credit : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="weeklyHours" label="周学时" width="100">
          <template #default="scope">
            {{ (scope.row.weeklyHours ?? scope.row.weekly_hours) !== undefined ? (scope.row.weeklyHours ?? scope.row.weekly_hours) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="isPractical" label="是否纯实践" width="100">
          <template #default="scope">
            <el-tag :type="(scope.row.isPractical ?? scope.row.is_practical) ? 'warning' : 'info'">
              {{ (scope.row.isPractical ?? scope.row.is_practical) ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="150">
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

    <!-- 添加/编辑课程对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑课程' : '添加课程'"
      width="800px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="rules"
        label-width="100px"
        class="course-form"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="课程编号" prop="id">
              <el-input v-model="courseForm.id" placeholder="请输入课程编号" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程名称" prop="name">
              <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="课程类别" prop="category">
              <el-select v-model="courseForm.category" placeholder="请选择课程类别" class="w-full">
                <el-option label="纯理论课" value="A" />
                <el-option label="理论+实践课" value="B" />
                <el-option label="纯实践课" value="C" />
                <el-option label="实验课" value="D" />
                <el-option label="其他" value="E" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="courseForm.courseType" placeholder="请选择课程类型" class="w-full">
                <el-option v-for="item in departmentOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="开课院系" prop="departmentName">
              <el-select v-model="courseForm.departmentName" placeholder="请选择开课院系" class="w-full">
                <el-option
                  v-for="item in departmentOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程属性" prop="attribute">
              <el-input v-model="courseForm.attribute" placeholder="请输入课程属性" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="课程性质" prop="nature">
              <el-input v-model="courseForm.nature" placeholder="请输入课程性质" />
            </el-form-item>
          </el-col>
          <el-col :span="12" class="flex items-center">
            <el-form-item label="是否启用" prop="isActive">
              <el-switch v-model="courseForm.isActive" />
            </el-form-item>
            <el-form-item label="纯实践" prop="isPractical" label-width="60px" class="ml-4">
              <el-switch v-model="courseForm.isPractical" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">学时信息</el-divider>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="总学时" prop="totalHours">
              <el-input-number v-model="courseForm.totalHours" :min="0" class="w-full" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="理论学时" prop="theoryHours">
              <el-input-number v-model="courseForm.theoryHours" :min="0" class="w-full" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="实验学时" prop="experimentHours">
              <el-input-number v-model="courseForm.experimentHours" :min="0" class="w-full" controls-position="right" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="上机学时" prop="computerHours">
              <el-input-number v-model="courseForm.computerHours" :min="0" class="w-full" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="实践学时" prop="practiceHours">
              <el-input-number v-model="courseForm.practiceHours" :min="0" class="w-full" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="周学时" prop="weeklyHours">
              <el-input-number v-model="courseForm.weeklyHours" :min="0" class="w-full" controls-position="right" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="学分" prop="credit">
              <el-input-number 
                v-model="courseForm.credit" 
                :min="0" 
                :precision="2" 
                :step="0.5" 
                class="w-full"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">
            {{ isEdit ? '保 存' : '创 建' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="批量导入课程"
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
            <el-icon><Download /></el-icon>下载导入模板
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
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, UploadFilled, Upload, Download } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import { getDepartments, getCourses, createCourse, updateCourse, deleteCourse, importCourses, downloadCourseTemplate } from '@/api/school'
import type { Course } from '@/api/school'
import axios from 'axios'

interface DepartmentItem {
  id: string
  name: string
  value?: string
  label?: string
}

// 院系选项
const departmentOptions = ref<{ value: string; label: string }[]>([
  { value: '1001', label: '计算机科学与技术学院' },
  { value: '1002', label: '电子信息工程学院' },
  { value: '1003', label: '机械工程学院' },
  { value: '1004', label: '经济管理学院' },
  { value: '1005', label: '外国语学院' },
  { value: '1006', label: '数学与统计学院' },
  { value: '1007', label: '物理与光电工程学院' },
  { value: '1008', label: '化学与环境工程学院' }
])

// 课程列表数据
const loading = ref(false)
const courseList = ref<Course[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索表单
const searchForm = reactive({
  name: '',
  departmentName: '',
  category: '',
  courseType: ''
})

// 课程表单
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const courseFormRef = ref<FormInstance>()
const courseForm = reactive<Course>({
  id: '',
  name: '',
  category: 'A',
  attribute: '',
  courseType: '必修',
  nature: '',
  departmentName: '',
  isActive: true,
  totalHours: 36,
  theoryHours: 0,
  experimentHours: 0,
  computerHours: 0,
  practiceHours: 0,
  credit: 2,
  weeklyHours: 2,
  isPractical: false
})

// 表单验证规则
const rules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择课程类别', trigger: 'change' },
    { type: 'enum', enum: ['A', 'B', 'C', 'D', 'E'], message: '无效的课程类别', trigger: 'change' }
  ],
  courseType: [
    { required: true, message: '请选择课程类型', trigger: 'change' },
    { type: 'enum', enum: ['必修', '选修', '公共'], message: '无效的课程类型', trigger: 'change' }
  ],
  departmentName: [
    { required: true, message: '请选择开课院系', trigger: 'change' }
  ],
  totalHours: [
    { required: true, message: '请输入总学时', trigger: 'blur' },
    { type: 'number', min: 0, message: '总学时必须大于等于0', trigger: 'blur' }
  ],
  credit: [
    { required: true, message: '请输入学分', trigger: 'blur' },
    { type: 'number', min: 0, message: '学分必须大于等于0', trigger: 'blur' }
  ],
  weeklyHours: [
    { required: true, message: '请输入周学时', trigger: 'blur' },
    { type: 'number', min: 0, message: '周学时必须大于等于0', trigger: 'blur' }
  ]
})

// 导入相关
const importDialogVisible = ref(false)
const importLoading = ref(false)
const importFile = ref<File | null>(null)

// 获取院系列表
const fetchDepartments = async () => {
  try {
    console.log('开始获取部门列表');
    const response = await getDepartments();
    console.log('部门列表响应:', response);

    if (!response.success) {
      console.error('获取部门列表失败:', response.message);
      ElMessage.error(response.message || '获取部门列表失败');
      return;
    }

    if (!response.data || !Array.isArray(response.data)) {
      console.warn('部门数据格式不正确:', response.data);
      departmentOptions.value = [];
      return;
    }

    console.log('原始部门数据:', response.data);
    departmentOptions.value = response.data.map(dept => {
      // 确保从后端返回的部门数据结构正确映射到前端选项结构
      return {
        value: dept.id,
        label: dept.name
      };
    });
    console.log('处理后的部门选项:', departmentOptions.value);

    // 强制更新选择器
    await nextTick();
    if (departmentOptions.value.length > 0) {
      console.log('部门选项已更新，总数:', departmentOptions.value.length);
    } else {
      console.warn('部门选项为空');
    }
  } catch (error) {
    console.error('获取部门列表出错:', error);
    ElMessage.error('获取部门列表失败');
  }
}

// 获取课程列表
const fetchCourseList = async () => {
  loading.value = true
  try {
    console.log('[课程列表] 开始获取，参数:', {
      page: currentPage.value,
      pageSize: pageSize.value,
      name: searchForm.name,
      departmentName: searchForm.departmentName,
      category: searchForm.category,
      courseType: searchForm.courseType
    })

    // 直接使用axios调用API，查看原始响应
    try {
      const axiosResponse = await axios.get('/api/courses', {
        params: {
          page: currentPage.value,
          pageSize: pageSize.value,
          name: searchForm.name,
          departmentName: searchForm.departmentName,
          category: searchForm.category,
          courseType: searchForm.courseType
        }
      })
      console.log('[直接调用] 原始响应:', axiosResponse.data)
      
      if (Array.isArray(axiosResponse.data)) {
        // 如果直接返回数组数据
        courseList.value = axiosResponse.data.map(course => {
          // 处理字段命名差异
          const processedCourse: any = { ...course };
          
          // 处理部门数据
          if (processedCourse.department_name && !processedCourse.departmentName) {
            processedCourse.departmentName = processedCourse.department_name;
          }
          
          // 尝试通过部门ID查找部门名称
          if (!processedCourse.departmentName && processedCourse.departmentId && departmentOptions.value.length > 0) {
            const department = departmentOptions.value.find(dept => dept.value === processedCourse.departmentId);
            if (department) {
              processedCourse.departmentName = department.label;
            }
          }
          
          return processedCourse;
        });
        total.value = courseList.value.length;
        console.log(`[直接调用] 处理完成，获取到${courseList.value.length}条数据`);
        loading.value = false;
        return;
      }
    } catch (axiosError) {
      console.error('[直接调用] 请求失败:', axiosError)
    }

    const response = await getCourses({
      page: currentPage.value,
      pageSize: pageSize.value,
      name: searchForm.name,
      departmentName: searchForm.departmentName,
      category: searchForm.category,
      courseType: searchForm.courseType
    })

    console.log('[课程列表] 原始响应数据:', response)

    if (!response.success) {
      throw new Error(response.message || '获取课程列表失败')
    }

    // 查看原始数据结构
    console.log('[课程列表] 响应数据结构:', {
      isArray: Array.isArray(response.data),
      type: typeof response.data,
      keys: response.data ? Object.keys(response.data) : '无数据'
    })

    // 处理课程数据
    if (Array.isArray(response.data)) {
      console.log('[课程列表] 数组类型数据，长度:', response.data.length)
      
      // 检查数据字段
      if (response.data.length > 0) {
        const fieldMapping = {
          'id': '课程编号',
          'name': '课程名称',
          'category': '课程类别',
          'attribute': '课程属性',
          'courseType': '课程类型(驼峰)',
          'nature': '课程性质',
          'departmentName': '开课院系(驼峰)',
          'isActive': '是否启用(驼峰)',
          'totalHours': '总学时(驼峰)',
          'theoryHours': '理论学时(驼峰)',
          'experimentHours': '实验学时(驼峰)',
          'computerHours': '上机学时(驼峰)',
          'practiceHours': '实践学时(驼峰)',
          'credit': '学分',
          'weeklyHours': '周学时(驼峰)',
          'isPractical': '是否纯实践(驼峰)'
        }
        
        const firstItem = response.data[0]
        console.log('[课程列表] 首条数据字段映射:')
        for (const [key, label] of Object.entries(fieldMapping)) {
          console.log(`${label}: ${key} = ${JSON.stringify(firstItem[key])}`)
        }
        
        // 转换字段名称(如果需要)
        courseList.value = response.data.map(item => {
          const processedItem: any = { ...item }
          
          // 处理可能使用驼峰命名的字段
          if (processedItem.courseType !== undefined && processedItem.course_type === undefined) {
            processedItem.course_type = processedItem.courseType
          } else if (processedItem.course_type !== undefined && processedItem.courseType === undefined) {
            processedItem.courseType = processedItem.course_type
          }
          
          if (processedItem.departmentName !== undefined && processedItem.department_name === undefined) {
            processedItem.department_name = processedItem.departmentName
          } else if (processedItem.department_name !== undefined && processedItem.departmentName === undefined) {
            processedItem.departmentName = processedItem.department_name
          }
          
          // 如果没有部门名称但有部门ID，则尝试查找部门名称
          if (!processedItem.departmentName && !processedItem.department_name && 
              processedItem.departmentId && departmentOptions.value.length > 0) {
            const department = departmentOptions.value.find(dept => dept.value === processedItem.departmentId);
            if (department) {
              processedItem.departmentName = department.label;
            }
          }
          
          // 处理其他字段
          if (processedItem.isActive !== undefined && processedItem.is_active === undefined) {
            processedItem.is_active = processedItem.isActive
          } else if (processedItem.is_active !== undefined && processedItem.isActive === undefined) {
            processedItem.isActive = processedItem.is_active
          }
          
          if (processedItem.totalHours !== undefined && processedItem.total_hours === undefined) {
            processedItem.total_hours = processedItem.totalHours
          } else if (processedItem.total_hours !== undefined && processedItem.totalHours === undefined) {
            processedItem.totalHours = processedItem.total_hours
          }
          
          if (processedItem.theoryHours !== undefined && processedItem.theory_hours === undefined) {
            processedItem.theory_hours = processedItem.theoryHours
          } else if (processedItem.theory_hours !== undefined && processedItem.theoryHours === undefined) {
            processedItem.theoryHours = processedItem.theory_hours
          }
          
          if (processedItem.experimentHours !== undefined && processedItem.experiment_hours === undefined) {
            processedItem.experiment_hours = processedItem.experimentHours
          } else if (processedItem.experiment_hours !== undefined && processedItem.experimentHours === undefined) {
            processedItem.experimentHours = processedItem.experiment_hours
          }
          
          if (processedItem.computerHours !== undefined && processedItem.computer_hours === undefined) {
            processedItem.computer_hours = processedItem.computerHours
          } else if (processedItem.computer_hours !== undefined && processedItem.computerHours === undefined) {
            processedItem.computerHours = processedItem.computer_hours
          }
          
          if (processedItem.practiceHours !== undefined && processedItem.practice_hours === undefined) {
            processedItem.practice_hours = processedItem.practiceHours
          } else if (processedItem.practice_hours !== undefined && processedItem.practiceHours === undefined) {
            processedItem.practiceHours = processedItem.practice_hours
          }
          
          if (processedItem.weeklyHours !== undefined && processedItem.weekly_hours === undefined) {
            processedItem.weekly_hours = processedItem.weeklyHours
          } else if (processedItem.weekly_hours !== undefined && processedItem.weeklyHours === undefined) {
            processedItem.weeklyHours = processedItem.weekly_hours
          }
          
          if (processedItem.isPractical !== undefined && processedItem.is_practical === undefined) {
            processedItem.is_practical = processedItem.isPractical
          } else if (processedItem.is_practical !== undefined && processedItem.isPractical === undefined) {
            processedItem.isPractical = processedItem.is_practical
          }
          
          return processedItem
        })
        
        if (courseList.value.length > 0) {
          console.log('[课程列表] 处理后首条数据:', courseList.value[0])
        }
      } else {
        courseList.value = []
      }
      
      total.value = response.data.length
    } else if (response.data && typeof response.data === 'object') {
      // 检查对象结构
      console.log('[课程列表] 对象类型数据，属性:', Object.keys(response.data))
      
      // 尝试不同的属性名
      const possibleListProps = ['list', 'courses', 'items', 'data']
      const possibleTotalProps = ['total', 'totalCount', 'totalItems', 'count']
      
      let foundListProp = null
      for (const prop of possibleListProps) {
        if (Array.isArray(response.data[prop])) {
          foundListProp = prop
          console.log(`[课程列表] 找到数据列表属性: ${prop}, 长度:`, response.data[prop].length)
          break
        }
      }
      
      let foundTotalProp = null
      for (const prop of possibleTotalProps) {
        if (typeof response.data[prop] === 'number') {
          foundTotalProp = prop
          console.log(`[课程列表] 找到总数属性: ${prop}, 值:`, response.data[prop])
          break
        }
      }
      
      if (foundListProp) {
        const dataList = response.data[foundListProp]
        if (dataList.length > 0) {
          const fieldMapping = {
            'id': '课程编号',
            'name': '课程名称',
            'category': '课程类别',
            'attribute': '课程属性',
            'courseType': '课程类型(驼峰)',
            'nature': '课程性质',
            'departmentName': '开课院系(驼峰)',
            'isActive': '是否启用(驼峰)',
            'totalHours': '总学时(驼峰)',
            'theoryHours': '理论学时(驼峰)',
            'experimentHours': '实验学时(驼峰)',
            'computerHours': '上机学时(驼峰)',
            'practiceHours': '实践学时(驼峰)',
            'credit': '学分',
            'weeklyHours': '周学时(驼峰)',
            'isPractical': '是否纯实践(驼峰)'
          }
          
          const firstItem = dataList[0]
          console.log('[课程列表] 首条数据字段映射:')
          for (const [key, label] of Object.entries(fieldMapping)) {
            console.log(`${label}: ${key} = ${JSON.stringify(firstItem[key])}`)
          }
        }
        
        // 转换字段名称(如果需要)
        courseList.value = dataList.map(item => {
          const processedItem: any = { ...item }
          
          // 处理可能使用驼峰命名的字段
          if (processedItem.courseType !== undefined && processedItem.course_type === undefined) {
            processedItem.course_type = processedItem.courseType
          } else if (processedItem.course_type !== undefined && processedItem.courseType === undefined) {
            processedItem.courseType = processedItem.course_type
          }
          
          if (processedItem.departmentName !== undefined && processedItem.department_name === undefined) {
            processedItem.department_name = processedItem.departmentName
          } else if (processedItem.department_name !== undefined && processedItem.departmentName === undefined) {
            processedItem.departmentName = processedItem.department_name
          }
          
          // 如果没有部门名称但有部门ID，则尝试查找部门名称
          if (!processedItem.departmentName && !processedItem.department_name && 
              processedItem.departmentId && departmentOptions.value.length > 0) {
            const department = departmentOptions.value.find(dept => dept.value === processedItem.departmentId);
            if (department) {
              processedItem.departmentName = department.label;
            }
          }
          
          // 处理其他字段
          if (processedItem.isActive !== undefined && processedItem.is_active === undefined) {
            processedItem.is_active = processedItem.isActive
          } else if (processedItem.is_active !== undefined && processedItem.isActive === undefined) {
            processedItem.isActive = processedItem.is_active
          }
          
          if (processedItem.totalHours !== undefined && processedItem.total_hours === undefined) {
            processedItem.total_hours = processedItem.totalHours
          } else if (processedItem.total_hours !== undefined && processedItem.totalHours === undefined) {
            processedItem.totalHours = processedItem.total_hours
          }
          
          if (processedItem.theoryHours !== undefined && processedItem.theory_hours === undefined) {
            processedItem.theory_hours = processedItem.theoryHours
          } else if (processedItem.theory_hours !== undefined && processedItem.theoryHours === undefined) {
            processedItem.theoryHours = processedItem.theory_hours
          }
          
          if (processedItem.experimentHours !== undefined && processedItem.experiment_hours === undefined) {
            processedItem.experiment_hours = processedItem.experimentHours
          } else if (processedItem.experiment_hours !== undefined && processedItem.experimentHours === undefined) {
            processedItem.experimentHours = processedItem.experiment_hours
          }
          
          if (processedItem.computerHours !== undefined && processedItem.computer_hours === undefined) {
            processedItem.computer_hours = processedItem.computerHours
          } else if (processedItem.computer_hours !== undefined && processedItem.computerHours === undefined) {
            processedItem.computerHours = processedItem.computer_hours
          }
          
          if (processedItem.practiceHours !== undefined && processedItem.practice_hours === undefined) {
            processedItem.practice_hours = processedItem.practiceHours
          } else if (processedItem.practice_hours !== undefined && processedItem.practiceHours === undefined) {
            processedItem.practiceHours = processedItem.practice_hours
          }
          
          if (processedItem.weeklyHours !== undefined && processedItem.weekly_hours === undefined) {
            processedItem.weekly_hours = processedItem.weeklyHours
          } else if (processedItem.weekly_hours !== undefined && processedItem.weeklyHours === undefined) {
            processedItem.weeklyHours = processedItem.weekly_hours
          }
          
          if (processedItem.isPractical !== undefined && processedItem.is_practical === undefined) {
            processedItem.is_practical = processedItem.isPractical
          } else if (processedItem.is_practical !== undefined && processedItem.isPractical === undefined) {
            processedItem.isPractical = processedItem.is_practical
          }
          
          return processedItem
        })
        
        total.value = foundTotalProp ? response.data[foundTotalProp] : dataList.length
      } else if (response.data.courses && Array.isArray(response.data.courses)) {
        courseList.value = response.data.courses
        total.value = response.data.total || response.data.courses.length
      } else {
        // 最后尝试直接使用整个对象
        console.log('[课程列表] 尝试直接使用整个对象作为数据源')
        courseList.value = [response.data]
        total.value = 1
      }
    } else {
      throw new Error('响应数据格式不正确')
    }

    // 查看处理后的数据
    console.log('[课程列表] 处理后的数据列表长度:', courseList.value.length)
    if (courseList.value.length > 0) {
      console.log('[课程列表] 第一条数据示例:', courseList.value[0])
    } else {
      console.log('[课程列表] 数据为空')
    }
    
    // 确保所有必要的字段都存在
    if (courseList.value.length > 0) {
      const firstItem = courseList.value[0]
      const requiredFields = ['id', 'name', 'category', 'courseType', 'departmentName', 'totalHours', 'credit']
      const missingFields = requiredFields.filter(field => firstItem[field] === undefined)
      if (missingFields.length > 0) {
        console.warn('[课程列表] 缺少必要字段:', missingFields)
      }
    }
  } catch (error) {
    console.error('[课程列表] 获取失败:', error)
    ElMessage.error(error instanceof Error ? error.message : '获取课程列表失败')
    courseList.value = []
    total.value = 0
  } finally {
    loading.value = false
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

// 获取部门名称
const getDepartmentName = (departmentId: string) => {
  if (!departmentId) return '';
  const department = departmentOptions.value.find(item => item.value === departmentId);
  return department ? department.label : departmentId;
}

// 搜索课程
const handleSearch = () => {
  currentPage.value = 1
  fetchCourseList()
}

// 重置搜索
const handleReset = () => {
  searchForm.name = ''
  searchForm.departmentName = ''
  searchForm.category = ''
  searchForm.courseType = ''
  currentPage.value = 1
  fetchCourseList()
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  Object.assign(courseForm, {
    id: '',
    name: '',
    category: 'A',
    attribute: '',
    courseType: '必修',
    nature: '',
    departmentName: '',
    isActive: true,
    totalHours: 36,  // 设置默认值
    theoryHours: 0,
    experimentHours: 0,
    computerHours: 0,
    practiceHours: 0,
    credit: 2,  // 设置默认值
    weeklyHours: 2,  // 设置默认值
    isPractical: false
  })
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row: Course) => {
  console.log('[课程编辑] 开始编辑课程:', row)
  isEdit.value = true
  
  // 确保所有字段都被正确赋值，包括默认值
  courseForm.id = row.id
  courseForm.name = row.name
  courseForm.category = row.category
  courseForm.attribute = row.attribute || ''
  courseForm.courseType = row.courseType || '必修'
  courseForm.nature = row.nature || ''
  courseForm.departmentName = row.departmentName
  courseForm.isActive = row.isActive ?? true
  courseForm.totalHours = row.totalHours
  courseForm.theoryHours = row.theoryHours || 0
  courseForm.experimentHours = row.experimentHours || 0
  courseForm.computerHours = row.computerHours || 0
  courseForm.practiceHours = row.practiceHours || 0
  courseForm.credit = row.credit
  courseForm.weeklyHours = row.weeklyHours
  courseForm.isPractical = row.isPractical ?? false

  console.log('[课程编辑] 表单数据已更新:', courseForm)
  dialogVisible.value = true
}

// 处理删除
const handleDelete = async (row: Course) => {
  try {
    await ElMessageBox.confirm('确定要删除该课程吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await deleteCourse(row.id)
    if (response.success) {
      ElMessage.success('删除成功')
      await fetchCourseList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    console.error('删除课程失败:', error)
    ElMessage.error('删除失败')
  }
}

// 提交表单
const submitForm = async () => {
  if (!courseFormRef.value) return

  await courseFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        const isEdit = Boolean(courseForm.id)
        console.log('[课程编辑] 提交的表单数据:', courseForm)
        
        // 确保所有必填字段都有值
        if (!courseForm.id || !courseForm.name || !courseForm.category || 
            !courseForm.courseType || !courseForm.departmentName || 
            courseForm.totalHours === undefined || courseForm.totalHours === null ||
            courseForm.credit === undefined || courseForm.credit === null ||
            courseForm.weeklyHours === undefined || courseForm.weeklyHours === null) {
          throw new Error('请填写所有必填字段')
        }

        // 验证枚举值
        const validCategories = ['A', 'B', 'C', 'D', 'E']
        const validCourseTypes = ['必修', '选修', '公共']
        
        if (!validCategories.includes(courseForm.category)) {
          throw new Error('无效的课程类别')
        }
        
        if (!validCourseTypes.includes(courseForm.courseType)) {
          throw new Error('无效的课程类型')
        }

        // 使用驼峰命名法构建数据
        const data = {
          id: courseForm.id,
          name: courseForm.name.trim(),
          category: courseForm.category,
          attribute: courseForm.attribute || '',
          courseType: courseForm.courseType,
          nature: courseForm.nature || '',
          departmentName: courseForm.departmentName,
          isActive: courseForm.isActive ?? true,
          totalHours: Number(courseForm.totalHours),
          theoryHours: Number(courseForm.theoryHours || 0),
          experimentHours: Number(courseForm.experimentHours || 0),
          computerHours: Number(courseForm.computerHours || 0),
          practiceHours: Number(courseForm.practiceHours || 0),
          credit: Number(courseForm.credit),
          weeklyHours: Number(courseForm.weeklyHours),
          isPractical: courseForm.isPractical ?? false
        }

        // 验证数值字段
        if (isNaN(data.totalHours) || data.totalHours < 0) {
          throw new Error('总学时必须是非负数')
        }
        if (isNaN(data.credit) || data.credit < 0) {
          throw new Error('学分必须是非负数')
        }
        if (isNaN(data.weeklyHours) || data.weeklyHours < 0) {
          throw new Error('周学时必须是非负数')
        }

        console.log('[课程编辑] 处理后的提交数据:', data)

        const response = isEdit
          ? await updateCourse(data.id, data)
          : await createCourse(data)

        if (response.success) {
          ElMessage.success(isEdit ? '更新成功' : '创建成功')
          dialogVisible.value = false
          await fetchCourseList()
        } else {
          throw new Error(response.message || '操作失败')
        }
      } catch (error) {
        console.error('[课程编辑] 操作失败:', error)
        ElMessage.error(error instanceof Error ? error.message : '操作失败，请稍后重试')
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
const downloadTemplate = async () => {
  try {
    const response = await downloadCourseTemplate()
    const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' })
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = '课程导入模板.xlsx'
    link.click()
    window.URL.revokeObjectURL(link.href)
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('下载模板失败，请稍后重试')
  }
}

// 提交导入
const submitImport = async () => {
  if (!importFile.value) {
    ElMessage.warning('请选择要导入的文件')
    return
  }

  importLoading.value = true
  try {
    const response = await importCourses(importFile.value)
    if (response.data.success) {
      ElMessage.success('导入成功')
      importDialogVisible.value = false
      fetchCourseList()
    } else {
      ElMessage.error(response.data.message || '导入失败')
    }
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
  fetchCourseList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchCourseList()
}

// 组件挂载时初始化
onMounted(() => {
  fetchCourseList()
  fetchDepartments()
})
</script>

<style scoped>
.course-library-container {
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

.course-form {
  padding: 20px 10px;
}

.course-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.course-form :deep(.el-input-number) {
  width: 100%;
}

.course-form :deep(.el-select) {
  width: 100%;
}

.course-form :deep(.el-divider__text) {
  font-size: 16px;
  font-weight: 500;
  color: #606266;
}

.dialog-footer {
  text-align: right;
  padding-top: 10px;
}

.w-full {
  width: 100%;
}

.flex {
  display: flex;
}

.items-center {
  align-items: center;
}

.ml-4 {
  margin-left: 16px;
}
</style>

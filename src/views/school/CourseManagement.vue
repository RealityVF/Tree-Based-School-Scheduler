<template>
  <div class="course-management-container">
    <div class="page-header">
      <h2>课程管理</h2>
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
          <el-input v-model="searchForm.keyword" placeholder="请输入课程名称或编号" clearable />
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="searchForm.courseType" placeholder="选择课程类型" clearable>
            <el-option
              v-for="item in courseTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开课院系">
          <el-select v-model="searchForm.departmentId" placeholder="选择开课院系" clearable>
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
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon> 重置
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
        <el-table-column prop="id" label="课程编号" width="120" />
        <el-table-column prop="name" label="课程名称" width="200" />
        <el-table-column prop="category" label="课程类别" width="120">
          <template #default="scope">
            {{ getCategoryLabel(scope.row.category) }}
          </template>
        </el-table-column>
        <el-table-column prop="attribute" label="课程属性" width="120" />
        <el-table-column prop="courseType" label="课程类型" width="120" />
        <el-table-column prop="nature" label="课程性质" width="120" />
        <el-table-column prop="departmentName" label="开课院系" width="150">
          <template #default="scope">
            {{ scope.row.departmentName || scope.row.department_name || getDepartmentName(scope.row.departmentId) || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="isActive" label="是否启用" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isActive ? 'success' : 'danger'">
              {{ scope.row.isActive ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalHours" label="总学时" width="100" />
        <el-table-column prop="theoryHours" label="理论学时" width="100" />
        <el-table-column prop="experimentHours" label="实验学时" width="100" />
        <el-table-column prop="computerHours" label="上机学时" width="100" />
        <el-table-column prop="practiceHours" label="实践学时" width="100" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column prop="weeklyHours" label="周学时" width="100" />
        <el-table-column prop="isPractical" label="是否纯实践" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isPractical ? 'warning' : 'info'">
              {{ scope.row.isPractical ? '是' : '否' }}
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
      :title="isEdit ? '编辑课程' : '新增课程'"
      width="650px"
    >
      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="rules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="name">
              <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程类别" prop="category">
              <el-select v-model="courseForm.category" placeholder="请选择课程类别">
                <el-option
                  v-for="item in categoryOptions"
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
            <el-form-item label="课程属性" prop="attribute">
              <el-input v-model="courseForm.attribute" placeholder="请输入课程属性" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程类型" prop="courseType">
              <el-select v-model="courseForm.courseType" placeholder="请选择课程类型">
                <el-option
                  v-for="item in courseTypeOptions"
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
            <el-form-item label="课程性质" prop="nature">
              <el-input v-model="courseForm.nature" placeholder="请输入课程性质" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开课院系" prop="departmentId">
              <el-select v-model="courseForm.departmentId" placeholder="请选择开课院系">
                <el-option
                  v-for="item in departmentOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="总学时" prop="totalHours">
              <el-input-number v-model="courseForm.totalHours" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="理论学时" prop="theoryHours">
              <el-input-number v-model="courseForm.theoryHours" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="实验学时" prop="experimentHours">
              <el-input-number v-model="courseForm.experimentHours" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="上机学时" prop="computerHours">
              <el-input-number v-model="courseForm.computerHours" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="实践学时" prop="practiceHours">
              <el-input-number v-model="courseForm.practiceHours" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="周学时" prop="weeklyHours">
              <el-input-number v-model="courseForm.weeklyHours" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="学分" prop="credit">
              <el-input-number v-model="courseForm.credit" :min="0" :precision="1" :step="0.5" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否启用" prop="isActive">
              <el-switch v-model="courseForm.isActive" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="纯实践" prop="isPractical">
              <el-switch v-model="courseForm.isPractical" />
            </el-form-item>
          </el-col>
        </el-row>
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
            <el-icon><Download /></el-icon> 下载导入模板
          </el-button>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="submitImport"
            :loading="importLoading"
            :disabled="!importFile"
          >
            开始导入
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
import type { FormInstance, FormRules } from 'element-plus'
import {
  createCourse,
  updateCourse,
  deleteCourse,
  importCourses,
  getDepartments,
  getTeachers,
  getClassrooms
} from '@/api/school'
import axios from 'axios'

interface OptionItem {
  id: string
  name: string
  buildingName?: string
  value?: string
  label?: string
}

// 更新 Course 接口定义
interface Course {
  id?: string;
  name: string;
  category: string;
  attribute: string;
  courseType: string;
  nature: string;
  departmentId: string;
  departmentName?: string;
  isActive: boolean;
  totalHours: number;
  theoryHours: number;
  experimentHours: number;
  computerHours: number;
  practiceHours: number;
  credit: number;
  weeklyHours: number;
  isPractical: boolean;
  createdAt?: string;
}

// 课程类别选项
const categoryOptions = [
  { value: 'A', label: '纯理论课' },
  { value: 'B', label: '理论+实践课' },
  { value: 'C', label: '纯实践课' },
  { value: 'D', label: '实验课' },
  { value: 'E', label: '其他' }
];

// 课程类型选项
const courseTypeOptions = [
  { value: '必修', label: '必修' },
  { value: '选修', label: '选修' },
  { value: '公共', label: '公共' }
];

// 院系选项
const departmentOptions = ref<{ value: string; label: string }[]>([])

// 专业选项
const majorOptions = ref<{ value: string; label: string }[]>([])

// 教师选项
const teacherOptions = ref<{ value: string; label: string }[]>([])

// 教室选项
const classroomOptions = ref<{ value: string; label: string }[]>([])

// 课程数据
const loading = ref(false)
const courseList = ref<Course[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  courseType: '',
  departmentId: ''
});

// 课程表单
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const courseFormRef = ref<FormInstance>()
const courseForm = ref<Course>({
  name: '',
  category: '',
  attribute: '',
  courseType: '',
  nature: '',
  departmentId: '',
  isActive: true,
  totalHours: 0,
  theoryHours: 0,
  experimentHours: 0,
  computerHours: 0,
  practiceHours: 0,
  credit: 0,
  weeklyHours: 0,
  isPractical: false
});

// 表单验证规则
const rules = reactive<FormRules>({
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择课程类别', trigger: 'change' }],
  attribute: [{ required: true, message: '请选择课程属性', trigger: 'change' }],
  courseType: [{ required: true, message: '请选择课程类型', trigger: 'change' }],
  nature: [{ required: true, message: '请选择课程性质', trigger: 'change' }],
  departmentId: [{ required: true, message: '请选择开课院系', trigger: 'change' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'blur' }],
  totalHours: [{ required: true, message: '请输入总学时', trigger: 'blur' }],
  weeklyHours: [{ required: true, message: '请输入周学时', trigger: 'blur' }]
});

// 导入相关
const importDialogVisible = ref(false)
const importLoading = ref(false)
const importFile = ref<File | null>(null)

// 排课相关
const scheduleDialogVisible = ref(false)
const scheduleActiveTab = ref('schedule')
const scheduleLoading = ref(false)
const scheduleForm = ref({
  courseId: '',
  classroomId: '',
  weekday: '',
  period: ''
})
const scheduleList = ref<Course[]>([])
const studentList = ref([])

// 获取课程列表
const fetchCourseList = async () => {
  loading.value = true
  try {
    console.log('开始获取课程列表，参数:', {
      keyword: searchForm.keyword,
      courseType: searchForm.courseType,
      departmentId: searchForm.departmentId,
      page: currentPage.value,
      size: pageSize.value
    });
    
    const response = await axios.get('/api/courses', {
      params: {
        keyword: searchForm.keyword,
        courseType: searchForm.courseType,
        departmentId: searchForm.departmentId,
        page: currentPage.value,
        size: pageSize.value
      }
    })
    
    console.log('课程列表原始响应:', response.data);
    
    // 处理课程列表数据
    if (response.data && Array.isArray(response.data.courses)) {
      courseList.value = response.data.courses.map((course: any) => {
        // 处理字段命名差异
        const processedCourse: any = { ...course };
        
        // 处理部门数据
        if (course.department_name && !course.departmentName) {
          processedCourse.departmentName = course.department_name;
        }
        
        // 处理课程类型
        if (course.course_type && !course.courseType) {
          processedCourse.courseType = course.course_type;
        }
        
        // 处理学时字段
        if (course.total_hours !== undefined && course.totalHours === undefined) {
          processedCourse.totalHours = course.total_hours;
        }
        if (course.theory_hours !== undefined && course.theoryHours === undefined) {
          processedCourse.theoryHours = course.theory_hours;
        }
        if (course.experiment_hours !== undefined && course.experimentHours === undefined) {
          processedCourse.experimentHours = course.experiment_hours;
        }
        if (course.computer_hours !== undefined && course.computerHours === undefined) {
          processedCourse.computerHours = course.computer_hours;
        }
        if (course.practice_hours !== undefined && course.practiceHours === undefined) {
          processedCourse.practiceHours = course.practice_hours;
        }
        if (course.weekly_hours !== undefined && course.weeklyHours === undefined) {
          processedCourse.weeklyHours = course.weekly_hours;
        }
        
        // 处理布尔值字段
        if (course.is_active !== undefined && course.isActive === undefined) {
          processedCourse.isActive = course.is_active;
        }
        if (course.is_practical !== undefined && course.isPractical === undefined) {
          processedCourse.isPractical = course.is_practical;
        }
        
        // 如果有departmentId但没有departmentName，尝试通过部门ID获取部门名称
        if (course.departmentId && !processedCourse.departmentName && departmentOptions.value.length > 0) {
          const department = departmentOptions.value.find(dept => dept.value === course.departmentId);
          if (department) {
            processedCourse.departmentName = department.label;
          }
        }
        
        return processedCourse;
      });
      
      if (courseList.value.length > 0) {
        console.log('处理后的第一条课程数据:', courseList.value[0]);
      }
    } else if (response.data && Array.isArray(response.data)) {
      // 直接返回数组的情况
      courseList.value = response.data.map((course: any) => {
        const processedCourse: any = { ...course };
        
        // 同样处理字段命名差异
        if (course.department_name && !course.departmentName) {
          processedCourse.departmentName = course.department_name;
        }
        
        return processedCourse;
      });
    } else {
      courseList.value = [];
    }
    
    total.value = response.data.total || courseList.value.length || 0;
    console.log(`获取到 ${courseList.value.length} 条课程数据, 总计: ${total.value}`);
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
    courseList.value = [];
    total.value = 0;
  } finally {
    loading.value = false
  }
}

// 获取院系列表
const fetchDepartments = async () => {
  try {
    console.log('开始获取院系列表');
    const response = await getDepartments()
    console.log('院系列表响应:', response);
    
    if (response.success) {
      // 根据后端返回数据结构进行映射
      departmentOptions.value = (response.data || []).map(dept => ({
        value: dept.id,
        label: dept.name
      }));
      console.log('处理后的院系选项:', departmentOptions.value);
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
    // 直接使用axios调用API获取专业列表
    const response = await axios.get('/api/majors')
    console.log('专业列表响应:', response)
    
    if (response && response.status === 200) {
      let majorsData = []
      
      // 处理不同的响应格式
      if (response.data && Array.isArray(response.data)) {
        majorsData = response.data
      } else if (response.data && response.data.data && Array.isArray(response.data.data)) {
        majorsData = response.data.data
      }
      
      // 转换为选项格式
      majorOptions.value = majorsData.map((major: any) => ({
        value: major.id || major.majorId,
        label: major.name || major.majorName
      }))
      
      console.log('处理后的专业选项:', majorOptions.value)
    } else {
      ElMessage.error('获取专业列表失败')
    }
  } catch (error) {
    console.error('获取专业列表失败:', error)
    ElMessage.error('获取专业列表失败，请稍后重试')
  }
}

// 获取教师列表
const fetchTeachers = async () => {
  try {
    const response = await getTeachers()
    if (response.success) {
      teacherOptions.value = (response.data as OptionItem[]).map(item => ({
        value: item.id,
        label: item.name
      }))
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
    ElMessage.error('获取教师列表失败，请稍后重试')
  }
}

// 获取教室列表
const fetchClassrooms = async () => {
  try {
    const response = await getClassrooms()
    if (response.success) {
      classroomOptions.value = (response.data as OptionItem[]).map(item => ({
        value: item.id,
        label: `${item.buildingName ? item.buildingName + '-' : ''}${item.name}`
      }))
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('获取教室列表失败:', error)
    ElMessage.error('获取教室列表失败，请稍后重试')
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.courseType = ''
  searchForm.departmentId = ''
  handleSearch()
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchCourseList()
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false
  courseForm.value.id = ''
  courseForm.value.name = ''
  courseForm.value.category = ''
  courseForm.value.attribute = ''
  courseForm.value.courseType = ''
  courseForm.value.nature = ''
  courseForm.value.departmentId = ''
  courseForm.value.isActive = true
  courseForm.value.totalHours = 0
  courseForm.value.theoryHours = 0
  courseForm.value.experimentHours = 0
  courseForm.value.computerHours = 0
  courseForm.value.practiceHours = 0
  courseForm.value.credit = 0
  courseForm.value.weeklyHours = 0
  courseForm.value.isPractical = false
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (course: Course) => {
  // 找到对应的部门名称转换为ID
  let departmentId = course.departmentId;
  if (!departmentId && course.departmentName) {
    // 如果没有部门ID但有部门名称，则查找匹配的部门
    const matchedDepartment = departmentOptions.value.find(
      dept => dept.label === course.departmentName
    );
    if (matchedDepartment) {
      departmentId = matchedDepartment.value;
    }
  }

  courseForm.value = { 
    ...course,
    departmentId: departmentId || ''
  };
  
  isEdit.value = true;
  dialogVisible.value = true;
}

// 处理删除
const handleDelete = async (course: Course) => {
  try {
    await ElMessageBox.confirm('确认删除该课程吗？', '提示', {
      type: 'warning'
    })
    loading.value = true
    const response = await axios.delete(`/api/courses/${course.id}`)
    if (response.status === 200) {
      ElMessage.success('删除成功')
      await fetchCourseList()
    }
  } catch (error) {
    console.error('删除课程失败:', error)
    ElMessage.error('删除失败')
  } finally {
    loading.value = false
  }
}

// 提交表单
const submitForm = async () => {
  if (!courseFormRef.value) return

  await courseFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true
      try {
        const isEdit = Boolean(courseForm.value.id)
        console.log('提交的表单数据:', courseForm.value)
        
        // 获取部门名称
        const selectedDepartment = departmentOptions.value.find(
          dept => dept.value === courseForm.value.departmentId
        );
        
        const data = {
          name: courseForm.value.name,
          category: courseForm.value.category,
          attribute: courseForm.value.attribute,
          course_type: courseForm.value.courseType,
          nature: courseForm.value.nature,
          department_id: courseForm.value.departmentId,
          department_name: selectedDepartment ? selectedDepartment.label : '',
          is_active: courseForm.value.isActive,
          total_hours: courseForm.value.totalHours,
          theory_hours: courseForm.value.theoryHours,
          experiment_hours: courseForm.value.experimentHours,
          computer_hours: courseForm.value.computerHours,
          practice_hours: courseForm.value.practiceHours,
          credit: courseForm.value.credit,
          weekly_hours: courseForm.value.weeklyHours,
          is_practical: courseForm.value.isPractical
        }

        console.log('处理后的提交数据:', data)

        const response = isEdit
          ? await updateCourse(courseForm.value.id, data)
          : await createCourse(data);
        
        if (response.success) {
          ElMessage.success(isEdit ? '更新成功' : '创建成功')
          dialogVisible.value = false
          await fetchCourseList()
        } else {
          throw new Error(response.message || '操作失败')
        }
      } catch (error) {
        console.error(isEdit ? '更新课程失败:' : '创建课程失败:', error)
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
const downloadTemplate = () => {
  // 实际项目中，应该提供一个API端点来下载模板
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
    const response = await importCourses(importFile.value)
    if (response.success) {
      ElMessage.success('导入成功')
      importDialogVisible.value = false
      fetchCourseList()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('导入失败:', error)
    ElMessage.error('导入失败，请稍后重试')
  } finally {
    importLoading.value = false
  }
}

// 处理排课
const handleSchedule = () => {
  ElMessage.info('排课功能正在开发中，敬请期待')
}

// 添加排课
const addSchedule = async () => {
  if (!scheduleForm.value.classroomId || !scheduleForm.value.weekday || !scheduleForm.value.period) {
    ElMessage.warning('请填写完整的排课信息')
    return
  }

  try {
    const classroom = classroomOptions.value.find(item => item.value === scheduleForm.value.classroomId)
    const scheduleData: Partial<Course> = {
      id: scheduleForm.value.courseId,
      classroom: classroom?.label || '',
      weekday: scheduleForm.value.weekday,
      timeSlot: `第${scheduleForm.value.period}节`
    }

    const response = await createCourseSchedule(scheduleData as Course)
    if (response.success) {
      ElMessage.success('添加排课成功')
      scheduleList.value.push(response.data!)
      // 清空表单
      scheduleForm.value.classroomId = ''
      scheduleForm.value.weekday = ''
      scheduleForm.value.period = ''
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('添加排课失败:', error)
    ElMessage.error('添加排课失败，请稍后重试')
  }
}

// 移除排课
const removeSchedule = async (index: number) => {
  const schedule = scheduleList.value[index]
  try {
    const response = await deleteCourseSchedule(schedule.id)
    if (response.success) {
      ElMessage.success('删除排课成功')
      scheduleList.value.splice(index, 1)
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('删除排课失败:', error)
    ElMessage.error('删除排课失败，请稍后重试')
  }
}

// 移除学生
const removeStudent = (index: number) => {
  studentList.value.splice(index, 1)
}

// 获取星期文本
const getWeekdayText = (weekday: string) => {
  const weekdayMap: Record<string, string> = {
    '1': '周一',
    '2': '周二',
    '3': '周三',
    '4': '周四',
    '5': '周五'
  }
  return weekdayMap[weekday] || weekday
}

// 提交排课
const submitSchedule = async () => {
  if (scheduleList.value.length === 0) {
    ElMessage.warning('请至少添加一个排课信息')
    return
  }

  scheduleLoading.value = true
  try {
    // 在实际项目中，这里应该是调用API提交所有排课信息
    // 由于已经在添加排课时调用了API，这里只需关闭对话框
    ElMessage.success('排课操作完成')
    scheduleDialogVisible.value = false
  } catch (error) {
    console.error('排课失败:', error)
    ElMessage.error('排课失败，请稍后重试')
  } finally {
    scheduleLoading.value = false
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

// Updating placeholder functions
const createCourseSchedule = async (scheduleData: Course) => {
  // Placeholder implementation
  return { success: true, data: { ...scheduleData, id: 'new-id' }, message: 'Success' };
};

const deleteCourseSchedule = async (scheduleId: string) => {
  // Placeholder implementation
  return { success: true, message: 'Success' }
};

// 获取课程类别标签
const getCategoryLabel = (value: string) => {
  const option = categoryOptions.find(item => item.value === value)
  return option ? option.label : value
}

// 获取部门名称
const getDepartmentName = (departmentId: string) => {
  if (!departmentId) return '';
  const department = departmentOptions.value.find(item => item.value === departmentId);
  return department ? department.label : departmentId;
}

// 组件挂载时初始化
onMounted(() => {
  fetchCourseList()
  fetchDepartments()
  fetchMajors()
  fetchTeachers()
  fetchClassrooms()
})
</script>

<style scoped>
.course-management-container {
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
  min-height: 0; /* 重要：允许flex子项收缩 */
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

.schedule-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.schedule-button {
  font-weight: bold;
  font-size: 14px;
  margin: 0 5px;
}
</style>



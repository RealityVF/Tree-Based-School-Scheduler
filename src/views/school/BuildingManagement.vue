<template>
  <div class="building-management-container">
    <div class="page-header">
      <div class="header-actions">
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon> {{ activeTab === 'building' ? '添加教学楼' : '添加教室' }}
        </el-button>
        <el-button type="success" @click="importDialogVisible = true">
          <el-icon><Upload /></el-icon> 批量导入
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="教学楼管理" name="building">
        <div class="search-card">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="校区">
              <el-select v-model="searchForm.campus" placeholder="选择校区" clearable>
                <el-option
                  v-for="item in campusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="名称">
              <el-input v-model="searchForm.name" placeholder="输入教学楼名称" clearable />
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
            :data="buildingList"
            border
            style="width: 100%"
          >
            <el-table-column prop="id" label="ID" width="180" />
            <el-table-column prop="campus" label="校区" width="120" />
            <el-table-column prop="name" label="教学楼名称" width="150" />
            <el-table-column prop="floors" label="楼层数" width="100" />
            <el-table-column prop="location" label="位置" />
            <el-table-column prop="description" label="描述" show-overflow-tooltip />
            <el-table-column label="操作" width="250" fixed="right">
              <template #default="scope">
                <el-button
                  link
                  type="primary"
                  @click="handleEdit(scope.row)"
                >
                  编辑
                </el-button>
                <el-button
                  link
                  type="danger"
                  @click="handleDelete(scope.row)"
                >
                  删除
                </el-button>
                <el-button
                  link
                  type="success"
                  @click="viewBuildingClassrooms(scope.row)"
                >
                  查看教室
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
      </el-tab-pane>

      <el-tab-pane label="教室管理" name="classroom">
        <div class="classroom-header" v-if="selectedBuilding">
          <el-alert
            type="info"
            :title="`当前教学楼: ${selectedBuilding.name}`"
            :closable="false"
            show-icon
          />
          <el-button size="small" @click="clearSelectedBuilding">返回全部教室</el-button>
        </div>

        <div class="search-card">
          <el-form :inline="true" :model="classroomSearchForm" class="search-form">
            <el-form-item label="教学楼">
              <el-select
                v-model="classroomSearchForm.buildingId"
                placeholder="选择教学楼"
                clearable
                filterable
                @change="handleBuildingChange"
              >
                <el-option
                  v-for="item in buildingList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="名称">
              <el-input v-model="classroomSearchForm.name" placeholder="输入教室名称" clearable />
            </el-form-item>
            <el-form-item label="类型">
              <el-select v-model="classroomSearchForm.type" placeholder="教室类型" clearable>
                <el-option label="普通教室" value="普通教室" />
                <el-option label="多媒体教室" value="多媒体教室" />
                <el-option label="计算机教室" value="计算机教室" />
                <el-option label="实验室" value="实验室" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleClassroomSearch">
                <el-icon><Search /></el-icon> 搜索
              </el-button>
              <el-button @click="handleClassroomReset">
                <el-icon><Refresh /></el-icon> 重置
              </el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="table-card">
          <el-table
            v-loading="classroomLoading"
            :data="classroomList"
            border
            style="width: 100%"
          >
            <el-table-column prop="id" label="ID" width="180" />
            <el-table-column prop="name" label="教室名称" width="150" />
            <el-table-column prop="buildingName" label="所属教学楼" width="150" />
            <el-table-column prop="floor" label="楼层" width="80" />
            <el-table-column prop="capacity" label="容量" width="80" />
            <el-table-column prop="type" label="类型" width="120" />
            <el-table-column label="多媒体设备" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.hasMultimedia ? 'success' : 'info'">
                  {{ scope.row.hasMultimedia ? '有' : '无' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" show-overflow-tooltip />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-button
                  link
                  type="primary"
                  @click="handleClassroomEdit(scope.row)"
                >
                  编辑
                </el-button>
                <el-button
                  link
                  type="danger"
                  @click="handleClassroomDelete(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-model:current-page="classroomCurrentPage"
              v-model:page-size="classroomPageSize"
              :total="classroomTotal"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleClassroomSizeChange"
              @current-change="handleClassroomCurrentChange"
            />
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 教学楼添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ?
        (activeTab === 'building' ? '编辑教学楼' : '编辑教室') :
        (activeTab === 'building' ? '添加教学楼' : '添加教室')"
      width="500px"
    >
      <!-- 教学楼表单 -->
      <el-form
        v-if="activeTab === 'building'"
        ref="buildingFormRef"
        :model="buildingForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="校区" prop="campus">
          <el-select v-model="buildingForm.campus" placeholder="请选择校区">
            <el-option
              v-for="item in campusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="buildingForm.name" placeholder="请输入教学楼名称" />
        </el-form-item>
        <el-form-item label="楼层数" prop="floors">
          <el-input-number v-model="buildingForm.floors" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="buildingForm.location" placeholder="请输入教学楼位置" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="buildingForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入教学楼描述信息"
          />
        </el-form-item>
      </el-form>

      <!-- 教室表单 -->
      <el-form
        v-else
        ref="classroomFormRef"
        :model="classroomForm"
        :rules="classroomFormRules"
        label-width="100px"
      >
        <el-form-item label="所属教学楼" prop="buildingId">
          <el-select
            v-model="classroomForm.buildingId"
            placeholder="请选择教学楼"
            filterable
            @change="onClassroomBuildingChange"
          >
            <el-option
              v-for="item in buildingList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教室名称" prop="name">
          <el-input v-model="classroomForm.name" placeholder="请输入教室名称" />
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number
            v-model="classroomForm.floor"
            :min="1"
            :max="selectedBuildingFloors || 100"
            :disabled="!classroomForm.buildingId"
          />
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number v-model="classroomForm.capacity" :min="1" :max="500" />
        </el-form-item>
        <el-form-item label="教室类型" prop="type">
          <el-select v-model="classroomForm.type" placeholder="请选择教室类型">
            <el-option label="普通教室" value="普通教室" />
            <el-option label="多媒体教室" value="多媒体教室" />
            <el-option label="计算机教室" value="计算机教室" />
            <el-option label="实验室" value="实验室" />
          </el-select>
        </el-form-item>
        <el-form-item label="多媒体设备" prop="hasMultimedia">
          <el-switch v-model="classroomForm.hasMultimedia" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="classroomForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入教室描述信息"
          />
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
      :title="activeTab === 'building' ? '批量导入教学楼' : '批量导入教室'"
      width="500px"
    >
      <el-form v-if="activeTab === 'classroom'" label-width="100px">
        <el-form-item label="所属教学楼" required>
          <el-select
            v-model="importBuildingId"
            placeholder="请选择教学楼"
            filterable
          >
            <el-option
              v-for="item in buildingList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>

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
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, UploadFilled, Upload, Download } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import { getBuildings, createBuilding, updateBuilding, deleteBuilding } from '@/api/school'
import { getClassrooms, createClassroom, updateClassroom, deleteClassroom } from '@/api/school'
import type { Building, Classroom } from '@/api/school'

const router = useRouter()
const route = useRoute()

// 标签页控制
const activeTab = ref('building')

// 教学楼数据
const loading = ref(false)
const buildingList = ref<Building[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 教室数据
const classroomLoading = ref(false)
const classroomList = ref<Classroom[]>([])
const classroomTotal = ref(0)
const classroomCurrentPage = ref(1)
const classroomPageSize = ref(10)
const selectedBuilding = ref<Building | null>(null)
const selectedBuildingFloors = computed(() => selectedBuilding.value?.floors || 100)

// 表单引用
const buildingFormRef = ref<FormInstance>()
const classroomFormRef = ref<FormInstance>()

// 校区选项
const campusOptions = [
  { value: '主校区', label: '主校区' },
  { value: '东校区', label: '东校区' },
  { value: '西校区', label: '西校区' },
  { value: '南校区', label: '南校区' },
  { value: '北校区', label: '北校区' }
]

// 搜索表单 - 教学楼
const searchForm = reactive({
  name: '',
  campus: ''
})

// 搜索表单 - 教室
const classroomSearchForm = reactive({
  buildingId: '',
  name: '',
  type: ''
})

// 添加/编辑表单 - 教学楼
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const buildingForm = reactive({
  id: '',
  campus: '',
  name: '',
  floors: 1,
  location: '',
  description: ''
})

// 添加/编辑表单 - 教室
const classroomForm = reactive({
  id: '',
  buildingId: '',
  name: '',
  floor: 1,
  capacity: 30,
  type: '普通教室',
  hasMultimedia: false,
  description: ''
})

// 导入相关
const importDialogVisible = ref(false)
const importLoading = ref(false)
const importFile = ref<File | null>(null)
const importBuildingId = ref('')

// 表单验证规则 - 教学楼
const formRules = {
  campus: [
    { required: true, message: '请选择校区', trigger: 'change' }
  ],
  name: [
    { required: true, message: '请输入教学楼名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  floors: [
    { required: true, message: '请输入楼层数', trigger: 'blur' },
    { type: 'number', min: 1, message: '楼层数必须大于0', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入教学楼位置', trigger: 'blur' }
  ]
}

// 表单验证规则 - 教室
const classroomFormRules = {
  buildingId: [
    { required: true, message: '请选择所属教学楼', trigger: 'change' }
  ],
  name: [
    { required: true, message: '请输入教室名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  floor: [
    { required: true, message: '请输入楼层', trigger: 'change' }
  ],
  capacity: [
    { required: true, message: '请输入容量', trigger: 'change' }
  ],
  type: [
    { required: true, message: '请选择教室类型', trigger: 'change' }
  ]
}

// 获取教学楼列表
const fetchBuildings = async () => {
  loading.value = true
  try {
    // 这里应该根据后端实际情况调整，可能需要处理分页
    const response = await getBuildings()
    if (response.success) {
      buildingList.value = response.data
      total.value = response.data.length
    } else {
      ElMessage.error(response.message || '获取教学楼列表失败')
    }
  } catch (error) {
    console.error('获取教学楼列表失败:', error)
    ElMessage.error('获取教学楼列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 搜索教学楼
const handleSearch = () => {
  // 在实际开发中，可能需要根据查询条件调用接口
  // 这里简单实现为前端过滤
  const filteredList = buildingList.value.filter(item => {
    const nameMatch = !searchForm.name || item.name.includes(searchForm.name)
    const campusMatch = !searchForm.campus || item.campus === searchForm.campus
    return nameMatch && campusMatch
  })
  buildingList.value = filteredList
}

// 重置教学楼搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.campus = ''
}

const handleReset = () => {
  resetSearch()
  fetchBuildings()
}

// 分页处理 - 教学楼
const resetPagination = () => {
  currentPage.value = 1
  pageSize.value = 10
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchBuildings()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchBuildings()
}

// 显示添加对话框
const showAddDialog = () => {
  isEdit.value = false

  if (activeTab.value === 'building') {
    buildingForm.id = ''
    buildingForm.campus = ''
    buildingForm.name = ''
    buildingForm.floors = 1
    buildingForm.location = ''
    buildingForm.description = ''
  } else {
    classroomForm.id = ''
    classroomForm.buildingId = classroomSearchForm.buildingId || ''
    classroomForm.name = ''
    classroomForm.floor = 1
    classroomForm.capacity = 30
    classroomForm.type = '普通教室'
    classroomForm.hasMultimedia = false
    classroomForm.description = ''
  }

  dialogVisible.value = true
}

// 处理教学楼编辑
const handleEdit = (row: Building) => {
  isEdit.value = true
  buildingForm.id = row.id
  buildingForm.campus = row.campus
  buildingForm.name = row.name
  buildingForm.floors = row.floors
  buildingForm.location = row.location
  buildingForm.description = row.description || ''
  dialogVisible.value = true
}

// 处理教学楼删除
const handleDelete = async (row: Building) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除教学楼 "${row.name}" 吗？删除后将无法恢复，且关联的教室也可能被删除`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await deleteBuilding(row.id)
    if (response.success) {
      ElMessage.success('删除成功')
      fetchBuildings()
      // 如果删除的是当前选中的教学楼，清除选择
      if (selectedBuilding.value && selectedBuilding.value.id === row.id) {
        clearSelectedBuilding()
      }
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除教学楼失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

// 提交表单
const submitForm = async () => {
  if (activeTab.value === 'building') {
    await submitBuildingForm()
  } else {
    await submitClassroomForm()
  }
}

// 提交教学楼表单
const submitBuildingForm = async () => {
  if (!buildingFormRef.value) return

  await buildingFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          // 更新
          const updateData = {
            campus: buildingForm.campus,
            name: buildingForm.name,
            floors: buildingForm.floors,
            location: buildingForm.location,
            description: buildingForm.description
          }
          const response = await updateBuilding(buildingForm.id, updateData)
          if (response.success) {
            ElMessage.success('更新成功')
            dialogVisible.value = false
            fetchBuildings()
          } else {
            ElMessage.error(response.message || '更新失败')
          }
        } else {
          // 创建
          const createData = {
            campus: buildingForm.campus,
            name: buildingForm.name,
            floors: buildingForm.floors,
            location: buildingForm.location,
            description: buildingForm.description
          }
          const response = await createBuilding(createData)
          if (response.success) {
            ElMessage.success('创建成功')
            dialogVisible.value = false
            fetchBuildings()
          } else {
            ElMessage.error(response.message || '创建失败')
          }
        }
      } catch (error) {
        console.error(isEdit.value ? '更新教学楼失败:' : '创建教学楼失败:', error)
        ElMessage.error(isEdit.value ? '更新失败，请稍后重试' : '创建失败，请稍后重试')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 查看教学楼下的教室
const viewBuildingClassrooms = (row: Building) => {
  selectedBuilding.value = row
  activeTab.value = 'classroom'
  classroomSearchForm.buildingId = row.id
  fetchClassrooms()
}

// 处理文件变更
const handleFileChange = (file: { raw: File }) => {
  importFile.value = file.raw
}

// 下载导入模板
const downloadTemplate = () => {
  // 根据当前标签页决定下载哪个模板
  const templateName = activeTab.value === 'building' ? '教学楼导入模板.xlsx' : '教室导入模板.xlsx'

  // 实际项目中，应该提供一个API端点来下载模板
  // 这里仅作演示，实际应该从服务器下载
  ElMessage.info(`下载${templateName}功能待实现`)

  // 模拟示例：
  // const link = document.createElement('a')
  // link.href = `/api/template/${activeTab.value}`
  // link.download = templateName
  // document.body.appendChild(link)
  // link.click()
  // document.body.removeChild(link)
}

// 提交导入
const submitImport = async () => {
  if (!importFile.value) {
    ElMessage.warning('请选择要导入的文件')
    return
  }

  // 对于教室导入，需要选择教学楼
  if (activeTab.value === 'classroom' && !importBuildingId.value) {
    ElMessage.warning('请选择所属教学楼')
    return
  }

  importLoading.value = true
  try {
    // 这里应该实现批量导入逻辑
    // 由于需要后端支持，这里只是模拟
    setTimeout(() => {
      ElMessage.success('导入成功')
      importDialogVisible.value = false

      if (activeTab.value === 'building') {
        fetchBuildings()
      } else {
        fetchClassrooms()
      }

      importLoading.value = false
    }, 1500)
  } catch (error) {
    console.error('导入失败:', error)
    ElMessage.error('导入失败，请稍后重试')
    importLoading.value = false
  }
}

// 处理标签页切换
const handleTabClick = () => {
  // 标签页切换时重置搜索表单和分页
  if (activeTab.value === 'building') {
    resetSearch()
    resetPagination()
    fetchBuildings()
  } else {
    handleClassroomReset()
    resetClassroomPagination()
    fetchClassrooms()
  }
}

// 获取教室列表
const fetchClassrooms = async () => {
  classroomLoading.value = true
  try {
    const buildingId = classroomSearchForm.buildingId
    const response = await getClassrooms(buildingId)
    if (response.success) {
      // 如果有搜索条件，在前端过滤
      let filteredList = response.data

      if (classroomSearchForm.name) {
        filteredList = filteredList.filter(item =>
          item.name.includes(classroomSearchForm.name)
        )
      }

      if (classroomSearchForm.type) {
        filteredList = filteredList.filter(item =>
          item.type === classroomSearchForm.type
        )
      }

      classroomList.value = filteredList
      classroomTotal.value = filteredList.length
    } else {
      ElMessage.error(response.message || '获取教室列表失败')
    }
  } catch (error) {
    console.error('获取教室列表失败:', error)
    ElMessage.error('获取教室列表失败，请稍后重试')
  } finally {
    classroomLoading.value = false
  }
}

// 当选择教学楼时更新选中的教学楼信息
const handleBuildingChange = (buildingId: string) => {
  if (!buildingId) {
    selectedBuilding.value = null
    return
  }

  const building = buildingList.value.find(item => item.id === buildingId)
  if (building) {
    selectedBuilding.value = building
  }

  fetchClassrooms()
}

// 清除选中的教学楼
const clearSelectedBuilding = () => {
  selectedBuilding.value = null
  classroomSearchForm.buildingId = ''
  fetchClassrooms()
}

// 搜索教室
const handleClassroomSearch = () => {
  fetchClassrooms()
}

// 重置教室搜索
const handleClassroomReset = () => {
  // 保留buildingId，只重置其他搜索条件
  const buildingId = classroomSearchForm.buildingId
  classroomSearchForm.name = ''
  classroomSearchForm.type = ''
  classroomSearchForm.buildingId = buildingId

  fetchClassrooms()
}

// 分页处理 - 教室
const resetClassroomPagination = () => {
  classroomCurrentPage.value = 1
  classroomPageSize.value = 10
}

const handleClassroomSizeChange = (val: number) => {
  classroomPageSize.value = val
  fetchClassrooms()
}

const handleClassroomCurrentChange = (val: number) => {
  classroomCurrentPage.value = val
  fetchClassrooms()
}

// 处理教室编辑
const handleClassroomEdit = (row: Classroom) => {
  isEdit.value = true

  classroomForm.id = row.id
  classroomForm.buildingId = row.buildingId
  classroomForm.name = row.name
  classroomForm.floor = row.floor
  classroomForm.capacity = row.capacity
  classroomForm.type = row.type
  classroomForm.hasMultimedia = row.hasMultimedia
  classroomForm.description = row.description || ''

  // 更新当前选中的教学楼信息
  const building = buildingList.value.find(item => item.id === row.buildingId)
  if (building) {
    selectedBuilding.value = building
  }

  dialogVisible.value = true
}

// 处理教室删除
const handleClassroomDelete = async (row: Classroom) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除教室 "${row.name}" 吗？删除后将无法恢复`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const response = await deleteClassroom(row.id)
    if (response.success) {
      ElMessage.success('删除成功')
      fetchClassrooms()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除教室失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

// 当教室表单中选择教学楼时
const onClassroomBuildingChange = (buildingId: string) => {
  if (!buildingId) return

  const building = buildingList.value.find(item => item.id === buildingId)
  if (building) {
    selectedBuilding.value = building
    // 根据教学楼楼层数重置当前楼层
    if (classroomForm.floor > building.floors) {
      classroomForm.floor = building.floors
    }
  }
}

// 提交教室表单
const submitClassroomForm = async () => {
  if (!classroomFormRef.value) return

  await classroomFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          // 更新
          const updateData = {
            buildingId: classroomForm.buildingId,
            name: classroomForm.name,
            floor: classroomForm.floor,
            capacity: classroomForm.capacity,
            type: classroomForm.type,
            hasMultimedia: classroomForm.hasMultimedia,
            description: classroomForm.description
          }
          const response = await updateClassroom(classroomForm.id, updateData)
          if (response.success) {
            ElMessage.success('更新成功')
            dialogVisible.value = false
            fetchClassrooms()
          } else {
            ElMessage.error(response.message || '更新失败')
          }
        } else {
          // 创建
          const createData = {
            buildingId: classroomForm.buildingId,
            name: classroomForm.name,
            floor: classroomForm.floor,
            capacity: classroomForm.capacity,
            type: classroomForm.type,
            hasMultimedia: classroomForm.hasMultimedia,
            description: classroomForm.description
          }
          const response = await createClassroom(createData)
          if (response.success) {
            ElMessage.success('创建成功')
            dialogVisible.value = false
            fetchClassrooms()
          } else {
            ElMessage.error(response.message || '创建失败')
          }
        }
      } catch (error) {
        console.error(isEdit.value ? '更新教室失败:' : '创建教室失败:', error)
        ElMessage.error(isEdit.value ? '更新失败，请稍后重试' : '创建失败，请稍后重试')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 监听路由查询参数
watch(() => route.query, (query) => {
  if (query.buildingId && query.buildingName) {
    activeTab.value = 'classroom'
    classroomSearchForm.buildingId = query.buildingId as string
    selectedBuilding.value = {
      id: query.buildingId as string,
      name: query.buildingName as string,
      floors: 1,
      location: '',
      description: ''
    }
    fetchClassrooms()
  }
}, { immediate: true })

// 组件挂载时初始化
onMounted(async () => {
  // 首先获取教学楼列表
  await fetchBuildings()

  // 如果路由参数中有教学楼信息，切换到教室管理标签页
  if (route.query.buildingId) {
    activeTab.value = 'classroom'
    classroomSearchForm.buildingId = route.query.buildingId as string
    importBuildingId.value = route.query.buildingId as string

    // 查找完整的教学楼信息
    const building = buildingList.value.find(b => b.id === route.query.buildingId)
    if (building) {
      selectedBuilding.value = building
    } else if (route.query.buildingName) {
      // 如果没找到，但有名称，创建一个简单的对象
      selectedBuilding.value = {
        id: route.query.buildingId as string,
        name: route.query.buildingName as string,
        floors: 1,
        location: '',
        description: ''
      }
    }

    fetchClassrooms()
  }
})
</script>

<style scoped>
.building-management-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-card {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 24px;
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.table-card {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.classroom-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.template-download {
  margin-top: 10px;
  text-align: right;
}

.import-area {
  margin-bottom: 20px;
}
</style>

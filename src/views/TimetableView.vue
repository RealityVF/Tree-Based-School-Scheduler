<template>
  <div class="timetable-container">
    <div class="timetable-header">
      <h2 class="title">课表查询</h2>
      <div class="query-form">
        <el-form :inline="true" :model="queryForm" size="default">
          <!-- 管理员可以切换查询类型 -->
          <el-form-item v-if="userStore.isAdmin">
            <div class="type-switch">
              <el-button
                :type="queryForm.type === 'student' ? 'primary' : 'default'"
                @click="switchType('student')"
              >
                学生课表
              </el-button>
              <el-button
                :type="queryForm.type === 'teacher' ? 'primary' : 'default'"
                @click="switchType('teacher')"
              >
                教师课表
              </el-button>
            </div>
          </el-form-item>

          <!-- 学生课表查询表单 -->
          <template v-if="(userStore.isAdmin && queryForm.type === 'student') || userStore.isStudent">
            <el-form-item label="专业" v-if="userStore.isAdmin">
              <el-select
                v-model="queryForm.majorId"
                placeholder="请选择专业"
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
            <el-form-item label="班级" v-if="userStore.isAdmin">
              <el-select
                v-model="queryForm.classId"
                placeholder="请选择班级"
                style="width: 180px"
                :disabled="!queryForm.majorId"
              >
                <el-option
                  v-for="item in classOptions"
                  :key="item.classId"
                  :label="item.className"
                  :value="item.classId"
                />
              </el-select>
            </el-form-item>
          </template>

          <!-- 教师课表查询表单 -->
          <template v-else-if="(userStore.isAdmin && queryForm.type === 'teacher') || userStore.isTeacher">
            <el-form-item label="教师" v-if="userStore.isAdmin">
              <el-select
                v-model="queryForm.teacherId"
                filterable
                remote
                :remote-method="remoteSearchTeachers"
                :loading="isSearchingTeacher"
                placeholder="输入教师姓名搜索"
                class="filter-item"
                clearable
                @change="handleTeacherSelect"
              >
                <el-option
                  v-for="item in teacherOptions"
                  :key="item.id"
                  :label="`${item.name}${item.code ? ` (${item.code})` : ''}`"
                  :value="item.id"
                />
                <template #empty>
                  <el-empty
                    v-if="searchStatus === 'no-results'"
                    description="未找到匹配的教师"
                  />
                  <el-empty
                    v-else-if="searchStatus === 'error'"
                    description="搜索出错，请重试"
                  />
                  <span v-else-if="searchStatus === 'searching'">
                    搜索中...
                  </span>
                  <span v-else>
                    请输入教师姓名搜索
                  </span>
                </template>
              </el-select>
            </el-form-item>
          </template>

          <el-form-item label="学年">
            <el-select
              v-model="queryForm.academicYear"
              placeholder="请选择学年"
              style="width: 180px"
            >
              <el-option
                v-for="item in getAcademicYearOptions()"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="学期">
            <el-select
              v-model="queryForm.semester"
              placeholder="请选择学期"
              style="width: 180px"
            >
              <el-option
                v-for="item in termOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="周次">
            <el-select
              v-model="queryForm.weekNumber"
              placeholder="请选择周次"
              style="width: 120px"
              clearable
            >
              <el-option label="整个学期" :value="null" />
              <el-option
                v-for="i in 20"
                :key="i"
                :label="`第${i}周`"
                :value="i"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              @click="handleQuery"
              :loading="loading"
            >
              查询
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="timetable-content">
      <div class="schedule-wrapper" v-loading="loading">
        <div class="schedule-grid">
          <!-- 表头 -->
          <div class="schedule-header">
            <div class="time-column">时间</div>
            <div class="day-column" v-for="day in 7" :key="day">
              {{ weekDayMap[day] }}
            </div>
          </div>

          <!-- 时间段 -->
          <div class="schedule-body">
            <div class="time-row" v-for="timeSlot in timeSlots" :key="timeSlot.key">
              <div class="time-column">
                {{ timeSlot.start }}<br>{{ timeSlot.end }}
              </div>
              <div
                class="schedule-cell"
                v-for="day in 7"
                :key="day"
              >
                <template v-for="(course, index) in getCoursesByTime(day, timeSlot.key)" :key="course.courseId">
                  <div
                    class="course-card"
                    :class="{
                      'has-multiple-courses': getCoursesByTime(day, timeSlot.key).length > 1 && index === 0
                    }"
                    @click="showCourseDetail(course)"
                  >
                    <div class="course-name">{{ course.courseName }}</div>
                    <div class="course-info">{{ course.teacherName }}</div>
                    <div class="course-info">{{ course.classroomName }}</div>
                    <div class="course-info">{{ course.weekNumberDisplay }}</div>
                    <div v-if="getCoursesByTime(day, timeSlot.key).length > 1 && index === 0" class="multi-course-indicator">
                      多课程 ({{ getCoursesByTime(day, timeSlot.key).length }}门)
                    </div>
                  </div>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="empty-text" v-if="!loading && tableData.length === 0">
        暂无课程数据
      </div>
    </div>

    <!-- 课程详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedCourse?.courseName"
      width="500px"
    >
      <template v-if="selectedCourse">
        <div class="course-detail">
          <p><strong>课程编号：</strong>{{ selectedCourse.courseId }}</p>
          <p><strong>课程类型：</strong>{{ selectedCourse.courseType }}</p>
          <p><strong>授课教师：</strong>{{ selectedCourse.teacherName }}</p>
          <p><strong>上课地点：</strong>{{ selectedCourse.classroomName }}</p>
          <p><strong>上课时间：</strong>{{ weekDayMap[selectedCourse.weekday] }} {{ selectedCourse.startTime }}-{{ selectedCourse.endTime }}</p>
          <p><strong>周次：</strong>{{ selectedCourse.weekNumberDisplay || (selectedCourse.weekNumber ? `第${selectedCourse.weekNumber}周` : '全学期') }}</p>
          <p><strong>学分：</strong>{{ selectedCourse.credits ?? '未设置' }}</p>
          <p><strong>课程描述：</strong>{{ selectedCourse.description || '暂无描述' }}</p>
          <p v-if="selectedCourse.totalStudents !== undefined && selectedCourse.maxStudents !== undefined">
            <strong>选课人数：</strong>{{ selectedCourse.totalStudents }}/{{ selectedCourse.maxStudents }}
          </p>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { CourseSchedule, Major, Class, Teacher, TeacherTimetableQuery, StudentTimetableQuery } from '@/types/timetable'
import { getStudentSchedule, getTeacherSchedule, getMajors, getClasses, searchTeachers } from '@/api/timetable'
import { useUserStore } from '@/stores/counter'

const loading = ref(false)
const tableData = ref<CourseSchedule[]>([])
const dialogVisible = ref(false)
const selectedCourse = ref<CourseSchedule | null>(null)

const majorOptions = ref<Major[]>([])
const classOptions = ref<Class[]>([])

const userStore = useUserStore()

const queryForm = reactive({
  type: userStore.isTeacher ? 'teacher' : 'student',
  majorId: '',
  classId: '',
  teacherId: '',
  teacherQuery: '',
  academicYear: '2024-2025',
  semester: 1 as number,
  weekNumber: undefined as number | undefined
})

const teacherOptions = ref<Teacher[]>([])
const isSearchingTeacher = ref(false)
const searchStatus = ref<'idle' | 'searching' | 'no-results' | 'error'>('idle')

const weekDayMap: Record<number, string> = {
  1: '周一',
  2: '周二',
  3: '周三',
  4: '周四',
  5: '周五',
  6: '周六',
  7: '周日'
}

// 时间段定义
const timeSlots = [
  { start: '08:00', end: '09:35', key: '08:00-09:35' as TimeSlotKey },  // 第1-2节课
  { start: '10:05', end: '11:40', key: '10:05-11:40' as TimeSlotKey },  // 第3-4节课
  { start: '14:00', end: '15:35', key: '14:00-15:35' as TimeSlotKey },  // 第5-6节课
  { start: '16:05', end: '17:40', key: '16:05-17:40' as TimeSlotKey },  // 第7-8节课
  { start: '19:00', end: '20:35', key: '19:00-20:35' as TimeSlotKey }   // 第9-10节课
]

type TimeSlotKey = string;

// 根据时间段和星期几获取课程
const getCoursesByTime = (weekday: number, timeSlot: TimeSlotKey): CourseSchedule[] => {
  console.log(`开始查找时间槽 ${timeSlot} 的课程 - 星期${weekday}`);
  const [startTime, endTime] = timeSlot.split('-');
  console.log('查询参数:', { weekday, startTime, endTime });

  // 过滤出符合条件的课程
  const matchingCourses = tableData.value.filter(course => {
    // 检查星期是否匹配
    if (course.weekday !== weekday) {
      return false;
    }

    // 优先使用节次匹配
    if (course.startSection && course.endSection) {
      // 获取当前时间槽对应的节次范围
      const timeSlotSections = {
        '08:00-09:35': { start: 1, end: 2 },
        '10:05-11:40': { start: 3, end: 4 },
        '14:00-15:35': { start: 5, end: 6 },
        '16:05-17:40': { start: 7, end: 8 },
        '19:00-20:35': { start: 9, end: 10 }
      };

      const slotSections = timeSlotSections[timeSlot as keyof typeof timeSlotSections];
      if (!slotSections) return false;

      // 检查课程节次是否与时间槽节次匹配
      if (course.startSection === slotSections.start && course.endSection === slotSections.end) {
        return true;
      }

      // 检查是否有重叠
      if (course.startSection <= slotSections.end && course.endSection >= slotSections.start) {
        return true;
      }
    }

    // 如果没有节次信息，使用时间匹配
    if (course.startTime && course.endTime) {
      const courseStartTime = course.startTime.substring(0, 5);
      const courseEndTime = course.endTime.substring(0, 5);
      return courseStartTime === startTime && courseEndTime === endTime;
    }

    return false;
  });

  console.log(`【时间槽 ${timeSlot}】找到 ${matchingCourses.length} 门匹配的课程:`, matchingCourses);

  // 如果没有找到匹配的课程，直接返回空数组
  if (matchingCourses.length === 0) {
    return [];
  }

  // 合并相同课程的周次
  const mergedMap = new Map();

  for (const course of matchingCourses) {
    // 创建唯一的课程标识符
    const courseKey = `${course.courseId}-${course.teacherId}-${course.classroomId}`;

    if (mergedMap.has(courseKey)) {
      // 如果已存在，合并周次
      const existing = mergedMap.get(courseKey);
      if (course.weekNumber) {
        existing.weekNumbers = existing.weekNumbers || [];
        if (!existing.weekNumbers.includes(course.weekNumber)) {
          existing.weekNumbers.push(course.weekNumber);
          // 确保周次排序
          existing.weekNumbers.sort((a: number, b: number) => a - b);
        }
      }
    } else {
      // 否则，添加新课程
      mergedMap.set(courseKey, {
        ...course,
        weekNumbers: course.weekNumber ? [course.weekNumber] : []
      });
    }
  }

  // 将合并后的课程转为数组，并添加格式化的周次显示
  const finalCourses = Array.from(mergedMap.values()).map(course => ({
    ...course,
    weekNumberDisplay: formatWeekNumbers(course.weekNumbers || [])
  }));

  console.log(`【时间槽 ${timeSlot}】合并后返回 ${finalCourses.length} 门课程:`, finalCourses);
  return finalCourses;
};

// 格式化周次显示
const formatWeekNumbers = (weekNumbers: number[]) => {
  if (!weekNumbers.length) return ''

  // 对周次进行排序
  weekNumbers.sort((a, b) => a - b)

  // 检查是否所有周次都连续且从1到16
  if (weekNumbers.length === 16 &&
      weekNumbers[0] === 1 &&
      weekNumbers[weekNumbers.length - 1] === 16 &&
      weekNumbers.every((num, index) => index === 0 || num === weekNumbers[index - 1] + 1)) {
    return '第1-16周'
  }

  // 合并连续的周次
  const ranges: string[] = []
  let start = weekNumbers[0]
  let prev = weekNumbers[0]

  for (let i = 1; i <= weekNumbers.length; i++) {
    const current = i < weekNumbers.length ? weekNumbers[i] : null

    // 如果到达末尾或者下一个数不连续
    if (current === null || current !== prev + 1) {
      if (start === prev) {
        // 单个周次
        ranges.push(`${start}`)
      } else {
        // 连续周次
        ranges.push(`${start}-${prev}`)
      }
      if (current !== null) {
        start = current
        prev = current
      }
    } else {
      prev = current
    }
  }

  return `第${ranges.join(',')}周`
}

// 切换查询类型
const switchType = (type: 'student' | 'teacher') => {
  queryForm.type = type
  tableData.value = []
  if (type === 'student') {
    queryForm.teacherQuery = ''
    queryForm.teacherId = ''
    teacherOptions.value = []
  } else {
    queryForm.majorId = ''
    queryForm.classId = ''
  }
}

// 初始化函数
const initOptions = async () => {
  try {
    loading.value = true
    const majors = await getMajors()
    console.log('获取到的专业列表:', majors)
    majorOptions.value = majors
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('获取专业列表失败: ' + error.message)
    } else {
      ElMessage.error('获取专业列表失败')
    }
  } finally {
    loading.value = false
  }
}

// 专业变更处理函数
const handleMajorChange = async (majorId: string) => {
  console.log('选择的专业ID:', majorId)
  if (!majorId) {
    classOptions.value = []
    queryForm.classId = ''
    return
  }

  try {
    loading.value = true
    const classes = await getClasses(majorId)
    console.log('获取到的班级列表:', classes)
    classOptions.value = classes
    queryForm.classId = ''
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('获取班级列表失败: ' + error.message)
    } else {
      ElMessage.error('获取班级列表失败')
    }
  } finally {
    loading.value = false
  }
}

// 显示课程详情
const showCourseDetail = (row: CourseSchedule) => {
  selectedCourse.value = row
  dialogVisible.value = true
}

// 生成学年选项
const getAcademicYearOptions = () => {
  const currentYear = new Date().getFullYear()
  const options = []
  // 生成最近5个学年的选项
  for (let i = 0; i < 5; i++) {
    const year = currentYear - i
    options.push({
      label: `${year}-${year + 1}`,
      value: `${year}-${year + 1}`
    })
  }
  return options
}

// 学期选项
const termOptions = [
  { label: '第一学期', value: 1 },
  { label: '第二学期', value: 2 }
]

// 防抖函数类型定义
type DebouncedFunction<T> = T extends (...args: infer P) => infer R ? (...args: P) => R : never

// 防抖函数
const debounce = <T extends (...args: Parameters<T>) => ReturnType<T>>(
  fn: T,
  delay: number
): DebouncedFunction<T> => {
  let timer: number | undefined

  const debouncedFn = (...args: Parameters<T>) => {
    if (timer) clearTimeout(timer)
    timer = window.setTimeout(() => {
      void fn(...args)
    }, delay)
  }

  return debouncedFn as DebouncedFunction<T>
}

// 类型定义
type SearchFunction = (query: string) => Promise<void>

// 基础的教师搜索函数
const searchTeachersBase = async (query: string): Promise<void> => {
  console.log('Starting teacher search with query:', query)

  // 处理空查询
  if (!query || !query.trim()) {
    teacherOptions.value = []
    searchStatus.value = 'idle'
    console.log('Empty query, clearing teacher options')
    return
  }

  try {
    searchStatus.value = 'searching'
    isSearchingTeacher.value = true
    console.log('Calling searchTeachers API with query:', query)

    const results = await searchTeachers(query.trim())
    console.log('Received teacher search results:', results)

    teacherOptions.value = results
    searchStatus.value = results.length > 0 ? 'idle' : 'no-results'

    if (results.length === 0) {
      ElMessage.info('未找到匹配的教师')
    }
  } catch (error) {
    console.error('Teacher search failed:', error)
    searchStatus.value = 'error'
    teacherOptions.value = []
    ElMessage.error(error instanceof Error ? error.message : '搜索教师失败')
  } finally {
    isSearchingTeacher.value = false
  }
}

// 使用防抖包装的搜索函数
const remoteSearchTeachers: SearchFunction = debounce(searchTeachersBase, 300)

// 处理教师选择变化
const handleTeacherSelect = (value: string) => {
  console.log('Teacher selection changed:', value)
  if (!value) {
    queryForm.teacherId = ''
    queryForm.teacherQuery = ''
  } else {
    const selectedTeacher = teacherOptions.value.find(t => t.id === value)
    if (selectedTeacher) {
      queryForm.teacherId = selectedTeacher.id
      queryForm.teacherQuery = selectedTeacher.name
      console.log('Updated query form:', queryForm)
    }
  }
}

// 查询课表
const handleQuery = async () => {
  try {
    loading.value = true
    let scheduleData: CourseSchedule[] = []

    // 详细记录用户和存储状态
    console.log('=== 开始查询课表 ===');
    console.log('用户状态:', {
      isAdmin: userStore.isAdmin,
      isStudent: userStore.isStudent,
      isTeacher: userStore.isTeacher,
      userInfo: userStore.userInfo,
      token: userStore.token
    });

    if ((userStore.isAdmin && queryForm.type === 'student') || userStore.isStudent) {
      // 学生课表查询
      if (!queryForm.classId && userStore.isAdmin) {
        ElMessage.warning('请选择班级')
        return
      }
      const classId = userStore.isAdmin ? queryForm.classId : userStore.userInfo?.classId

      // 详细记录班级信息
      console.log('班级信息详情:', {
        fromAdmin: userStore.isAdmin ? queryForm.classId : '非管理员',
        fromUserInfo: userStore.userInfo?.classId,
        finalClassId: classId,
        queryForm: queryForm
      });

      if (!classId) {
        console.warn('无法获取班级信息，用户信息:', userStore.userInfo);
        ElMessage.warning('无法获取班级信息')
        return
      }

      // 验证学年和学期
      if (!queryForm.academicYear) {
        ElMessage.warning('请选择学年')
        return
      }
      if (!queryForm.semester) {
        ElMessage.warning('请选择学期')
        return
      }

      const params: StudentTimetableQuery = {
        classId,
        academicYear: queryForm.academicYear,
        semester: queryForm.semester,
        weekNumber: queryForm.weekNumber
      }
      console.log('发送学生课表查询请求，参数:', params)

      try {
        console.log('开始调用 getStudentSchedule API...');
        scheduleData = await getStudentSchedule(params)
        console.log('收到学生课表响应数据:', scheduleData)

        // 检查返回的数据结构
        if (scheduleData.length > 0) {
          console.log('第一条课程数据示例:', {
            课程名称: scheduleData[0].courseName,
            教师: scheduleData[0].teacherName,
            星期: scheduleData[0].weekday,
            节次: `${scheduleData[0].startSection}-${scheduleData[0].endSection}`,
            时间: `${scheduleData[0].startTime}-${scheduleData[0].endTime}`,
            周次: scheduleData[0].weekNumber
          });
        }
      } catch (error) {
        console.error('获取学生课表失败:', error)
        throw error
      }
    } else if ((userStore.isAdmin && queryForm.type === 'teacher') || userStore.isTeacher) {
      // 教师课表查询
      if (!queryForm.teacherId && userStore.isAdmin) {
        ElMessage.warning('请选择教师')
        return
      }
      const params: TeacherTimetableQuery = {
        teacherId: userStore.isAdmin ? queryForm.teacherId : userStore.userInfo?.userId || '',
        academicYear: queryForm.academicYear,
        semester: queryForm.semester,
        weekNumber: queryForm.weekNumber
      }
      console.log('查询教师课表，参数:', params)
      scheduleData = await getTeacherSchedule(params)
      console.log('获取到的课表数据:', scheduleData)
    }

    tableData.value = scheduleData
    console.log('=== 课表查询完成 ===\n课程数量:', scheduleData.length)

    if (scheduleData.length === 0) {
      ElMessage.info('未找到课表数据')
    }
  } catch (error) {
    console.error('查询课表失败:', error)
    if (error instanceof Error) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('查询失败')
    }
  } finally {
    loading.value = false
  }
}

// 在组件挂载时获取专业列表
onMounted(() => {
  // 加载用户信息
  userStore.loadUserFromStorage()

  // 根据用户类型预设查询参数
  if (userStore.isStudent && userStore.userInfo) {
    queryForm.majorId = userStore.userInfo.majorId || ''
    queryForm.classId = userStore.userInfo.classId || ''
    // 如果是学生，自动加载班级数据
    if (queryForm.majorId && queryForm.classId) {
      handleQuery()
    }
  } else if (userStore.isTeacher && userStore.userInfo) {
    queryForm.teacherId = userStore.userInfo.userId || ''
    queryForm.teacherQuery = userStore.userInfo.name || ''
    // 如果是教师，自动加载教师课表
    if (queryForm.teacherId) {
      handleQuery()
    }
  }

  initOptions()
})
</script>

<style scoped>
.timetable-container {
  position: relative;
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.timetable-header {
  background-color: #fff;
  padding: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  z-index: 3;
  flex: 0 0 auto;
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

.query-form {
  :deep(.el-form) {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    align-items: center;
  }

  :deep(.el-form-item) {
    margin: 0;
  }

  :deep(.el-form-item__label) {
    padding-right: 8px;
    font-weight: 500;
  }
}

.type-switch {
  display: flex;
  gap: 10px;
}

.timetable-content {
  flex: 1;
  padding: 0 16px 16px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  position: relative;
}

.schedule-wrapper {
  flex: 1;
  overflow: hidden;
  position: relative;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  background-color: #fff;
  display: flex;
  flex-direction: column;
}

.schedule-grid {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  position: relative;
}

.schedule-header {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  position: sticky;
  top: 0;
  z-index: 10;
}

.time-column, .day-column {
  padding: 12px 8px;
  text-align: center;
  font-weight: 600;
  border-right: 1px solid #ebeef5;
  color: #606266;
}

.schedule-body {
  display: flex;
  flex-direction: column;
  overflow: auto;
  max-height: calc(100vh - 250px);
  width: 100%;
  flex: 1;
}

.time-row {
  display: grid;
  grid-template-columns: 80px repeat(7, 1fr);
  border-bottom: 1px solid #ebeef5;
  min-height: 120px;
}

.time-row:last-child {
  border-bottom: none;
}

.time-column {
  padding: 10px 5px;
  text-align: center;
  background-color: #f5f7fa;
  color: #606266;
  font-size: 13px;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: sticky;
  left: 0;
  z-index: 5;
}

.schedule-cell {
  padding: 5px;
  min-height: 120px;
  max-height: 250px;
  border-right: 1px solid #ebeef5;
  position: relative;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.schedule-cell:last-child {
  border-right: none;
}

.course-card {
  background-color: #ecf5ff;
  border-radius: 4px;
  padding: 8px;
  margin-bottom: 4px;
  cursor: default;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  border-left: 3px solid #409eff;
  min-height: 90px;
  width: 100%;
  flex: 0 0 auto;
  position: relative;
}

.course-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 2;
}

.course-card.is-elective {
  background-color: #f0f9eb;
  border-left-color: #67c23a;
  cursor: pointer;
}

.course-card.has-multiple-courses {
  border-bottom: 2px dashed #e0e0e0;
  padding-bottom: 12px;
  margin-bottom: 6px;
}

.multi-course-indicator {
  position: absolute;
  bottom: 2px;
  right: 6px;
  font-size: 10px;
  color: #909399;
  background-color: rgba(144, 147, 153, 0.1);
  padding: 1px 4px;
  border-radius: 2px;
}

.course-name {
  font-weight: 600;
  margin-bottom: 5px;
  color: #303133;
  font-size: 14px;
  line-height: 1.3;
  word-break: break-all;
}

.course-info {
  font-size: 12px;
  color: #606266;
  margin-bottom: 3px;
  line-height: 1.3;
}

.course-info:last-child {
  margin-bottom: 0;
}

.pagination-container {
  margin-top: 15px;
  display: flex;
  justify-content: center;
}

.empty-text {
  text-align: center;
  padding: 30px;
  color: #909399;
  font-size: 16px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.course-detail {
  padding: 10px;
}

.course-detail p {
  margin: 10px 0;
  line-height: 1.5;
}

:deep(.el-loading-mask) {
  border-radius: 8px;
}

/* 自定义滚动条样式 */
.schedule-body::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.schedule-body::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.schedule-body::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}

.schedule-body::-webkit-scrollbar-thumb:hover {
  background: #555;
}

.schedule-cell::-webkit-scrollbar {
  width: 4px;
}

.schedule-cell::-webkit-scrollbar-track {
  background: transparent;
}

.schedule-cell::-webkit-scrollbar-thumb {
  background: rgba(136, 136, 136, 0.4);
  border-radius: 2px;
}

.schedule-cell:hover::-webkit-scrollbar-thumb {
  background: rgba(136, 136, 136, 0.6);
}

@media (max-width: 1024px) {
  .schedule-header, .time-row {
    min-width: 900px;
  }

  .time-column {
    z-index: 10;
  }

  .schedule-header .day-column {
    min-width: 120px;
  }

  .schedule-body {
    overflow-x: auto;
  }
}

@media (max-width: 768px) {
  .schedule-header, .time-row {
    grid-template-columns: 60px repeat(7, 1fr);
    min-width: 700px;
  }

  .time-column, .day-column, .schedule-cell {
    padding: 5px 2px;
    font-size: 12px;
  }

  .time-column {
    min-width: 60px;
  }

  .course-name {
    font-size: 12px;
  }

  .course-info {
    font-size: 10px;
  }
}
</style>


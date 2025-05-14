import request from '@/utils/request'
import type { AxiosError } from 'axios'
import type { CourseSchedule, CourseDetail, Major, Class, StudentTimetableQuery, TeacherTimetableQuery, Teacher } from '@/types/timetable'

// API响应接口
interface ApiResponse<T> {
  success: boolean
  message?: string
  data?: T
}

// 原始课表数据接口
interface RawCourseSchedule {
  id: number
  course_id?: string
  courseId?: string
  course_name?: string
  courseName?: string
  course_type?: string
  courseType?: string
  teacher_id?: string
  teacherId?: string
  teacher_name?: string
  teacherName?: string
  classroom_id?: string
  classroomId?: string
  classroom_name?: string
  classroomName?: string
  building_id?: string
  buildingId?: string
  academic_year?: string
  academicYear?: string
  semester: number
  weekday: number
  start_section?: number
  startSection?: number
  end_section?: number
  endSection?: number
  week_number?: number
  weekNumber?: number
  credits?: number
  description?: string
  total_students?: number
  totalStudents?: number
  max_students?: number
  maxStudents?: number
}

// 节次到时间的映射
const sectionTimeMap: Record<number, { start: string; end: string }> = {
  1: { start: '08:00', end: '09:35' },  // 第1-2节
  2: { start: '08:00', end: '09:35' },  // 第1-2节
  3: { start: '10:05', end: '11:40' },  // 第3-4节
  4: { start: '10:05', end: '11:40' },  // 第3-4节
  5: { start: '14:00', end: '15:35' },  // 第5-6节
  6: { start: '14:00', end: '15:35' },  // 第5-6节
  7: { start: '16:05', end: '17:40' },  // 第7-8节
  8: { start: '16:05', end: '17:40' },  // 第7-8节
  9: { start: '19:00', end: '20:35' },  // 第9-10节
  10: { start: '19:00', end: '20:35' }  // 第9-10节
}

// 获取时间段
const getTimeFromSection = (startSection: number) => {
  // 由于每两节课是一个时间段，我们使用开始节次的时间
  const start = sectionTimeMap[startSection]?.start || ''
  const end = sectionTimeMap[startSection]?.end || ''
  return { start, end }
}

/**
 * 获取学生课表
 */
export const getStudentSchedule = async (params: StudentTimetableQuery): Promise<CourseSchedule[]> => {
  console.log('开始请求学生课表 API，参数:', params)
  try {
    const response = await request.get('/api/schedule/student', { params })
    console.log('收到学生课表 API 原始响应:', response)

    // 检查响应格式
    if (!response) {
      console.error('响应为空')
      throw new Error('获取课表失败：服务器响应为空')
    }

    // 处理不同的响应格式
    let rawData: RawCourseSchedule[] = []

    if (typeof response === 'object') {
      if ('success' in response && !response.success) {
        const errorResponse = response as { success: boolean; message?: string }
        throw new Error(errorResponse.message || '获取课表失败')
      }
      if ('data' in response) {
        if (Array.isArray(response.data)) {
          rawData = response.data
        } else {
          console.error('响应数据格式错误，期望数组但收到:', typeof response.data)
          throw new Error('获取课表失败：数据格式错误')
        }
      } else if (Array.isArray(response)) {
        rawData = response
      }
    } else {
      console.error('响应格式错误:', response)
      throw new Error('获取课表失败：响应格式错误')
    }

    console.log('学生课表原始数据:', rawData)

    if (rawData.length === 0) {
      console.warn('学生课表数据为空')
      return []
    }

    // 记录第一条数据的结构
    console.log('学生课表数据结构示例:', {
      原始数据: rawData[0],
      字段列表: Object.keys(rawData[0]).join(', ')
    })

    // 转换字段名并确保所有必需字段都有值
    const result = rawData.map(item => {
      const startSection = Number(item.startSection || item.start_section || 0)
      const endSection = Number(item.endSection || item.end_section || 0)
      const times = getTimeFromSection(startSection)

      const schedule: CourseSchedule = {
        id: String(item.id || ''),
        courseId: String(item.courseId || item.course_id || ''),
        courseName: String(item.courseName || item.course_name || ''),
        courseType: String(item.courseType || item.course_type || ''),
        teacherId: String(item.teacherId || item.teacher_id || ''),
        teacherName: String(item.teacherName || item.teacher_name || ''),
        classroomId: String(item.classroomId || item.classroom_id || ''),
        classroomName: String(item.classroomName || item.classroom_name || ''),
        buildingId: String(item.buildingId || item.building_id || ''),
        academicYear: String(item.academicYear || item.academic_year || ''),
        semester: Number(item.semester || 0),
        weekday: Number(item.weekday || 0),
        startSection: startSection,
        endSection: endSection,
        startTime: times.start,
        endTime: times.end,
        weekNumber: Number(item.weekNumber || item.week_number || 0),
        credits: item.credits !== undefined ? Number(item.credits) : undefined,
        description: item.description,
        totalStudents: item.totalStudents !== undefined ? Number(item.totalStudents) :
                      item.total_students !== undefined ? Number(item.total_students) : undefined,
        maxStudents: item.maxStudents !== undefined ? Number(item.maxStudents) :
                    item.max_students !== undefined ? Number(item.max_students) : undefined
      }
      return schedule
    })

    console.log('处理后的学生课表数据:', result)
    return result
  } catch (error) {
    console.error('获取学生课表失败:', error)
    if (error instanceof Error) {
      const axiosError = error as AxiosError<ApiResponse<unknown>>
      if (axiosError.response?.data) {
        const data = axiosError.response.data
        const status = axiosError.response.status

        if (status === 400) {
          console.error('请求参数错误:', data)
          throw new Error(`请求参数错误: ${data.message || '参数格式不正确'}`)
        } else if (status === 404) {
          console.error('未找到课表数据:', data)
          throw new Error('未找到相关课表数据')
        } else {
          console.error('服务器错误:', data)
          throw new Error(data.message || '服务器错误，请稍后重试')
        }
      }
    }
    // 如果是其他类型的错误，直接抛出
    throw error
  }
}

/**
 * 获取教师课表
 */
export const getTeacherSchedule = async (params: TeacherTimetableQuery): Promise<CourseSchedule[]> => {
  console.log('开始请求教师课表 - 参数:', params)
  try {
    const response = await request.get('/api/schedule/teacher', { params })
    console.log('教师课表原始响应:', response)

    // 处理不同的响应格式
    let rawData: RawCourseSchedule[] = []

    // 检查响应格式
    if (!response) {
      console.error('响应为空')
      throw new Error('获取课表失败：服务器响应为空')
    }

    if (typeof response !== 'object') {
      console.error('响应格式错误:', response)
      throw new Error('获取课表失败：响应格式错误')
    }

    // 处理响应数据
    if ('data' in response) {
      const responseData = response.data
      if (Array.isArray(responseData)) {
        rawData = responseData
      } else if (responseData && typeof responseData === 'object') {
        if ('success' in responseData && !responseData.success) {
          throw new Error(responseData.message || '获取课表失败')
        }
        if ('data' in responseData && Array.isArray(responseData.data)) {
          rawData = responseData.data
        }
      }
    } else if (Array.isArray(response)) {
      rawData = response
    }

    console.log('原始数据:', rawData)

    if (rawData.length === 0) {
      console.warn('未找到课表数据')
    }

    // 转换字段名并确保所有必需字段都有值
    const result = rawData.map(item => {
      const startSection = Number(item.startSection || item.start_section || 0)
      const endSection = Number(item.endSection || item.end_section || 0)
      const times = getTimeFromSection(startSection)

      const schedule: CourseSchedule = {
        id: String(item.id || ''),
        courseId: String(item.courseId || item.course_id || ''),
        courseName: String(item.courseName || item.course_name || ''),
        courseType: String(item.courseType || item.course_type || ''),
        teacherId: String(item.teacherId || item.teacher_id || ''),
        teacherName: String(item.teacherName || item.teacher_name || ''),
        classroomId: String(item.classroomId || item.classroom_id || ''),
        classroomName: String(item.classroomName || item.classroom_name || ''),
        buildingId: String(item.buildingId || item.building_id || ''),
        academicYear: String(item.academicYear || item.academic_year || ''),
        semester: Number(item.semester || 0),
        weekday: Number(item.weekday || 0),
        startSection: startSection,
        endSection: endSection,
        startTime: times.start,
        endTime: times.end,
        weekNumber: Number(item.weekNumber || item.week_number || 0),
        credits: item.credits !== undefined ? Number(item.credits) : undefined,
        description: item.description,
        totalStudents: item.totalStudents !== undefined ? Number(item.totalStudents) :
                      item.total_students !== undefined ? Number(item.total_students) : undefined,
        maxStudents: item.maxStudents !== undefined ? Number(item.maxStudents) :
                    item.max_students !== undefined ? Number(item.max_students) : undefined
      }
      return schedule
    })

    console.log('处理后的教师课表数据:', result)
    return result
  } catch (error) {
    console.error('获取教师课表失败:', error)
    throw error
  }
}

/**
 * 获取课程详情
 */
export const getCourseDetail = async (courseId: string, semester: string): Promise<CourseDetail> => {
  console.log('开始请求课程详情 - 课程ID:', courseId, '学期:', semester)
  try {
    const response = await request.get(`/api/course/${courseId}/detail`, { params: { semester } })
    console.log('课程详情原始响应:', response)
    if (!response) {
      throw new Error('获取课程详情失败')
    }
    const result = response.data || response
    console.log('处理后的课程详情数据:', result)
    return result
  } catch (error) {
    console.error('获取课程详情失败:', error)
    throw error
  }
}

/**
 * 获取专业列表
 */
export const getMajors = async (): Promise<Major[]> => {
  try {
    const response = await request.get('/api/majors')
    console.log('专业列表原始响应:', response)

    // 检查响应格式
    if (!response) {
      throw new Error('获取专业列表失败：响应为空')
    }

    // 处理响应数据
    const rawData = response.data || response
    console.log('处理后的专业列表数据:', rawData)

    // 确保返回的是数组
    if (!Array.isArray(rawData)) {
      throw new Error('获取专业列表失败：数据格式错误')
    }

    // 转换数据格式
    const majors = rawData.map(item => ({
      majorId: String(item.majorId || item.id || ''),
      majorName: String(item.majorName || item.name || ''),
      departmentId: String(item.departmentId || item.department_id || '')
    }))

    console.log('最终返回的专业列表:', majors)
    return majors
  } catch (error) {
    console.error('获取专业列表失败:', error)
    throw error
  }
}

/**
 * 获取班级列表
 */
export const getClasses = async (majorId: string): Promise<Class[]> => {
  const response = await request.get(`/api/classes/major/${majorId}`)
  // 使用类型守卫更安全地检查属性
  if (response &&
      typeof response === 'object' &&
      'success' in response &&
      response.success === false &&
      !Array.isArray(response)) {
    throw new Error(
      'message' in response && typeof response.message === 'string'
        ? response.message
        : '获取班级列表失败'
    )
  }
  return Array.isArray(response) ? response : (response.data || [])
}

// 搜索教师
export const searchTeachers = async (query: string): Promise<Teacher[]> => {
  try {
    console.log('Sending teacher search request with query:', query)
    const response = await request({
      url: '/api/teachers/search',
      method: 'get',
      params: { query }
    })
    console.log('Raw API response:', response)

    // 检查响应格式
    if (!response) {
      console.error('Invalid response format:', response)
      throw new Error('API 响应格式错误')
    }

    // 处理响应数据
    const rawData = Array.isArray(response) ? response : (response.data || [])
    console.log('Raw data:', rawData)

    // 防御性数据处理
    if (!Array.isArray(rawData)) {
      console.error('Invalid data format - expected array:', rawData)
      return []
    }

    // 过滤并转换有效数据
    const validTeachers = rawData
      .filter(item => item && typeof item === 'object')
      .map(item => ({
        id: String(item.id || item.teacher_id || item.teacherId || ''),
        name: String(item.name || item.teacher_name || item.teacherName || ''),
        code: String(item.code || item.teacher_code || item.teacherCode || item.id || item.teacher_id || item.teacherId || ''),
        title: String(item.title || item.teacher_title || ''),
        departmentId: String(item.departmentId || item.department_id || ''),
        office: String(item.office || item.teacher_office || ''),
        gender: String(item.gender || item.teacher_gender || '')
      }))
      .filter(teacher => teacher.id && teacher.name); // Only filter out entries missing essential fields

    console.log('Valid teachers after processing:', validTeachers)
    return validTeachers

  } catch (error) {
    console.error('Teacher search API error details:', error)
    if (error instanceof Error) {
      throw new Error(`搜索教师失败: ${error.message}`)
    }
    throw new Error('搜索教师失败')
  }
}

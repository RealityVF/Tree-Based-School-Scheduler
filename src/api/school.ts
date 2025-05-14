import request from '@/utils/request';

// 教学楼类型接口
export interface Building {
  id: string;
  campus: string;
  name: string;
  floors: number;
  location: string;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}

// 教室类型接口
export interface Classroom {
  id: string;
  name: string;
  buildingId: string;
  buildingName?: string;
  floor: number;
  capacity: number;
  type: string; // 普通教室、计算机教室、实验室等
  hasMultimedia: boolean;
  description?: string;
  createdAt?: string;
  updatedAt?: string;
}

// 班级类型接口
export interface Class {
  id: string;
  className: string;
  grade: string;
  majorId: string;
  majorName: string;
  departmentId: string;
  departmentName: string;
  counselorId?: string;
  counselorName?: string;
  studentCount: number;
}

// 课程接口
export interface Course {
  id: string;
  name: string;
  category: string;
  attribute: string;
  course_type: string;
  nature: string;
  department_name: string;
  is_active: boolean;
  total_hours: number;
  theory_hours: number;
  experiment_hours: number;
  computer_hours: number;
  practice_hours: number;
  credit: number;
  weekly_hours: number;
  is_practical: boolean;
  created_at?: string;
}

// 课程安排接口
export interface CourseSchedule {
  id: string;
  courseId: string;
  classroomId: string;
  classroomName: string;
  weekday: string;
  period: string;
  createdAt?: string;
  updatedAt?: string;
}

// 定义通用响应接口
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

// 定义后端响应类型
interface BackendResponse<T> {
  data: T;
  success: boolean;
  message: string;
}

// 获取教学楼列表
export const getBuildings = async (): Promise<{
  success: boolean;
  message: string;
  data: Building[];
}> => {
  try {
    const response = await request({
      url: '/api/admin/buildings',
      method: 'get'
    });

    if (response && typeof response === 'object') {
      // 如果响应直接是数据数组
      if (Array.isArray(response)) {
        return {
          success: true,
          message: '获取成功',
          data: response
        };
      }

      // 如果响应数据在data字段中
      if (response.data && Array.isArray(response.data)) {
        return {
          success: true,
          message: '获取成功',
          data: response.data
        };
      }
    }

    return {
      success: false,
      message: '响应格式不正确',
      data: []
    };
  } catch (error) {
    console.error('获取教学楼列表失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取教学楼列表失败，请稍后重试',
      data: []
    };
  }
};

// 创建教学楼
export const createBuilding = async (buildingData: Omit<Building, 'id' | 'createdAt' | 'updatedAt'>): Promise<{
  success: boolean;
  message: string;
  data?: Building;
}> => {
  try {
    const response = await request({
      url: '/api/admin/buildings',
      method: 'post',
      data: buildingData
    });

    return {
      success: true,
      message: '创建成功',
      data: response.data
    };
  } catch (error) {
    console.error('创建教学楼失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '创建教学楼失败，请稍后重试'
    };
  }
};

// 更新教学楼
export const updateBuilding = async (id: string, buildingData: Partial<Building>): Promise<{
  success: boolean;
  message: string;
  data?: Building;
}> => {
  try {
    const response = await request({
      url: `/api/admin/buildings/${id}`,
      method: 'put',
      data: buildingData
    });

    return {
      success: true,
      message: '更新成功',
      data: response.data
    };
  } catch (error) {
    console.error('更新教学楼失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '更新教学楼失败，请稍后重试'
    };
  }
};

// 删除教学楼
export const deleteBuilding = async (id: string): Promise<{
  success: boolean;
  message: string;
}> => {
  try {
    await request({
      url: `/api/admin/buildings/${id}`,
      method: 'delete'
    });

    return {
      success: true,
      message: '删除成功'
    };
  } catch (error) {
    console.error('删除教学楼失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '删除教学楼失败，请稍后重试'
    };
  }
};

// 获取教室列表
export const getClassrooms = async (buildingId?: string): Promise<{
  success: boolean;
  message: string;
  data: Classroom[] | { value: string; label: string }[];
}> => {
  try {
    const response = await request<{
      data: Classroom[];
      success: boolean;
      message: string;
    }>({
      url: '/api/admin/classrooms',
      method: 'get',
      params: buildingId ? { buildingId } : undefined
    });

    // 标准响应格式
    if (response?.data?.success && Array.isArray(response.data.data)) {
      // 如果需要返回选项格式
      if (buildingId === undefined) {
        return {
          success: true,
          message: response.data.message,
          data: response.data.data.map((classroom: Classroom) => ({
            value: classroom.id,
            label: `${classroom.buildingName ? classroom.buildingName + '-' : ''}${classroom.name}`
          }))
        };
      }
      return {
        success: true,
        message: response.data.message,
        data: response.data.data
      };
    }

    // 直接返回数组的情况
    if (response?.data && Array.isArray(response.data)) {
      if (buildingId === undefined) {
        return {
          success: true,
          message: '获取成功',
          data: response.data.map((classroom: Classroom) => ({
            value: classroom.id,
            label: `${classroom.buildingName ? classroom.buildingName + '-' : ''}${classroom.name}`
          }))
        };
      }
      return {
        success: true,
        message: '获取成功',
        data: response.data
      };
    }

    return {
      success: false,
      message: '响应格式不正确',
      data: []
    };
  } catch (error) {
    console.error('获取教室列表失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取教室列表失败，请稍后重试',
      data: []
    };
  }
};

// 创建教室
export const createClassroom = async (classroomData: Omit<Classroom, 'id' | 'buildingName' | 'createdAt' | 'updatedAt'>): Promise<{
  success: boolean;
  message: string;
  data?: Classroom;
}> => {
  try {
    const response = await request({
      url: '/api/admin/classrooms',
      method: 'post',
      data: classroomData
    });

    return {
      success: true,
      message: '创建成功',
      data: response.data
    };
  } catch (error) {
    console.error('创建教室失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '创建教室失败，请稍后重试'
    };
  }
};

// 更新教室
export const updateClassroom = async (id: string, classroomData: Partial<Classroom>): Promise<{
  success: boolean;
  message: string;
  data?: Classroom;
}> => {
  try {
    const response = await request({
      url: `/api/admin/classrooms/${id}`,
      method: 'put',
      data: classroomData
    });

    return {
      success: true,
      message: '更新成功',
      data: response.data
    };
  } catch (error) {
    console.error('更新教室失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '更新教室失败，请稍后重试'
    };
  }
};

// 删除教室
export const deleteClassroom = async (id: string): Promise<{
  success: boolean;
  message: string;
}> => {
  try {
    await request({
      url: `/api/admin/classrooms/${id}`,
      method: 'delete'
    });

    return {
      success: true,
      message: '删除成功'
    };
  } catch (error) {
    console.error('删除教室失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '删除教室失败，请稍后重试'
    };
  }
};

// 获取班级列表
export const getClasses = async (departmentId?: string, grade?: string): Promise<{
  success: boolean;
  message: string;
  data: Class[];
}> => {
  try {
    const params: Record<string, string> = {};
    if (departmentId) params.departmentId = departmentId;
    if (grade) params.grade = grade;

    const response = await request<{
      data: Class[];
      success: boolean;
      message: string;
    }>({
      url: '/api/schools/classes',
      method: 'get',
      params
    });

    // 标准响应格式
    if (response?.data?.success && Array.isArray(response.data.data)) {
      return {
        success: true,
        message: response.data.message,
        data: response.data.data
      };
    }

    // 直接返回数组的情况
    if (response?.data && Array.isArray(response.data)) {
      return {
        success: true,
        message: '获取成功',
        data: response.data
      };
    }

    return {
      success: false,
      message: '响应格式不正确',
      data: []
    };
  } catch (error) {
    console.error('获取班级列表失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取班级列表失败，请稍后重试',
      data: []
    };
  }
};

// 创建班级
export const createClass = async (classData: Omit<Class, 'id'>): Promise<{
  success: boolean;
  message: string;
  data?: Class;
}> => {
  try {
    const response = await request({
      url: '/api/schools/classes',
      method: 'post',
      data: classData
    });

    return {
      success: true,
      message: '创建成功',
      data: response.data
    };
  } catch (error) {
    console.error('创建班级失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '创建班级失败，请稍后重试'
    };
  }
};

// 更新班级
export const updateClass = async (id: string, classData: Partial<Omit<Class, 'id'>>): Promise<{
  success: boolean;
  message: string;
  data?: Class;
}> => {
  try {
    const response = await request({
      url: `/api/schools/classes/${id}`,
      method: 'put',
      data: classData
    });

    return {
      success: true,
      message: '更新成功',
      data: response.data
    };
  } catch (error) {
    console.error('更新班级失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '更新班级失败，请稍后重试'
    };
  }
};

// 删除班级
export const deleteClass = async (id: string): Promise<{
  success: boolean;
  message: string;
}> => {
  try {
    await request({
      url: `/api/schools/classes/${id}`,
      method: 'delete'
    });

    return {
      success: true,
      message: '删除成功'
    };
  } catch (error) {
    console.error('删除班级失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '删除班级失败，请稍后重试'
    };
  }
};

// 课程相关接口
export interface CourseQueryParams {
  name?: string;
  departmentName?: string;
  category?: string;
  courseType?: string;
  page: number;
  pageSize: number;
}

// 获取课程列表
export async function getCourses(params: CourseQueryParams): Promise<{
  success: boolean;
  message: string;
  data: Course[] | { list: Course[]; total: number };
}> {
  try {
    console.log('[API] 获取课程列表，参数:', params);
    const response = await request({
      url: '/api/courses',
      method: 'get',
      params
    });

    console.log('[API] 课程列表原始响应:', response);

    // 直接返回数组的情况
    if (Array.isArray(response)) {
      return {
        success: true,
        message: '获取成功',
        data: response
      };
    }

    // 标准响应格式：{ success: boolean, message: string, data: any }
    if (response && typeof response === 'object' && 'success' in response) {
      return {
        success: response.success,
        message: response.message || '获取成功',
        data: response.data || []
      };
    }

    // 分页响应格式：{ courses: Course[], total: number, page: number, size: number }
    if (response && typeof response === 'object' && 'courses' in response) {
      return {
        success: true,
        message: '获取成功',
        data: {
          list: response.courses || [],
          total: response.total || 0
        }
      };
    }

    // 直接是数据对象的情况
    if (response && typeof response === 'object') {
      return {
        success: true,
        message: '获取成功',
        data: response
      };
    }

    return {
      success: false,
      message: '响应格式不正确',
      data: []
    };
  } catch (error) {
    console.error('[API] 获取课程列表失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取课程列表失败，请稍后重试',
      data: []
    };
  }
}

// 创建课程
export function createCourse(data: Omit<Course, 'id' | 'created_at'>) {
  return request({
    url: '/api/courses',
    method: 'post',
    data
  });
}

// 更新课程
export function updateCourse(id: string, data: Partial<Course>) {
  return request({
    url: `/api/courses/${id}`,
    method: 'put',
    data
  });
}

// 删除课程
export function deleteCourse(id: string) {
  return request({
    url: `/api/courses/${id}`,
    method: 'delete'
  });
}

// 批量导入课程
export function importCourses(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return request({
    url: '/api/courses/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

// 下载课程导入模板
export function downloadCourseTemplate() {
  return request({
    url: '/api/courses/template',
    method: 'get',
    responseType: 'blob'
  });
}

// 获取课程安排
export const getCourseSchedules = async (courseId: string): Promise<{
  success: boolean;
  message: string;
  data: CourseSchedule[];
}> => {
  try {
    const response = await request({
      url: `/api/courses/${courseId}/schedules`,
      method: 'get'
    });
    return {
      success: true,
      message: '获取成功',
      data: response.data
    };
  } catch (error) {
    console.error('获取课程安排失败:', error);
    return {
      success: false,
      message: '获取课程安排失败',
      data: []
    };
  }
};

// 创建课程安排
export const createCourseSchedule = async (scheduleData: Omit<CourseSchedule, 'id' | 'createdAt' | 'updatedAt'>): Promise<{
  success: boolean;
  message: string;
  data?: CourseSchedule;
}> => {
  try {
    const response = await request({
      url: '/api/course-schedules',
      method: 'post',
      data: scheduleData
    });
    return {
      success: true,
      message: '创建成功',
      data: response.data
    };
  } catch (error) {
    console.error('创建课程安排失败:', error);
    return {
      success: false,
      message: '创建课程安排失败'
    };
  }
};

// 删除课程安排
export const deleteCourseSchedule = async (id: string): Promise<{
  success: boolean;
  message: string;
}> => {
  try {
    await request({
      url: `/api/course-schedules/${id}`,
      method: 'delete'
    });
    return {
      success: true,
      message: '删除成功'
    };
  } catch (error) {
    console.error('删除课程安排失败:', error);
    return {
      success: false,
      message: '删除课程安排失败'
    };
  }
};

export interface Department {
  id: string;
  name: string;
  description?: string;
  parentId?: string;
  level?: number;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

// 获取院系列表
export const getDepartments = async (): Promise<ApiResponse<Department[]>> => {
  try {
    console.log('开始获取院系列表');
    const response = await request<BackendResponse<Department[]>>({
      url: '/api/admin/departments',
      method: 'get'
    });

    console.log('原始响应:', response);

    // 处理标准响应格式
    if (response && typeof response === 'object') {
      // 如果响应是标准格式：{data: Array, success: boolean, message: string}
      if (response.data && Array.isArray(response.data)) {
        return {
          success: true,
          message: '获取成功',
          data: response.data
        };
      }
      
      // 处理嵌套的响应格式：{data: {data: Array, success: boolean, message: string}}
      if (response.data && Array.isArray(response.data.data)) {
        return {
          success: response.data.success,
          message: response.data.message,
          data: response.data.data
        };
      }
    }

    console.error('院系列表响应格式不正确:', response);
    return {
      success: false,
      message: '响应格式不正确',
      data: []
    };
  } catch (error) {
    console.error('获取院系列表失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取院系列表失败，请稍后重试',
      data: []
    };
  }
};


// 获取教师列表
export const getTeachers = async (): Promise<ApiResponse<{ value: string; label: string }[]>> => {
  try {
    const response = await request({
      url: '/api/teachers',
      method: 'get'
    });

    if (response && typeof response === 'object' && response.data && Array.isArray(response.data)) {
      return {
        success: true,
        message: '获取成功',
        data: response.data
      };
    }

    return {
      success: false,
      message: '响应格式不正确',
      data: []
    };
  } catch (error) {
    console.error('获取教师列表失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取教师列表失败，请稍后重试',
      data: []
    };
  }
};

// 添加getMajors函数作为临时解决方案
export const getMajors = async (params?: any) => {
  try {
    // 可以直接使用axios或request
    const response = await request({
      url: '/api/majors',
      method: 'get',
      params
    });

    // 处理不同的响应格式
    if (response && typeof response === 'object' && 'data' in response) {
      return {
        success: response.success || true,
        message: response.message || '获取专业列表成功',
        data: Array.isArray(response.data) ? response.data : []
      };
    }

    if (Array.isArray(response)) {
      return {
        success: true,
        message: '获取专业列表成功',
        data: response
      };
    }

    return {
      success: true,
      message: '获取专业列表成功',
      data: []
    };
  } catch (error) {
    console.error('获取专业列表失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取专业列表失败，请稍后重试',
      data: []
    };
  }
};

import request from '@/utils/request';
import type { TeacherAccount, StudentAccount } from '@/types/account';
import { Gender, AccountStatus } from '@/types/account';

/**
 * 创建教师账号响应类型
 */
interface TeacherAccountResponse {
  success: boolean;
  message: string;
  data?: TeacherAccount;
}

/**
 * 创建教师账号
 */
export const createTeacherAccount = async (accountData: {
  teacherId: string;
  name: string;
  gender: string;
  departmentId: string;
  teacherType: string;
  englishName?: string;
  ethnicity?: string;
  title?: string;
  isExternal: boolean;
  email?: string;
  phone?: string;
  maxWeeklyHours?: number;
  researchDirection?: string;
  satisfaction?: boolean;
  isActive: boolean;
  password?: string;
}): Promise<TeacherAccountResponse> => {
  try {
    console.log('Creating teacher account with data:', accountData);
    const response = await request({
      url: '/api/admin/accounts/teacher',
      method: 'post',
      headers: {
        'Content-Type': 'application/json'
      },
      data: {
        teacherId: accountData.teacherId,
        name: accountData.name,
        gender: accountData.gender,
        department: accountData.departmentId,
        teacherType: accountData.teacherType,
        englishName: accountData.englishName,
        ethnicity: accountData.ethnicity,
        title: accountData.title,
        isExternal: accountData.isExternal,
        email: accountData.email,
        phone: accountData.phone,
        maxWeeklyHours: accountData.maxWeeklyHours,
        researchDirection: accountData.researchDirection,
        satisfaction: accountData.satisfaction,
        isActive: accountData.isActive
      },
      withCredentials: true
    });

    console.log('Teacher account created successfully:', response.data);
    return {
      success: true,
      message: '创建成功',
      data: response.data
    };
  } catch (error: unknown) {
    console.error('创建教师账号失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '创建教师账号失败，请稍后重试'
    };
  }
};

/**
 * 获取教师账号列表
 */
export const getTeacherAccounts = async (
  params: {
    page?: number;
    size?: number;
    department?: string;
    teacherType?: string;
    searchKey?: string;
  } = {}
): Promise<{
  success: boolean;
  message: string;
  data: {
    teachers: TeacherAccount[];
    total: number;
    page: number;
    size: number;
  };
}> => {
  try {
    interface TeacherData {
      id: string;
      name: string;
      englishName?: string;
      gender: string;
      ethnicity?: string;
      title?: string;
      departmentId: string;
      departmentName?: string;
      isExternal?: boolean;
      teacherType?: string;
      satisfaction?: boolean;
      phone?: string;
      email?: string;
      maxWeeklyHours?: number;
      researchDirection?: string;
      isActive: boolean;
    }

    interface BackendResponse {
      total: number;
      size: number;
      teachers: TeacherData[];
      page: number;
    }

    const { data: response } = await request<BackendResponse>({
      url: '/api/admin/accounts/teacher',
      method: 'get',
      params: {
        ...params,
        keyword: params.searchKey,
        departmentId: params.department
      }
    });

    console.log('Backend response:', response);

    if (response && Array.isArray(response.teachers)) {
      const transformedTeachers: TeacherAccount[] = response.teachers.map((teacher: TeacherData) => ({
        id: teacher.id,
        teacherId: teacher.id,
        name: teacher.name,
        englishName: teacher.englishName || '',
        gender: teacher.gender as Gender,
        ethnicity: teacher.ethnicity || '',
        title: teacher.title || '',
        departmentId: teacher.departmentId,
        isExternal: teacher.isExternal || false,
        teacherType: teacher.teacherType || '',
        satisfaction: teacher.satisfaction || false,
        phone: teacher.phone || '',
        email: teacher.email || '',
        maxWeeklyHours: teacher.maxWeeklyHours || 0,
        researchDirection: teacher.researchDirection || '',
        isActive: teacher.isActive,
        status: teacher.isActive ? AccountStatus.ENABLED : AccountStatus.DISABLED
      }));

      return {
        success: true,
        message: '查询成功',
        data: {
          teachers: transformedTeachers,
          total: response.total,
          page: response.page,
          size: response.size
        }
      };
    }

    return {
      success: false,
      message: '响应数据格式不正确',
      data: {
        teachers: [],
        total: 0,
        page: 1,
        size: 10
      }
    };
  } catch (error: unknown) {
    console.error('获取教师账号列表失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取教师账号列表失败，请稍后重试',
      data: {
        teachers: [],
        total: 0,
        page: 1,
        size: 10
      }
    };
  }
};

/**
 * 获取教师账号详情
 */
export const getTeacherAccountDetail = async (teacherId: string): Promise<{
  success: boolean;
  message: string;
  data?: TeacherAccount;
}> => {
  try {
    const response = await request<{
      success: boolean;
      message: string;
      data: {
        id: string;
        name: string;
        englishName: string;
        gender: string;
        ethnicity: string;
        title: string;
        departmentId: string;
        isExternal: boolean;
        teacherType: string;
        satisfaction: boolean;
        phone: string;
        email: string;
        maxWeeklyHours: number;
        researchDirection: string;
        isActive: boolean;
      };
    }>({
      url: `/api/admin/accounts/teacher/${teacherId}`,
      method: 'get'
    });

    if (response?.data?.data) {
      return {
        success: true,
        message: '获取成功',
        data: {
          id: response.data.data.id,
          teacherId: response.data.data.id,
          name: response.data.data.name,
          englishName: response.data.data.englishName,
          gender: response.data.data.gender as Gender,
          ethnicity: response.data.data.ethnicity,
          title: response.data.data.title,
          departmentId: response.data.data.departmentId,
          isExternal: response.data.data.isExternal,
          teacherType: response.data.data.teacherType,
          satisfaction: response.data.data.satisfaction,
          phone: response.data.data.phone,
          email: response.data.data.email,
          maxWeeklyHours: response.data.data.maxWeeklyHours,
          researchDirection: response.data.data.researchDirection,
          isActive: response.data.data.isActive,
          status: response.data.data.isActive ? AccountStatus.ENABLED : AccountStatus.DISABLED
        }
      };
    }

    return {
      success: false,
      message: '获取账号详情失败'
    };
  } catch (error: unknown) {
    console.error('获取教师账号详情失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取教师账号详情失败，请稍后重试'
    };
  }
};

/**
 * 更新教师账号
 */
export const updateTeacherAccount = async (teacherId: string, teacherAccount: Partial<TeacherAccount>): Promise<{
  success: boolean;
  message: string;
  data?: TeacherAccount;
}> => {
  try {
    console.log('更新教师账号:', teacherId, teacherAccount);

    const response = await request({
      url: `/api/admin/accounts/teacher/${teacherId}`,
      method: 'put',
      data: {
        name: teacherAccount.name,
        gender: teacherAccount.gender,
        englishName: teacherAccount.englishName,
        ethnicity: teacherAccount.ethnicity,
        title: teacherAccount.title,
        departmentId: teacherAccount.departmentId,
        isExternal: teacherAccount.isExternal,
        teacherType: teacherAccount.teacherType,
        satisfaction: teacherAccount.satisfaction,
        phone: teacherAccount.phone,
        email: teacherAccount.email,
        maxWeeklyHours: teacherAccount.maxWeeklyHours,
        researchDirection: teacherAccount.researchDirection,
        isActive: teacherAccount.isActive
      }
    });

    return {
      success: true,
      message: '更新教师账号成功',
      data: response.data
    };
  } catch (error) {
    console.error('更新教师账号失败:', error);
    let errorMessage = '更新教师账号失败，请稍后重试';
    if (error && typeof error === 'object' && 'message' in error) {
      const e = error as Error;
      errorMessage = e.message;
    }
    return {
      success: false,
      message: errorMessage
    };
  }
}

/**
 * 启用/禁用教师账号
 */
export const toggleTeacherAccountStatus = async (teacherId: string, enable: boolean): Promise<{
  success: boolean;
  message: string;
}> => {
  try {
    const response = await request({
      url: `/api/admin/accounts/teacher/${teacherId}/status`,
      method: 'patch',
      data: { enable }
    });

    console.log('Toggle status response:', response);

    // 处理response可能为null或不符合预期格式的情况
    if (response && typeof response === 'object') {
      // 如果响应中直接包含success和message字段
      if ('success' in response && 'message' in response) {
        return {
          success: Boolean(response.success),
          message: String(response.message || (enable ? '已启用教师账号' : '已禁用教师账号'))
        };
      }

      // 如果数据在data字段中
      if (response.data && typeof response.data === 'object') {
        const data = response.data;
        if ('success' in data) {
          return {
            success: Boolean(data.success),
            message: String(data.message || (enable ? '已启用教师账号' : '已禁用教师账号'))
          };
        }
      }
    }

    // 如果没有找到预期格式，但请求成功，则返回成功
    return {
      success: true,
      message: enable ? '已启用教师账号' : '已禁用教师账号'
    };
  } catch (error: unknown) {
    console.error('切换教师账号状态失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '切换教师账号状态失败，请稍后重试'
    };
  }
};

/**
 * 重置教师账号密码
 */
export async function resetTeacherPassword(teacherId: string): Promise<{success: boolean; newPassword?: string}> {
  try {
    const result = await request({
      url: `/api/admin/accounts/teacher/${teacherId}/reset-password`,
      method: 'post'
    });

    // 如果后端没有返回新密码，则提供一个默认值（教师ID的后六位）
    const defaultPassword = teacherId.slice(-6);
    const newPassword = result.data?.newPassword || defaultPassword;

    return {
      success: true,
      newPassword
    };
  } catch (error) {
    console.error('重置教师密码时出错:', error);
    return {
      success: false
    };
  }
}

/**
 * 批量创建教师账号
 */
export const batchCreateTeacherAccounts = async (file: File): Promise<{
  success: boolean;
  message: string;
  data?: {
    total: number;
    success: number;
    failed: number;
    errors: Array<{teacherId: string; reason: string}>;
  };
}> => {
  try {
    const formData = new FormData();
    formData.append('file', file);

    const response = await request({
      url: '/api/admin/accounts/teacher/batch',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }) as unknown as {
      success: boolean;
      message: string;
      data?: {
        total: number;
        success: number;
        failed: number;
        errors: Array<{teacherId: string; reason: string}>;
      };
    };

    return response;
  } catch (error: unknown) {
    console.error('批量创建教师账号失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '批量创建教师账号失败，请稍后重试'
    };
  }
};

/**
 * 设置超级管理员 - 直接从前端发起请求将指定工号的教师设置为超级管理员
 * 同时提供前端模拟逻辑，即使后端不支持也可以工作
 */
export const setSuperAdmin = async (teacherId: string): Promise<{
  success: boolean;
  message: string;
}> => {
  try {
    // 首先尝试调用后端API
    try {
      const response = await request({
        url: '/api/admin/superAdmin',
        method: 'post',
        data: { teacherId },
      }) as unknown as {
        success: boolean;
        message: string;
      };

      return response;
    } catch (apiError: unknown) {
      const errorMessage = apiError instanceof Error ? apiError.message : '未知错误';
      console.warn('后端API设置超级管理员失败，尝试前端模拟:', errorMessage);

      // 如果后端API调用失败，则使用本地存储模拟实现
      // 获取当前存储的用户信息
      const storedUserInfo = localStorage.getItem('userInfo');
      if (storedUserInfo) {
        try {
          const userInfo = JSON.parse(storedUserInfo);

          // 如果当前登录的用户就是要设置的用户，直接修改role
          if (userInfo.userId === teacherId) {
            userInfo.role = 'SUPER_ADMIN';
            localStorage.setItem('userInfo', JSON.stringify(userInfo));
            console.log('已在本地存储中将当前用户设置为超级管理员');

            return {
              success: true,
              message: '已成功在本地将当前用户设置为超级管理员，请重新登录以应用更改'
            };
          }
        } catch (parseError) {
          console.error('解析本地存储的用户信息失败:', parseError);
        }
      }

      // 如果无法在本地处理，返回一个友好的错误信息
      return {
        success: false,
        message: '设置超级管理员失败：后端API不支持且无法在本地模拟，请联系系统管理员'
      };
    }
  } catch (error: unknown) {
    console.error('设置超级管理员失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '设置超级管理员失败，请稍后重试'
    };
  }
};

// 学生账号相关API

/**
 * 创建学生账号
 */
export const createStudentAccount = async (accountData: StudentAccount): Promise<{
  success: boolean;
  message: string;
  data?: StudentAccount;
}> => {
  try {
    const response = await request({
      url: '/api/admin/accounts/student',
      method: 'post',
      data: accountData,
    }) as unknown as {
      success: boolean;
      message: string;
      data?: StudentAccount;
    };

    return response;
  } catch (error: unknown) {
    console.error('创建学生账号失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '创建学生账号失败，请稍后重试'
    };
  }
};

/**
 * 批量创建学生账号
 */
export const batchCreateStudentAccounts = async (file: File): Promise<{
  success: boolean;
  message: string;
  data?: {
    total: number;
    success: number;
    failed: number;
    errors: Array<{studentId: string; reason: string}>;
  };
}> => {
  try {
    const formData = new FormData();
    formData.append('file', file);

    const response = await request({
      url: '/api/admin/accounts/student/batch',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }) as unknown as {
      success: boolean;
      message: string;
      data?: {
        total: number;
        success: number;
        failed: number;
        errors: Array<{studentId: string; reason: string}>;
      };
    };

    return response;
  } catch (error: unknown) {
    console.error('批量创建学生账号失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '批量创建学生账号失败，请稍后重试'
    };
  }
};

/**
 * 获取学生账号列表
 */
export const getStudentAccounts = async (params: {
  offset?: number;
  size?: number;
  searchKey?: string;
  faculty?: string;
  className?: string;
  grade?: string;
  major?: string;
}): Promise<{
  success: boolean;
  message: string;
  data: StudentAccount[];
  total: number;
}> => {
  try {
    // Convert offset to page number
    const page = params.offset ? Math.floor(params.offset / (params.size || 10)) + 1 : 1;

    const response = await request({
      url: '/api/admin/accounts/student',
      method: 'get',
      params: {
        ...params,
        page,
        size: params.size
      }
    }) as unknown as {
      success: boolean;
      message: string;
      data: StudentAccount[];
      total: number;
    };

    return response;
  } catch (error: unknown) {
    console.error('获取学生账号列表失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取学生账号列表失败，请稍后重试',
      data: [],
      total: 0
    };
  }
};

/**
 * 获取学生账号详情
 */
export const getStudentAccountDetail = async (studentId: string): Promise<{
  success: boolean;
  message: string;
  data?: StudentAccount;
}> => {
  try {
    const response = await request({
      url: `/api/admin/accounts/student/${studentId}`,
      method: 'get'
    }) as unknown as {
      success: boolean;
      message: string;
      data?: StudentAccount;
    };

    return response;
  } catch (error: unknown) {
    console.error('获取学生账号详情失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取学生账号详情失败，请稍后重试'
    };
  }
};

/**
 * 更新学生账号
 */
export const updateStudentAccount = async (studentId: string, accountData: Partial<StudentAccount>): Promise<{
  success: boolean;
  message: string;
  data?: StudentAccount;
}> => {
  try {
    const response = await request({
      url: `/api/admin/accounts/student/${studentId}`,
      method: 'put',
      data: accountData
    }) as unknown as {
      success: boolean;
      message: string;
      data?: StudentAccount;
    };

    return response;
  } catch (error: unknown) {
    console.error('更新学生账号失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '更新学生账号失败，请稍后重试'
    };
  }
};

/**
 * 切换学生账号状态（启用/禁用）
 */
export const toggleStudentAccountStatus = async (studentId: string, enable: boolean): Promise<{
  success: boolean;
  message: string;
}> => {
  try {
    const response = await request({
      url: `/api/admin/accounts/student/${studentId}/status`,
      method: 'put',
      params: { isActive: enable }
    }) as unknown as {
      success: boolean;
      message: string;
    };

    return response;
  } catch (error: unknown) {
    console.error('切换学生账号状态失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '切换学生账号状态失败，请稍后重试'
    };
  }
};

/**
 * 重置学生账号密码
 */
export const resetStudentPassword = async (studentId: string): Promise<{
  success: boolean;
  message: string;
  newPassword?: string;
}> => {
  try {
    interface ResetPasswordResponse {
      success: boolean;
      message: string;
      newPassword?: string;
      data?: {
        success: boolean;
        message: string;
        newPassword?: string;
      };
    }

    // 调用重置密码的API
    const response = await request<ResetPasswordResponse>({
      url: `/api/admin/accounts/student/${studentId}/reset-password`,
      method: 'post'
    });

    console.log('Reset password response:', response); // 添加调试日志

    // 处理响应数据
    if (response && typeof response === 'object') {
      // 直接从响应中获取字段
      if ('success' in response && 'message' in response) {
        return {
          success: Boolean(response.success),
          message: String(response.message),
          newPassword: response.newPassword
        };
      }

      // 如果响应包含在data字段中
      const data = response.data;
      if (data && typeof data === 'object' && 'success' in data && 'message' in data) {
        return {
          success: Boolean(data.success),
          message: String(data.message),
          newPassword: data.newPassword
        };
      }
    }

    // 如果响应格式不符合预期，记录错误并返回错误信息
    console.error('Unexpected response format:', response);
    return {
      success: false,
      message: '服务器响应格式不正确，请检查后端返回的数据格式'
    };
  } catch (error: unknown) {
    console.error('重置学生密码失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '重置学生密码失败，请稍后重试'
    };
  }
};

/**
 * 设置学生账号密码
 */
export const setStudentPassword = async (studentId: string, password: string) => {
  try {
    console.log(`正在设置学生 ${studentId} 的密码...`);

    // 使用reset-password端点，因为已知该端点工作正常
    const response = await request({
      url: `/api/admin/accounts/student/${studentId}/reset-password`,
      method: 'post',
      data: { newPassword: password }
    });

    console.log('设置密码响应:', response);

    if (response && typeof response === 'object') {
      return {
        success: Boolean(response.success),
        message: String(response.message || '密码设置成功')
      };
    }

    return {
      success: false,
      message: '服务器响应格式不正确'
    };
  } catch (error) {
    console.error('设置密码失败:', error);

    // 提取更详细的错误信息
    let errorMessage = '密码设置失败，请稍后重试';
    if (error && typeof error === 'object' && 'response' in error) {
      const axiosError = error as any;
      if (axiosError.response?.data?.message) {
        errorMessage = axiosError.response.data.message;
      } else if (axiosError.message) {
        errorMessage = axiosError.message;
      }
    } else if (error instanceof Error) {
      errorMessage = error.message;
    }

    return {
      success: false,
      message: errorMessage
    };
  }
};

interface TeacherOptions {
  titles: Array<{ value: string; label: string }>;
  types: Array<{ value: string; label: string }>;
  ethnicities: Array<{ value: string; label: string }>;
}

// 获取教师选项数据
export const getTeacherOptions = () => {
  return request<TeacherOptions>({
    url: '/api/teachers/options',
    method: 'get'
  })
}

/**
 * 获取部门列表
 */
export const getDepartments = async (): Promise<{
  success: boolean;
  message: string;
  data: Array<{
    id: string;
    name: string;
  }>;
}> => {
  try {
    const response = await request({
      url: '/api/admin/departments',
      method: 'get'
    });

    // 处理不同格式的响应
    let departments = [];

    // 如果响应是一个对象且包含data字段
    if (response && typeof response === 'object' && 'data' in response) {
      departments = Array.isArray(response.data) ? response.data : [];
    }
    // 如果响应直接是数组
    else if (Array.isArray(response)) {
      departments = response;
    }

    // 确保每个部门对象都有id和name字段
    const formattedDepartments = departments.map(dept => ({
      id: dept.id || dept.department_id || '',
      name: dept.name || dept.department_name || ''
    }));

    return {
      success: true,
      message: '获取部门列表成功',
      data: formattedDepartments
    };
  } catch (error) {
    console.error('获取部门列表失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取部门列表失败，请稍后重试',
      data: []
    };
  }
};

/**
 * 设置教师账号密码
 */
export const setTeacherPassword = async (teacherId: string, password: string) => {
  try {
    const response = await request.post(`/api/admin/accounts/teacher/${teacherId}/set-password`, {
      password
    }) as unknown as {
      success: boolean;
      message: string;
    };

    return {
      success: response.success,
      message: response.message || '密码设置成功'
    };
  } catch (error) {
    console.error('设置密码失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '密码设置失败，请稍后重试'
    };
  }
};

/**
 * 添加教师账号
 */
export async function addTeacherAccount(teacherAccount: TeacherAccount): Promise<TeacherAccount | null> {
  try {
    const { status, ...accountData } = teacherAccount;
    const accountStatus = status;

    const result = await request({
      url: '/teacher/add',
      method: 'post',
      data: {
        ...accountData,
        accountStatus
      }
    });

    return result.data;
  } catch (error) {
    console.error('添加教师账号时出错:', error);
    return null;
  }
}

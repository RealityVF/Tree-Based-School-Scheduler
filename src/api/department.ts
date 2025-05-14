import request from '@/utils/request'
import type { Department } from '@/types/department'
import type { AxiosError } from 'axios'

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

/**
 * 获取部门列表
 */
export const getDepartments = async (): Promise<ApiResponse<Department[]>> => {
  try {
    console.log('开始请求院系列表...');
    // 检查用户信息和权限
    const userInfo = localStorage.getItem('userInfo');
    const token = localStorage.getItem('token');
    console.log('当前用户Token:', token ? '已存在' : '不存在');
    
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo);
        console.log('当前用户信息:', {
          role: user.role,
          permissions: user.permissions,
          username: user.username
        });
      } catch (e) {
        console.error('解析用户信息失败:', e);
      }
    } else {
      console.warn('未找到用户信息');
    }

    const response = await request({
      url: '/api/admin/departments',
      method: 'get'
    }) as ApiResponse<Department[]>;

    console.log('院系列表原始响应:', response);

    // 检查响应格式
    if (!response) {
      console.error('响应为空');
      throw new Error('获取院系列表失败：响应为空');
    }

    if (!Array.isArray(response.data)) {
      console.error('响应数据不是数组格式:', response.data);
      throw new Error('获取院系列表失败：数据格式错误');
    }

    console.log('成功获取院系列表，数量:', response.data.length);
    return response;
  } catch (error) {
    console.error('获取院系列表失败:', error);
    const axiosError = error as AxiosError;
    if (axiosError.response) {
      console.error('错误响应状态:', axiosError.response.status);
      console.error('错误响应数据:', axiosError.response.data);
      
      // 处理特定的错误状态
      if (axiosError.response.status === 403) {
        return {
          success: false,
          message: '您没有权限访问院系列表，请确认您的账号权限',
          data: []
        };
      }
    }
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取院系列表失败，请稍后重试',
      data: []
    };
  }
};

/**
 * 获取部门详情
 * @param id 部门ID
 */
export const getDepartmentById = async (id: string): Promise<{
  success: boolean;
  message: string;
  data: Department | null;
}> => {
  try {
    const response = await request({
      url: `/api/admin/departments/${id}`,
      method: 'get'
    });

    if (response && typeof response === 'object') {
      if ('data' in response) {
        // 响应是包装对象
        return {
          success: response.success || true,
          message: response.message || '获取部门详情成功',
          data: response.data
        };
      } else {
        // 响应直接是部门对象
        return {
          success: true,
          message: '获取部门详情成功',
          data: response
        };
      }
    }

    return {
      success: false,
      message: '获取部门详情失败，响应格式错误',
      data: null
    };
  } catch (error) {
    console.error('获取部门详情失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取部门详情失败，请稍后重试',
      data: null
    };
  }
};

/**
 * 添加院系
 * @param department 院系信息
 */
export const addDepartment = async (department: Department) => {
  return request({
    url: '/api/admin/departments',
    method: 'post',
    data: department
  })
}

/**
 * 更新院系
 * @param id 院系ID
 * @param department 院系信息
 */
export const updateDepartment = async (id: string, department: Department) => {
  return request({
    url: `/api/admin/departments/${id}`,
    method: 'put',
    data: department
  })
}

/**
 * 删除院系
 * @param id 院系ID
 */
export const deleteDepartment = async (id: string) => {
  return request({
    url: `/api/admin/departments/${id}`,
    method: 'delete'
  })
}

import request from '@/utils/request';
import type { Major, MajorQueryParams } from '@/types/major';

/**
 * 获取专业列表
 * @param params 查询参数
 */
export const getMajors = async (params?: MajorQueryParams) => {
  try {
    const response = await request({
      url: '/api/majors',
      method: 'get',
      params
    });

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

/**
 * 获取专业详情
 * @param id 专业ID
 */
export const getMajorById = async (id: string) => {
  try {
    const response = await request({
      url: `/api/majors/${id}`,
      method: 'get'
    });

    if (response && typeof response === 'object') {
      if ('data' in response) {
        return {
          success: response.success || true,
          message: response.message || '获取专业详情成功',
          data: response.data
        };
      } else {
        return {
          success: true,
          message: '获取专业详情成功',
          data: response
        };
      }
    }

    return {
      success: false,
      message: '获取专业详情失败，响应格式错误',
      data: null
    };
  } catch (error) {
    console.error('获取专业详情失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取专业详情失败，请稍后重试',
      data: null
    };
  }
};

/**
 * 根据院系ID获取专业列表
 * @param departmentId 院系ID
 */
export const getMajorsByDepartmentId = async (departmentId: string) => {
  try {
    console.log(`准备请求院系 ${departmentId} 的专业列表...`);
    const response = await request({
      url: `/api/majors/department/${departmentId}`,
      method: 'get'
    });

    console.log(`专业列表API响应:`, response);

    // 处理返回数据
    if (response && typeof response === 'object' && 'data' in response) {
      // 标准格式: { success: true, data: [...] }
      const result = {
        success: response.success !== false,
        message: response.message || '获取专业列表成功',
        data: Array.isArray(response.data) ? response.data.map(formatMajorData) : []
      };
      console.log(`处理后的专业数据:`, result);
      return result;
    }

    if (Array.isArray(response)) {
      // 直接返回数组
      const result = {
        success: true,
        message: '获取专业列表成功',
        data: response.map(formatMajorData)
      };
      console.log(`处理后的专业数据:`, result);
      return result;
    }

    console.log(`无法识别的响应格式，返回空数组`);
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

/**
 * 格式化专业数据，统一字段名
 */
function formatMajorData(majorData: any) {
  return {
    ...majorData,
    // 确保id和name字段存在
    id: majorData.id || majorData.majorId || '',
    name: majorData.name || majorData.majorName || '',
    // 确保departmentId字段存在
    departmentId: majorData.departmentId || '',
  };
}

/**
 * 根据院系ID获取专业列表（别名）
 * @param departmentId 院系ID
 */
export const getMajorsByDepartment = getMajorsByDepartmentId;

/**
 * 添加专业
 * @param major 专业信息
 */
export const addMajor = async (major: Major) => {
  return request({
    url: '/api/majors',
    method: 'post',
    data: major
  });
};

/**
 * 更新专业
 * @param id 专业ID
 * @param major 专业信息
 */
export const updateMajor = async (id: string, major: Major) => {
  return request({
    url: `/api/majors/${id}`,
    method: 'put',
    data: major
  });
};

/**
 * 删除专业
 * @param id 专业ID
 */
export const deleteMajor = async (id: string) => {
  return request({
    url: `/api/majors/${id}`,
    method: 'delete'
  });
};

/**
 * 导出专业列表为Excel
 * @param params 查询参数
 */
export const exportMajorsToExcel = async (params?: MajorQueryParams) => {
  return request({
    url: '/api/majors/export',
    method: 'get',
    params,
    responseType: 'blob'
  });
};

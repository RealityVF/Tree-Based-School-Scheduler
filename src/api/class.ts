import request from '@/utils/request';
import type { Class, ClassQueryParams } from '@/types/class';

/**
 * 获取班级列表
 * @param params 查询参数
 */
export const getClasses = async (params?: ClassQueryParams) => {
  try {
    const response = await request({
      url: '/api/classes',
      method: 'get',
      params
    });

    if (response && typeof response === 'object' && 'data' in response) {
      return {
        success: response.success || true,
        message: response.message || '获取班级列表成功',
        data: Array.isArray(response.data) ? response.data : []
      };
    }

    if (Array.isArray(response)) {
      return {
        success: true,
        message: '获取班级列表成功',
        data: response
      };
    }

    return {
      success: true,
      message: '获取班级列表成功',
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

/**
 * 获取班级详情
 * @param id 班级ID
 */
export const getClassById = async (id: string) => {
  try {
    const response = await request({
      url: `/api/classes/${id}`,
      method: 'get'
    });

    if (response && typeof response === 'object') {
      if ('data' in response) {
        return {
          success: response.success || true,
          message: response.message || '获取班级详情成功',
          data: response.data
        };
      } else {
        return {
          success: true,
          message: '获取班级详情成功',
          data: response
        };
      }
    }

    return {
      success: false,
      message: '获取班级详情失败，响应格式错误',
      data: null
    };
  } catch (error) {
    console.error('获取班级详情失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取班级详情失败，请稍后重试',
      data: null
    };
  }
};

/**
 * 根据专业ID获取班级列表
 * @param majorId 专业ID
 */
export const getClassesByMajorId = async (majorId: string) => {
  try {
    console.log(`准备请求专业 ${majorId} 的班级列表...`);
    const response = await request({
      url: `/api/classes/major/${majorId}`,
      method: 'get'
    });

    console.log(`班级列表API响应:`, response);

    // 处理返回数据
    if (response && typeof response === 'object' && 'data' in response) {
      // 标准格式: { success: true, data: [...] }
      const result = {
        success: response.success !== false,
        message: response.message || '获取班级列表成功',
        data: Array.isArray(response.data) ? response.data.map(formatClassData) : []
      };
      console.log(`处理后的班级数据:`, result);
      return result;
    }

    if (Array.isArray(response)) {
      // 直接返回数组
      const result = {
        success: true,
        message: '获取班级列表成功',
        data: response.map(formatClassData)
      };
      console.log(`处理后的班级数据:`, result);
      return result;
    }

    console.log(`无法识别的响应格式，返回空数组`);
    return {
      success: true,
      message: '获取班级列表成功',
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

/**
 * 格式化班级数据，统一字段名
 */
function formatClassData(classData: any) {
  return {
    ...classData,
    // 确保id和name字段存在
    id: classData.id || classData.classId || '',
    name: classData.name || classData.className || '',
  };
}

/**
 * 根据专业ID获取班级列表（别名）
 * @param majorId 专业ID
 */
export const getClassesByMajor = getClassesByMajorId;

/**
 * 添加班级
 * @param classData 班级信息
 */
export const addClass = async (classData: Class) => {
  try {
    const response = await request({
      url: '/api/classes',
      method: 'post',
      data: classData
    });

    if (response && typeof response === 'object') {
      return {
        success: response.success || true,
        message: response.message || '添加班级成功',
        data: response.data || null
      };
    }

    return {
      success: false,
      message: '添加班级失败，响应格式错误',
      data: null
    };
  } catch (error) {
    console.error('添加班级失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '添加班级失败，请稍后重试',
      data: null
    };
  }
};

/**
 * 更新班级
 * @param id 班级ID
 * @param classData 班级信息
 */
export const updateClass = async (id: string, classData: Class) => {
  try {
    const response = await request({
      url: `/api/classes/${id}`,
      method: 'put',
      data: classData
    });

    if (response && typeof response === 'object') {
      return {
        success: response.success || true,
        message: response.message || '更新班级成功',
        data: response.data || null
      };
    }

    return {
      success: false,
      message: '更新班级失败，响应格式错误',
      data: null
    };
  } catch (error) {
    console.error('更新班级失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '更新班级失败，请稍后重试',
      data: null
    };
  }
};

/**
 * 删除班级
 * @param id 班级ID
 */
export const deleteClass = async (id: string) => {
  try {
    const response = await request({
      url: `/api/classes/${id}`,
      method: 'delete'
    });

    if (response && typeof response === 'object') {
      return {
        success: response.success || true,
        message: response.message || '删除班级成功',
        data: null
      };
    }

    return {
      success: false,
      message: '删除班级失败，响应格式错误',
      data: null
    };
  } catch (error) {
    console.error('删除班级失败:', error);
    return {
      success: false,
      message: error instanceof Error ? error.message : '删除班级失败，请稍后重试',
      data: null
    };
  }
};

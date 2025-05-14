import request from '@/utils/request'
import type { 
  CourseResource, 
  ResourceListParams as ImportedResourceListParams, 
  ResourceListResponse,
  ResourceUploadParams,
  ResourceUploadResponse,
  ResourceDeleteResponse,
  CourseListResponse
} from '@/types/resources'

export interface UploadResourceParams {
  courseId: string;
  title: string;
  description?: string;
  resourceType: 'DOCUMENT' | 'VIDEO' | 'IMAGE' | 'AUDIO' | 'OTHER';
  file: File;
}

// 重命名本地接口以避免与导入的接口冲突
export interface LocalResourceListParams {
  courseId?: string;
  resourceType?: string;
  page?: number;
  pageSize?: number;
  keyword?: string;
}

/**
 * 上传课程资源
 * @param params 上传参数
 * @returns 上传结果
 */
export async function uploadCourseResource(params: ResourceUploadParams): Promise<ResourceUploadResponse> {
  try {
    const formData = new FormData()
    formData.append('courseId', params.courseId)
    formData.append('title', params.title)
    
    if (params.description) {
      formData.append('description', params.description)
    }
    
    formData.append('resourceType', params.resourceType)
    formData.append('file', params.file)
    
    const response = await request({
      url: '/api/resources/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }) as unknown as ResourceUploadResponse
    
    return response
  } catch (error: unknown) {
    console.error('上传课程资源失败:', error)
    return {
      success: false,
      message: error instanceof Error ? error.message : '上传失败，请稍后重试'
    }
  }
}

/**
 * 获取课程资源列表
 * @param params 查询参数
 * @returns 资源列表
 */
export async function getCourseResources(params: ImportedResourceListParams): Promise<ResourceListResponse> {
  try {
    const response = await request({
      url: '/api/resources',
      method: 'get',
      params
    }) as unknown as ResourceListResponse
    
    return response
  } catch (error: unknown) {
    console.error('获取课程资源列表失败:', error)
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取资源列表失败，请稍后重试',
      data: [],
      total: 0
    }
  }
}

/**
 * 删除课程资源
 * @param resourceId 资源ID
 * @returns 删除结果
 */
export async function deleteCourseResource(resourceId: string): Promise<ResourceDeleteResponse> {
  try {
    const response = await request({
      url: `/api/resources/${resourceId}`,
      method: 'delete'
    }) as unknown as ResourceDeleteResponse
    
    return response
  } catch (error: unknown) {
    console.error('删除课程资源失败:', error)
    return {
      success: false,
      message: error instanceof Error ? error.message : '删除失败，请稍后重试'
    }
  }
}

/**
 * 获取课程资源详情
 * @param resourceId 资源ID
 * @returns 资源详情
 */
export async function getCourseResourceDetail(resourceId: string): Promise<{ success: boolean; message?: string; data?: CourseResource }> {
  try {
    const response = await request({
      url: `/api/resources/${resourceId}`,
      method: 'get'
    }) as unknown as { success: boolean; message?: string; data?: CourseResource }
    
    return response
  } catch (error: unknown) {
    console.error('获取课程资源详情失败:', error)
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取资源详情失败，请稍后重试'
    }
  }
}

/**
 * 获取用户可访问的课程列表
 * @returns 课程列表
 */
export async function getUserCourses(): Promise<CourseListResponse> {
  try {
    const response = await request({
      url: '/api/courses/user',
      method: 'get'
    }) as unknown as CourseListResponse
    
    return response
  } catch (error: unknown) {
    console.error('获取用户课程列表失败:', error)
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取课程列表失败，请稍后重试',
      data: []
    }
  }
} 
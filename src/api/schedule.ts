import request from '@/utils/request'
import type {
  ScheduleChangeForm,
  ScheduleChangeQueryParams,
  ScheduleChangeListResponse,
  ScheduleChangeDetailResponse,
  ScheduleChangeSubmitResponse,
  ScheduleChangeReviewParams,
  ScheduleChangeReviewResponse
} from '@/types/schedule'

/**
 * 获取调课申请列表
 * @param params 查询参数
 */
export async function getScheduleChangeList(params: ScheduleChangeQueryParams): Promise<ScheduleChangeListResponse> {
  try {
    const response = await request({
      url: '/api/schedule/change',
      method: 'get',
      params
    }) as unknown as ScheduleChangeListResponse

    return response
  } catch (error: unknown) {
    console.error('获取调课申请列表失败:', error)
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取调课申请列表失败，请稍后重试',
      data: [],
      total: 0
    }
  }
}

/**
 * 获取调课申请详情
 * @param id 申请ID
 */
export async function getScheduleChangeDetail(id: string): Promise<ScheduleChangeDetailResponse> {
  try {
    const response = await request({
      url: `/api/schedule/change/${id}`,
      method: 'get'
    }) as unknown as ScheduleChangeDetailResponse

    return response
  } catch (error: unknown) {
    console.error('获取调课申请详情失败:', error)
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取调课申请详情失败，请稍后重试'
    }
  }
}

/**
 * 提交调课申请
 * @param data 调课申请表单
 */
export async function submitScheduleChange(data: ScheduleChangeForm): Promise<ScheduleChangeSubmitResponse> {
  try {
    const response = await request({
      url: '/api/schedule/change',
      method: 'post',
      data
    }) as unknown as ScheduleChangeSubmitResponse

    return response
  } catch (error: unknown) {
    console.error('提交调课申请失败:', error)
    return {
      success: false,
      message: error instanceof Error ? error.message : '提交调课申请失败，请稍后重试'
    }
  }
}

/**
 * 取消调课申请
 * @param id 申请ID
 */
export async function cancelScheduleChange(id: string): Promise<ScheduleChangeReviewResponse> {
  try {
    const response = await request({
      url: `/api/schedule/change/${id}/cancel`,
      method: 'post'
    }) as unknown as ScheduleChangeReviewResponse

    return response
  } catch (error: unknown) {
    console.error('取消调课申请失败:', error)
    return {
      success: false,
      message: error instanceof Error ? error.message : '取消调课申请失败，请稍后重试'
    }
  }
}

/**
 * 审核调课申请
 * @param params 审核参数
 */
export async function reviewScheduleChange(params: ScheduleChangeReviewParams): Promise<ScheduleChangeReviewResponse> {
  try {
    const response = await request({
      url: `/api/schedule/change/${params.id}/review`,
      method: 'post',
      data: {
        status: params.status,
        rejectReason: params.rejectReason
      }
    }) as unknown as ScheduleChangeReviewResponse

    return response
  } catch (error: unknown) {
    console.error('审核调课申请失败:', error)
    return {
      success: false,
      message: error instanceof Error ? error.message : '审核调课申请失败，请稍后重试'
    }
  }
}

/**
 * 获取教师课程列表
 */
export async function getTeacherCourses(): Promise<{ success: boolean; message?: string; data: Array<{ id: string; name: string }> }> {
  try {
    const response = await request({
      url: '/api/teacher/courses',
      method: 'get'
    }) as unknown as { success: boolean; message?: string; data: Array<{ id: string; name: string }> }

    return response
  } catch (error: unknown) {
    console.error('获取教师课程列表失败:', error)
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取教师课程列表失败，请稍后重试',
      data: []
    }
  }
}

/**
 * 获取课程班级列表
 * @param courseId 课程ID
 */
export async function getCourseClasses(courseId: string): Promise<{ success: boolean; message?: string; data: Array<{ id: string; name: string }> }> {
  try {
    const response = await request({
      url: `/api/courses/${courseId}/classes`,
      method: 'get'
    }) as unknown as { success: boolean; message?: string; data: Array<{ id: string; name: string }> }

    return response
  } catch (error: unknown) {
    console.error('获取课程班级列表失败:', error)
    return {
      success: false,
      message: error instanceof Error ? error.message : '获取课程班级列表失败，请稍后重试',
      data: []
    }
  }
}

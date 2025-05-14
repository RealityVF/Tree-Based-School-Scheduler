// 调课申请状态
export type ScheduleChangeStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

// 调课申请信息
export interface ScheduleChangeRequest {
  id: string;
  teacherId: string;
  teacherName: string;
  courseId: string;
  courseName: string;
  classId?: string;
  className?: string;
  originalDate: string;
  originalSection: number; // 原课程节数
  targetDate: string;
  targetSection: number; // 调整后节数
  reason: string;
  status: ScheduleChangeStatus;
  applyTime: string;
  reviewTime?: string;
  reviewerId?: string;
  reviewerName?: string;
  rejectReason?: string;
}

// 调课申请表单
export interface ScheduleChangeForm {
  courseId: string;
  classId?: string;
  originalDate: string;
  originalSection: number; // 原课程节数
  targetDate: string;
  targetSection: number; // 调整后节数
  reason: string;
}

// 调课申请查询参数
export interface ScheduleChangeQueryParams {
  teacherId?: string;
  courseId?: string;
  classId?: string;
  status?: ScheduleChangeStatus;
  startDate?: string;
  endDate?: string;
  page: number;
  pageSize: number;
}

// 调课申请列表响应
export interface ScheduleChangeListResponse {
  success: boolean;
  message?: string;
  data: ScheduleChangeRequest[];
  total: number;
}

// 调课申请详情响应
export interface ScheduleChangeDetailResponse {
  success: boolean;
  message?: string;
  data?: ScheduleChangeRequest;
}

// 提交调课申请响应
export interface ScheduleChangeSubmitResponse {
  success: boolean;
  message?: string;
  data?: ScheduleChangeRequest;
}

// 审核调课申请参数
export interface ScheduleChangeReviewParams {
  id: string;
  status: 'APPROVED' | 'REJECTED';
  rejectReason?: string;
}

// 审核调课申请响应
export interface ScheduleChangeReviewResponse {
  success: boolean;
  message?: string;
} 
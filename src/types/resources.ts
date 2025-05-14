export interface CourseResource {
  id: string;
  title: string;
  description?: string;
  fileName: string;
  fileSize: number;
  fileType: string;
  fileUrl: string;
  resourceType: 'DOCUMENT' | 'VIDEO' | 'IMAGE' | 'AUDIO' | 'OTHER';
  courseId: string;
  courseName: string;
  uploaderId: string;
  uploaderName: string;
  uploadTime: string;
}

export interface ResourceListParams {
  courseId?: string;
  resourceType?: string;
  keyword?: string;
  page: number;
  pageSize: number;
}

export interface ResourceListResponse {
  success: boolean;
  message?: string;
  data: CourseResource[];
  total: number;
}

export interface ResourceUploadParams {
  courseId: string;
  title: string;
  description?: string;
  resourceType: 'DOCUMENT' | 'VIDEO' | 'IMAGE' | 'AUDIO' | 'OTHER';
  file: File;
}

export interface ResourceUploadResponse {
  success: boolean;
  message?: string;
  data?: CourseResource;
}

export interface ResourceDeleteResponse {
  success: boolean;
  message?: string;
}

export interface CourseOption {
  id: string;
  name: string;
}

export interface CourseListResponse {
  success: boolean;
  message?: string;
  data: CourseOption[];
} 
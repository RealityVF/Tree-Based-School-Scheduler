import request from '@/utils/request'
import type { Student } from '@/types/student'

/**
 * 添加学生
 * @param student 学生信息
 */
export const addStudent = async (student: Student) => {
  return request({
    url: '/api/admin/students',
    method: 'post',
    data: student
  })
}

/**
 * 更新学生信息
 * @param id 学号
 * @param student 学生信息
 */
export const updateStudent = async (id: string, student: Student) => {
  return request({
    url: `/api/admin/students/${id}`,
    method: 'put',
    data: student
  })
}

/**
 * 删除学生
 * @param id 学号
 */
export const deleteStudent = async (id: string) => {
  return request({
    url: `/api/admin/students/${id}`,
    method: 'delete'
  })
}

/**
 * 获取学生详情
 * @param id 学号
 */
export const getStudentById = async (id: string) => {
  return request({
    url: `/api/admin/students/${id}`,
    method: 'get'
  })
}

/**
 * 分页查询学生
 * @param params 查询参数
 */
export const getStudents = async (params: {
  page?: number
  size?: number
  departmentId?: string
  majorId?: string
  classId?: string
  studentStatus?: string
  keyword?: string
}): Promise<{
  success: boolean
  data: Student[]
  total: number
  message?: string
}> => {
  return request({
    url: '/api/admin/students',
    method: 'get',
    params
  })
}

/**
 * 获取班级学生列表
 * @param classId 班级ID
 */
export const getStudentsByClassId = async (classId: string) => {
  return request({
    url: `/api/admin/students/class/${classId}`,
    method: 'get'
  })
}

/**
 * 获取专业学生列表
 * @param majorId 专业ID
 */
export const getStudentsByMajorId = async (majorId: string) => {
  return request({
    url: `/api/admin/students/major/${majorId}`,
    method: 'get'
  })
}

/**
 * 批量导入学生
 * @param formData 包含Excel文件的FormData
 */
export const batchImportStudents = async (formData: FormData) => {
  return request({
    url: '/api/admin/students/batch-import',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: formData
  })
}

/**
 * 下载导入模板
 */
export const downloadImportTemplate = () => {
  return request({
    url: '/api/admin/students/template',
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 获取当前学生个人信息
 */
export const getCurrentStudentProfile = async () => {
  return request({
    url: '/api/student/profile',
    method: 'get'
  })
}

/**
 * 更新当前学生个人信息
 * @param student 学生信息
 */
export const updateCurrentStudentProfile = async (student: Partial<Student>) => {
  return request({
    url: '/api/student/profile',
    method: 'put',
    data: student
  })
}

/**
 * 修改当前学生密码
 * @param oldPassword 旧密码
 * @param newPassword 新密码
 */
export const changeStudentPassword = async (oldPassword: string, newPassword: string) => {
  return request({
    url: '/api/student/password',
    method: 'put',
    data: {
      oldPassword,
      newPassword
    }
  })
}

/**
 * 学生信息接口
 */
export interface Student {
  id: string; // 学号(格式：年份4位+部门号4位+专业号2位+班级号2位+学生编号2位)
  name: string; // 姓名
  gender: '男' | '女'; // 性别
  idCard?: string; // 身份证号
  birthday?: Date | string | null; // 出生日期
  ethnicity?: string; // 民族
  politicalStatus?: string; // 政治面貌
  classId: string; // 所属班级
  majorId: string; // 所属专业
  departmentId: string; // 所属院系
  educationLevel: string; // 培养层次
  enrollmentDate: Date | string; // 入学日期
  expectedGraduation?: Date | string | null; // 预计毕业日期
  studentStatus?: string; // 学籍状态
  isActive: boolean; // 是否在校
  phone?: string; // 联系电话
  email?: string; // 电子邮箱
  address?: string; // 家庭住址
  notes?: string; // 备注
  createdAt?: Date | string; // 创建时间
  updatedAt?: Date | string; // 更新时间
}

/**
 * 学生账号接口
 */
export interface StudentAccount {
  studentId: string; // 学号
  username: string; // 用户名
  passwordHash?: string; // 密码哈希（仅在服务端使用）
  lastLogin?: Date | string | null; // 最后登录时间
  isActive: boolean; // 是否激活
  passwordUpdatedAt?: Date | string | null; // 密码更新时间
}

/**
 * 学生查询参数
 */
export interface StudentQueryParams {
  page?: number;
  size?: number;
  departmentId?: string;
  majorId?: string;
  classId?: string;
  studentStatus?: string;
  keyword?: string;
}

/**
 * 学生分页结果
 */
export interface StudentPageResult {
  records: Student[];
  total: number;
  size: number;
  current: number;
}

/**
 * 批量导入结果
 */
export interface BatchImportResult {
  success: boolean;
  message: string;
  totalCount: number;
  successCount: number;
  errors?: Array<{
    studentId: string;
    reason: string;
  }>;
}

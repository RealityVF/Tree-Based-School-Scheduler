// 性别枚举
export enum Gender {
  男 = '男',
  女 = '女'
}

// 教师账号类型定义
export interface TeacherAccount {
  id: string;
  teacherId: string;
  name: string;
  englishName?: string;
  gender: Gender;
  ethnicity?: string;
  title?: string;
  departmentId: string;
  departmentName?: string;
  isExternal: boolean;
  teacherType: string;
  satisfaction: boolean;
  phone?: string;
  email?: string;
  maxWeeklyHours?: number;
  researchDirection?: string;
  isActive: boolean;
  status?: AccountStatus;
  createdAt?: string;
  updatedAt?: string;
  lastLogin?: string;
}

// 账号状态枚举
export enum AccountStatus {
  ENABLED = 'ENABLED',
  DISABLED = 'DISABLED'
}

// 添加角色类型枚举
export enum RoleType {
  TEACHER = 'TEACHER',           // 普通教师
  ADMIN = 'ADMIN',               // 普通管理员（有部分管理权限）
  SUPER_ADMIN = 'SUPER_ADMIN'    // 超级管理员（拥有所有权限）
}

// 添加角色类型选项
export const ROLE_TYPE_OPTIONS = [
  { value: RoleType.TEACHER, label: '普通教师' },
  { value: RoleType.ADMIN, label: '普通管理员' },
  { value: RoleType.SUPER_ADMIN, label: '超级管理员' }
];

// 教职工类别选项
export const TEACHER_TYPES = [
  { value: '校本部教职工', label: '校本部教职工' },
  { value: '外聘', label: '外聘' },
  { value: '援疆教师', label: '援疆教师' },
  { value: '其他', label: '其他' }
];

// 民族选项
export const ETHNICITY_OPTIONS = [
  { value: '汉族', label: '汉族' },
  { value: '回族', label: '回族' },
  { value: '维吾尔族', label: '维吾尔族' },
  { value: '哈萨克族', label: '哈萨克族' },
  { value: '其他', label: '其他' }
];

// 教师职称选项
export const TEACHER_TITLES = [
  { value: '教授', label: '教授' },
  { value: '副教授', label: '副教授' },
  { value: '讲师', label: '讲师' },
  { value: '助教', label: '助教' },
  { value: '技工学校教师', label: '技工学校教师' },
  { value: '其他', label: '其他' }
];

// 创建账号响应
export interface CreateAccountResponse {
  success: boolean;
  message: string;
  data?: TeacherAccount;
}

// 部门选项
export const DEPARTMENT_OPTIONS = [
  { value: '1001', label: '化学与材料工程学院' },
  { value: '1002', label: '计算机学院' },
  { value: '1003', label: '数学学院' },
  { value: '1004', label: '物理学院' },
  { value: '1005', label: '生物学院' },
  { value: '1006', label: '行政部门' },
  { value: '1007', label: '其他部门' }
];

// 添加学生账号类型
export interface StudentAccount {
  studentId: string;
  username: string;
  name: string;
  gender: string;
  idCard: string;
  birthday: string;
  ethnicity: string;
  politicalStatus: string;
  classId: string;
  majorId: string;
  departmentId: string;
  educationLevel: string;
  enrollmentDate: string;
  expectedGraduation: string;
  studentStatus: string;
  isActive: boolean;
  phone: string;
  email: string;
  address: string;
  notes: string;
  createdAt: string;
  updatedAt: string;
  className: string;
  major: string;
  faculty: string;
  grade: string;
  counselor: string;
}

// 政治面貌选项
export const POLITICAL_STATUS_OPTIONS = [
  { value: 'party_member', label: '中共党员' },
  { value: 'probationary_member', label: '中共预备党员' },
  { value: 'league_member', label: '共青团员' },
  { value: 'democratic_party', label: '民主党派' },
  { value: 'masses', label: '群众' }
];

// 性别选项
export const GENDER_OPTIONS = [
  { value: '男', label: '男' },
  { value: '女', label: '女' }
];

// 年级选项
export const GRADE_OPTIONS = [
  { value: '2023', label: '2023级' },
  { value: '2022', label: '2022级' },
  { value: '2021', label: '2021级' },
  { value: '2020', label: '2020级' }
];

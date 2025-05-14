/**
 * 班级信息
 */
export interface Class {
  /** 班级编号(格式：年份4位+部门号4位+专业号2位+班级号2位) */
  id?: string;
  /** 班级名称 */
  name?: string;
  /** 班级编号(后端返回格式) */
  classId?: string;
  /** 班级名称(后端返回格式) */
  className?: string;
  /** 班级简称 */
  shortName?: string;
  /** 所属专业ID */
  majorId: string;
  /** 专业方向ID */
  directionId?: string;
  /** 年级 */
  grade?: number;
  /** 培养层次 */
  educationLevel?: string;
  /** 班级类别 */
  classType?: string;
  /** 辅导员工号 */
  counselorId?: string;
  /** 班主任工号 */
  headTeacherId?: string;
  /** 预计毕业年度 */
  expectedGraduation?: number;
  /** 是否毕业 */
  isGraduated?: boolean;
  /** 班级人数 */
  studentCount?: number;
  /** 男生人数 */
  maleCount?: number;
  /** 女生人数 */
  femaleCount?: number;
  /** 班级最大人数 */
  maxStudentCount?: number;
  /** 入学年份 */
  admissionYear?: number;
  /** 入学年份(后端返回格式) */
  startYear?: any;
  /** 班主任联系电话 */
  headTeacherPhone?: string;
  /** 毕业学年学期 */
  graduationTerm?: string;
  /** 是否扩招 */
  isExpanded?: boolean;
  /** 学业导师 */
  academicAdvisor?: string;
  /** 创建时间 */
  createdAt?: string;
  /** 更新时间 */
  updatedAt?: string;
}

/**
 * 班级查询参数
 */
export interface ClassQueryParams {
  /** 所属专业ID */
  majorId?: string;
  /** 年级 */
  grade?: number;
  /** 培养层次 */
  educationLevel?: string;
  /** 班级类别 */
  classType?: string;
  /** 是否毕业 */
  isGraduated?: boolean;
  /** 关键词搜索 */
  keyword?: string;
}

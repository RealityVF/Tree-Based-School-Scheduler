/**
 * 部门/院系信息
 */
export interface Department {
  /** 部门/院系ID */
  id: string;
  /** 部门/院系名称 */
  name: string;
  /** 是否启用 */
  isEnabled: number;
  /** 创建时间 */
  createdTime: string;
  /** 更新时间 */
  updatedTime: string;
  /** 部门类型 */
  type: string;
  /** 部门分类 */
  category: string;
  /** 父级部门ID */
  parentId: string | null;
  /** 部门编码 */
  code: string | null;
  /** 是否教学单位 */
  isTeaching: number;
  /** 联系电话 */
  phone: string;
  /** 部门图标 */
  icon: string;
  /** 是否激活 */
  isActive: number;
}

/**
 * 部门查询参数
 */
export interface DepartmentQueryParams {
  /** 父级部门ID */
  parentId?: string;
  /** 关键词搜索 */
  keyword?: string;
  /** 是否启用 */
  isActive?: boolean;
  /** 部门类型 */
  type?: string;
  /** 部门分类 */
  category?: string;
}

/**
 * 院系树接口
 */
export interface DepartmentTree extends Department {
  children?: DepartmentTree[];
}

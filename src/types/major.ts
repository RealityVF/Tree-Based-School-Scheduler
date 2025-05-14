/**
 * 专业信息
 */
export interface Major {
  /**
   * 专业ID
   */
  majorId: string;
  /**
   * 专业名称
   */
  majorName: string;
  /**
   * 所属院系ID
   */
  departmentId: string;
  /**
   * 专业代码
   */
  code?: string;
  /**
   * 专业描述
   */
  description?: string;
  /**
   * 是否启用
   */
  isActive?: boolean;
  /**
   * 创建时间
   */
  createdAt?: string;
  /**
   * 更新时间
   */
  updatedAt?: string;
}

/**
 * 专业查询参数
 */
export interface MajorQueryParams {
  /**
   * 所属院系ID
   */
  departmentId?: string;
  /**
   * 关键词搜索
   */
  keyword?: string;
  /**
   * 是否启用
   */
  isActive?: boolean;
}

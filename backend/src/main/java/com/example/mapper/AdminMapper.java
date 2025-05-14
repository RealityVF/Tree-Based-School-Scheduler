package com.example.mapper;

import com.example.entity.Admin;
import org.apache.ibatis.annotations.*;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminMapper {
    
    /**
     * 根据ID查询管理员
     */
    Admin findByUsername(@Param("username") String id);
    
    /**
     * 更新最后登录时间
     */
    void updateLastLogin(@Param("id") String id, @Param("lastLogin") LocalDateTime lastLogin);
    
    /**
     * 查询管理员列表
     */
    List<Admin> selectAdminList(Admin query);
    
    /**
     * 根据ID查询管理员
     */
    Admin selectAdminById(@Param("id") String id);
    
    /**
     * 新增管理员
     */
    int insertAdmin(Admin admin);
    
    /**
     * 更新管理员信息
     */
    int updateAdmin(Admin admin);
    
    /**
     * 删除管理员
     */
    int deleteAdmin(@Param("id") String id);
} 
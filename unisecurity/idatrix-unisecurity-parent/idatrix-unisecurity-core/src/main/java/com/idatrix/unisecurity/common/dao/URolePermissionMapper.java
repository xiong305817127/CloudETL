package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.common.domain.URolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface URolePermissionMapper {

    int insert(URolePermission record);

    int insertSelective(URolePermission record);

    List<URolePermission> findRolePermissionByPid(Long id);

    List<URolePermission> findRolePermissionByRid(Long id);

    List<URolePermission> find(URolePermission entity);

    // 根据权限ID删除
    int deleteByPid(Long id);

    // 根据角色ID删除
    int deleteByRid(Long id);

    int delete(URolePermission entity);

    // 根据角色ID删除关联关系
    int deleteByRids(@Param("roleIds") String roleIds);

	void batchInsert(Map map);

	// 角色关联权限
    int roleRelationPermission(Map map);

    // 给租户管理员给予权限
    void permissionToRenterAdminRole(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds, @Param("date") Date date);

    // 根据租户开通系统权限去同步租户下所有（不包括租户管理员角色）的角色权限
    void roleSynchPermissionByRenter(@Param("renterId") Long renterId, @Param("systemIds") String[] systemIds);
}
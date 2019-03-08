package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.common.domain.UPermission;
import com.idatrix.unisecurity.permission.bo.UPermissionBo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UPermissionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UPermission record);

    int insertSelective(UPermission record);

    UPermission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UPermission record);

    int updateByPrimaryKey(UPermission record);

    // 根据角色ID查询所对应的权限
    List<UPermissionBo> selectPermissionByRoleId(Long id);

    // 根据用户ID获取权限的Set集合
    Set<String> findPermissionByUserId(Long id);

    List<UPermission> selectPermByUserIdAndCid(Map<String,Object> params);

    List<UPermission> selectPermissionByUserId(Long userId);

    List<UPermission> getSystemPermission();

    int findPermission(Map<String, String> map);

    Set<Long> getChildrenById(Long id);

    Set<Long> getAdminRoleIdsByClientSystemId(String clientSystemId);

    Set<Long> findSystemByClientId(String clientSystemId);
    
    List<UPermission> selectPermByCid(@Param("cid")String cid);

	Set<String> findRentPermissionByUserId(Long userId);

	List<Long> findPermissionIdsBySystemIds(String[] systemIds);
}
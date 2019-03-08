package com.idatrix.unisecurity.permission.service;

import com.idatrix.unisecurity.common.domain.UPermission;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.permission.bo.UPermissionBo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PermissionService {

    int deleteByPrimaryKey(Long id);

    UPermission insert(UPermission record);

    /**
     * 新增一个权限
     * @param record
     * @return
     */
    UPermission insertSelective(UPermission record);

    UPermission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UPermission record);

    int updateByPrimaryKey(UPermission record);

    Integer deletePermissionById(String ids);

    /**
     * 分页查询权限列表
     * @param resultMap
     * @param pageNo
     * @param pageSize
     * @return
     */
    Pagination<UPermission> findPage(Map<String, Object> resultMap, Integer pageNo,
                                     Integer pageSize);

    List<UPermissionBo> selectPermissionByRoleId(Long id);

    Integer addPermission2Role(Long roleId, String ids);

    Integer deleteByRids(String roleIds);

    Set<String> findPermissionByUserId(Long userId);

    List<UPermission> selectPermByUserIdAndCid(Long userId,String cid);

    /**
     * 根据用户id查询用户权限
     * @param userId
     * @return
     */
    List<UPermission> selectPermissionByUserId(Long userId);

    /**
     * 获取所有的系统级别权限（一级权限）
     * @return
     */
    List<UPermission> getSystemPermission();

    /**
     * 根据父类id和资源url判断当前资源是否存在
     * @param map
     * @return
     */
    int findPermission(Map<String, String> map);
    
    List<UPermission> selectPermByCid(String cid);

	Set<String> findRentPermissionByUserId(Long userId);

    List<UPermission> selectPermissionByUserIdAndSystemId(Long userId, String cid);
}

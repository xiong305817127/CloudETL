package com.idatrix.unisecurity.permission.service;

import com.idatrix.unisecurity.common.domain.URole;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.permission.bo.RolePermissionAllocationBo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoleService {

    int deleteByPrimaryKey(Long id);

    int insert(URole record);

    int insertSelective(URole record);

    int updateByPrimaryKeySelective(URole record);

    URole selectByPrimaryKey(Long id);

    int updateByPrimaryKey(URole record);

    Pagination<URole> findPage(Map<String, Object> resultMap, Integer pageNo,
                               Integer pageSize);

    ResultVo findPage(Integer page, Integer size, Long renterId, String key);

    Integer deleteRoleById(String ids) throws Exception;

    Pagination<RolePermissionAllocationBo> findRoleAndPermissionPage(
            Map<String, Object> resultMap, Integer pageNo, Integer pageSize);

    Set<String> findRoleByUserId(Long userId);

    List<URole> findNowAllPermission();

    //初始化数据
    void initData();

    int findRoleByCode(String code);

    int findRoleByType(String type);

    List<Long> getRenterIdByRType(String rType);

	List<URole> findRolesByUserId(Long userId);

	// 根据角色名查询角色
    int findRoleByName(URole role);

    // 根据用户id查询当前对应所有角色的code
    List<String> findRoleCodesByUserId(Long userId);
}

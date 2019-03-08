package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.common.domain.UUserRole;

import java.util.List;
import java.util.Map;

public interface UUserRoleMapper {

    int insert(UUserRole record);

    int insertSelective(UUserRole record);

    int deleteByUserId(Long id);

    int deleteRoleByUserIds(Map<String, Object> resultMap);

    List<Long> findUserIdByRoleId(Long id);

    List<UUserRole> find(UUserRole entity);

	void deleteByRoleId(Long roleId);
	
	List<Long> findRoleIdsByUserId(Long id);

}
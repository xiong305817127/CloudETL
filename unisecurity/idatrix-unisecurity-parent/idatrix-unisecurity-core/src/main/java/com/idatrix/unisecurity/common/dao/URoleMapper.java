package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.common.domain.URole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface URoleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(URole record);

    int insertSelective(URole record);

    URole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(URole record);

    int updateByPrimaryKey(URole record);

    Set<String> findRoleByUserId(Long id);

    List<URole> findNowAllPermission(Map<String, Object> map);

    void initData();

    int findAdminRoleByRenterId(Long renterId);

    Long getIdByRenterIdAndName(Map<String, Object> map);

    /*根据code查询角色*/
    int findRoleByCode(String code);

    /*根据type查询角色*/
    int findRoleByType(String type);

    List<Long> getRenterIdByRType(String rType);

	List<URole> findRolesByUserId(Long userId);

	Long getRoleIdByCode(URole record);

    List<URole> findPage(@Param("renterId") Long renterId, @Param("key") String key);

    // 根据角色名查询角色
    int findRoleByName(URole role);

    // 根据租户id和角色名确定一条角色信息
    URole getRoleByName(@Param("renterId") Long renterId, @Param("roleName") String roleName);

    List<String> findRoleCodesByUserId(@Param("userId") Long userId);
}
package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.common.domain.URenter;
import org.apache.ibatis.annotations.Param;

/**
 */
public interface URenterMapper {

    // 根据id重新租户信息
    URenter findRenterById(Long id);

    // 根据租户name查询租户
    int findByName(String name);

    // 新增租户
    int insertRenter(URenter record);

    // 修改租户信息
    int updateByPrimaryKeySelective(URenter record);

    // 删除租户
    int deleteByPrimaryKey(Long id);

    // 禁用租户，可禁用多个
    int updateStatus(@Param("array")String[] idArray, @Param("status") Long status);

    String getAccountById(Long id);

    // 根据管理员账号查询租户信息
    URenter getByAccount(String account);

    void updateRenterInfo(URenter uRenter);

    Integer isExist(URenter uRenter);

	// 根据用户name查询租户表信息，判断当前用户是否为租户
	int isRentByUserName(String username);

    URenter findByAdminAccount(@Param("username") String username);
}

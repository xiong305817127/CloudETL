package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.api.domain.UPermission;
import com.idatrix.unisecurity.api.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UUserProviderMapper {

	List<UPermission> findPermitByUserId(Long userId);

	List<User> findSFTPUser();

	User getAuthorUser(@Param("username")String username, @Param("password")String password);

	List<User> findUsersByRenterId(Long renterId);

	User getUserInfo(Long userId);

	List<String> findRenterUsersByUsername(String username);

	User findRenterByRenterId(Long renterId);

	List<User> findUsersByDeptAndRole(@Param("deptId")int deptId, @Param("roleId")int roleId);

    List<User> findUserByRoleAndRenter(@Param("roleId") int roleId, @Param("renterId") Long renterId);

	User findByUserName(@Param("username") String username);

	List<String> findRoleCodesByUserName(@Param("username") String username);
}

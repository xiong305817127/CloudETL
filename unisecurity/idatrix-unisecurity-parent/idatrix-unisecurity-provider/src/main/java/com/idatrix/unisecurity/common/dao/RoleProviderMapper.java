package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.api.domain.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleProviderMapper {

	List<Role> findRoles();

	List<Role> findRoleListByRenterId(@Param("renterId") Long renterId);
}

package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrganizationProviderMapper {

	Organization getUserOrganizationByUserId(Long userId);

	List<User> findUsersByDeptId(Long deptId);

	List<Organization> findOrganizationsByUserId(Long userId);

	List<Organization> findOrganizations();

	List<Integer> findParentIdsByDeptCode(@Param("deptCode")String deptCode, @Param("renterId")Long renterId);

	List<Integer> findParentIdsByUnifiedCreditCode(@Param("unifiedCreditCode")String unifiedCreditCode,
			@Param("renterId")Long renterId);

	List<Long> findByName(@Param("renterId") Long renterId, @Param("name") String name);

	Organization findById(@Param("id") Long id);

    int findAscriptionDeptCountByRenterId(@Param("renterId") Long renterId);

	Organization findByRenterId(@Param("renterId") Long renterId);

    List<Organization> findAllAscriptionDept(@Param("renterId") Long renterId);

	Organization findOrganizationByUserId(@Param("userId") Long userId);

	Organization findOrganizationByUserName(@Param("userName") String userName);

    Organization findByCode(@Param("code") String code);

	List<Organization> findByCodeList(@Param("codeList") List<String> codeList);
}

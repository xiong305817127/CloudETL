package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.common.domain.Organization;
import com.idatrix.unisecurity.common.domain.OrganizationExcelData;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by james on 2017/7/22.
 */
public interface OrganizationMapper {

    // 新增组织
    int insertOrganization(Organization record);

    // 修改组织
    int updateByPrimaryKeySelective(Organization record);

    // 根据id删除部门
    int deleteByPrimaryKey(Long id);

    List<Organization> findDeptByCode(Organization record);

    // 根据组织机构代码查询组织
    int findDeptCountByCode(Organization record);

    // 根据用户id查询部门
    List<Organization> selectOrganizationByUserId(Long userId);

    Long getOrganization(Map<String, Object> map);

    OrganizationExcelData selectByPrimaryKey(Long id);

    List<Organization> findOrganization(Organization organization);

    List<Organization> findOrganizationByParentId(Long parentId);

    // int findOrganizationByParentIdName(@Param("parentId") Long parentId, @Param("deptName") String deptName, @Param("renterId") Long renterId);

    int findOrganizationByParentIdName(Organization organization);

    Long getDeptIdByCode(Organization record);

    List findChildOrganizationIdsByOrgId(Long deptId);

    List<Organization> findAllOrganizations(@Param("userId") long userId);

    Organization findRentDeptByRentId(@Param("renterId") Long renterId);

    // 批量删除
    Integer batchDelete(List<Long> ids);

    // 根据统一社会信用代码查询组织
    int findByUnifiedCreditCode(Organization organization);

    // 根据id查询用户信息
    Organization findById(@Param("id") Long id);

    // 查询组织下的所属组织列表
    List<Organization> findByAscriptionDeptId(@Param("ascriptionDeptId") Long ascriptionDeptId);

    // 查询组织下所有可选的所属组织
    List<Organization> findAscriptionDeptList(@Param("renterId") Long renterId);
}

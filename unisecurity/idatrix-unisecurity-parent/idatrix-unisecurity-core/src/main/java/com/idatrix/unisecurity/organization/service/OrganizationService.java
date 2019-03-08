package com.idatrix.unisecurity.organization.service;

import com.idatrix.unisecurity.common.domain.Organization;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.organization.bo.OrganizationBo;
import com.idatrix.unisecurity.organization.vo.OrganizationVo;

import java.util.List;
import java.util.Map;

/**
 * Created by james on 2017/5/26.
 */
public interface OrganizationService {

    Pagination<Organization> findPage(Map<String, Object> resultMap, Integer pageNo, Integer pageSize);

    int insert(Organization record);

    int updateOrganization(Organization record);

    int deleteOrganizationById(String ids) throws Exception;

    List<Organization> findDeptByCode(Organization record);

    int findDeptCountByCode(Organization record);

    List<Organization> selectOrganizationByUserId(Long userId);

    List<OrganizationBo> export(String ids);

    List<Organization> findOrganization(Organization organization);

    List<Organization> findOrganizationByParentId(Long parentId);

    // 查询通层次的组织名是否重复
    int findOrganizationByParentIdName(Organization organization);

    List getChildDeptIdsByDeptId(Long deptId);

    List<Organization> findAllOrganizations(long userId);

    Organization findRentDeptByRentId(Long renterId);

    /*批量删除部门*/
    Integer batchDelete(List<Long> ids) throws Exception;

    // 根据统一社会信用代码查询组织
    int findByUnifiedCreditCode(Organization organization);

    // 根据父组织id查询出所有父组织
    List<OrganizationVo> findAscriptionDeptList(Long id);
}

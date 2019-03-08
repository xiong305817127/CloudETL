package com.idatrix.unisecurity.api.service;

import com.idatrix.unisecurity.api.domain.Organization;

import java.util.List;

/***
 * @author oyr
 * @date 2018/9/6 14:01
 * @param
 * @return
 */
public interface OrganizationService {

    // 根据租户id查询顶层组织
    Organization findByRenterId(Long renterId);

    List<Long> findByName(Long renterId, String name);

    Organization findById(Long id);

    // 查租户下所属部门的个数
    Integer findAscriptionDeptCountByRenterId(Long renterId);

    // 查询所有的所属组织
    List<Organization> findAllAscriptionDept(Long renterId);

    // 查询当前用户的所属组织
    Organization findAscriptionDeptByUserId(Long userId);

    // 查询当前用户的所属组织
    Organization findAscriptionDeptByUserName(String userName);

    // 根据code查询组织
    Organization findByCode(String code);
}

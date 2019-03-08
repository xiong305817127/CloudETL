package com.idatrix.unisecurity.provider.serivce;

import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.service.OrganizationService;
import com.idatrix.unisecurity.common.dao.OrganizationProviderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName OrganizationServiceImpl
 * @Description 部门service
 * @Author ouyang
 * @Date 2018/9/6 14:02
 * @Version 1.0
 **/
@Service
public class OrganizationServiceImpl implements OrganizationService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private OrganizationProviderMapper organizationMapper;

    public Organization findByRenterId(Long renterId) {
        Organization organization = organizationMapper.findByRenterId(renterId);
        log.debug("findByRenterId renterId:{}", renterId);
        return organization;
    }

    @Override
    public List<Long> findByName(Long renterId, String name) {
        log.debug("findByName renterId：{},name：{}", renterId, name);
        return organizationMapper.findByName(renterId, name);
    }

    @Override
    public Organization findById(Long id) {
        log.debug("findById id：{}", id);
        return organizationMapper.findById(id);
    }

    @Override
    public Integer findAscriptionDeptCountByRenterId(Long renterId) {
        log.debug("===== findAscriptionDeptCountByRenterId =====");
        int count = organizationMapper.findAscriptionDeptCountByRenterId(renterId);
        return count;
    }

    @Override
    public List<Organization> findAllAscriptionDept(Long renterId) {
        log.debug("==== findAllAscriptionDept ====");
        List<Organization> result = organizationMapper.findAllAscriptionDept(renterId);
        return result;
    }

    @Override
    public Organization findAscriptionDeptByUserId(Long userId) {
        log.debug(" === findAscriptionDeptByUserId ===");
        Organization organization = organizationMapper.findOrganizationByUserId(userId);
        Organization ascriptionDept = getAscriptionDept(organization);
        return ascriptionDept;
    }

    @Override
    public Organization findAscriptionDeptByUserName(String userName) {
        log.debug(" === findAscriptionDeptByUserName ===");
        Organization organization = organizationMapper.findOrganizationByUserName(userName);
        Organization ascriptionDept = getAscriptionDept(organization);
        return ascriptionDept;
    }

    @Override
    public Organization findByCode(String code) {
        log.debug(" === findByCode ===");
        Organization organization = organizationMapper.findByCode(code);
        return organization;
    }

    private Organization getAscriptionDept(Organization organization) {
        if (organization == null) {
            return null;
        }
        if (organization.getAscriptionDeptId() == null) {
            // 当前组织即是归属组织
            return organization;
        }
        Organization ascriptionDept = organizationMapper.findById(organization.getAscriptionDeptId());
        return getAscriptionDept(ascriptionDept);
    }

}

package com.idatrix.unisecurity.organization.service.impl;

import com.idatrix.unisecurity.common.dao.OrganizationMapper;
import com.idatrix.unisecurity.common.dao.UUserMapper;
import com.idatrix.unisecurity.common.domain.Organization;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.exception.SecurityException;
import com.idatrix.unisecurity.common.utils.Constants;
import com.idatrix.unisecurity.common.utils.LoggerUtils;
import com.idatrix.unisecurity.common.utils.SecurityStringUtils;
import com.idatrix.unisecurity.core.mybatis.BaseMybatisDao;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.freeipa.model.FreeIPATemplate;
import com.idatrix.unisecurity.freeipa.proxy.IFreeIPAProxy;
import com.idatrix.unisecurity.freeipa.proxy.factory.LdapHttpDataBuilder;
import com.idatrix.unisecurity.freeipa.proxy.impl.FreeIPAProxyImpl;
import com.idatrix.unisecurity.organization.bo.OrganizationBo;
import com.idatrix.unisecurity.organization.service.OrganizationService;
import com.idatrix.unisecurity.organization.vo.OrganizationVo;
import com.idatrix.unisecurity.ranger.usersync.process.LdapMgrUserGroupBuilder;
import com.idatrix.unisecurity.user.Config;
import com.idatrix.unisecurity.user.service.UUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by james on 2017/5/26.
 */
@Service
public class OrganizationServiceImpl extends BaseMybatisDao<OrganizationMapper> implements OrganizationService {

    @Autowired(required = false)
    private OrganizationMapper organizationMapper;

    @Autowired(required = false)
    private FreeIPATemplate freeIPATemplate;

    @Autowired(required = false)
    private LdapHttpDataBuilder ldapHttpDataBuilder;

    @Autowired(required = false)
    private LdapMgrUserGroupBuilder ldapMgrUserGroupBuilder;

    @Autowired(required = false)
    private UUserMapper userMapper;

    @Autowired(required = false)
    private Config config;

    @Autowired
    private UUserService userService;

    @Override
    public Pagination<Organization> findPage(Map<String, Object> resultMap, Integer pageNo, Integer pageSize) {
        return super.findPage(resultMap, pageNo, pageSize);
    }

    @Override
    public int insert(Organization record) {
        // 保存部门
        organizationMapper.insertOrganization(record);

        Long deptId = 0l;
        // 是否要同步到free ipa
        if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
            try {
                deptId = organizationMapper.getDeptIdByCode(record);
                logger.debug("create group freeipa  group =" + deptId);
                IFreeIPAProxy proxy = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
                proxy.addGroup("d_" + deptId, record.getDeptName());
            } catch (Exception e) {
                e.printStackTrace();
                LoggerUtils.fmtError(getClass(), e, "freeipa 新增部门出现错误，ids[%s]", deptId);
            }
        }
        return 1;
    }

    @Override
    public int updateOrganization(Organization record) {
        Organization organization = organizationMapper.findById(record.getId());
        if(organization.getAscriptionDeptId() == null && record.getAscriptionDeptId() != null) {
            // 判断当前所选部门没有被当做所属组织
            List<Organization> list = organizationMapper.findByAscriptionDeptId(record.getId());
            if (!CollectionUtils.isEmpty(list)) {
                throw new SecurityException(500, "该组织机构为其他组织的所属组织，操作失败");
            }
        }
        return organizationMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int deleteOrganizationById(String ids) throws Exception {
        int successCount = 0;
        String[] idArray = null;
        if (SecurityStringUtils.contains(ids, ",")) {
            idArray = ids.split(",");
        } else {
            idArray = new String[]{ids};
        }

        // 判断当前所选部门下有没有下级部门存在了
        for (String strId : idArray) {
            Long id = Long.valueOf(strId);
            // 获取子部门id
            List<Organization> sonList = organizationMapper.findOrganizationByParentId(id);
            if (!CollectionUtils.isEmpty(sonList)) {
                throw new SecurityException(500, "所选组织机构还存在子机构，不能删除");
            }
            // 判断当前所选部门没有被当做所属组织
            List<Organization> list = organizationMapper.findByAscriptionDeptId(id);
            if (!CollectionUtils.isEmpty(list)) {
                throw new SecurityException(500, "所选组织机构为其他组织的所属组织，无法被删除");
            }
            List<UUser> users = userMapper.findUsersByOrganizationId(id);
            if (!CollectionUtils.isEmpty(users)) {
                throw new SecurityException(500, "所选组织机构还存在组织机构-用户关联关系，不能删除");
            }
        }

        // 判断是否还有用户在即将删除的部门中
        for (String idx : idArray) {
            Long id = Long.valueOf(idx);
            // 删除当前部门，纪录删除数量
            successCount += organizationMapper.deleteByPrimaryKey(id);
            if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
                IFreeIPAProxy proxy = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
                proxy.deleteGroup("d_" + id);
            }
        }
        return successCount;
    }

    @Override
    public int findDeptCountByCode(Organization record) {
        return organizationMapper.findDeptCountByCode(record);
    }

    @Override
    public List<Organization> selectOrganizationByUserId(Long userId) {
        return organizationMapper.selectOrganizationByUserId(userId);
    }

    @Override
    public List<OrganizationBo> export(String ids) {
        List<OrganizationBo> result = new LinkedList<>();
        if (StringUtils.isNotBlank(ids)) {
            String[] idArr = ids.split(",");
            for (int i = 0; i < idArr.length; i++) {
                // 循环每个选中的组织
                Organization organization = organizationMapper.findById(Long.valueOf(idArr[i]));
                if (organization != null) {
                    OrganizationBo organizationBo = new OrganizationBo();
                    BeanUtils.copyProperties(organization, organizationBo);
                    // 获取当前组织的父组织
                    Organization parentOrganization = organizationMapper.findById(organization.getParentId());
                    if (parentOrganization != null) {
                        organizationBo.setParentDeptName(parentOrganization.getDeptName());
                        organizationBo.setParentDeptCode(parentOrganization.getDeptCode());
                    }
                    result.add(organizationBo);
                    result.addAll(recursionAllSonOrganization(organization.getId(), new LinkedList<OrganizationBo>()));
                }
            }
        }
        return result;
    }

    /**
     * 递归查询所有的子部门，将所有的子部门分装到一个list中
     *
     * @param parentId
     * @param list
     * @return
     */
    private List<OrganizationBo> recursionAllSonOrganization(Long parentId, List<OrganizationBo> list) {
        Organization parentOrganization = organizationMapper.findById(parentId);
        // 子组织信息
        List<Organization> organizationList = organizationMapper.findOrganizationByParentId(parentOrganization.getId());
        if (organizationList != null && organizationList.size() > 0) {
            for (Organization organization : organizationList) {
                OrganizationBo result = new OrganizationBo();
                BeanUtils.copyProperties(organization, result);
                result.setParentDeptName(parentOrganization.getDeptName());
                result.setParentDeptCode(parentOrganization.getDeptCode());
                list.add(result);
                list.addAll(recursionAllSonOrganization(organization.getId(), new LinkedList<>()));
            }
        }
        return list;
    }

    @Override
    public List<Organization> findOrganization(Organization organization) {
        List<Organization> depts = new ArrayList<Organization>();
        depts = organizationMapper.findOrganization(organization);
        return depts;
    }


    @Override
    public List<Organization> findOrganizationByParentId(Long parentId) {
        try {
            return organizationMapper.findOrganizationByParentId(parentId);
        } catch (Exception e) {
            logger.error("findOrganizationByParentIdName error" + e.getMessage());
        }
        return null;
    }

    @Override
    public int findOrganizationByParentIdName(Organization organization) {
        try {
            return organizationMapper.findOrganizationByParentIdName(organization);
        } catch (Exception e) {
            logger.error("findOrganizationByParentIdName error" + e.getMessage());
        }
        return 0;
    }

    @Override
    public List getChildDeptIdsByDeptId(Long deptId) {
        return organizationMapper.findChildOrganizationIdsByOrgId(deptId);
    }

    @Override
    public List<Organization> findAllOrganizations(long userId) {
        return organizationMapper.findAllOrganizations(userId);
    }

    @Override
    public List<Organization> findDeptByCode(Organization record) {
        return organizationMapper.findDeptByCode(record);
    }

    @Override
    public Organization findRentDeptByRentId(Long renterId) {
        return organizationMapper.findRentDeptByRentId(renterId);
    }

    @Override
    public Integer batchDelete(List<Long> ids) throws Exception {
        if (CollectionUtils.isEmpty(ids)) {
            throw new SecurityException(500, "所选组织不能为空！！！");
        }

        for (Long id : ids) {
            // 判断当前所选部门下有没有下级部门存在了
            List<Organization> sonOrganizationList = organizationMapper.findOrganizationByParentId(id);
            if (!CollectionUtils.isEmpty(sonOrganizationList)) {
                throw new SecurityException(500, "所需组织机构含有子组织，无法被删除");
            }

            // 判断当前所选部门没有被当做所属组织
            List<Organization> list = organizationMapper.findByAscriptionDeptId(id);
            if (!CollectionUtils.isEmpty(list)) {
                throw new SecurityException(500, "所需组织机构为其他组织的所属组织，无法被删除");
            }
        }

        // 判断当前所选部门下有没有用户关联
        List<UUser> users = userService.findUsersByOrganizationIds(ids);
        if (!CollectionUtils.isEmpty(users)) {
            throw new SecurityException(500, "所需组织机构下含有用户关联关系，无法被删除");
        }
        // 删除
        return organizationMapper.batchDelete(ids);
    }

    @Override
    public int findByUnifiedCreditCode(Organization organization) {
        return organizationMapper.findByUnifiedCreditCode(organization);
    }

    @Override
    public List<OrganizationVo> findAscriptionDeptList(Long id) {
        List<Organization> list = organizationMapper.findAscriptionDeptList(ShiroTokenManager.getToken().getRenterId());
        List<OrganizationVo> result = new ArrayList<>();
        list.forEach(value -> {
            if(!value.getId().equals(id)) {
                OrganizationVo vo = new OrganizationVo();
                BeanUtils.copyProperties(value, vo);
                result.add(vo);
            }
        });
        return result;
        /*Organization organization = organizationMapper.findById(parentId);
        if (organization == null) {
            throw new SecurityException(500, "父组织信息不存在");
        }
        List<OrganizationVo> result = recursiveGetyAllParentDeptByDeptId(parentId, new ArrayList<OrganizationVo>());
        return result;*/
    }


    /**
     * 根据父组织id递归出所有父组织
     *
     * @param parentId
     * @param list
     * @return
     */
    public List<OrganizationVo> recursiveGetyAllParentDeptByDeptId(Long parentId, List<OrganizationVo> list) {
        Organization organization = organizationMapper.findById(parentId);
        if (organization != null) {
            OrganizationVo organizationVo = new OrganizationVo();
            BeanUtils.copyProperties(organization, organizationVo);
            list.add(organizationVo);
            if (organization.getParentId() != null) {
                recursiveGetyAllParentDeptByDeptId(organization.getParentId(), list);
            }
        }
        return list;
    }

    /**
     * 递归拼接所有的子部门id
     */
    public List<Long> recursiveGetyAllSonOrganizationId(List<OrganizationVo> organizationVos, List<Long> idList) {
        if (organizationVos != null && organizationVos.size() > 0) {
            for (OrganizationVo organizationVo : organizationVos) {
                idList.add(organizationVo.getId());
                recursiveGetyAllSonOrganizationId(organizationVo.getOrganizationList(), idList);
            }
        }
        return idList;
    }

    /**
     * 递归查询所有的子部门
     *
     * @return
     */
    public List<OrganizationVo> recursiveQueryAllSonOrganization(Long parentId, List<OrganizationVo> list) {
        List<Organization> organizationList = organizationMapper.findOrganizationByParentId(parentId);
        if (organizationList != null && organizationList.size() > 0) {
            for (Organization organization : organizationList) {
                OrganizationVo result = new OrganizationVo();
                BeanUtils.copyProperties(organization, result);
                result.setOrganizationList(recursiveQueryAllSonOrganization(organization.getId(), new ArrayList<>()));
                list.add(result);
            }
        }
        return list;
    }

}

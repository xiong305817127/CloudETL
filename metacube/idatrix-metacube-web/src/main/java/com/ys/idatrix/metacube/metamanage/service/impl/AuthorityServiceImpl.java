package com.ys.idatrix.metacube.metamanage.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.service.OrganizationService;
import com.ys.idatrix.metacube.api.beans.ActionTypeEnum;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.ModuleTypeEnum;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.common.enums.AuthorityApprovalStatus;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.ApprovalProcess;
import com.ys.idatrix.metacube.metamanage.domain.EsMetadataPO;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.domain.ResourceAuth;
import com.ys.idatrix.metacube.metamanage.mapper.ApprovalProcessMapper;
import com.ys.idatrix.metacube.metamanage.mapper.ResourceAuthMapper;
import com.ys.idatrix.metacube.metamanage.service.*;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ResourceAuthVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName AuthorityServiceImpl
 * @Description
 * @Author ouyang
 * @Date
 */
@Slf4j
@Transactional
@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private EsIndexService esIndexService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private SystemSettingsService settingsService;

    @Autowired
    private ResourceAuthMapper resourceAuthMapper;

    @Autowired
    private ApprovalProcessMapper approvalProcessMapper;

    @Autowired
    private ResourceAuthService resourceAuthService;

    @Override
    public PageResultBean<ApprovalProcessVO> searchResourceApplyList(ApprovalProcessSearchVo search) {
        // 搜索前置执行
        searchPre(search);

        PageHelper.startPage(search.getPageNum(), search.getPageSize());
        List<ApprovalProcessVO> list = approvalProcessMapper.searchResourceApplyList(search);
        PageInfo<ApprovalProcessVO> pageInfo = new PageInfo<>(list);

        //  搜索后置执行
        searchAfter(list);

        // 数据返回
        return PageResultBean.builder(pageInfo.getTotal(), list, search.getPageNum(), search.getPageSize());
    }

    @Override
    public List<ResourceAuth> getAuthorityListByMetadataId(Long metadataId, Integer resourceType) {
        if (!resourceType.equals(DatabaseTypeEnum.ELASTICSEARCH.getCode())) {
            Metadata metadata = metadataService.findById(metadataId);
            if (metadata.getResourceType().equals(2)) {
                // 当前元数据为视图，则返回读的权限列表
                List<ResourceAuth> list = resourceAuthMapper.findByAuthType(1);
                return list;
            }
        }
        // 如果不是视图则返回所有权限列表
        List<ResourceAuth> list = resourceAuthMapper.findAll();
        // 处理后返回给前端
        afterResourceAuth(list);
        return list;
    }

    @Override
    public Boolean userIfAuthority(Long metadataId, Integer resourceType) {
        /**
         * 1. 如果当前用户是数据中心管理员，可以访问所有信息，无法申请
         * 2. 与当前用户相同“所属组织”的元数据，无法申请
         * 3. 如果当前用户“所属组织”已经申请或申请已经通过，无法申请
         */
        if (settingsService.isDataCentreAdmin()) {
            return true;
        }

        // 获取当前用户所属部门
        Organization ascriptionDept = getAscriptionOrganization(UserUtils.getUserName());

        // 2.. 与当前用户相同 '所属组织' 的元数据，无法申请
        // 当前元数据所属组织代码
        String metadataAscriptionDeptCodes;
        if (resourceType.equals(DatabaseTypeEnum.ELASTICSEARCH.getCode())) {
            EsMetadataPO esMetadata = esIndexService.findById(metadataId);
            metadataAscriptionDeptCodes = esMetadata.getDeptCodes();
        } else {
            Metadata metadata = metadataService.findById(metadataId);
            metadataAscriptionDeptCodes = metadata.getDeptCodes();
        }

        // 与当前用户相同“所属组织”的元数据，则表示拥有所有的权限
        if (metadataAscriptionDeptCodes != null) {
            String[] codeArr = metadataAscriptionDeptCodes.split(",");
            for (String code : codeArr) {
                if (code.equals(ascriptionDept.getDeptCode())) {
                    return true;
                }
            }
        }

        // 3. 如果当前用户 '所属组织' 对这个资源已经申请或申请已经通过，无法申请
        ApprovalProcess param = new ApprovalProcess();
        param.setDeptCode(ascriptionDept.getDeptCode());
        param.setStatus(AuthorityApprovalStatus.IN_THE_APPLICATION.getCode());
        param.setResourceId(metadataId);
        param.setResourceType(resourceType);

        int inTheApplicationCount =
                approvalProcessMapper.find(param);

        param.setStatus(AuthorityApprovalStatus.PASS.getCode());
        int passCount =
                approvalProcessMapper.find(param);
        if (inTheApplicationCount >= 1 || passCount >= 1) {
            return true;
        }

        return false;
    }

    @Override
    public void userApplyAuthority(ApprovalProcess approvalProcess) {
        if (userIfAuthority(approvalProcess.getResourceId(), approvalProcess.getResourceType())) {
            throw new MetaDataException("当前用户已拥有操作当前数据权限 或 当前用户 '所属组织' 已经申请通过 或 申请尚在审核中，无法申请");
        }

        // 当前用户所属组织
        Organization ascriptionDept = getAscriptionOrganization(UserUtils.getUserName());

        // 参数补全
        Date now = new Date();
        approvalProcess.setCreator(UserUtils.getUserName());
        approvalProcess.setRenterId(UserUtils.getRenterId());
        approvalProcess.setCreateTime(now);
        approvalProcess.setDeptCode(ascriptionDept.getDeptCode());
        approvalProcess.setStatus(AuthorityApprovalStatus.IN_THE_APPLICATION.getCode()); // 状态设置为申请中
        approvalProcessMapper.insertSelective(approvalProcess);
    }

    @Override
    public PageResultBean<ApprovalProcessVO> searchAuthorityApproveList(ApprovalProcessSearchVo search) {
        if (!settingsService.isDatabaseAdmin()) {
            throw new MetaDataException("当前用户非部门数据库管理员，不能操作审批列表");
        }
        // 搜索前置执行
        searchPre(search);

        PageHelper.startPage(search.getPageNum(), search.getPageSize());
        List<ApprovalProcessVO> list = approvalProcessMapper.searchAuthorityApproveList(search);
        PageInfo<ApprovalProcessVO> pageInfo = new PageInfo<>(list);

        // 搜索后置执行
        searchAfter(list);

        return PageResultBean.builder(pageInfo.getTotal(), list, search.getPageNum(), search.getPageSize());
    }

    @Override
    public int batchToPass(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            throw new MetaDataException("参数错误");
        }
        int count = approvalProcessMapper.batchToPass(idList, UserUtils.getUserName(), new Date());
        return count;
    }

    @Override
    public int batchToNoPass(String ids, String opinion) {
        List<Long> idList = new ArrayList<>();
        for (String idStr : ids.split(",")) {
            idList.add(Long.parseLong(idStr));
        }
        int count = approvalProcessMapper.batchToNoPass(idList, UserUtils.getUserName(), opinion, new Date());
        return count;
    }

    @Override
    public int batchToRecycled(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            throw new MetaDataException("参数错误");
        }
        int count = approvalProcessMapper.batchToRecycled(idList, UserUtils.getUserName(), new Date());
        return count;
    }

    @Override
    public List<ApprovalProcessVO> getAuthorizedResource(String username, ModuleTypeEnum module, ActionTypeEnum actionType,
                                                         List<Integer> databaseTypes, List<Integer>  resourceTypes) {
        // 获取所需权限值
        List<Integer> authValues = getAuthValues(module, actionType);
        // 获取所属组织
        Organization ascriptionOrganization = getAscriptionOrganization(username);
        List<ApprovalProcessVO> result = approvalProcessMapper.
                findAuthResource(ascriptionOrganization.getDeptCode(), databaseTypes, resourceTypes, authValues);
        return result;
    }

    public void searchAfter(List<ApprovalProcessVO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 补全信息
        for (ApprovalProcessVO vo : list) {
            String deptCode = vo.getDeptCode();
            // 补全申请组织名
            Organization applyOrganization = organizationService.findByCode(deptCode);
            if (applyOrganization != null) {
                vo.setDeptName(applyOrganization.getDeptName());
            }

            // 补全当前资源所属组织名
            String schemaDeptNames = "";
            String schemaDeptCodes = vo.getSchemaDeptCodes();
            String[] deptCodeArr = schemaDeptCodes.split(",");
            for (String code : deptCodeArr) {
                Organization organization = organizationService.findByCode(code);
                if (organization != null) {
                    schemaDeptNames += organization.getDeptName();
                }
            }
            vo.setSchemaDeptNames(schemaDeptNames);

            // 补全权限
            List<ResourceAuthVO> resourceAuthVOList = approvalProcessMapper.getPermissionInfo(vo.getId());
            for (ResourceAuthVO authVO : resourceAuthVOList) {
                if (authVO.getAuthName().equals(ModuleTypeEnum.ANALYZE.getCode())) {
                    authVO.setAuthName(ModuleTypeEnum.ANALYZE.getName());
                }
                if (authVO.getAuthName().equals(ModuleTypeEnum.ETL.getCode())) {
                    authVO.setAuthName(ModuleTypeEnum.ETL.getName());
                }
            }
            vo.setResourceAuthVOList(resourceAuthVOList);
        }
    }

    public void searchPre(ApprovalProcessSearchVo search) {
        // 补全信息，根据所属部门来区分（获取当前用户的所属组织编码）
        Organization ascriptionDept = getAscriptionOrganization(UserUtils.getUserName());
        search.setAscriptionDeptCode(ascriptionDept.getDeptCode());
    }

    // 获取当前用户的所属部门
    public Organization getAscriptionOrganization(String userName) {
        Organization ascriptionDept = organizationService.findAscriptionDeptByUserName(userName);
        if (ascriptionDept == null) {
            // 当前用户没有设置部门
            throw new MetaDataException("当前用户还没有设置部门，请设置后再操作");
        }
        return ascriptionDept;
    }

    public void afterResourceAuth(List<ResourceAuth> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (ResourceAuth resourceAuth : list) {
            if (resourceAuth.getAuthName().equals(ModuleTypeEnum.ANALYZE.getCode())) {
                resourceAuth.setAuthName(ModuleTypeEnum.ANALYZE.getName());
            }
            if (resourceAuth.getAuthName().equals(ModuleTypeEnum.ETL.getCode())) {
                resourceAuth.setAuthName(ModuleTypeEnum.ETL.getName());
            }
        }
    }

    public List<Integer> getAuthValues(ModuleTypeEnum module, ActionTypeEnum actionType) {
        List<ResourceAuth> authList = resourceAuthService.findByModuleTypeAndActionType(module, actionType);
        if (CollectionUtils.isEmpty(authList)) {
            return null;
        }
        List<Integer> authValues = new ArrayList<>();
        if (actionType.getCode() != ActionTypeEnum.READORWRITE.getCode()) {
            // 获取当前需要的权限值总和
            int sumAuthValue = 0;
            for (ResourceAuth auth : authList) {
                sumAuthValue += auth.getAuthValue();
            }
            authValues.add(sumAuthValue);
        } else {
            for (ResourceAuth auth : authList) {
                authValues.add(auth.getAuthValue());
            }
        }
        return authValues;
    }

}
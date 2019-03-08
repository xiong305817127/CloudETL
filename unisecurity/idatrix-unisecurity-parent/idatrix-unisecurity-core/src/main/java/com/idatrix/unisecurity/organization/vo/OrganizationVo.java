package com.idatrix.unisecurity.organization.vo;

import java.util.List;

/**
 * @ClassName OrganizationVo
 * @Description 树形部门
 * @Author ouyang
 * @Date 2018/11/11 11:20
 * @Version 1.0
 */
public class OrganizationVo {

    private Long id;

    private Long parentId;

    private String deptName;

    // 组织机构代码
    private String deptCode;

    // 统一信用代码
    private String unifiedCreditCode;

    // 父组织机构代码
    private String parentDeptCode;

    // 父组织名字
    private String parentDeptName;

    // 所属组织
    private Long ascriptionDeptId;

    private List<OrganizationVo> organizationList;

    public String getParentDeptName() {
        return parentDeptName;
    }

    public void setParentDeptName(String parentDeptName) {
        this.parentDeptName = parentDeptName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getUnifiedCreditCode() {
        return unifiedCreditCode;
    }

    public void setUnifiedCreditCode(String unifiedCreditCode) {
        this.unifiedCreditCode = unifiedCreditCode;
    }

    public String getParentDeptCode() {
        return parentDeptCode;
    }

    public void setParentDeptCode(String parentDeptCode) {
        this.parentDeptCode = parentDeptCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public List<OrganizationVo> getOrganizationList() {
        return organizationList;
    }

    public void setOrganizationList(List<OrganizationVo> organizationList) {
        this.organizationList = organizationList;
    }

    public Long getAscriptionDeptId() {
        return ascriptionDeptId;
    }

    public void setAscriptionDeptId(Long ascriptionDeptId) {
        this.ascriptionDeptId = ascriptionDeptId;
    }
}

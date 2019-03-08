package com.idatrix.unisecurity.organization.bo;

import com.idatrix.unisecurity.organization.vo.OrganizationVo;

import java.util.List;

/**
 * @ClassName OrganizationBo
 * @Description TODO
 * @Author ouyang
 * @Date 2018/12/10 14:32
 * @Version 1.0
 */
public class OrganizationBo {

    // 父组织机构代码
    private String parentDeptCode;

    // 组织机构代码
    private String deptCode;

    private String deptName;

    // 父组织名字
    private String parentDeptName;

    // 统一信用代码
    private String unifiedCreditCode;

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

}

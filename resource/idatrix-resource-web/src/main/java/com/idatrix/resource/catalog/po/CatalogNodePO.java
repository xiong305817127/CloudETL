package com.idatrix.resource.catalog.po;

import java.util.Date;

/**
 * 政务信息资源分类表
 * @Author: Wangbin
 * @Date: 2018/5/23
 */
public class CatalogNodePO {

    /*目录ID使用 32位*/
    private Long id;

    /*父节点 id*/
    private Long parentId;

    /*父节点ID 全称*/
    private String parentFullCode;

    /*节点资源名称*/
    private String resourceName;

    /*节点资源编码，为不同长度数据，类、项目、目、细目：长度数字分别长度为：1位、2位、3位、不定长度*/
    private String resourceEncode;

    /*节点所在层级深度，分为类，项目，目，细目*/
    private int dept;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    public String getParentFullCode() {
        return parentFullCode;
    }

    public void setParentFullCode(String parentFullCode) {
        this.parentFullCode = parentFullCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceEncode() {
        return resourceEncode;
    }

    public void setResourceEncode(String resourceEncode) {
        this.resourceEncode = resourceEncode;
    }

    public int getDept() {
        return dept;
    }

    public void setDept(int dept) {
        this.dept = dept;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "CatalogNodePO{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", parentFullCode='" + parentFullCode + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", resourceEncode='" + resourceEncode + '\'' +
                ", dept=" + dept +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", modifier='" + modifier + '\'' +
                ", modifyTime=" + modifyTime +
                '}';
    }
}

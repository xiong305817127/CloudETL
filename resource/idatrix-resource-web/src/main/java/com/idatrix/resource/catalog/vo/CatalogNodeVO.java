package com.idatrix.resource.catalog.vo;

import java.util.List;

/**
 * Created by Robin Wing on 2018-5-17.
 */
public class CatalogNodeVO {

    /*目录ID使用 32位*/
    private Long id;

    /*父节点 id*/
    private Long parentId;

    /*父节点 code*/
    private String parentCode;

    /*目录分类编码全称（包含所有父节点分类编码）*/
    private String parentFullCode;

    /*节点资源名称*/
    private String resourceName;

    /*节点资源编码，为不同长度数据，类、项目、目、细目：长度数字分别长度为：1位、2位、3位、不定长度*/
    private String resourceEncode;

    /*节点所在层级深度，分为类，项目，目，细目*/
    private int dept;

    /*是否有子节点*/
    private Boolean hasChildFlag;

    /*将列表在此处转换成树形结构*/
    private List<CatalogNodeVO> children;

    public String getParentFullCode() {
        return parentFullCode;
    }

    public void setParentFullCode(String parentFullCode) {
        this.parentFullCode = parentFullCode;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public Boolean getHasChildFlag() {
        return hasChildFlag;
    }

    public void setHasChildFlag(Boolean hasChildFlag) {
        this.hasChildFlag = hasChildFlag;
    }

    public List<CatalogNodeVO> getChildren() {
        return children;
    }

    public void setChildren(List<CatalogNodeVO> children) {
        this.children = children;
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

    @Override
    public String toString() {
        return "CatalogNodeVO{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", parentCode='" + parentCode + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", resourceEncode='" + resourceEncode + '\'' +
                ", dept=" + dept +
                ", hasChildFlag=" + hasChildFlag +
                ", children=" + children +
                '}';
    }
}

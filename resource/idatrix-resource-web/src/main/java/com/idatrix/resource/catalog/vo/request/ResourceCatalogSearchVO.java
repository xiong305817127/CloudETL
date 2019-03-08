package com.idatrix.resource.catalog.vo.request;

import com.alibaba.fastjson.JSON;
import com.idatrix.resource.common.vo.BaseRequestParamVO;
import java.util.List;

/**
 * 资源目录搜索VO
 *
 * @author wzl
 */
public class ResourceCatalogSearchVO extends BaseRequestParamVO {

    /**
     * 资源分类id
     */
    private Long catalogId;
    /**
     * 资源分类编码
     */
    private String catalogCode;
    /**
     * 资源名称
     */
    private String name;
    /**
     * 资源编码
     */
    private String code;
    /**
     * 资源提供方名称
     */
    private String deptName;
    /**
     * 资源提供方代码
     */
    private String deptCode;
    /**
     * 创建者
     */
    private String creator;
    /**
     * 资源状态
     */
    private String status;

    /**
     * 资源id列表
     */
    private List<Long> resourceIds;

    /**
     * 全文搜索关键字
     */
    private String keyword;

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public String getCatalogCode() {
        return catalogCode;
    }

    public void setCatalogCode(String catalogCode) {
        this.catalogCode = catalogCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

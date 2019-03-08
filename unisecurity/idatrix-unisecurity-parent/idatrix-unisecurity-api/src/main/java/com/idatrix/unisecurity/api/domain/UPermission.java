package com.idatrix.unisecurity.api.domain;

import java.io.Serializable;

/**
 * 开发公司：粤数大数据
 */
public class UPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 父权限id
     */
    private Long parentId;
    /**
     * 权限类型
     */
    private String type;
    /**
     * 权限的url
     */
    private String url;
    /**
     * 权限的名称
     */
    private String name;
    /**
     * 是否显示
     */
    private Boolean isShow;
    /**
     * 显示顺序
     */
    private Integer showOrder;
    /**
     * 权限描述
     */
    private String urlDesc;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsShow() {
        return isShow;
    }

    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    public Integer getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(Integer showOrder) {
        this.showOrder = showOrder;
    }

    public String getUrlDesc() {
        return urlDesc;
    }

    public void setUrlDesc(String urlDesc) {
        this.urlDesc = urlDesc;
    }
}
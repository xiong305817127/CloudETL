package com.idatrix.resource.catalog.vo;

/**
 * Created by Robin Wing on 2018-6-25.
 */
public class ResourcePubVO {

    /*资源代码ID*/
    public Long id;

    /*资源代码全称*/
    public String resourceFullCode;

    /*资源代码*/
    public String resourceCode;

    /*资源分类*/
    public String catalogName;

    /*资源代码名称*/
    public String resourceName;

    /*资源格式大类:1电子文件，2电子表格，3数据库，4图形图像，5流媒体，6自描述格式，7服务接口*/
    public int formatType;

    /*资源格式信息（mysql,oracle,db2,sqlserver，文件，pdf,xls等）*/
    public String formatInfo;

    /*资源格式分类为： 自描述格式时，该描述信息有效*/
    public String formatInfoExtend;

    public String getFormatInfoExtend() {
        return formatInfoExtend;
    }

    public void setFormatInfoExtend(String formatInfoExtend) {
        this.formatInfoExtend = formatInfoExtend;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceFullCode() {
        return resourceFullCode;
    }

    public void setResourceFullCode(String resourceFullCode) {
        this.resourceFullCode = resourceFullCode;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public int getFormatType() {
        return formatType;
    }

    public void setFormatType(int formatType) {
        this.formatType = formatType;
    }

    public String getFormatInfo() {
        return formatInfo;
    }

    public void setFormatInfo(String formatInfo) {
        this.formatInfo = formatInfo;
    }
}

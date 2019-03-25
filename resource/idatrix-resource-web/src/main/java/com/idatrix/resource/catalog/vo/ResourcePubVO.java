package com.idatrix.resource.catalog.vo;

import lombok.Data;

/**
 * Created by Robin Wing on 2018-6-25.
 */
@Data
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

}

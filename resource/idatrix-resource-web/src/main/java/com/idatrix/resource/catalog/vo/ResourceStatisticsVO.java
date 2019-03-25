package com.idatrix.resource.catalog.vo;

import lombok.Data;

/**
 * Created by Robin Wing on 2018-6-1.
 */
@Data
public class ResourceStatisticsVO {

    /*资源ID，用来查看详情时候查询*/
    private Long resourceId;

    /*更新时间*/
    private String updateTime;

    /*资源信息名称*/
    private String  name;

    /*资源摘要*/
    private String remark;

    /*浏览人数*/
    private int visitCount;

    /*订阅人数*/
    private  int subCount;

}

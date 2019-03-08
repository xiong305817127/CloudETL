package com.idatrix.resource.catalog.vo;

import java.util.Date;

/**
 * Created by Robin Wing on 2018-6-1.
 */
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

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getSubCount() {
        return subCount;
    }

    public void setSubCount(int subCount) {
        this.subCount = subCount;
    }
}

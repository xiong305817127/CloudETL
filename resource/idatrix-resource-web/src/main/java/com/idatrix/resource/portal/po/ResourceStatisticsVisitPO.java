package com.idatrix.resource.portal.po;

import lombok.Data;

import java.util.Date;

/**
 * rc_statistics_visit
 * @author 
 */
@Data
public class ResourceStatisticsVisitPO {
    /**
     * 主键
     */
    private Long id;

    /**
     * yyyy-MM-DD
     */
    private String dayTime;

    /**
     * 上一天浏览统计

     */
    private Long lastVisitTotal;

    /**
     * 今天浏览次数
     */
    private Long visitCount;

    /**
     * 租户ID
     */
    private Long rentId;

    /**
     * 创建用户
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新用户
     */
    private String updater;

    /**
     * 更新时间
     */
    private Date updateTime;

    public ResourceStatisticsVisitPO(){

    }

    /**
     * 构造初始化
     * @param rentId
     * @param user
     * @param dayTime  统计时间
     */
    public ResourceStatisticsVisitPO(Long rentId, String user, String dayTime){
        this.dayTime = dayTime;
        this.rentId = rentId;
        this.createTime = new Date();
        this.updater = user;
        this.creator = user;
        this.updateTime = new Date();
    }

}
package com.idatrix.resource.report.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * rc_statistics_resource_visit  资源访问-日数据统计
 * @author 
 */
@Data
public class RcStatisticsResourceVisitPO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 信息资源ID
     */
    private Long resourceId;

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
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updater;

    /**
     * 更新时间
	 */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public RcStatisticsResourceVisitPO(){
        super();
    }

    public RcStatisticsResourceVisitPO(String user, Long rentId, Long resourceId){

        this.rentId = rentId;
        this.resourceId = resourceId;
        this.updater = user;
        this.creator = user;
        this.updateTime = new Date();
        this.createTime = new Date();

    }


}
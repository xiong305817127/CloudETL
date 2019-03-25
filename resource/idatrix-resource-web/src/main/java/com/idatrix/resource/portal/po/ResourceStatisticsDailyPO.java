package com.idatrix.resource.portal.po;

import lombok.Data;

import java.util.Date;

/**
 * rc_statistics_daily
 * @author 
 */

@Data
public class ResourceStatisticsDailyPO{
    /**
     * 主键
     */
    private Integer id;

    /**
     * 时间日期,以日为单位
     */
    private String dayTime;

    /**
     * 资源ID

     */
    private Long resourceId;

    /**
     * 资源所属类型  部门类 dept/主题类 topic/基础类 base

     */
    private String resourceLibType;

    /**
     * 资源种类 db/file/interface

     */
    private String resourceType;

    /**
     * 数据库上报数据量
     */
    private Long dbCount;

    /**
     * 文件类型资源上报量
     */
    private Long fileCount;

    /**
     * 接口调用次数
     */
    private Long interfaceCount;

    /**
     * 资源所属部门ID
     */
    private Long provideDeptId;

    /**
     * 资源所属部门名称
     */
    private String provideDeptName;

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
     * 更新人
     */
    private String updater;

    /**
     * 更新时间
     */
    private Date updateTime;

    public ResourceStatisticsDailyPO(){
        super();
    }


    public ResourceStatisticsDailyPO(Long rentId, String user){
        this.dbCount=0L;
        this.fileCount=0L;
        this.interfaceCount=0L;
        this.rentId = rentId;
        this.creator = user;
        this.updater = user;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

}
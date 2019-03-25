package com.idatrix.resource.portal.po;

import lombok.Data;

import java.util.Date;

/**
 * rc_statistics_dept
 * @author 
 */
@Data
public class ResourceStatisticsDeptPO{
    /**
     * 主键
     */
    private Long id;

    /**
     * 租户ID
     */
    private Long rentId;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 订阅资源部门ID
     */
    private Long deptId;

    /**
     * 订阅资源部门名称
     */
    private String deptName;

    /**
     * 资源类型db/file/interface
     */
    private String resourceType;

    /**
     * 数据库交换数量
     */
    private Long dbCount;

    /**
     * 文件下载次数
     */
    private Long fileCount;

    /**
     * 接口调用次数
     */
    private Long interfaceCount;

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

    public ResourceStatisticsDeptPO(){

    }

    public ResourceStatisticsDeptPO(Long rentId, String user, Long deptId, String deptName){
        this.dbCount = 0L;
        this.fileCount = 0L;
        this.interfaceCount = 0L;
        this.rentId = rentId;
        this.updater = user;
        this.creator = user;
        this.updateTime = new Date();
        this.createTime = new Date();
        this.deptId = deptId;
        this.deptName = deptName;
    }


}
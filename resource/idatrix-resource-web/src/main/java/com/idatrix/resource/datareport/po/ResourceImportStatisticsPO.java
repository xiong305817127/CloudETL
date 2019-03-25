package com.idatrix.resource.datareport.po;

import lombok.Data;

import java.util.Date;

/**
 * rc_resource_import
 * @author 
 */

@Data
public class ResourceImportStatisticsPO{
    /**
     *  
     */
    private Long id;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 资源绑定元数据里面对应ID
     */
    private Long metaId;

    /**
     * 上一次统计结束时间点
     */
    private Date lastCountTime;

    /**
     * 数据库总条数
     */
    private Long totalRecord;

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
     * 修改时间
     */
    private Date updateTime;


}
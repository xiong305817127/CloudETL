package com.idatrix.resource.servicelog.po;

import lombok.Data;

import java.util.Date;

@Data
public class ServiceLogPO {

    /*主键*/
    private Long id;

    /*服务名称*/
    private String serviceName;

    /*服务类型: HTTP/WEBSERVICE*/
    private String serviceType;

    /*服务代码*/
    private String serviceCode;

    /*调用方部门ID*/
    private Long callerDeptId;

    /*调用方部门编码*/
    private String callerDeptCode;

    /*调用方部门名称*/
    private String callerDeptName;

    /*执行时长*/
    private Integer execTime;

    /*是否成功：0失败，1成功*/
    private Integer isSuccess;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

}

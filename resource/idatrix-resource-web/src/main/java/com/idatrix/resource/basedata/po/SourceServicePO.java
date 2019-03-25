package com.idatrix.resource.basedata.po;

import lombok.Data;

import java.util.Date;

/**
 *  源服务信息表
 * @Description： 共享交换平台调用的服务
 * @Author: Wangbin
 * @Date: 2018/5/23
 */
@Data
public class SourceServicePO {
    /*主键*/
    private Long id;

    /*服务提供商ID*/
    private String providerId;

    /*服务提供方名称*/
    private String providerName;

    /*服务名称*/
    private String serviceName;

    /*服务类型：http,webservice*/
    private String serviceType;

    /*服务代码*/
    private String serviceCode;

    /*服务描述*/
    private String remark;

    /*服务url*/
    private String url;

    /*webservice服务，wsdl内容*/
    private byte wsdl[];

    /*组合ID，租户隔离使用*/
    private Long rentId;

    /*状态(软删除用n)*/
    private String status;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

}

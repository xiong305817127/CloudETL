package com.idatrix.resource.basedata.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by Robin Wing on 2018-5-23.
 */

@Data
public class SourceServiceVO {

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

    /*服务创建时间*/
    private String createTime;

    /*服务创建者*/
    private String creator;

    /* 服务修改者 */
    private String modifier;

    /* 修改时间 */
    private Date modifyTime;

    /*webservice服务，wsdl内容*/
    private String wsdl;

}

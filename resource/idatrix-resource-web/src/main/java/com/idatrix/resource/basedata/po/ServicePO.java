package com.idatrix.resource.basedata.po;

import java.util.Date;
import lombok.Data;

/**
 * 服务信息表
 *
 * @Description： 共享交换平台对外提供的服务
 * @Author: Wangbin
 * @Date: 2018/5/23
 */
@Data
public class ServicePO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务类型：SOAP,RESTful
     */
    private String serviceType;
    /**
     * 服务代码
     */
    private String serviceCode;
    /**
     * 服务描述
     */
    private String remark;
    /**
     * 服务url
     */
    private String url;
    /**
     * webservice服务，wsdl内容 note: 该字段已废弃
     */
    @Deprecated
    private byte[] wsdl;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    /**
     * 服务提供者ID
     */
    private String providerId;
    /**
     * 服务提供者名称
     */
    private String providerName;
    /**
     * 租户ID
     */
    private Long rentId;
    /**
     * 技术支持单位
     */
    private String technicalSupportUnit;
    /**
     * 技术支持联系人
     */
    private String technicalSupportContact;
    /**
     * 技术支持联系电话
     */
    private String technicalSupportContactNumber;
    /**
     * 请求示例
     */
    private String requestExample;
    /**
     * 成功返回示例
     */
    private String successfulReturnExample;
    /**
     * 失败返回示例
     */
    private String failureReturnExample;
    /**
     * 逻辑删除字段 1 表示删除， 0 表示未删除
     */
    private Integer isDeleted;
}

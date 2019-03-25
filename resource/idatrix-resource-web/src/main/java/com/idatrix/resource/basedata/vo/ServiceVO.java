package com.idatrix.resource.basedata.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * Created by Robin Wing on 2018-5-23.
 */

@Data
public class ServiceVO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 服务提供商ID
     */
    private String providerId;
    /**
     * 服务提供方名称
     */
    private String providerName;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务类型
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
     * 服务创建时间
     */
    private String createTime;
    /**
     * 服务创建者
     */
    private String creator;
    /**
     * 服务修改者
     */
    private String modifier;
    /**
     * 修改时间
     */
    private Date modifyTime;
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
     * 上传的文件id列表
     */
    @JsonProperty(access = Access.WRITE_ONLY)
    private List<Long> fileIds;
    /**
     * 上传的文件列表
     */
    @JsonProperty(access = Access.READ_ONLY)
    private List<FileVO> fileList;
}

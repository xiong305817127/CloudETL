package com.idatrix.resource.portal.vo;

import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.portal.po.ResourceQueryPO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 资源目录查询返回数据
 */

@Data
@ApiModel("资源目录查询返回数据")
public class ResourceQueryVO {

    /*资源ID*/
    @ApiModelProperty("资源ID")
    private Long resourceId;

    /*资源名称*/
    @ApiModelProperty("资源名称")
    private String resourceName;

    /*资源类型 type: db/file/interface*/
    @ApiModelProperty("资源类型 type: db/file/interface")
    private String resourceType;

    /*资源提供方名称*/
    @ApiModelProperty("资源提供方名称")
    private String provideDeptName;

    /*资源摘要*/
    @ApiModelProperty("资源摘要")
    private String resourceRemark;

    /*资源所属分类*/
    @ApiModelProperty("资源所属分类")
    private String catalogFullName;

    /*最近更新时间 YYYY-MM-DD HH:MM:SS*/
    @ApiModelProperty("最近更新时间 YYYY-MM-DD HH:MM:SS")
    private String updateTime;

    /*共享属性：有条件共享、无条件共享、不予共享*/
    @ApiModelProperty("共享属性：共享类型（无条件共享、有条件共享、不予共享三类。值域范围对应共享类型排序分别为1、2、3。）")
    private int shareType;

    /*开放属性：是否开放*/
    @ApiModelProperty("开放属性：是否开放 分布用数据 0/1")
    private int openType;

    /*访问量*/
    @ApiModelProperty("访问量")
    private Long visitCount;

    /*申请量*/
    @ApiModelProperty("申请量")
    private Long subCount;

    /*订阅权限标志：0表示没有订阅权限，1表示可以订阅，2，表示已经订阅*/
    @ApiModelProperty("订阅权限标志：0表示没有订阅权限，1表示可以订阅，2，表示已经订阅")
    private int subscribeFlag;

    public ResourceQueryVO(){
        super();
    }

    public ResourceQueryVO(ResourceQueryPO po){
        this.resourceId = po.getResourceId();
        this.resourceName = po.getResourceName();
        String resourceType = null;
        if(po.getFormatType().equals(7L)){
            resourceType = "interface";
        }else if(po.getFormatType().equals(3L)){
            resourceType = "db";
        }else{
            resourceType = "file";
        }
        this.resourceType = resourceType;
        this.provideDeptName = po.getProvideDeptName();
        this.resourceRemark = po.getResourceRemark();
        this.catalogFullName = po.getCatalogFullName();
        this.updateTime = DateTools.formatDate(po.getUpdateTime(), null);
        this.shareType = po.getShareType();
        this.openType = po.getOpenType();
        this.visitCount = po.getVisitCount();
        this.subCount = po.getSubCount();
        this.subscribeFlag = 1;
    }

}




package com.idatrix.resource.portal.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.idatrix.resource.catalog.po.ResourceConfigPO;
import com.idatrix.resource.common.utils.DateTools;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: ResourceSearchVO
 * @Description: 资源目录搜索返回数据
 * @Author: ZhouJian
 * @Date: 2019/1/7
 */

@Data
@ApiModel("资源目录搜索返回数据")
public class ResourceSearchVO {

    @ApiModelProperty("资源ID")
    private Long resourceId;

    @ApiModelProperty("资源名称")
    private String resourceName;

    @ApiModelProperty("资源类型 type: db/file/interface")
    private String resourceType;

    @ApiModelProperty("资源提供方名称")
    private String provideDeptName;

    @ApiModelProperty("资源摘要")
    private String resourceRemark;

    @ApiModelProperty("资源所属分类")
    private String catalogFullName;

    @ApiModelProperty("最近更新时间 YYYY-MM-DD HH:MM:SS")
    private String updateTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "ES查询高亮内容",hidden = true)
    private Map<String,String> highlightData;

    /*订阅权限标志：0表示没有订阅权限，1表示可以订阅，2，表示已经订阅*/
    @ApiModelProperty("订阅权限标志：0表示没有订阅权限，1表示可以订阅，2，表示已经订阅")
    private int subscribeFlag;

    public ResourceSearchVO() {
        super();
    }

    public ResourceSearchVO(ResourceConfigPO po) {
        this.resourceId = po.getId();
        this.resourceName = po.getName();
        String resourceType = null;
        if (po.getFormatType() == 7) {
            resourceType = "interface";
        } else if (po.getFormatType() == 4) {
            resourceType = "db";
        } else {
            resourceType = "file";
        }
        this.resourceType = resourceType;
        this.provideDeptName = po.getDeptName();
        this.resourceRemark = po.getRemark();
        this.catalogFullName = po.getCatalogFullName();
        this.updateTime = DateTools.formatDate(po.getUpdateTime(), null);
    }

}




package com.ys.idatrix.metacube.metamanage.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(value = "Tag", description = "标签实体类")
@Data
public class Tag {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("标签名")
    private String tagName;

    @ApiModelProperty("租户ID")
    private Long renterId;

    @ApiModelProperty("录入人")
    private String creator;

    @ApiModelProperty("录入时间")
    private Date createTime;

    @ApiModelProperty("是否删除")
    private Boolean isDeleted;

}
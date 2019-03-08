package com.ys.idatrix.metacube.metamanage.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(value = "SystemSettings", description = "元数据系统设置实体类")
@Data
public class SystemSettings {

    @ApiModelProperty("id")
    private Long id;

    @NotNull(message = "数据中心管理员不能为空")
    @ApiModelProperty("数据中心管理员不能为空，对应角色编码")
    private String dataCentreAdmin;

    @NotNull(message = "数据库管理员不能为空")
    @ApiModelProperty("数据库管理员，对应角色编码")
    private String databaseAdmin;

    @NotBlank(message = "HDFS根目录不能为空")
    @ApiModelProperty("HDFS根目录")
    private String rootPath;

    @NotNull(message = "必须指定自动采集")
    @ApiModelProperty("是否自动采集")
    private Boolean isGather;

    @ApiModelProperty("上次采集时间")
    private Date gatherTime;

    @ApiModelProperty("时间类型 1:每月 2:每周")
    private Integer timeType;

    @ApiModelProperty("日")
    private Integer day;

    @ApiModelProperty("时")
    private Integer hour;

    @NotNull(message = "血缘分析字段显示数量不能为空")
    @ApiModelProperty("血缘分析字段显示数量")
    private Integer columnShowCount;

    @ApiModelProperty("租户id")
    private Long renterId;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("修改时间")
    private Date modifyTime;
}
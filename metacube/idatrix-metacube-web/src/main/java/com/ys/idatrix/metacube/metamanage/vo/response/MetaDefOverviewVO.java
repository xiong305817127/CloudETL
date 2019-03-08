package com.ys.idatrix.metacube.metamanage.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 元数据定义概览
 * @author robin
 *
 */
@Data
@ApiModel("元数据定义配置概览")
public class MetaDefOverviewVO {

    @ApiModelProperty("id")
    private Long id;

    /*名称：HDFS-目录名称*/
    @ApiModelProperty("名称：HDFS-目录名称")
    private String name;

    /*内容描述：HDFS-存储路径*/
    @ApiModelProperty("内容描述：HDFS-子目录")
    private String  identification;

    /*创建者*/
    @ApiModelProperty("创建者")
    private String creator;

    /*创建日志*/
    @ApiModelProperty("创建日志")
    private Date createTime;

    /*状态：正常/禁用*/
    @ApiModelProperty(value = "状态：正常/禁用对应数值1/0")
    private Integer status;

    @ApiModelProperty("公开状态：0:不公开 1:授权访问")
    private Integer publicStatus;

    /*备注*/
    @ApiModelProperty("备注")
    private String remark;

    public MetaDefOverviewVO(){
        super();
    }

    public MetaDefOverviewVO(String name, String des, String creator, Integer status, int openLevel, String note){
        super();
        this.name = name;
        this.identification = des;
        this.creator = creator;
        this.createTime = new Date();
        this.status = status;
        this.publicStatus = openLevel;
        this.remark = note;
    }

}

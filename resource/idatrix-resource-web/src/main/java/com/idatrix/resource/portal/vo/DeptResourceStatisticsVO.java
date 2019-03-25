package com.idatrix.resource.portal.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 部门与发布的资源情况统计
 */
@Data
@ApiModel("部门与发布的资源情况统计")
public class DeptResourceStatisticsVO {

    /*统计内容*/
    @ApiModelProperty("统计内容,如资源、数据、接口、所有资源统计、文件、数据库分布表示为resource/data/interface/all/file/db")
    private String contentType;

    /*部门名称*/
    @ApiModelProperty("部门名称")
    private String deptName;

    /*资源统计数量*/
    @ApiModelProperty("资源统计数量")
    private Long count;

    /*统计时间*/
    @ApiModelProperty("统计时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

    public DeptResourceStatisticsVO(){
        super();
    }

    public DeptResourceStatisticsVO(String contentType, String deptName, Long count, Date updateTime){
        this.contentType = contentType;
        this.deptName = deptName;
        this.count = count;
        this.updateTime = updateTime;
    }
}

package com.idatrix.resource.portal.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Random;

/**
 * 资源统计情况概括
 */
@Data
@ApiModel("主要记录资源统计情况概括")
public class ResourceUseStatisticsVO {

    @ApiModelProperty("统计截止时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date statisticsTime;

    @ApiModelProperty("今日报送单位总数")
    private Long deptDailyCount;

    @ApiModelProperty("累计报送单位总数")
    private Long deptCount;

    @ApiModelProperty("今日委办局上报资源总条目")
    private Long resourceDailyCount;

    @ApiModelProperty("累计委办局上报资源总条目")
    private Long resourceRegisterTotal;

    @ApiModelProperty("上报文件个数：电子文件+电子表格+图形图像+流媒体+自描述")
    private Long filesUpload;

    @ApiModelProperty("上报数据表条目")
    private Long datasUpload;

    @ApiModelProperty("开放数据接口交换条目")
    private Long serviceApiUpload;

    @ApiModelProperty("总数据条数")
    private Long totalUploadData;

    public ResourceUseStatisticsVO(){
        super();
        this.serviceApiUpload=0L;
    }

    public ResourceUseStatisticsVO(Long value){

        Random ran1 = new Random();
        int valueCount = value.intValue();
        this.deptDailyCount = new Long(ran1.nextInt(valueCount));
        this.deptCount = new Long(ran1.nextInt(valueCount));
        this.resourceDailyCount = new Long(ran1.nextInt(valueCount));
        this.resourceRegisterTotal = new Long(ran1.nextInt(valueCount));
        this.filesUpload = new Long(ran1.nextInt(valueCount));
        this.datasUpload = new Long(ran1.nextInt(valueCount));
        this.serviceApiUpload = new Long(ran1.nextInt(valueCount));
        this.totalUploadData = new Long(ran1.nextInt(valueCount));
    }

}

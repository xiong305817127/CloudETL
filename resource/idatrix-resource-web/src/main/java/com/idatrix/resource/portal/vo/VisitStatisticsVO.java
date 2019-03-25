package com.idatrix.resource.portal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 每天目录访问量统计，包含时间和访问次数
 */

@Data
@ApiModel("每天目录访问量统计，包含时间和访问次数")
public class VisitStatisticsVO { //extends ContinuousInfoBase<T>{

    /*访问次数*/
    @ApiModelProperty("访问次数")
    private Long visitCount;

    /*访问时间*/
    @ApiModelProperty("访问时间")
    private String visitTime;

    public VisitStatisticsVO(){
        super();
    }

    public VisitStatisticsVO(String visitTime){
        super();
        this.visitTime = visitTime;
        this.visitCount = 0L;
    }

    public VisitStatisticsVO(Long visit, String visitTime){
        super();
        this.visitCount = visit;
        this.visitTime = visitTime;
    }
}

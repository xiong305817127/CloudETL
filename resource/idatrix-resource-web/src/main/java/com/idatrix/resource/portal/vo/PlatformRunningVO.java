package com.idatrix.resource.portal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 平台运行情况统计
 */
@Data
@ApiModel("获取资源门户平台运行情况统计")
public class PlatformRunningVO {

    @ApiModelProperty("今天登陆次数")
    private Long loginDailyCount;

    @ApiModelProperty("今天登陆单位")
    private Long loginDeptDailyCount;

    @ApiModelProperty("累计登陆参数")
    private Long loginTotal;

    @ApiModelProperty("今日目录访问量")
    private Long visitDailyCount;

    @ApiModelProperty("累计访问量")
    private Long visitTotal;

    public PlatformRunningVO(){

    }

    public PlatformRunningVO(Long loginDaily, Long deptDaily, Long loginTotal,
                             Long visitDaily, Long visitTotal){
        this.loginDailyCount = loginDaily;
        this.loginDeptDailyCount = deptDaily;
        this.loginTotal = loginTotal;
        this.visitDailyCount = visitDaily;
        this.visitTotal = visitTotal;
    }

}

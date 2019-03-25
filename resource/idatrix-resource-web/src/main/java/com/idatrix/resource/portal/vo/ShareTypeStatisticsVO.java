package com.idatrix.resource.portal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 主要是生成共享类型饼状图
 */
@Data
@ApiModel("信息资源不同共享类型数量")
public class ShareTypeStatisticsVO {

    /*资源总数:无条件共享、有条件共享、不予共享三类。值域范围对应共享类型排序分别为1、2、3。*/
    @ApiModelProperty("信息资源总数")
    private Long totalCount;

    /*无条件共享数目*/
    @ApiModelProperty("无条件共享信息资源数量")
    private Long unconditionalShareCount;

    /*有条件共享*/
    @ApiModelProperty("有条件共享信息资源数量")
    private Long conditionalShareCount;

    /*不予共享*/
    @ApiModelProperty("不予共享信息资源数量")
    private Long noShareCount;

    public ShareTypeStatisticsVO(){
        super();
    }

    public ShareTypeStatisticsVO(Long total, Long unconditional, Long conditional, Long noShare){
        this.totalCount = total;
        this.unconditionalShareCount = unconditional;
        this.conditionalShareCount = conditional;
        this.noShareCount = noShare;
    }

}

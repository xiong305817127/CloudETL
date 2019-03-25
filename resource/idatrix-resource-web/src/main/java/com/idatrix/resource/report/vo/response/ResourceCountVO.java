package com.idatrix.resource.report.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@ApiModel
@Data
@Accessors(chain = true)
public class ResourceCountVO {

    @ApiModelProperty("注册量")
    private Long registerCount;

    @ApiModelProperty("订阅量")
    private Long subscriptionCount;

    @ApiModelProperty("发布量")
    private Long publicationCount;

    @ApiModelProperty("资源使用频率")
    private Long frequencyCount;
}

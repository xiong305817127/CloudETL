package com.idatrix.resource.subscribe.vo.request;

import com.idatrix.resource.catalog.vo.ResourceColumnVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by Administrator on 2018/7/18.
 */
@Data
@ApiModel("订阅审批处理意见")
public class SubscribeApproveRequestVO {

    @ApiModelProperty("订阅ID")
    private Long id;

    @ApiModelProperty("是否同意agree/disagree")
    private String action;

    @ApiModelProperty("审批意见")
    private String suggestion;

    @ApiModelProperty("脱敏处理规则")
    private List<ResourceColumnVO> inputDbioList;

}

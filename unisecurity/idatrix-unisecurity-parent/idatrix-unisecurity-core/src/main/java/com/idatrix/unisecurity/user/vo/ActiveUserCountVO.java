package com.idatrix.unisecurity.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ActiveUserCountVO
 * @Description
 * @Author ouyang
 * @Date
 */
@ApiModel(value = "ActiveUserCountVO", description = "活跃用户数实体类")
@Data
public class ActiveUserCountVO {

    @ApiModelProperty("本周内登录次数大于3的用户数")
    private Integer weekMoreThanThreeCount;

    @ApiModelProperty("本月内登次数大于10的用户数")
    private Integer monthMoreThanTenCount;


    @ApiModelProperty("本周内使用平台的用户数")
    private Integer weekUseSystemCount;

    @ApiModelProperty("本月内使用平台的用户数")
    private Integer monthUseSystemCount;

    public ActiveUserCountVO() {
    }

    public ActiveUserCountVO(Integer weekMoreThanThreeCount, Integer monthMoreThanTenCount, Integer weekUseSystemCount, Integer monthUseSystemCount) {
        this.weekMoreThanThreeCount = weekMoreThanThreeCount;
        this.monthMoreThanTenCount = monthMoreThanTenCount;
        this.weekUseSystemCount = weekUseSystemCount;
        this.monthUseSystemCount = monthUseSystemCount;
    }
}
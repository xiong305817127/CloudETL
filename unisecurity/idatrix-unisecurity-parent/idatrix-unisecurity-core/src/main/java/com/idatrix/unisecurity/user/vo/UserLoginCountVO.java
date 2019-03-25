package com.idatrix.unisecurity.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName UserLoginCountVO
 * @Description
 * @Author ouyang
 * @Date
 */
@Data
@ApiModel(value = "UserLoginCountVO", description = "用户登陆次数统计实体类")
public class UserLoginCountVO {

    @ApiModelProperty("月")
    private Integer month;

    @ApiModelProperty("用户登录次数")
    private Integer userLoginCount;

    public UserLoginCountVO() {

    }

    public UserLoginCountVO(Integer month, Integer userLoginCount) {
        this.month = month;
        this.userLoginCount = userLoginCount;
    }
}
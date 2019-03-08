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
@ApiModel(value = "UserLoginCountVO", description = "用户登陆次数统计实体类")
@Data
public class UserLoginCountVO {

    @ApiModelProperty("月")
    private Integer month;

    @ApiModelProperty("登录用户数量")
    private Integer loginUserCount;

    public UserLoginCountVO() {

    }

    public UserLoginCountVO(Integer month, Integer loginUserCount) {
        this.month = month;
        this.loginUserCount = loginUserCount;
    }
}
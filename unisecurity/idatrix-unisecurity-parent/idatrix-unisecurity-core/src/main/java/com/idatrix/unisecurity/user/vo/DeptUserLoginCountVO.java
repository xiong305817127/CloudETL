package com.idatrix.unisecurity.user.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName DeptUserLoginCountVO
 * @Description
 * @Author ouyang
 * @Date
 */
@Data
public class DeptUserLoginCountVO {

    @ApiModelProperty("所属组织ID")
    private Long deptId;

    @ApiModelProperty("所属组织名")
    private String deptName;

    @ApiModelProperty("用户登录次数")
    private String userLoginCount;
}
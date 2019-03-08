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

    @ApiModelProperty("所属组织名")
    private String deptName;

    @ApiModelProperty("登录用户数量")
    private String loginUserCount;

    public DeptUserLoginCountVO() {
    }
}
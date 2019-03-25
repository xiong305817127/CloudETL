package com.idatrix.unisecurity.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName LoginDetailsInfoVO
 * @Description
 * @Author ouyang
 * @Date
 */
@Data
public class LoginDetailsInfoVO {

    @ApiModelProperty("登录用户名")
    private String username;

    @ApiModelProperty("客户端登录IP地址")
    private String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("登录时间")
    private Date loginTime;

    @ApiModelProperty("当前组织名")
    private String deptName;

    @ApiModelProperty("所属组织名")
    private String ascriptionDeptName;
}
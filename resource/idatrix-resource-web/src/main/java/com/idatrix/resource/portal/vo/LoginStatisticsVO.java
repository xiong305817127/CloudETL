package com.idatrix.resource.portal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统登陆图
 */
@Data
@ApiModel("资源门户每天登陆次数和登陆单位统计")
public class LoginStatisticsVO {

    @ApiModelProperty("登陆次数")
    private Long loginCount;

    @ApiModelProperty("登陆单位数")
    private Long loginDeptCount;

    @ApiModelProperty("日期时间 yyyy-MM-DD格式")
    private String logintTime;

    public LoginStatisticsVO(Long count, Long dept, String loginTime){
        this.loginCount = count;
        this.loginDeptCount = dept;
        this.logintTime = loginTime;
    }


    public LoginStatisticsVO(String loginTime){
        this.loginCount = 0L;
        this.loginDeptCount = 0L;
        this.logintTime = loginTime;
    }
}

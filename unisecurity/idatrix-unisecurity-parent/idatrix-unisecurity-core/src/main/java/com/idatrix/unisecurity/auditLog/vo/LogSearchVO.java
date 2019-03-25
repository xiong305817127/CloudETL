package com.idatrix.unisecurity.auditLog.vo;

import com.idatrix.unisecurity.common.vo.SearchVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @ClassName SearchVO
 * @Description
 * @Author ouyang
 * @Date
 */
@Data
public class LogSearchVO extends SearchVO {

    @ApiModelProperty("操作类型 操作类型 1：普通操作 2：登录 3：登出")
    private Integer opType;

    @ApiModelProperty("用户名")
    private String userName;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("登陆时间开始时间")
    private Date loginDateStart;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("登陆时间开始时间")
    private Date loginDateEnd;

    @ApiModelProperty("调用结果 1：成功 2：失败")
    private Integer result;
}
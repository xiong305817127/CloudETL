package com.idatrix.unisecurity.user.vo;

import com.idatrix.unisecurity.common.vo.SearchVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName LoginSearchVO
 * @Description
 * @Author ouyang
 * @Date
 */
@Data
public class LoginSearchVO extends SearchVO {

    @ApiModelProperty("年，查询某年的登陆详情，不传递则默认当前年")
    private Integer year;

    @ApiModelProperty("月份，查询某月的登陆详情")
    private Integer month;

    @ApiModelProperty("所属组织ID，查询某个所属组织下的登陆详情")
    private Integer deptId;

}
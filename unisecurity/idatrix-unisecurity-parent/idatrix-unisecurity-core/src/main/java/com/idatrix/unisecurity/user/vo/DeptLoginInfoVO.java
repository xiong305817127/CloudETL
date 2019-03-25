package com.idatrix.unisecurity.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName DeptLoginInfoVO
 * @Description
 * @Author ouyang
 * @Date
 */
@ApiModel(value = "DeptLoginInfoVO", description = "部门用户登陆信息实体类")
@Data
public class DeptLoginInfoVO {

    // 本月登录用户数排行TOP10
    @ApiModelProperty("本月登录用户次数排行 TOP 10")
    private List<DeptUserLoginCountVO> monthLoginUserRankingList;

    // 登录用户数总排行TOP10
    @ApiModelProperty("登录用户数总排行 TOP 10")
    private List<DeptUserLoginCountVO> sumLoginUserCountRankingList;

    public DeptLoginInfoVO() {
    }

    public DeptLoginInfoVO(List<DeptUserLoginCountVO> monthLoginUserRankingList,
                           List<DeptUserLoginCountVO> sumLoginUserCountRankingList) {
        this.monthLoginUserRankingList = monthLoginUserRankingList;
        this.sumLoginUserCountRankingList = sumLoginUserCountRankingList;
    }
}
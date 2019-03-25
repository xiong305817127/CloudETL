package com.idatrix.unisecurity.user.controller;

import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.PageResultVo;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.user.service.LoginCountService;
import com.idatrix.unisecurity.user.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @ClassName LoginCountController
 * @Description
 * @Author ouyang
 * @Date
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/login/count")
@Api(value = "/LoginCountController", tags = "安全管理-登陆统计处理接口")
public class LoginCountController {

    @Autowired
    private LoginCountService loginCountService;

    @ApiOperation(value = "登陆统计-获取当前租户下某年的登录用户数量月度统计", notes = "", httpMethod = "GET")
    @ApiImplicitParam(name = "year", value = "年", dataType = "Int", paramType = "path")
    @RequestMapping(value = "/show/monthly/statistics/{year}", method = RequestMethod.GET)
    public ResultVo<List<UserLoginCountVO>> searchLoginUserCountMonthlyStatistics(@NotNull(message = "年度不能为空") @PathVariable Integer year) {
        List<UserLoginCountVO> list = loginCountService.getLoginUserCountMonthlyStatistics(ShiroTokenManager.getToken().getRenterId(), year);
        return ResultVoUtils.ok(list);
    }

    @ApiOperation(value = "登陆统计-获取当前租户下用户的活跃信息", notes = "", httpMethod = "GET")
    @RequestMapping(value = "/show/active/user", method = RequestMethod.GET)
    public ResultVo<ActiveUserCountVO> searchActiveUserInfo() {
        ActiveUserCountVO info = loginCountService.getActiveInfo(ShiroTokenManager.getToken().getRenterId());
        return ResultVoUtils.ok(info);
    }

    @ApiOperation(value = "登陆统计-获取部门登录排行详情", notes = "", httpMethod = "GET")
    @RequestMapping(value = "/show/dept/login/info", method = RequestMethod.GET)
    public ResultVo<DeptLoginInfoVO> searchDeptLoginInfo() {
        DeptLoginInfoVO deptLoginInfo = loginCountService.getDeptLoginInfo(ShiroTokenManager.getToken().getRenterId());
        return ResultVoUtils.ok(deptLoginInfo);
    }

    @ApiOperation(value = "登陆统计-获取登录详情信息", notes = "", httpMethod = "GET")
    @RequestMapping(value = "/show/login/details", method = RequestMethod.GET)
    public ResultVo<PageResultVo<LoginDetailsInfoVO>> searchLoginDetailsInfo(LoginSearchVO search) {
        // 年份补齐
        if(search.getYear() == null) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            search.setYear(c.get(Calendar.YEAR));
        }
        search.setRenterId(ShiroTokenManager.getToken().getRenterId());
        PageResultVo<LoginDetailsInfoVO> result = loginCountService.searchLoginDetailsInfo(search);
        return ResultVoUtils.ok(result);
    }

}
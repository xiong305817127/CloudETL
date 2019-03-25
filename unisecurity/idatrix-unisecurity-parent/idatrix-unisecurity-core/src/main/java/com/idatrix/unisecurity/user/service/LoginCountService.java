package com.idatrix.unisecurity.user.service;


import com.idatrix.unisecurity.common.domain.LoginCount;
import com.idatrix.unisecurity.common.vo.PageResultVo;
import com.idatrix.unisecurity.user.vo.*;

import java.util.List;

/**
 * Created by Administrator on 2018/12/26.
 */
public interface LoginCountService {

	int insertSelective(LoginCount loginCount);

	// 获取某个租户下某年的登录用户数量月度统计
	List<UserLoginCountVO> getLoginUserCountMonthlyStatistics(Long renterId, Integer year);

	// 获取用户的活跃信息
	ActiveUserCountVO getActiveInfo(Long renterId);

	// 获取部门登录排行详情
	DeptLoginInfoVO getDeptLoginInfo(Long renterId);

	// 获取登陆详情信息
    PageResultVo<LoginDetailsInfoVO> searchLoginDetailsInfo(LoginSearchVO search);
}

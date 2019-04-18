package com.idatrix.unisecurity.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.unisecurity.common.dao.LoginCountMapper;
import com.idatrix.unisecurity.common.domain.LoginCount;
import com.idatrix.unisecurity.common.vo.PageResultVo;
import com.idatrix.unisecurity.user.service.LoginCountService;
import com.idatrix.unisecurity.user.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/12/26.
 */
@Service
public class LoginCountServiceImpl implements LoginCountService {

    @Autowired
    private LoginCountMapper loginCountMapper;

    @Override
    public List<UserLoginCountVO> getLoginUserCountMonthlyStatistics(Long renterId, Integer year) {
        List<UserLoginCountVO> result = new ArrayList<>();
        // 获取某个租户下某年的登录用户次数月度统计
        List<UserLoginCountVO> voList = loginCountMapper.findLoginUserCountMonthlyStatistics(renterId, year);
        // 拼接出没有的月份
        Map<Integer, UserLoginCountVO> voMap =
                voList.stream().collect(Collectors.toMap((key -> key.getMonth()), (value -> value)));
        for (Integer i = 1; i <= 12; i++) {
            UserLoginCountVO vo = voMap.get(i);
            if (vo == null) {
                vo = new UserLoginCountVO(i, 0);
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public ActiveUserCountVO getActiveInfo(Long renterId) {
        // 本周内登录次数大于3的用户数
        Integer weekMoreThanThreeCount = loginCountMapper.findWeekLoginCountMoreThanThreeUserCount(renterId);
        // 本月内登次数大于10的用户数
        Integer monthMoreThanTenCount = loginCountMapper.findMonthLoginCountMoreThanTenUserCount(renterId);
        // 本周内使用平台的用户数
        Integer weekUseSystemCount = loginCountMapper.findWeekUseSystemUserCount(renterId);
        // 本月内使用平台的用户数
        Integer monthUseSystemCount = loginCountMapper.findMonthUseSystemUserCount(renterId);
        // 封装对象返回
        ActiveUserCountVO vo = new ActiveUserCountVO(weekMoreThanThreeCount, monthMoreThanTenCount, weekUseSystemCount, monthUseSystemCount);
        return vo;
    }

    @Override
    public DeptLoginInfoVO getDeptLoginInfo(Long renterId) {
        // 本月登录用户次数排行 TOP 10
        List<DeptUserLoginCountVO> monthLoginUserRankingList = loginCountMapper.findMonthLoginUserRankingList(renterId);
        // 登录用户次数总排行 TOP 10
        List<DeptUserLoginCountVO> sumLoginUserCountRankingList = loginCountMapper.findSumLoginUserCountRankingList(renterId);
        // 封装参数返回
        DeptLoginInfoVO vo = new DeptLoginInfoVO(monthLoginUserRankingList, sumLoginUserCountRankingList);
        return vo;
    }

    @Override
    public PageResultVo<LoginDetailsInfoVO> searchLoginDetailsInfo(LoginSearchVO search) {
        PageHelper.startPage(search.getPage(), search.getSize());
        List<LoginDetailsInfoVO> list = loginCountMapper.searchLoginDetailsInfo(search);
        PageInfo<LoginDetailsInfoVO> pageInfo = new PageInfo<>(list);
        PageResultVo result = new PageResultVo(pageInfo.getTotal(), list, search.getPage(), search.getSize());
        return result;
    }

    @Override
    public int insertSelective(LoginCount loginCount) {
        return loginCountMapper.insertSelective(loginCount);
    }

}

package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.common.domain.LoginCount;
import com.idatrix.unisecurity.user.vo.DeptUserLoginCountVO;
import com.idatrix.unisecurity.user.vo.UserLoginCountVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LoginCountMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoginCount record);

    int insertSelective(LoginCount record);

    LoginCount selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoginCount record);

    int updateByPrimaryKey(LoginCount record);

    // 获取某个租户下某年的登录用户数量月度统计
    List<UserLoginCountVO> findLoginUserCountMonthlyStatistics(@Param("renterId") Long renterId, @Param("year") Integer year);

    // 查询本周内登录次数大于3的用户数
    Integer findWeekLoginCountMoreThanThreeUserCount(@Param("renterId") Long renterId);

    // 查询本月内登次数大于10的用户数
    Integer findMonthLoginCountMoreThanTenUserCount(@Param("renterId") Long renterId);

    // 查询本周内使用平台的用户数
    Integer findWeekUseSystemUserCount(@Param("renterId") Long renterId);

    // 查询本月内使用平台的用户数
    Integer findMonthUseSystemUserCount(@Param("renterId") Long renterId);

    // 查询本月登录用户数排行 TOP 10
    List<DeptUserLoginCountVO> findMonthLoginUserRankingList(@Param("renterId") Long renterId);

    // 登录用户数总排行 TOP 10
    List<DeptUserLoginCountVO> findSumLoginUserCountRankingList(@Param("renterId") Long renterId);
}
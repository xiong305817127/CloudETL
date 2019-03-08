package com.idatrix.unisecurity.user.service;

import com.idatrix.unisecurity.common.domain.UserStatistic;

import java.util.List;

/**
 * Created by james on 2017/7/4.
 */
public interface UserStatisticService {
    int deleteByPrimaryKey(Long id);
    int insertSelective(UserStatistic record);
    List<UserStatistic> selectByRecent(Long id);
}

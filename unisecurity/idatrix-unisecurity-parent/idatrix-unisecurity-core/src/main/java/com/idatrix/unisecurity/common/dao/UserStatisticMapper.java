package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.common.domain.UserStatistic;

import java.util.List;

/**
 */
public interface UserStatisticMapper {

    int deleteByPrimaryKey(Long id);

    int insertSelective(UserStatistic record);

    List<UserStatistic> selectByRecent(Long id);

}

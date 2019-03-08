package com.idatrix.unisecurity.user.service.impl;

import com.idatrix.unisecurity.common.dao.UserStatisticMapper;
import com.idatrix.unisecurity.common.domain.UserStatistic;
import com.idatrix.unisecurity.core.mybatis.BaseMybatisDao;
import com.idatrix.unisecurity.user.service.UserStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 */
@Service
public class UserStatisticServiceImpl extends BaseMybatisDao<UserStatisticMapper> implements UserStatisticService{

    @Autowired
    private UserStatisticMapper userStatisticMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return userStatisticMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insertSelective(UserStatistic record) {
        return userStatisticMapper.insertSelective(record);
    }

    @Override
    public List<UserStatistic> selectByRecent(Long id) {
        return userStatisticMapper.selectByRecent(id);
    }

}

package com.idatrix.unisecurity.common.dao;

import com.idatrix.unisecurity.common.domain.ClientSystem;
import java.util.List;

/**
 * Created by james on 2017/6/19.
 */
public interface ClientSystemMapper {

    int insertSelective(ClientSystem record);

    List<ClientSystem> selectClientSystemByUserId(Long userId);

    /*查询出数据库中所有的子系统信息*/
    List<ClientSystem> loadClientSystem();

    List<ClientSystem> findByIds(String[] lientSystemArray);
}

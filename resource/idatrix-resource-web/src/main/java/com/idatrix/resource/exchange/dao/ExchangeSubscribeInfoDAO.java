package com.idatrix.resource.exchange.dao;


import com.idatrix.resource.exchange.po.ExchangeSubscribeInfoPO;

import java.util.List;

/**
 * Created by Administrator on 2018/11/8.
 */
public interface ExchangeSubscribeInfoDAO {

    void insert(ExchangeSubscribeInfoPO exchangeSubscribePO);

    void deleteById(Long id);

    int updateById(ExchangeSubscribeInfoPO exchangeSubscribePO);

    List<ExchangeSubscribeInfoPO> getByResourceCode(String resourceCode);

    ExchangeSubscribeInfoPO getById(Long id);
}

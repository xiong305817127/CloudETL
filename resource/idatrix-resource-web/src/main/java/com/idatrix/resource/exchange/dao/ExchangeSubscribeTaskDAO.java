package com.idatrix.resource.exchange.dao;


import com.idatrix.resource.exchange.po.ExchangeSubscribeTaskPO;

import java.util.List;

/**
 * Created by Administrator on 2018/11/8.
 */
public interface ExchangeSubscribeTaskDAO {

    void insert(ExchangeSubscribeTaskPO exchangeSubscribePO);

    int updateById(ExchangeSubscribeTaskPO exchangeSubscribePO);

    void deleteById(Long id);

    List<ExchangeSubscribeTaskPO> getByResourceCode(String resourceId);

    ExchangeSubscribeTaskPO getById(Long id);

    ExchangeSubscribeTaskPO getBySubNo(String subNo);

    /*获取最大的序列号，使用redis分布式序列号*/
    Long getMaxSubscribeSeq();
}

package com.idatrix.resource.exchange.service;

import com.idatrix.resource.exchange.vo.request.ExchangeSubscribeVO;

/**
 * Created by Administrator on 2018/11/7.
 */
public interface IExchangeSubscribeService {

    void processExchange(Long rentId, String user, ExchangeSubscribeVO exchangeSubscribeVO) throws Exception;
}

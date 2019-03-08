package com.idatrix.resource.webservice.dao;


import com.idatrix.resource.webservice.po.SubscribePO;

/**
 * Created by Administrator on 2018/7/16.
 */
public interface SubscribeDAO {

    SubscribePO getBySubscribeKey(String subKey);

}

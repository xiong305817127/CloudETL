package com.idatrix.resource.webservice.dao;

import com.idatrix.resource.webservice.po.SubscribeDbioPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2018/7/16.
 */
public interface SubscribeDbioDAO {

    /*通过订阅号和数据类型 input/output*/
    List<SubscribeDbioPO> getBySubscribeIdAndType(@Param("subscribeId")Long subscribeId,
                                                 @Param("paramType")String paramType);

}

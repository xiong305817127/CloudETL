package com.idatrix.resource.subscribe.dao;

import com.idatrix.resource.subscribe.po.SubscribeDbioPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2018/7/16.
 */
public interface SubscribeDbioDAO {

    void insert(SubscribeDbioPO subscribeDbioPO);

    void deleteById(Long id);

    int updateById(SubscribeDbioPO subscribeDbioPO);

    SubscribeDbioPO getById(Long id);


    /*通过订阅号和数据类型 input/output*/
    List<SubscribeDbioPO> getBySubscribeIdAndType(@Param("subscribeId")Long subscribeId,
                                                 @Param("paramType")String paramType);

}

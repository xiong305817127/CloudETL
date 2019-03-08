package com.idatrix.resource.subscribe.dao;

import com.idatrix.resource.catalog.po.StatisticsPO;
import com.idatrix.resource.subscribe.po.SubscribePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/16.
 */
public interface SubscribeDAO {

    void insert(SubscribePO subscribePO);

    void deleteById(Long id);

    int updateById(SubscribePO subscribePO);

    SubscribePO getById(Long id);

    Long getMaxSubscribeSeq();

    List<SubscribePO> queryByCondition(Map<String, String> con);

    List<SubscribePO> getByResourceIdAndProposer(@Param("resourceId")Long resourceId,
                    @Param("proposer") String proposer);

    /*根据状态和需要获取的月份数查询统计数据*/
    List<StatisticsPO> getStatisticsByStatusAndNums( @Param("num")int num,
                                                     @Param("status")String status);

    /*根据部门信息获取*/
    List<SubscribePO> getByDeptId(Long deptId);

    /*根据SUBXXXXX 获取订阅信息*/
    SubscribePO getBySubNo(String subNo);
}

package com.idatrix.resource.taskmanage.dao;

import com.idatrix.resource.taskmanage.po.SubTaskOverviewPO;
import com.idatrix.resource.taskmanage.po.SubTaskPO;
import com.idatrix.resource.taskmanage.vo.DescribeInfoVO;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/7.
 */
public interface SubTaskDAO {

    void insert(SubTaskPO subTaskPO);

    void deleteById(Long id);

    int updateById(SubTaskPO subTaskPO);

    SubTaskPO getBySubscribe(String etlSubscribeId);

    List<SubTaskPO> getByStatus(String status);

    List<SubTaskOverviewPO> queryOverview(Map<String, String> com);

    SubTaskPO getById(Long id);

    SubTaskPO getBySubTaskId(String subTaskId);

    /*获取任务执行数目*/
    Long getTaskCount();

    /*获取最近几个月的任务，导入数目统计情况，其中num表示月份数*/
    List<DescribeInfoVO> getTaskInfoByMonth(Long num);
}

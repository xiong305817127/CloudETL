package com.idatrix.resource.taskmanage.dao;

import com.idatrix.resource.taskmanage.po.SubTaskExecPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2018/8/7.
 */
public interface SubTaskExecDAO {

    void insert(SubTaskExecPO subTaskExecPO);

    void deleteById(Long id);

    int updateById(SubTaskExecPO subTaskExecPO);

    List<SubTaskExecPO> getExecInfoByTaskId(String taskId);

    SubTaskExecPO getById(Long id);

    /*根据ETL的Subscribe和Running查询任务具体执行*/
    SubTaskExecPO getByEtlSubscribeAndRunningId(@Param("subscribeId") String subscribeId,
                                                @Param("runningId") String runningId);

    /*根据ETL的subcribe获取数据导入总量*/
    Long getTotalImport(String subscribeId);
}

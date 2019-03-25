package com.idatrix.resource.taskmanage.vo;

import lombok.Data;

/**
 * Created by Administrator on 2018/8/11.
 */
@Data
public class SubTaskHistoryVO {

    /*对应 sub id*/
    private String taskId;

    /*对应 etl subscribe id*/
    private String etlSubscribeId;

    /*对应 etl subscribe id*/
    private String etlRunningId;

    /*任务运行结构*/
    private String status;

    /*任务开始时间*/
    private String startTime;

    /*任务结束时间*/
    private String endTime;

    /*数据量*/
    private Long dataCount;

}

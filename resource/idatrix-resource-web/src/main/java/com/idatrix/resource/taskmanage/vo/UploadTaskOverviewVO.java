package com.idatrix.resource.taskmanage.vo;

import lombok.Data;

/**
 * Created by Administrator on 2018/8/8.
 */
@Data
public class UploadTaskOverviewVO {

    /*作业名称*/
    private String taskName;

    /*etl subscribe id用户监控展示*/
    private String etlSubcribeId;

    /*部门*/
    private String deptName;

    /*类型*/
    private String taskType;

    /*开始时间*/
    private String startTime;

    /*结束时间*/
    private String endTime;

    /*数据量*/
    private Long dataCount;

    /*作业状态*/
    private String status;

    /*任务创建者*/
    private String creator;

}

package com.idatrix.resource.taskmanage.vo;

import lombok.Data;

/**
 * Created by Administrator on 2018/8/8.
 */
@Data
public class DescribeInfoVO {

    /*月份数*/
    private String month;

    /*任务数*/
    private Long taskCount;

    /*数据量*/
    private Long dateCount;

}



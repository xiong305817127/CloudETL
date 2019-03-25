package com.idatrix.resource.taskmanage.vo;

import lombok.Data;

import java.util.List;

/**
 * 作业统计
 */
@Data
public class TaskStatisticsVO {

    /*作业总数*/
    private Long count;

    /*作业统计信息*/
    List<DescribeInfoVO> describes;

}

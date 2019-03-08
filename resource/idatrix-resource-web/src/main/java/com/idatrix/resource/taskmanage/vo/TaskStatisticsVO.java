package com.idatrix.resource.taskmanage.vo;

import java.util.List;

/**
 * 作业统计
 */
public class TaskStatisticsVO {

    /*作业总数*/
    private Long count;

    /*作业统计信息*/
    List<DescribeInfoVO> describes;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<DescribeInfoVO> getDescribes() {
        return describes;
    }

    public void setDescribes(List<DescribeInfoVO> describes) {
        this.describes = describes;
    }
}

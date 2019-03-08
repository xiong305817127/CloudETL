package com.idatrix.resource.taskmanage.vo;

/**
 * Created by Administrator on 2018/8/8.
 */
public class DescribeInfoVO {

    /*月份数*/
    private String month;

    /*任务数*/
    private Long taskCount;

    /*数据量*/
    private Long dateCount;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Long taskCount) {
        this.taskCount = taskCount;
    }

    public Long getDateCount() {
        return dateCount;
    }

    public void setDateCount(Long dateCount) {
        this.dateCount = dateCount;
    }
}



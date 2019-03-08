package com.idatrix.resource.taskmanage.vo;

import java.util.List;

/**
 * 正在运行作业概览
 */
public class RunnningTaskVO {

    /*正在运行作业总数*/
    private Long count;

    /*正在运行的作业*/
    List<UploadTaskOverviewVO> taskInfo;

    /*正在运行的作业*/
    List<SubTaskOverviewVO> exchangTaskInfo;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<UploadTaskOverviewVO> getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(List<UploadTaskOverviewVO> taskInfo) {
        this.taskInfo = taskInfo;
    }

    public List<SubTaskOverviewVO> getExchangTaskInfo() {
        return exchangTaskInfo;
    }

    public void setExchangTaskInfo(List<SubTaskOverviewVO> exchangTaskInfo) {
        this.exchangTaskInfo = exchangTaskInfo;
    }
}

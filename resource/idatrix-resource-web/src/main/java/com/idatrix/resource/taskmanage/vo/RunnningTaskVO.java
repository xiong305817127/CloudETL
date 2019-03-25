package com.idatrix.resource.taskmanage.vo;

import lombok.Data;

import java.util.List;

/**
 * 正在运行作业概览
 */
@Data
public class RunnningTaskVO {

    /*正在运行作业总数*/
    private Long count;

    /*正在运行的作业*/
    List<UploadTaskOverviewVO> taskInfo;

    /*正在运行的作业*/
    List<SubTaskOverviewVO> exchangTaskInfo;

  }

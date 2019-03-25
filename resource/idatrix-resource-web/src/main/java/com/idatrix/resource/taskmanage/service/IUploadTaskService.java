package com.idatrix.resource.taskmanage.service;

import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.taskmanage.vo.RunnningTaskVO;
import com.idatrix.resource.taskmanage.vo.TaskStatisticsVO;
import com.idatrix.resource.taskmanage.vo.UploadTaskOverviewVO;

import java.util.Map;

/**
 * Created by Administrator on 2018/8/8.
 */
public interface IUploadTaskService {

    /*
   *  获取上报作业信息概览,由于是管理员操作，不做用户区分
   */
    ResultPager<UploadTaskOverviewVO> queryOverview(Map<String, String> con, Integer pageNum,
                                                    Integer pageSize);

    /*开始任务*/
    void startTask(String user, Long taskId);

    /*暂停任务*/
    void stopTask(String user, Long taskId);

    /*获取上报任务统计情况， 参数num: 表示统计月份个数，默认为6个*/
    TaskStatisticsVO getTaskStatistics(Long rentId, Long num);

    /*获取正在运行任务个数，参数num:  表示任务个数，默认为5个*/
    RunnningTaskVO getRunningTask(Long rentId, Long num);
}

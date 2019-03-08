package com.idatrix.resource.taskmanage.controller;

import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.taskmanage.service.ISubTaskService;
import com.idatrix.resource.taskmanage.vo.RunnningTaskVO;
import com.idatrix.resource.taskmanage.vo.SubTaskHistoryVO;
import com.idatrix.resource.taskmanage.vo.TaskStatisticsVO;
import com.idatrix.resource.taskmanage.vo.request.SubTaskOverviewRequestVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  交换任务
 */
@Controller
@RequestMapping("/subTask")
public class SubTaskController extends BaseController {

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ISubTaskService subTaskService;


    @RequestMapping(value="/getOverview", method = RequestMethod.POST)
    @ResponseBody
    public Result getOverview(@RequestBody SubTaskOverviewRequestVO requestVO) {

        String user = getUserName(); //"admin";
        LOG.info("/subTask/getOverview请求参数: {}", requestVO.toString());
        Map<String, String> conMap = newQueryCondition(user, requestVO);
        ResultPager tasks = null;
        try {
            tasks = subTaskService.queryOverview(conMap, requestVO.getPage(),
                    requestVO.getPageSize());
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
        }
//        LOG.info("/subTask/getOverview返回参数: {}", tasks.toString());
        return Result.ok(tasks);
    }

    @RequestMapping(value="/startTask", method = RequestMethod.GET)
    @ResponseBody
    public Result startTask(@RequestParam(value="taskId", required=true)Long taskId) {

        String user = getUserName(); //"admin";
        ResultPager tasks = null;
        try {
            subTaskService.startTask(user, taskId);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
        }
        return Result.ok(true);
    }


    @RequestMapping(value="/stopTask", method = RequestMethod.GET)
    @ResponseBody
    public Result stopTask(@RequestParam(value="taskId", required=true)Long taskId) {

        String user = getUserName(); //"admin";
        try {
            subTaskService.stopTask(user, taskId);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
        }
        return Result.ok(true);
    }

    @RequestMapping(value="/getHistory", method = RequestMethod.GET)
    @ResponseBody
    public Result getHistory(@RequestParam(value="taskId", required=true)String taskId,
                              @RequestParam(value = "page", required = false) Integer pageNum,
                               @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        String user = getUserName(); //"admin";
        List<SubTaskHistoryVO> subHistory = new ArrayList<SubTaskHistoryVO>();
        try {
            subHistory = subTaskService.getHistory(user, taskId);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
        }
        return Result.ok(subHistory);
    }


    @RequestMapping(value="/getRunning", method = RequestMethod.GET)
    @ResponseBody
    public Result getRunning(@RequestParam(value="num", required=false, defaultValue="5")Long num) {

        String user = getUserName(); //"admin";
        RunnningTaskVO runVO = new RunnningTaskVO();
        try {
            runVO = subTaskService.getRunningTask(num);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
        }
        return Result.ok(runVO);
    }


    @RequestMapping(value="/getStatistics", method = RequestMethod.GET)
    @ResponseBody
    public Result getStatistics(@RequestParam(value="num", required=false, defaultValue="6")Long num) {

        String user = getUserName(); //"admin";
        TaskStatisticsVO taskVO = new TaskStatisticsVO();
        try {
            taskVO = subTaskService.getTaskStatistics(num);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
        }
        return Result.ok(taskVO);
    }

    private Map<String, String> newQueryCondition(String user, SubTaskOverviewRequestVO requestVO){
        Map<String, String> queryCondition = new HashMap<String, String>();

        queryCondition.put("user", user);

        String taskName = requestVO.getTaskName();
        if(StringUtils.isNotEmpty(taskName)){
            queryCondition.put("subTaskId", taskName);
        }
        String code = requestVO.getCode();
        if(StringUtils.isNotEmpty(code)){
            queryCondition.put("code", code);
        }
        String subscribeDept = requestVO.getSubscribeDept();
        if(StringUtils.isNotEmpty(subscribeDept)){
            queryCondition.put("subDeptName", subscribeDept);
        }

        String provideDept = requestVO.getProvideDept();
        if(StringUtils.isNotEmpty(provideDept)) {
            queryCondition.put("provideDept", provideDept);
        }

        String taskStatus = requestVO.getTaskStatus();
        if(StringUtils.isNotEmpty(taskStatus)) {
            queryCondition.put("taskStatus", taskStatus);
        }
        return queryCondition;
    }

}

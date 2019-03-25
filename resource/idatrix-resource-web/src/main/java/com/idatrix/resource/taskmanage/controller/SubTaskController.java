package com.idatrix.resource.taskmanage.controller;

import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.common.utils.UserUtils;
import com.idatrix.resource.taskmanage.service.ISubTaskService;
import com.idatrix.resource.taskmanage.vo.RunnningTaskVO;
import com.idatrix.resource.taskmanage.vo.SubTaskHistoryVO;
import com.idatrix.resource.taskmanage.vo.TaskStatisticsVO;
import com.idatrix.resource.taskmanage.vo.request.SubTaskOverviewRequestVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@Api(value = "/subTask" , tags="任务管理-交换任务管理接口")
public class SubTaskController extends BaseController {

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ISubTaskService subTaskService;

    @Autowired
    private UserUtils userUtils;

    /**
     * 交换任务查询
     * @param requestVO
     * @return
     */
    @ApiOperation(value = "交换任务查询", notes="交换任务查询", httpMethod = "POST")
    @RequestMapping(value="/getOverview", method = RequestMethod.POST)
    @ResponseBody
    public Result getOverview(@RequestBody SubTaskOverviewRequestVO requestVO) {

        String user = getUserName(); //"admin";
        LOG.info("/subTask/getOverview请求参数: {}", requestVO.toString());
        Map<String, String> conMap = newQueryCondition(user, requestVO);
        conMap.put("rentId", userUtils.getCurrentUserRentId().toString());
        ResultPager tasks = null;
        try {
            tasks = subTaskService.queryOverview(conMap, requestVO.getPage(),
                    requestVO.getPageSize());
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
//        LOG.info("/subTask/getOverview返回参数: {}", tasks.toString());
        return Result.ok(tasks);
    }

    /**
     * 开始交换任务
     * @param taskId
     * @return
     */
    @ApiOperation(value = "开始交换任务", notes="开始交换任务", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="taskId", value="交换任务ID", required=true, dataType="Long")
    })
    @RequestMapping(value="/startTask", method = RequestMethod.GET)
    @ResponseBody
    public Result startTask(@RequestParam(value="taskId", required=true)Long taskId) {

        String user = getUserName(); //"admin";
        ResultPager tasks = null;
        try {
            subTaskService.startTask(user, taskId);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /**
     * 暂停交换任务
     * @param taskId
     * @return
     */
    @ApiOperation(value = "暂停交换任务", notes="暂停交换任务", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="taskId", value="交换任务ID", required=true, dataType="Long")
    })
    @RequestMapping(value="/stopTask", method = RequestMethod.GET)
    @ResponseBody
    public Result stopTask(@RequestParam(value="taskId", required=true)Long taskId) {

        String user = getUserName(); //"admin";
        try {
            subTaskService.stopTask(user, taskId);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /**
     * 获取交换历史
     * @param taskId
     * @return
     */
    @ApiOperation(value = "获取交换历史", notes="获取交换历史", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="taskId", value="交换任务ID", required=true, dataType="Long")
    })
    @RequestMapping(value="/getHistory", method = RequestMethod.GET)
    @ResponseBody
    public Result getHistory(@RequestParam(value="taskId", required=true)String taskId) {

        String user = getUserName(); //"admin";
        List<SubTaskHistoryVO> subHistory = new ArrayList<SubTaskHistoryVO>();
        try {
            subHistory = subTaskService.getHistory(user, taskId);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(subHistory);
    }

    /**
     * 获取正常运行的任务信息
     * @param num
     * @return
     */
    @ApiOperation(value = "获取正常运行的任务信息", notes="获取正常运行的任务信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="num", value="获取任务数，默认为5", required=false, dataType="Long")
    })
    @RequestMapping(value="/getRunning", method = RequestMethod.GET)
    @ResponseBody
    public Result getRunning(@RequestParam(value="num", required=false, defaultValue="5")Long num) {

        String user = getUserName(); //"admin";
        Long rentId = userUtils.getCurrentUserRentId();
        RunnningTaskVO runVO = new RunnningTaskVO();
        try {
            runVO = subTaskService.getRunningTask(rentId, num);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(runVO);
    }


    /**
     * 统计交换任务执行情况
     * @param num
     * @return
     */
    @ApiOperation(value = "统计交换任务执行情况", notes="统计交换任务执行情况", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="num", value="统计时间段，默认为最近6月", required=false, dataType="Long")
    })
    @RequestMapping(value="/getStatistics", method = RequestMethod.GET)
    @ResponseBody
    public Result getStatistics(@RequestParam(value="num", required=false, defaultValue="6")Long num) {

        String user = getUserName(); //"admin";
        TaskStatisticsVO taskVO = new TaskStatisticsVO();
        try {
            taskVO = subTaskService.getTaskStatistics(userUtils.getCurrentUserRentId(), num);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
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

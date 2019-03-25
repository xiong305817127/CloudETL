package com.idatrix.resource.taskmanage.controller;

import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.common.utils.UserUtils;
import com.idatrix.resource.datareport.service.IDataUploadService;
import com.idatrix.resource.taskmanage.service.IUploadTaskService;
import com.idatrix.resource.taskmanage.vo.RunnningTaskVO;
import com.idatrix.resource.taskmanage.vo.TaskStatisticsVO;
import com.idatrix.resource.taskmanage.vo.request.UploadTaskRequestVO;
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

import java.util.HashMap;
import java.util.Map;

/**
 * 上报作业查看和展示
 */

@Controller
@RequestMapping("/uploadTask")
@Api(value = "/uploadTask" , tags="任务管理-上报任务管理接口")
public class UploadTaskController extends BaseController{

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IDataUploadService dataUploadService;

    @Autowired
    private IUploadTaskService uploadTaskService;

    @Autowired
    private UserUtils userUtils;


    /**
     * 上报任务查询
     * @param requestVO
     * @return
     */
    @ApiOperation(value = "上报任务查询", notes="上报任务查询", httpMethod = "POST")
    @RequestMapping(value="/getOverview", method = RequestMethod.POST)
    @ResponseBody
    public Result getOverview(@RequestBody UploadTaskRequestVO requestVO) {

        String user = getUserName(); //"admin";
        Map<String, String> conMap = newQueryCondition(user, requestVO);
        conMap.put("rentId", userUtils.getCurrentUserRentId().toString());
        ResultPager tasks = null;
        try {
            tasks = uploadTaskService.queryOverview(conMap, requestVO.getPage(),
                    requestVO.getPageSize());
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(tasks);
    }

    /**
     * 获取正在运行的上报任务
     * @param num
     * @return
     */
    @ApiOperation(value = "获取正在运行的上报任务", notes="获取正在运行的上报任务", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="num", value="获取任务数，默认为5", required=false, dataType="Long")
    })
    @RequestMapping(value="/getRunning", method = RequestMethod.GET)
    @ResponseBody
    public Result getRunning(@RequestParam(value="num", required=false, defaultValue="5")Long num) {

        String user = getUserName(); //"admin";
        RunnningTaskVO runVO = new RunnningTaskVO();
        try {
            runVO = uploadTaskService.getRunningTask(userUtils.getCurrentUserRentId(), num);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(runVO);
    }

    /**
     * 统计上报任务执行情况
     * @param num
     * @return
     */
    @ApiOperation(value = "获取正在运行的上报任务", notes="获取正在运行的上报任务", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="num", value="统计最近月份数，默认为6个月", required=false, dataType="Long")
    })
    @RequestMapping(value="/getStatistics", method = RequestMethod.GET)
    @ResponseBody
    public Result getStatistics(@RequestParam(value="num", required=false, defaultValue="6")Long num) {

        String user = getUserName(); //"admin";
        Long rentId = userUtils.getCurrentUserRentId();
        TaskStatisticsVO taskVO = new TaskStatisticsVO();
        try {
            taskVO = uploadTaskService.getTaskStatistics(rentId, num);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(taskVO);
    }

    /**
     * 开始上报任务
     * @param taskId
     * @return
     */
    @ApiOperation(value = "开始上报任务", notes="开始上报任务", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="taskId", value="上报任务Id", required=false, dataType="Long")
    })
    @RequestMapping(value="/startTask", method = RequestMethod.GET)
    @ResponseBody
    public Result startTask(@RequestParam(value="taskId", required=true)Long taskId) {

        String user = getUserName(); //"admin";
        ResultPager tasks = null;
        try {
            uploadTaskService.startTask(user, taskId);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /**
     * 暂停上报任务
     * @param taskId
     * @return
     */
    @ApiOperation(value = "暂停上报任务", notes="暂停上报任务", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="taskId", value="上报任务Id", required=false, dataType="Long")
    })
    @RequestMapping(value="/stopTask", method = RequestMethod.GET)
    @ResponseBody
    public Result stopTask(@RequestParam(value="taskId", required=true)Long taskId) {

        String user = getUserName(); //"admin";
        try {
            uploadTaskService.stopTask(user, taskId);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }


    private Map<String, String> newQueryCondition(String user, UploadTaskRequestVO requestVO){
        Map<String, String> queryCondition = new HashMap<String, String>();

        queryCondition.put("user", user);

        String taskName = requestVO.getTaskName();
        if(StringUtils.isNotEmpty(taskName)){
            queryCondition.put("importTaskId", taskName);
        }
        String deptName = requestVO.getDeptName();
        if(StringUtils.isNotEmpty(deptName)){
            queryCondition.put("deptName", deptName);
        }
        String taskType = requestVO.getTaskType();
        if(StringUtils.isNotEmpty(taskType)){
            queryCondition.put("dataType", taskType);
        }

        String status = requestVO.getStatus();
        if(StringUtils.isNotEmpty(status)){
            queryCondition.put("status", status);
        }
        return queryCondition;
    }
}

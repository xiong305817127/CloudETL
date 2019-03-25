package com.idatrix.resource.subscribe.controller;

import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.common.utils.UserUtils;
import com.idatrix.resource.subscribe.service.ISubscribeService;
import com.idatrix.resource.subscribe.vo.SubscribeOverviewVO;
import com.idatrix.resource.subscribe.vo.SubscribeVO;
import com.idatrix.resource.subscribe.vo.SubscribeWebServiceVO;
import com.idatrix.resource.subscribe.vo.request.SubscribeApproveRequestVO;
import com.idatrix.resource.subscribe.vo.request.SubscribeOverviewRequestVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 订阅服务
 */

@Controller
@RequestMapping("/subscribe")
@Api(value = "/subscribe" , tags="资源管理-订阅流程处理接口")
public class SubscribeController extends BaseController {

    @Autowired
    private ISubscribeService resourceSubscribeService;


    @Autowired
    private UserUtils userUtils;

    /*数据库服务开放描述*/
    @ApiOperation(value = "获取数据库服务开放描述", notes="获取数据库服务开放描述", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订阅ID", required = true, dataType="Long")
    })
    @RequestMapping(value="/getWebservice", method = RequestMethod.GET)
    @ResponseBody
    public Result getWebservice(@RequestParam(value="id", required=true)Long id) {
        String user = getUserName();
        SubscribeWebServiceVO webServiceVO = null;
        try {
            webServiceVO = resourceSubscribeService.getWebserviceDescription(id);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage()); //调试Ajax屏蔽掉
        }
        return Result.ok(webServiceVO);
    }

    /*订阅关系管理*/
    @ApiOperation(value = "订阅关系管理", notes="对订阅关系进行管理", httpMethod = "POST")
    @RequestMapping(value="/getManage", method = RequestMethod.POST)
    @ResponseBody
    public Result getManage(@RequestBody SubscribeOverviewRequestVO requestVO) {

        String user = null;
        Map<String, String> conMap = newQueryCondition(user, requestVO);
        conMap.put("rentId", userUtils.getCurrentUserRentId().toString());
        ResultPager tasks = null;
        try {
            tasks = resourceSubscribeService.queryOverview(conMap, requestVO.getPage(),
                    requestVO.getPageSize());
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage()); //调试Ajax屏蔽掉
        }
        return Result.ok(tasks);
    }

    /*管理本人审批同意流程的订阅关系管理*/
    @ApiOperation(value = "已处理订阅关系管理", notes="管理本人审批同意流程的订阅关系管理", httpMethod = "POST")
    @RequestMapping(value="/getOwnManage", method = RequestMethod.POST)
    @ResponseBody
    public Result getOwnManage(@RequestBody SubscribeOverviewRequestVO requestVO) {

        String user = getUserName();
        Map<String, String> conMap = newQueryCondition(user, requestVO);
        ResultPager tasks = null;
        try {
            tasks = resourceSubscribeService.queryApproverSuccessOverview(conMap, requestVO.getPage(),
                    requestVO.getPageSize());
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage()); //调试Ajax屏蔽掉
        }
        return Result.ok(tasks);
    }

    /*停止订阅关系*/
    @ApiOperation(value = "停止订阅关系", notes="根据订阅ID停止订阅关系", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订阅ID", required = true, dataType="Long")
    })
    @RequestMapping(value="/stop", method = RequestMethod.GET)
    @ResponseBody
    public Result stop(@RequestParam(value="id", required=true)Long id){

        String user = getUserName(); //"admin";
        try {
            resourceSubscribeService.stopSubscribe(id);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage()); //调试Ajax屏蔽掉
        }
        return Result.ok(true);
    }

    /*恢复订阅关系*/
    @ApiOperation(value = "恢复订阅关系", notes="根据订阅ID恢复订阅关系", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订阅ID", required = true, dataType="Long")
    })
    @RequestMapping(value="/resume", method = RequestMethod.GET)
    @ResponseBody
    public Result resume(@RequestParam(value="id", required=true)Long id){

        String user = getUserName(); //"admin";
        try {
            resourceSubscribeService.resumeSubscibe(id);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage()); //调试Ajax屏蔽掉
        }
        return Result.ok(true);
    }

    /*
    *  获取资源订阅概览
    */
    @ApiOperation(value = "获取资源订阅概览", notes="获取资源订阅概览", httpMethod = "POST")
    @RequestMapping(value="/getOverview", method = RequestMethod.POST)
    @ResponseBody
    public Result<ResultPager<SubscribeOverviewVO>> getOverview(@RequestBody SubscribeOverviewRequestVO requestVO) {

        String user = getUserName(); //"admin";
        Map<String, String> conMap = newQueryCondition(user, requestVO);
        ResultPager tasks = null;
        try {
            tasks = resourceSubscribeService.queryOverview(conMap, requestVO.getPage(),
                    requestVO.getPageSize());
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage()); //调试Ajax屏蔽掉
        }
        return Result.ok(tasks);
    }

    /*
    *  订阅时候页面初始配置
    */
    @ApiOperation(value = "订阅时候页面初始配置", notes="获取订阅页面初始化配置", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "resourceId", value = "被订阅的资源ID", required = true, dataType="Long")
    })
    @RequestMapping(value="/getConfigInit")
    @ResponseBody
    public Result<SubscribeVO> getConfigInit(@RequestParam(value="resourceId", required=true)Long resourceId) {

        String user = getUserName(); //"admin";
        SubscribeVO svo = new SubscribeVO();
        try {

            svo = resourceSubscribeService.getInitConfig(resourceId);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage()); //调试Ajax屏蔽掉
        }
        return Result.ok(svo);
    }

   /*
    *  增加资源订阅
    */
    @ApiOperation(value = "新增资源订阅", notes="新增资源订阅", httpMethod = "POST")
    @RequestMapping(value="/add", method = RequestMethod.POST)
    @ResponseBody
    public Result addSubscribe(@RequestBody SubscribeVO subscribeVO) {

        String user = getUserName(); //"admin";
        try {
            resourceSubscribeService.addSubscribe(user, subscribeVO);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage()); //调试Ajax屏蔽掉
        }
        return Result.ok(true);
    }

    /*
    *  获取已经审核信息
    */
    @ApiOperation(value = "获取已经审核信息", notes="获取已经审核信息", httpMethod = "POST")
    @RequestMapping(value="/getProcessedApprove", method = RequestMethod.POST)
    @ResponseBody
    public Result getProcessedApprove(@RequestBody SubscribeOverviewRequestVO requestVO) {

        String user = getUserName(); //"admin";
        Map<String, String> conMap = newQueryCondition(user, requestVO);
        ResultPager tasks = null;
        try {
            tasks = resourceSubscribeService.queryProcessedApproveOverview(conMap, requestVO.getPage(),
                    requestVO.getPageSize());
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(tasks);
    }

    /*
     *   获取等待评审的资源
     */
    @ApiOperation(value = "获取等待评审的资源", notes="获取等待评审的资源", httpMethod = "POST")
    @RequestMapping(value="/getWaitApprove", method = RequestMethod.POST)
    @ResponseBody
    public Result getWaitApprove(@RequestBody SubscribeOverviewRequestVO requestVO) {

        String user = getUserName(); //"admin";
        Map<String, String> conMap = newQueryCondition(user, requestVO);
        ResultPager tasks = null;
        try {
            tasks = resourceSubscribeService.queryWaitApproveOverview(conMap, requestVO.getPage(),
                    requestVO.getPageSize());
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(tasks);
    }

    /*
    *  单个评审
    */
    @ApiOperation(value = "处理资源订阅评审", notes="处理资源订阅评审", httpMethod = "POST")
    @RequestMapping(value="/process", method = RequestMethod.POST)
    @ResponseBody
    public Result process(@RequestBody SubscribeApproveRequestVO requestVO) {

        String user = getUserName(); //"admin";
        try {
            resourceSubscribeService.processApprove(user, requestVO);
            //resourceSubscribeService.processApprove(user, requestVO.getId(), requestVO.getAction(),requestVO.getSuggestion());
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /*
    *  批量评审
    */
    @ApiOperation(value = "批量评审", notes="批量处理资源评审", httpMethod = "GET")
    @RequestMapping(value="/batchProcess", method = RequestMethod.GET)
    @ResponseBody
    public Result batchProcess(@RequestParam(value="ids", required=true)List<Long> ids){

        String user = getUserName(); //"admin";
        try {
            resourceSubscribeService.batchProcessApprove(user, ids);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /*
    *  获取订阅详情详情
    *
    */
    @ApiOperation(value = "获取订阅详情", notes="获取资源订阅详情", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订阅ID", required = true, dataType="Long")
    })
    @RequestMapping(value="/getDetail", method = RequestMethod.GET)
    @ResponseBody
    public Result<SubscribeVO> getDetail(@RequestParam(value="id", required=true)Long id){

        String user = getUserName(); //"admin";
        SubscribeVO subscribeVO = null;
        try {
            subscribeVO = resourceSubscribeService.getSubscribeById(id);
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(subscribeVO);
    }

    private Map<String, String> newQueryCondition(String user, SubscribeOverviewRequestVO requestVO){
        Map<String, String> queryCondition = new HashMap<String, String>();

        if(StringUtils.isNotEmpty(user)){
            queryCondition.put("user", user);
        }

        String name = requestVO.getName();
        if(StringUtils.isNotEmpty(name)){
            queryCondition.put("name", name);
        }
        String code = requestVO.getCode();
        if(StringUtils.isNotEmpty(code)){
            queryCondition.put("code", code);
        }
        String type = requestVO.getShareMethod();
        if(StringUtils.isNotEmpty(type)){
            queryCondition.put("type", type);
        }
        String deptName = requestVO.getDeptName();
        if(StringUtils.isNotEmpty(deptName)){
            queryCondition.put("deptName", deptName);
        }
        String deptCode = requestVO.getDeptCode();
        if(StringUtils.isNotEmpty(deptCode)){
            queryCondition.put("deptCode", deptCode);
        }
        String subStatus = requestVO.getSubStatus();
        if(StringUtils.isNotEmpty(subStatus)){
            queryCondition.put("subStatus", subStatus);
        }
        String startTime = requestVO.getApplyStartTime();
        if(StringUtils.isNotEmpty(startTime)){
            queryCondition.put("startTime", startTime);
        }
        String endTime = requestVO.getApplyEndTime();
        if(StringUtils.isNotEmpty(endTime)){
            queryCondition.put("endTime", changeQueryEndTime(endTime));
        }

        String subDeptName = requestVO.getSubDeptName();
        if(StringUtils.isNotEmpty(subDeptName)){
            queryCondition.put("subDeptName", subDeptName);
        }

        String approveStartTime = requestVO.getApproveStartTime();
        if(StringUtils.isNotEmpty(approveStartTime)){
            queryCondition.put("approveStartTime", approveStartTime);
        }

        String approveEndTime = requestVO.getApproveEndTime();
        if(StringUtils.isNotEmpty(approveEndTime)){
            queryCondition.put("approveEndTime", changeQueryEndTime(approveEndTime));
        }
        return queryCondition;
    }

    //调整结束时间，查询时候为包含关系，所有结束加一
    private String changeQueryEndTime(String endTime){
        Date end = DateTools.parseDate(endTime);

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(end);
        calendar.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天

        Date newEndTime = calendar.getTime();
        return DateTools.formatDate(newEndTime, "yyyy-MM-dd");
    }

}

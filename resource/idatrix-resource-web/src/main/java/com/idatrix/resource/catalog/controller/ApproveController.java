package com.idatrix.resource.catalog.controller;

import com.idatrix.resource.catalog.service.IApproveService;
import com.idatrix.resource.catalog.vo.ApproveRequestVO;
import com.idatrix.resource.catalog.vo.QueryRequestVO;
import com.idatrix.resource.catalog.vo.ResourceApproveVO;
import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.common.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.ConvertUtils;
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
 * 资源审批服务
 * @Author: Wangbin
 * @Date: 2018/6/9
 */

@Controller
@RequestMapping("/approve")
@Api(value = "/approve" , tags="资源管理-审批流程处理接口")
public class ApproveController extends BaseController{

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IApproveService iApproveService;

    @Autowired
    private UserUtils userUtils;

    /*
     *  用户点击提交审核：主要存在批量提交审核情况
     *
     */
    @ApiOperation(value = "提交审核", notes="用户点击提交审核，能够处理批量提交审核情况", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "需要批量审批ids", required = true, dataType="String"),
    })
    @RequestMapping("/submit")
    @ResponseBody
    public Result submit(@RequestParam(value = "ids", required = true) String ids) {

        String user = getUserName();
        String[] idArray = ids.split(",");
        for(String idEvery: idArray) {
            try{
                if(StringUtils.isNotEmpty(idEvery)) {
                    iApproveService.submitApprove(user, Long.valueOf(idEvery));
                }
            }catch (Exception e) {
                e.printStackTrace();
                return Result.error(e.getMessage());
            }
        }
        return Result.ok(true);
    }

    /*
    *  获取资源审批历史
     */
    @ApiOperation(value = "获取资源审批历史", notes="获取资源的审批流程处理历史", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "需要处理的ID", required = true, dataType="Long"),
    })
    @RequestMapping("/getHistory")
    @ResponseBody
    public Result getHistory(@RequestParam(value = "id", required = true) Long id) {

        String user = getUserName(); //"admin";
        List<ResourceApproveVO> raList = new ArrayList<ResourceApproveVO>();
        try{
            raList = iApproveService.getHistory(id);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(raList);
    }

    /*
    *  获取待注册审批资源列表
     */
    @ApiOperation(value = "获取待注册审批资源列表", notes="获取待注册审批资源列表", httpMethod = "POST")
    @RequestMapping(value = "/getWaitReg", method = RequestMethod.POST)
    @ResponseBody
    public Result getWaitReg(@RequestBody QueryRequestVO queryRequestVO){

        //TODO:考虑是否分页
        String user = getUserName(); //"admin";
        Map<String, String> queryCondition = newQueryCondition(user, queryRequestVO);

        ResultPager reg = null;
        try{
            reg = iApproveService.queryWaitRegApprove(queryCondition, queryRequestVO.getPage(),
                    queryRequestVO.getPageSize());
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(reg);
    }

    /*
    *  获取待发布审批资源
     */
    @ApiOperation(value = "获取待发布审批资源", notes="获取待发布审批资源列表", httpMethod = "POST")
    @RequestMapping(value = "/getWaitPub" , method = RequestMethod.POST)
    @ResponseBody
    public Result getWaitPub(@RequestBody  QueryRequestVO queryRequestVO){

        //TODO:考虑是否分页
        String user = getUserName(); //"admin";
        Map<String, String> queryCondition = newQueryCondition(user, queryRequestVO);

        ResultPager reg = null;
        try{
            reg = iApproveService.queryWaitPubApprove(queryCondition, queryRequestVO.getPage(),
                    queryRequestVO.getPageSize());
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(reg);
    }

    /*
     *  获取我审批过的资源
     */
    @ApiOperation(value = "获取我审批过发布流程资源列表", notes="获取我审批过发布流程资源列表", httpMethod = "POST")
    @RequestMapping(value = "/getProcessedPub", method = RequestMethod.POST)
    @ResponseBody
    public Result getProcessedPub(@RequestBody QueryRequestVO queryRequestVO){

        String user = getUserName(); //"admin";
        Map<String, String> queryCondition = newQueryCondition(user, queryRequestVO);
        ResultPager reg = null;
        try{
            reg = iApproveService.queryProcessedPubApprove(queryCondition, queryRequestVO.getPage(),
                    queryRequestVO.getPageSize());
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(reg);
    }

    /*
   *  获取我审批过的资源
   */
    @ApiOperation(value = "获取我审批过注册流程资源列表", notes="获取我审批过注册流程资源列表", httpMethod = "POST")
    @RequestMapping(value = "/getProcessedReg", method = RequestMethod.POST)
    @ResponseBody
    public Result getProcessedReg(@RequestBody QueryRequestVO queryRequestVO){

        String user = getUserName(); //"admin";
        Map<String, String> queryCondition = newQueryCondition(user, queryRequestVO);
        ResultPager reg = null;
        try{
            reg = iApproveService.queryProcessedRegApprove(queryCondition, queryRequestVO.getPage(),
                    queryRequestVO.getPageSize());
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(reg);
    }


    /*
   *  处理审核资源
    */

    @ApiOperation(value = "处理资源审核", notes="处理资源审核", httpMethod = "GET")
    @RequestMapping("/process")
    @ResponseBody
    public Result process(@RequestBody ApproveRequestVO approveRequestVO) {

        String user = getUserName(); //"admin";
        try{
            iApproveService.processApprove(user, approveRequestVO.getId(), approveRequestVO.getAction(),
                    approveRequestVO.getSuggestion());
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /*
     *  批量处理审核资源
     */
    @ApiOperation(value = "批量处理审核资源", notes="批量处理审核资源", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "需要处理资源ID", required = true, dataType="String"),
    })
    @RequestMapping("/batchProcess")
    @ResponseBody
    public Result batchProcess(@RequestParam(value="ids", required = true) String ids) {

        String user = getUserName(); //"admin";
        String idsArr[] = ids.split(",");
        Long[] resourceIds = (Long[]) ConvertUtils.convert(idsArr,Long.class);
        try{
            iApproveService.batchProcessApprove(user, resourceIds);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }


    /*
    *  中心管理员直接将下架的资源直接上架
    */
    @ApiOperation(value = "资源维护直接发布已经下架的资源", notes="中心管理员直接将下架的资源直接上架", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "需要处理资源ID", required = true, dataType="String"),
    })
    @RequestMapping("/pub")
    @ResponseBody
    public Result pub(@RequestParam(value="ids", required = true) String ids) {

        String user = getUserName(); //"admin";
        String idsArr[] = ids.split(",");
        Long[] resourceIds = (Long[]) ConvertUtils.convert(idsArr,Long.class);
        try{
            iApproveService.pubResource(user, resourceIds);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /*
    *  中心管理员直接将上架的资源直接下架
    */
    @ApiOperation(value = "资源维护将已发布资源直接下架", notes="资源维护将已发布资源直接下架的资源", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "需要处理资源ID", required = true, dataType="String"),
    })
    @RequestMapping("/recall")
    @ResponseBody
    public Result recall(@RequestParam(value="ids", required = true) String ids) {

        String user = getUserName(); //"admin";
        String idsArr[] = ids.split(",");
        Long[] resourceIds = (Long[]) ConvertUtils.convert(idsArr,Long.class);
        try{
            iApproveService.recallResource(user, resourceIds);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /*
   *  中心管理员直接退回修改
   */
    @ApiOperation(value = "资源维护将已下架资源直接退回修改", notes="资源维护将已下架资源直接退回修改", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "需要处理资源ID", required = true, dataType="String"),
    })
    @RequestMapping("/back")
    @ResponseBody
    public Result back(@RequestParam(value="ids", required = true) String ids) {

        String user = getUserName(); //"admin";
        String idsArr[] = ids.split(",");
        Long[] resourceIds = (Long[]) ConvertUtils.convert(idsArr,Long.class);
        try{
            iApproveService.backResource(user, resourceIds);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }

    /*
     *  查询流程处理中的资源
     */
    @ApiOperation(value = "查询正在各个流程处理中的资源", notes="查询正在各个流程处理中的资源", httpMethod = "GET")
    @RequestMapping(value = "/maintainQuery", method = RequestMethod.POST)
    @ResponseBody
    public Result maintainQuery(@RequestBody QueryRequestVO queryRequestVO){

        String user = getUserName(); //"admin";
        Map<String, String> queryCondition = newQueryCondition(user, queryRequestVO);
        queryCondition.put("rentId", userUtils.getCurrentUserRentId().toString());
        ResultPager reg = null;
        try{
            reg = iApproveService.queryMaintainResource(queryCondition, queryRequestVO.getPage(),
                    queryRequestVO.getPageSize());
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(reg);
    }


    private Map<String, String> newQueryCondition(String user, QueryRequestVO queryRequestVO){
        Map<String, String> queryCondition = new HashMap<String, String>();
        queryCondition.put("approver", user);
        String name = queryRequestVO.getName();
        if(StringUtils.isNotEmpty(name)){
            queryCondition.put("name", name);
        }
        String code = queryRequestVO.getCode();
        if(StringUtils.isNotEmpty(code)){
            queryCondition.put("code", code);
        }

        String deptName = queryRequestVO.getDeptName();
        if(StringUtils.isNotEmpty(deptName)){
            queryCondition.put("dept_name", deptName);
        }

        String deptCode = queryRequestVO.getDeptCode();
        if(StringUtils.isNotEmpty(deptCode)){
            queryCondition.put("dept_code", deptCode);
        }

        String status = queryRequestVO.getStatus();
        if(StringUtils.isNotEmpty(status)){
            queryCondition.put("status", status);
        }
        return queryCondition;
    }

}

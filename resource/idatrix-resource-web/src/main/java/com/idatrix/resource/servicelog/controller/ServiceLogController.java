package com.idatrix.resource.servicelog.controller;

import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.servicelog.service.IServiceLogService;
import com.idatrix.resource.servicelog.vo.ServiceLogDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务日志管理
 */

@Controller
@RequestMapping("/serviceLog")
@Api(value = "/serviceLog" , tags="日志管理-服务日志管理接口")
public class ServiceLogController extends BaseController {

    private final IServiceLogService iServiceLogService;

    @Autowired
    public ServiceLogController(IServiceLogService iServiceLogService) {
        this.iServiceLogService = iServiceLogService;
    }

    /*根据ID查询源服务信息*/
    @ApiOperation(value = "查询服务日志信息", notes="查询服务日志信息", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="serviceCode", value="服务编码", required=false, dataType="String"),
            @ApiImplicitParam(name="serviceName", value="服务名称", required=false, dataType="String"),
            @ApiImplicitParam(name="serviceType", value="服务类型", required=false, dataType="String"),
            @ApiImplicitParam(name="callerDeptName", value="服务调用部门", required=false, dataType="String"),
            @ApiImplicitParam(name="isSuccess", value="是否执行成功", required=false, dataType="String"),
            @ApiImplicitParam(name="startTime", value="开始时间", required=false, dataType="String"),
            @ApiImplicitParam(name="endTime", value="结束时间", required=false, dataType="String"),
            @ApiImplicitParam(name="page", value="分页起始页", required=false, dataType="Long"),
            @ApiImplicitParam(name="pageSize", value="分页页面大小", required=false, dataType="Long")
    })
    @RequestMapping("/getAllServiceLog")
    @ResponseBody
    public Result getAllServiceLogByCondition(@RequestParam(value = "serviceCode", required = false) String  serviceCode,
                                              @RequestParam(value = "serviceName", required = false) String  serviceName,
                                              @RequestParam(value = "serviceType", required = false) String  serviceType,
                                              @RequestParam(value = "callerDeptName", required = false) String callerDeptName,
                                              @RequestParam(value = "isSuccess", required = false) String isSuccess,
                                              @RequestParam(value = "startTime", required = false) String startTime,
                                              @RequestParam(value = "endTime", required = false) String endTime,
                                              @RequestParam(value = "page", required = false) Integer pageNum,
                                              @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        Map<String, Object> queryCondition = new HashMap<String, Object>();

        if (!CommonUtils.isEmptyStr(serviceCode))
            queryCondition.put("serviceCode", serviceCode);

        if (!CommonUtils.isEmptyStr(serviceName))
            queryCondition.put("serviceName", serviceName);

        if (!CommonUtils.isEmptyStr(serviceType))
            queryCondition.put("serviceType", serviceType);

        if (!CommonUtils.isEmptyStr(callerDeptName))
            queryCondition.put("callerDeptName", callerDeptName);

        if (!CommonUtils.isEmptyStr(isSuccess))
            queryCondition.put("isSuccess", isSuccess);

        if (!CommonUtils.isEmptyStr(startTime))
            queryCondition.put("startTime", startTime);

        if (!CommonUtils.isEmptyStr(endTime))
            queryCondition.put("endTime", endTime);

        try {
            ResultPager tasks =
                    iServiceLogService.getServiceLogInfoByCondition(queryCondition, pageNum, pageSize);
            return Result.ok(tasks);
        } catch (Exception e) {
            return Result.error("查询服务日志出现异常");
        }
    }

    /*根据服务日志ID查询服务执行具体信息*/
    @ApiOperation(value = "获取服务日志详情", notes="查询服务日志具体信息", httpMethod = "GET")
    @ApiImplicitParams({
           @ApiImplicitParam(name="id", value="服务日志ID", required=true, dataType="Long")
    })
    @RequestMapping("/getServiceLogDetailById")
    @ResponseBody
    public Result getServiceLogDetailById(@RequestParam(value = "id", required = true) Long id) {
        ServiceLogDetailVO serviceLogDetailVO;
        try {
            serviceLogDetailVO =  iServiceLogService.getServiceLogDetailById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取日志详情发生错误 " + e.getMessage());
        }
        return Result.ok(serviceLogDetailVO);
    }
}

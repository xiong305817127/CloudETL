package com.idatrix.resource.servicelog.controller;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.CommonConstants;
import com.idatrix.resource.common.utils.CommonUtils;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.servicelog.service.IServiceLogService;
import com.idatrix.resource.servicelog.vo.ServiceLogDetailVO;
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
public class ServiceLogController extends BaseController {

    private final IServiceLogService iServiceLogService;

    private static final Logger LOG = LoggerFactory.getLogger(ServiceLogController.class);

    @Autowired
    public ServiceLogController(IServiceLogService iServiceLogService) {
        this.iServiceLogService = iServiceLogService;
    }

    /*根据ID查询源服务信息*/
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

//            if (tasks != null)
//                return Result.ok(tasks);
//            else
//                return Result.error(CommonConstants.EC_NOT_EXISTED_VALUE, "服务日志不存在");
        } catch (Exception e) {
            return Result.error(CommonConstants.EC_UNEXPECTED, "查询服务日志出现异常");
        }
    }

    /*根据服务日志ID查询服务执行具体信息*/
    @RequestMapping("/getServiceLogDetailById")
    @ResponseBody
    public Result getServiceLogDetailById(@RequestParam(value = "id", required = true) Long id) {
        ServiceLogDetailVO serviceLogDetailVO;
        try {
            serviceLogDetailVO =  iServiceLogService.getServiceLogDetailById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CommonConstants.EC_UNEXPECTED, "获取日志详情发生错误 " + e.getMessage());
        }
        return Result.ok(serviceLogDetailVO);

//        if (serviceLogDetailVO != null)
//            return Result.ok(serviceLogDetailVO);
//        else
//            return Result.error(CommonConstants.EC_NOT_EXISTED_VALUE, "当前服务日志详情不存在");
    }
}

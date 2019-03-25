package com.idatrix.resource.basedata.controller;

import com.idatrix.resource.basedata.service.ISystemConfigService;
import com.idatrix.resource.basedata.vo.SystemConfigVO;
import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robin Wing on 2018-6-14.
 */

@Controller
@RequestMapping("/sysconfig")
@Api(value = "/sysconfig" , tags="基础功能-系统配置")
public class SystemConfigController extends BaseController {

    @Autowired
    private ISystemConfigService systemConfigService;

    @Autowired
    private UserUtils userUtils;

    /*获取系统配置*/
    @ApiOperation(value = "获取系统配置", notes="获取系统配置", httpMethod = "GET")
    @RequestMapping("/getConfig")
    @ResponseBody
    public Result getConfig() {

       //String user = getUserName();
       Long rentId  = userUtils.getCurrentUserRentId();
       SystemConfigVO scVO = systemConfigService.getSystemConfig(rentId);
       return Result.ok(scVO);
    }

    /*
    *  存储系统配置
    */
    @ApiOperation(value = "存储系统配置", notes="存储系统配置", httpMethod = "POST")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody SystemConfigVO sysconfigVO) {

//        String user = getUserName();
//        Long id = systemConfigService.save(user, sysconfigVO);
//        Map<String, Object> attach = new HashMap<String, Object>();
//        attach.put("id", id);
//        return Result.ok(attach);

        //配置时候需要处理异常：源路径和目标路径一致时ETL会报错，配置时候过滤掉。
        String user = getUserName();
        Long rentId = userUtils.getCurrentUserRentId();

        Long id = 0L;
        try {
            id = systemConfigService.save(rentId, user, sysconfigVO);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }

        Map<String, Object> attach = new HashMap<String, Object>();
        attach.put("id", id);
        return Result.ok(attach);
    }
}

package com.idatrix.resource.basedata.controller;

import com.idatrix.resource.basedata.service.ISystemConfigService;
import com.idatrix.resource.basedata.vo.SystemConfigVO;
import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.UserUtils;
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
public class SystemConfigController extends BaseController {

    @Autowired
    private ISystemConfigService systemConfigService;

    @Autowired
    private UserUtils userUtils;

    /*获取系统配置*/
    @RequestMapping("/getConfig")
    @ResponseBody
    public Result getConfig() {

        String user = getUserName();
        SystemConfigVO scVO = systemConfigService.getSystemConfig(user);
       return Result.ok(scVO);
    }

    /*
    *  存储系统配置
    */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody SystemConfigVO sysconfigVO) {

//        String user = getUserName();
//        Long id = systemConfigService.save(user, sysconfigVO);
//        Map<String, Object> attach = new HashMap<String, Object>();
//        attach.put("id", id);
//        return Result.ok(attach);

        //配置时候需要处理异常：源路径和目标路径一致时ETL会报错，配置时候过滤掉。
        String user = userUtils.getCurrentSaveUserInfo(); //getUserName();

        Long id = 0L;
        try {
            id = systemConfigService.save(user, sysconfigVO);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
        }

        Map<String, Object> attach = new HashMap<String, Object>();
        attach.put("id", id);
        return Result.ok(attach);
    }
}

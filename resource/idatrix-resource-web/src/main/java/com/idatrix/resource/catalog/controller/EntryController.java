package com.idatrix.resource.catalog.controller;

import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.common.collect.ImmutableMap;

@Controller
@RequestMapping("/entry")
@Api(value = "/entry" , tags="系统测试-启动测试接口", hidden = true)
public class EntryController extends BaseController {

    /**
     * 应用启动验证
     *
     * @return
     */
    @ApiOperation(value = "请求测试接口", notes="请求测试接口", httpMethod = "GET")
    @RequestMapping("/verify")
    @ResponseBody
    public Result getSystemData() {
        ImmutableMap entryMap = ImmutableMap.of("verification","idatrix-resource Operating normally!");
        return Result.ok(entryMap);
    }


}
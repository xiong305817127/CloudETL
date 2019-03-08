package com.idatrix.resource.catalog.controller;

import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.common.collect.ImmutableMap;

@Controller
@RequestMapping("/entry")
public class EntryController extends BaseController {

    /**
     * 应用启动验证
     *
     * @return
     */
    @RequestMapping("/verify")
    @ResponseBody
    public Result getSystemData() {
        ImmutableMap entryMap = ImmutableMap.of("verification","idatrix-resource Operating normally!");
        return Result.ok(entryMap);
    }


}
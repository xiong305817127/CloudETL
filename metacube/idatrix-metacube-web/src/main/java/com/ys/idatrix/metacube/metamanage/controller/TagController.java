package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.Tag;
import com.ys.idatrix.metacube.metamanage.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName TagController
 * @Description
 * @Author ouyang
 * @Date
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/tag")
@Api(value = "/TagController", tags = "元数据管理-元数据定义-标签处理接口")
public class TagController {

    @Autowired
    private TagService tagService;

    @ApiOperation(value = "元数据定义-标签列表", notes = "获取当前用户使用过的标签列表", httpMethod = "GET")
    @GetMapping("/list")
    public ResultBean<List<Tag>> tagList() {
        List<Tag> tagList = tagService.findTagList();
        return ResultBean.ok(tagList);
    }

    @ApiOperation(value = "元数据定义-标签列表", notes = "获取当前租户下所有使用过的标签列表", httpMethod = "GET")
    @GetMapping("/renter/list")
    public ResultBean<List<String>> showTagListByRenter() {
        List<String> tagList = tagService.findTagListByRenterId(UserUtils.getRenterId());
        return ResultBean.ok(tagList);
    }

}
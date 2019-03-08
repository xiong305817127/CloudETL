package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.service.MetadataService;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName MetadataController
 * @Description 元数据控制层
 * @Author ouyang
 * @Date
 */
@Validated
@Slf4j
@RestController
@RequestMapping("/metadata")
@Api(value = "/metadata" , tags="元数据管理-元数据定义")
public class MetadataController {

    @Autowired
    private MetadataService metadataService;

    @ApiOperation(value = "查询元数据信息", notes = "可以使用在表，视图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "metadataVo", value = "元数据搜索实体类", dataType = "MetadataSearchVo", paramType = "body"),
            @ApiImplicitParam(name = "page", value = "当前页码", dataType = "Int", paramType = "query"),
            @ApiImplicitParam(name = "rows", value = "当前显示行数", dataType = "Int", paramType = "query")
    })
    @GetMapping("/search")
    public ResultBean<PageResultBean<Metadata>> search(@RequestBody MetadataSearchVo searchVo, @RequestParam(required = false, defaultValue = "1") int page,
                           @RequestParam(required = false, defaultValue = "10") int rows) {
        PageResultBean<Metadata> pageResultVo = metadataService.search(searchVo);
        return ResultBean.ok(pageResultVo);
    }

}

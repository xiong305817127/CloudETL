package com.ys.idatrix.metacube.sysmanage.controller;

import com.idatrix.unisecurity.sso.client.enums.ResultEnum;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.common.group.Update;
import com.ys.idatrix.metacube.metamanage.domain.Theme;
import com.ys.idatrix.metacube.sysmanage.service.ThemeService;
import com.ys.idatrix.metacube.metamanage.vo.request.SearchVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Created by Administrator on 2019/1/15.
 */
@Slf4j
@Validated
@RequestMapping("/theme")
@RestController
@Api(value = "/ThemeController", tags = "元数据管理-系统管理-主题处理接口")
public class ThemeController {

    @Autowired
    private ThemeService themeService;

    @ApiOperation(value = "查询主题", notes = "带分页，主题管理使用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "搜索关键字", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "Int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", dataType = "Int", paramType = "query"),
    })
    @GetMapping("/search")
    public ResultBean<PageResultBean<Theme>> search(@RequestParam(value = "keyword", required = false) String keyword,
                                                    @RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                                                    @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        SearchVO searchVO = new SearchVO();
        searchVO.setKeyword(keyword);
        searchVO.setPageNum(pageNum);
        searchVO.setPageSize(pageSize);
        PageResultBean<Theme> result = themeService.search(searchVO);
        return ResultBean.ok(result);
    }

    @ApiOperation(value = "新增主题", notes = "主题管理使用")
    @ApiImplicitParam(name = "theme", value = "主题实体类", required = true, dataType = "Theme")
    @PostMapping("/add")
    public ResultBean add(@Validated(Save.class) @RequestBody Theme theme, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        themeService.add(theme);
        return ResultBean.ok("新增成功");
    }

    @ApiOperation(value = "修改主题", notes = "，主题管理使用")
    @ApiImplicitParam(name = "theme", value = "主题实体类", required = true, dataType = "Theme")
    @PutMapping("/update")
    public ResultBean update(@Validated(Update.class) @RequestBody Theme theme, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        themeService.update(theme);
        return ResultBean.ok("修改成功");
    }

    @ApiOperation(value = "删除主题", notes = "，主题管理使用")
    @ApiImplicitParam(name = "ids", value = "id，可能多个，以,隔开", dataType = "String", paramType = "query")
    @DeleteMapping("/delete")
    public ResultBean delete(@NotBlank(message = "参数ids不能为空") String ids) {
        int count = themeService.delete(ids);
        return ResultBean.ok("成功删除" + count + "数据");
    }

    @ApiOperation(value = "查询主题列表", notes = "")
    @GetMapping("/list")
    public ResultBean<List<Theme>> themeList() {
        List<Theme> list = themeService.findThemeList();
        return ResultBean.ok(list);
    }

}

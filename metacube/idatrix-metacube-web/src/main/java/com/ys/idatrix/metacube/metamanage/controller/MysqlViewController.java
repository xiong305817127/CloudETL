package com.ys.idatrix.metacube.metamanage.controller;

import com.idatrix.unisecurity.sso.client.enums.ResultEnum;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.common.group.Update;
import com.ys.idatrix.metacube.metamanage.service.ViewService;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.ViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName MysqlViewController
 * @Description mysql 视图控制层
 * @Author ouyang
 * @Date
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/mysql/view")
@Api(value = "/MysqlViewController", tags = "元数据管理-元数据定义-MYSQL视图处理接口")
public class MysqlViewController {

    @Autowired
    @Qualifier("mysqlViewService")
    private ViewService viewService;

    @ApiOperation(value = "元数据定义-MYSQL视图搜索", notes="传递参数schemaId,status", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schemaId", value = "模式ID", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "status", value = "状态，0,草稿，1,有效", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", dataType = "int", paramType = "query"),
    })
    @GetMapping("/search")
    public ResultBean<PageResultBean<ViewVO>> search(@RequestParam("schemaId") Long schemaId,
                                                     @RequestParam(value = "status", defaultValue = "1", required = false) Integer status,
                                                     @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                     @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        MetadataSearchVo searchVO = new MetadataSearchVo();
        searchVO.setDatabaseType(DatabaseTypeEnum.MYSQL.getCode()); // 当前为mysql
        searchVO.setResourceType(2); // 2 为视图
        searchVO.setPageNum(pageNum);
        searchVO.setPageSize(pageSize);
        searchVO.setStatus(status);
        searchVO.setSchemaId(schemaId);
        PageResultBean<ViewVO> result = viewService.search(searchVO);
        return ResultBean.ok(result);
    }

    @ApiOperation(value = "元数据定义-MYSQL根据ID查询视图", notes="", httpMethod = "GET")
    @GetMapping("/search/{viewId}")
    public ResultBean<DBViewVO> searchById(@NotNull(message = "视图ID不能为空") @PathVariable Long viewId) {
        DBViewVO result = viewService.searchById(viewId);
        return ResultBean.ok(result);
    }

    @ApiOperation(value = "元数据定义-MYSQL视图保存并生效", notes = "新增数据时，尝试生效数据库中", httpMethod = "POST")
    @ApiImplicitParam(name = "view", value = "mysql view 实体类", dataType = "DBViewVO", paramType = "body")
    @PostMapping("/add")
    public ResultBean add(@Validated(Save.class) @RequestBody DBViewVO view, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        log.info("新增 mysql 视图数据：{}", view);
        viewService.add(view);
        return ResultBean.ok();
    }

    @ApiOperation(value = "元数据定义-MYSQL视图修改并生效", notes = "修改数据时，尝试生效到数据库中", httpMethod = "PUT")
    @ApiImplicitParam(name = "view", value = "mysql view 实体类", dataType = "DBViewVO", paramType = "body")
    @PutMapping("/update")
    public ResultBean update(@Validated(Update.class) @RequestBody DBViewVO view, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        log.info("修改 mysql 视图数据：{}", view);
        viewService.update(view);
        return ResultBean.ok();
    }

    @ApiOperation(value = "元数据定义-MYSQL视图存为草稿", notes = "存为草稿，并不去生效到数据库中", httpMethod = "POST")
    @ApiImplicitParam(name = "view", value = "mysql view 实体类", dataType = "DBViewVO", paramType = "body")
    @PostMapping("/add/draft")
    public ResultBean addDraft(@Validated(Save.class) @RequestBody DBViewVO view, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        log.info("保存 mysql 视图草稿：{}", view);
        viewService.addDraft(view);
        return ResultBean.ok();
    }

    @ApiOperation(value = "元数据定义-MYSQL视图草稿修改", notes = "草稿修改并不生效，生效表存为草稿", httpMethod = "PUT")
    @ApiImplicitParam(name = "id", value = "草稿id", dataType = "Long", paramType = "body")
    @PutMapping("/update/draft")
    public ResultBean updateDraft(@Validated(Update.class) @RequestBody DBViewVO view, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        log.info("修改 mysql 视图草稿：{}", view);
        viewService.updateDraft(view);
        return ResultBean.ok();
    }

    @ApiOperation(value = "元数据定义-MYSQL视图删除", notes = "实体和草稿删除都采用此方法", httpMethod = "DELETE")
    @ApiImplicitParam(name = "idList", value = "视图id集合，数组", dataType = "array", paramType = "body")
    @DeleteMapping("/delete")
    public ResultBean delete(@RequestBody List<Long> idList) {
        log.info("删除视图实体或视图草稿，idList：{}", idList);
        viewService.delete(idList);
        return ResultBean.ok();
    }

}
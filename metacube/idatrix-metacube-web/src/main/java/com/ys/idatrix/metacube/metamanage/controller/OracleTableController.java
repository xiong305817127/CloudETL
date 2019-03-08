package com.ys.idatrix.metacube.metamanage.controller;

import com.idatrix.unisecurity.sso.client.enums.ResultEnum;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.common.group.Update;
import com.ys.idatrix.metacube.metamanage.service.OracleTableService;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.OracleTableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.TableConstraintVO;
import com.ys.idatrix.metacube.metamanage.vo.request.TableVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName OracleTableController
 * @Description oracle表操作相关API
 * @Author ouyang
 * @Date
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/oracle/table")
@Api(value = "/OracleTableController", tags = "元数据管理-元数据定义-ORACLE表处理接口")
public class OracleTableController {

    @Autowired
    private OracleTableService oracleTableService;

    @ApiOperation(value = "元数据定义-ORACLE表搜索", notes="传递参数schemaId,status", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schemaId", value = "模式ID", dataType = "Long", paramType = "query", required = true),
            @ApiImplicitParam(name = "status", value = "状态，0,草稿，1,有效", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageNum", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", dataType = "int", paramType = "query"),
    })
    @GetMapping("/search")
    public ResultBean<PageResultBean<TableVO>> search(@RequestParam("schemaId") Long schemaId,
                                                      @RequestParam(value = "status", defaultValue = "1", required = false) Integer status,
                                                      @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        MetadataSearchVo searchVO = new MetadataSearchVo();
        searchVO.setDatabaseType(DatabaseTypeEnum.ORACLE.getCode()); // 当前为oracle
        searchVO.setResourceType(1); // 1 为表
        searchVO.setPageNum(pageNum);
        searchVO.setPageSize(pageSize);
        searchVO.setStatus(status);
        searchVO.setSchemaId(schemaId);
        PageResultBean<TableVO> result = oracleTableService.search(searchVO);
        return ResultBean.ok(result);
    }

    @ApiOperation(value = "元数据定义-ORACLE根据表ID查询数据", notes="", httpMethod = "GET")
    @GetMapping("/search/{tableId}")
    public ResultBean<OracleTableVO> searchById(@NotNull(message = "表ID不能为空") @PathVariable Long tableId) {
        OracleTableVO result = oracleTableService.searchById(tableId);
        return ResultBean.ok(result);
    }

    @ApiOperation(value = "元数据定义-ORACLE根据模式ID查询表集合", notes = "", httpMethod = "GET")
    @GetMapping("/search/table/{schemaId}")
    public ResultBean<List<TableVO>> searchBySchemaId(@NotNull(message = "模式ID不能为空") @PathVariable Long schemaId) {
        List<TableVO> list = oracleTableService.searchBySchemaId(schemaId);
        return ResultBean.ok(list);
    }

    @ApiOperation(value = "元数据定义-ORACLE查询所有的表空间", notes = "注意是当前模式下所有表空间", httpMethod = "GET")
    @ApiImplicitParam(name = "schemaId", value = "模式ID", dataType = "Long", paramType = "path")
    @GetMapping("/tablespace/{schemaId}")
    public ResultBean<List<String>> allTablespace(@PathVariable @NotNull(message = "模式ID不能为空")  Long schemaId) {
        List<String> list = oracleTableService.findTablespaceListBySchemaId(schemaId);
        return ResultBean.ok(list);
    }

    @ApiOperation(value = "元数据定义-ORACLE查询所有的序列", notes = "注意是当前模式下所有序列", httpMethod = "GET")
    @ApiImplicitParam(name = "schemaId", value = "模式ID", dataType = "Long", paramType = "path")
    @GetMapping("/sequence/{schemaId}")
    public ResultBean<List<String>> allSequence(@PathVariable @NotNull(message = "模式ID不能为空")  Long schemaId) {
        List<String> list = oracleTableService.findSequenceListBySchemaId(schemaId);
        return ResultBean.ok(list);
    }

    @ApiOperation(value = "元数据定义-ORACLE查询某张表下可参考的约束", notes = "包含：主键约束，唯一约束", httpMethod = "GET")
    @ApiImplicitParam(name = "tableId", value = "表ID", dataType = "Long", paramType = "path")
    @GetMapping("/constraint/{tableId}")
    public ResultBean<List<TableConstraintVO>> tableConstraint(@PathVariable @NotNull(message = "表id不能为空") Long tableId) {
        List<TableConstraintVO> list = oracleTableService.findConstraintByTableId(tableId);
        return ResultBean.ok(list);
    }

    @ApiOperation(value = "元数据定义-ORACLE表保存并生效", notes = "新增数据时，尝试生效到实体表", httpMethod = "POST")
    @ApiImplicitParam(name = "oracleTable", value = "oracle table 实体类", dataType = "OracleTableVO", paramType = "body")
    @PostMapping("/add")
    public ResultBean add(@Validated(Save.class) @RequestBody OracleTableVO oracleTable, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        log.info("oracle 新增表数据：{}", oracleTable);
        oracleTableService.add(oracleTable);
        return ResultBean.ok();
    }

    @ApiOperation(value = "元数据定义-ORACLE表修改并生效", notes = "修改数据时，尝试生效到实体表", httpMethod = "PUT")
    @ApiImplicitParam(name = "oracleTable", value = "oracle table 实体类", dataType = "OracleTableVO", paramType = "body")
    @PutMapping("/update")
    public ResultBean update(@Validated(Update.class) @RequestBody OracleTableVO oracleTable, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        log.info("oracle 修改表数据：{}", oracleTable);
        oracleTableService.update(oracleTable);
        return ResultBean.ok();
    }

    @ApiOperation(value = "元数据定义-ORACLE存为草稿表", notes = "存为草稿，并不去生效到实体表", httpMethod = "POST")
    @ApiImplicitParam(name = "oracleTable", value = "oracle table 实体类", dataType = "OracleTableVO", paramType = "body")
    @PostMapping("/add/draft")
    public ResultBean addDraft(@Validated(Save.class) @RequestBody OracleTableVO oracleTable, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        log.info("保存表为草稿：{}", oracleTable);
        oracleTableService.addDraft(oracleTable);
        return ResultBean.ok();
    }

    @ApiOperation(value = "元数据定义-ORACLE草稿表修改", notes = "草稿修改并不生效，生效表存为草稿", httpMethod = "PUT")
    @ApiImplicitParam(name = "oracleTable", value = "oracle table 实体类", dataType = "OracleTableVO", paramType = "body")
    @PutMapping("/update/draft")
    public ResultBean updateDraft(@Validated(Update.class) @RequestBody OracleTableVO oracleTable, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        log.info("修改表草稿数据：{}", oracleTable);
        oracleTableService.updateDraft(oracleTable);
        return ResultBean.ok();
    }

    @ApiOperation(value = "元数据定义-ORACLE表删除", notes = "正常表或草稿表删除都采用此方法", httpMethod = "DELETE")
    @ApiImplicitParam(name = "idList", value = "表id集合，数组", dataType = "array", paramType = "body")
    @DeleteMapping("/delete")
    public ResultBean delete(@RequestBody List<Long> idList) {
        log.info("删除表或草稿表，idList：{}", idList);
        oracleTableService.delete(idList);
        return ResultBean.ok();
    }
}
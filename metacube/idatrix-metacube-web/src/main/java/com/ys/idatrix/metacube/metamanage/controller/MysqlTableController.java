package com.ys.idatrix.metacube.metamanage.controller;

import com.idatrix.unisecurity.sso.client.enums.ResultEnum;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.common.group.Update;
import com.ys.idatrix.metacube.metamanage.service.MysqlTableService;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.MySqlTableVO;
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
 * @ClassName MysqlTableController
 * @Description mysql表操作相关API
 * @Author ouyang
 * @Date
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/mysql/table")
@Api(value = "/MysqlTableController", tags = "元数据管理-元数据定义-MYSQL表处理接口")
public class MysqlTableController {

    @Autowired
    private MysqlTableService mysqlTableService;

    @ApiOperation(value = "元数据定义-MYSQL表搜索", notes = "传递参数schemaId,status", httpMethod = "GET")
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
        searchVO.setDatabaseType(DatabaseTypeEnum.MYSQL.getCode()); // 当前为mysql
        searchVO.setResourceType(1); // 1 为表
        searchVO.setPageNum(pageNum);
        searchVO.setPageSize(pageSize);
        searchVO.setStatus(status);
        searchVO.setSchemaId(schemaId);
        PageResultBean<TableVO> result = mysqlTableService.search(searchVO);
        return ResultBean.ok(result);
    }

    @ApiOperation(value = "元数据定义-MYSQL根据表ID查询数据", notes = "", httpMethod = "GET")
    @GetMapping("/search/{tableId}")
    public ResultBean<MySqlTableVO> searchById(@NotNull(message = "表ID不能为空") @PathVariable Long tableId) {
        MySqlTableVO result = mysqlTableService.searchById(tableId);
        return ResultBean.ok(result);
    }

    @ApiOperation(value = "元数据定义-MYSQL根据模式ID查询表集合", notes = "", httpMethod = "GET")
    @GetMapping("/search/table/{schemaId}")
    public ResultBean<List<TableVO>> searchBySchemaId(@NotNull(message = "模式ID不能为空") @PathVariable Long schemaId) {
        List<TableVO> list = mysqlTableService.searchBySchemaId(schemaId);
        return ResultBean.ok(list);
    }

    @ApiOperation(value = "元数据定义-MYSQL表保存并生效", notes = "新增数据时，尝试生效到实体表", httpMethod = "POST")
    @ApiImplicitParam(name = "mysqlTable", value = "mysql table 实体类", dataType = "MySqlTableVO", paramType = "body")
    @PostMapping("/add")
    public ResultBean add(@Validated(Save.class) @RequestBody MySqlTableVO mysqlTable, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        log.info("新增表数据：{}", mysqlTable);
        mysqlTableService.add(mysqlTable);
        return ResultBean.ok();
    }

    @ApiOperation(value = "元数据定义-MYSQL表修改并生效", notes = "修改数据时，尝试生效到实体表", httpMethod = "PUT")
    @ApiImplicitParam(name = "mysqlTable", value = "mysql table 实体类", dataType = "MySqlTableVO", paramType = "body")
    @PutMapping("/update")
    public ResultBean update(@Validated(Update.class) @RequestBody MySqlTableVO mysqlTable, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        log.info("修改表数据：{}", mysqlTable);
        mysqlTableService.update(mysqlTable);
        return ResultBean.ok();
    }

    @ApiOperation(value = "元数据定义-MYSQL存为草稿表", notes = "存为草稿，并不去生效到实体表", httpMethod = "POST")
    @ApiImplicitParam(name = "mysqlTable", value = "mysql table 实体类", dataType = "MySqlTableVO", paramType = "body")
    @PostMapping("/add/draft")
    public ResultBean addDraft(@Validated(Save.class) @RequestBody MySqlTableVO mysqlTable, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        log.info("保存草稿：{}", mysqlTable);
        mysqlTableService.addDraft(mysqlTable);
        return ResultBean.ok();
    }

    @ApiOperation(value = "元数据定义-MYSQL草稿表修改", notes = "草稿修改并不生效，生效表存为草稿", httpMethod = "PUT")
    @ApiImplicitParam(name = "mysqlTable", value = "mysql table 实体类", dataType = "MySqlTableVO", paramType = "body")
    @PutMapping("/update/draft")
    public ResultBean updateDraft(@Validated(Update.class) @RequestBody MySqlTableVO mysqlTable, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        log.info("修改表草稿数据：{}", mysqlTable);
        mysqlTableService.updateDraft(mysqlTable);
        return ResultBean.ok();
    }

    @ApiOperation(value = "元数据定义-MYSQL表删除", notes = "正常表或草稿表删除都采用此方法", httpMethod = "DELETE")
    @ApiImplicitParam(name = "idList", value = "表id集合，数组", dataType = "array", paramType = "body")
    @DeleteMapping("/delete")
    public ResultBean delete(@RequestBody List<Long> idList) {
        log.info("删除表或草稿表，idList：{}", idList);
        mysqlTableService.delete(idList);
        return ResultBean.ok();
    }

    /*@ApiOperation(value = "元数据定义-MYSQL表导入", notes = "一次只能导入一张表", httpMethod = "POST")
    @PostMapping("/import")
    public ResultBean importTable(@RequestParam("sourceFile") MultipartFile sourceFile) throws IOException {
        // 获取文件名加后缀
        String name = sourceFile.getOriginalFilename();
        // 进一步判断文件是否为空（即判断其大小是否为0或其名称是否为null）
        long size = sourceFile.getSize();
        // 判断文件是否为空
        if (null == sourceFile || size == 0 || StringUtils.isBlank(name)) {
            log.error("上传文件为空");
            return ResultBean.error("上传文件为空");
        }
        // 判断外键类型
        if (!name.endsWith(".xls") && !name.endsWith(".xlsx")) {
            log.error("文件不是excel类型");
            return ResultBean.error("文件不是excel类型");
        }
        // 将Excel内容封装到mysqlTableVo中
        MySqlTableVO table = MetadataImportUtils.readExcelToMySqlTableVO(name, sourceFile.getInputStream());
        mysqlTableService.add(table);
        return ResultBean.ok("导入成功");
    }

    @ApiOperation(value = "元数据定义-MYSQL表导出", notes = "", httpMethod = "POST")
    @PostMapping("/export")
    public ResultBean exportTable() {
        return null;
    }*/

}

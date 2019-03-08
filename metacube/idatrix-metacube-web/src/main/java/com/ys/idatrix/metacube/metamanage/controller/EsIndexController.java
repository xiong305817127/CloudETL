package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.dubbo.consumer.SecurityConsumer;
import com.ys.idatrix.metacube.metamanage.domain.EsMetadataPO;
import com.ys.idatrix.metacube.metamanage.service.EsIndexService;
import com.ys.idatrix.metacube.metamanage.service.SystemSettingsService;
import com.ys.idatrix.metacube.metamanage.vo.request.EsIndexVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: EsIndexController
 * @Description: ES 索引 rest api
 * @Author: ZhouJian
 * @Date: 2019/1/23
 */
@Validated
@Slf4j
@RestController
@RequestMapping("/es")
@Api(value = "/es", tags = "元数据管理-元数据定义-ES索引定义")
public class EsIndexController {

    @Autowired(required = false)
    private EsIndexService esIndexService;

    @Autowired
    private SecurityConsumer securityConsumer;

    @Autowired
    private SystemSettingsService systemSettingsService;


    @GetMapping(value = "search")
    @ApiOperation(value = "查询(生效、删除、草稿)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", value = "状态", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, dataType = "Integer")
    })
    public ResultBean<PageResultBean<EsMetadataPO>> search(@RequestParam("status") Integer status,
                                                           @RequestParam("page") Integer page,
                                                           @RequestParam("pageSize") Integer pageSize) {

        checkOperateAuth();

        boolean isCenterAdmin = systemSettingsService.isDataCentreAdmin();
        boolean isDeptAdmin = systemSettingsService.isDatabaseAdmin();

        String deptCode = securityConsumer.getAscriptionDeptByUserName(UserUtils.getUserName()).getDeptCode();
        MetadataSearchVo metadataSearch = new MetadataSearchVo();
        if (isCenterAdmin) {
            metadataSearch.setRenterId(UserUtils.getRenterId());
        }

        if (isDeptAdmin) {
            metadataSearch.setRegCode(deptCode);
        }

        metadataSearch.setStatus(status);
        metadataSearch.setPageNum(page);
        metadataSearch.setPageSize(pageSize);
        PageResultBean<EsMetadataPO> result = esIndexService.search(metadataSearch);

        //设置是否可以切换版本
        if (result.getTotal() > 0L && status.equals(1)) {
            result.getData().stream().forEach(metadata -> {

                List<Map<Long, Integer>> snapshotVersions = esIndexService.queryVersionsByMetaId(metadata.getId());
                if (CollectionUtils.isNotEmpty(snapshotVersions) && snapshotVersions.size() > 1) {
                    metadata.setCanSwitch(true);
                }

            });
        }

        return ResultBean.ok(result);
    }


    @GetMapping(value = "details/{id}")
    @ApiOperation(value = "根据标识ID查询详细")
    @ApiImplicitParam(name = "id", value = "索引标识id", required = true, dataType = "Long")
    public ResultBean<EsIndexVO> queryEsDetail(@PathVariable("id") Long id) {
        EsIndexVO esIndexVO = esIndexService.queryEsDetail(id);
        return ResultBean.ok(esIndexVO);
    }


    @GetMapping(value = "checkOperateAuth")
    @ApiOperation(value = "检查是否有权限")
    public ResultBean<Boolean> checkUserOperateAuth() {
        checkOperateAuth();
        return ResultBean.ok(true);
    }


    @GetMapping(value = "indexExists")
    @ApiOperation(value = "索引是否存在")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schemaId", value = "索引模式Id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "schemaName", value = "索引模式名称", required = true, dataType = "string"),
            @ApiImplicitParam(name = "isDrafted", value = "索引模式名称", required = true, dataType = "Boolean")
    })
    public ResultBean<Boolean> checkExistsIndex(@RequestParam("schemaId") Long schemaId,
                                                @RequestParam("schemaName") String schemaName,
                                                @RequestParam("isDrafted") Boolean isDrafted) {
        return ResultBean.ok(esIndexService.checkExistsIndex(schemaId, schemaName, isDrafted));
    }


    @PostMapping(value = "saveToDraft")
    @ApiOperation(value = "保存草稿")
    @ApiImplicitParam(name = "esIndexVO", value = "索引对象,json格式", required = true, dataType = "EsIndexVO")
    public ResultBean<Boolean> saveAndDraft(@Validated(Save.class) @RequestBody EsIndexVO esIndexVO) {
        esIndexVO.setRenterId(UserUtils.getRenterId());
        esIndexVO.setCreator(UserUtils.getUserName());
        return ResultBean.ok(esIndexService.saveOrCreatedIndex(esIndexVO, true));
    }


    @PostMapping(value = "saveToCreated")
    @ApiOperation(value = "保存并生效")
    @ApiImplicitParam(name = "esIndexVO", value = "索引对象,json格式", required = true, dataType = "EsIndexVO")
    public ResultBean<Boolean> saveAndCreated(@Validated(Save.class) @RequestBody EsIndexVO esIndexVO) {
        esIndexVO.setRenterId(UserUtils.getRenterId());
        esIndexVO.setCreator(UserUtils.getUserName());
        return ResultBean.ok(esIndexService.saveOrCreatedIndex(esIndexVO, false));
    }


    @PostMapping(value = "update")
    @ApiOperation(value = "修改")
    @ApiImplicitParam(name = "esIndexVO", value = "索引对象,json格式", required = true, dataType = "EsIndexVO")
    public ResultBean<Boolean> update(@Validated(Save.class) @RequestBody EsIndexVO esIndexVO) {
        esIndexVO.setRenterId(UserUtils.getRenterId());
        esIndexVO.setModifier(UserUtils.getUserName());
        return ResultBean.ok(esIndexService.updateIndex(esIndexVO));
    }


    @PostMapping(value = "delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParam(name = "esIndexVO", value = "索引对象,json格式", required = true, dataType = "EsIndexVO")
    public ResultBean<Boolean> delete(@RequestBody List<Long> ids) {
        checkOperateAuth();
        return ResultBean.ok(esIndexService.softDeleteIndex(ids));
    }


    @GetMapping(value = "openOrStart/{id}/{isOpen}")
    @ApiOperation(value = "启停")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "索引标识id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "isOpen", value = "是否开启", required = true, dataType = "Boolean")
    })
    public ResultBean<Boolean> openOrStart(@PathVariable("id") Long id, @PathVariable("isOpen") boolean isOpen) {
        return ResultBean.ok(esIndexService.openOrStartIndex(id, isOpen));
    }


    @GetMapping(value = "queryVersions/{id}")
    @ApiOperation(value = "查询历史版本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "索引标识id", required = true, dataType = "Long")
    })
    public ResultBean<List<Map<Long, Integer>>> queryVersions(@PathVariable("id") Long id) {
        return ResultBean.ok(esIndexService.queryVersionsByMetaId(id));
    }


    @GetMapping(value = "switch/{id}/{version}")
    @ApiOperation(value = "版本切换")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "索引标识id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "version", value = "版本名称", required = true, dataType = "Integer")
    })
    public ResultBean<Boolean> switchIndex(@PathVariable("id") Long id, @PathVariable("version") Integer version) {
        return ResultBean.ok(esIndexService.switchIndex(id, version));
    }


    /**
     * 检查用户操作ES的权限
     *
     * @return
     */
    private void checkOperateAuth() {
        boolean isCenterAdmin = systemSettingsService.isDataCentreAdmin();
        boolean isDeptAdmin = systemSettingsService.isDatabaseAdmin();
        if (!isCenterAdmin && !isDeptAdmin) {
            throw new MetaDataException("用户不是数据库管理员,没有权限");
        }
    }

}

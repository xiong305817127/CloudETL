package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.group.Save;
import com.ys.idatrix.metacube.common.group.Update;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.dubbo.consumer.SecurityConsumer;
import com.ys.idatrix.metacube.metamanage.domain.EsMetadataPO;
import com.ys.idatrix.metacube.metamanage.service.EsIndexService;
import com.ys.idatrix.metacube.sysmanage.service.SystemSettingsService;
import com.ys.idatrix.metacube.metamanage.vo.request.EsIndexVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.SchemaSearchVO;
import com.ys.idatrix.metacube.metamanage.vo.response.SchemaVO;
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
            @ApiImplicitParam(name = "dbId", value = "数据库Id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "status", value = "状态", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, dataType = "Integer")
    })
    public ResultBean<PageResultBean<EsMetadataPO>> search(@RequestParam("dbId") Long dbId,
                                                           @RequestParam("status") Integer status,
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

        metadataSearch.setCreator(UserUtils.getUserName());
        metadataSearch.setDbId(dbId);
        metadataSearch.setStatus(status);
        metadataSearch.setPageNum(page);
        metadataSearch.setPageSize(pageSize);
        PageResultBean<EsMetadataPO> result = esIndexService.search(metadataSearch);

        //设置是否可以切换版本
        if (result.getTotal() > 0L && status.equals(1)) {
            result.getData().stream().forEach(metadata -> {

                List<Map<Long, Integer>> snapshotVersions = esIndexService.querySwitchVersionsByMetaId(metadata.getId());
                if (CollectionUtils.isNotEmpty(snapshotVersions) && snapshotVersions.size() > 1) {
                    metadata.setCanSwitch(true);
                }

            });
        }

        return ResultBean.ok(result);
    }


    @GetMapping(value = "esSchema")
    @ApiOperation(value = "查询ES 模式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dbId", value = "数据库Id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "schemaId", value = "模式ID", dataType = "Long")
    })
    public ResultBean<List<SchemaVO>> queryEsSchema(@RequestParam("dbId") Long dbId,
                                                    @RequestParam(value = "schemaId", required = false) Long schemaId) {
        boolean isCenterAdmin = systemSettingsService.isDataCentreAdmin();
        boolean isDeptAdmin = systemSettingsService.isDatabaseAdmin();
        String deptCode = securityConsumer.getAscriptionDeptByUserName(UserUtils.getUserName()).getDeptCode();
        SchemaSearchVO searchVO = new SchemaSearchVO();
        if (isCenterAdmin) {
            searchVO.setRenterId(UserUtils.getRenterId());
        }

        if (isDeptAdmin) {
            searchVO.setOrgCode(deptCode);
        }
        searchVO.setDbId(dbId);
        searchVO.setCreator(UserUtils.getUserName());
        List<SchemaVO> schemaList = esIndexService.queryEsSchema(searchVO, schemaId);
        return ResultBean.ok(schemaList);
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


    @PostMapping(value = "new/draft")
    @ApiOperation(value = "新建保存草稿")
    @ApiImplicitParam(name = "esIndexVO", value = "索引对象,json格式", required = true, dataType = "EsIndexVO")
    public ResultBean<Boolean> newToDraft(@Validated(Save.class) @RequestBody EsIndexVO esIndexVO) {
        esIndexVO.setRenterId(UserUtils.getRenterId());
        esIndexVO.setCreator(UserUtils.getUserName());
        return ResultBean.ok(esIndexService.saveIndexDraftedOrCreated(esIndexVO, false, true));
    }


    @PostMapping(value = "new/created")
    @ApiOperation(value = "新建保存并生效")
    @ApiImplicitParam(name = "esIndexVO", value = "索引对象,json格式", required = true, dataType = "EsIndexVO")
    public ResultBean<Boolean> newToCreated(@Validated(Save.class) @RequestBody EsIndexVO esIndexVO) {
        esIndexVO.setRenterId(UserUtils.getRenterId());
        esIndexVO.setCreator(UserUtils.getUserName());
        return ResultBean.ok(esIndexService.saveIndexDraftedOrCreated(esIndexVO, false, false));
    }


    @PostMapping(value = "update/draft")
    @ApiOperation(value = "修改保存草稿")
    @ApiImplicitParam(name = "esIndexVO", value = "索引对象,json格式", required = true, dataType = "EsIndexVO")
    public ResultBean<Boolean> updateToDraft(@Validated(Update.class) @RequestBody EsIndexVO esIndexVO) {
        esIndexVO.setRenterId(UserUtils.getRenterId());
        esIndexVO.setModifier(UserUtils.getUserName());
        return ResultBean.ok(esIndexService.saveIndexDraftedOrCreated(esIndexVO, true, true));
    }


    @PostMapping(value = "update/created")
    @ApiOperation(value = "修改保存生效")
    @ApiImplicitParam(name = "esIndexVO", value = "索引对象,json格式", required = true, dataType = "EsIndexVO")
    public ResultBean<Boolean> updateToCreated(@Validated(Update.class) @RequestBody EsIndexVO esIndexVO) {
        esIndexVO.setRenterId(UserUtils.getRenterId());
        esIndexVO.setModifier(UserUtils.getUserName());
        return ResultBean.ok(esIndexService.saveIndexDraftedOrCreated(esIndexVO, true, false));
    }


    @PostMapping(value = "delete")
    @ApiOperation(value = "删除")
    @ApiImplicitParam(name = "ids", value = "索引", required = true, dataType = "List")
    public ResultBean<Boolean> delete(@RequestBody List<Long> ids) {
        checkOperateAuth();
        if(CollectionUtils.isEmpty(ids)){
            return ResultBean.error("待删除数据为空");
        }
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
    @ApiOperation(value = "查询可切换版本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "索引标识id", required = true, dataType = "Long")
    })
    public ResultBean<List<Map<Long, Integer>>> querySwitchVersions(@PathVariable("id") Long id) {
        return ResultBean.ok(esIndexService.querySwitchVersionsByMetaId(id));
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

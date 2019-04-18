package com.ys.idatrix.metacube.metamanage.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.service.OrganizationService;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.beans.dataswap.DataSource;
import com.ys.idatrix.metacube.api.beans.dataswap.FrontEndServer;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.dubbo.consumer.SecurityConsumer;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.*;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefHDFSService;
import com.ys.idatrix.metacube.metamanage.vo.request.HdfsFileDirectory;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.ServerSearchVO;
import com.ys.idatrix.metacube.metamanage.vo.response.DbFieldBean;
import com.ys.idatrix.metacube.metamanage.vo.response.DbTableFieldInfo;
import com.ys.idatrix.metacube.metamanage.vo.response.DbTableInfo;
import com.ys.idatrix.metacube.metamanage.vo.response.MetaDefOverviewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Classname MetadataToDataSwapController
 * @Description 元数据提供给数据共享交换接口
 * @Author robin
 * @Date 2019/3/14 10:56
 * @Version v1.0
 */

@Validated
@Slf4j
@RestController
@RequestMapping("/metadataToSwap")
@Api(value = "/metadataToSwap", tags = "元数据接口-数据共享交换")
public class MetadataToDataSwapController {

    @Autowired(required = false)
    private SecurityConsumer securityConsumer;

    @Autowired(required = false)
    private IMetaDefHDFSService metaDefHDFSService;

    @Autowired(required = false)
    private McServerMapper serverMapper;

    @Autowired(required = false)
    private McSchemaMapper schemaMapper;

    @Autowired(required =false)
    private McDatabaseMapper databaseMapper;

    @Autowired(required = false)
    private MetadataMapper metadataMapper;

    @Autowired(required = false)
    private TableColumnMapper tableColumnMapper;

    @Autowired(required = false)
    private OrganizationService organizationService;

    @ResponseBody
    @RequestMapping(value = "/findDeptByUserId", method = RequestMethod.GET)
    @ApiOperation(value = "根据用户id查询部门信息")
    public ResultBean<List<Organization>> findDeptByUserId(@RequestParam("userid") Long userid) {
        List<Organization> list = null;
        try {
            if (null == userid) {
                userid = UserUtils.getUserId();
            }
            list = securityConsumer.findOrganizationsByUserId(userid);
            log.info("根据用户id查詢部门信息有:{} 条", list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBean.ok(list);
    }

    /**
     * 查询跨租户的组织机构
     */
    @ResponseBody
    @RequestMapping(value = "/findOrganizations", method = RequestMethod.GET)
    @ApiOperation(value = "新增资源-查询所有组织信息")
    public ResultBean<List<Organization>> findOrganizations() {
        List<Organization> list = securityConsumer.findOrganizations();
        return ResultBean.ok(list);
    }


    /**
     * 查询HDFS目录信息
     */
    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ApiOperation(value = "系统配置-查询HDFS目录信息", notes = "本方法获取所有HDFS列表，请求参数使用renterId和dirSearchKey分布表示租户和路径搜索关键字")
    public ResultBean<List<HdfsFileDirectory>> search(
            @RequestBody HdfsFileDirectory hdfsFileDirectory) {

        if (null != hdfsFileDirectory) {
            if (StringUtils.isBlank(hdfsFileDirectory.getRenterId())) {
                hdfsFileDirectory.setRenterId(UserUtils.getRenterId().toString());
            }

            String searchKey = null;
            if (StringUtils.isNotEmpty(hdfsFileDirectory.getDirSearchKey())) {
                searchKey = hdfsFileDirectory.getDirSearchKey();
            }

            if (StringUtils.isEmpty(hdfsFileDirectory.getRenterId())) {
                return ResultBean.error("查询参数缺少rentID");
            }
            Long rentId = Long.valueOf(hdfsFileDirectory.getRenterId());
            List<HdfsFileDirectory> hdfsDirList = new ArrayList<>();
            List<Metadata> dirList = metaDefHDFSService.getAllDirByRentId(rentId, searchKey);
            if(CollectionUtils.isEmpty(dirList)){
                return ResultBean.ok(hdfsDirList);
            }
            dirList = dirList.stream().filter(p->!p.getStatus().equals(2)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dirList)) {
                dirList.stream().forEach(p -> {
                    HdfsFileDirectory dfs = new HdfsFileDirectory();
                    dfs.setAllPathname(p.getIdentification());
                    dfs.setId(p.getId().intValue());
                    hdfsDirList.add(dfs);
                });
            }
            return ResultBean.ok(hdfsDirList);
        }
        return ResultBean.error("请求参数异常");
    }


    /**
     * 根据组织（部门）编码 查询前置机服务列表
     */
    @ResponseBody
    @RequestMapping(value = "/getDeptServer/{organizationId}", method = RequestMethod.GET)
    @ApiOperation(value = "前置机管理-根据部门获取前置机服务器,organizationCode 没有请求部门组织参数")
    public ResultBean<List<FrontEndServer>> getServerByDept(
            @PathVariable("organizationId") Long organizationId) {

        if(organizationId==null || organizationId.equals(0L)){
            return ResultBean.error("请求组织ID参数不正确");
        }
        Organization org= organizationService.findById(organizationId);
        if(org==null){
            return ResultBean.error("查询不到该组织参数信息，组织ID为"+organizationId);
        }
        String organizationCode = org.getDeptCode();

        if (StringUtils.isNotEmpty(organizationCode)) {
            ServerSearchVO serverPO = new ServerSearchVO();
            serverPO.setUseList(Arrays.asList(1)); //前置机
            serverPO.setOrgList(Arrays.asList(organizationCode));
            List<McServerPO> serverList = serverMapper.list(serverPO);
            if(CollectionUtils.isEmpty(serverList)){
                return ResultBean.ok(null);
            }
            Collections.sort(serverList,
                        Comparator.comparing(server -> server.getName().toUpperCase()));

            serverList = serverList.stream().filter(p->p.getIsDeleted().equals(0)).collect(Collectors.toList());
            List<Long> serviceIds = serverList.stream().map(p->Long.valueOf(p.getId())).collect(Collectors.toList());
            List<McDatabasePO> dbList = databaseMapper.listDatabaseByServerIds(serviceIds);
            if(CollectionUtils.isNotEmpty(dbList)){
                List<McDatabasePO> dbRDBList = dbList.stream().filter(p->p.getType()<5).collect(Collectors.toList());
                if(CollectionUtils.isEmpty(dbRDBList)){
                    return ResultBean.ok(null);
                }
                List<Long> serviceRDBList = dbRDBList.stream().map(p->p.getServerId()).collect(Collectors.toList());
                serverList = serverList.stream().filter(p->serviceRDBList.contains(p.getId())).collect(Collectors.toList());
            }

            List<FrontEndServer> results = Lists.newArrayList();
            serverList.stream().forEach(p->{
                FrontEndServer frontServer = new FrontEndServer();
                frontServer.setId(p.getId().intValue());
                frontServer.setServerName(p.getName());
                frontServer.setServerIp(p.getIp());
                frontServer.setPositionInfo(p.getLocation());
                frontServer.setOrganization(p.getOrgCode());
                results.add(frontServer);
            });
            return ResultBean.ok(results);
        }
        return ResultBean.error("没有配置部门组织参数");
    }

    /**
     * 查询前置机数据源
     */
    @ResponseBody
    @RequestMapping(value = "/getFSDatabase/{serviceId}", method = RequestMethod.GET)
    @ApiOperation(value = "前置机管理-查询前置机数据源")
    public ResultBean<List<DataSource>> getFSDatabase(
            @PathVariable("serviceId") Long serviceId) {
        if (null != serviceId || !serviceId.equals(0L)) {

            McServerPO serverPO = serverMapper.getServerPOById(serviceId);
            if(serverPO==null){
                return ResultBean.error("服务器参数配置不正确");
            }

            List<McDatabasePO> dbList = databaseMapper.listDatabaseByServerIds(Arrays.asList(serviceId));
            if(CollectionUtils.isNotEmpty(dbList)){
                List<McDatabasePO> dbRDB = dbList.stream()
                        //关系型数据库才显示
                        .filter(p->p.getType().equals(1)||p.getType().equals(2)||p.getType().equals(3)||p.getType().equals(4))
                        .collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(dbRDB)){
                    List<Long> dbRDBIdList = dbRDB.stream().map(p->p.getId()).collect(Collectors.toList());
                    List<McSchemaPO> schemaList = schemaMapper.listSchemaByDatabaseIds(dbRDBIdList);
                    if (CollectionUtils.isNotEmpty(schemaList)) {
                        Collections.sort(schemaList,
                                Comparator.comparing(server -> server.getName().toUpperCase()));
                    }
                    schemaList=schemaList.stream().filter(p->p.getIsDeleted().equals(0)).collect(Collectors.toList());
                    List<DataSource> dataList = Lists.newArrayList();
                    schemaList.stream().forEach(p->{
                        DataSource dataSource = new DataSource();
                        dataSource.setDsId(p.getId().intValue());
                        dataSource.setDsName(p.getName());
                        dataSource.setDbDatabasename(p.getName());
                        dataSource.setDbUsername(p.getUsername());
                        dataSource.setDbPassword(p.getPassword());
                        McDatabasePO db = dbList.stream().filter(m->m.getId().equals(p.getDbId())).findAny().orElse(null);
                        dataSource.setDbPort(db.getPort().toString());
                        dataSource.setDbHostname(serverPO.getName());
                        dataSource.setDsType(DatabaseTypeEnum.getName(db.getType()).toLowerCase());
                        dataList.add(dataSource);
                    });
                    return ResultBean.ok(dataList);
                }
            }
            return ResultBean.ok(null);
        }
        return ResultBean.error("请求参数不正确");
    }


    /**
     * 查询租户内的所有（平台、前置机）的数据源列表
     */
    @ResponseBody
    @RequestMapping(value = "/getDatabasesByRentIdDsType", method = RequestMethod.GET)
    @ApiOperation(value = "查询外部数据源列表", notes = "dsType里面参数含义数据库类型 ,1.mysql,2.oracle,3.dm,4.postgreSQL,5.hive,6.base,7.hdfs,8.ElasticSearch")
    public ResultBean<List<Map<String, Object>>> getDatabasesByRentIdDsType(
            @RequestParam("rentId") Long rentId,
            @RequestParam("dsType") Integer dsType) {

        long startMS =  System.currentTimeMillis();
        log.error("getDatabasesByRentIdDsType 开始时间 {}", startMS);
        List<Map<String, Object>> resultMapList = Lists.newArrayList();
        List<McSchemaPO> schemaList = schemaMapper.listSchema(null, rentId, Arrays.asList(dsType)
                , null);
        if (CollectionUtils.isNotEmpty(schemaList)) {
            Collections.sort(schemaList,
                    Comparator.comparing(server -> server.getName().toUpperCase()));

            for (McSchemaPO source : schemaList) {
                if (source.getStatus().equals(1)) {  //禁用不显示
                    continue;
                }
                Map<String, Object> resultMap = Maps.newHashMap();
                resultMap.put("databaseName", source.getName());
                resultMap.put("name", source.getNameCn());
                resultMap.put("status", source.getStatus());
                resultMap.put("dsId", source.getId());
                resultMapList.add(resultMap);
            }
        }
        log.error("getDatabasesByRentIdDsType 结束时间 {}", System.currentTimeMillis()-startMS);
        return ResultBean.ok(resultMapList);
    }

    /**
     * 根据 schemaId 获取租户下的所有 tables 信息
     */
    @ResponseBody
    @RequestMapping(value = "/getRentTablesBySchemaId", method = RequestMethod.GET)
    @ApiOperation(value = "新增资源-根据模式查询租户下的所有表")
    public ResultBean<DbTableInfo> getRentTablesBySchemaId(@RequestParam("rentId") Long rentId,
            @RequestParam(value = "schemaId") Long schemaId) {

        long startMS =  System.currentTimeMillis();
        log.error("getRentTablesBySchemaId 开始时间 {}", startMS);
        DbTableInfo dbTableInfo = new DbTableInfo();
        McSchemaPO schemaPO = schemaMapper.getSchemaById(schemaId);
        if (schemaPO == null) {
            return ResultBean.error("存储系统中没有当前schemaId信息 " + schemaId);
        }

        MetadataSearchVo searchVO = new MetadataSearchVo();
        searchVO.setSchemaId(schemaId);
        searchVO.setStatus(1); //有效
        List<Metadata> metadataList = metadataMapper.search(searchVO);
        if (CollectionUtils.isNotEmpty(metadataList)) {
            metadataList = metadataList.stream().filter(p->!p.getStatus().equals(2)).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(metadataList)){
                return ResultBean.ok(null);
            }

            Collections.sort(metadataList,
                    Comparator.comparing(server -> server.getName().toUpperCase()));
            List<MetaDefOverviewVO> metaDefList = Lists.newArrayList();
            metadataList.stream().forEach(p -> {
                MetaDefOverviewVO def = new MetaDefOverviewVO();
                def.setId(p.getId());
                def.setName(p.getName());
                metaDefList.add(def);
            });

            //排序
            if (CollectionUtils.isNotEmpty(metaDefList)) {
                Collections.sort(metaDefList,
                        Comparator.comparing(table -> table.getName().toUpperCase()));
            }

            dbTableInfo.setId(schemaId.intValue());
            dbTableInfo.setName(schemaPO.getName());
            dbTableInfo.setDatabaseName(schemaPO.getName());
            dbTableInfo.setTableList(metaDefList);
        }
        log.error("getRentTablesBySchemaId 结束时间 {}", System.currentTimeMillis()-startMS);
        return ResultBean.ok(dbTableInfo);
    }

    /**
     * 查询外部数据Fields列表
     */
    @ResponseBody
    @RequestMapping(value = "/getFieldsByMetaId", method = RequestMethod.GET)
    @ApiOperation(value = "新增资源-查询表的字段列表")
    public ResultBean<DbTableFieldInfo> getFields(@RequestParam("metaId") Long metaId) {

        if (metaId == null || metaId.equals(0L)) {
            return ResultBean.error("请求参数异常");
        }
        Metadata metadata = metadataMapper.selectByPrimaryKey(metaId);
        if (metadata == null) {
            return ResultBean.error("存储系统中没有当前metaId信息 " + metaId);
        }

        DbTableFieldInfo dbTableFieldInfo = new DbTableFieldInfo();
        List<TableColumn> columnList = tableColumnMapper.findTableColumnListByTableId(metaId);
        if (CollectionUtils.isNotEmpty(columnList)) {

            Collections.sort(columnList,
                    Comparator.comparing(server -> server.getColumnName().toUpperCase()));
            List<DbFieldBean> dbFieldList = new ArrayList<DbFieldBean>();
            columnList.stream().forEach(p -> {
                DbFieldBean field = new DbFieldBean();
                field.setId(p.getId());
                field.setFieldName(p.getColumnName());
                field.setFieldLength(p.getTypeLength());
                field.setFieldPrecision(p.getTypePrecision());
                field.setFieldType(p.getColumnType());
                field.setDescription(p.getDescription());
                field.setIsNull(p.getIsNull());
                field.setIsPk(p.getIsPk());
                dbFieldList.add(field);
            });

            dbTableFieldInfo.setId(metaId);
            dbTableFieldInfo.setTableName(metadata.getName());
            dbTableFieldInfo.setFields(dbFieldList);
        }
        return ResultBean.ok(dbTableFieldInfo);
    }


}

package com.ys.idatrix.metacube.dubbo.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.internal.util.ImmutableList;
import com.idatrix.unisecurity.api.domain.Organization;
import com.ys.idatrix.metacube.api.beans.*;
import com.ys.idatrix.metacube.api.service.MetadataSchemaService;
import com.ys.idatrix.metacube.api.service.MetadataToDataAnalysisService;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.dubbo.consumer.SecurityConsumer;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.*;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.response.AuthMetadataVO;
import com.ys.idatrix.metacube.metamanage.vo.response.DatasourceVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Classname MetadataToDataAnalysisServiceImpl
 * @Description 提供给DataLab 元数据服务
 * @Author robin
 * @Date 2019/2/18 11:54
 * @Version v1.0
 */
@Slf4j
@Service
@Component
public class MetadataToDataAnalysisServiceImpl implements MetadataToDataAnalysisService {

    @Autowired(required = false)
    private McDatabaseMapper databaseMapper;

    @Autowired(required = false)
    private McSchemaMapper mcSchemaMapper;

    @Autowired(required = false)
    private MetadataMapper metadataMapper;

    @Autowired(required = false)
    private TableColumnMapper tableColumnMapper;

    @Autowired(required = false)
    private EsMetadataMapper esMetadataMapper;

    @Autowired(required = false)
    private EsFieldMapper esFieldMapper;

    @Autowired(required = false)
    private ApprovalProcessMapper approvalProcessMapper;

    @Autowired(required = false)
    private SecurityConsumer securityConsumer;

    @Autowired
    private MetadataSchemaService metadataSchemaService;


    @Override
    public ResultBean<List<MetaDbResourceDTO>> getDatabaseResource(String username) {
        try {
            //查询用户所属组织
            Organization organization = securityConsumer.getAscriptionDeptByUserName(username);
            String deptCode = organization.getDeptCode();

            List<Metadata> allMetadataList = Lists.newArrayList();

            //查询所属组织的表、视图
            List<Metadata> ownMetadataList = metadataMapper.searchByDeptAndDbTypes(ImmutableList.of(1, 2, 3, 4, 5, 6), deptCode);
            if (CollectionUtils.isNotEmpty(ownMetadataList)) {
                allMetadataList.addAll(ownMetadataList);
            }

            //查询所属组织授权的表、视图
            List<AuthMetadataVO> authMetadataList = approvalProcessMapper.getAuthMetadata(null, 2, deptCode,
                    ModuleTypeEnum.ANALYZE.getName(), ImmutableList.of(1, 2), ImmutableList.of(1, 2, 3, 4, 5, 6));
            if (CollectionUtils.isNotEmpty(authMetadataList)) {
                for (AuthMetadataVO authMetadata : authMetadataList) {
                    Long resourceId = authMetadata.getResourceId();
                    Metadata metadata = metadataMapper.findById(resourceId);
                    allMetadataList.add(metadata);
                }
            }

            //去重
            List<Metadata> distinctMetadataList = allMetadataList.stream().collect(
                    Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getId()))),
                            ArrayList::new));

            List<MetaDbResourceDTO> metaDbResourceList = Lists.newArrayList();
            //根据schema分组
            Map<Long, List<Metadata>> schemaMetadataMap = distinctMetadataList.stream().collect(Collectors.groupingBy(data -> data.getSchemaId()));
            for (Map.Entry<Long, List<Metadata>> entry : schemaMetadataMap.entrySet()) {

                MetaDbResourceDTO metaDbResourceDTO = new MetaDbResourceDTO();
                Long schemaId = entry.getKey();
                List<Metadata> metadataList = entry.getValue();
                Map<Long, String> metadataMap = metadataList.stream().collect(Collectors.toMap((key -> key.getId()), (value -> value.getName())));
                metaDbResourceDTO.setMetadataMap(metadataMap);
                ResultBean<SchemaDetails> result = metadataSchemaService.getSchemaById(username, schemaId);
                if (result.isSuccess()) {
                    SchemaDetails schemaDetails = result.getData();
                    BeanUtils.copyProperties(schemaDetails, metaDbResourceDTO);
                    if(StringUtils.isNotBlank(schemaDetails.getServiceName())){
                        metaDbResourceDTO.setSchemaName(schemaDetails.getServiceName());
                    }
                } else {
                    return ResultBean.error(result.getMsg());
                }
                metaDbResourceList.add(metaDbResourceDTO);
            }

            return ResultBean.ok(metaDbResourceList);
        } catch (Exception e) {
            log.error("getDatabaseResource 失败", e);
            return ResultBean.error(e.getMessage());
        }

    }


    @Override
    public ResultBean<HashMap<String, List<MetaFieldDTO>>> getTablesAndFields(String username, Long schemaId) {
        try {
            HashMap<String, List<MetaFieldDTO>> maps = Maps.newHashMap();

            //查询用户所属组织
            Organization organization = securityConsumer.getAscriptionDeptByUserName(username);
            String deptCode = organization.getDeptCode();

            //查询用户所有可操作的schemaId下的生效的表
            List<Metadata> metadataList = metadataMapper.getAllMetadataByUser(deptCode, schemaId, 1, null);
            if (CollectionUtils.isEmpty(metadataList)) {
                metadataList = Lists.newArrayList();
            }

            //添加赋权的表数据(所有数据，需要根据所属schemaId 过滤后添加)
            List<AuthMetadataVO> authMetadataList = approvalProcessMapper.getAuthMetadata(null, 2, deptCode,
                    ModuleTypeEnum.ANALYZE.getName(), ImmutableList.of(1, 2), ImmutableList.of(1, 2, 3, 4, 5, 6));
            if (CollectionUtils.isNotEmpty(authMetadataList)) {
                for (AuthMetadataVO authMetadataVO : authMetadataList) {
                    Long resourceId = authMetadataVO.getResourceId();
                    Metadata metadata = metadataMapper.findById(resourceId);
                    if (metadata.getSchemaId().equals(schemaId)) {
                        metadataList.add(metadata);
                    }
                }
            }

            //去重
            metadataList = metadataList.stream().collect(
                    Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getId()))), ArrayList::new));

            for (Metadata metadata : metadataList) {
                // 根据表id查询表字段
                List<TableColumn> columns = tableColumnMapper.findTableColumnListByTableId(metadata.getId());
                if (CollectionUtils.isNotEmpty(columns)) {
                    List<MetaFieldDTO> listFields = columns.stream().map(col -> {
                        MetaFieldDTO metaFieldDTO = new MetaFieldDTO();
                        metaFieldDTO.setColumnName(col.getColumnName());
                        metaFieldDTO.setDataType(col.getColumnType());
                        return metaFieldDTO;
                    }).collect(Collectors.toList());

                    maps.put(metadata.getName(), listFields);
                }
            }

            return ResultBean.ok(maps);

        } catch (Exception e) {
            log.error("getTablesAndFields 失败", e);
            return ResultBean.error(e.getMessage());
        }
    }


    @Override
    public ResultBean<List<MetaEsDTO>> getEsIndices(String username) {
        try {
            //用户所属组织是资源的所属组织
            String deptCode = securityConsumer.getAscriptionDeptByUserName(UserUtils.getUserName()).getDeptCode();
            MetadataSearchVo searchVo = new MetadataSearchVo();
            searchVo.setStatus(1);
            searchVo.setRegCode(deptCode);

            List<EsMetadataPO> metadataList = esMetadataMapper.search(searchVo);

            List<MetaEsDTO> normalList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(metadataList)) {
                normalList = metadataList.stream().filter(es -> es.getDisabled() == 0).map(es -> {
                    MetaEsDTO metaEsDTO = new MetaEsDTO();
                    metaEsDTO.setIndexName(es.getSchemaName());
                    metaEsDTO.setVersion(es.getVersion());
                    metaEsDTO.setId(es.getSchemaId());
                    metaEsDTO.setActionTypeEnum(ActionTypeEnum.ALL);
                    return metaEsDTO;
                }).collect(Collectors.toList());
            }

            //添加授权的部分数据
            List<MetaEsDTO> approvedList = Lists.newArrayList();
            List<AuthMetadataVO> authMetadataList = approvalProcessMapper.getAuthMetadata(null, 2, deptCode,
                    ModuleTypeEnum.ANALYZE.getName(), ImmutableList.of(1, 2), ImmutableList.of(8));
            if (CollectionUtils.isNotEmpty(authMetadataList)) {
                approvedList = authMetadataList.stream().map(vo -> {

                    EsMetadataPO es = esMetadataMapper.selectByPrimaryKey(vo.getResourceId());

                    MetaEsDTO metaEsDTO = new MetaEsDTO();
                    metaEsDTO.setIndexName(es.getSchemaName());
                    metaEsDTO.setVersion(es.getVersion());
                    metaEsDTO.setId(es.getSchemaId());
                    metaEsDTO.setActionTypeEnum(getAuthFromAuthMetadata(Arrays.asList(vo)));

                    return metaEsDTO;
                }).collect(Collectors.toList());
            }

            //去重
            List<MetaEsDTO> fullList = Stream.of(normalList, approvedList).flatMap(Collection::stream).collect(
                    Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getId()))),
                            ArrayList::new));
            return ResultBean.ok(fullList);
        } catch (Exception e) {
            log.error("getEsIndices 失败", e);
            return ResultBean.error(e.getMessage());
        }
    }


    @Override
    public ResultBean<List<MetaEsFieldDTO>> getEsFields(String username, Long metaId) {
        try {
            List<EsFieldPO> esFieldPOList = esFieldMapper.queryFieldsByIndexId(metaId);
            if (CollectionUtils.isNotEmpty(esFieldPOList)) {
                List<MetaEsFieldDTO> fieldDTOList = esFieldPOList.stream().map(field -> {
                    MetaEsFieldDTO fieldDTO = new MetaEsFieldDTO();
                    fieldDTO.setTypeName("default_type");
                    fieldDTO.setFieldName(field.getFieldName());
                    fieldDTO.setFieldType(field.getFieldType());
                    return fieldDTO;
                }).collect(Collectors.toList());
                return ResultBean.ok(fieldDTOList);
            }
            return ResultBean.ok();
        } catch (Exception e) {
            log.error("getEsFields 失败", e);
            return ResultBean.error(e.getMessage());
        }
    }


    /**
     * 根据用户获取hdfs路径信息
     *
     * @param username
     * @return
     */
    @Override
    public ResultBean<List<MetaHdfsDTO>> getHdfsPaths(String username) {
        try {
            //创建的资源
            List<MetaHdfsDTO> hdfsList = Lists.newArrayList();
            List<Metadata> dataList = Lists.newArrayList();
            //用户创建: 存在用户为其他人创建的情况，所以不用查询用户创建
//            List<Metadata> dataList = metadataMapper.getAllHDFSFolderInfo(username, null, null);
//            if (CollectionUtils.isEmpty(dataList)) {
//                return ResultBean.ok(hdfsList);
//            }

            //所属组织
            Organization org = securityConsumer.getAscriptionDeptByUserName(username);
            if (org != null) {
                String orgCode = org.getDeptCode();
                if (StringUtils.isNotEmpty(orgCode)) {
                    //所属组织查询
                    List<Metadata> orgHdfsList = metadataMapper.getAllHDFSFolderInfo(null, orgCode, null);
                    if (CollectionUtils.isNotEmpty(orgHdfsList)) {
                        dataList.addAll(orgHdfsList);
                    }

                    //查询赋权的表
                    List<AuthMetadataVO> authMetadataVOList = approvalProcessMapper.getAuthMetadata(null, 2, orgCode, ModuleTypeEnum.ANALYZE.getName(), null, ImmutableList.of(7));
                    if (CollectionUtils.isNotEmpty(authMetadataVOList)) {
                        for (AuthMetadataVO authMetadataVO : authMetadataVOList) {
                            Long resourceId = authMetadataVO.getResourceId();
                            Metadata metadata = metadataMapper.findById(resourceId);
                            McSchemaPO schemaPO = mcSchemaMapper.findById(metadata.getSchemaId());
                            metadata.setIdentification(schemaPO.getName() + metadata.getIdentification());
                            dataList.add(metadata);
                        }
                    }
                }
            }

            for (Metadata data : dataList) {
                MetaHdfsDTO hdfsDTO = new MetaHdfsDTO();
                hdfsDTO.setCreatetime(data.getCreateTime());
                hdfsDTO.setId(data.getId().intValue());
                hdfsDTO.setParentId(data.getSchemaId().intValue());
                hdfsDTO.setPath(data.getIdentification());
                hdfsDTO.setDesc(data.getName());
                hdfsDTO.setOrganization(data.getDeptCodes());

                hdfsList.add(hdfsDTO);
            }
            return ResultBean.ok(hdfsList);
        } catch (Exception e) {
            log.error("getHdfsPaths 失败", e);
            return ResultBean.error(e.getMessage());
        }

    }


    @Override
    public ResultBean<MetaDatabaseDTO> getDatabaseInfo(String username, Long schemaId) {
        try {
            McSchemaPO mcSchemaPO = mcSchemaMapper.findById(schemaId);
            DatasourceVO datasource = databaseMapper.getDatasourceInfoById(mcSchemaPO.getDbId());
            String dbType = datasource.getType();
            MetaDatabaseDTO databaseDTO = new MetaDatabaseDTO();
            databaseDTO.setType(DatabaseTypeEnum.getName(Integer.valueOf(dbType)));
            databaseDTO.setIp(datasource.getIp());
            databaseDTO.setPort(datasource.getPort());
            databaseDTO.setUsername(mcSchemaPO.getUsername());
            databaseDTO.setPassword(mcSchemaPO.getPassword());
            if(StringUtils.isNotBlank(mcSchemaPO.getServiceName())){
                databaseDTO.setDbName(mcSchemaPO.getServiceName());
            }else{
                databaseDTO.setDbName(mcSchemaPO.getServiceName());
            }
            return ResultBean.ok(databaseDTO);
        } catch (Exception e) {
            log.error("getDatabaseInfo 失败", e);
            return ResultBean.error(e.getMessage());
        }
    }


    @Override
    public ResultBean<ActionTypeEnum> getEsPermiss(String username, Long metaId) {
        try {
            Preconditions.checkNotNull(username, "用户信息为空");
            Preconditions.checkNotNull(metaId, "索引元数据标识为空");

            //查询用户所属组织
            Organization organization = securityConsumer.getAscriptionDeptByUserName(username);
            String deptCode = organization.getDeptCode();


            EsMetadataPO esMetadataPO = esMetadataMapper.selectByPrimaryKey(metaId);
            if (null == esMetadataPO) {
                return ResultBean.ok(ActionTypeEnum.NONE);
            }

            //根据schemaId，查询schema
            McSchemaPO mcSchemaPO = mcSchemaMapper.findById(esMetadataPO.getSchemaId());
            String regCode = mcSchemaPO.getOrgCode();
            List<String> regCodes = Arrays.asList(regCode.split(","));
            if (regCodes.contains(deptCode)) {
                return ResultBean.ok(ActionTypeEnum.ALL);
            }

            //查询权限表,获取权限
            List<AuthMetadataVO> authMetadataVOList = approvalProcessMapper.getAuthMetadata(metaId, 2, deptCode, ModuleTypeEnum.ANALYZE.getName(), null, null);
            return ResultBean.ok(getAuthFromAuthMetadata(authMetadataVOList));
        } catch (Exception e) {
            log.error("getEsPermiss 失败", e);
            return ResultBean.error(e.getMessage());
        }
    }


    @Override
    public ResultBean<ActionTypeEnum> getTbPermiss(String username, Long schemaId, String tableName) {
        try {
            //查询用户所属组织
            Organization organization = securityConsumer.getAscriptionDeptByUserName(username);
            String deptCode = organization.getDeptCode();

            //根据schemaId，查询schema
            McSchemaPO mcSchemaPO = mcSchemaMapper.findById(schemaId);
            String regCode = mcSchemaPO.getOrgCode();
            List<String> regCodes = Arrays.asList(regCode.split(","));
            if (regCodes.contains(deptCode)) {
                return ResultBean.ok(ActionTypeEnum.ALL);
            }

            //根据schemaId 和 表名 查询 表信息
            List<Metadata> metadataList = metadataMapper.queryMetaData(schemaId, tableName, null);
            if (CollectionUtils.isEmpty(metadataList)) {
                return ResultBean.error("当前模式下不存在:" + tableName);
            }

            Metadata metadata = metadataList.get(0);
            Long metaId = metadata.getId();
            //查询权限表,获取权限
            List<AuthMetadataVO> authMetadataVOList = approvalProcessMapper.getAuthMetadata(metaId, 2, deptCode, ModuleTypeEnum.ANALYZE.getName(), null, null);
            return ResultBean.ok(getAuthFromAuthMetadata(authMetadataVOList));
        } catch (Exception e) {
            log.error("getTbPermiss 失败", e);
            return ResultBean.error(e.getMessage());
        }
    }


    @Override
    public ResultBean<ActionTypeEnum> getHdfsPermiss(String username, String hdfsPath) {

        //先根据路径查询正向最大匹配的元数据值，在根据元数据id查询权限
        //TODO:现在是全字匹配

        String pathPrefix = getPathPrefix(hdfsPath);
        if (StringUtils.isEmpty(pathPrefix)) {
            return ResultBean.error("HDFS地址参数不准确，应该修改成:/path1/path2");
        }

        List<Metadata> dataList = Lists.newArrayList();
        //用户创建: 存在用户为其他人创建的情况，所以不用查询用户创建
//        List<Metadata> dataList =metadataMapper.getAllHDFSFolderInfo(username, null, pathPrefix);
//        if (CollectionUtils.isEmpty(dataList)) {
//            return ResultBean.error("HDFS列表为空");
//        }

        //所属组织
        long matchLen = 0;
        Organization org = securityConsumer.getAscriptionDeptByUserName(username);
        if (org != null) {
            String orgCode = org.getDeptCode();
            if (StringUtils.isNotEmpty(orgCode)) {
                //所属组织查询
                List<Metadata> orgHdfsList = metadataMapper.getAllHDFSFolderInfo(null, orgCode, pathPrefix);
                if (CollectionUtils.isNotEmpty(orgHdfsList)) {
                    matchLen = getMaxMatchLen(orgHdfsList, hdfsPath);
                }


                //查询赋权的表
                ActionTypeEnum authActionType = ActionTypeEnum.NONE;
                long maxLen = 0;
                String prefectMatch = null;
                AuthMetadataVO targetAuthMetaData = null;

                List<AuthMetadataVO> authMetadataVOList = approvalProcessMapper.getAuthMetadata(null, 2, orgCode, ModuleTypeEnum.ANALYZE.getName(), null, ImmutableList.of(7));
                if (CollectionUtils.isNotEmpty(authMetadataVOList)) {
                    for (AuthMetadataVO authMetadataVO : authMetadataVOList) {
                        Long resourceId = authMetadataVO.getResourceId();
                        Metadata metadata = metadataMapper.findById(resourceId);
                        McSchemaPO schemaPO = mcSchemaMapper.findById(metadata.getSchemaId());
                        String authPath = schemaPO.getName() + metadata.getIdentification();
                        if (StringUtils.isNotEmpty(authPath) && authPath.startsWith(hdfsPath)) {
                            if (StringUtils.isEmpty(prefectMatch) || authPath.length() > prefectMatch.length()) {
                                prefectMatch = authPath;
                                maxLen = authPath.length();
                                targetAuthMetaData = authMetadataVO;
                            }
                        }
                    }
                    if (targetAuthMetaData != null) {
                        authActionType = getAuthFromAuthMetadata(Arrays.asList(targetAuthMetaData));
                    }
                }
                if (maxLen > matchLen) {
                    return ResultBean.ok(authActionType);
                }
            }
        }
        if (matchLen > 0) {
            return ResultBean.ok(ActionTypeEnum.ALL);
        }
        return ResultBean.ok(ActionTypeEnum.NONE);
    }

    private long getMaxMatchLen(List<Metadata> dataList, String value) {
        long length = 0;
        Metadata maxMatch = null;
        for (Metadata data : dataList) {
            if (value.startsWith(data.getIdentification())) {
                if (maxMatch == null || data.getIdentification().length() > maxMatch.getIdentification().length()) {
                    maxMatch = data;
                }
            }
        }
        return maxMatch.getIdentification().length();
    }


    private String getPathPrefix(String infoPath) {
//        String infoPath = "中国/山东省";
        if (!infoPath.startsWith("/")) {
            log.info("HDFS地址参数不准确，应该修改成:/path1/path2");
            return null;
            //return ResultBean.error("HDFS地址参数不准确，应该修改成:/path1/path2");
        }

        String[] pathNameList = infoPath.split("\\/");
        String prefix = "/";
        if (ArrayUtils.isNotEmpty(pathNameList)) {
            List<String> path = Arrays.asList(pathNameList).stream().filter(p -> (!p.isEmpty())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(path)) {
                prefix += path.get(0);
            }
        }
        log.info("路径前缀 {}", prefix);
        return prefix;
    }


    /**
     * 根据AuthMetadata获取业务操作权限值
     *
     * @param authMetadataVOList
     * @return
     */
    private ActionTypeEnum getAuthFromAuthMetadata(List<AuthMetadataVO> authMetadataVOList) {
        if (CollectionUtils.isNotEmpty(authMetadataVOList)) {
            AuthMetadataVO vo = authMetadataVOList.get(0);
            if (vo.getAuthTypes().contains(ActionTypeEnum.READ.getCode() + "") && vo.getAuthTypes().contains(ActionTypeEnum.WRITE.getCode() + "")) {
                return ActionTypeEnum.ALL;
            } else if (vo.getAuthTypes().contains(ActionTypeEnum.READ.getCode() + "")) {
                return ActionTypeEnum.READ;
            } else if (vo.getAuthTypes().contains(ActionTypeEnum.WRITE.getCode() + "")) {
                return ActionTypeEnum.WRITE;
            } else {
                return ActionTypeEnum.NONE;
            }
        }
        return ActionTypeEnum.NONE;
    }


}

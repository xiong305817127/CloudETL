package com.ys.idatrix.metacube.dubbo.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.ImmutableList;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.service.OrganizationService;
import com.ys.idatrix.metacube.api.beans.*;
import com.ys.idatrix.metacube.api.service.MetadataServiceProvide;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.dubbo.consumer.SecurityConsumer;
import com.ys.idatrix.metacube.metamanage.domain.McDatabasePO;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import com.ys.idatrix.metacube.metamanage.mapper.ApprovalProcessMapper;
import com.ys.idatrix.metacube.metamanage.mapper.McDatabaseMapper;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.service.AuthorityService;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import com.ys.idatrix.metacube.metamanage.service.ResourceAuthService;
import com.ys.idatrix.metacube.metamanage.service.TableColumnService;
import com.ys.idatrix.metacube.metamanage.vo.request.ApprovalProcessVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.response.AuthMetadataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 元数据模式服务提供者实现
 *
 * @author robin
 */
@Slf4j
@Service
@Component
public class MetadataServiceProvideImpl implements MetadataServiceProvide {

    @Autowired
    private McDatabaseMapper databaseMapper;

    @Autowired
    @Qualifier("mySqlSchemaService")
    private McSchemaService schemaService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired(required = false)
    private ResourceAuthService resourceAuthService;

    @Autowired(required = false)
    private OrganizationService organizationService;

    @Autowired(required = false)
    private McSchemaMapper schemaMapper;

    @Autowired(required = false)
    private MetadataMapper metadataMapper;

    @Autowired(required = false)
    private TableColumnService columnService;

    @Autowired(required = false)
    private SecurityConsumer securityConsumer;

    @Autowired(required = false)
    private ApprovalProcessMapper approvalProcessMapper;

    @Autowired(required = false)
    private McSchemaMapper mcSchemaMapper;

    @Override
    public ResultBean<List<MetadataDTO>> findTableListBySchemaId(Long schemaId) {
        try {
            int resourceType = 1; // 当前查表
            return ResultBean.ok(findListBySchemaIdAndResourceType(schemaId, resourceType));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultBean.error(e.getMessage());
        }
    }

    @Override
    public ResultBean<MetadataDTO> findTableId(Long renterId, String ip, int databaseType, String serviceName, String schemaName, String tableName) {
        try {
            int resourceType = 1; // 当前查表
            return ResultBean.ok(getMetadataByDatabaseInfo(ip, databaseType, serviceName, schemaName, resourceType, tableName, renterId));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultBean.error(e.getMessage());
        }
    }

    @Override
    public ResultBean<List<MetaFieldDTO>> findColumnListByTable(Long tableId) {
        try {
            return ResultBean.ok(getColumnListByMetadataId(tableId));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultBean.error(e.getMessage());
        }
    }

    @Override
    public ResultBean<MetadataDTO> findViewId(Long renterId, String ip, int databaseType, String serviceName, String schemaName, String tableName) {
        try {
            int resourceType = 2; // 当前查视图
            return ResultBean.ok(getMetadataByDatabaseInfo(ip, databaseType, serviceName, schemaName, resourceType, tableName, renterId));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultBean.error(e.getMessage());
        }
    }

    @Override
    public ResultBean<List<MetadataDTO>> findViewListBySchemaId(Long schemaId) {
        try {
            int resourceType = 2; // 当前查视图
            return ResultBean.ok(findListBySchemaIdAndResourceType(schemaId, resourceType));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultBean.error(e.getMessage());
        }
    }

    @Override
    public ResultBean<TableViewDTO> findTableOrViewBySchemaId(Long schemaId, String username, ModuleTypeEnum module, ActionTypeEnum actionType) {
        try {
            Set<MetadataDTO> tableResult = new HashSet<>();
            Set<MetadataDTO> viewResult = new HashSet<>();

            // 获取数据库的信息，判断当前是什么数据库
            McSchemaPO schema = schemaService.findById(schemaId);
            if (schema == null) {
                throw new MetaDataException("错误的模式信息");
            }
            McDatabasePO database = databaseMapper.getDatabaseById(schema.getDbId());
            if (database == null) {
                throw new MetaDataException("错误的数据库信息");
            }

            // 用户所属组织下元数据信息
            Organization ascriptionDept = securityConsumer.getAscriptionDeptByUserName(username);
            MetadataSearchVo vo = new MetadataSearchVo();
            vo.setRegCode(ascriptionDept.getDeptCode()); // 所属组织代码
            vo.setSchemaId(schemaId); // 模式ID
            vo.setDatabaseType(database.getType()); // 数据库类型
            vo.setResourceType(1); // 资源类型
            List<Metadata> ascriptionDeptTableList = metadataMapper.searchList(vo);
            metadataPropertyCopy(ascriptionDeptTableList, tableResult);

            vo.setResourceType(2); // 视图
            List<Metadata> ascriptionDeptViewList = metadataMapper.searchList(vo);
            metadataPropertyCopy(ascriptionDeptViewList, viewResult);

            // 用户授权数据
            // 授权的表
            List<ApprovalProcessVO> tableResourceList =
                    authorityService.getAuthorizedResource(username, module, actionType, ImmutableList.of(database.getType()), ImmutableList.of(1));
            addTableOrView(tableResourceList, tableResult, schemaId);

            // 授权的视图
            List<ApprovalProcessVO> viewResourceList =
                    authorityService.getAuthorizedResource(username, module, actionType, ImmutableList.of(database.getType()), ImmutableList.of(2));
            addTableOrView(viewResourceList, viewResult, schemaId);

            TableViewDTO dto = new TableViewDTO();
            dto.setTableList(new ArrayList<>(tableResult));
            dto.setViewList(new ArrayList<>(viewResult));
            return ResultBean.ok(dto);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultBean.error(e.getMessage());
        }
    }

    public void addTableOrView(List<ApprovalProcessVO> resourceList, Set<MetadataDTO> target, Long schemaId) {
        if (CollectionUtils.isEmpty(resourceList)) {
            return;
        }
        List<Long> resourceId = new ArrayList<>();
        for (ApprovalProcessVO vo : resourceList) {
            if (!vo.getSchemaId().equals(schemaId)) {
                continue;
            }
            resourceId.add(vo.getResourceId());
        }
        List<Metadata> list = metadataMapper.findByIdList(resourceId);
        metadataPropertyCopy(list, target);
    }

    @Override
    public ResultBean<List<MetaFieldDTO>> findColumnListByTableIdOrViewId(Long metaId) {
        try {
            return ResultBean.ok(getColumnListByMetadataId(metaId));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultBean.error(e.getMessage());
        }
    }

    @Override
    public ResultBean<MetadataDTO> findHDFSId(String folderPath)  {
        List<Metadata> dataList = metadataMapper.getAllHDFSFolderInfo(null, null, null);
        if (CollectionUtils.isEmpty(dataList)) {
            return ResultBean.ok("HDFS当前数据为空");
        }
        MetadataDTO dataDTO = null;
        for (Metadata data : dataList) {
            if (StringUtils.equals(folderPath, data.getIdentification())) {
                dataDTO = new MetadataDTO();
                dataDTO.setMetaId(data.getId().intValue());
                dataDTO.setSchemaId(data.getSchemaId().intValue());
                dataDTO.setMetaName(data.getName());
                return ResultBean.ok(dataDTO);
            }
        }
        return ResultBean.ok(dataDTO);
    }

    /**
     * ETL 调用根据用户，返回当前用户有权限的根目录列表
     *
     * @param username 需要查询的用户名
     * @param mod      需要查询的权限 FileSystem.ACCESS_READ|FileSystem.ACCESS_WRITE
     * @return
     */
    @Override
    public ResultBean<List<String>> findHDFSFolderByUser(String username, ActionTypeEnum mod) {

        //用户创建
        List<Metadata> dataList = metadataMapper.getAllHDFSFolderInfo(username, null, null);
        if (CollectionUtils.isEmpty(dataList)) {
            return ResultBean.error("HDFS列表为空");
        }
        //所属组织
        Organization org = securityConsumer.getAscriptionDeptByUserName(username);
        if(org!=null){
            String orgCode = org.getDeptCode();
            if(StringUtils.isNotEmpty(orgCode)){
                //所属组织查询
                List<Metadata> orgHdfsList = metadataMapper.getAllHDFSFolderInfo(null, orgCode,  null);
                if(CollectionUtils.isNotEmpty(orgHdfsList)){
                    dataList.addAll(orgHdfsList);
                }

                //查询赋权的表
                List<AuthMetadataVO> authMetadataVOList = approvalProcessMapper.getAuthMetadata(null,
                        2, orgCode, ModuleTypeEnum.ETL.getName(), Arrays.asList(mod.getCode()), ImmutableList.of(7));
                if (CollectionUtils.isNotEmpty(authMetadataVOList)) {
                    for (AuthMetadataVO authMetadataVO : authMetadataVOList) {
                        Long resourceId = authMetadataVO.getResourceId();
                        Metadata metadata = metadataMapper.findById(resourceId);
                        McSchemaPO schemaPO = mcSchemaMapper.findById(metadata.getSchemaId());
                        metadata.setIdentification(schemaPO.getName()+metadata.getIdentification());
                        dataList.add(metadata);
                    }
                }
            }
        }

        List<String> folderList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(dataList)){
            for(Metadata data:dataList){
                if(CollectionUtils.isEmpty(folderList) || !folderList.contains(data.getIdentification())){
                    folderList.add(data.getIdentification());
                }
            }
        }
        return ResultBean.ok(folderList);
    }

    public List<MetaFieldDTO> getColumnListByMetadataId(Long metadataId) {
        List<MetaFieldDTO> result = new ArrayList<>();
        List<TableColumn> columnList = columnService.getTableColumnListByTableId(metadataId);
        for (TableColumn column : columnList) {
            MetaFieldDTO field = new MetaFieldDTO();
            field.setColumnName(column.getColumnName());
            field.setDataType(column.getColumnType());
            field.setTypePrecision(column.getTypePrecision());
            field.setIsPk(column.getIsPk());
            field.setLocation(column.getLocation());
            result.add(field);
        }
        return result;
    }

    public void metadataPropertyCopy(List<Metadata> source, Set<MetadataDTO> target) {
        if (CollectionUtils.isEmpty(source)) {
            return;
        }
        for (Metadata metadata : source) {
            MetadataDTO metadataDTO = new MetadataDTO();
            metadataDTO.setMetaId(metadata.getId().intValue());
            metadataDTO.setSchemaId(metadata.getSchemaId().intValue());
            metadataDTO.setMetaName(metadata.getName());
            target.add(metadataDTO);
        }
    }

    public MetadataDTO getMetadataByDatabaseInfo(String ip, int databaseType, String serviceName, String schemaName, int resourceType, String tableName, Long renterId) {
        return metadataMapper.findByDatabaseInfo(ip, databaseType, serviceName, schemaName, tableName, resourceType, renterId);
    }

    private List<MetadataDTO> findListBySchemaIdAndResourceType(Long schemaId, int resourceType) {
        return metadataMapper.findListBySchemaIdAndResourceType(schemaId, resourceType);
    }
}

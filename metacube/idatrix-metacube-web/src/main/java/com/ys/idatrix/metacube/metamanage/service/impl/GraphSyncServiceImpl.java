package com.ys.idatrix.metacube.metamanage.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.ys.idatrix.graph.service.api.DeleteResult;
import com.ys.idatrix.graph.service.api.NodeService;
import com.ys.idatrix.graph.service.api.def.DatabaseType;
import com.ys.idatrix.graph.service.api.dto.node.TableNodeDto;
import com.ys.idatrix.graph.service.api.dto.node.ViewNodeDto;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.McDatabasePO;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.service.GraphSyncService;
import com.ys.idatrix.metacube.metamanage.service.McDatabaseService;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import com.ys.idatrix.metacube.metamanage.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName GraphSyncService
 * @Description
 * @Author ouyang
 * @Date
 */
@Transactional
@Service
public class GraphSyncServiceImpl implements GraphSyncService {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private McDatabaseService databaseService;

    @Autowired
    @Qualifier("mySqlSchemaService")
    private McSchemaService schemaService;

    @Autowired
    private MetadataService metadataService;

    @Override
    public void graphSaveTableNode(Long id) {
        // 表信息信息
        Metadata table = metadataService.findById(id);
        // 模式信息
        McSchemaPO schema = schemaService.findById(table.getSchemaId());
        // 数据库信息
        McDatabasePO database = databaseService.getDatabaseById(schema.getDbId());
        // 封装信息
        TableNodeDto dto = new TableNodeDto();
        dto.setRenterId(UserUtils.getRenterId());
        dto.setDatabaseId(database.getId());
        dto.setServerId(database.getServerId());
        dto.setSchemaId(schema.getId());
        dto.setSchemaName(schema.getName());
        dto.setTableId(table.getId());
        dto.setTableName(table.getName());
        if(table.getDatabaseType() == DatabaseTypeEnum.MYSQL.getCode()) {
            dto.setDatabaseType(DatabaseType.MySQL);
        } else if(table.getDatabaseType() == DatabaseTypeEnum.ORACLE.getCode()) {
            dto.setDatabaseType(DatabaseType.Oracle);
        }
        // 创建表节点
        nodeService.createTableNode(dto);
    }

    @Override
    public void graphDeleteTableNode(Long id) {
        // 1为表
        deleteById(id, 1);
    }

    @Override
    public void graphSaveViewNode(Long id) {
        // 视图信息
        Metadata view = metadataService.findById(id);
        // 模式信息
        McSchemaPO schema = schemaService.findById(view.getSchemaId());
        // 数据库信息
        McDatabasePO database = databaseService.getDatabaseById(schema.getDbId());
        // 封装信息
        ViewNodeDto dto = new ViewNodeDto();
        dto.setRenterId(UserUtils.getRenterId());
        dto.setDatabaseId(database.getId());
        dto.setServerId(database.getServerId());
        dto.setSchemaId(schema.getId());
        dto.setSchemaName(schema.getName());
        dto.setViewId(view.getId());
        dto.setViewName(view.getName());
        if(view.getDatabaseType() == DatabaseTypeEnum.MYSQL.getCode()) {
            dto.setDatabaseType(DatabaseType.MySQL);
        } else if(view.getDatabaseType() == DatabaseTypeEnum.ORACLE.getCode()) {
            dto.setDatabaseType(DatabaseType.Oracle);
        }
        // 创建视图节点
        nodeService.createViewNode(dto);
    }

    @Override
    public void graphDeleteViewNode(Long id) {
        // 2为视图
        deleteById(id, 2);
    }

    private void deleteById(Long id, Integer type) {
        try {
            DeleteResult deleteResult = null;
            if(type == 1) {
                deleteResult = nodeService.deleteTableNode(id);
            } else if (type == 2) {
                deleteResult = nodeService.deleteVieweNode(id);
            }
            if(!deleteResult.isSuccess()) {
                List<String> msgList = new ArrayList<>();
                if(CollectionUtils.isNotEmpty(deleteResult.getRelatedEtlTransList())) {
                    msgList.add("与etl的关联个数：" + deleteResult.getRelatedEtlTransList().size());
                }
                if(CollectionUtils.isNotEmpty(deleteResult.getRelatedOlapCubeList())) {
                    msgList.add("与olap的关联个数：" + deleteResult.getRelatedOlapCubeList().size());
                }
                throw new MetaDataException("删除数据同步到数据地图失败，可能当前数据已有关联数据。"  + StringUtils.join(msgList, ","));
            }
        } catch (Exception e) {
            throw new MetaDataException("删除数据同步到数据地图失败，信息：", e.getMessage());
        }
    }

}
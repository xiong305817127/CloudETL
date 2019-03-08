package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.graph.service.api.DeleteResult;
import com.ys.idatrix.graph.service.api.NodeService;
import com.ys.idatrix.graph.service.api.def.DatabaseType;
import com.ys.idatrix.graph.service.api.dto.node.FolderNodeDto;
import com.ys.idatrix.graph.service.api.dto.node.TableNodeDto;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.metamanage.domain.McDatabasePO;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.mapper.McDatabaseMapper;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.service.IMetaDefBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ys.idatrix.graph.service.api.def.DatabaseType.HDFS;

/**
 * 元数据定义提供接口
 * @author robin
 *
 */
@Transactional
@Slf4j
@Service("metaDefBaseService")
public class MetaDefBaseServiceImpl implements IMetaDefBaseService {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private McSchemaMapper schemaMapper;

    @Autowired
    private McDatabaseMapper databaseMapper;


    /**
     * 判断模式使用次数
     *
     * @param schemaId     模式ID
     * @param databaseType 数据库类型
     * @return  大于0 表示模式使用次数（删除的不统计），0表示没有使用，
     */
    @Override
    public long verifySchemaUse(DatabaseTypeEnum databaseType, Long schemaId) {
        return 0L;
    }

    /**
     * 更新新增/修改操作到数据地图
     *
     * @param databaseType  元数据类型
     * @param data
     */
    @Override
    public void updateMetadataChangeInfoToGraph(DatabaseType databaseType, Metadata data) {

        Long value = 0L;

        McSchemaPO schemaPO = schemaMapper.findById(data.getSchemaId());
        if(schemaPO==null){
            log.error("更新新增/修改到数据地图失败：查询shemaId失败 {}",data.getSchemaId());
            return;
        }
        McDatabasePO databasePO = databaseMapper.getDatabaseById(schemaPO.getDbId());
        if(databasePO==null){
            log.error("更新新增/修改到数据地图失败：查询databaseId失败 {}" ,schemaPO.getDbId());
            return;
        }

        if(databaseType.equals(HDFS)){
            FolderNodeDto nodeDto = new FolderNodeDto();
            nodeDto.setRenterId(data.getRenterId());
            nodeDto.setFolderId(data.getId());
            nodeDto.setSchemaId(data.getSchemaId());
            nodeDto.setSchemaName(schemaPO.getNameCn());
            nodeDto.setDatabaseType(HDFS);
            String schemaPath = schemaPO.getName()+data.getIdentification();
            nodeDto.setFolderPath(schemaPath);
            nodeDto.setDatabaseId(schemaPO.getDbId());
            nodeDto.setServerId(databasePO.getServerId());
            value = nodeService.createOrUpdateFolderNode(nodeDto);
        }else{
            TableNodeDto tableDto = new TableNodeDto();
            tableDto.setRenterId(data.getRenterId());
            tableDto.setSchemaId(data.getSchemaId());
            tableDto.setSchemaName(schemaPO.getNameCn());
            tableDto.setTableId(data.getId());
            tableDto.setTableName(data.getName());
            tableDto.setDatabaseType(databaseType);
            tableDto.setDatabaseId(schemaPO.getDbId());
            tableDto.setServerId(databasePO.getServerId());
            value = nodeService.createTableNode(tableDto);
        }
        if(value<0L){
            log.error("更新新增/修改到数据地图失败：调用地图接口失败");
        }
    }

    /**
     * 更新删除操作到数据地图
     *
     * @param databaseType 元数据类型
     * @param metadataId
     */
    @Override
    public void updateMetadataDeleteInfoToGraph(DatabaseType databaseType, Long metadataId) {

        DeleteResult result = null;
        if (databaseType.equals(HDFS)) {
            result = nodeService.deleteFolderNode(metadataId);
        } else {
            result = nodeService.deleteTableNode(metadataId);
        }
        if(!result.isSuccess()){
            log.error("更新元数据删除到数据地图失败");
        }

    }


}

package com.ys.idatrix.metacube.metamanage.service;


import com.ys.idatrix.graph.service.api.def.DatabaseType;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;

/**
 * 元数据定义提供接口
 */
public interface IMetaDefBaseService {

    /**
     * 判断模式是否被占用
     *
     * @param schemaId 模式ID
     * @param databaseType 数据库类型
     * @return 大于0 表示模式使用次数（删除的不统计），0表示没有使用，
     */
    long verifySchemaUse(DatabaseTypeEnum databaseType, Long schemaId);

    /**
     * 更新新增/修改操作到数据地图
     */
    void updateMetadataChangeInfoToGraph(DatabaseType databaseType, Metadata data);

    /**
     * 更新删除操作到数据地图
     */
    void updateMetadataDeleteInfoToGraph(DatabaseType databaseType, Long metadataId);

}

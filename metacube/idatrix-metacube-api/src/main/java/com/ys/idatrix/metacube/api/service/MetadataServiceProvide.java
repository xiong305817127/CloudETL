package com.ys.idatrix.metacube.api.service;

import com.ys.idatrix.metacube.api.beans.*;

import java.util.List;

/**
 * @ClassName MetadataService
 * @Description
 * @Author ouyang
 * @Date
 */
public interface MetadataServiceProvide {

    // =========== table    数据地图 需要接口，不需要考虑授权信息

    // 根据模式id查询可以访问的表
    ResultBean<List<MetadataDTO>> findTableListBySchemaId(Long schemaId);

    // 表ID查询接口
    ResultBean<MetadataDTO> findTableId(Long renterId, String ip, int databaseType, String serviceName, String schemaName, String tableName);

    // 根据表ID查询表字段
    ResultBean<List<MetaFieldDTO>> findColumnListByTable(Long tableId);

    // ========== view

    // 根据模式id查询可以访问的视图列表
    ResultBean<List<MetadataDTO>> findViewListBySchemaId(Long schemaId);

    // 视图查询接口
    ResultBean<MetadataDTO> findViewId(Long renterId, String ip, int databaseType, String serviceName, String schemaName, String tableName);


    // ======  view & table     etl 需要接口
    // 根据模式ID查询用户可以访问的表和视图，加上权限判断（查出当前用户可访问的集合）
    ResultBean<TableViewDTO> findTableOrViewBySchemaId(Long schemaId, String username, ModuleTypeEnum module, ActionTypeEnum actionType);

    // 根据表ID/视图ID 查询表字段
    ResultBean<List<MetaFieldDTO>> findColumnListByTableIdOrViewId(Long metaId);


    // ========== HDFS

    // 目录ID查询接口
    ResultBean<MetadataDTO> findHDFSId(String folderPath);

    /**
     * ETL 调用根据用户，返回当前用户有权限的根目录列表  提供给ETL使用
     * @param username  需要查询的用户名
     * @param mod 需要查询的权限 ActionTypeEnum
     * @return
     */
    ResultBean<List<String>> findHDFSFolderByUser(String username, ActionTypeEnum mod);

}
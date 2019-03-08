package com.ys.idatrix.db.api.hbase.service;


import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hbase.dto.HBaseColumn;
import com.ys.idatrix.db.api.hbase.dto.HBaseTable;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;

import java.util.List;


/**
 * @ClassName: HBaseService
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public interface HBaseService {

    /**
     * 创建namespace
     *
     * @param username
     * @param namespace
     * @return
     */
    RespResult<SqlExecRespDto> createNamespace(String username, String namespace);


    /**
     * 删除 namespace(schema)
     *
     * @param username
     * @param namespace
     * @return
     */
    RespResult<SqlExecRespDto> dropNamespace(String username, String namespace);


    /**
     * 建表
     *
     * @param username
     * @param createTable
     * @return
     */
    RespResult<SqlExecRespDto> createTable(String username, HBaseTable createTable);


    /**
     * 删表
     *
     * @param username
     * @param namespace
     * @param tableName
     * @param bForced
     * @return
     */
    RespResult<SqlExecRespDto> dropTable(String username, String namespace, String tableName, boolean bForced);


    /**
     * 更新表结构，支持表全局属性修改，列的新增和删除动作
     *
     * @param username
     * @param oldTable
     * @param newTable
     * @return
     */
    RespResult<SqlExecRespDto> alterTable(String username, HBaseTable oldTable, HBaseTable newTable);


    /**
     * 新增列
     *
     * @param username
     * @param namespace
     * @param tableName
     * @param columns
     * @return
     */
    RespResult<SqlExecRespDto> addColumn(String username, String namespace, String tableName, List<HBaseColumn> columns);


    /**
     * 删除列
     *
     * @param username
     * @param namespace
     * @param tableName
     * @param columnNames
     * @return
     */
    RespResult<SqlExecRespDto> dropColumn(String username, String namespace, String tableName, List<String> columnNames);


    /**
     * 创建二级索引
     *
     * @param username
     * @param indexName
     * @param namespace
     * @param tableName
     * @param columnNames
     * @return
     */
    RespResult<SqlExecRespDto> createIndex(String username, String indexName, String namespace, String tableName, List<String> columnNames);

}

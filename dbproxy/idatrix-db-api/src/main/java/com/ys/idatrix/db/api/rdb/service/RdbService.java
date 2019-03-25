package com.ys.idatrix.db.api.rdb.service;


import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.*;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;

/**
 * 关系型数据库操作接口类
 *
 * @author lijie@gdbigdata.com
 * @version 1.0
 * @date 创建时间：2017年4月24日 上午10:10:35
 * @parameter
 * @return
 */
public interface RdbService {


    /**
     * 创建存储系统
     *
     * @param username
     * @param rdbLink jdbc信息
     * @param database
     * @return
     */
    RespResult<SqlExecRespDto> createDatabase(String username, RdbLinkDto rdbLink, RdbCreateDatabase database);


    /**
     * 删除数据库
     *
     * @param username
     * @param rdbLink
     * @param database
     * @return
     */
    RespResult<SqlExecRespDto> dropDatabase(String username, RdbLinkDto rdbLink, RdbDropDatabase database);


    /**
     * 创建表
     *
     * @param username
     * @param rdbLink
     * @param rct
     * @return
     */
    RespResult<SqlExecRespDto> createTable(String username, RdbLinkDto rdbLink, RdbCreateTable rct);


    /**
     * alter表
     *
     * @param username
     * @param rdbLink
     * @param alterTable
     * @return
     */
    RespResult<SqlExecRespDto> alterTable(String username, RdbLinkDto rdbLink, RdbAlterTable alterTable);


    /**
     * 删除表接口
     *
     * @param username   用户名
     * @param tableName  删除的表名
     * @param rdbLink jdbc连接信息
     * @param bForced    是否强制删除（已存在数据的）
     * @return
     */
    RespResult<SqlExecRespDto> dropTable(String username, String tableName, RdbLinkDto rdbLink, boolean bForced);


    /**
     * oracle 创建序列接口
     *
     * @param username   用户名
     * @param rdbLink jdbc信息
     * @param seqName    序列名称
     * @return
     */
    RespResult<SqlExecRespDto> createSequence(String username, RdbLinkDto rdbLink, String seqName);

    /**
     * oracle 删除序列
     *
     * @param username   用户名
     * @param rdbLink jdbc信息
     * @param seqName    序列名称
     * @return
     */
    RespResult<SqlExecRespDto> dropSequence(String username, RdbLinkDto rdbLink, String seqName);


    /**
     * 数据库用户校验
     *
     * @param dbUserName
     * @param rdbLink
     * @return
     */
    RespResult<Boolean> dbUserExists(String dbUserName, RdbLinkDto rdbLink);


    /**
     * 数据库名称校验
     *
     * @param dbName
     * @param rdbLink
     * @return
     */
    RespResult<Boolean> dbNameExists(String dbName, RdbLinkDto rdbLink);


    /**
     * db link 连接测试
     *
     * @param rdbLink
     * @return
     */
    RespResult<Boolean> testDBLink(RdbLinkDto rdbLink);

}

package com.ys.idatrix.db.service.internal;

import com.ys.idatrix.db.api.rdb.dto.*;

import java.util.List;

/**
 * @ClassName: IRdbDDL
 * @Description:
 * @Author: ZhouJian
 * @Date: 2017/12/25
 */
public interface IRdbDDL {

    /**
     * 获取数据库类型
     * @return
     */
    RdbEnum.DBType getDBType();


    /**
     * 创建用户（oracle、dm 必须是管理员用户操作，mysql 必须是root用户操作）
     * @return
     */
    List<String> getCreateUserCommands();


    /**
     * 创建表空间
     * @return
     */
    List<String> getCreateTablespace();


    /**
     * 赋予操作权限给用户
     * @return
     */
    List<String> getGrantOptionToUser();


    /**
     * 创建数据库sql
     * @param database
     * @return
     */
    List<String> getCreateDatabaseCommands(RdbCreateDatabase database);


    /**
     * 删除数据库sql
     * @param database
     * @return
     */
    List<String> getDropDatabaseCommands(RdbDropDatabase database);


    /**
     * 创建数据库sql
     * @param database
     * @return
     */
    //List<String> getCreateSchemaCommands(RDBCreateDatabase database) ;


    /**
     * 创建数据库sql
     * @param database
     * @return
     */
    //List<String> getDropSchemaCommands(RDBCreateDatabase database);


    /**
     * 创建表sql
     *
     * @param rct
     * @return
     */
    List<String> getCreateTableCommands(RdbCreateTable rct);


    /**
     * 删除表sql
     * @param tableName
     * @return
     */
    List<String> getDropTableCommands(String tableName);


    /**
     *  修改表
     *
     * @param alterTable
     * @return
     */
    List<String> getAlterTableCommands(RdbAlterTable alterTable);


    /**
     * 创建序列sql(oracle)
     *
     * @param seqName
     * @return
     */
    List<String> getCreateSequenceCommands(String seqName);


    /**
     * 删除序列sql(oracle)
     *
     * @param seqName
     * @return
     */
    List<String> getDropSequenceCommands(String seqName);


    /**
     * 查询数据库用户
     *
     * @param user
     * @return
     */
    String getSelectUserCommand(String user);


    /**
     * 查询数据库
     *
     * @param DbName
     * @return
     */
    String getSelectDbNameCommand(String DbName);


}

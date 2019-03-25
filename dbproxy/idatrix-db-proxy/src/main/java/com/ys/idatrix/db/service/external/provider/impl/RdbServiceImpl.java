package com.ys.idatrix.db.service.external.provider.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.base.Preconditions;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.*;
import com.ys.idatrix.db.api.rdb.service.RdbService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.core.rdb.RdbExecService;
import com.ys.idatrix.db.exception.DbProxyException;
import com.ys.idatrix.db.init.RdbDDLServiceAware;
import com.ys.idatrix.db.service.external.provider.base.DbServiceAware;
import com.ys.idatrix.db.service.internal.IRdbDDL;
import com.ys.idatrix.db.util.SqlExecuteUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 关系型数据库操作实现类
 *
 * @ClassName: RdbServiceImpl
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Slf4j
@Service(protocol = "dubbo", timeout = 60000, interfaceClass = RdbService.class)
@Component
public class RdbServiceImpl extends DbServiceAware implements RdbService {

    @Autowired
    private RdbExecService rdbExecService;

    /**
     * 创建数据库
     *
     * @param username
     * @param rdbLink  jdbc信息
     * @param database
     * @return
     */
    @Override
    public RespResult<SqlExecRespDto> createDatabase(String username, RdbLinkDto rdbLink, RdbCreateDatabase database) {

        if (StringUtils.isBlank(username)) {
            return RespResult.buildFailWithMsg("create database, username is null");
        }

        try {
            Preconditions.checkNotNull(rdbLink, "create database, RdbLinkDto is null");

            Preconditions.checkNotNull(database, "create database, RdbCreateDatabase is null");

            if (rdbLink.getType().equalsIgnoreCase(RdbEnum.DBType.ORACLE.name())) {
                return RespResult.buildFailWithMsg("create database, Oracle does not support");
            }

            //根据DB类型获取实际的处理类
            IRdbDDL actualDBDDL = getActualDBDDL(rdbLink);

            //获取执行sql
            List<String> processCommands = actualDBDDL.getCreateDatabaseCommands(database);
            log.info("create database, sql is: {},username:{}", processCommands, username);

            //执行结果
            List<SqlExecRespDto> results = rdbExecService.batchExecuteUpdate(processCommands, rdbLink);

            return wrapExecuteResult(results, processCommands);
        } catch (Exception e) {
            log.error("createDatabase 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }

    }


    /**
     * 删除库
     *
     * @param username
     * @param rdbLink
     * @param database
     * @return
     */
    @Override
    public RespResult<SqlExecRespDto> dropDatabase(String username, RdbLinkDto rdbLink, RdbDropDatabase database) {

        if (StringUtils.isBlank(username)) {
            return RespResult.buildFailWithMsg("drop database, username is null");
        }

        if (null == database) {
            return RespResult.buildFailWithMsg("drop database, RdbDropDatabase is null");
        }


        if (rdbLink.getType().equalsIgnoreCase(RdbEnum.DBType.ORACLE.name())) {
            return RespResult.buildFailWithMsg("drop database, Oracle does not support");
        }

        try {
            Preconditions.checkNotNull(rdbLink, "drop database, RdbLinkDto is null");
            //根据DB类型获取实际的处理类
            IRdbDDL actualDBDDL = getActualDBDDL(rdbLink);

            //获取执行sql
            List<String> processCommands = actualDBDDL.getDropDatabaseCommands(database);
            log.info("drop database, processCommands is : {},username:{}", processCommands, username);

            //执行结果
            List<SqlExecRespDto> results = rdbExecService.batchExecuteUpdate(processCommands, rdbLink);

            return wrapExecuteResult(results, processCommands);
        } catch (Exception e) {
            log.error("dropDatabase 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }

    }


    /**
     * 创建表
     *
     * @param username 用户名
     * @param rdbLink
     * @param rct      建表bean
     * @return
     */
    @Override
    public RespResult<SqlExecRespDto> createTable(String username, RdbLinkDto rdbLink, RdbCreateTable rct) {

        if (StringUtils.isBlank(username)) {
            return RespResult.buildFailWithMsg("create table, username is null");
        }

        Preconditions.checkNotNull(rct, "create table, RdbCreateTable is null");

        if (StringUtils.isBlank(rct.getTableName())) {
            return RespResult.buildFailWithMsg("create table, tableName is null");
        }

        if ((CollectionUtils.isEmpty(rct.getRdbColumns()))) {
            return RespResult.buildFailWithMsg("create table, columns is null");
        }

        try {

            Preconditions.checkNotNull(rdbLink, "create table, RdbLinkDto is null");

            //根据DB类型获取实际的处理类
            IRdbDDL actualDBDDL = getActualDBDDL(rdbLink);

            //获取执行sql
            List<String> processCommands = actualDBDDL.getCreateTableCommands(rct);
            log.info("create table, processCommands is : {},username:{}", processCommands, username);

            //执行结果
            List<SqlExecRespDto> results = rdbExecService.batchExecuteUpdate(processCommands, rdbLink);

            return wrapExecuteResult(results, processCommands);
        } catch (Exception e) {
            log.error("createTable 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }

    }


    /**
     * 修改表
     *
     * @param username
     * @param rdbLink
     * @param alterTable
     * @return
     */
    @Override
    public RespResult<SqlExecRespDto> alterTable(String username, RdbLinkDto rdbLink, RdbAlterTable alterTable) {

        if (StringUtils.isBlank(username)) {
            return RespResult.buildFailWithMsg("alter table, username is null");
        }

        try {

            Preconditions.checkNotNull(rdbLink, "alter table, RdbLinkDto is null");

            Preconditions.checkNotNull(alterTable, "alter table, RDBAlterTable is null");

            //根据DB类型获取实际的处理类
            IRdbDDL actualDBDDL = getActualDBDDL(rdbLink);

            //获取执行sql
            List<String> processCommands = actualDBDDL.getAlterTableCommands(alterTable);
            log.info("alter table, processCommands is : {},username:{}", processCommands, username);

            // modified by zhoujian on 2018-07-07
            // 修改原因：元数据修改表时，如果没有任何变动，db-proxy 返回"success=false,message=update sql commands is null",
            // 而元数据没有对错误信息进行判断，只是根据标识判断了。所有主动加上判断，然后返回"success=true,message=unchanged:no sql commands"
            if (CollectionUtils.isEmpty(processCommands)) {
                log.warn("unchanged:no sql commands");
                return RespResult.buildSuccessWithMsg("unchanged:no sql commands");
            }

            //执行结果
            List<SqlExecRespDto> results = rdbExecService.batchExecuteUpdate(processCommands, rdbLink);
            return wrapExecuteResult(results, processCommands);
        } catch (Exception e) {
            log.error("alterTable 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }

    }


    /**
     * 删除表
     *
     * @param username  用户名
     * @param tableName 删除的表名
     * @param rdbLink   jdbc连接信息
     * @param bForced   是否强制删除（已存在数据的）
     * @return
     */
    @Override
    public RespResult<SqlExecRespDto> dropTable(String username, String tableName, RdbLinkDto rdbLink, boolean bForced) {

        if (StringUtils.isBlank(username)) {
            return RespResult.buildFailWithMsg("drop table, username is null");
        }

        if (StringUtils.isBlank(tableName)) {
            return RespResult.buildFailWithMsg("drop table, TableName is null");
        }

        try {

            Preconditions.checkNotNull(rdbLink, "drop table, RdbLinkDto is null");

            //根据DB类型获取实际的处理类
            IRdbDDL actualDBDDL = getActualDBDDL(rdbLink);

            //是否强制删除 否：查询删除表中是否存在数据。有则提示错误; 是：直接删除.跳过
            if (!bForced) {
                checkDropTable(tableName, rdbLink);
            }

            //获取执行sql
            List<String> processCommands = actualDBDDL.getDropTableCommands(tableName);
            log.info("drop table, processCommands is : {},username:{}", processCommands, username);

            //执行结果
            List<SqlExecRespDto> results = rdbExecService.batchExecuteUpdate(processCommands, rdbLink);
            return wrapExecuteResult(results, processCommands);
        } catch (Exception e) {
            log.error("dropTable 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }

    }


    /**
     * oracle 创建序列
     *
     * @param username
     * @param rdbLink
     * @param seqName
     * @return
     */
    @Override
    public RespResult<SqlExecRespDto> createSequence(String username, RdbLinkDto rdbLink, String seqName) {

        if (StringUtils.isBlank(username)) {
            return RespResult.buildFailWithMsg("create Sequence, username is null");
        }

        if (StringUtils.isBlank(seqName)) {
            return RespResult.buildFailWithMsg("create Sequence, seqName is null");
        }

        Preconditions.checkNotNull(rdbLink, "create Sequence, RdbLinkDto is null");

        if (!rdbLink.getType().equalsIgnoreCase(RdbEnum.DBType.ORACLE.name())) {
            return RespResult.buildFailWithMsg("create Sequence, Non-Oracle types does not support");
        }

        try {

            //根据DB类型获取实际的处理类
            IRdbDDL actualDBDDL = getActualDBDDL(rdbLink);

            //获取执行sql
            List<String> processCommands = actualDBDDL.getCreateSequenceCommands(seqName);
            log.info("create sequence, processCommands is : {},username:{}", processCommands, username);

            //执行结果
            List<SqlExecRespDto> results = rdbExecService.batchExecuteUpdate(processCommands, rdbLink);

            return wrapExecuteResult(results, processCommands);
        } catch (Exception e) {
            log.error("createSequence 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }

    }


    /**
     * oracle 删除序列
     *
     * @param username
     * @param rdbLink
     * @param seqName
     * @return
     */
    @Override
    public RespResult<SqlExecRespDto> dropSequence(String username, RdbLinkDto rdbLink, String seqName) {

        if (StringUtils.isBlank(username)) {
            return RespResult.buildFailWithMsg("drop Sequence, username is null");
        }

        if (StringUtils.isBlank(seqName)) {
            return RespResult.buildFailWithMsg("drop Sequence, seqName is null");
        }

        Preconditions.checkNotNull(rdbLink, "drop Sequence, RdbLinkDto is null");

        if (!rdbLink.getType().equalsIgnoreCase(RdbEnum.DBType.ORACLE.name())) {
            return RespResult.buildFailWithMsg("drop Sequence, Non-Oracle types does not support");
        }

        try {

            //根据DB类型获取实际的处理类
            IRdbDDL actualDBDDL = getActualDBDDL(rdbLink);

            //获取执行sql
            List<String> processCommands = actualDBDDL.getDropSequenceCommands(seqName);
            log.info("drop sequence, processCommands is : {},username:{}", processCommands, username);

            //执行结果
            List<SqlExecRespDto> results = rdbExecService.batchExecuteUpdate(processCommands, rdbLink);

            return wrapExecuteResult(results, processCommands);
        } catch (Exception e) {
            log.error("dropSequence 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }

    }


    /**
     * 查询用户
     *
     * @param dbUserName
     * @param rdbLink
     * @return
     */
    @Override
    public RespResult<Boolean> dbUserExists(String dbUserName, RdbLinkDto rdbLink) {

        if (StringUtils.isBlank(dbUserName)) {
            return RespResult.buildFailWithMsg("用户名为空");
        }

        try {

            Preconditions.checkNotNull(rdbLink, "check db-user exists, RdbLinkDto is null");

            //根据DB类型获取实际的处理类
            IRdbDDL actualDBDDL = getActualDBDDL(rdbLink);

            //获取执行sql
            String processCommands = actualDBDDL.getSelectUserCommand(dbUserName);
            log.info("check db-user exists, processCommands is : {},username:{}", processCommands);

            //执行结果
            SqlQueryRespDto resp = rdbExecService.executeQuery(processCommands, rdbLink);

            return RespResult.buildSuccessWithData(CollectionUtils.isEmpty(resp.getData()) ? Boolean.FALSE : Boolean.TRUE);

        } catch (Exception e) {
            log.error("dbUserExists 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }

    }


    /**
     * 查询DB名称是否存在（仅限MySqL）
     *
     * @param dbName
     * @param rdbLink
     * @return
     */
    @Override
    public RespResult<Boolean> dbNameExists(String dbName, RdbLinkDto rdbLink) {

        if (StringUtils.isBlank(dbName)) {
            return RespResult.buildFailWithMsg("数据库名为空");
        }

        Preconditions.checkNotNull(rdbLink, "check db-name exists, RdbLinkDto is null");

        if (!RdbEnum.DBType.MYSQL.name().equals(rdbLink.getType().toUpperCase())) {
            return RespResult.buildFailWithMsg("Qualify Mysql call");
        }

        try {

            //根据DB类型获取实际的处理类
            IRdbDDL actualDBDDL = getActualDBDDL(rdbLink);

            //获取执行sql
            String processCommands = actualDBDDL.getSelectDbNameCommand(dbName);
            log.info("check db-user exists, processCommands is : {},username:{}", processCommands);

            //执行结果
            SqlQueryRespDto resp = rdbExecService.executeQuery(processCommands, rdbLink);
            if (CollectionUtils.isEmpty(resp.getData())) {
                return RespResult.buildSuccessWithData(Boolean.FALSE);
            } else {
                if (Integer.valueOf(resp.getData().get(0).get("cnt") + "") > 0) {
                    return RespResult.buildSuccessWithData(Boolean.TRUE);
                } else {
                    return RespResult.buildSuccessWithData(Boolean.FALSE);
                }
            }
        } catch (Exception e) {
            log.error("dbNameExists 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }

    }


    /**
     * 测试数据库连接
     *
     * @param rdbLink
     * @return
     */
    @Override
    public RespResult<Boolean> testDBLink(RdbLinkDto rdbLink) {
        try {
            //重构RdbLinkDto
            rdbLink = SqlExecuteUtils.rebuildRdbLink(rdbLink);
            return RespResult.buildSuccessWithData(rdbExecService.testDBLink(rdbLink));
        } catch (Exception e) {
            log.error("testDBLink 执行异常:{}", e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }
    }


    /**
     * 获取数据库类型  MYSQL or ORACLE or DM7
     *
     * @param linkDto
     * @return
     */
    private RdbEnum.DBType getDBType(RdbLinkDto linkDto) {
        return RdbEnum.DBType.valueOf(linkDto.getType().toUpperCase());
    }


    /**
     * drop 表操作做校验 -- 查询表记录数据
     *
     * @param tableName
     * @param rdbLink
     */
    private void checkDropTable(String tableName, RdbLinkDto rdbLink) throws Exception {
        String queryRecords = "select count(1) from " + (getDBType(rdbLink) == RdbEnum.DBType.MYSQL ? addBackquote(tableName) : tableName);
        SqlQueryRespDto rt = rdbExecService.executeQuery(queryRecords, rdbLink);
        if (CollectionUtils.isNotEmpty(rt.getData())) {
            long cnt = rt.getData().get(0).values().stream().filter(rts -> Integer.parseInt(rts + "") > 0).count();
            if (cnt > 0L) {
                throw new DbProxyException("records already exists in the table");
            }
        }
    }


    /**
     * 获取RDB的实际处理Dao
     *
     * @param rdbLink
     * @return
     * @throws Exception
     */
    private IRdbDDL getActualDBDDL(RdbLinkDto rdbLink) {

        //重构RDBConfiguration
        SqlExecuteUtils.rebuildRdbLink(rdbLink);

        //根据DB类型获取实际的处理类
        IRdbDDL actualDBDao = RdbDDLServiceAware.getRealRdbDDL(getDBType(rdbLink).name());
        if (null == actualDBDao) {
            throw new DbProxyException("没有找到当前类型:" + rdbLink.getType() + " 的处理类");
        }
        log.info("operation dbType:{}", rdbLink.getType());
        return actualDBDao;
    }


    /**
     * mysql 添加反引号。区分特殊字符与数据库保留字段
     *
     * @param value
     * @return
     */
    private String addBackquote(String value) {
        return "`" + value + "`";
    }

}

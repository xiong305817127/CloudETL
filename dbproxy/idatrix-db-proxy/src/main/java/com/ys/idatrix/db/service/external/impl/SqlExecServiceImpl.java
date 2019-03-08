package com.ys.idatrix.db.service.external.impl;

import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.ys.idatrix.db.annotation.TargetMetric;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.sql.dto.*;
import com.ys.idatrix.db.api.sql.service.SqlExecService;
import com.ys.idatrix.db.core.hbase.PhoenixExecService;
import com.ys.idatrix.db.core.hive.SparkExecService;
import com.ys.idatrix.db.core.rdb.RdbExecService;
import com.ys.idatrix.db.domain.DbSqlExecution;
import com.ys.idatrix.db.domain.DbSqlResult;
import com.ys.idatrix.db.dto.ParseResultDto;
import com.ys.idatrix.db.enums.HBaseOperator;
import com.ys.idatrix.db.enums.HiveOperator;
import com.ys.idatrix.db.enums.RdbOperator;
import com.ys.idatrix.db.exception.DbProxyException;
import com.ys.idatrix.db.init.SqlParserService;
import com.ys.idatrix.db.service.consumer.MetadataConsumer;
import com.ys.idatrix.db.service.internal.SqlExecWorker;
import com.ys.idatrix.db.service.internal.SqlTaskExecService;
import com.ys.idatrix.db.util.Constants;
import com.ys.idatrix.db.util.SqlExecuteUtils;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.MetaDatabaseDTO;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: SqlExecuteServiceImpl
 * @Description: sql 执行服务
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Slf4j
@Service(protocol = "dubbo", timeout = 60000, interfaceClass = SqlExecService.class)
@Component
public class SqlExecServiceImpl implements SqlExecService {

    @Autowired(required = false)
    private RdbExecService rdbExecService;

    @Autowired(required = false)
    private SparkExecService sparkExecService;

    @Autowired(required = false)
    private PhoenixExecService phoenixExecService;

    @Autowired
    private SqlTaskExecService sqlTaskExecService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private SqlParserService sqlParserService;

    @Autowired(required = false)
    private MetadataConsumer metadataConsumer;


    @TargetMetric
    @Override
    public RespResult<SqlQueryRespDto> executeQuery(String username, SqlExecReqDto sqlExecReqDto) {
        try {
            wrapSqlExecuteDto(sqlExecReqDto);
            List<ParseResultDto> parseResults = sqlParserService.parseSQL(sqlExecReqDto.getCommand(), sqlExecReqDto.getType());
            sqlParserService.validateAndRebuildParseResult(parseResults, sqlExecReqDto, username, "select");
            // 获取存储系统信息
            RdbLinkDto linkDto = SqlExecuteUtils.generateRdbLink(sqlExecReqDto);
            SqlQueryRespDto respDto = rdbExecService.executeQuery(sqlExecReqDto.getCommand(), linkDto);
            return RespResult.buildSuccessWithData(respDto);
        } catch (ParserException e) {
            log.error("输入SQL:{} 语法错误:{}", sqlExecReqDto.getCommand(), e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        } catch (Exception e) {
            log.error("executeQuery executed sql:{} is error:{}", sqlExecReqDto.getCommand(), e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }
    }


    @TargetMetric
    @Override
    public RespResult<SqlExecRespDto> executeUpdate(String username, SqlExecReqDto update) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public RespResult<List<SqlExecRespDto>> batchExecuteUpdate(String username, List<SqlExecReqDto> updates) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public RespResult<SqlExecRespDto> asyncExecute(String username, SqlExecReqDto sqlExecReqDto) {
        try {
            wrapSqlExecuteDto(sqlExecReqDto);
            //记录用户原始输入的sql
            String original_sql = sqlExecReqDto.getCommand();
            List<ParseResultDto> parseResults = sqlParserService.parseSQL(sqlExecReqDto.getCommand(), sqlExecReqDto.getType());
            sqlParserService.validateAndRebuildParseResult(parseResults, sqlExecReqDto, username, null);
            String sqlExecType;
            if (DatabaseTypeEnum.HIVE.getName().equalsIgnoreCase(sqlExecReqDto.getType())) {
                sqlExecType = HiveOperator.valueOf(parseResults.get(0).getMainOperator()).getRunnerType();
            } else if (DatabaseTypeEnum.HBASE.getName().equalsIgnoreCase(sqlExecReqDto.getType())) {
                sqlExecType = HBaseOperator.valueOf(parseResults.get(0).getMainOperator()).getRunnerType();
            } else {
                sqlExecType = RdbOperator.valueOf(parseResults.get(0).getMainOperator()).getRunnerType();
            }

            log.info("数据源类型:{}, 执行SQL:{},操作类型:{} ", sqlExecReqDto.getType(), sqlExecReqDto.getCommand(), sqlExecType);

            //获取存储系统配置
            RdbLinkDto linkDto = SqlExecuteUtils.generateRdbLink(sqlExecReqDto);

            SqlExecRespDto respDto = asyncExecute(username, linkDto, sqlExecReqDto, sqlExecType, original_sql);

            return RespResult.buildSuccessWithData(respDto);

        } catch (ParserException e) {
            log.error("输入SQL:{} 语法错误:{}", sqlExecReqDto.getCommand(), e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        } catch (Exception e) {
            log.error("异步执行SQL:{} 错误:{}", sqlExecReqDto.getCommand(), e.getMessage());
            return RespResult.buildFailWithMsg(e.getMessage());
        }
    }


    /**
     * select insert,update upsert,delete DML操作
     *
     * @param username
     * @param linkDto
     * @param sqlExecReqDto
     * @param sqlExecType
     * @param original_sql
     * @return
     */
    private SqlExecRespDto asyncExecute(String username, RdbLinkDto linkDto,
                                        SqlExecReqDto sqlExecReqDto, String sqlExecType, String original_sql) {
        // 创建异步执行信息
        DbSqlExecution execution = new DbSqlExecution();
        execution.setSystem(linkDto.getType());
        execution.setType(sqlExecType);
        execution.setExecutingCount(1);
        List<DbSqlResult> sqlResults = new ArrayList<DbSqlResult>();
        DbSqlResult dbSqlResult = new DbSqlResult();
        dbSqlResult.setDbSource(sqlExecReqDto.getSchemaName());
        dbSqlResult.setDbType(linkDto.getType());
        //保存数据库记录用户输入的原始sql(可历史查看展示)
        dbSqlResult.setSql(original_sql);
        dbSqlResult.setStatus(Constants.SQL_EXEC_STATUS_WAIT);
        sqlResults.add(dbSqlResult);
        int executionId = sqlTaskExecService.createExecution(username, execution, sqlResults);
        //实际执行sql为重构后的sql
        sqlResults.get(0).setSql(sqlExecReqDto.getCommand());
        // 提交给线程池异步执行
        Runnable executor;
        if (DatabaseTypeEnum.HIVE.getName().equalsIgnoreCase(linkDto.getType())) {
            executor = new SqlExecWorker(null, sparkExecService, null,
                    sqlExecType, linkDto,
                    sqlTaskExecService, dbSqlResult, username);
        } else if (DatabaseTypeEnum.HBASE.getName().equalsIgnoreCase(linkDto.getType())) {
            executor = new SqlExecWorker(null, null, phoenixExecService,
                    sqlExecType, linkDto,
                    sqlTaskExecService, dbSqlResult, username);
        } else {//RDBMS操作(mysql)
            executor = new SqlExecWorker(rdbExecService, null, null,
                    sqlExecType, linkDto,
                    sqlTaskExecService, dbSqlResult, username);
        }
        taskExecutor.execute(executor);
        return new SqlExecRespDto(executionId, sqlExecReqDto.getCommand());
    }


    @Override
    public RespResult<Integer> batchExecuteUpdateNoBlocking(String username, String system, List<SqlExecReqDto> updates) {
        // TODO Auto-generated method stub
        return RespResult.buildSuccessWithData(0);
    }


    @Override
    public RespResult<List<SqlTaskExecDto>> getLatestSqlTasks(String username, String system, int rows) {
        List<SqlTaskExecDto> results = Lists.newArrayList();
        List<DbSqlExecution> dbSqlExecutions = sqlTaskExecService.findLatestExecutions(username, system, rows);
        for (DbSqlExecution dbSqlExecution : dbSqlExecutions) {
            RespResult<SqlTaskExecDto> result = getSqlTaskDetail(dbSqlExecution.getId());
            results.add(result.getData());
        }
        return RespResult.buildSuccessWithData(results);
    }


    @Override
    public RespResult<SqlTaskExecDto> getSqlTaskDetail(int executionId) {
        DbSqlExecution dbSqlExecution = sqlTaskExecService.getExecution(executionId);
        List<DbSqlResult> dbSqlResults = sqlTaskExecService.findSqlResults(executionId);
        List<SqlTaskExecResultDto> sqlResults = Lists.newArrayList();
        for (DbSqlResult dbSqlResult : dbSqlResults) {
            SqlTaskExecResultDto sqlResult = new SqlTaskExecResultDto();
            sqlResult.setStatus(dbSqlResult.getStatus());
            sqlResult.setResult(dbSqlResult.getResult());
            sqlResult.setSql(dbSqlResult.getSql());
            sqlResults.add(sqlResult);
        }
        SqlTaskExecDto taskDto = new SqlTaskExecDto();
        taskDto.setId(executionId);
        taskDto.setCreator(dbSqlExecution.getCreator());
        taskDto.setCreateTime(dbSqlExecution.getCreateTime());
        taskDto.setModifyTime(dbSqlExecution.getModifyTime());
        if (null != dbSqlExecution.getModifyTime()) {
            long expendTime = dbSqlExecution.getModifyTime().getTime() - dbSqlExecution.getCreateTime().getTime();
            taskDto.setExpendTime(new Double(Math.round(expendTime) / 1000.0).toString());
        }
        taskDto.setType(dbSqlExecution.getType());
        taskDto.setResults(sqlResults);
        return RespResult.buildSuccessWithData(taskDto);
    }


    @Override
    public RespResult<Boolean> querySqlTaskIsCompleted(int executionId) {
        DbSqlExecution dbSqlExecution = sqlTaskExecService.getExecution(executionId);
        return dbSqlExecution.getExecutingCount() == 0 ? RespResult.buildSuccessWithData(Boolean.TRUE) : RespResult.buildSuccessWithData(Boolean.FALSE);
    }


    /**
     * 根据schemaId 获取 schemaName 及 dblink 信息
     *
     * @param executeDto
     */
    private void wrapSqlExecuteDto(SqlExecReqDto executeDto) {
        if (StringUtils.isBlank(executeDto.getSchemaName()) && executeDto.getSchemaId() != null) {
            ResultBean<MetaDatabaseDTO> dbRet = metadataConsumer.getDatabaseInfo(null, executeDto.getSchemaId());
            if (dbRet.isSuccess()) {
                MetaDatabaseDTO dbDTO = dbRet.getData();
                executeDto.setSchemaId(executeDto.getSchemaId());
                executeDto.setSchemaName(dbDTO.getDbName());
                executeDto.setIp(dbDTO.getIp());
                executeDto.setPort(dbDTO.getPort());
                executeDto.setUsername(dbDTO.getUsername());
                executeDto.setPassword(dbDTO.getPassword());
                executeDto.setType(DatabaseTypeEnum.getName(Integer.valueOf(dbDTO.getType())));
            } else {
                throw new DbProxyException("获取元数据db链接失败");
            }
        }
    }

}

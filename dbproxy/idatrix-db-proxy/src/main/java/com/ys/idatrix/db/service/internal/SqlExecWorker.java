package com.ys.idatrix.db.service.internal;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.aspect.MetricSink;
import com.ys.idatrix.db.core.hbase.PhoenixExecService;
import com.ys.idatrix.db.core.hive.SparkExecService;
import com.ys.idatrix.db.core.rdb.RdbExecService;
import com.ys.idatrix.db.domain.DbSqlResult;
import com.ys.idatrix.db.util.Constants;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metric.impl.ServiceMetricSinkImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SqlExecWorker
 * @Description: sql 执行者
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Slf4j
public class SqlExecWorker implements Runnable {

    private RdbExecService rdbExecService;

    private SparkExecService sparkExecService;

    private PhoenixExecService phoenixExecService;

    private String executionType;

    private RdbLinkDto linkDto;

    private SqlTaskExecService sqlTaskExecService;

    private DbSqlResult sqlResult;

    private String userId;

    private long startTimeStamp;

    public SqlExecWorker(RdbExecService rdbExecService, SparkExecService sparkExecService, PhoenixExecService phoenixExecService,
                         String executionType, RdbLinkDto linkDto,
                         SqlTaskExecService sqlTaskExecService, DbSqlResult sqlResult, String userId) {
        super();
        this.rdbExecService = rdbExecService;
        this.sparkExecService = sparkExecService;
        this.phoenixExecService = phoenixExecService;
        this.executionType = executionType;
        this.linkDto = linkDto;
        this.sqlTaskExecService = sqlTaskExecService;
        this.sqlResult = sqlResult;
        this.userId = userId;
        this.startTimeStamp = System.currentTimeMillis();
    }

    @Override
    public void run() {
        try {
            //查询操作
            if (Constants.SQL_EXEC_TYPE_Q.equalsIgnoreCase(executionType)) {
                SqlQueryRespDto queryResult;
                if (DatabaseTypeEnum.HBASE.getName().equalsIgnoreCase(linkDto.getType())) {
                    queryResult = phoenixExecService.executeQuery(sqlResult.getSql());
                } else if (DatabaseTypeEnum.HIVE.getName().equalsIgnoreCase(linkDto.getType())) {
                    queryResult = sparkExecService.executeQuery(linkDto.getDbName(), sqlResult.getSql());
                } else {
                    // 获取存储系统信息
                    queryResult = rdbExecService.executeQuery(linkDto, sqlResult.getSql());
                }
                sqlResult.setStatus(Constants.SQL_EXEC_STATUS_SUCCESS);
                String result = JSON.toJSONString(convertDateToString(queryResult), false);
                sqlResult.setResult(result);
            } else {
                List<SqlExecRespDto> executeResults;
                if (DatabaseTypeEnum.HBASE.getName().equalsIgnoreCase(linkDto.getType())) {
                    executeResults = phoenixExecService.batchExecuteUpdate(sqlResult.getSql());
                } else if (DatabaseTypeEnum.HIVE.getName().equalsIgnoreCase(linkDto.getType())) {
                    executeResults = sparkExecService.batchExecuteUpdate(sqlResult.getSql());
                } else {
                    executeResults = rdbExecService.batchExecuteUpdate(linkDto, sqlResult.getSql());
                }

                int updatedRows = 0;
                String status = Constants.SQL_EXEC_STATUS_SUCCESS;
                String errorMsg = null;
                if (CollectionUtils.isNotEmpty(executeResults)) {
                    for (SqlExecRespDto result : executeResults) {
                        updatedRows = updatedRows + result.getEffectRow();
                    }
                } else {
                    status = Constants.SQL_EXEC_STATUS_FAILED;
                    errorMsg = "SQL 执行不成功";
                }
                sqlResult.setStatus(status);
                sqlResult.setResult(null != errorMsg ? errorMsg : "执行成功，影响行数： " + updatedRows);
            }
        } catch (Exception e) {
            log.error("存储系统:" + linkDto.getType() + "sql执行:" + executionType);
            sqlResult.setStatus(Constants.SQL_EXEC_STATUS_FAILED);
            sqlResult.setResult(e.getMessage());
        } finally {
            try {
                //上传监控信息　Added By Wangbin 2017/08/24
                MetricSink metricSink = MetricSink.getInstrance();
                ServiceMetricSinkImpl sink = metricSink.getSink();
                long runTimeMs = System.currentTimeMillis() - startTimeStamp;
                int runStatus = StringUtils.equals(sqlResult.getStatus(), "success") ? 1 : 0;
                //上传成功状态
                sink.publishSingleMetric("dbproxy_exec_time_sqlexecutedao_asyncexecute", runTimeMs);
                //上传运行时间
                sink.publishSingleMetric("dbproxy_exec_success_sqlexecutedao_asyncexecute", runStatus);
                log.info("dbproxy_exec_time_sqlexecutedao_asyncexecute:{}", runTimeMs);
                log.info("dbproxy_exec_success_sqlexecutedao_asyncexecute:{}", runStatus);
            } catch (Exception e) {
                log.error("运维监控异常", e);
            }
            sqlTaskExecService.writeExecuteResult(userId, sqlResult);
        }

    }


    /**
     * 查询结果将数据库日期类型（Date、Timestamp、Time）转换字符串,datalab显示
     * <p>
     * 拆分为 columns:list<string>,values:list<list<object>>
     *
     * @param queryResult
     */
    private Map<String, List<Object>> convertDateToString(SqlQueryRespDto queryResult) {
        Map<String, List<Object>> resultList = Maps.newHashMap();
        if (null != queryResult && CollectionUtils.isNotEmpty(queryResult.getData())) {

            List<Object> columnNames = Lists.newArrayList(queryResult.getData().get(0).keySet());
            resultList.put("columns", columnNames);

            List<Object> columnValues = Lists.newArrayList();
            for (Map<String, Object> data : queryResult.getData()) {
                List<Object> rowValues = Lists.newArrayList();
                for (Object colValue : data.values()) {
                    if (null != colValue) {
                        if (colValue instanceof Timestamp) {
                            colValue = DateFormatUtils.format(((Timestamp) colValue).getTime(), "yyyy-MM-dd HH:mm:ss");
                        }
                        if (colValue instanceof java.sql.Date) {
                            colValue = DateFormatUtils.format(((java.sql.Date) colValue).getTime(), "yyyy-MM-dd HH:mm:ss");
                        }
                        if (colValue instanceof Time) {
                            colValue = DateFormatUtils.format(((Time) colValue).getTime(), "HH:mm:ss");
                        }
                    }
                    rowValues.add(colValue);
                }
                columnValues.add(rowValues);
            }
            resultList.put("values", columnValues);
        } else {
            resultList.put("columns", null);
            resultList.put("values", null);
        }
        return resultList;
    }

}

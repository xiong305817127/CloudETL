package com.ys.idatrix.db.service.external.provider.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hive.dto.HiveColumn;
import com.ys.idatrix.db.api.hive.dto.HiveTable;
import com.ys.idatrix.db.api.hive.dto.StoredType;
import com.ys.idatrix.db.api.hive.service.HiveService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.core.hive.SparkExecService;
import com.ys.idatrix.db.service.external.provider.base.DbServiceAware;
import com.ys.idatrix.db.util.HiveColumnComparator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName: HiveServiceImpl
 * @Description: 元数据注册目录增、删、改操作
 * @Author: ZhouJian
 * @Date: 2017/4/25
 */
@Slf4j
@Service(protocol = "dubbo", timeout = 60000, interfaceClass = HiveService.class)
@Component
public class HiveServiceImpl extends DbServiceAware implements HiveService {

    @Autowired(required = false)
    private SparkExecService sparkExecService;

    private final String createDatabase = "create database IF NOT EXISTS {0}";

    private final String dropDatabase = "drop database IF EXISTS {0}";

    private final String userDatabase = "use  {0}";

    private final String dropTable = "drop table IF EXISTS {0}";

    @Override
    public RespResult<SqlExecRespDto> createDatabase(String username, String database) {
        if (StringUtils.isBlank(database)) {
            return RespResult.buildFailWithMsg("database 为空");
        }

        try {
            String sql = MessageFormat.format(createDatabase, database);
            List<SqlExecRespDto> result = sparkExecService.batchExecuteUpdate(Lists.newArrayList(sql));
            return wrapExecuteResult(result);
        } catch (Exception e) {
            log.error("createDatabase 执行异常:{}",e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }

    @Override
    public RespResult<SqlExecRespDto> dropDatabase(String username, String database) {
        if (StringUtils.isBlank(database)) {
            return RespResult.buildFailWithMsg("database 为空");
        }

        try {
            String sql = MessageFormat.format(dropDatabase, database);
            List<SqlExecRespDto> result = sparkExecService.batchExecuteUpdate(Lists.newArrayList(sql));
            return wrapExecuteResult(result);
        } catch (Exception e) {
            log.error("dropDatabase 执行异常:{}",e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }

    @Override
    public RespResult<SqlExecRespDto> createTable(String username, HiveTable createTable) {
        try {
            if (StringUtils.isEmpty(createTable.getDatabase())) {
                return RespResult.buildFailWithMsg("database属性未定义");
            }

            StringBuffer sb = new StringBuffer(
                    "create table  " + createTable.getDatabase() + "." + createTable.getTableName() + " (");
            // 列定义
            HiveColumn[] columns = createTable.getColumns();
            List<HiveColumn> partitions = Lists.newArrayList();
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].getPartitionOrder() > 0) {
                    // 分区列放到分区定义中
                    partitions.add(columns[i]);
                } else {
                    sb.append(columns[i].getColumnName() + " " + columns[i].getDataType().name());
                    if (!StringUtils.isEmpty(columns[i].getComment())) {
                        // 定义列的说明
                        sb.append(" COMMENT '" + columns[i].getComment() + "'");
                    }
                    sb.append(",	");
                }
            }
            //删除最后一个逗号
            if (sb.lastIndexOf(",") > -1) {
                sb.deleteCharAt(sb.lastIndexOf(","));
            }
            sb.append(")		");
            // 添加表说明
            if (!StringUtils.isEmpty(createTable.getComment())) {
                sb.append(" COMMENT '" + createTable.getComment() + "'	");
            }
            // 分区定义
            if (!partitions.isEmpty()) {
                sb.append("PARTITIONED BY(");
                Collections.sort(partitions, new HiveColumnComparator());
                for (int i = 0; i < partitions.size(); i++) {
                    sb.append(partitions.get(i).getColumnName() + " " + partitions.get(i).getDataType().name());
                    if (i != partitions.size() - 1) {
                        // 每一列定义结束，添加逗号
                        sb.append(",");
                    }
                }
                sb.append(")		");
            }
            // 根据存储格式定义分隔符
            if (createTable.getStoredType().equals(StoredType.TEXTFILE)) {
                sb.append("ROW FORMAT DELIMITED FIELDS TERMINATED BY '"
                        + terminatedCharToString(createTable.getFieldsTerminated()) + "' 	");
                sb.append("LINES TERMINATED BY '" + terminatedCharToString(createTable.getLinesTerminated()) + "' ");
            } else {
                sb.append("STORED AS " + createTable.getStoredType().name());
            }

            log.info("create table commands:{}", sb.toString());
            List<String> commands = Lists.newArrayList(sb.toString());
            List<SqlExecRespDto> result = sparkExecService.batchExecuteUpdate(commands);
            return wrapExecuteResult(result);
        } catch (Exception e) {
            log.error("createTable 执行异常:{}",e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }


    @Override
    public RespResult<SqlExecRespDto> dropTable(String username, String database, String tableName, boolean bForced) {
        try {
            /**
             * 是否强制删除
             * 否：查询删除表中是否存在数据。有则提示错误
             * 是：直接删除
             */
            if (!bForced) {
                String queryRecords = "select count(1) from " + tableName;
                SqlQueryRespDto sr = sparkExecService.executeQuery(database, queryRecords);
                if (CollectionUtils.isNotEmpty(sr.getData())) {
                    long cnt = sr.getData().get(0).values().stream().filter(rts -> Integer.parseInt(rts + "") > 0).count();
                    if (cnt > 0L) {
                        return RespResult.buildFailWithMsg("records already exists in the table");
                    }
                }
            }

            List<String> commands = new ArrayList<String>();
            commands.add(MessageFormat.format(userDatabase, database));
            commands.add(MessageFormat.format(dropTable, tableName));
            List<SqlExecRespDto> result = sparkExecService.batchExecuteUpdate(commands);
            return wrapExecuteResult(result);
        } catch (Exception e) {
            log.error("dropTable 执行异常:{}",e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }


    private String terminatedCharToString(char c) {
        String t;
        switch (c) {
            case '\n':
                t = "\\n";
                break;
            case '\t':
                t = "\\t";
                break;
            case '\r':
                t = "\\r";
                break;
            default:
                t = String.valueOf(c);
                break;
        }
        return t;
    }


}

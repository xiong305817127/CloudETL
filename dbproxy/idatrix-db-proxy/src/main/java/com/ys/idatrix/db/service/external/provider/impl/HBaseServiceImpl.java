package com.ys.idatrix.db.service.external.provider.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.hbase.dto.HBaseColumn;
import com.ys.idatrix.db.api.hbase.dto.HBaseTable;
import com.ys.idatrix.db.api.hbase.service.HBaseService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.core.hbase.PhoenixExecService;
import com.ys.idatrix.db.service.external.provider.base.DbServiceAware;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;


/**
 * @ClassName: HBaseServiceImpl
 * @Description:
 * @Author: ZhouJian
 * @Date: 2017/10/31
 */
@Slf4j
@Service(protocol = "dubbo", timeout = 60000, interfaceClass = HBaseService.class)
@Component
public class HBaseServiceImpl extends DbServiceAware implements HBaseService {

    @Autowired(required = false)
    private PhoenixExecService phoenixExecService;

    private final String createSchema = "create schema IF NOT EXISTS {0}";

    private final String dropSchema = "drop schema IF EXISTS {0}";

    private final String dropTable = "drop table IF EXISTS {0}";

    private final String createIndex = "CREATE INDEX IF NOT EXISTS {0} ON {1}({2})";


    @Override
    public RespResult<SqlExecRespDto> createNamespace(String username, String namespace) {
        if (StringUtils.isBlank(namespace)) {
            return RespResult.buildFailWithMsg("namespace 为空");
        }

        try {
            String sql = MessageFormat.format(createSchema, namespace);
            List<SqlExecRespDto> result = phoenixExecService.batchExecuteUpdate(sql);
            return wrapExecuteResult(result);
        } catch (Exception e) {
            log.error("createNamespace 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }


    @Override
    public RespResult<SqlExecRespDto> dropNamespace(String username, String namespace) {
        if (StringUtils.isBlank(namespace)) {
            return RespResult.buildFailWithMsg("namespace 为空");
        }

        try {
            String sql = MessageFormat.format(dropSchema, namespace);
            List<SqlExecRespDto> result = phoenixExecService.batchExecuteUpdate(sql);
            return wrapExecuteResult(result);
        } catch (Exception e) {
            log.error("dropNamespace 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }


    @Override
    public RespResult<SqlExecRespDto> createTable(String username, HBaseTable createTable) {
        try {
            StringBuffer sb = new StringBuffer("create table  ");
            // schema.tablename
            if (StringUtils.isBlank(createTable.getTableName())) {
                return RespResult.buildFailWithMsg("表名称为空");
            }

            if (StringUtils.isBlank(createTable.getNamespace())) {
                sb.append(createTable.getTableName());
            } else {
                sb.append(createTable.getNamespace() + "." + createTable.getTableName());
            }

            if (null == createTable.getPrimaryKey()) {
                return RespResult.buildFailWithMsg("主键未定义");
            }

            // 提取主键
            Set<HBaseColumn> pkSet = createTable.getPrimaryKey().getColumns();

            createTable.getPrimaryKey().getColumns();
            sb.append("(");
            for (HBaseColumn column : createTable.getColumns()) {
                String cf = column.getColumnFamily();
                String col = column.getColumnName();
                String dataType = column.getDataType().name();
                if (!StringUtils.isEmpty(cf)) {
                    sb.append(cf + ".");
                }
                sb.append(col + " " + dataType);
                // 检查主键约束
                if (pkSet.contains(column)) {
                    if (!StringUtils.isEmpty(cf)) {
                        return RespResult.buildFailWithMsg("主键字段" + cf + "." + col + "不可以包含列族属性：" + cf);
                    }
                    sb.append(" not null");
                }
                sb.append(",  ");
            }

            // 主键
            sb.append("CONSTRAINT " + createTable.getPrimaryKey().getKeyName() + " PRIMARY KEY (");
            String pk = "";
            for (HBaseColumn column : pkSet) {
                String cf = column.getColumnFamily();
                String col = column.getColumnName();
                if (!StringUtils.isEmpty(cf)) {
                    return RespResult.buildFailWithMsg("主键字段" + cf + "." + col + "不可以包含列族属性：" + cf);
                }
                pk = pk + col + ",";
            }
            sb.append(pk.substring(0, pk.length() - 1) + ") ) ");
            // 列编码方式
            sb.append(" COLUMN_ENCODED_BYTES=" + createTable.getColumnEncodedBytes().getValue());
            // 是否一次写入不再修改
            sb.append(" ,IMMUTABLE_ROWS=" + createTable.isImmutableRows());
            // 历史数据保存版本数
            sb.append(" ,VERSIONS=" + createTable.getVersion());
            log.info("创建表语句:" + sb.toString());

            // 执行sql
            List<SqlExecRespDto> result = phoenixExecService.batchExecuteUpdate(sb.toString());
            return wrapExecuteResult(result);
        } catch (Exception e) {
            log.error("createTable 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }


    @Override
    public RespResult<SqlExecRespDto> dropTable(String username, String namespace, String tableName, boolean bForced) {
        try {
            String fullName = StringUtils.isEmpty(namespace) ? tableName : namespace + "." + tableName;
            /**
             * 是否强制删除
             * 否：查询删除表中是否存在数据。有则提示错误
             * 是：直接删除
             */
            if (!bForced) {
                String queryRecords = "select count(1) from " + fullName;
                SqlQueryRespDto sr = phoenixExecService.executeQuery(queryRecords);
                if (CollectionUtils.isNotEmpty(sr.getData())) {
                    long cnt = sr.getData().get(0).values().stream().filter(rts -> Integer.parseInt(rts + "") > 0).count();
                    if (cnt > 0L) {
                        return RespResult.buildFailWithMsg("records already exists in the table");
                    }
                }
            }
            String sql = MessageFormat.format(dropTable, fullName);
            List<SqlExecRespDto> result = phoenixExecService.batchExecuteUpdate(sql);
            return wrapExecuteResult(result);
        } catch (Exception e) {
            log.error("dropTable 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }


    @Override
    public RespResult<SqlExecRespDto> alterTable(String username, HBaseTable oldTable, HBaseTable newTable) {
        try {
            String fullName = StringUtils.isEmpty(oldTable.getNamespace()) ? oldTable.getTableName()
                    : oldTable.getNamespace() + "." + oldTable.getTableName();
            // 比较新老版本，识别需要add和drop的column
            HBaseColumn[] newTables = newTable.getColumns();
            HBaseColumn[] oldTables = oldTable.getColumns();
            List<HBaseColumn> addList = Lists.newArrayList();
            List<HBaseColumn> dropList = Lists.newArrayList();
            {// 识别新增字段
                for (int i = 0; i < newTables.length; i++) {
                    HBaseColumn newVersion = newTables[i];
                    boolean isNew = true;
                    for (int j = 0; j < oldTables.length; j++) {
                        HBaseColumn oldVersion = oldTables[j];
                        if (newVersion.equals(oldVersion)) {
                            // 列族和列都相等，是已存在的列
                            isNew = false;
                            break;
                        }
                    }
                    if (isNew) {
                        addList.add(newVersion);
                    }
                }
            }
            {// 识别删除字段
                for (int i = 0; i < oldTables.length; i++) {
                    HBaseColumn oldVersion = oldTables[i];
                    boolean isDrop = true;
                    for (int j = 0; j < newTables.length; j++) {
                        HBaseColumn newVersion = newTables[j];
                        if (newVersion.equals(oldVersion)) {
                            // 是已存在的列
                            isDrop = false;
                            break;
                        }
                    }
                    if (isDrop) {
                        dropList.add(oldVersion);
                    }
                }
            }
            // 拼装执行语句
            List<String> commands = Lists.newArrayList();
            {// drop语句
                if (!dropList.isEmpty()) {
                    StringBuffer dropSql = new StringBuffer("alter table " + fullName + " drop column ");
                    for (HBaseColumn hBaseColumn : dropList) {
                        String cf = hBaseColumn.getColumnFamily();
                        String col = hBaseColumn.getColumnName();
                        if (!StringUtils.isEmpty(cf)) {
                            dropSql.append(cf + "." + col);
                        } else {
                            dropSql.append(col);
                        }
                        dropSql.append(",");
                    }
                    commands.add(dropSql.substring(0, dropSql.length() - 1));
                }
            }

            {// add语句
                if (!addList.isEmpty()) {
                    StringBuffer addSql = new StringBuffer("alter table " + fullName + " add ");
                    for (HBaseColumn hBaseColumn : addList) {
                        String cf = hBaseColumn.getColumnFamily();
                        String col = hBaseColumn.getColumnName();
                        String dataType = hBaseColumn.getDataType().name();
                        if (!StringUtils.isEmpty(cf)) {
                            addSql.append(cf + "." + col);
                        } else {
                            addSql.append(col);
                        }
                        addSql.append(" " + dataType + ",");
                    }
                    commands.add(addSql.substring(0, addSql.length() - 1));
                }
            }
            {// set语句
                boolean hasChange = false;
                StringBuffer setSql = new StringBuffer("alter table " + fullName + " set ");
                if (newTable.isImmutableRows() != oldTable.isImmutableRows()) {
                    setSql.append("IMMUTABLE_ROWS=" + newTable.isImmutableRows() + ",");
                    hasChange = true;
                }
                if (!newTable.getColumnEncodedBytes().equals(oldTable.getColumnEncodedBytes())) {
                    setSql.append(" COLUMN_ENCODED_BYTES=" + newTable.getColumnEncodedBytes().getValue() + ",");
                    hasChange = true;
                }
                if (newTable.getVersion() != oldTable.getVersion()) {
                    setSql.append(" VERSIONS=" + newTable.getVersion() + ",");
                    hasChange = true;
                }
                if (hasChange) {
                    commands.add(setSql.substring(0, setSql.length() - 1));
                }
            }

            // modified by zhoujian on 2018-07-07
            // 修改原因：元数据修改表时，如果没有任何变动，db-proxy 返回"success=false,message=update sql commands is null",
            // 而元数据没有对错误信息进行判断，只是根据标识判断了。所有主动加上判断，然后返回"success=true,message=unchanged:no sql commands"
            if (CollectionUtils.isEmpty(commands)) {
                log.warn("unchanged:no sql commands");
                return RespResult.buildSuccessWithMsg("unchanged:no sql commands");
            }

            List<SqlExecRespDto> result = phoenixExecService.batchExecuteUpdate(commands.toArray(new String[0]));
            return wrapExecuteResult(result);

        } catch (Exception e) {
            log.error("alterTable 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }


    @Override
    public RespResult<SqlExecRespDto> addColumn(String username, String namespace, String tableName, List<HBaseColumn> columns) {
        try {
            String fullName = StringUtils.isEmpty(namespace) ? tableName : namespace + "." + tableName;
            String sql = "alter table " + fullName + " ADD ";

            for (HBaseColumn column : columns) {
                String cf = column.getColumnFamily();
                String col = column.getColumnName();
                String dataType = column.getDataType().name();
                if (!StringUtils.isEmpty(cf)) {
                    sql = sql + cf + "." + col;
                } else {
                    sql = sql + col;
                }
                sql = sql + " " + dataType + ",";
            }

            List<SqlExecRespDto> result = phoenixExecService.batchExecuteUpdate(sql.substring(0, sql.length() - 1));
            log.info("addColumn -> result:{}", JSONObject.toJSONString(result, true));
            return wrapExecuteResult(result);
        } catch (Exception e) {
            log.error("addColumn 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }


    @Override
    public RespResult<SqlExecRespDto> dropColumn(String username, String namespace, String tableName, List<String> columnNames) {
        try {
            String fullName = StringUtils.isEmpty(namespace) ? tableName : namespace + "." + tableName;
            String sql = "alter table " + fullName + " DROP COLUMN ";
            for (String name : columnNames) {
                sql = sql + name + ",";
            }
            List<SqlExecRespDto> result = phoenixExecService.batchExecuteUpdate(sql.substring(0, sql.length() - 1));
            return wrapExecuteResult(result);
        } catch (Exception e) {
            log.error("dropColumn 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }


    @Override
    public RespResult<SqlExecRespDto> createIndex(String username, String indexName, String namespace, String
            tableName, List<String> columnNames) {
        try {
            String fullName = StringUtils.isEmpty(namespace) ? tableName : namespace + "." + tableName;

            StringBuffer indexCols = new StringBuffer();
            for (String columnName : columnNames) {
                indexCols.append(columnName).append(",");
            }
            if (StringUtils.isNotEmpty(indexCols.toString())) {
                indexCols.deleteCharAt(indexCols.lastIndexOf(","));
            }
            String indexCommand = MessageFormat.format(createIndex, indexName, fullName, indexCols);
            List<SqlExecRespDto> result = phoenixExecService.batchExecuteUpdate(indexCommand);
            return wrapExecuteResult(result);
        } catch (Exception e) {
            log.error("createIndex 执行异常:{}", e.getMessage());
            return wrapExecuteResultWithException(e);
        }
    }

}

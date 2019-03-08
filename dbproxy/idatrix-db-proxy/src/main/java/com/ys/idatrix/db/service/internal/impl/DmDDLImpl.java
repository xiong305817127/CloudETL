package com.ys.idatrix.db.service.internal.impl;

import com.google.common.collect.ImmutableList;
import com.ys.idatrix.db.api.rdb.dto.RdbColumn;
import com.ys.idatrix.db.api.rdb.dto.RdbEnum;
import com.ys.idatrix.db.api.rdb.dto.RdbIndex;
import com.ys.idatrix.db.api.rdb.dto.RdbPrimaryKey;
import com.ys.idatrix.db.exception.DbProxyException;
import com.ys.idatrix.db.service.internal.RdbDDLWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: DmDDLImpl
 * @Description:
 * @Author: ZhouJian
 * @Date: 2018/7/30
 */
@Slf4j
@Component
public class DmDDLImpl extends RdbDDLWrapper {

    /**
     * 删除表
     */
    private final String DROP_TB_SQL = "DROP TABLE {0}";

    /**
     * 删除主键
     */
    private final String DROP_PK_SQL = "ALTER TABLE {0} DROP CONSTRAINT {1}";

    /**
     * 添加主键
     */
    private final String ADD_PK_SQL = "ALTER TABLE {0} add CONSTRAINT {1} PRIMARY KEY({2})";

    /**
     * 修改表注释
     */
    private final String ALTER_TB_COMMENT_SQL = "COMMENT ON TABLE {0} IS {1}{2}{3}";


    @Override
    public RdbEnum.DBType getDBType() {
        return RdbEnum.DBType.DM7;
    }


    @Override
    public List<String> getDropTableCommands(String tableName) {
        String dropCommand = MessageFormat.format(DROP_TB_SQL, tableName);
        return ImmutableList.of(dropCommand);
    }


    @Override
    protected List<String> getAddColumnCommands(String tableName, RdbColumn rc) {
        return null;
    }


    @Override
    protected List<String> getModifyColumnCommands(String tableName, RdbColumn rc) {
        return null;
    }


    @Override
    protected String getDropColumnCommand(String tableName, String columnName) {
        return null;
    }


    @Override
    protected String getRenameColumnCommand(String tableName, String oldColumnName, RdbColumn rc) {
        return null;
    }


    /**
     * 创建索引sql
     *
     * @param tableName
     * @param indices
     * @param columns
     * @param otherParams 其它参数 DM 忽略
     * @return
     * @throws Exception
     */
    @Override
    protected List<String> getCreateIndexCommands(String tableName, ArrayList<RdbIndex> indices, List<RdbColumn> columns, Object... otherParams) {
        if (CollectionUtils.isEmpty(indices)) {
            return null;
        }

        ArrayList<String> commands = new ArrayList<>();

        //列名
        List<String> columnNames = columns.stream().map(column -> column.getColumnName()).collect(Collectors.toList());

        for (RdbIndex index : indices) {

            if (StringUtils.isBlank(index.getName())) {
                throw new DbProxyException("create index, index name is null ");
            }

            if (CollectionUtils.isEmpty(index.getColumns())) {
                throw new DbProxyException("create index, index columns is null ");
            }

            //验证并获取索引类型
            String indexType = getAndVerifiedIndexType(index.getIndexType());

            StringBuilder sb = new StringBuilder();
            sb.append("CREATE ");

            //索引名称处理
            switch (RdbEnum.OracleIndexType.valueOf(index.getIndexType())) {
                // 普通索引 e.g: create index idx_ename on emp(ename);
                case NORMAL:
                    // 唯一索引 e.g: create unique index idx_unique_dname on dept(dname);
                    // 位图索引 e.g: create bitmap index idx_bmjob on emp(job);
                case UNIQUE:
                case BITMAP:
                    sb.append(indexType).append(" INDEX ").append(index.getName()).append(" ON ").append(tableName);
                    break;
                default:
                    throw new DbProxyException("create index, DM not apply [" + indexType + "] index type");
            }

            //索引列处理
            sb.append(" (");
            for (String column : index.getColumns()) {
                if (StringUtils.isNotBlank(column)) {
                    // 判断索引列是否在表列中
                    if (!columnNames.contains(column)) {
                        throw new DbProxyException("the index column:" + index.getName() + " is not included in the table column");
                    }

                    sb.append(column).append(" , ");
                }
            }
            // 删除最后一个逗号
            if (sb.lastIndexOf(SEPARATOR) > -1) {
                sb.deleteCharAt(sb.lastIndexOf(SEPARATOR));
            }
            sb.append(")");
            commands.add(sb.toString());
        }

        log.info("create index name,sql is : {}", commands);

        return commands;
    }


    @Override
    protected List<String> getDropIndexCommands(String tableName, ArrayList<RdbIndex> indices) {
        return null;
    }


    @Override
    public String getSelectUserCommand(String user) {
        return null;
    }


    @Override
    protected List<String> getAlterPKCommands(String tableName, RdbPrimaryKey oldPrimaryKey, RdbPrimaryKey newPrimaryKey) {
        ArrayList<String> commands = new ArrayList<>();
        if (null != oldPrimaryKey) {
            String oldPKName = oldPrimaryKey.getPrimaryKeyName();
            if (StringUtils.isBlank(oldPKName)) {
                throw new DbProxyException("dm 主键名必须赋值");
            }
            String dropPkSql = MessageFormat.format(DROP_PK_SQL, tableName, oldPKName);
            commands.add(dropPkSql);
        }

        if (null != newPrimaryKey) {
            StringBuilder pk_sb = new StringBuilder();
            for (RdbColumn pkColumn : newPrimaryKey.getPrimaryKeys()) {
                pk_sb.append(pkColumn.getColumnName()).append(",");
            }
            // 删除最后一个逗号
            if (pk_sb.lastIndexOf(SEPARATOR) > -1) {
                pk_sb.deleteCharAt(pk_sb.lastIndexOf(SEPARATOR));
            }
            String addPkSql = MessageFormat.format(ADD_PK_SQL, tableName, newPrimaryKey.getPrimaryKeyName(), pk_sb.toString());
            commands.add(addPkSql);
        }
        return commands;
    }


    @Override
    protected String getAlterTableCommentSql() {
        return ALTER_TB_COMMENT_SQL;
    }
}

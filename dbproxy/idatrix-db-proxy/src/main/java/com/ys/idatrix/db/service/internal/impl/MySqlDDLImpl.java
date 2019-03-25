package com.ys.idatrix.db.service.internal.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ys.idatrix.db.api.rdb.dto.*;
import com.ys.idatrix.db.exception.DbProxyException;
import com.ys.idatrix.db.service.internal.RdbDDLWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MySqlDDLImpl
 * @Description:
 * @Author: ZhouJian
 * @Date: 2017/12/25
 */
@Slf4j
@Component
public class MySqlDDLImpl extends RdbDDLWrapper {

    /**
     * 建库
     */
    private final String CREATE_DB_SQL = "create database `{0}` default character set utf8 collate utf8_general_ci";

    /**
     * 创建用户
     */
    private final String CREATE_USER_SQL = "create user {0}{1}{2}@{3}%{4} identified by {5}{6}{7}";

    /**
     * 删除用户
     */
    private final String DROP_USER_SQL = "drop user {0}{1}{2}";

    /**
     * 用户赋权
     */
    //private final String GRANT_SQL = "grant select,insert,update,delete,create,alter,index,drop on `{0}`.* to {1}{2}{3}@{4}%{5} with grant option";
    private final String GRANT_SQL = "grant all on `{0}`.* to {1}{2}{3}@{4}%{5} with grant option";

    /**
     * 查询用户
     */
    private final String SELECT_USER_SQL = "select user from mysql.user where user = {0}{1}{2}";


    /**
     * 查库
     */
    private final String SELECT_DB_SQL = "SELECT COUNT(1) as cnt FROM information_schema.schemata WHERE schema_name = {0}{1}{2}";

    /**
     * 删库
     */
    private final String DROP_DB_SQL = "drop database if exists `{0}`";

    /**
     * 删表
     */
    private final String DROP_TB_SQL = "drop table if exists {0}";

    /**
     * 删列
     */
    private final String DROP_COL_SQL = "alter table {0} drop `{1}`";

    /**
     * 删索引
     */
    private final String DROP_IDX_SQL = "drop index `{0}` on {1}";

    /**
     * 删主键
     */
    private final String DROP_PK_SQL = "alter table {0} drop primary key";

    /**
     * 新增主键
     */
    private final String ADD_PK_SQL = "alter table {0} add primary key({1})";

    /**
     * 修改表注释
     */
    private final String ALTER_TB_COMMENT_SQL = "alter table {0} comment {1}{2}{3}";


    @Override
    public RdbEnum.DBType getDBType() {
        return RdbEnum.DBType.MYSQL;
    }


    @Override
    public List<String> getCreateDatabaseCommands(RdbCreateDatabase database) {
        List<String> commands = Lists.newArrayList(MessageFormat.format(CREATE_DB_SQL, database.getDatabase()));
        //验证用户是否已经创建。是-跳过创建用户，否-创建新用户
        if (!database.isUserReusing()) {
            commands.add(MessageFormat.format(CREATE_USER_SQL,
                    QUOTATION, database.getUserName(), QUOTATION,
                    QUOTATION, QUOTATION,
                    QUOTATION, database.getPassword(), QUOTATION));
        }
        commands.add(MessageFormat.format(GRANT_SQL,
                database.getDatabase(),
                QUOTATION, database.getUserName(), QUOTATION,
                QUOTATION, QUOTATION));
        commands.add("flush  privileges");
        return commands;
    }


    @Override
    public List<String> getDropDatabaseCommands(RdbDropDatabase database) {
        //删除用户
        String dropUser = MessageFormat.format(DROP_USER_SQL, QUOTATION, database.getUserName(), QUOTATION);
        //删除库
        String dropCommand = MessageFormat.format(DROP_DB_SQL, database.getDatabase());
        return ImmutableList.of(dropUser,dropCommand);
    }


    @Override
    public List<String> getCreateTableCommands(RdbCreateTable rct){

        // 取出所有列
        ArrayList<RdbColumn> rdbColumns = rct.getRdbColumns();
        if (CollectionUtils.isEmpty(rdbColumns)) {
            throw new DbProxyException("create table, columns is null");
        }

        // 用来存放 主键SQL
        StringBuilder sbPK = null;

        // 取出主键
        RdbPrimaryKey primaryKey = rct.getPrimaryKey();
        if (null != primaryKey) {

            // 主键的集合
            List<RdbColumn> primaryKeys = primaryKey.getPrimaryKeys();

            // 用来存放primary key
            sbPK = new StringBuilder();

            if (CollectionUtils.isEmpty(primaryKeys)) {
                throw new DbProxyException("create table, primaryKeys exception");
            } else {
                sbPK.append(" primary key ( ");
            }

            for (RdbColumn pks : primaryKey.getPrimaryKeys()) {
                sbPK.append(addBackQuote(pks.getColumnName())).append(SEPARATOR);
            }

            //删除最后一个逗号
            if (sbPK.lastIndexOf(SEPARATOR) > -1) {
                sbPK.deleteCharAt(sbPK.lastIndexOf(SEPARATOR));
            }
            sbPK.append(")");
        }

        StringBuilder sb = new StringBuilder("CREATE TABLE ");

        sb.append(addBackQuote(rct.getTableName())).append(" ( ");

        for (RdbColumn RdbColumn : rdbColumns) {

            // 列名
            String columnName = RdbColumn.getColumnName();

            // 类型
            String dataType = RdbColumn.getDataType();

            // 类型范围
            String columnChamp = RdbColumn.getColumnChamp();

            // 判断列名是否为空
            if (StringUtils.isBlank(columnName)) {
                throw new DbProxyException("create table, columnName is null or \" \"" + " " + sb.toString());
            }

            sb.append(addBackQuote(columnName) + " ")
                    .append(getAndVerifiedDataType(dataType) + " ");

            // 是否有类型范围限制,有就加上,没有就不加
            if (StringUtils.isNotBlank(columnChamp)) {
                sb.append("(" + columnChamp + ") ");
            }

            defineColumnSyntax(sb, RdbColumn);

            sb.append(SEPARATOR);
        }

        //拼装主键
        assembledPK(sb, sbPK);

        // 添加表注释
        if (StringUtils.isNotBlank(rct.getComment())) {
            sb.append(" COMMENT='").append(rct.getComment()).append("' ");
        }

        sb.append("ENGINE=" + rct.getMysqlEngineType().name());
        sb.append(" DEFAULT CHARSET=" + rct.getCharSet());

        List<String> commands = Lists.newArrayList(sb.toString());

        // 创建索引sql
        List<String> createIndexCommands = getCreateIndexCommands(rct.getTableName(), rct.getIndices(), rdbColumns, null);
        if (CollectionUtils.isNotEmpty(createIndexCommands)) {
            commands.addAll(createIndexCommands);
        }
        return commands;
    }


    @Override
    public List<String> getDropTableCommands(String tableName) {
        String dropCommand = MessageFormat.format(DROP_TB_SQL, addBackQuote(tableName));
        return ImmutableList.of(dropCommand);
    }


    @Override
    public String getSelectUserCommand(String user) {
        String selectUserSql = MessageFormat.format(SELECT_USER_SQL, QUOTATION, user, QUOTATION);
        return selectUserSql;
    }


    @Override
    public String getSelectDbNameCommand(String DbName) {
        String selectDbNameSql = MessageFormat.format(SELECT_DB_SQL, QUOTATION, DbName, QUOTATION);
        return selectDbNameSql;
    }


    /**
     * 删除列sql
     *
     * @param tableName
     * @param columnName
     * @return
     * @throws Exception
     */
    @Override
    protected String getDropColumnCommand(String tableName, String columnName) {
        String commands = MessageFormat.format(DROP_COL_SQL, addBackQuote(tableName), columnName);
        log.info("drop column,sql is : {}", commands);
        return commands;
    }


    /**
     * 新增列sql
     *
     * @param tableName
     * @param rc
     * @return
     * @throws Exception
     */
    @Override
    protected List<String> getAddColumnCommands(String tableName, RdbColumn rc) {
        // 列名
        String columnName = rc.getColumnName();
        // 类型
        String dataType = rc.getDataType();
        // 类型范围
        String columnChamp = rc.getColumnChamp();

        StringBuilder sb = new StringBuilder("ALTER TABLE ");

        // 判断列表名是否为空
        if (StringUtils.isBlank(tableName)) {
            throw new DbProxyException("add Column, tableName is null");
        }

        sb.append(addBackQuote(tableName) + " ADD COLUMN ");

        // 判断列名是否为空
        if (StringUtils.isBlank(columnName)) {
            throw new DbProxyException("add Column, columnName is null");
        }

        sb.append(addBackQuote(columnName))
                .append(" ")
                .append(getAndVerifiedDataType(dataType) + " ");

        // 是否有类型范围限制,有就加上,没有就不加
        if (StringUtils.isNotBlank(columnChamp)) {
            sb.append("(" + columnChamp + ") ");
        }

        defineColumnSyntax(sb, rc);

        log.info("mysql add column,sql is : {}", sb.toString());

        return ImmutableList.of(sb.toString());
    }


    /**
     * 修改一列sql
     *
     * @param tableName
     * @param rc
     * @return
     * @throws Exception
     */
    @Override
    protected List<String> getModifyColumnCommands(String tableName, RdbColumn rc) {
        // 列名
        String columnName = rc.getColumnName();
        // 类型
        String dataType = rc.getDataType();
        // 类型范围
        String columnChamp = rc.getColumnChamp();

        StringBuilder sb = new StringBuilder("ALTER TABLE ");

        // 判断列表名是否为空
        if (StringUtils.isEmpty(tableName)) {
            throw new DbProxyException("modify Column, tableName is null");
        }

        // 判断列名是否为空
        if (StringUtils.isEmpty(columnName)) {
            throw new DbProxyException("modify Column, columnName is null");
        }

        sb.append(addBackQuote(tableName))
                .append(" MODIFY ")
                .append(addBackQuote(columnName))
                .append(" ")
                .append(getAndVerifiedDataType(dataType) + " ");

        // 是否有类型范围限制,有就加上,没有就不加
        if (null != columnChamp) {
            sb.append("(" + columnChamp + ") ");
        }

        defineColumnSyntax(sb, rc);

        log.info("modify column,sql is : {}", sb.toString());

        return Lists.newArrayList(sb.toString());
    }


    /**
     * 重命名列
     *
     * @param tableName
     * @param oldColumnName
     * @param rc
     * @return
     * @throws Exception
     */
    @Override
    protected String getRenameColumnCommand(String tableName, String oldColumnName, RdbColumn rc) {
        // 当为mysql时候必须指定新列的类型
        String dataType = rc.getDataType();

        StringBuilder sb = new StringBuilder("ALTER TABLE ");

        sb.append(addBackQuote(tableName)).append(" ");

        sb.append("CHANGE ").append(addBackQuote(oldColumnName)).append(" ").append(addBackQuote(rc.getColumnName()));
        if (StringUtils.isEmpty(rc.getColumnChamp())) {
            sb.append(" ").append(getAndVerifiedDataType(dataType));
        } else {
            sb.append(" ").append(getAndVerifiedDataType(dataType)).append(" (").append(rc.getColumnChamp()).append(" )");
        }

        defineColumnSyntax(sb, rc);

        log.info("rename column name,sql is : {}", sb.toString());

        return sb.toString();
    }


    /**
     * 创建索引sql
     *
     * @param tableName
     * @param indices
     * @param columns
     * @return
     * @throws Exception
     */
    @Override
    protected List<String> getCreateIndexCommands(String tableName, ArrayList<RdbIndex> indices, List<RdbColumn> columns, Object... otherParams) {
        if (CollectionUtils.isEmpty(indices)) {
            return null;
        }

        // 需要被执行commands的集合
        ArrayList<String> commands = new ArrayList<>();

        // 列名
        Map<String, RdbColumn> columnMap = columns.stream().collect(Collectors.toMap((key -> key.getColumnName()), (value -> value)));

        for (RdbIndex index : indices) {

            if (StringUtils.isBlank(index.getName())) {
                throw new DbProxyException("create index, index name is null ");
            }

            if (CollectionUtils.isEmpty(index.getColumns())) {
                throw new DbProxyException("create index, index columns is null ");
            }

            // 验证并获取索引类型
            String indexType = getAndVerifiedIndexType(index.getIndexType());

            StringBuilder sb = new StringBuilder();
            sb.append("ALTER TABLE " + addBackQuote(tableName) + " ADD ");

            //索引名称处理
            switch (RdbEnum.MysqlIndexType.valueOf(index.getIndexType())) {
                // 普通索引、唯一索引
                case NORMAL:
                case UNIQUE:
                    sb.append(indexType).append(" ").append(addBackQuote(index.getName()));
                    break;
                //全文索引
                case FULLTEXT:
                    //全文索引 不支持innodb
                    if (null != otherParams && otherParams[0] == RdbEnum.MysqlEngineType.INNODB) {
                        throw new DbProxyException("存储引擎 INNODB 不支持索引类型 FullText");
                    }
                    sb.append(indexType).append(" ").append(addBackQuote(index.getName()));
                    break;
                default:
                    throw new DbProxyException("create index, MYSQL not apply [" + indexType + "] index type");

            }

            //索引列处理
            sb.append(" (");
            for (String column : index.getColumns()) {
                if (StringUtils.isNotBlank(column)) {
                    // 判断索引列是否在表列中
                    if (!columnMap.containsKey(column)) {
                        throw new DbProxyException("the index column:" + index.getName() + " is not included in the table column");
                    }

                    //FULLTEXT is not support some data type
                    if (RdbEnum.MysqlIndexType.FULLTEXT.getName().equals(indexType)) {
                        RdbColumn indexCol = columnMap.get(column);
                        if (!RdbEnum.MysqlDataType.CHAR.name().equals(indexCol.getDataType()) ||
                                !RdbEnum.MysqlDataType.VARCHAR.name().equals(indexCol.getDataType()) ||
                                !RdbEnum.MysqlDataType.TEXT.name().equals(indexCol.getDataType()) ||
                                !RdbEnum.MysqlDataType.MEDIUMTEXT.name().equals(indexCol.getDataType()) ||
                                !RdbEnum.MysqlDataType.LONGTEXT.name().equals(indexCol.getDataType())) {
                            throw new DbProxyException("the index type FULLTEXT is not support data type:" + indexCol.getDataType());
                        }
                    }
                    sb.append(addBackQuote(column)).append(" , ");
                }
            }

            //删除最后一个逗号
            if (sb.lastIndexOf(SEPARATOR) > -1) {
                sb.deleteCharAt(sb.lastIndexOf(SEPARATOR));
            }

            sb.append(")");

            //索引方法
            if (null != index.getMysqlIndexMethod()) {
                sb.append(" USING ").append(index.getMysqlIndexMethod().getName());
            }

            commands.add(sb.toString());
        }

        log.info("create index name,sql is : {}", commands);

        return commands;

    }

    /**
     * 删除索引
     *
     * @param tableName
     * @param indices
     * @return
     * @throws Exception
     */
    @Override
    protected List<String> getDropIndexCommands(String tableName, ArrayList<RdbIndex> indices) {
        ArrayList<String> commands = Lists.newArrayList();
        for (RdbIndex index : indices) {
            if (StringUtils.isBlank(index.getName())) {
                throw new DbProxyException("drop index name is null");
            }
            commands.add(MessageFormat.format(DROP_IDX_SQL, index.getName(), addBackQuote(tableName)));
        }
        log.info("drop index,sql is : {}", commands);
        return commands;
    }

    @Override
    protected List<String> getAlterPKCommands(String tableName, RdbPrimaryKey oldPrimaryKey, RdbPrimaryKey newPrimaryKey) {
        ArrayList<String> commands = Lists.newArrayList();
        if (null != oldPrimaryKey) {

            if (oldPrimaryKey.getPrimaryKeys().stream().anyMatch(oldPkColumn -> oldPkColumn.isHasAutoIncrement())) {
                throw new DbProxyException("表原主键包含自增列,不宜做主键修改");
            }

            String dropPkSql = MessageFormat.format(DROP_PK_SQL, addBackQuote(tableName));
            commands.add(dropPkSql);
        }

        if (null != newPrimaryKey) {
            StringBuilder pk_sb = new StringBuilder();
            for (RdbColumn pkColumn : newPrimaryKey.getPrimaryKeys()) {
                pk_sb.append(addBackQuote(pkColumn.getColumnName())).append(",");
            }
            // 删除最后一个逗号
            if (pk_sb.lastIndexOf(SEPARATOR) > -1) {
                pk_sb.deleteCharAt(pk_sb.lastIndexOf(SEPARATOR));
            }
            String addPkSql = MessageFormat.format(ADD_PK_SQL, addBackQuote(tableName), pk_sb.toString());
            commands.add(addPkSql);
        }
        return commands;
    }


    @Override
    protected String getAlterTableCommentSql() {
        return ALTER_TB_COMMENT_SQL;
    }

    /**
     * 定义列的语法
     * <p>
     * 数据类型为timestamp、datetime、date、time
     * 为了兼容mysql的不同版本，统一设置默认可为空，不设置 default value
     *
     * @param sb
     * @param rc
     */
    private void defineColumnSyntax(StringBuilder sb, RdbColumn rc) {
        switch (RdbEnum.MysqlDataType.valueOf(rc.getDataType())) {
            case TIMESTAMP:
                sb.append(" NULL ");
                break;
            case DATETIME:
            case TIME:
            case DATE:
            case YEAR:
                break;
            default:
                // 是否有默认值
                if (!StringUtils.isEmpty(rc.getDefaultValue())) {
                    sb.append(" DEFAULT '" + rc.getDefaultValue() + "' ");
                }

                // 判断是否为无符号数,当为oracle时 排除,oracle中没有UNSIGNED概念
                if (rc.isHasUnsigned()) {
                    sb.append(" UNSIGNED ");
                }

                // 判断是否为能为空
                if (rc.isHasNotNull()) {
                    sb.append(" NOT NULL ");
                }

                // 判断是否是自增,mysql可以使用AUTO_INCREMENT关键字,oracle特殊处理
                if (rc.isHasAutoIncrement()) {
                    sb.append(" AUTO_INCREMENT ");
                }
                break;
        }

        // 是否有注释
        if (StringUtils.isNotBlank(rc.getComment())) {
            sb.append(" COMMENT '" + rc.getComment() + "' ");
        }
    }

}

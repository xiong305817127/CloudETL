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
 * @ClassName: OracleDDLImpl
 * @Description:
 * @Author: ZhouJian
 * @Date: 2017/12/25
 */
@Slf4j
@Component
public class OracleDDLImpl extends RdbDDLWrapper {

    /**
     * 连接到oracle后查询当前oracle版本
     */
    private final String DB_VERSION_SQL = "SELECT VERSION FROM V$INSTANCE";

    /**
     * 创建表空间
     * 角色：DBA   sys、system
     * 数{0}-存储文件类型,{1}-表空间名称,{2}-文件名(可包含路径),{3}-初始化大小,{4}-开启自动拓展，下一个大小
     * <p>
     * e.g:
     * CREATE SMALLFILE TABLESPACE "zj_ns_1" DATAFILE 'zj_ns_1_df.dbf' SIZE 5242880 AUTOEXTEND ON NEXT 1048576 MAXSIZE 500M BLOCKSIZE 8192
     * LOGGING FORCE LOGGING DEFAULT COMPRESS OFFLINE EXTENT MANAGEMENT LOCAL AUTOALLOCATE SEGMENT SPACE MANAGEMENT AUTO FLASHBACK ON
     */
    private final String CREATE_TS_BASE_SQL = "CREATE {0} TABLESPACE {1} DATAFILE {2} SIZE {3} ";

    /**
     * 创建公共用户
     * ①数据库版本为12c之前的 没有此操作
     * ②数据库版本为12c之后（包含12c）用户名以c##或者C##开头即可
     * 参数{0} - 密码,{1} -默认表空间
     */
    private final String CREATE_COMMON_USER_SQL = "CREATE USER C##zhoujian IDENTIFIED BY {0} DEFAULT TABLESPACE {1} profile DEFAULT ACCOUNT UNLOCK";
    /**
     * 创建本地用户（12c与之前版本都一样）
     * 参数{0} - 密码,{1} -默认表空间
     */
    private final String CREATE_LOCAL_USER_SQL = "CREATE USER zhoujian IDENTIFIED BY {0} DEFAULT TABLESPACE {1} profile DEFAULT ACCOUNT UNLOCK";


    /**
     * 查看数据库里面所有用户
     * 角色：DBA   sys、system
     */
    private final String SELECT_USER_SQL = "SELECT * FROM DBA_USERS WHERE USER = {0}";
    //select * from all_users;  查看你能管理的所有用户！
    //select * from user_users; 查看当前用户信息 ！

    /**
     * 删除表
     */
    private final String DROP_TB_SQL = "DROP TABLE {0}";

    /**
     * 创建序列
     */
    private final String CREATE_SEQ_SQL = "CREATE SEQUENCE {0} INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE";

    /**
     * 删除序列
     */
    private final String DROP_SEQ_SQL = "DROP SEQUENCE {0}";

    /**
     * 改表 删除列
     */
    private final String DROP_COL_SQL = "ALTER TABLE {0} DROP COLUMN {1}";

    /**
     * 删除索引
     */
    private final String DROP_IDX_SQL = "DROP INDEX {0}";

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
        return RdbEnum.DBType.ORACLE;
    }


    @Override
    public List<String> getDropTableCommands(String tableName) {
        String dropCommand = MessageFormat.format(DROP_TB_SQL, tableName);
        return ImmutableList.of(dropCommand);
    }


    /**
     * example:
     * // e.g:
     * CREATE SEQUENCE seqTest
     * INCREMENT BY 1 -- 每次加几个
     * START WITH 1 -- 从1开始计数
     * NOMAXVALUE -- 不设置最大值
     * NOCYCLE -- 一直累加，不循环
     * CACHE 10;
     * // --设置缓存cache个序列，如果系统down掉了或者其它情况将会导致序列不连续，也可以设置为---------NOCACHE
     *
     * @return
     * @throws Exception
     */
    @Override
    public List<String> getCreateSequenceCommands(String seqName) {
        return ImmutableList.of(MessageFormat.format(CREATE_SEQ_SQL, seqName));
    }


    @Override
    public List<String> getDropSequenceCommands(String seqName) {
        return ImmutableList.of(MessageFormat.format(DROP_SEQ_SQL, seqName));
    }


    @Override
    public String getSelectUserCommand(String user) {
        String selectUserSql = MessageFormat.format(SELECT_USER_SQL, user);
        return selectUserSql;
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
        return MessageFormat.format(DROP_COL_SQL, tableName, columnName);
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
        // 注释
        String comment = rc.getComment();
        // 默认值
        String defaultValue = rc.getDefaultValue();

        StringBuilder sb = new StringBuilder("ALTER TABLE ");

        // 判断列表名是否为空
        if (StringUtils.isBlank(tableName)) {
            throw new DbProxyException("add Column, tableName is null");
        }

        sb.append(tableName + " ADD ( ");

        // 判断列名是否为空
        if (StringUtils.isBlank(columnName)) {
            throw new DbProxyException("add Column, columnName is null");
        }

        sb.append(columnName)
                .append(" ")
                .append(getAndVerifiedDataType(dataType) + " ");

        // 是否有类型范围限制,有就加上,没有就不加
        if (StringUtils.isNotBlank(columnChamp)) {
            sb.append("(" + columnChamp + ") ");
        }

        // 是否有默认值
        if (StringUtils.isNotEmpty(defaultValue)) {
            sb.append("DEFAULT '" + defaultValue + "' ");
        }
        // 判断是否为能为空
        if (rc.isHasNotNull()) {
            sb.append("NOT NULL ");
        }
        sb.append(" )");

        // 用来装oracle单独的注释
        StringBuilder sComment = null;

        // oracle 字段注释
        // 是否有注释
        if (StringUtils.isNotBlank(comment)) {
            // 添加字段注释
            sComment = new StringBuilder("COMMENT ON COLUMN ");
            sComment.append(tableName).append(".").append(columnName).append(" IS '").append(comment).append("'");
        }

        log.info("add column,sql is : {}", sb.toString());

        ArrayList<String> commands = new ArrayList<>();

        commands.add(sb.toString());
        if (null != sComment) {
            log.info("add column,oracle comment sql is : {}", sComment.toString());
            commands.add(sComment.toString());
        }

        return commands;
    }


    /**
     * 修改列sql
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
        // 注释
        String comment = rc.getComment();
        // 默认值
        String defaultValue = rc.getDefaultValue();

        StringBuilder sb = new StringBuilder("ALTER TABLE ");

        // 判断列表名是否为空
        if (StringUtils.isEmpty(tableName)) {
            throw new DbProxyException("add Column, tableName is null");
        }

        // 判断列名是否为空
        if (StringUtils.isEmpty(columnName)) {
            throw new DbProxyException("add Column, columnName is null");
        }

        sb.append(tableName).append(" MODIFY ")
                .append(columnName)
                .append(" ")
                .append(getAndVerifiedDataType(dataType) + " ");


        // 是否有类型范围限制,有就加上,没有就不加
        if (StringUtils.isNotBlank(columnChamp)) {
            sb.append("(" + columnChamp + ") ");
        }

        // 是否有默认值
        if (StringUtils.isNotEmpty(defaultValue)) {
            sb.append("DEFAULT '" + defaultValue + "' ");
        }

        // 判断是否为能为空
        if (rc.isHasNotNull()) {
            sb.append("NOT NULL ");
        }

        // 用来装oracle单独的注释
        StringBuilder sComment = null;

        // 是否有注释
        if (!StringUtils.isEmpty(comment)) {
            // 添加字段注释
            sComment = new StringBuilder("COMMENT ON COLUMN ");
            sComment.append(tableName).append(".").append(columnName).append(" IS '").append(comment).append("'");
        }

        log.info("add column,sql is : {}", sb.toString());

        ArrayList<String> commands = new ArrayList<>();

        commands.add(sb.toString());
        if (sComment != null) {
            commands.add(sComment.toString());
        }

        return commands;
    }


    @Override
    protected String getRenameColumnCommand(String tableName, String oldColumnName, RdbColumn rc) {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");

        sb.append(tableName).append(" ");

        sb.append("RENAME COLUMN ").append(oldColumnName).append(" TO ").append(rc.getColumnName());

        log.info("rename column name,sql is : {}", sb.toString());

        return sb.toString();
    }


    /**
     * 创建索引sql
     *
     * @param tableName
     * @param indices
     * @param columns
     * @param otherParams 其它参数 Oracle 忽略
     * @return
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
                case REVERSE:
                    sb.append(indexType).append(" ").append(index.getName()).append(" ON ").append(tableName);
                    break;
                // 唯一索引 e.g: create unique index idx_unique_dname on dept(dname);
                // 位图索引 e.g: create bitmap index idx_bmjob on emp(job);
                case UNIQUE:
                case BITMAP:
                    sb.append(indexType).append(" INDEX ").append(index.getName()).append(" ON ").append(tableName);
                    break;
                default:
                    throw new DbProxyException("create index, ORACLE not apply [" + indexType + "] index type");
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


    /**
     * 删除索引
     *
     * @param tableName
     * @param indices
     * @return
     */
    @Override
    protected List<String> getDropIndexCommands(String tableName, ArrayList<RdbIndex> indices) {

        ArrayList<String> commands = new ArrayList<>();

        for (RdbIndex index : indices) {

            if (StringUtils.isBlank(index.getName())) {
                throw new DbProxyException("drop index name is null");
            }
            commands.add(MessageFormat.format(DROP_IDX_SQL, index.getName()));

        }

        log.info("drop index,sql is : {}", commands);
        return commands;

    }


    @Override
    protected List<String> getAlterPKCommands(String tableName, RdbPrimaryKey oldPrimaryKey, RdbPrimaryKey newPrimaryKey) {
        ArrayList<String> commands = new ArrayList<>();

        if (null != oldPrimaryKey) {

            String oldPKName = oldPrimaryKey.getPrimaryKeyName();
            if (StringUtils.isBlank(oldPKName)) {
                throw new DbProxyException("oracle 主键名必须赋值");
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

package com.ys.idatrix.db.service.internal.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ys.idatrix.db.api.rdb.dto.*;
import com.ys.idatrix.db.service.internal.RdbDDLWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: PostgreSqlImpl
 * @Description:
 * @Author: ZhouJian
 * @Date: 2018/11/27
 */
@Slf4j
@Component
public class PostgreSqlImpl extends RdbDDLWrapper {

    /**
     * 建库
     */
    private final String CREATE_DB_SQL = "CREATE DATABASE {0}";


    /**
     * 删库
     */
    private final String DROP_DB_SQL = "DROP DATABASE IF EXISTS {0} ";


    /**
     * 建模式
     */
    private final String CREATE_SCHEMA_SQL = "CREATE SCHEMA {0}";


    @Override
    public List<String> getCreateDatabaseCommands(RdbCreateDatabase database) {
        List<String> commands = Lists.newArrayList(MessageFormat.format(CREATE_DB_SQL, database.getDatabase()));
        return commands;
    }


    @Override
    public List<String> getDropDatabaseCommands(RdbDropDatabase database) {
        String dropCommand = MessageFormat.format(DROP_DB_SQL, database.getDatabase());
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


    @Override
    protected List<String> getCreateIndexCommands(String tableName, ArrayList<RdbIndex> indices, List<RdbColumn> columns, Object... otherParams) {
        return null;
    }


    @Override
    protected List<String> getDropIndexCommands(String tableName, ArrayList<RdbIndex> indices) {
        return null;
    }


    @Override
    protected List<String> getAlterPKCommands(String tableName, RdbPrimaryKey oldPrimaryKey, RdbPrimaryKey newPrimaryKey) {
        return null;
    }


    @Override
    protected String getAlterTableCommentSql() {
        return null;
    }


    @Override
    public List<String> getDropTableCommands(String tableName) {
        return null;
    }


    @Override
    public String getSelectUserCommand(String user) {
        return null;
    }


    @Override
    public RdbEnum.DBType getDBType() {
        return RdbEnum.DBType.POSTGRESQL;
    }

}

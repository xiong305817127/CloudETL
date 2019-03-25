package com.ys.idatrix.metacube.metamanage.service.impl;

import com.google.common.collect.Lists;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.rdb.service.OracleService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.common.enums.DBEnum;
import com.ys.idatrix.metacube.common.enums.ResultCodeEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.McDatabaseMapper;
import com.ys.idatrix.metacube.metamanage.mapper.TablePkOracleMapper;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import com.ys.idatrix.metacube.metamanage.service.MetadataService;
import com.ys.idatrix.metacube.metamanage.service.OracleDDLService;
import com.ys.idatrix.metacube.metamanage.service.TableColumnService;
import com.ys.idatrix.metacube.metamanage.vo.request.AlterSqlVO;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;
import com.ys.idatrix.metacube.metamanage.vo.request.OracleTableVO;
import com.ys.idatrix.metacube.metamanage.vo.response.DatasourceVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName OracleDDLServiceImpl
 * @Description oracle ddl 语句 实现类
 * @Author ouyang
 * @Date
 */
@Transactional
@Slf4j
@Service
public class OracleDDLServiceImpl implements OracleDDLService {

    // 分隔符
    private static final String SEPARATOR = ",";

    // 删除表
    private static final String DROP_TABLE = "DROP TABLE %s";

    // 表重命名，注意只能修改当前模式下的表
    private static final String ALTER_TABLE_NAME = "alter table %s RENAME TO %s";

    // 表注释
    private static final String TABLE_COMMENT = "COMMENT ON TABLE %s IS %s";

    // 字段注释
    private static final String COLUMN_COMMENT = "COMMENT ON COLUMN %s.%s IS %s";

    // ======= 字段

    // 新增字段
    private static final String ADD_COLUMN = "ALTER TABLE %s ADD (%s %s %s)"; // column dataType [default value] [null/not null]

    // 修改字段
    private static final String MODIFY_COLUMN = "ALTER TABLE %s MODIFY (%s %s)"; // column dataType [default value] [null/not null]

    // 删除字段
    private static final String DROP_COLUMN = "ALTER TABLE %s DROP COLUMN %s";

    // 字段重命名
    private static final String RENAME_FIELD_NAME = "ALTER TABLE %s RENAME COLUMN %s to %s";


    // ===== 索引
    private static final String DROP_INDEX = "DROP INDEX %s";

    // ====== 约束

    // 新增主键，适用于没有主键名
    private static final String ADD_PRIMARY_KEY = "ALTER TABLE %s ADD PRIMARY KEY(%s)";

    // 新增主键，适用于有主键名
    private static final String ADD_PRIMARY_KEY_NAME = "alter table %s add constraint %s primary key(%s)";

    // 删除主键，适用于没有主键名
    private static final String DROP_PRIMARY_KEY = "ALTER TABLE %s DROP PRIMARY KEY";

    // 新增唯一约束
    private static final String ADDUNIQUECONSTRAINT = "alter table %s add constraint %s unique%s";

    // 新增检查约束
    private static final String ADDCHECKCONSTRAINT = "alter table %s add constraint %s check%s";

    // 新增外键约束
    private static final String ADDFOREIGNKEYCONSTRAINT = "alter table %s add constraint %s foreign key%s references %s %s";

    // 删除约束，适用于已知主键名
    private static final String DROP_CONSTRAINT = "ALTER TABLE %s DROP CONSTRAINT %s";

    // 使用约束
    private static final String ENABLE_CONSTRAINT = "ALTER TABLE %s ENABLE CONSTRAINT %s";

    // 禁用约束
    private static final String DISABLE_CONSTRAINT = "ALTER TABLE %s DISABLE CONSTRAINT %s";


    // 创建序列
    private static final String CREATESEQUENCE = "CREATE SEQUENCE %s INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE";

    // ===== 触发器

    private static final String TRIGGER_PRE = "tri_tb_";

    // 创建触发器
    // private static final String ADDTRIGGER = "CREATE OR REPLACE TRIGGER %s BEFORE INSERT ON %s FOR EACH ROW DECLARE next_checkup_no number; BEGIN SELECT %s.nextval into next_checkup_no FROM dual; :NEW.%s := next_checkup_no; END;";
    private static final String ADDTRIGGER = "create or replace trigger %s before insert on %s for each row declare begin select %s.nextval into:new.%s from dual; end;";

    // 删除触发器
    private static final String DROPTRIGGER = "DROP TRIGGER %s";


    // 修改表的所属表空间
    private static final String ALTER_TABLE_TABLESPACE = "alter table %s move tablespace %s";

    @Autowired
    @Qualifier("oracleSchemaService")
    private McSchemaService schemaService;

    @Autowired
    private OracleService oracleService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private TableColumnService tableColumnService;

    @Autowired
    private McDatabaseMapper databaseMapper;

    @Autowired
    private TablePkOracleMapper tablePkOracleMapper;

    @Override
    public AlterSqlVO getCreateTableSql(OracleTableVO table) {
        AlterSqlVO createVo = new AlterSqlVO();

        String tableName = table.getName();
        if (table == null || StringUtils.isBlank(tableName)) {
            throw new MetaDataException("create table, table info error");
        }

        // 表设置
        TableSetOracle setting = table.getTableSetting();
        if (setting == null || StringUtils.isBlank(setting.getTablespace())) {
            throw new MetaDataException("create table, setting error");
        }

        // 当前表的所有字段
        List<TableColumn> columnList = table.getColumnList();
        if (CollectionUtils.isEmpty(columnList)) {
            throw new MetaDataException("create table, columns is null");
        }

        ArrayList commands = new ArrayList();

        StringBuilder sbPK = null; // 主键
        List<TableColumn> pkColumnList = new ArrayList<>();
        TablePkOracle primaryKey = table.getPrimaryKey();
        if (primaryKey.getSequenceStatus() != 1) { // 当前是有主键的
            String primaryKeyName = primaryKey.getName();
            if (StringUtils.isEmpty(primaryKeyName)) {
                throw new MetaDataException("错误的主键");
            }
            sbPK = new StringBuilder();
            sbPK.append(" CONSTRAINT ").append(primaryKeyName).append("  PRIMARY KEY( ");
        }

        // 用来存放 oracle/dm7 的字段注释,为保证执行的顺序 建表语句在注释语句之前执行
        ArrayList<String> COLUMN_COMMENTs = new ArrayList<>();
        if (StringUtils.isNotBlank(table.getIdentification())) {
            String.format(TABLE_COMMENT, addBackQuote(tableName),
                    "'" + table.getIdentification() + "'");
        }

        StringBuilder sb = new StringBuilder("CREATE TABLE "); // create table tableName
        sb.append(addBackQuote(tableName)).append(" ( "); // 表名

        for (TableColumn column : columnList) {// 循环每个字段
            String columnName = column.getColumnName(); // 列名
            String columnType = column.getColumnType(); // 类型
            String typeLength = column.getTypeLength(); // 类型范围
            String typePrecision = column.getTypePrecision(); // 类型精度
            String defaultValue = column.getDefaultValue(); // 默认值
            String description = column.getDescription(); // 注释值

            sb.append(addBackQuote(columnName) + " ")
                    .append(getAndVerifiedDataType(columnType) + " "); // 字段名和字段类型

            // 是否有类型范围限制,有就加上,没有就不加
            if (StringUtils.isNotBlank(typeLength)) {
                sb.append("(");
                sb.append(typeLength);
                // 精度如果不为空
                if (StringUtils.isNotBlank(typePrecision)) {
                    sb.append("," + typePrecision);
                }
                sb.append(") ");
            }

            // 判断是否为能为空
            if (!column.getIsNull()) {
                sb.append("NOT NULL ");
            }

            // 是否有默认值
            if (StringUtils.isNotEmpty(defaultValue)) {
                sb.append("DEFAULT '" + defaultValue + "' ");
            }

            // 是否有注释
            if (StringUtils.isNotBlank(description)) {
                // 添加字段注释
                COLUMN_COMMENTs.add(String
                        .format(COLUMN_COMMENT, addBackQuote(tableName), addBackQuote(columnName),
                                "'" + description + "'"));
            }

            // 分隔符,
            sb.append(SEPARATOR);

            // 拼装主键
            if (sbPK != null) {
                if (column.getIsPk()) {
                    sbPK.append(addBackQuote(columnName)).append(SEPARATOR);
                    pkColumnList.add(column);
                }
            }
        }

        // 主键最后处理
        if (sbPK != null) {
            // 删除最后一个逗号
            if (sbPK.lastIndexOf(SEPARATOR) > -1) {
                sbPK.deleteCharAt(sbPK.lastIndexOf(SEPARATOR));
            }
            sbPK.append(")").append(SEPARATOR);
            // 加入主键
            sb.append(sbPK);
        }

        // 建表语句删除最后的分隔符
        if (sb.lastIndexOf(SEPARATOR) > -1) {
            sb.deleteCharAt(sb.lastIndexOf(SEPARATOR));
        }

        sb.append(")"); // 字段主键结束

        sb.append(" TABLESPACE ").append(setting.getTablespace()); // 表空间设置

        commands.add(sb.toString()); // 将建表语句新增进去

        // 字段注释
        if (CollectionUtils.isNotEmpty(COLUMN_COMMENTs)) {
            commands.addAll(COLUMN_COMMENTs);
        }

        // 索引
        List<String> createIndexCommands = getCreateIndexCommands(tableName, table.getIndexList(),
                table.getColumnList());
        if (CollectionUtils.isNotEmpty(createIndexCommands)) {
            commands.addAll(createIndexCommands);
        }

        // 唯一约束
        List<String> createUniqueCommands = getCreateUniqueCommands(tableName,
                table.getUniqueList(), table.getColumnList());
        if (CollectionUtils.isNotEmpty(createUniqueCommands)) {
            commands.addAll(createUniqueCommands);
        }

        // 检查约束
        List<String> createCheckCommands = getCreateCheckCommands(tableName, table.getCheckList());
        if (CollectionUtils.isNotEmpty(createCheckCommands)) {
            commands.addAll(createCheckCommands);
        }

        // 外键约束
        List<String> createForeignKeyCommands = getCreateForeignKeyCommands(tableName,
                table.getForeignKeyList(), table.getColumnList());
        if (CollectionUtils.isNotEmpty(createForeignKeyCommands)) {
            commands.addAll(createForeignKeyCommands);
        }

        // 主键序列处理
        if (sbPK != null) { // 代表有主键
            // 获取创建触发器语句
            List<String> addTriggerSql = getAddTriggerSql(tableName, primaryKey, pkColumnList);
            if (CollectionUtils.isNotEmpty(addTriggerSql)) {
                createVo.getSpecialSql().addAll(addTriggerSql);
            }
        }
        createVo.setAddSql(commands);
        return createVo;
    }

    private List<String> getCreateForeignKeyCommands(String tableName,
                                                     List<TableFkOracle> foreignKeyList, List<TableColumn> columnList) {
        if (CollectionUtils.isEmpty(foreignKeyList)) {
            return null;
        }
        if (StringUtils.isEmpty(tableName)) {
            throw new MetaDataException("create foreign key, table name is null");
        }

        ArrayList<String> commands = new ArrayList<>(); // 命令集合

        // 当前表所有的列
        Map<Long, TableColumn> columnMap =
                columnList.stream()
                        .collect(Collectors.toMap((key -> key.getId()), (value -> value)));

        // 遍历
        for (TableFkOracle foreignKey : foreignKeyList) {
            if (StringUtils.isBlank(foreignKey.getName())) {
                throw new MetaDataException("create foreign key, foreign key name is null ");
            }
            if (StringUtils.isBlank(foreignKey.getColumnIds())) {
                throw new MetaDataException("create foreign key, foreign key columns is null ");
            }

            String[] columnIdArr = foreignKey.getColumnIds().split(",");
            String[] referenceColumnIdArr = foreignKey.getReferenceColumn().split(",");
            if (columnIdArr.length != referenceColumnIdArr.length) { // 字段对应数量必须一致
                throw new MetaDataException(
                        "create foreign key, this column count and reference column count quantity discrepancy ");
            }

            StringBuilder columnNames = new StringBuilder(); // 当前表关联字段
            columnNames.append(" (");
            for (String columnId : columnIdArr) {
                if (StringUtils.isBlank(columnId)) {
                    throw new MetaDataException(
                            "foreign key name：" + foreignKey.getName() + "，column error");
                }
                TableColumn tableColumn = columnMap.get(Long.parseLong(columnId));
                if (tableColumn == null) {
                    throw new MetaDataException(
                            "foreign key name：" + foreignKey.getName() + "，column error");
                }
                columnNames.append(addBackQuote(tableColumn.getColumnName())).append(" , ");
            }
            // 删除最后一个逗号
            if (columnNames.lastIndexOf(SEPARATOR) > -1) {
                columnNames.deleteCharAt(columnNames.lastIndexOf(SEPARATOR));
            }
            columnNames.append(")"); // 当前表关联字段拼装结束

            // 外键参考表处理
            Metadata referenceTable = metadataService.findById(foreignKey.getReferenceTableId());
            if (referenceTable == null) {
                throw new MetaDataException(
                        "foreign key name：" + foreignKey.getName() + "，error reference table");
            }
            String referenceTableName = referenceTable.getName(); // 参考表名

            // 参考表的所有列
            List<TableColumn> referenceTableColumnList = tableColumnService
                    .getTableColumnListByTableId(foreignKey.getReferenceTableId());
            Map<Long, TableColumn> referenceTableColumnMap =
                    referenceTableColumnList.stream()
                            .collect(Collectors.toMap((key -> key.getId()), (value -> value)));

            StringBuilder referenceColumnNames = new StringBuilder(); // 参考字段
            referenceColumnNames.append(" (");
            for (int i = 0; i < referenceColumnIdArr.length; i++) {
                String referenceColumnId = referenceColumnIdArr[i]; // 参考字段
                TableColumn referenceCol = referenceTableColumnMap
                        .get(Long.parseLong(referenceColumnId));
                if (StringUtils.isBlank(referenceColumnId) || referenceCol == null) {
                    throw new MetaDataException(
                            "foreign key name：" + foreignKey.getName() + "，reference column error");
                }

                String tableColId = columnIdArr[i]; // 参考列对应的关联列
                TableColumn tableCol = columnMap.get(Long.parseLong(tableColId));
                if (!tableCol.getColumnType()
                        .equals(referenceCol.getColumnType())) { // 关联字段和参考字段数据类型必须一致
                    throw new MetaDataException("foreign key name：" + foreignKey.getName()
                            + "， column and reference column data type error");
                }

                referenceColumnNames.append(addBackQuote(referenceCol.getColumnName()))
                        .append(" , ");
            }
            // 删除最后一个逗号
            if (referenceColumnNames.lastIndexOf(SEPARATOR) > -1) {
                referenceColumnNames.deleteCharAt(referenceColumnNames.lastIndexOf(SEPARATOR));
            }
            referenceColumnNames.append(")"); // 参考字段拼装结束

            // 删除时触发事件
            if (StringUtils.isNotBlank(foreignKey.getDeleteTrigger())) {
                String affair = getAndVerifiedTriggerAffair(foreignKey.getDeleteTrigger());
                referenceColumnNames.append(" on delete ").append(affair);
            }

            // 是否启动
            if (foreignKey.getIsEnabled()) {
                referenceColumnNames.append(" ").append("ENABLE");
            } else {
                referenceColumnNames.append(" ").append("DISABLE");
            }

            String sql = String.format(ADDFOREIGNKEYCONSTRAINT, addBackQuote(tableName),
                    addBackQuote(foreignKey.getName()), columnNames,
                    addBackQuote(referenceTableName), referenceColumnNames);
            commands.add(sql);
        }
        return commands;
    }

    private List<String> getCreateCheckCommands(String tableName, List<TableChOracle> checkList) {
        if (CollectionUtils.isEmpty(checkList)) {
            return null;
        }
        if (StringUtils.isEmpty(tableName)) {
            throw new MetaDataException("create check, table name is null");
        }
        ArrayList<String> commands = new ArrayList<>(); // 命令集合
        for (TableChOracle check : checkList) {
            if (StringUtils.isBlank(check.getName())) {
                throw new MetaDataException("create check, check name is null ");
            }
            if (StringUtils.isBlank(check.getCheckSql())) {
                throw new MetaDataException("create check, check sql is null ");
            }
            StringBuilder sb = new StringBuilder();
            sb.append("("); // sql 开始
            sb.append(check.getCheckSql());
            sb.append(")"); // sql 结束
            // 是否开启
            if (check.getIsEnabled()) {
                sb.append(" ").append("ENABLE");
            } else {
                sb.append(" ").append("DISABLE");
            }
            String sql = String.format(ADDCHECKCONSTRAINT, addBackQuote(tableName),
                    addBackQuote(check.getName()), sb.toString());
            commands.add(sql);
        }
        return commands;
    }

    private List<String> getCreateUniqueCommands(String tableName, List<TableUnOracle> uniqueList,
                                                 List<TableColumn> columnList) {
        if (CollectionUtils.isEmpty(uniqueList)) {
            return null;
        }

        ArrayList<String> commands = new ArrayList<>(); // 命令集合

        // 当前表所有的列
        Map<Long, TableColumn> columnMap =
                columnList.stream()
                        .collect(Collectors.toMap((key -> key.getId()), (value -> value)));

        for (TableUnOracle unique : uniqueList) {
            if (StringUtils.isBlank(unique.getName())) {
                throw new MetaDataException("create unique, unique name is null ");
            }
            if (StringUtils.isBlank(unique.getColumnIds())) {
                throw new MetaDataException("create unique, unique columns is null ");
            }

            StringBuilder sb = new StringBuilder();

            // 字段拼装
            sb.append("(");

            String[] columnIdArr = unique.getColumnIds().split(","); // 字段ids
            for (String columnId : columnIdArr) {
                TableColumn column = columnMap.get(Long.parseLong(columnId));
                if (column == null) {
                    throw new MetaDataException(
                            "unique name :" + unique.getName() + " column error");
                }
                sb.append(addBackQuote(column.getColumnName())); // 字段名
                sb.append(" , ");
            }
            // 删除最后一个逗号
            if (sb.lastIndexOf(SEPARATOR) > -1) {
                sb.deleteCharAt(sb.lastIndexOf(SEPARATOR));
            }
            sb.append(")"); // 字段拼装结束

            // 是否开启
            if (unique.getIsEnabled()) {
                sb.append(" ").append("ENABLE");
            } else {
                sb.append(" ").append("DISABLE");
            }

            // 拼装执行sql
            String sql = String.format(ADDUNIQUECONSTRAINT, addBackQuote(tableName),
                    addBackQuote(unique.getName()), sb.toString());
            commands.add(sql);
        }
        return commands;
    }

    private List<String> getCreateIndexCommands(String tableName, List<TableIdxOracle> indexList,
                                                List<TableColumn> columnList) {
        if (CollectionUtils.isEmpty(indexList)) {
            return null;
        }

        ArrayList<String> commands = new ArrayList<>();

        // 当前表所有的列
        Map<Long, TableColumn> columnMap =
                columnList.stream()
                        .collect(Collectors.toMap((key -> key.getId()), (value -> value)));

        for (TableIdxOracle index : indexList) {
            if (StringUtils.isBlank(index.getIndexName())) {
                throw new MetaDataException("create index, index name is null ");
            }
            if (StringUtils.isBlank(index.getColumnIds())) {
                throw new MetaDataException("create index, index columns is null ");
            }
            StringBuilder sb = new StringBuilder("CREATE ");

            // 验证并获取索引类型
            String indexType = getAndVerifiedIndexType(index.getIndexType());
            if (!indexType.equals("NON-UNIQUE")) {
                sb.append(indexType).append(" ");
            }
            sb.append("INDEX "); // 索引类型

            sb.append(addBackQuote(index.getSchemaName())).append(".")
                    .append(addBackQuote(index.getIndexName())); // 模式名.索引名
            sb.append(" ON ");
            sb.append(addBackQuote(index.getSchemaName())).append(".")
                    .append(addBackQuote(tableName)); // 模式名.表名

            // 索引列的处理
            sb.append(" (");
            String[] columnIdArr = index.getColumnIds().split(","); // 字段ids
            String[] sortArr = index.getColumnSort().split(",");// 字段对应的排序
            for (int i = 0; i < columnIdArr.length; i++) {
                String columnId = columnIdArr[i];
                if (StringUtils.isBlank(columnId)) {
                    throw new MetaDataException(
                            "index name :" + index.getIndexName() + " column error");
                }

                TableColumn tableColumn = columnMap.get(Long.parseLong(columnId)); // 当前索引对应的列
                if (tableColumn == null) {
                    throw new MetaDataException(
                            "index name :" + index.getIndexName() + " column error");
                }

                sb.append(addBackQuote(tableColumn.getColumnName())); // 字段名
                String sort = sortArr[i];
                if (StringUtils.isNotBlank(sort) || !sort.equals("ASC")) {
                    sb.append(" DESC");
                }
                sb.append(" , ");
            }
            // 删除最后一个逗号
            if (sb.lastIndexOf(SEPARATOR) > -1) {
                sb.deleteCharAt(sb.lastIndexOf(SEPARATOR));
            }
            sb.append(")"); // 字段拼装结束

            sb.append(" TABLESPACE ").append(index.getTablespace()); // 当前索引表空间

            // 一个index拼装完成
            commands.add(sb.toString());
        }
        return commands;
    }

    @Override
    public AlterSqlVO getAlterTableSql(OracleTableVO newTable, OracleTableVO oldTable) {
        if (newTable == null || oldTable == null) {
            throw new MetaDataException("alter table，newTable or oldTable is null");
        }

        // 当前用户数据库中的实体表还是快照中的数据
        String oldTableName = oldTable.getName();
        if (StringUtils.isBlank(oldTableName)) {
            throw new MetaDataException("alter table，table name is null");
        }

        AlterSqlVO alterSql = new AlterSqlVO();
        ArrayList<String> allCommands = new ArrayList<>(); // 所有需要执行sql
        StringBuilder message = new StringBuilder(); // 版本变更详情

        // 获取表基本信息修改sql(表名，表注释)
        AlterSqlVO alterTableInfoVo = alterTableInfoSql(oldTable, newTable);

        // 获取修改表字段的sql
        AlterSqlVO alterColumnVo = getAlterColumnSql(oldTableName, oldTable.getColumnList(),
                newTable.getColumnList());

        // 获取修改表主键sql
        AlterSqlVO alterPrimaryKeyVo = getAlterPrimaryKeySql(oldTableName, oldTable.getPrimaryKey(),
                newTable.getPrimaryKey(), oldTable.getColumnList(), newTable.getColumnList());

        // 获取修改索引的sql
        AlterSqlVO alterIndexVo = getAlterIndexSql(oldTableName, oldTable.getIndexList(),
                newTable.getIndexList(), newTable.getColumnList());

        /**
         * 约束注意：
         * 无法修改一个约束，只能新增约束或删除约束，
         * 可以去做禁用约束和启用约束
         */
        // 获取修改唯一约束的sql
        AlterSqlVO alterUniqueVo = getAlterUniqueSql(oldTableName, oldTable.getUniqueList(),
                newTable.getUniqueList(), newTable.getColumnList());

        // 获取修改检查约束的sql
        AlterSqlVO alterCheckVo = getAlterCheckSql(oldTableName, oldTable.getCheckList(),
                newTable.getCheckList());

        // 获取修改外键的sql
        AlterSqlVO alterForeignKeyVo = getAlterForeignKeySql(oldTableName,
                oldTable.getForeignKeyList(), newTable.getForeignKeyList(),
                newTable.getColumnList());

        // 获取修改表设置的sql
        AlterSqlVO alterSettingVo = getAlterSettingSql(oldTableName, oldTable.getTableSetting(),
                newTable.getTableSetting());

        /**
         * sql执行顺序：
         * 1.外键删除 2.主键删除 3.索引删除 4.检查约束删除 5.唯一约束删除 6.索引删除 7.字段修改（修改，删除，新增） 8.新增索引 9.新增唯一约束
         * 10.新增检查约束 11.新增主键 12.新增外键 13.表基本信息修改
         */

        // 外键删除
        if (alterForeignKeyVo != null) {
            if (CollectionUtils.isNotEmpty(alterForeignKeyVo.getDeleteSql())) {
                allCommands.addAll(alterForeignKeyVo.getDeleteSql());
            }
        }

        // 主键删除
        if (alterPrimaryKeyVo != null) {
            if (CollectionUtils.isNotEmpty(alterPrimaryKeyVo.getDeleteSql())) {
                allCommands.addAll(alterPrimaryKeyVo.getDeleteSql());
            }
        }

        // 唯一约束删除
        if (alterUniqueVo != null) {
            if (CollectionUtils.isNotEmpty(alterUniqueVo.getDeleteSql())) {
                allCommands.addAll(alterUniqueVo.getDeleteSql());
            }
        }

        // 检查约束删除
        if (alterCheckVo != null) {
            if (CollectionUtils.isNotEmpty(alterCheckVo.getDeleteSql())) {
                allCommands.addAll(alterCheckVo.getDeleteSql());
            }
        }

        // 索引删除
        if (alterIndexVo != null) {
            if (CollectionUtils.isNotEmpty(alterIndexVo.getDeleteSql())) {
                allCommands.addAll(alterIndexVo.getDeleteSql());
            }
        }

        // 字段修改
        if (alterColumnVo != null) {
            if (CollectionUtils.isNotEmpty(alterColumnVo.getChangeSql())) {
                allCommands.addAll(alterColumnVo.getChangeSql());
            }
            if (CollectionUtils.isNotEmpty(alterColumnVo.getDeleteSql())) {
                allCommands.addAll(alterColumnVo.getDeleteSql());
            }
            if (CollectionUtils.isNotEmpty(alterColumnVo.getAddSql())) {
                allCommands.addAll(alterColumnVo.getAddSql());
            }
        }

        // 新增索引
        if (alterIndexVo != null) {
            if (CollectionUtils.isNotEmpty(alterIndexVo.getAddSql())) {
                allCommands.addAll(alterIndexVo.getAddSql());
            }
        }

        // 新增唯一约束
        if (alterUniqueVo != null) {
            if (CollectionUtils.isNotEmpty(alterUniqueVo.getAddSql())) {
                allCommands.addAll(alterUniqueVo.getAddSql());
            }
        }

        // 新增检查约束
        if (alterCheckVo != null) {
            if (CollectionUtils.isNotEmpty(alterCheckVo.getAddSql())) {
                allCommands.addAll(alterCheckVo.getAddSql());
            }
        }

        // 新增主键
        if (alterPrimaryKeyVo != null) {
            if (CollectionUtils.isNotEmpty(alterPrimaryKeyVo.getAddSql())) {
                allCommands.addAll(alterPrimaryKeyVo.getAddSql());
            }
            // 特殊sql
            if (CollectionUtils.isNotEmpty(alterPrimaryKeyVo.getSpecialSql())) {
                alterSql.getSpecialSql().addAll(alterPrimaryKeyVo.getSpecialSql());
            }
        }

        // 新增外键
        if (alterForeignKeyVo != null) {
            if (CollectionUtils.isNotEmpty(alterForeignKeyVo.getAddSql())) {
                allCommands.addAll(alterForeignKeyVo.getAddSql());
            }
        }

        // 表基本信息修改
        if (alterTableInfoVo != null) {
            if (CollectionUtils.isNotEmpty(alterTableInfoVo.getChangeSql())) {
                allCommands.addAll(alterTableInfoVo.getChangeSql());
            }
        }

        // 表设置修改
        if (alterSettingVo != null) {
            if (CollectionUtils.isNotEmpty(alterSettingVo.getChangeSql())) {
                allCommands.addAll(alterSettingVo.getChangeSql());
            }
        }

        /**
         * 快照详情信息返回
         */
        // 表基本信息修改详情
        if (alterTableInfoVo != null) {
            if (StringUtils.isNotBlank(alterTableInfoVo.getMessage())) {
                message.append(alterTableInfoVo.getMessage());
            }
        }

        // 表设置修改详情
        if (alterSettingVo != null) {
            if (StringUtils.isNotBlank(alterSettingVo.getMessage())) {
                message.append(alterSettingVo.getMessage());
            }
        }

        // 表字段修改详情
        if (alterColumnVo != null) {
            if (StringUtils.isNotBlank(alterColumnVo.getMessage())) {
                message.append(alterColumnVo.getMessage());
            }
        }

        // 表主键修改详情
        if (alterPrimaryKeyVo != null) {
            if (StringUtils.isNotBlank(alterPrimaryKeyVo.getMessage())) {
                message.append(alterPrimaryKeyVo.getMessage());
            }
        }

        // 表索引修改详情
        if (alterIndexVo != null) {
            if (StringUtils.isNotBlank(alterIndexVo.getMessage())) {
                message.append(alterIndexVo.getMessage());
            }
        }

        // 表唯一约束修改详情
        if (alterUniqueVo != null) {
            if (StringUtils.isNotBlank(alterUniqueVo.getMessage())) {
                message.append(alterUniqueVo.getMessage());
            }
        }

        // 表检查约束修改详情
        if (alterCheckVo != null) {
            if (StringUtils.isNotBlank(alterCheckVo.getMessage())) {
                message.append(alterCheckVo.getMessage());
            }
        }

        // 表外键修改详情
        if (alterForeignKeyVo != null) {
            if (StringUtils.isNotBlank(alterForeignKeyVo.getMessage())) {
                message.append(alterForeignKeyVo.getMessage());
            }
        }

        // message 删除最后的一个,
        if (message.lastIndexOf(SEPARATOR) > -1) {
            message.deleteCharAt(message.lastIndexOf(SEPARATOR));
        }

        alterSql.setChangeSql(allCommands);
        alterSql.setMessage(message.toString());
        return alterSql;
    }

    @Override
    public List<String> getDeleteTableSql(List<String> tableNames) {
        if (CollectionUtils.isEmpty(tableNames)) {
            return null;
        }
        List<String> result = new ArrayList<>();
        for (String tableName : tableNames) {
            if (StringUtils.isNotBlank(tableName)) {
                result.add(String.format(DROP_TABLE, addBackQuote(tableName)));
            }
        }
        return result;
    }

    private AlterSqlVO getAlterSettingSql(String tableName, TableSetOracle oldTableSetting,
                                          TableSetOracle newTableSetting) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("alter table setting, table name is null");
        }

        AlterSqlVO alterSqlVO = new AlterSqlVO();
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if (!oldTableSetting.getTablespace().equals(newTableSetting.getTablespace())) {
            result.add(String.format(ALTER_TABLE_TABLESPACE, tableName,
                    newTableSetting.getTablespace()));
            sb.append("修改表空间,");
        }

        alterSqlVO.setChangeSql(result);
        alterSqlVO.setMessage(sb.toString());
        return alterSqlVO;
    }

    private AlterSqlVO getAlterForeignKeySql(String tableName,
                                             List<TableFkOracle> oldForeignKeyList, List<TableFkOracle> newForeignKeyList,
                                             List<TableColumn> columnList) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("alter foreign key, tableName is null");
        }
        if (CollectionUtils.isEmpty(oldForeignKeyList) && CollectionUtils
                .isEmpty(newForeignKeyList)) {
            log.warn(
                    "alter foreign, no foreign changed, oldForeignKeyList and  newForeignKeyList is null ");
            return null;
        }
        if (CollectionUtils.isEmpty(columnList)) {
            throw new MetaDataException("alter foreign key, columnList is null");
        }

        // 如果新或旧的外键集合其中一个为null,则主动一个空ArrayList,便于后续处理
        if (oldForeignKeyList == null) {
            oldForeignKeyList = new ArrayList<>();
        }
        if (newForeignKeyList == null) {
            newForeignKeyList = new ArrayList<>();
        }

        // copy一份不要修改到之前参数
        ArrayList<TableFkOracle> oldForeignKeyListCopy = Lists.newArrayList(oldForeignKeyList);
        ArrayList<TableFkOracle> newForeignKeyListCopy = Lists.newArrayList(newForeignKeyList);

        // 在copy一份做参照
        ArrayList<TableFkOracle> oldCopy = Lists.newArrayList(oldForeignKeyList);
        ArrayList<TableFkOracle> newCopy = Lists.newArrayList(newForeignKeyList);

        // 不变的索引bean集合 //求交集。自定义对象重写 hashcode 和 equals
        oldCopy.retainAll(newCopy);

        // 待删除的外键集合，解释：被修改或以被删除的外键
        oldForeignKeyListCopy.removeAll(oldCopy);

        // 待新增的外键集合，解释：被修改或新增的外键
        newForeignKeyListCopy.removeAll(oldCopy);

        AlterSqlVO alterSql = new AlterSqlVO();

        // 先删除外键
        List<String> dropForeignKeyCommands = getDropForeignKeySql(tableName,
                oldForeignKeyListCopy);
        if (CollectionUtils.isNotEmpty(dropForeignKeyCommands)) {
            alterSql.setDeleteSql(dropForeignKeyCommands);
        }

        // 后新增外键
        List<String> addForeignKeyCommands = getCreateForeignKeyCommands(tableName,
                newForeignKeyListCopy, columnList);
        if (CollectionUtils.isNotEmpty(addForeignKeyCommands)) {
            alterSql.setAddSql(addForeignKeyCommands);
        }

        // 获取修改了启动/禁用的检查约束
        List<String> changeForeignKeyIsEnabledSql = getChangeForeignKeyIsEnabledSql(tableName,
                oldCopy, newForeignKeyList);
        if (CollectionUtils.isNotEmpty(changeForeignKeyIsEnabledSql)) {
            alterSql.setChangeSql(changeForeignKeyIsEnabledSql);
        }

        // TODO 外键修改信息
        StringBuffer message = new StringBuffer();

        return alterSql;
    }

    private List<String> getChangeForeignKeyIsEnabledSql(String tableName,
                                                         ArrayList<TableFkOracle> oldForeignKeyList, List<TableFkOracle> newForeignKeyList) {
        if (CollectionUtils.isEmpty(oldForeignKeyList) || CollectionUtils
                .isEmpty(newForeignKeyList)) {
            return null;
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < oldForeignKeyList.size(); i++) {
            TableFkOracle oldForeignKey = oldForeignKeyList.get(i);
            TableFkOracle newForeignKey = newForeignKeyList.get(i);
            if (!oldForeignKey.getIsEnabled().equals(newForeignKey.getIsEnabled())) {
                if (newForeignKey.getIsEnabled()) {
                    result.add(String.format(ENABLE_CONSTRAINT, addBackQuote(tableName),
                            addBackQuote(newForeignKey.getName())));
                } else {
                    result.add(String.format(DISABLE_CONSTRAINT, addBackQuote(tableName),
                            addBackQuote(newForeignKey.getName())));
                }
            }
        }
        return result;
    }

    private List<String> getDropForeignKeySql(String tableName,
                                              List<TableFkOracle> oldForeignKeyList) {
        if (CollectionUtils.isEmpty(oldForeignKeyList)) {
            return null;
        }
        List<String> result = new ArrayList<>();
        for (TableFkOracle foreignKey : oldForeignKeyList) {
            result.add(String.format(DROP_CONSTRAINT, addBackQuote(tableName),
                    addBackQuote(foreignKey.getName())));
        }
        return result;
    }

    private AlterSqlVO getAlterCheckSql(String tableName, List<TableChOracle> oldCheckList,
                                        List<TableChOracle> newCheckList) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("alter check, tableName is null");
        }
        if (CollectionUtils.isEmpty(oldCheckList) && CollectionUtils.isEmpty(newCheckList)) {
            log.warn("alter check, no check changed, oldCheckList and  newCheckList is null ");
            return null;
        }
        // 如果新或旧的外键集合其中一个为null,则主动一个空ArrayList,便于后续处理
        if (oldCheckList == null) {
            oldCheckList = new ArrayList<>();
        }
        if (newCheckList == null) {
            newCheckList = new ArrayList<>();
        }

        // copy一份不要修改到之前参数
        ArrayList<TableChOracle> oldUniqueListCopy = Lists.newArrayList(oldCheckList);
        ArrayList<TableChOracle> newUniqueListCopy = Lists.newArrayList(newCheckList);

        // 在copy一份做参照
        ArrayList<TableChOracle> oldCopy = Lists.newArrayList(oldCheckList);
        ArrayList<TableChOracle> newCopy = Lists.newArrayList(newCheckList);

        // 不变的索引bean集合
        // 求交集。自定义对象重写 hashcode 和 equals
        oldCopy.retainAll(newCopy);

        // 待删除的外键集合，解释：被修改或以被删除的外键
        oldUniqueListCopy.removeAll(oldCopy);

        // 待新增的外键集合，解释：被修改或新增的外键
        newUniqueListCopy.removeAll(oldCopy);

        AlterSqlVO alterSql = new AlterSqlVO();

        // 先删除检查约束
        List<String> dropCheckSql = getDropCheckSql(tableName, oldUniqueListCopy);
        if (CollectionUtils.isNotEmpty(dropCheckSql)) {
            alterSql.setDeleteSql(dropCheckSql);
        }

        // 后新增检查约束
        List<String> addCheckSql = getCreateCheckCommands(tableName, newUniqueListCopy);
        if (CollectionUtils.isNotEmpty(addCheckSql)) {
            alterSql.setAddSql(addCheckSql);
        }

        // 获取修改了启动/禁用的检查约束
        List<String> changeCheckIsEnabledSql = getChangeCheckIsEnabledSql(tableName, oldCopy,
                newCheckList);
        if (CollectionUtils.isNotEmpty(changeCheckIsEnabledSql)) {
            alterSql.setChangeSql(changeCheckIsEnabledSql);
        }

        // TODO 拼装唯一约束修改信息

        return alterSql;
    }

    private List<String> getChangeCheckIsEnabledSql(String tableName,
                                                    ArrayList<TableChOracle> oldCheckList, List<TableChOracle> newCheckList) {
        if (CollectionUtils.isEmpty(oldCheckList) || CollectionUtils.isEmpty(newCheckList)) {
            return null;
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < oldCheckList.size(); i++) {
            TableChOracle oldCheck = oldCheckList.get(i);
            TableChOracle newCheck = newCheckList.get(i);
            if (!oldCheck.getIsEnabled().equals(newCheck.getIsEnabled())) {
                if (newCheck.getIsEnabled()) {
                    result.add(String.format(ENABLE_CONSTRAINT, addBackQuote(tableName),
                            addBackQuote(newCheck.getName())));
                } else {
                    result.add(String.format(DISABLE_CONSTRAINT, addBackQuote(tableName),
                            addBackQuote(newCheck.getName())));
                }
            }
        }
        return result;
    }

    private List<String> getDropCheckSql(String tableName,
                                         ArrayList<TableChOracle> oldUniqueListCopy) {
        if (CollectionUtils.isEmpty(oldUniqueListCopy)) {
            return null;
        }
        List<String> result = new ArrayList<>();
        for (TableChOracle check : oldUniqueListCopy) {
            result.add(String.format(DROP_CONSTRAINT, addBackQuote(tableName),
                    addBackQuote(check.getName())));
        }
        return result;
    }

    private AlterSqlVO getAlterUniqueSql(String tableName, List<TableUnOracle> oldUniqueList,
                                         List<TableUnOracle> newUniqueList, List<TableColumn> columnList) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("alter unique, tableName is null");
        }
        if (CollectionUtils.isEmpty(oldUniqueList) && CollectionUtils.isEmpty(newUniqueList)) {
            log.warn("alter unique, no unique changed, oldUniqueList and  newUniqueList is null ");
            return null;
        }
        if (CollectionUtils.isEmpty(columnList)) {
            throw new MetaDataException("alter unique, columnList is null");
        }

        // 如果新或旧的外键集合其中一个为null,则主动一个空ArrayList,便于后续处理
        if (oldUniqueList == null) {
            oldUniqueList = new ArrayList<>();
        }
        if (newUniqueList == null) {
            newUniqueList = new ArrayList<>();
        }

        // copy一份不要修改到之前参数
        ArrayList<TableUnOracle> oldUniqueListCopy = Lists.newArrayList(oldUniqueList);
        ArrayList<TableUnOracle> newUniqueListCopy = Lists.newArrayList(newUniqueList);

        // 在copy一份做参照
        ArrayList<TableUnOracle> oldCopy = Lists.newArrayList(oldUniqueList);
        ArrayList<TableUnOracle> newCopy = Lists.newArrayList(newUniqueList);

        // 不变的唯一约束bean集合
        // 求交集。自定义对象重写 hashcode 和 equals
        oldCopy.retainAll(newCopy);

        // 待删除的外键集合，解释：被修改或以被删除的外键
        oldUniqueListCopy.removeAll(oldCopy);

        // 待新增的外键集合，解释：被修改或新增的外键
        newUniqueListCopy.removeAll(oldCopy);

        AlterSqlVO alterSql = new AlterSqlVO();

        // 先删除唯一约束
        List<String> dropUniqueSql = getDropUniqueSql(tableName, oldUniqueListCopy);
        if (CollectionUtils.isNotEmpty(dropUniqueSql)) {
            alterSql.setDeleteSql(dropUniqueSql);
        }

        // 后新增唯一约束
        List<String> addUniqueSql = getCreateUniqueCommands(tableName, newUniqueListCopy,
                columnList);
        if (CollectionUtils.isNotEmpty(addUniqueSql)) {
            alterSql.setAddSql(addUniqueSql);
        }

        // 获取修改了启动/禁用的唯一约束
        List<String> changeIsEnabledSql = getChangeUniqueIsEnabledSql(tableName, oldCopy,
                newUniqueList);
        if (CollectionUtils.isNotEmpty(changeIsEnabledSql)) {
            alterSql.setChangeSql(changeIsEnabledSql);
        }

        // TODO 拼装唯一约束修改信息

        return alterSql;
    }

    private List<String> getChangeUniqueIsEnabledSql(String tableName,
                                                     ArrayList<TableUnOracle> oldUniqueList, List<TableUnOracle> newUniqueList) {
        if (CollectionUtils.isEmpty(oldUniqueList) || CollectionUtils.isEmpty(newUniqueList)) {
            return null;
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < oldUniqueList.size(); i++) {
            TableUnOracle oldUnique = oldUniqueList.get(i);
            TableUnOracle newUnique = newUniqueList.get(i);
            if (!oldUnique.getIsEnabled().equals(newUnique.getIsEnabled())) {
                if (newUnique.getIsEnabled()) {
                    result.add(String.format(ENABLE_CONSTRAINT, addBackQuote(tableName),
                            addBackQuote(newUnique.getName())));
                } else {
                    result.add(String.format(DISABLE_CONSTRAINT, addBackQuote(tableName),
                            addBackQuote(newUnique.getName())));
                }
            }
        }
        return result;
    }

    private List<String> getDropUniqueSql(String tableName,
                                          ArrayList<TableUnOracle> oldUniqueListCopy) {
        if (CollectionUtils.isEmpty(oldUniqueListCopy)) {
            return null;
        }
        List<String> result = new ArrayList<>();
        for (TableUnOracle unique : oldUniqueListCopy) {
            result.add(String.format(DROP_CONSTRAINT, addBackQuote(tableName),
                    addBackQuote(unique.getName())));
        }
        return result;
    }

    private AlterSqlVO getAlterIndexSql(String tableName, List<TableIdxOracle> oldIndexList,
                                        List<TableIdxOracle> newIndexList, List<TableColumn> columnList) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("alter Index, tableName is null");
        }
        if (CollectionUtils.isEmpty(oldIndexList) && CollectionUtils.isEmpty(newIndexList)) {
            log.warn("alter Index, no index changed, oldIndexList and  newIndexList is null ");
            return null;
        }
        if (CollectionUtils.isEmpty(columnList)) {
            throw new MetaDataException("alter Index, columnList is null");
        }
        /**
         * 索引的修改：
         * 先删除，后新增
         */
        // 如果新或旧的索引集合其中一个为null,则主动一个空ArrayList,便于后续处理
        if (oldIndexList == null) {
            oldIndexList = new ArrayList<>();
        }
        if (newIndexList == null) {
            newIndexList = new ArrayList<>();
        }

        // copy一个新的，不要改变之前的参数，因为之前参数还需要做快照
        ArrayList<TableIdxOracle> oldIndexListCopy = Lists.newArrayList(oldIndexList);
        ArrayList<TableIdxOracle> newIndexListCopy = Lists.newArrayList(newIndexList);

        // 这里再copy一份做参照
        ArrayList<TableIdxOracle> oldCopy = Lists.newArrayList(oldIndexListCopy);
        ArrayList<TableIdxOracle> newCopy = Lists.newArrayList(newIndexListCopy);

        // 不变的索引bean集合 //求交集。自定义对象重写 hashcode 和 equals
        oldCopy.retainAll(newCopy);

        // 待删除的索引集合，解释：被修改或以被删除的索引
        oldIndexListCopy.removeAll(oldCopy);

        // 待新增的索引集合，解释：被修改或新增的索引
        newIndexListCopy.removeAll(oldCopy);

        AlterSqlVO alterSql = new AlterSqlVO();

        // 先删除索引
        List<String> dropIndexCommands = getDropIndexSql(oldIndexListCopy);
        if (CollectionUtils.isNotEmpty(dropIndexCommands)) {
            alterSql.setDeleteSql(dropIndexCommands);
        }

        // 再新建索引
        List<String> addIndexCommands = getCreateIndexCommands(tableName, newIndexListCopy,
                columnList);
        if (CollectionUtils.isNotEmpty(addIndexCommands)) {
            alterSql.setAddSql(addIndexCommands);
        }

        /**
         * 索引修改信息详情：
         * 因为上面做法是先删除被删除或被修改的索引，后再去做新增索引。所以不确定详细情况，这边要重做
         */
        StringBuilder message = new StringBuilder();

        // 老版本的索引id
        ArrayList<Long> oldIndexIdList = getIndexVersionList(oldIndexList);
        // 新版本的索引id
        ArrayList<Long> newIndexIdList = getIndexVersionList(newIndexList);

        // 复制旧表索引id
        ArrayList<Long> oldIdCopy = Lists.newArrayList(oldIndexIdList);
        // 复制新表索引id
        ArrayList<Long> newIdCopy = Lists.newArrayList(newIndexIdList);

        // 删除的索引
        oldIdCopy.removeAll(newIdCopy);

        // 被修改或未被修改的索引
        oldIndexIdList.removeAll(oldIdCopy);

        // 新增的索引
        newIndexIdList.removeAll(oldIndexIdList);

        // 返回旧索引中被修改或未被修改的索引bean
        ArrayList<TableIdxOracle> modifyOrNorOld = getIndexList(oldIndexIdList, oldIndexList);

        // 返回新索引中被修改或未被修改的索引bean
        ArrayList<TableIdxOracle> modifyOrNorNew = getIndexList(oldIndexIdList, newIndexList);

        // 返回真正被修改的bean集合
        modifyOrNorOld.retainAll(modifyOrNorNew);
        modifyOrNorNew.removeAll(modifyOrNorOld);

        if (CollectionUtils.isNotEmpty(newIndexIdList)) {
            message.append("新增索引数").append(newIndexIdList.size()).append(",");
        }
        if (CollectionUtils.isNotEmpty(modifyOrNorNew)) {
            message.append("被修改的索引数").append(modifyOrNorNew.size()).append(",");
        }
        if (CollectionUtils.isNotEmpty(oldIdCopy)) {
            message.append("删除的索引数").append(newIndexIdList.size()).append(",");
        }

        alterSql.setMessage(message.toString());
        return alterSql;
    }

    private ArrayList<TableIdxOracle> getIndexList(ArrayList<Long> indexIdList,
                                                   List<TableIdxOracle> indexList) {
        ArrayList<TableIdxOracle> result = new ArrayList<>();
        // 遍历拿到所需要的索引
        for (TableIdxOracle index : indexList) {
            Long id = index.getId();
            if (id == null) {
                throw new MetaDataException(" index id is null");
            }
            for (Long indexId : indexIdList) {
                if (id.equals(indexId)) {
                    result.add(index);
                }
            }
        }
        return result;
    }

    private ArrayList<Long> getIndexVersionList(List<TableIdxOracle> indexList) {
        ArrayList<Long> list = new ArrayList<>();
        for (TableIdxOracle index : indexList) {
            list.add(index.getId());
        }
        return list;
    }

    private List<String> getDropIndexSql(ArrayList<TableIdxOracle> IndexList) {
        if (CollectionUtils.isEmpty(IndexList)) {
            return null;
        }
        ArrayList<String> commands = Lists.newArrayList();
        for (TableIdxOracle index : IndexList) {
            if (StringUtils.isBlank(index.getIndexName())) {
                throw new MetaDataException("drop index name is null");
            }
            // drop index indexName on tableName
            commands.add(String.format(DROP_INDEX, addBackQuote(index.getIndexName())));
        }
        return commands;
    }

    private AlterSqlVO getAlterPrimaryKeySql(String tableName, TablePkOracle oldPrimaryKey,
                                             TablePkOracle newPrimaryKey, List<TableColumn> oldColumnList,
                                             List<TableColumn> newColumnList) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("alter primary key, tableName is null");
        }
        if (oldPrimaryKey == null) {
            throw new MetaDataException("alter primary key, oldPrimaryKey is null");
        }
        if (newPrimaryKey == null) {
            throw new MetaDataException("alter primary key, newPrimaryKey is null");
        }
        if (CollectionUtils.isEmpty(oldColumnList)) {
            throw new MetaDataException("alter primary key, oldColumnList is null");
        }
        if (CollectionUtils.isEmpty(newColumnList)) {
            throw new MetaDataException("alter primary key, newColumnList is null");
        }
        if (oldPrimaryKey.getSequenceStatus() <= 1 && newPrimaryKey.getSequenceStatus() <= 1) {
            // 无主键，并且主键没有做修改
            return null;
        }

        /**
         * 主键修改：
         * 如果约束名变动，则修改
         * 如果主键字段变动，则修改
         */
        Boolean isChangePK = false;

        // 如果约束名变动，则修改
        String oldPrimaryKeyName = oldPrimaryKey.getName(); // 旧主键约束名
        String newPrimaryKeyName = newPrimaryKey.getName(); // 新主键约束名
        if (!oldPrimaryKeyName.equals(newPrimaryKeyName)) {
            isChangePK = true;
        }

        // 如果主键字段变动，则修改
        List<TableColumn> oldPrimaryKeyColumn = new ArrayList();
        List<TableColumn> newPrimaryKeyColumn = new ArrayList();

        for (TableColumn column : oldColumnList) {
            if (column.getIsPk()) {
                oldPrimaryKeyColumn.add(column);
            }
        }

        for (TableColumn column : newColumnList) {
            if (column.getIsPk()) {
                newPrimaryKeyColumn.add(column);
            }
        }

        ArrayList<TableColumn> oldCopy = Lists.newArrayList(oldPrimaryKeyColumn); // 之前主键字段
        ArrayList<TableColumn> newCopy = Lists.newArrayList(newPrimaryKeyColumn); // 现在主键字段

        // 求交集
        oldCopy.retainAll(newCopy);

        // 如果交集后与最新不一致，说明主键有修改
        if (oldCopy.size() != newCopy.size()) {
            isChangePK = true;
        }

        // 如果不一致，就修改主键，先删除，后修改
        AlterSqlVO alterSql = new AlterSqlVO();

        if (isChangePK) {
            // 主键如果有所修改，那么都是先删除后新增
            // 先删除主键
            String dropPrimaryKeySql = getDropPrimaryKeySql(tableName, oldPrimaryKeyName);
            if (StringUtils.isNoneBlank(dropPrimaryKeySql)) {
                alterSql.getDeleteSql().add(dropPrimaryKeySql);
            }

            // 后新增主键
            String addPrimaryKeySql = getAddPrimaryKeySql(tableName, newPrimaryKeyName,
                    newPrimaryKeyColumn);
            if (StringUtils.isNoneBlank(addPrimaryKeySql)) {
                List<String> list = new ArrayList<>();
                list.add(addPrimaryKeySql);
                alterSql.setAddSql(list);
            }

            alterSql.setMessage("修改主键,");
        }

        if (oldPrimaryKey.getSequenceStatus() <= 2 && newPrimaryKey.getSequenceStatus() <= 2) {
            // 没有序列关联
            return alterSql;
        }

        /**
         * 序列修改：
         * 序列状态修改
         * 序列名修改
         */
        Boolean isChangeSequence = false;

        // 序列状态修改
        if (!oldPrimaryKey.getSequenceStatus().equals(newPrimaryKey.getSequenceStatus())) {
            isChangeSequence = true;
        }

        // 序列名修改
        if (!oldPrimaryKey.getSequenceName().equals(newPrimaryKey.getSequenceName())) {
            isChangeSequence = true;
        }

        if (isChangeSequence) {
            // 序列如果有修改，都是先删除触发器，后新增
            // 删除除触发器
            String dropTriggerSql = getDropTriggerSql(tableName);
            if (StringUtils.isNoneBlank(dropTriggerSql)) {
                alterSql.getDeleteSql().add(dropTriggerSql);
            }

            // 新增触发器
            List<String> addTriggerSql = getAddTriggerSql(tableName, newPrimaryKey,
                    newPrimaryKeyColumn);
            if (CollectionUtils.isNotEmpty(addTriggerSql)) {
                alterSql.getSpecialSql().addAll(addTriggerSql);
            }
        }
        return alterSql;
    }

    private List<String> getAddTriggerSql(String tableName, TablePkOracle primaryKey,
                                          List<TableColumn> primaryKeyColumn) {
        // 不需要创建触发器
        if (primaryKey.getSequenceStatus() <= 2) {
            return null;
        }
        String sequenceName = primaryKey.getSequenceName();
        if (StringUtils.isBlank(sequenceName)) {
            throw new MetaDataException("alter primary key, create trigger sequenceName is null ");
        }

        List<String> commands = new ArrayList<>();

        if (primaryKey.getSequenceStatus() == 3) { // 从新序列填充
            // 需要去创建序列
            String createSequenceSql = String.format(CREATESEQUENCE, primaryKey.getSequenceName());
            commands.add(createSequenceSql);
            // 创建完序列后，将主键状态改成从序列中填充
            primaryKey.setSequenceStatus(4);
            tablePkOracleMapper.updateByPrimaryKeySelective(primaryKey);
        }

        // 创建触发器去将主键关联
        if (primaryKeyColumn.size() > 1) {
            throw new MetaDataException("序列不能对应多个字段");
        }
        TableColumn column = primaryKeyColumn.get(0);

        // 获取创建触发器的sql
        String addTriggerSql = String
                .format(ADDTRIGGER, addBackQuote(TRIGGER_PRE + tableName), addBackQuote(tableName),
                        sequenceName, addBackQuote(column.getColumnName()));
        commands.add(addTriggerSql);
        return commands;
    }

    private String getDropTriggerSql(String tableName) {
        String dropTriggerSql = String.format(DROPTRIGGER, TRIGGER_PRE + tableName);
        return dropTriggerSql;
    }

    private String getAddPrimaryKeySql(String tableName, String primaryKeyName,
                                       List<TableColumn> primaryKeyColumn) {
        if (CollectionUtils.isEmpty(primaryKeyColumn)) {
            // 当前没有主键
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (TableColumn column : primaryKeyColumn) {
            sb.append(addBackQuote(column.getColumnName())).append(" , ");
        }
        String addPrimaryKeySql = String
                .format(ADD_PRIMARY_KEY_NAME, tableName, primaryKeyName, sb.toString());
        return addPrimaryKeySql;
    }

    private String getDropPrimaryKeySql(String tableName, String primaryKeyName) {
        String dropPrimaryKeySql = String
                .format(DROP_CONSTRAINT, addBackQuote(tableName), addBackQuote(primaryKeyName));
        return dropPrimaryKeySql;
    }

    private AlterSqlVO getAlterColumnSql(String tableName, List<TableColumn> oldColumnList,
                                         List<TableColumn> newColumnList) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("alter Column, tableName is null");
        }
        if (CollectionUtils.isEmpty(oldColumnList)) {
            throw new MetaDataException("alter Column, oldColumnList is null");
        }
        if (CollectionUtils.isEmpty(newColumnList)) {
            throw new MetaDataException("alter Column, newColumnList is null");
        }

        // 老版本的列id
        ArrayList<Long> oldColumnIdList = getColumnVersionList(oldColumnList);

        // 新版本的列id
        ArrayList<Long> newColumnIdList = getColumnVersionList(newColumnList);

        // 复制旧表列id
        ArrayList<Long> oldCopy = Lists.newArrayList(oldColumnIdList);

        // 复制新表列id
        ArrayList<Long> newCopy = Lists.newArrayList(newColumnIdList);

        // oldCopy内容：删除的列
        oldCopy.removeAll(newCopy);

        // oldColumnIdList内容：被修改或者未被修改的列
        oldColumnIdList.removeAll(oldCopy);

        // newColumnIdList内容：里面就为新增加的列
        newColumnIdList.removeAll(oldColumnIdList);

        // 新增的列
        ArrayList<Long> addList = newColumnIdList;

        // 删除的列
        ArrayList<Long> deleteList = oldCopy;

        // 修改或者未改列，需要依次比较属性
        ArrayList<Long> modifyOrNorList = oldColumnIdList;

        // 返回新增加的列的bean集合
        ArrayList<TableColumn> addColumns = getAlterColumnBeans(addList, newColumnList);

        // 返回被删除的列的bean集合
        ArrayList<TableColumn> dropColumns = getAlterColumnBeans(deleteList, oldColumnList);

        // 返回旧表修改或者未修改的bean集合
        ArrayList<TableColumn> modifyOrNorOldColumns = getAlterColumnBeans(modifyOrNorList,
                oldColumnList);

        // 返回新表的修改或者未修改的bean集合
        ArrayList<TableColumn> modifyOrNorNewColumns = getAlterColumnBeans(modifyOrNorList,
                newColumnList);

        // 返回真正被修改的bean集合
        ArrayList<TableColumn> modifyColumns = getModifyColumns(modifyOrNorOldColumns,
                modifyOrNorNewColumns);

        // 旧表修改或者未修改数据，以id为key
        Map<Long, TableColumn> oldColumnMap =
                modifyOrNorOldColumns.stream()
                        .collect(Collectors.toMap((key -> key.getId()), (value -> value)));

        AlterSqlVO alterColumnSql = new AlterSqlVO();
        // 修改详情
        StringBuilder message = new StringBuilder();

        // 修改操作的commands 生成
        if (CollectionUtils.isNotEmpty(modifyColumns)) {
            List list = new ArrayList();
            for (TableColumn change : modifyColumns) {
                List<String> changeColumnList = changeColumn(tableName, change,
                        oldColumnMap.get(change.getId()));
                if (CollectionUtils.isNotEmpty(changeColumnList)) {
                    list.addAll(changeColumnList);
                }
            }
            alterColumnSql.setChangeSql(list);
            message.append("修改字段数").append(modifyColumns.size()).append(",");
        }

        // 删除操作的commands 生成
        if (CollectionUtils.isNotEmpty(dropColumns)) {
            if (dropColumns.size() == oldColumnList.size()) {
                throw new MetaDataException("违规操作，不能一次将之前字段全部删除");
            }
            List list = new ArrayList();
            for (TableColumn drop : dropColumns) {
                String dropColumnSql = dropColumn(tableName, drop);
                if (StringUtils.isNotBlank(dropColumnSql)) {
                    list.add(dropColumnSql);
                }
            }
            alterColumnSql.setDeleteSql(list);
            message.append("删除字段数").append(dropColumns.size()).append(",");
        }

        // 增加操作的commands 生成,追加到list
        if (CollectionUtils.isNotEmpty(addColumns)) {
            List list = new ArrayList();
            for (TableColumn add : addColumns) {
                String addColumnSql = addColumn(tableName, add);
                if (StringUtils.isNotBlank(addColumnSql)) {
                    list.add(addColumnSql);
                }
            }
            alterColumnSql.setAddSql(list);
            message.append("新增字段数").append(addColumns.size()).append(",");
        }

        alterColumnSql.setMessage(message.toString());
        return alterColumnSql;
    }

    private List<String> changeColumn(String tableName, TableColumn newColumn,
                                      TableColumn oldColumn) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("change column，table name is null");
        }
        if (newColumn == null) {
            throw new MetaDataException("change column，newColumn is null");
        }
        if (oldColumn == null) {
            throw new MetaDataException("change column，oldColumn is null");
        }

        List<String> changeSqls = new ArrayList<>();

        String newColumnName = newColumn.getColumnName(); // 新字段名
        String oldColumnName = oldColumn.getColumnName(); // 旧字段名
        // 判断列名是否为空
        if (StringUtils.isBlank(newColumnName) || StringUtils.isBlank(oldColumnName)) {
            throw new MetaDataException("change column, column name is null");
        }
        // 字段名有所修改
        if (!newColumnName.equals(oldColumnName)) {
            changeSqls.add(String
                    .format(RENAME_FIELD_NAME, addBackQuote(tableName), addBackQuote(oldColumnName),
                            addBackQuote(newColumnName)));
        }
        // 类型
        String dataType = getAndVerifiedDataType(newColumn.getColumnType());
        // 类型范围
        String typeLength = newColumn.getTypeLength();
        // 精度
        String typePrecision = newColumn.getTypePrecision();
        // 默认值
        String defaultValue = newColumn.getDefaultValue();
        // 是否为空
        Boolean isNull = newColumn.getIsNull();

        // 是否有类型范围限制,有就加上,没有就不加
        if (StringUtils.isNotBlank(typeLength)) {
            dataType += "(";
            dataType += typeLength;
            // 精度
            if (StringUtils.isNotBlank(typePrecision)) {
                dataType += "," + typePrecision;
            }
            dataType += ")";
        }

        String replenish = "";
        // 是否有默认值
        if (StringUtils.isNotBlank(defaultValue)) {
            replenish = "DEFAULT '" + defaultValue + "'";
        }
        // 是否为空
        if (!isNull) {
            replenish += " NOT NULL";
        }
        changeSqls.add(String
                .format(MODIFY_COLUMN, addBackQuote(tableName), addBackQuote(newColumnName),
                        dataType, replenish));
        return changeSqls;
    }

    private String addColumn(String tableName, TableColumn add) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("add column，table name is null");
        }
        if (add == null) {
            throw new MetaDataException("add column，column is null");
        }
        // 列名
        String columnName = add.getColumnName();
        // 类型
        String dataType = getAndVerifiedDataType(add.getColumnType());
        // 类型范围
        String typeLength = add.getTypeLength();
        // 精度
        String typePrecision = add.getTypePrecision();
        // 默认值
        String defaultValue = add.getDefaultValue();
        // 是否为空
        Boolean isNull = add.getIsNull();

        // 判断列名是否为空
        if (StringUtils.isBlank(columnName)) {
            throw new MetaDataException("add column, column name is null");
        }

        // 是否有类型范围限制,有就加上,没有就不加
        if (StringUtils.isNotBlank(typeLength)) {
            dataType += "(";
            dataType += typeLength;
            // 精度
            if (StringUtils.isNotBlank(typePrecision)) {
                dataType += "," + typePrecision;
            }
            dataType += ")";
        }

        String replenish = "";
        // 是否有默认值
        if (StringUtils.isNotBlank(defaultValue)) {
            replenish = "DEFAULT '" + defaultValue + "'";
        }
        // 是否为空
        if (!isNull) {
            replenish += " NOT NULL";
        }
        String addColumnSql = String
                .format(ADD_COLUMN, addBackQuote(tableName), columnName, dataType, replenish);
        return addColumnSql;
    }

    private String dropColumn(String tableName, TableColumn drop) {
        // 判断列bean是否为空
        if (drop == null) {
            throw new MetaDataException("drop Column, column bean is null");
        }

        // 列名
        String columnName = drop.getColumnName();
        // 判断列名是否为空
        if (StringUtils.isEmpty(columnName)) {
            throw new MetaDataException("drop Column, column name is null");
        }
        String dropColumnSql = String
                .format(DROP_COLUMN, addBackQuote(tableName), addBackQuote(columnName));
        return dropColumnSql;
    }

    private ArrayList<TableColumn> getModifyColumns(ArrayList<TableColumn> modifyOrNorOldColumns,
                                                    ArrayList<TableColumn> modifyOrNorNewColumns) {
        ArrayList<TableColumn> oldCopy = Lists.newArrayList(modifyOrNorOldColumns);
        ArrayList<TableColumn> newCopy = Lists.newArrayList(modifyOrNorNewColumns);

        // oldCopy内容：没有被修改的列的bean，通过集合交接获取
        oldCopy.retainAll(newCopy);

        // modifyOrNorNewColumns内容：新版本被修改的列bean
        modifyOrNorNewColumns.removeAll(oldCopy);
        return modifyOrNorNewColumns;
    }

    public ArrayList<TableColumn> getAlterColumnBeans(List<Long> idList,
                                                      List<TableColumn> tableColumnList) {
        ArrayList<TableColumn> result = new ArrayList<>();
        // 遍历拿到所需要的字段
        for (TableColumn column : tableColumnList) {
            Long columnId = column.getId();
            if (columnId == null) {
                throw new MetaDataException(" column id is null");
            }
            for (Long id : idList) {
                if (columnId.equals(id)) {
                    result.add(column);
                }
            }
        }
        return result;
    }

    private ArrayList<Long> getColumnVersionList(List<TableColumn> columnList) {
        ArrayList<Long> columnIdList = new ArrayList<>();
        for (TableColumn column : columnList) {
            if (column != null && column.getId() != null) {
                columnIdList.add(column.getId());
            }
        }
        return columnIdList;
    }

    private AlterSqlVO alterTableInfoSql(OracleTableVO oldTable, OracleTableVO newTable) {
        if (oldTable == null) {
            throw new MetaDataException("alter table base info，oldTable is null");
        }
        if (newTable == null) {
            throw new MetaDataException("alter table base info，newTable is null");
        }

        AlterSqlVO alterTableInfoSql = new AlterSqlVO();
        List<String> list = new ArrayList<>();
        StringBuilder message = new StringBuilder();

        // 如果表注释不一致，获取修改表注释的sql
        if ((oldTable.getIdentification() == null && newTable.getIdentification() != null) ||
                (oldTable.getIdentification() != null && newTable.getIdentification() == null) ||
                (oldTable.getIdentification() != null && newTable.getIdentification() != null
                        && !oldTable.getIdentification().equals(newTable.getIdentification())
                )) {
            String identification = "";
            if (StringUtils.isNotBlank(newTable.getIdentification())) {
                identification = newTable.getIdentification();
            }
            list.add(String.format(TABLE_COMMENT, addBackQuote(oldTable.getName()),
                    "'" + identification + "'"));
            message.append("修改表注释,");
        }

        // 如果表名不一致，获取修改表名的sql
        if (StringUtils.isNotBlank(oldTable.getName()) && StringUtils.isNotBlank(newTable.getName())
                && !oldTable.getName().equals(newTable.getName())) {
            list.add(String.format(ALTER_TABLE_NAME, addBackQuote(oldTable.getName()),
                    addBackQuote(newTable.getName())));
            message.append("修改表名,");
        }

        alterTableInfoSql.setChangeSql(list);
        alterTableInfoSql.setMessage(message.toString());
        return alterTableInfoSql;
    }

    // ====================================
    // ====================================
    // ====================================
    // ============= view
    // ====================================
    // ====================================
    // ====================================

    /**
     * CREATE [OR REPLACE] [{FORCE|NOFORCE}] VIEW view_name AS SELECT查询 [WITH READ ONLY CONSTRAINT]
     */

    // 创建视图：oracle暂时采用默认的创建视图，不指定特殊的设置
    private static final String CREATE_VIEW = "CREATE OR REPLACE NOFORCE VIEW %s AS %s ";

    // 删除视图
    private static final String DROP_VIEW = "DROP VIEW %s";

    @Override
    public String getCreateOrUpdateViewSql(String viewName, ViewDetail viewDetail) {
        if (StringUtils.isBlank(viewName)) {
            throw new MetaDataException("create view, view name is null");
        }
        if (viewDetail == null) {
            throw new MetaDataException("create view, viewDetail is null");
        }
        if (StringUtils.isBlank(viewDetail.getViewSql())) {
            throw new MetaDataException("create view, view sql is null");
        }
        String createViewSql = String
                .format(CREATE_VIEW, addBackQuote(viewName), viewDetail.getViewSql());
        return createViewSql;
    }

    @Override
    public AlterSqlVO getAlterViewSql(DBViewVO oldView, DBViewVO newView) {
        StringBuilder sb = new StringBuilder(); // 变更详情
        ArrayList<String> sqlList = new ArrayList<>(); // 变更sql

        if (!oldView.getName().equals(newView.getName())) {
            // 视图名有所修改，视图不能单独的去修改name，先将视图删除，再新建视图处理
            String dropView = String.format(DROP_VIEW, oldView.getName());
            sqlList.add(dropView);
        }

        if (!oldView.equals(newView)) {
            sb.append("基本信息修改").append(",");
        }

        ViewDetail oldDetail = oldView.getViewDetail();
        ViewDetail newDetail = newView.getViewDetail();

        if (!oldDetail.equals(newDetail)) {
            // 视图详情有所修改
            String createViewSql = getCreateOrUpdateViewSql(newView.getName(),
                    newView.getViewDetail());
            sqlList.add(createViewSql);
            sb.append("视图详情修改").append(",");
        }

        AlterSqlVO alterSqlVO = new AlterSqlVO();
        alterSqlVO.setMessage(sb.toString());
        alterSqlVO.setChangeSql(sqlList);
        return alterSqlVO;
    }

    @Override
    public List<String> getDropViewSql(List<String> viewNames) {
        if (CollectionUtils.isEmpty(viewNames)) {
            return null;
        }

        List<String> dropList = new ArrayList<>();
        for (String viewName : viewNames) {
            String dropView = String.format(DROP_VIEW, viewName);
            dropList.add(dropView);
        }
        return dropList;
    }

    @Override
    public RdbLinkDto getConnectionConfig(Metadata metadata) {
        if (metadata == null) {
            throw new MetaDataException("错误的元数据信息");
        }
        McSchemaPO schema = schemaService.findById(metadata.getSchemaId());
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if (!datasource.getType().equals(DatabaseTypeEnum.ORACLE.getCode() + "")) {
            throw new MetaDataException("错误的数据库类型");
        }
        // TODO 密码以后会进行加密
        RdbLinkDto connectionConfig = new RdbLinkDto(schema.getUsername(),
                schema.getPassword(), "ORACLE", datasource.getIp(), datasource.getPort(),
                schema.getServiceName());
        return connectionConfig;
    }

    @Override
    public void goToDatabase(Metadata metadata, ArrayList<String> commands) {
        RdbLinkDto connectionConfig = getConnectionConfig(metadata);
        try {
            // 调用db proxy 运行sql
            if (CollectionUtils.isNotEmpty(commands)) {
                ArrayList list = new ArrayList();
                list.addAll(commands);
                RespResult<SqlExecRespDto> result = oracleService
                        .batchExecuteUpdate(list, connectionConfig);
                log.info("db proxy result：{}", result);
                if (!result.isSuccess()) {
                    throw new MetaDataException(ResultCodeEnum.DATABASE_ERROR.getCode(),
                            "生效到数据库失败，信息：" + result.getMsg());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 生效到数据库失败后，前台根据code来做处理
            throw new MetaDataException(ResultCodeEnum.DATABASE_ERROR.getCode(),
                    "生效到数据库失败，信息：" + e.getMessage());
        }
    }

    @Override
    public void specialSqlGoToDatabase(Metadata metadata, List<String> specialSql) {
        RdbLinkDto connectionConfig = getConnectionConfig(metadata);
        try {
            // 调用db proxy 运行sql
            if (CollectionUtils.isNotEmpty(specialSql)) {
                RespResult<Boolean> result = oracleService.execute(specialSql, connectionConfig);
                log.info("db proxy result：{}", result);
                if (!result.isSuccess()) {
                    throw new MetaDataException(ResultCodeEnum.DATABASE_ERROR.getCode(),
                            "生效到数据库失败，信息：" + result.getMsg());
                }
            }
        } catch (Exception e) {
            // 生效到数据库失败后，前台根据code来做处理
            throw new MetaDataException(ResultCodeEnum.DATABASE_ERROR.getCode(),
                    "生效到数据库失败，信息：" + e.getMessage());
        }
    }

    protected String addBackQuote(String value) {
        if (StringUtils.isNotBlank(value)) {
            String valueArray[] = value.split("\\.");
            StringBuilder valueSb = new StringBuilder();
            for (String str : valueArray) {
                valueSb.append("\"").append(str).append("\"").append(".");
            }
            valueSb.deleteCharAt(valueSb.lastIndexOf("."));
            return valueSb.toString();
        }
        return value;
    }

    protected String getAndVerifiedDataType(String dataType) {
        // 判断列的类型是否为空
        if (null == dataType) {
            throw new MetaDataException("column dataType is null");
        }
        String realDataType = DBEnum.OracleTableDataType.valueOf(dataType.toUpperCase()).getName();
        return realDataType;
    }

    // 验证并获取索引类型
    public String getAndVerifiedIndexType(String indexType) {
        // 判断索引是否为空
        if (StringUtils.isBlank(indexType)) {
            throw new MetaDataException("index indexType is null");
        }
        try {
            return DBEnum.OracleIndexType.valueOf(indexType.toUpperCase()).getName();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MetaDataException("错误的索引类型：" + indexType);
        }
    }

    protected String getAndVerifiedTriggerAffair(String affair) {
        // 判断列的类型是否为空
        if (null == affair) {
            throw new MetaDataException("affair is null");
        }
        return DBEnum.OracleFKTriggerAffairEnum.valueOf(affair.toUpperCase()).getName();
    }
}
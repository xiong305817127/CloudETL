package com.ys.idatrix.metacube.metamanage.service.impl;

import com.google.common.collect.Lists;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.rdb.service.MysqlService;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.common.enums.DBEnum;
import com.ys.idatrix.metacube.common.enums.ResultCodeEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.McDatabaseMapper;
import com.ys.idatrix.metacube.metamanage.service.McSchemaService;
import com.ys.idatrix.metacube.metamanage.service.MetadataService;
import com.ys.idatrix.metacube.metamanage.service.MySqlDDLService;
import com.ys.idatrix.metacube.metamanage.service.TableColumnService;
import com.ys.idatrix.metacube.metamanage.vo.request.AlterSqlVO;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MySqlTableVO;
import com.ys.idatrix.metacube.metamanage.vo.response.DatasourceVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName MySqlDDLServiceImpl
 * @Description mysql ddl 操作sql拼装
 * @Author ouyang
 * @Date
 */
@Transactional
@Slf4j
@Service
public class MySqlDDLServiceImpl implements MySqlDDLService {

    /**
     * 分隔符
     */
    private static final String SEPARATOR = ",";

    /**
     * 字符单引号
     */
    private static final String QUOTATION = "'";

    // 删除表结构
    private static final String DROP_TABLE = "DROP TABLE %s";

    // 修改表注释
    private static final String ALTER_TABLE_COMMENT = "ALTER TABLE %s COMMENT %s";

    // 修改表名
    private static final String RENAME_TABLE = "RENAME TABLE %s TO %s";


    // 新增字段
    private static final String ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s %s %s";

    // 修改字段
    private static final String CHANGE_COLUMN = "ALTER TABLE %s CHANGE %s %s %s %s"; // tableName, oldName, newName, dataType, 补充（是否为空，默认值等等）

    // 删除字段
    private static final String DROP_COLUMN = "ALTER TABLE %s DROP COLUMN %s";


    // 删除主键
    private static final String DROP_PRIMARY_KEY = "ALTER TABLE %s DROP PRIMARY KEY";

    // 新增主键
    private static final String ADD_PRIMARY_KEY = "ALTER TABLE %s ADD PRIMARY KEY(%s)";

    // 新增索引
    private static final String ADD_INDEX = "ALTER TABLE %s ADD %s %s %s"; // tableName, indexType, indexName , 补充（索引关联字段和索引方法）

    // 删除索引
    private static final String DROP_INDEX = "DROP INDEX %s ON %s";


    // 新增外键
    private static final String ADD_FOREIGN_KEY = "ALTER TABLE　{1} ADD FOREIGN KEY {2}{3} REFERENCE VIP (id);";

    // 删除外键
    private static final String DROP_FOREIGN_KEY = "ALTER TABLE %s DROP FOREIGN KEY %s";


    @Autowired
    @Qualifier("mySqlSchemaService")
    private McSchemaService schemaService;

    @Autowired(required = false)
    private MysqlService mysqlService;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private TableColumnService tableColumnService;

    @Autowired
    private McDatabaseMapper databaseMapper;

    // ===================================================================
    // ===================================================================
    // ===================================================================
    //                              table
    // ===================================================================
    // ===================================================================
    // ===================================================================

    // ================================
    //              create
    // ================================
    @Override
    public ArrayList<String> getCreateTableSql(Metadata table, List<TableColumn> tableColumnList,
            List<TableIdxMysql> tableIndexList, List<TableFkMysql> tableFkMysqlList) {
        if (table == null || StringUtils.isBlank(table.getName())) {
            throw new MetaDataException("create table, table info error");
        }
        if (CollectionUtils.isEmpty(tableColumnList)) {
            throw new MetaDataException("create table, columns is null");
        }

        StringBuilder strPK = null; // 主键
        StringBuilder sb = new StringBuilder("CREATE TABLE "); // create table tableName
        sb.append(addBackQuote(table.getName())).append(" ( "); // 表名
        for (TableColumn column : tableColumnList) {// 循环每个字段
            String columnName = column.getColumnName(); // 列名
            String columnType = column.getColumnType(); // 类型
            String typeLength = column.getTypeLength(); // 类型范围
            String typePrecision = column.getTypePrecision(); // 精度

            if (column.getIsPk()) { // 主键拼装
                if (strPK == null) {
                    strPK = new StringBuilder();
                    strPK.append(" primary key ( ");
                }
                strPK.append(addBackQuote(columnName)).append(SEPARATOR);
            }

            sb.append(addBackQuote(columnName) + " ")
                    .append(getAndVerifiedDataType(columnType) + " "); // 字段名和字段类型

            // 是否有类型范围限制,有就加上,没有就不加
            if (StringUtils.isNotBlank(typeLength)) {
                sb.append("(");
                sb.append(typeLength);
                // 是否有精度
                if (StringUtils.isNotBlank(typePrecision)) {
                    sb.append("," + typePrecision);
                }
                sb.append(") ");
            }

            // 拼装字段其他属性
            defineColumnSyntax(sb, column);
            // 拼装分隔符
            sb.append(SEPARATOR);
        }

        // 主键删除最后一个逗号
        if (strPK != null && strPK.lastIndexOf(SEPARATOR) > -1) {
            strPK.deleteCharAt(strPK.lastIndexOf(SEPARATOR));
            strPK.append(")").append(SEPARATOR); // 主键拼装结束
            sb.append(strPK);
        }

        // 获取建索引的sql
        String createIndexSql = getCreateIndexSql(tableIndexList, tableColumnList);
        if (StringUtils.isNotBlank(createIndexSql)) {
            log.info("create index sql is : {}", createIndexSql);
            sb.append(createIndexSql);
        }

        // 获取建外键的sql
        String createForeignKeySql = getCreateForeignKeySql(tableFkMysqlList, tableColumnList);
        if (StringUtils.isNotBlank(createForeignKeySql)) {
            log.info("create foreign key sql is : {}", createForeignKeySql);
            sb.append(createForeignKeySql);
        }

        // TODO 新建外键时，关联列和参考列都需要新建索引（外键关联的字段必须要有索引）

        // 后处理
        after(sb);

        // 添加表注释
        if (StringUtils.isNotBlank(table.getIdentification())) {
            sb.append(" COMMENT='").append(table.getIdentification()).append("' ");
        }

        sb.append("ENGINE=" + DBEnum.MysqlEngineType.INNODB); // 数据引擎默认值
        sb.append(" DEFAULT CHARSET=" + DBEnum.CharSet.utf8); // 字符集

        log.info("create table sql : {}", sb.toString());
        ArrayList<String> list = new ArrayList<>();
        list.add(sb.toString());
        return list;
    }

    // 后处理
    private void after(StringBuilder mainStr) {
        // 删除最后一个逗号
        if (mainStr.lastIndexOf(SEPARATOR) > -1) {
            mainStr.deleteCharAt(mainStr.lastIndexOf(SEPARATOR));
        }
        mainStr.append(" ) ");
    }

    /**
     * CREATE TABLE 表名(字段名 数据类型 [完整性约束条件], [UNIQUE | FULLTEXT | SPATIAL] INDEX | KEY [索引名](字段名1
     * [(长度)] [ASC | DESC]) );
     */
    private String getCreateIndexSql(List<TableIdxMysql> tableIndexList,
            List<TableColumn> tableColumnList) {
        if (CollectionUtils.isEmpty(tableIndexList)) {
            return null;
        }
        // 当前表所有的列
        Map<Long, TableColumn> columnMap =
                tableColumnList.stream()
                        .collect(Collectors.toMap((key -> key.getId()), (value -> value)));

        StringBuilder sb = new StringBuilder();

        for (TableIdxMysql index : tableIndexList) {
            if (StringUtils.isBlank(index.getIndexName())) {
                throw new MetaDataException("create index, index name is null ");
            }
            if (StringUtils.isBlank(index.getColumnIds())) {
                throw new MetaDataException("create index, index columns is null ");
            }

            // 验证并获取索引类型
            String indexType = getAndVerifiedIndexType(index.getIndexType());

            if (!indexType.equals("INDEX")) {
                sb.append(indexType).append(" ");
            }
            sb.append(" INDEX").append(" ")
                    .append(addBackQuote(index.getIndexName())); // 拼装 索引类型 和 索引名

            // 索引列的处理
            sb.append(" (");
            String[] columnIdArr = index.getColumnIds().split(",");
            for (String colId : columnIdArr) {

                if (StringUtils.isBlank(colId)) {
                    throw new MetaDataException(
                            "index name :" + index.getIndexName() + " column error");
                }

                TableColumn tableColumn = columnMap.get(Long.parseLong(colId)); // 当前索引对应的列
                if (tableColumn == null) {
                    throw new MetaDataException(
                            "index name :" + index.getIndexName() + " column error");
                }

                // FULLTEXT is not support some data type
                if (DBEnum.MysqlIndexTypeEnum.FULLTEXT.getName().equals(indexType)) {
                    if (!DBEnum.MysqlTableDataType.CHAR.name().equals(tableColumn.getColumnType())
                            ||
                            !DBEnum.MysqlTableDataType.VARCHAR.name()
                                    .equals(tableColumn.getColumnType()) ||
                            !DBEnum.MysqlTableDataType.TEXT.name()
                                    .equals(tableColumn.getColumnType()) ||
                            !DBEnum.MysqlTableDataType.MEDIUMTEXT.name()
                                    .equals(tableColumn.getColumnType()) ||
                            !DBEnum.MysqlTableDataType.LONGTEXT.name()
                                    .equals(tableColumn.getColumnType())) {
                        throw new MetaDataException(
                                "the index type FULLTEXT is not support data type:" + tableColumn
                                        .getColumnType());
                    }
                }

                sb.append(addBackQuote(tableColumn.getColumnName())).append(" , ");
            }
            // 删除最后一个逗号
            if (sb.lastIndexOf(SEPARATOR) > -1) {
                sb.deleteCharAt(sb.lastIndexOf(SEPARATOR));
            }
            sb.append(")"); // 列拼装结束
            sb.append(SEPARATOR); // 一个index拼装结束
        }
        return sb.toString();
    }

    /**
     * constraint FK_Name foreign key (CharID) references ChineseCharInfo(ID) on delete cascade on
     * update cascade
     */
    private String getCreateForeignKeySql(List<TableFkMysql> tableFkMysqlList,
            List<TableColumn> tableColumnList) {
        if (CollectionUtils.isEmpty(tableFkMysqlList)) {
            return null;
        }
        // 当前表列名
        Map<Long, TableColumn> columnMap =
                tableColumnList.stream()
                        .collect(Collectors.toMap((key -> key.getId()), (value -> value)));

        StringBuilder sb = new StringBuilder();

        for (TableFkMysql foreignKey : tableFkMysqlList) {
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

            sb.append("CONSTRAINT").append(addBackQuote(foreignKey.getName()))
                    .append(" FOREIGN KEY");
            // 外键列处理
            sb.append(" (");
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
                sb.append(addBackQuote(tableColumn.getColumnName())).append(" , ");
            }
            // 删除最后一个逗号
            if (sb.lastIndexOf(SEPARATOR) > -1) {
                sb.deleteCharAt(sb.lastIndexOf(SEPARATOR));
            }
            sb.append(")"); // 列拼装结束

            // 外键参考表处理
            Metadata referenceTable = metadataService.findById(foreignKey.getReferenceTableId());
            if (referenceTable == null) {
                throw new MetaDataException(
                        "foreign key name：" + foreignKey.getName() + "，error reference table");
            }
            sb.append(" references " + addBackQuote(referenceTable.getName())); // 参考表

            // 参考列处理
            sb.append(" (");
            // 参考表的所有列
            List<TableColumn> referenceTableColumnList = tableColumnService
                    .getTableColumnListByTableId(foreignKey.getReferenceTableId());
            Map<Long, TableColumn> referenceTableColumnMap =
                    referenceTableColumnList.stream()
                            .collect(Collectors.toMap((key -> key.getId()), (value -> value)));

            for (int i = 0; i < referenceColumnIdArr.length; i++) {
                String referenceColumnId = referenceColumnIdArr[i]; // 参考列
                TableColumn referenceCol = referenceTableColumnMap
                        .get(Long.parseLong(referenceColumnId));
                if (StringUtils.isBlank(referenceColumnId) || referenceCol == null) {
                    throw new MetaDataException(
                            "foreign key name：" + foreignKey.getName() + "，reference column error");
                }

                String tableColId = columnIdArr[i]; // 参考列对应的关联列
                TableColumn tableCol = columnMap.get(Long.parseLong(tableColId));
                if (!tableCol.getColumnType().equals(referenceCol.getColumnType())) {
                    throw new MetaDataException("foreign key name：" + foreignKey.getName()
                            + "， column and reference column data type error");
                }

                sb.append(addBackQuote(referenceCol.getColumnName())).append(" , ");
            }
            // 删除最后一个逗号
            if (sb.lastIndexOf(SEPARATOR) > -1) {
                sb.deleteCharAt(sb.lastIndexOf(SEPARATOR));
            }
            sb.append(") "); // 参考列拼装结束

            // 验证并获取触发事件类型
            String deleteTriggerAffair = getAndVerifiedTriggerAffair(
                    foreignKey.getDeleteTrigger()); // 删除时触发事件
            String updateTriggerAffair = getAndVerifiedTriggerAffair(
                    foreignKey.getUpdateTrigger()); // 修改时触发事件
            sb.append("ON DELETE " + deleteTriggerAffair + " "); // 删除时触发
            sb.append("ON UPDATE " + updateTriggerAffair); // 修改时触发
            sb.append(SEPARATOR);
        }
        return sb.toString();
    }

    // =========================================
    // =========================================
    //                  update
    // =========================================
    // =========================================

    @Override
    public AlterSqlVO getAlterTableSql(MySqlTableVO newTable, MySqlTableVO snapshotTable) {
        if (newTable == null || snapshotTable == null) {
            throw new MetaDataException("alter table，newTable or snapshotTable is null");
        }

        // 当前用户数据库中的实体表还是快照中的数据
        String snapshotTableName = snapshotTable.getName();
        if (StringUtils.isBlank(snapshotTableName)) {
            throw new MetaDataException("alter table，table name is null");
        }

        ArrayList<String> allCommands = new ArrayList<>(); // 要执行sql
        StringBuilder message = new StringBuilder(); // 版本变更详情

        // 获取表基本信息修改sql(表名，表注释)
        AlterSqlVO alterTableInfoVo = alterTableInfoSql(newTable, snapshotTable);

        // 获取修改表字段的sql
        AlterSqlVO alterColumnVo = getAlterColumnSql(snapshotTableName,
                snapshotTable.getTableColumnList(), newTable.getTableColumnList());

        // 获取修改表主键sql
        AlterSqlVO alterPrimaryKeyVo = getAlterPrimaryKeySql(snapshotTableName,
                snapshotTable.getTableColumnList(), newTable.getTableColumnList());

        // 获取修改自增长的sql
        AlterSqlVO alterAutoIncrementVo = alterAutoIncrementSql(snapshotTableName,
                snapshotTable.getTableColumnList(), newTable.getTableColumnList());

        // 获取修改索引的sql
        AlterSqlVO alterIndexVo = getAlterIndexSql(snapshotTableName,
                snapshotTable.getTableIndexList(), newTable.getTableIndexList(),
                newTable.getTableColumnList());

        // 获取修改外键的sql
        AlterSqlVO alterForeignKeyVo = alterForeignKeySql(snapshotTableName,
                snapshotTable.getTableFkMysqlList(), newTable.getTableFkMysqlList(),
                newTable.getTableColumnList());

        /**
         * sql执行顺序：
         * 1.删除外键 2.删除自增长 3.删除主键 4.删除索引 5.表字段的修改（修改，删除，新增）
         * 6.新增索引 7.新增主键 8.新增自增长 9.新增外键 10.表基本信息修改
         *
         * 直观依赖：外键依赖索引，索引依赖字段，主键相对来说也是约束（唯一不重复，不为空）
         * 特殊情况：字段在自增时，出现字段依赖索引
         *
         */
        // 删除外键
        if (alterForeignKeyVo != null) {
            if (CollectionUtils.isNotEmpty(alterForeignKeyVo.getDeleteSql())) {
                allCommands.addAll(alterForeignKeyVo.getDeleteSql());
            }
        }

        // 删除自增长
        if(alterAutoIncrementVo != null) {
            if (CollectionUtils.isNotEmpty(alterAutoIncrementVo.getDeleteSql())) {
                allCommands.addAll(alterAutoIncrementVo.getDeleteSql());
            }
        }

        // 删除主键
        if (alterPrimaryKeyVo != null) {
            if (CollectionUtils.isNotEmpty(alterPrimaryKeyVo.getDeleteSql())) {
                allCommands.addAll(alterPrimaryKeyVo.getDeleteSql());
            }
        }

        // 删除索引
        if (alterIndexVo != null) {
            if (CollectionUtils.isNotEmpty(alterIndexVo.getDeleteSql())) {
                allCommands.addAll(alterIndexVo.getDeleteSql());
            }
        }

        // 表字段的修改
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

        // 新增主键
        if (alterPrimaryKeyVo != null) {
            if (CollectionUtils.isNotEmpty(alterPrimaryKeyVo.getAddSql())) {
                allCommands.addAll(alterPrimaryKeyVo.getAddSql());
            }
        }

        // 新增自增长
        if (alterAutoIncrementVo != null) {
            if (CollectionUtils.isNotEmpty(alterAutoIncrementVo.getAddSql())) {
                allCommands.addAll(alterAutoIncrementVo.getAddSql());
            }
        }

        // 新增外键
        if (alterForeignKeyVo != null) {
            if (CollectionUtils.isNotEmpty(alterForeignKeyVo.getAddSql())) {
                allCommands.addAll(alterForeignKeyVo.getAddSql());
            }
        }

        // 修改表的基本信息
        if (alterTableInfoVo != null) {
            // 修改sql
            if (CollectionUtils.isNotEmpty(alterTableInfoVo.getChangeSql())) {
                allCommands.addAll(alterTableInfoVo.getChangeSql());
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

        AlterSqlVO alterSql = new AlterSqlVO();
        alterSql.setChangeSql(allCommands);
        alterSql.setMessage(message.toString());
        return alterSql;
    }

    private AlterSqlVO alterAutoIncrementSql(String snapshotTableName, List<TableColumn> snapshotTableColumn,
                                             List<TableColumn> newTableColumn) {
        if (StringUtils.isBlank(snapshotTableName)) {
            throw new MetaDataException("alter auto increment, tableName is null");
        }
        if (CollectionUtils.isEmpty(snapshotTableColumn) || CollectionUtils.isEmpty(newTableColumn)) {
            throw new MetaDataException("alter auto increment, snapshotTableColumn or snapshotTableColumn is null");
        }

        TableColumn newAutoIncrementColumn = null; // 新自增长字段
        TableColumn snapshotAutoIncrementColumn = null; // 旧自增长字段

        for (TableColumn column : newTableColumn) {
            if(column.getIsAutoIncrement()) {
                newAutoIncrementColumn = column;
            }
        }
        for (TableColumn column : snapshotTableColumn) {
            if(column.getIsAutoIncrement()) {
                snapshotAutoIncrementColumn = column;
            }
        }

        // 没有自增长列，或没有修改自增长
        if(newAutoIncrementColumn == null && snapshotAutoIncrementColumn == null ||
                (newAutoIncrementColumn != null && snapshotAutoIncrementColumn != null
                        && newAutoIncrementColumn.equals(snapshotAutoIncrementColumn))) {
            return null;
        }

        AlterSqlVO vo = new AlterSqlVO();
        // 自增长有修改。先删除自增长，再新增。
        if(snapshotAutoIncrementColumn != null) {
            String changeColumnSql = changeAutoIncrementColumn(snapshotTableName, snapshotAutoIncrementColumn, snapshotAutoIncrementColumn, false);
            vo.getDeleteSql().add(changeColumnSql);
        }
        if(newAutoIncrementColumn != null) {
            String changeColumnSql = changeAutoIncrementColumn(snapshotTableName, newAutoIncrementColumn, newAutoIncrementColumn, true);
            vo.getAddSql().add(changeColumnSql);
        }
        return vo;
    }

    @Override
    public List<String> getDeleteTableSql(List<String> removeTableNames) {
        if (CollectionUtils.isEmpty(removeTableNames)) {
            return null;
        }
        List<String> result = new ArrayList<>();
        for (String tableName : removeTableNames) {
            if (StringUtils.isNotBlank(tableName)) {
                result.add(String.format(DROP_TABLE, tableName));
            }
        }
        return result;
    }

    private AlterSqlVO getAlterPrimaryKeySql(String snapshotTableName,
            List<TableColumn> snapshotTableColumn, List<TableColumn> newTableColumn) {
        if (StringUtils.isBlank(snapshotTableName)) {
            throw new MetaDataException("alter foreign key, tableName is null");
        }
        if (CollectionUtils.isEmpty(snapshotTableColumn) || CollectionUtils
                .isEmpty(newTableColumn)) {
            throw new MetaDataException(
                    "alter primary key, snapshotTableColumn or newTableColumn is null");
        }

        List<TableColumn> oldPrimaryKeyColumn = new ArrayList();
        List<TableColumn> newPrimaryKeyColumn = new ArrayList();

        for (TableColumn column : snapshotTableColumn) {
            if (column.getIsPk()) {
                oldPrimaryKeyColumn.add(column);
            }
        }

        for (TableColumn column : newTableColumn) {
            if (column.getIsPk()) {
                newPrimaryKeyColumn.add(column);
            }
        }

        ArrayList<TableColumn> oldPrimaryKey = Lists.newArrayList(oldPrimaryKeyColumn); // 之前主键
        ArrayList<TableColumn> newPrimaryKey = Lists.newArrayList(newPrimaryKeyColumn); // 现在主键

        ArrayList<TableColumn> oldCopy = Lists.newArrayList(oldPrimaryKeyColumn); // 之前主键
        ArrayList<TableColumn> newCopy = Lists.newArrayList(newPrimaryKeyColumn); // 现在主键

        // 求交集
        oldCopy.retainAll(newCopy);
        oldPrimaryKey.removeAll(oldCopy);
        newPrimaryKey.removeAll(oldCopy);

        // 如果删除交集数据后，集合为主0，则主键没有改变
        if (oldPrimaryKey.size() == 0 &&  newPrimaryKey.size()== 0) {
            return null;
        }

        // 如果不一致，就修改主键，先删除，后修改
        AlterSqlVO alterSql = new AlterSqlVO();

        for (TableColumn column : oldPrimaryKeyColumn) {
            // 删除主键前先删除自增长
            if (column.getIsPk()) { // 如果是主键
                if (column.getIsAutoIncrement()) { // 如果是自增
                    TableColumn columnCopy = new TableColumn();
                    BeanUtils.copyProperties(column, columnCopy);
                    columnCopy.setIsAutoIncrement(false); // 自增长置空
                    String drop = changeColumn(snapshotTableName, column, columnCopy);
                    alterSql.getDeleteSql().add(drop);
                }
            }
        }

        // 先删除主键
        if(CollectionUtils.isNotEmpty(oldPrimaryKeyColumn)) {
            String dropPrimaryKeySql = getDropPrimaryKeySql(snapshotTableName);
            if (StringUtils.isNoneBlank(dropPrimaryKeySql)) {
                alterSql.getDeleteSql().add(dropPrimaryKeySql);
            }
        }
        // 后新增主键
        if(CollectionUtils.isNotEmpty(newPrimaryKeyColumn)) {
            String addPrimaryKeySql = getAddPrimaryKeySql(snapshotTableName, newPrimaryKeyColumn);
            if (StringUtils.isNoneBlank(addPrimaryKeySql)) {
                alterSql.getAddSql().add(addPrimaryKeySql);
            }
        }
        alterSql.setMessage("修改主键,");
        return alterSql;
    }

    private String getAddPrimaryKeySql(String tableName, List<TableColumn> primaryKeyColumn) {
        if (CollectionUtils.isEmpty(primaryKeyColumn)) {
            // 当前没有主键
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (TableColumn column : primaryKeyColumn) {
            sb.append(addBackQuote(column.getColumnName())).append(" , ");
        }
        // 删除最后一个逗号
        if (sb.lastIndexOf(SEPARATOR) > -1) {
            sb.deleteCharAt(sb.lastIndexOf(SEPARATOR));
        }
        // alter table table_test add primary key(id);
        String addPrimaryKey = String
                .format(ADD_PRIMARY_KEY, addBackQuote(tableName), sb.toString());
        return addPrimaryKey;
    }

    private String getDropPrimaryKeySql(String tableName) {
        // alter table table_1 drop primary key
        return String.format(DROP_PRIMARY_KEY, addBackQuote(tableName));
    }

    private AlterSqlVO alterForeignKeySql(String tableName,
            List<TableFkMysql> snapshotTableFkMysqlList, List<TableFkMysql> newTableFkMysqlList,
            List<TableColumn> columns) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("alter foreign key, tableName is null");
        }
        if (CollectionUtils.isEmpty(newTableFkMysqlList) && CollectionUtils
                .isEmpty(snapshotTableFkMysqlList)) {
            log.warn(
                    "alter foreign key, no changed, snapshotTableFkMysqlList and  newTableFkMysqlList is null ");
            return null;
        }
        if (CollectionUtils.isEmpty(columns)) {
            throw new MetaDataException("alter foreign key, columns is null");
        }
        /**
         * 先删除被修改的外键
         * 后新增被修改的外键
         * 只要是被修改，那么就当做删除来做
         */
        // 如果新或旧的外键集合其中一个为null,则主动一个空ArrayList,便于后续处理
        if (snapshotTableFkMysqlList == null) {
            snapshotTableFkMysqlList = new ArrayList<>();
        }
        if (newTableFkMysqlList == null) {
            newTableFkMysqlList = new ArrayList<>();
        }

        // copy一份不要修改到之前参数
        ArrayList<TableFkMysql> oldForeignKeyList = Lists.newArrayList(snapshotTableFkMysqlList);
        ArrayList<TableFkMysql> newForeignKeyList = Lists.newArrayList(newTableFkMysqlList);

        // 在copy一份做参照
        ArrayList<TableFkMysql> oldCopy = Lists.newArrayList(snapshotTableFkMysqlList);
        ArrayList<TableFkMysql> newCopy = Lists.newArrayList(newTableFkMysqlList);

        // 不变的索引bean集合 //求交集。自定义对象重写 hashcode 和 equals
        oldCopy.retainAll(newCopy);

        // 待删除的外键集合，解释：被修改或以被删除的外键
        oldForeignKeyList.removeAll(oldCopy);

        // 待新增的外键集合，解释：被修改或新增的外键
        newForeignKeyList.removeAll(oldCopy);

        AlterSqlVO alterSql = new AlterSqlVO();

        // 先删除外键
        List<String> dropForeignKeyCommands = getDropForeignKeySql(tableName, oldForeignKeyList);
        if (CollectionUtils.isNotEmpty(dropForeignKeyCommands)) {
            alterSql.setDeleteSql(dropForeignKeyCommands);
        }

        // 后新增外键
        List<String> addForeignKeyCommands = getAddForeignKeySql(tableName, newForeignKeyList,
                columns);
        if (CollectionUtils.isNotEmpty(addForeignKeyCommands)) {
            alterSql.setAddSql(addForeignKeyCommands);
        }

        // TODO 外键修改信息
        StringBuffer message = new StringBuffer();

        return alterSql;
    }

    private List<String> getAddForeignKeySql(String tableName, List<TableFkMysql> foreignKeyList,
            List<TableColumn> columns) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("add foreign key，table name is null");
        }
        if (CollectionUtils.isEmpty(foreignKeyList)) {
            return null;
        }
        if (CollectionUtils.isEmpty(columns)) {
            throw new MetaDataException("add foreign key，columns is null");
        }

        // 当前表列，id为key
        Map<Long, TableColumn> columnMap =
                columns.stream().collect(Collectors.toMap((key -> key.getId()), (value -> value)));

        ArrayList<String> commands = Lists.newArrayList(); // 要返回的sql

        for (TableFkMysql foreignKey : foreignKeyList) {
            if (StringUtils.isBlank(foreignKey.getName())) {
                throw new MetaDataException("create foreign key, foreign key name is null ");
            }
            if (StringUtils.isBlank(foreignKey.getColumnIds())) {
                throw new MetaDataException("create foreign key, foreign key columns is null ");
            }
            if (StringUtils.isBlank(foreignKey.getReferenceColumn())) {
                throw new MetaDataException("create foreign key, reference columns is null ");
            }

            String[] columnIdArr = foreignKey.getColumnIds().split(",");
            String[] referenceColumnIdArr = foreignKey.getReferenceColumn().split(",");
            if (columnIdArr.length != referenceColumnIdArr.length) { // 外键字段与参考字段数量必须一致
                throw new MetaDataException(
                        "create foreign key, this column count and reference column count quantity discrepancy ");
            }

            StringBuilder sb = new StringBuilder();
            // ALTER TABLE `table_3` ADD CONSTRAINT `fk_cs1` FOREIGN KEY  (`cs_id_1`) REFERENCES `table_2` (`cs_1_update_2`) ON DELETE RESTRICT ON UPDATE RESTRICT
            sb.append("ALTER TABLE ");// alter table tableName add foreign key
            sb.append(addBackQuote(tableName));
            sb.append(" ADD CONSTRAINT ").append(addBackQuote(foreignKey.getName()));// 外键名
            sb.append(" FOREIGN KEY");

            // 外键列处理
            sb.append(" (");
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
                sb.append(addBackQuote(tableColumn.getColumnName())).append(" , ");
            }
            // 删除最后一个逗号
            if (sb.lastIndexOf(SEPARATOR) > -1) {
                sb.deleteCharAt(sb.lastIndexOf(SEPARATOR));
            }
            sb.append(")"); // 列拼装结束

            // 外键参考表处理
            Metadata referenceTable = metadataService.findById(foreignKey.getReferenceTableId());
            if (referenceTable == null) {
                throw new MetaDataException(
                        "foreign key name：" + foreignKey.getName() + "，reference table error");
            }
            sb.append(" REFERENCES ");
            sb.append(addBackQuote(referenceTable.getName())); // 参考表

            // 参考列处理
            sb.append(" (");
            // 参考表的所有列
            List<TableColumn> referenceTableColumnList = tableColumnService
                    .getTableColumnListByTableId(foreignKey.getReferenceTableId());
            Map<Long, TableColumn> referenceTableColumnMap =
                    referenceTableColumnList.stream()
                            .collect(Collectors.toMap((key -> key.getId()), (value -> value)));

            for (int i = 0; i < referenceColumnIdArr.length; i++) {
                String referenceColumnId = referenceColumnIdArr[i]; // 参考列
                TableColumn referenceCol = referenceTableColumnMap
                        .get(Long.parseLong(referenceColumnId));
                if (StringUtils.isBlank(referenceColumnId) || referenceCol == null) {
                    throw new MetaDataException(
                            "foreign key name：" + foreignKey.getName() + "，reference column error");
                }

                String tableColId = columnIdArr[i]; // 参考列对应的关联列
                TableColumn tableCol = columnMap.get(Long.parseLong(tableColId));
                if (!tableCol.getColumnType().equals(referenceCol.getColumnType())) {
                    throw new MetaDataException("foreign key name：" + foreignKey.getName()
                            + "， column and reference column data type error");
                }
                sb.append(addBackQuote(referenceCol.getColumnName())).append(" , ");
            }

            // 删除最后一个逗号
            if (sb.lastIndexOf(SEPARATOR) > -1) {
                sb.deleteCharAt(sb.lastIndexOf(SEPARATOR));
            }
            sb.append(") "); // 参考列拼装结束

            // 验证并获取触发事件类型
            String deleteTriggerAffair = getAndVerifiedTriggerAffair(
                    foreignKey.getDeleteTrigger()); // 删除时触发事件
            String updateTriggerAffair = getAndVerifiedTriggerAffair(
                    foreignKey.getUpdateTrigger()); // 修改时触发事件
            sb.append("ON DELETE ").append(deleteTriggerAffair).append(" "); // 删除时触发
            sb.append("ON UPDATE ").append(updateTriggerAffair); // 修改时触发

            commands.add(sb.toString());
        }
        log.info("create foreign key sql is : {}", commands);
        return commands;
    }


    private List<String> getDropForeignKeySql(String tableName,
            List<TableFkMysql> foreignKeyLList) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("drop foreign key，table name is null");
        }
        if (CollectionUtils.isEmpty(foreignKeyLList)) {
            return null;
        }
        ArrayList<String> commands = Lists.newArrayList(); // 要返回的sql
        for (TableFkMysql foreignKey : foreignKeyLList) {
            String name = foreignKey.getName(); // 外键名
            if (StringUtils.isBlank(name)) {
                throw new MetaDataException("drop foreign key，foreign key name is null");
            }
            // alter table table_name drop foreign key your_foreign_key_id; 删除外键
            commands.add(
                    String.format(DROP_FOREIGN_KEY, addBackQuote(tableName), addBackQuote(name)));
        }
        return commands;
    }

    private AlterSqlVO getAlterIndexSql(String tableName,
            List<TableIdxMysql> snapshotTableIndexList, List<TableIdxMysql> newTableIndexList,
            List<TableColumn> columns) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("alter Index, tableName is null");
        }
        if (CollectionUtils.isEmpty(newTableIndexList) && CollectionUtils
                .isEmpty(snapshotTableIndexList)) {
            log.warn(
                    "alter Index, no index changed, snapshotTableIndexList and  newTableIndexList is null ");
            return null;
        }
        if (CollectionUtils.isEmpty(columns)) {
            throw new MetaDataException("alter Index, columns is null");
        }
        /**
         * 索引的修改：
         * 先删除修改的索引
         * 后新增被修改的索引
         */
        // 如果新或旧的索引集合其中一个为null,则主动一个空ArrayList,便于后续处理
        if (snapshotTableIndexList == null) {
            snapshotTableIndexList = new ArrayList<>();
        }
        if (newTableIndexList == null) {
            newTableIndexList = new ArrayList<>();
        }

        // copy一个新的，不要改变之前的参数，因为之前参数还需要做快照
        ArrayList<TableIdxMysql> oldIndexList = Lists.newArrayList(snapshotTableIndexList);
        ArrayList<TableIdxMysql> newIndexList = Lists.newArrayList(newTableIndexList);

        // 这里再copy一份做参照
        ArrayList<TableIdxMysql> oldCopy = Lists.newArrayList(oldIndexList);
        ArrayList<TableIdxMysql> newCopy = Lists.newArrayList(newIndexList);

        // 不变的索引bean集合 //求交集。自定义对象重写 hashcode 和 equals
        oldCopy.retainAll(newCopy);

        // 待删除的索引集合，解释：被修改或以被删除的索引
        oldIndexList.removeAll(oldCopy);

        // 待新增的索引集合，解释：被修改或新增的索引
        newIndexList.removeAll(oldCopy);

        AlterSqlVO alterSql = new AlterSqlVO();

        // 先删除索引
        List<String> dropIndexCommands = getDropIndexSql(tableName, oldIndexList);
        if (CollectionUtils.isNotEmpty(dropIndexCommands)) {
            alterSql.setDeleteSql(dropIndexCommands);
        }

        // 再新建索引
        List<String> addIndexCommands = getCreateIndexSql(tableName, newIndexList, columns);
        if (CollectionUtils.isNotEmpty(addIndexCommands)) {
            alterSql.setAddSql(addIndexCommands);
        }

        /**
         * 索引修改信息详情：
         * 因为上面做法是先删除被删除或被修改的索引，后再去做新增索引。所以不确定详细情况，这边要重做
         */
        StringBuilder message = new StringBuilder();

        // 老版本的索引id
        ArrayList<Long> oldIndexIdList = getIndexVersionList(snapshotTableIndexList);
        // 新版本的索引id
        ArrayList<Long> newIndexIdList = getIndexVersionList(newTableIndexList);

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
        ArrayList<TableIdxMysql> modifyOrNorOld = getIndexList(oldIndexIdList,
                snapshotTableIndexList);

        // 返回新索引中被修改或未被修改的索引bean
        ArrayList<TableIdxMysql> modifyOrNorNew = getIndexList(oldIndexIdList, newTableIndexList);

        // 返回真正被修改的bean集合
        modifyOrNorOld.retainAll(modifyOrNorNew);
        modifyOrNorNew.removeAll(modifyOrNorOld);

        if (CollectionUtils.isNotEmpty(newIndexIdList)) {
            message.append("新增索引数").append(newIndexIdList.size()).append(",");
        }
        if (CollectionUtils.isNotEmpty(modifyOrNorNew)) {
            message.append("修改的索引数").append(modifyOrNorNew.size()).append(",");
        }
        if (CollectionUtils.isNotEmpty(oldIdCopy)) {
            message.append("删除的索引数").append(newIndexIdList.size()).append(",");
        }

        alterSql.setMessage(message.toString());
        return alterSql;
    }

    private ArrayList<TableIdxMysql> getIndexList(ArrayList<Long> indexIdList,
            List<TableIdxMysql> indexList) {
        ArrayList<TableIdxMysql> result = new ArrayList<>();
        // 遍历拿到所需要的索引
        for (TableIdxMysql index : indexList) {
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

    private ArrayList<Long> getIndexVersionList(List<TableIdxMysql> tableIndexList) {
        ArrayList<Long> list = new ArrayList<>();
        for (TableIdxMysql tableIdxMysql : tableIndexList) {
            list.add(tableIdxMysql.getId());
        }
        return list;
    }


    // 获取建索引的sql
    private List<String> getCreateIndexSql(String tableName, List<TableIdxMysql> tableIndexList,
            List<TableColumn> tableColumnList) {
        if (CollectionUtils.isEmpty(tableIndexList)) {
            return null;
        }

        // 建索引sql
        List<String> createIndexSql = new ArrayList<>();

        // 当前表所有的列
        Map<Long, TableColumn> columnMap =
                tableColumnList.stream()
                        .collect(Collectors.toMap((key -> key.getId()), (value -> value)));

        for (TableIdxMysql index : tableIndexList) {
            if (StringUtils.isBlank(index.getIndexName())) {
                throw new MetaDataException("create index, index name is null ");
            }
            if (StringUtils.isBlank(index.getColumnIds())) {
                throw new MetaDataException("create index, index columns is null ");
            }

            // 验证并获取索引类型
            String indexType = getAndVerifiedIndexType(index.getIndexType());
            if (!indexType.equals("INDEX")) {
                indexType = getAndVerifiedIndexType(index.getIndexType()) + " INDEX";
            }

            StringBuilder sb = new StringBuilder();
            // 索引列的处理
            sb.append(" (");
            String[] columnIdArr = index.getColumnIds().split(",");
            for (String colId : columnIdArr) {
                if (StringUtils.isBlank(colId)) {
                    throw new MetaDataException(
                            "index name :" + index.getIndexName() + " column error");
                }

                TableColumn tableColumn = columnMap.get(Long.parseLong(colId)); // 当前索引对应的列
                if (tableColumn == null) {
                    throw new MetaDataException(
                            "index name :" + index.getIndexName() + " column error");
                }

                // FULLTEXT is not support some data type
                if (DBEnum.MysqlIndexTypeEnum.FULLTEXT.getName().equals(indexType)) {
                    if (!DBEnum.MysqlTableDataType.CHAR.name().equals(tableColumn.getColumnType())
                            ||
                            !DBEnum.MysqlTableDataType.VARCHAR.name()
                                    .equals(tableColumn.getColumnType()) ||
                            !DBEnum.MysqlTableDataType.TEXT.name()
                                    .equals(tableColumn.getColumnType()) ||
                            !DBEnum.MysqlTableDataType.MEDIUMTEXT.name()
                                    .equals(tableColumn.getColumnType()) ||
                            !DBEnum.MysqlTableDataType.LONGTEXT.name()
                                    .equals(tableColumn.getColumnType())) {
                        throw new MetaDataException(
                                "the index type FULLTEXT is not support data type:" + tableColumn
                                        .getColumnType());
                    }
                }

                sb.append(addBackQuote(tableColumn.getColumnName())).append(" , ");
            }
            // 删除最后一个逗号
            if (sb.lastIndexOf(SEPARATOR) > -1) {
                sb.deleteCharAt(sb.lastIndexOf(SEPARATOR));
            }
            sb.append(")"); // 列拼装结束
            // 索引方法，当索引类型为 FULLTEXT 时不可指定索引方法
            if (index.getIndexMethod() != null && !DBEnum.MysqlIndexTypeEnum.FULLTEXT.getName()
                    .equals(indexType)) {
                sb.append(" USING ").append(index.getIndexMethod());
            }
            // 生成创建索引的sql
            String addIndexSql = String.format(ADD_INDEX, addBackQuote(tableName), indexType,
                    addBackQuote(index.getIndexName()), sb.toString());
            createIndexSql.add(addIndexSql);
        }
        log.info("create index sql is : {}", createIndexSql);
        return createIndexSql;
    }

    // 获取删除索引的sql
    private List<String> getDropIndexSql(String tableName, List<TableIdxMysql> tableIndexList) {
        if (CollectionUtils.isEmpty(tableIndexList)) {
            return null;
        }
        ArrayList<String> commands = Lists.newArrayList();
        for (TableIdxMysql index : tableIndexList) {
            if (StringUtils.isBlank(index.getIndexName())) {
                throw new MetaDataException("drop index name is null");
            }
            // drop index indexName on tableName
            commands.add(String.format(DROP_INDEX, addBackQuote(index.getIndexName()),
                    addBackQuote(tableName)));
        }
        return commands;
    }

    private AlterSqlVO alterTableInfoSql(MySqlTableVO newTable, MySqlTableVO snapshotTable) {
        if (newTable == null) {
            throw new MetaDataException("alter table base info，newTable is null");
        }
        if (snapshotTable == null) {
            throw new MetaDataException("alter table base info，snapshotTable is null");
        }

        AlterSqlVO alterTableInfoSql = new AlterSqlVO();
        List<String> list = new ArrayList<>();
        StringBuilder message = new StringBuilder();

        if (snapshotTable.getIdentification() != null && newTable.getIdentification() != null
                && !snapshotTable.getIdentification().equals(newTable.getIdentification())) {
            // 如果表注释不一致，拼装修改表注释的sql
            list.add(String.format(ALTER_TABLE_COMMENT, addBackQuote(snapshotTable.getName()),
                    "'" + newTable.getIdentification() + "'"));
            message.append("修改表注释,");
        }

        if (snapshotTable.getName() != null && newTable.getName() != null && !snapshotTable
                .getName().equals(newTable.getName())) {
            // 如果表那么不一致，拼装修改表名的sql
            list.add(String.format(RENAME_TABLE, addBackQuote(snapshotTable.getName()),
                    addBackQuote(newTable.getName())));
            message.append("修改表名,");
        }

        alterTableInfoSql.setChangeSql(list);
        alterTableInfoSql.setMessage(message.toString());
        return alterTableInfoSql;
    }

    private AlterSqlVO getAlterColumnSql(String snapshotTableName,
            List<TableColumn> oldTableColumnList, List<TableColumn> newTableColumnList) {
        if (StringUtils.isBlank(snapshotTableName)) {
            throw new MetaDataException("alter Column, snapshotTableName is null");
        }
        if (CollectionUtils.isEmpty(oldTableColumnList)) {
            throw new MetaDataException("alter Column, oldVersionColumns is null");
        }
        if (CollectionUtils.isEmpty(newTableColumnList)) {
            throw new MetaDataException("alter Column, newVersionColumns is null");
        }

        // 老版本的列id
        ArrayList<Long> oldColumnIdList = getColumnVersionList(oldTableColumnList);

        // 新版本的列id
        ArrayList<Long> newColumnIdList = getColumnVersionList(newTableColumnList);

        if (CollectionUtils.isEmpty(oldColumnIdList)) {
            throw new MetaDataException("alter Column, snapshot column is null");
        }

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
        ArrayList<TableColumn> addColumns = getAlterBeans(addList, newTableColumnList);

        // 返回被删除的列的bean集合
        ArrayList<TableColumn> dropColumns = getAlterBeans(deleteList, oldTableColumnList);

        // 返回旧表修改或者未修改的bean集合
        ArrayList<TableColumn> modifyOrNorOldColumns = getAlterBeans(modifyOrNorList,
                oldTableColumnList);

        // 返回新表的修改或者未修改的bean集合
        ArrayList<TableColumn> modifyOrNorNewColumns = getAlterBeans(modifyOrNorList,
                newTableColumnList);

        // 返回真正被修改的bean集合
        ArrayList<TableColumn> modifyColumns = getModifyColumns(modifyOrNorOldColumns,
                modifyOrNorNewColumns);

        // 旧表修改或者未修改数据，以id为key
        Map<Long, TableColumn> oldColumnMap =
                modifyOrNorOldColumns.stream()
                        .collect(Collectors.toMap((key -> key.getId()), (value -> value)));

        AlterSqlVO alterColumnSql = new AlterSqlVO();
        StringBuilder message = new StringBuilder();

        // 修改操作的 commands 生成
        if (CollectionUtils.isNotEmpty(modifyColumns)) {
            List list = new ArrayList();
            for (TableColumn change : modifyColumns) {
                String changeColumnSql = changeColumn(snapshotTableName, change,
                        oldColumnMap.get(change.getId()));
                if (StringUtils.isNoneBlank(changeColumnSql)) {
                    list.add(changeColumnSql);
                }
            }
            alterColumnSql.setChangeSql(list);
            message.append("修改字段数").append(list.size()).append(",");
        }

        // 删除操作的commands 生成
        if (CollectionUtils.isNotEmpty(dropColumns)) {
            if (dropColumns.size() == oldTableColumnList.size()) {
                throw new MetaDataException("违规操作，不能一次将之前字段全部删除");
            }
            List list = new ArrayList();
            for (TableColumn drop : dropColumns) {
                String dropColumnSql = dropColumn(snapshotTableName, drop);
                if (StringUtils.isNotBlank(dropColumnSql)) {
                    list.add(dropColumnSql);
                }
            }
            alterColumnSql.setDeleteSql(list);
            message.append("删除字段数").append(list.size()).append(",");
        }

        // 增加操作的commands 生成,追加到list
        if (CollectionUtils.isNotEmpty(addColumns)) {
            List list = new ArrayList();
            for (TableColumn add : addColumns) {
                String addColumnSql = addColumn(snapshotTableName, add);
                if (StringUtils.isNotBlank(addColumnSql)) {
                    list.add(addColumnSql);

                }
            }
            alterColumnSql.setAddSql(list);
            message.append("新增字段数").append(list.size()).append(",");
        }

        alterColumnSql.setMessage(message.toString());
        return alterColumnSql;
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
        String dataType = add.getColumnType();
        // 类型范围
        String typeLength = add.getTypeLength();
        // 精度
        String typePrecision = add.getTypePrecision();

        // 判断列名是否为空
        if (StringUtils.isBlank(columnName)) {
            throw new MetaDataException("add column, column name is null");
        }

        // 是否有类型范围限制,有就加上,没有就不加
        if (StringUtils.isNotBlank(typeLength)) {
            dataType = getAndVerifiedDataType(dataType) + "(";
            dataType += typeLength;
            // 精度不为空
            if (StringUtils.isNotBlank(typePrecision)) {
                dataType += "," + typePrecision;
            }
            dataType += ") ";
        } else {
            dataType = getAndVerifiedDataType(dataType);
        }

        // 拼装字段其他属性
        StringBuilder replenish = new StringBuilder();
        defineColumnSyntax(replenish, add);

        // alter table tableName add column columnType(length)
        String addColumnSql = String
                .format(ADD_COLUMN, addBackQuote(tableName), addBackQuote(columnName), dataType,
                        replenish.toString());
        return addColumnSql;
    }

    private String changeColumn(String tableName, TableColumn newColumn, TableColumn oldColumn) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("change column，table name is null");
        }
        if (newColumn == null) {
            throw new MetaDataException("change column，new column is null");
        }
        if (oldColumn == null) {
            throw new MetaDataException("change column，old column is null");
        }

        // 数据类型和长度
        String dataType = newColumn.getColumnType(); // 数据类型
        if (StringUtils.isEmpty(newColumn.getTypeLength())) {
            dataType = getAndVerifiedDataType(dataType);
        } else {
            dataType = getAndVerifiedDataType(dataType) + " (" + newColumn.getTypeLength();
            if (StringUtils.isNotBlank(newColumn.getTypePrecision())) {
                dataType += "," + newColumn.getTypePrecision();
            }
            dataType += ") ";
        }

        // 拼装字段其他属性
        StringBuilder replenish = new StringBuilder();

        defineColumnSyntax(replenish, newColumn);

        // ALTER TABLE user10 CHANGE test test1 CHAR(32) NOT NULL DEFAULT '123';
        String alterColumnSql = String.format(CHANGE_COLUMN, addBackQuote(tableName),
                addBackQuote(oldColumn.getColumnName()), addBackQuote(newColumn.getColumnName()),
                dataType, replenish.toString());
        return alterColumnSql;
    }

    private String changeAutoIncrementColumn(String tableName, TableColumn newColumn,
                                             TableColumn oldColumn, Boolean isAutoIncrement) {
        if (StringUtils.isBlank(tableName)) {
            throw new MetaDataException("change column，table name is null");
        }
        if (newColumn == null) {
            throw new MetaDataException("change column，new column is null");
        }
        if (oldColumn == null) {
            throw new MetaDataException("change column，old column is null");
        }

        // 数据类型和长度
        String dataType = newColumn.getColumnType(); // 数据类型
        if (StringUtils.isEmpty(newColumn.getTypeLength())) {
            dataType = getAndVerifiedDataType(dataType);
        } else {
            dataType = getAndVerifiedDataType(dataType) + " (" + newColumn.getTypeLength();
            if (StringUtils.isNotBlank(newColumn.getTypePrecision())) {
                dataType += "," + newColumn.getTypePrecision();
            }
            dataType += ") ";
        }

        // 拼装字段其他属性
        StringBuilder replenish = new StringBuilder();
        defineColumnSyntax(replenish, newColumn);
        // 判断是否是自增, mysql可以使用AUTO_INCREMENT关键字
        if (isAutoIncrement) {
            replenish.append(" AUTO_INCREMENT ");
        }

        // ALTER TABLE user10 CHANGE test test1 CHAR(32) NOT NULL DEFAULT '123';
        String alterColumnSql = String.format(CHANGE_COLUMN, addBackQuote(tableName),
                addBackQuote(oldColumn.getColumnName()), addBackQuote(newColumn.getColumnName()),
                dataType, replenish.toString());
        return alterColumnSql;
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

        // ALTER TABLE tableName drop column
        String dropSql = String
                .format(DROP_COLUMN, addBackQuote(tableName), addBackQuote(columnName));
        return dropSql;
    }

    private ArrayList<TableColumn> getModifyColumns(ArrayList<TableColumn> modifyOrNorOldColumns,
            ArrayList<TableColumn> modifyOrNorNewColumns) {
        ArrayList<TableColumn> oldCopy = Lists.newArrayList(modifyOrNorOldColumns);
        ArrayList<TableColumn> newCopy = Lists.newArrayList(modifyOrNorNewColumns);

        // oldCopy内容：没有被修改的列的bean，通过集合交接获取
        oldCopy.retainAll(newCopy);

        // newRcs内容：新版本被修改的列bean
        modifyOrNorNewColumns.removeAll(oldCopy);
        return modifyOrNorNewColumns;
    }

    public ArrayList<TableColumn> getAlterBeans(List<Long> idList,
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


    // 验证并获取事件类型
    public String getAndVerifiedTriggerAffair(String triggerAffair) {
        // 判断索引是否为空
        if (StringUtils.isBlank(triggerAffair)) {
            throw new MetaDataException("foreign key trigger affair is null");
        }
        try {
            return DBEnum.MysqlFKTriggerAffairEnum.valueOf(triggerAffair).getName();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MetaDataException("错误的触发事件：" + triggerAffair);
        }
    }

    // 验证并获取索引类型
    public String getAndVerifiedIndexType(String indexType) {
        // 判断索引是否为空
        if (StringUtils.isBlank(indexType)) {
            throw new MetaDataException("index indexType is null");
        }
        try {
            return DBEnum.MysqlIndexTypeEnum.valueOf(indexType.toUpperCase()).getName();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MetaDataException("错误的索引类型：" + indexType);
        }
    }


    /**
     * mysql 添加反引号。区分特殊字符与数据库保留字段
     */
    protected String addBackQuote(String value) {
        if (StringUtils.isNotBlank(value)) {
            String valueArray[] = value.split("\\.");
            StringBuilder valueSb = new StringBuilder();
            for (String str : valueArray) {
                valueSb.append("`").append(str).append("`").append(".");
            }
            valueSb.deleteCharAt(valueSb.lastIndexOf("."));
            return valueSb.toString();
        }
        return value;
    }

    /**
     * 根据不同数据源类型验证并获取数据类型
     */
    protected String getAndVerifiedDataType(String dataType) {
        // 判断列的类型是否为空
        if (null == dataType) {
            throw new MetaDataException("column dataType is null");
        }
        String realDataType = DBEnum.MysqlTableDataType.valueOf(dataType.toUpperCase()).getName();
        return realDataType;
    }


    /**
     * 定义列的语法
     * <p>
     * 数据类型为timestamp、datetime、date、time 为了兼容mysql的不同版本，统一设置默认可为空，不设置 default value
     */
    private void defineColumnSyntax(StringBuilder sb, TableColumn column) {
        switch (DBEnum.MysqlTableDataType.valueOf(column.getColumnType().toUpperCase())) {
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
                if (StringUtils.isNotBlank(column.getDefaultValue())) {
                    sb.append(" DEFAULT '" + column.getDefaultValue() + "' ");
                }

                // 判断是否为无符号数,当为oracle时 排除,oracle中没有UNSIGNED概念
                if (column.getIsUnsigned()) {
                    sb.append(" UNSIGNED ");
                }

                // 判断是否为能为空
                if (!column.getIsNull()) {
                    sb.append(" NOT NULL ");
                }

                // 判断是否是自增,mysql可以使用AUTO_INCREMENT关键字,oracle特殊处理
/*                if (column.getIsAutoIncrement()) {
                    sb.append(" AUTO_INCREMENT ");
                }*/
                break;
        }
        // 是否有注释
        if (StringUtils.isNotBlank(column.getDescription())) {
            sb.append(" COMMENT '" + column.getDescription() + "' ");
        }
    }

    // ===================================================================
    // ===================================================================
    // ===================================================================
    //                              view
    // ===================================================================
    // ===================================================================
    // ===================================================================

    // ===================================================================
    //                             create
    // ===================================================================

    // 创建视图
    private static final String CREATEORUPDATEVIEW = "CREATE OR REPLACE ALGORITHM = %s DEFINER = %s SQL SECURITY %s VIEW %s AS %s";

    // 检查选项
    private static final String CHECKOPTION = "WITH %s CHECK OPTION";

    // 删除视图
    private static final String DROPVIEW = "DROP VIEW IF EXISTS %s";

    @Override
    public String getCreateOrUpdateViewSql(String name, ViewDetail viewDetail) {
        if (StringUtils.isBlank(name)) {
            throw new MetaDataException("create view, view name is null");
        }
        if (viewDetail == null) {
            throw new MetaDataException("create view, viewDetail is null");
        }
        if (StringUtils.isBlank(viewDetail.getViewSql())) {
            throw new MetaDataException("create view, view sql is null");
        }

        /**
         *
         CREATE [OR REPLACE]
         　　[ALGORITHM = {UNDEFINED | MERGE | TEMPTABLE}]
         　　[DEFINER = { user | CURRENT_USER }]
         　　[SQL SECURITY { DEFINER | INVOKER }]
         VIEW view_name [(column_list)]
         AS select_statement
         　　[WITH [CASCADED | LOCAL] CHECK OPTION]

         */
        String definer = viewDetail.getDefiniens();
        if (StringUtils.isBlank(definer)) {
            throw new MetaDataException("视图没有指定定义者");
        }
        String algorithm = getAndVerifiedAlgorithm(viewDetail.getArithmetic()); // 算法
        String security = getAndVerifiedSecurity(viewDetail.getSecurity()); // 安全

        // 拼装创建视图的sql
        String sql = String.format(CREATEORUPDATEVIEW, algorithm, definer, security, name,
                viewDetail.getViewSql());

        // 检查选项不为空
        String checkOption = viewDetail.getCheckOption();
        if (StringUtils.isNotBlank(checkOption)) {
            checkOption = getAndVerifiedCheckOption(checkOption);
            String checkOptionSql = String.format(CHECKOPTION, checkOption);
            sql += " " + checkOptionSql;
        }

        return sql;
    }

    private String getAndVerifiedCheckOption(String checkOption) {
        try {
            return DBEnum.MysqlViewCheckOption.valueOf(checkOption.toUpperCase()).getName();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MetaDataException("错误的检查类型：" + checkOption);
        }
    }

    private String getAndVerifiedSecurity(String security) {
        if (StringUtils.isBlank(security)) {
            throw new MetaDataException("view security is null");
        }
        try {
            return DBEnum.MysqlViewSecurity.valueOf(security.toUpperCase()).getName();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MetaDataException("错误的安全类型：" + security);
        }
    }

    public String getAndVerifiedAlgorithm(String algorithm) {
        if (StringUtils.isBlank(algorithm)) {
            throw new MetaDataException("view algorithm is null");
        }
        try {
            return DBEnum.MysqlViewAlgorithm.valueOf(algorithm.toUpperCase()).getName();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MetaDataException("错误的算法类型：" + algorithm);
        }
    }

    @Override
    public AlterSqlVO getAlterViewSql(DBViewVO snapshotMySqlView, DBViewVO newMySqlView) {
        StringBuilder sb = new StringBuilder(); // 变更详情
        ArrayList<String> sqlList = new ArrayList<>(); // 变更sql

        if (!snapshotMySqlView.getName().equals(newMySqlView.getName())) {
            // 视图名有所修改，视图不能单独的去修改name，先将视图删除，再新建视图处理
            String dropView = String.format(DROPVIEW, snapshotMySqlView.getName());
            sqlList.add(dropView);
        }

        if (!snapshotMySqlView.equals(newMySqlView)) {
            sb.append("基本信息修改").append(",");
        }

        ViewDetail oldDetail = snapshotMySqlView.getViewDetail();
        ViewDetail newDetail = newMySqlView.getViewDetail();

        if (!oldDetail.equals(newDetail)) {
            // 视图详情有所修改
            String createViewSql = getCreateOrUpdateViewSql(newMySqlView.getName(),
                    newMySqlView.getViewDetail());
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
            String dropView = String.format(DROPVIEW, viewName);
            dropList.add(dropView);
        }
        return dropList;
    }

    public RdbLinkDto getConnectionConfig(Metadata metadata) {
        if (metadata == null) {
            throw new MetaDataException("错误的元数据信息");
        }
        McSchemaPO schema = schemaService.findById(metadata.getSchemaId());
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if (!datasource.getType().equals(DatabaseTypeEnum.MYSQL.getCode() + "")) {
            throw new MetaDataException("错误的数据库类型");
        }
        // TODO 密码以后会进行加密
        RdbLinkDto connectionConfig = new RdbLinkDto(schema.getUsername(),
                schema.getPassword(), "MYSQL", datasource.getIp(), datasource.getPort(),
                schema.getName());
        return connectionConfig;
    }

    @Transactional
    @Override
    public void goToDatabase(Metadata metadata, List<String> commands) {
        RdbLinkDto connectionConfig = getConnectionConfig(metadata);
        try {
            // 调用db proxy 运行sql
            if (CollectionUtils.isNotEmpty(commands)) {
                ArrayList list = new ArrayList();
                list.addAll(commands);
                RespResult<SqlExecRespDto> result = mysqlService
                        .createTable(UserUtils.getUserName(), connectionConfig, list);
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

    @Transactional
    @Override
    public void goToDatabase(String user, Metadata metadata, List<String> commands) {
        RdbLinkDto connectionConfig = getConnectionConfig(metadata);
        try {
            // 调用db proxy 运行sql
            if (CollectionUtils.isNotEmpty(commands)) {
                ArrayList list = new ArrayList();
                list.addAll(commands);
                RespResult<SqlExecRespDto> result = mysqlService
                        .createTable(user, connectionConfig, list);
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

}
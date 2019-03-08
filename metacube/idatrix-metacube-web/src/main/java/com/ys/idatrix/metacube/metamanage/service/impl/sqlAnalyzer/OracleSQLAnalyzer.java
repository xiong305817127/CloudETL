package com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLSequenceExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLBlockStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement;
import com.alibaba.druid.sql.ast.statement.SQLIfStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.google.inject.internal.util.Maps;
import com.ys.idatrix.db.api.sql.service.SqlExecService;
import com.ys.idatrix.metacube.common.enums.DBEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.domain.TableChOracle;
import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import com.ys.idatrix.metacube.metamanage.domain.TableFkOracle;
import com.ys.idatrix.metacube.metamanage.domain.TableIdxOracle;
import com.ys.idatrix.metacube.metamanage.domain.TablePkOracle;
import com.ys.idatrix.metacube.metamanage.domain.TableSetOracle;
import com.ys.idatrix.metacube.metamanage.domain.TableUnOracle;
import com.ys.idatrix.metacube.metamanage.mapper.McDatabaseMapper;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.mapper.TableColumnMapper;
import com.ys.idatrix.metacube.metamanage.mapper.TablePkOracleMapper;
import com.ys.idatrix.metacube.metamanage.mapper.TableUnOracleMapper;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto.TablesDependency;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataBaseVO;
import com.ys.idatrix.metacube.metamanage.vo.request.OracleTableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.TableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.ViewVO;
import com.ys.idatrix.metacube.metamanage.vo.response.DatasourceVO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("oracleSQLAnalyzer")
public class OracleSQLAnalyzer extends BaseSQLAnalyzer {

    @Autowired
    private McSchemaMapper schemaMapper;

    @Autowired
    private McDatabaseMapper databaseMapper;

    @Autowired
    private MetadataMapper metadataMapper;
    @Autowired
    private TableColumnMapper tableColumnMapper;

    @Reference(check = false)
    private SqlExecService sqlExecService;

    @Autowired
    private TablePkOracleMapper primaryKeyMapper;
    @Autowired
    private TableUnOracleMapper uniqueMapper;

    @Override
    public String getDbType() {
        return JdbcConstants.ORACLE;
    }

    @Override
    public List<OracleTableVO> getTablesFromDB(Long schemaId, String... tableFilter) {

        List<OracleTableVO> res = new ArrayList<>();
        List<MetadataBaseVO> result = getTablesInfoFromDB(schemaId, true, tableFilter);
        if (result != null) {
            result.stream().forEach(mb -> {
                OracleTableVO md = new OracleTableVO();
                md.setName(mb.getName());
                md.setRemark(mb.getRemark());
                md.setDatabaseType(1);
                md.setIsGather(true);
                md.setStatus(1);
                md.setResourceType(1);
                md.setVersion(1);
                res.add(md);
            });
        }
        return res;
    }

    @Override
    public List<ViewVO> getViewsFromDB(Long schemaId, String... viewFilter) {

        List<ViewVO> res = new ArrayList<>();
        List<MetadataBaseVO> result = getTablesInfoFromDB(schemaId, false, viewFilter);
        if (result != null) {

            result.stream().forEach(mb -> {
                ViewVO md = new ViewVO();
                md.setName(mb.getName());
                md.setRemark(mb.getRemark());
                md.setDatabaseType(1);
                md.setIsGather(true);
                md.setStatus(1);
                md.setResourceType(2);
                md.setVersion(1);
                //viewSql = getCreateViewSqlFromDB( schemaId, mb.getName() )

                res.add(md);
            });
        }
        return res;
    }


    @Override
    public List<TablesDependency> getTablesDependency(Long schemaId, String... tableFilter) {

        McSchemaPO schema = schemaMapper.findById(schemaId);
        if (schema == null) {
            throw new MetaDataException("Schema[" + schemaId + "]未找到.");
        }
        List<TablesDependency> result = new ArrayList<>();
        ;
        List<? extends TableVO> tables = getTablesFromDB(schemaId, tableFilter);
        if (tables != null && tables.size() > 0) {
            tables.stream().forEach(tab -> {
                try {
                    List<TableFkOracle> fks = getTableForeignkeys(schema, tab.getName(), true);
                    if (fks != null && fks.size() > 0) {
                        fks.forEach(fk -> {
                            TablesDependency td = new TablesDependency();
                            td.setSchemaId(schemaId);
                            td.setSchemaName(schema.getName());
                            td.setTableId(tab.getId());
                            td.setTableName(tab.getName());
                            td.setColumns(fk.getColumnNames());

                            td.setRefSchemaId(fk.getReferenceSchemaId());
                            td.setRefSchemaName(fk.getReferenceSchemaName());
                            td.setRefTableId(fk.getReferenceTableId());
                            td.setRefTableName(fk.getReferenceTableName());
                            td.setRefColumns(fk.getReferenceColumnNames());

                            result.add(td);
                        });
                    }
                } catch (DependencyNotExistException e) {
                }
            });
        }
        return result;
    }

    @Override
    public List<TableColumn> getViewColumns(Long schemaId, String viewName, String viewSql) {
        return getFieldsFromDB(schemaId, viewName, null);
    }

    //===========================================获取 外键/主键/唯一键/索引/字段 /检查约束/表设置 ============================================================

    /**
     * 获取 表的 外键 列表
     *
     * @param ignoreDependency 当依赖的外键表不在系统中时是否忽略(不抛出DependencyNotExistException)
     */
    public List<TableFkOracle> getTableForeignkeys(McSchemaPO schema, String tableName,
            boolean ignoreDependency) throws DependencyNotExistException {
        if (schema == null) {
            throw new MetaDataException("Schema 不能为空 .");
        }
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if (!"2".equalsIgnoreCase(datasource.getType())) {
            throw new MetaDataException("错误的数据库类型");
        }

        String sql = "select  c.constraint_name NAME , c.OWNER SCHEMANAME , " +
                "cl.constraint_name FLAG, c.R_OWNER REF_SCHEMA,c.r_constraint_name REF_RESTRAIN_ID, cl.table_name REF_TABLENAME, cl.column_name REF_COLUMNNAME, "
                +
                "cl.position LOCATION ,c.delete_rule DELETE_RULE , c.status STATUS  " +
                "from user_constraints c LEFT OUTER JOIN user_cons_columns cl on cl.constraint_name = c.constraint_name or cl.constraint_name = c.r_constraint_name "
                +
                "where c.constraint_type = 'R' and c.table_name = '" + tableName + "'";

        Map<String, TableFkOracle> result = new HashMap<>();
        List<String> ignoreList = Lists.newArrayList();
        execSqlCommand(sqlExecService, datasource, schema, sql, new dealRowInterface() {
            @Override
            public void dealRow(int index, Map<String, Object> map, List<String> columnNames)
                    throws MetaDataException {
                if (map.get("NAME") == null) {
                    return;
                }
                TableFkOracle fkDto;
                String name = map.get("NAME").toString();
                if (ignoreList.contains(name)) {
                    //关联的schema 不存在,忽略
                    return;
                }
                if (result.containsKey(name)) {
                    fkDto = result.get(name);
                } else {
                    fkDto = new TableFkOracle();
                    fkDto.setName(name);

                    String deleteType = map.get("DELETE_RULE").toString();
                    switch (deleteType) {
                        case "NO ACTION":
                            fkDto.setDeleteTrigger(
                                    DBEnum.OracleFKTriggerAffairEnum.SET_NULL.name());
                            break;
                        case "CASCADE":
                            fkDto.setDeleteTrigger(DBEnum.OracleFKTriggerAffairEnum.CASCADE.name());
                            break;
                    }

                    fkDto.setIsEnabled("ENABLED".equalsIgnoreCase(map.get("STATUS").toString()));
                    result.put(name, fkDto);
                }
                String flag = map.get("FLAG").toString();
                String refColumnname = map.get("REF_COLUMNNAME").toString();
                if (name.equals(flag)) {
                    //当前表字段信息
                    fkDto.setColumnNames(StringUtils.isEmpty(fkDto.getColumnNames()) ? refColumnname
                            : (fkDto.getColumnNames() + "," + refColumnname));
                } else {
                    //引用表字段
                    if (fkDto.getReferenceSchemaId() == null) {
                        Long ref_schemaId = null;
                        String ref_schemaName =
                                map.get("REF_SCHEMA") != null ? map.get("REF_SCHEMA").toString()
                                        : schema.getName();

                        if (StringUtils.isEmpty(ref_schemaName) || schema.getName()
                                .equalsIgnoreCase(ref_schemaName)) {
                            //相同的Schema
                            ref_schemaName = schema.getName();
                            ref_schemaId = schema.getId();
                            fkDto.setReferenceSchemaName(ref_schemaName);
                            fkDto.setReferenceSchemaId(ref_schemaId);
                        } else {
                            // 不同Schema下
                            McSchemaPO ref_schemaPO = schemaMapper
                                    .findByDbIdAndSchemaName(schema.getDbId(), ref_schemaName);
                            if (ref_schemaPO == null) {
                                //不存在 , 忽略,进行下一个
                                ignoreList.add(name);
                                result.remove(name);
                                return;
                            }
                            ref_schemaId = ref_schemaPO.getId();
                            fkDto.setReferenceSchemaId(ref_schemaId);
                            fkDto.setReferenceSchemaName(ref_schemaName);
                        }

                        String ref_tableName = deleteQuoted(map.get("REF_TABLENAME").toString());
                        fkDto.setReferenceTableName(ref_tableName);
                        List<Metadata> ref_table = metadataMapper
                                .queryMetaData(ref_schemaId, ref_tableName, null);
                        if (ref_table != null && !ref_table.isEmpty()) {
                            fkDto.setReferenceTableId(ref_table.get(0).getId());

                            //获取 referenceRestrain 和 referenceRestrainType
                            String restrainName = deleteQuoted(
                                    map.get("REF_RESTRAIN_ID").toString());
                            if (!StringUtils.isEmpty(restrainName)) {
                                //参考限制 类型
                                TablePkOracle ref_primary = primaryKeyMapper
                                        .findByTableId(fkDto.getReferenceTableId());
                                if (ref_primary != null && restrainName
                                        .equals(ref_primary.getName())) {
                                    fkDto.setReferenceRestrain(ref_primary.getId());
                                    fkDto.setReferenceRestrainType(
                                            DBEnum.ConstraintTypeEnum.PRIMARY_KEY.getCode());
                                } else {
                                    TableUnOracle uniqueList = uniqueMapper
                                            .findByTableIdAndName(fkDto.getReferenceTableId(),
                                                    restrainName);
                                    if (uniqueList != null) {
                                        fkDto.setReferenceRestrain(uniqueList.getId());
                                        fkDto.setReferenceRestrainType(
                                                DBEnum.ConstraintTypeEnum.UNIQUE.getCode());
                                    }
                                }
                            }

                        } else if (!ignoreDependency) {
                            //TODO 外键参考表不存在 , 需要先采集依赖表 , 需要先判断是否有循环依赖,避免死循环
                            throw new DependencyNotExistException(ref_tableName, ref_schemaId,
                                    tableName, schema.getId());
                        } else {
                            //不存在 , 忽略,进行下一个
                            ignoreList.add(name);
                            result.remove(name);
                            return;
                        }
                    }

                    if (fkDto.getReferenceTableId() != null) {
                        TableColumn col = tableColumnMapper
                                .selectByTableAndName(fkDto.getReferenceTableId(), refColumnname);
                        if (col == null) {
                            //引用表没有该列,忽略该外键
                            return;
                        }
                        Long res_id = col.getId();

                        fkDto.setReferenceColumn(
                                StringUtils.isEmpty(fkDto.getReferenceColumn()) ? res_id + ""
                                        : (fkDto.getReferenceColumn() + "," + res_id));
                        fkDto.setReferenceColumnNames(
                                StringUtils.isEmpty(fkDto.getReferenceColumnNames()) ? refColumnname
                                        : (fkDto.getReferenceColumnNames() + "," + refColumnname));
                    }
                }

            }
        });

        return Lists.newArrayList(result.values());
    }

    /**
     * 获取表的 主键信息
     */
    public TablePkOracle getTablePrimaryKeys(McSchemaPO schema, String tableName) {
        if (schema == null) {
            throw new MetaDataException("Schema 不能为空 .");
        }
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if (!"2".equalsIgnoreCase(datasource.getType())) {
            throw new MetaDataException("错误的数据库类型");
        }

        String sql =
                "select cu.constraint_name NAME ,cu.column_name COLUMNNAME, cu.position LOCATION \r\n"
                        +
                        "from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' and au.table_name = '"
                        + tableName + "'";

        List<String> columns = Lists.newArrayList();
        TablePkOracle result = new TablePkOracle();
        execSqlCommand(sqlExecService, datasource, schema, sql, new dealRowInterface() {

            @Override
            public void dealRow(int index, Map<String, Object> map, List<String> columnNames)
                    throws MetaDataException {
                if (map.get("NAME") == null) {
                    return;
                }
                if (StringUtils.isEmpty(result.getName())) {
                    result.setName(map.get("NAME").toString());
                    result.setSequenceStatus(2);
                    result.setCloumns(columns);
                }
                columns.add(map.get("COLUMNNAME").toString());
            }
        });

        if (!StringUtils.isEmpty(result.getName()) && columns.size() == 1 && !StringUtils
                .isEmpty(columns.get(0))) {
            //只有一个主键时 才查找序列
            String sequenceName = getSequenceFromTrigger(schema, tableName, columns.get(0));
            if (!StringUtils.isEmpty(sequenceName)) {
                result.setSequenceName(sequenceName);
                result.setSequenceStatus(4);
            }
        }

        return result;
    }

    /**
     * 获取表的 唯一键 列表
     */
    public List<TableUnOracle> getTableUniqueKey(McSchemaPO schema, String tableName) {
        if (schema == null) {
            throw new MetaDataException("Schema 不能为空 .");
        }
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if (!"2".equalsIgnoreCase(datasource.getType())) {
            throw new MetaDataException("错误的数据库类型");
        }

        String sql =
                "select au.constraint_name NAME , cu.column_name COLUMNNAME, cu.position POSITION from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'U' and au.table_name = '"
                        + tableName + "'";

        Map<String, TableUnOracle> result = new HashMap<>();
        execSqlCommand(sqlExecService, datasource, schema, sql, new dealRowInterface() {

            @Override
            public void dealRow(int index, Map<String, Object> map, List<String> columnNames)
                    throws MetaDataException {
                if (map.get("NAME") == null) {
                    return;
                }
                TableUnOracle tuDto;
                String uniqueName = map.get("NAME").toString();
                if (result.containsKey(uniqueName)) {
                    tuDto = result.get(uniqueName);
                } else {
                    tuDto = new TableUnOracle();
                    tuDto.setName(uniqueName);
                    result.put(uniqueName, tuDto);
                }
                //索引排序
                String columnName = map.get("COLUMNNAME").toString();
                tuDto.setColumnNames(StringUtils.isEmpty(tuDto.getColumnNames()) ? columnName
                        : (tuDto.getColumnNames() + "," + columnName));
            }

        });

        return Lists.newArrayList(result.values());
    }

    /**
     * 获取表的索引列表
     *
     * @param ignoreIndexs , 需要忽略的索引名列表 , 主键,唯一键 会默认建立对应索引,需要忽略
     */
    public List<TableIdxOracle> getTableIndexs(McSchemaPO schema, String tableName,
            List<String> ignoreIndexs) {
        if (schema == null) {
            throw new MetaDataException("Schema 不能为空 .");
        }
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if (!"2".equalsIgnoreCase(datasource.getType())) {
            throw new MetaDataException("错误的数据库类型");
        }

        String sql =
                "select t.index_name  INDEXNAME,t.table_name TABLENAME , t.column_name COLUMNNAME ,t.column_position COLUMNPOSITION ,t.DESCEND DESCEND , i.index_type INDEXTYPE , i.uniqueness UNIQUENESS  , e.column_expression COLUMNEXPRESSION "
                        +
                        "from user_ind_columns t LEFT JOIN user_indexes i on  t.index_name = i.index_name and t.table_name = i.table_name  "
                        +
                        "LEFT JOIN user_ind_expressions e on t.index_name = e.index_name and t.column_position = e.column_position "
                        +
                        "where  t.table_name = '" + tableName + "'";

        Map<String, TableIdxOracle> result = new HashMap<>();
        execSqlCommand(sqlExecService, datasource, schema, sql, new dealRowInterface() {

            @Override
            public void dealRow(int index, Map<String, Object> map, List<String> columnNames)
                    throws MetaDataException {
                if (map.get("INDEXNAME") == null || (ignoreIndexs != null && ignoreIndexs
                        .contains(map.get("INDEXNAME")))) {
                    return;
                }
                TableIdxOracle tiDto;
                String indexName = map.get("INDEXNAME").toString();
                if (result.containsKey(indexName)) {
                    tiDto = result.get(indexName);
                } else {
                    tiDto = new TableIdxOracle();
                    tiDto.setIndexName(indexName);

                    String indexType = map.get("UNIQUENESS").toString();
                    switch (indexType) {
                        case "UNIQUE":
                            tiDto.setIndexType(DBEnum.OracleIndexType.UNIQUE.name());
                            break;
                        case "NONUNIQUE":
                            tiDto.setIndexType(DBEnum.OracleIndexType.NON_UNIQUE.name());
                            break;
                        case "BITMAP":
                            tiDto.setIndexType(DBEnum.OracleIndexType.BITMAP.name());
                            break;
                    }
                    result.put(indexName, tiDto);
                }
                //索引排序
                String descend = map.get("DESCEND").toString();
                tiDto.setColumnSort(StringUtils.isEmpty(tiDto.getColumnSort()) ? descend
                        : (tiDto.getColumnSort() + "," + descend));

                String columnName = map.get("COLUMNNAME").toString();
                String columnExpression =
                        map.get("COLUMNEXPRESSION") != null ? map.get("COLUMNEXPRESSION").toString()
                                : null;
                if (StringUtils.isEmpty(columnExpression)) {
                    //表达式为空 , 直接使用字段名称
                    tiDto.setColumnNames(StringUtils.isEmpty(tiDto.getColumnNames()) ? columnName
                            : (tiDto.getColumnNames() + "," + columnName));
                } else {
                    //表达式不为空 , 排序类型为 DESC
                    columnExpression = columnExpression.replaceAll("\"", "");
                    tiDto.setColumnNames(
                            StringUtils.isEmpty(tiDto.getColumnNames()) ? columnExpression
                                    : (tiDto.getColumnNames() + "," + columnExpression));
                }

            }

        });

        return Lists.newArrayList(result.values());

    }

    /**
     * 获取表的 所有域字段列表
     */
    public List<TableColumn> getTableColumns(Long schemaId, String tableName, TablePkOracle pks) {
        return getFieldsFromDB(schemaId, tableName, pks);
    }

    /**
     * 获取 表的 检查约束 列表
     */
    public List<TableChOracle> getTableCheck(McSchemaPO schema, String tableName) {
        if (schema == null) {
            throw new MetaDataException("Schema 不能为空 .");
        }
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if (!"2".equalsIgnoreCase(datasource.getType())) {
            throw new MetaDataException("错误的数据库类型");
        }

        String sql =
                "select au.constraint_name NAME , au.search_condition SEARCHCONDITION from  user_constraints au where au.constraint_type = 'C' and au.table_name = '"
                        + tableName + "'";

        List<TableChOracle> result = Lists.newArrayList();
        execSqlCommand(sqlExecService, datasource, schema, sql, new dealRowInterface() {

            @Override
            public void dealRow(int index, Map<String, Object> map, List<String> columnNames)
                    throws MetaDataException {
                if (map.get("NAME") == null) {
                    return;
                }
                TableChOracle tcDto = new TableChOracle();
                ;
                tcDto.setName(map.get("NAME").toString());
                tcDto.setCheckSql(map.get("SEARCHCONDITION").toString());
                result.add(tcDto);
            }

        });

        return result;
    }

    /**
     * 获取 表的 设置信息
     */
    public TableSetOracle getTableSetting(McSchemaPO schema, String tableName) {
        if (schema == null) {
            throw new MetaDataException("Schema 不能为空 .");
        }
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if (!"2".equalsIgnoreCase(datasource.getType())) {
            throw new MetaDataException("错误的数据库类型");
        }

        String sql = "select tablespace_name TABLESPACENAME from dba_tables where table_name = '"
                + tableName + "'";

        List<TableSetOracle> result = Lists.newArrayList();
        execSqlCommand(sqlExecService, datasource, schema, sql, new dealRowInterface() {

            @Override
            public void dealRow(int index, Map<String, Object> map, List<String> columnNames)
                    throws MetaDataException {
                TableSetOracle tsDto = new TableSetOracle();
                tsDto.setTablespace(map.get("TABLESPACENAME").toString());
                result.add(tsDto);
            }

        });

        return result.isEmpty() ? null : result.get(0);
    }

    //#########################################Oracle 分析器 专用有方法#########################################################

    @Deprecated
    protected SQLStatement analyzerCreateSql(Long schemaId, String tableName) {
        return analyzerSql(getCreateTableSqlFromDB(schemaId, tableName));
    }

    @Deprecated
    protected String getCreateTableSqlFromDB(Long schemaId, String tableName) {

        McSchemaPO schema = schemaMapper.findById(schemaId);
        if (schema == null) {
            throw new MetaDataException("Schema[" + schemaId + "]未找到.");
        }
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if (!"2".equalsIgnoreCase(datasource.getType())) {
            throw new MetaDataException("错误的数据库类型");
        }

        String sql =
                "SELECT DBMS_LOB.SUBSTR( DBMS_METADATA.GET_DDL('TABLE', TABLE_NAME ),32767) FROM DUAL,USER_TABLES WHERE TABLE_NAME='"
                        + tableName + "'; ";

        StringBuffer result = new StringBuffer();
        execSqlCommand(sqlExecService, datasource, schema, sql, new dealRowInterface() {

            @Override
            public void dealRow(int index, Map<String, Object> map, List<String> columnNames)
                    throws MetaDataException {
                String key = columnNames.get(0);
                result.append(map.get(key).toString());
            }

        });
        return result.toString();
    }

    /**
     * 查询表或者视图的列表信息
     *
     * @param isTable ,是否是表, 否则是视图
     * @param filter , 不为空时 返回列表中的表信息, 否则返回所有的表信息
     */
    protected List<MetadataBaseVO> getTablesInfoFromDB(Long schemaId, boolean isTable,
            String... filter) {

        McSchemaPO schema = schemaMapper.findById(schemaId);
        if (schema == null) {
            throw new MetaDataException("Schema[" + schemaId + "]未找到.");
        }
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if (!"2".equalsIgnoreCase(datasource.getType())) {
            throw new MetaDataException("错误的数据库类型");
        }
        List<String> filterList = Lists.newArrayList();
        if (filter != null && filter.length > 0) {
            for (String f : filter) {
                filterList.add(f);
            }
        }

        String sql = "select table_name NAME ,comments REMARK from user_tab_comments where table_type ='TABLE' ";
        if (!isTable) {
            sql = "select table_name NAME ,comments REMARK from user_tab_comments where table_type ='VIEW' ";
        }

        List<MetadataBaseVO> res = new ArrayList<>();
        execSqlCommand(sqlExecService, datasource, schema, sql, new dealRowInterface() {

            @Override
            public void dealRow(int index, Map<String, Object> map, List<String> columnNames)
                    throws MetaDataException {
                if (map.get("NAME") == null) {
                    return;
                }
                String name = map.get("NAME").toString();
                if (name.toUpperCase().startsWith("BIN$")) {
                    return;
                }
                if (filterList != null && !filterList.isEmpty() && !filterList.contains(name)) {
                    return;
                }

                MetadataBaseVO md = new MetadataBaseVO();
                md.setName(map.get("NAME").toString());
                md.setRemark(map.get("REMARK") != null ? map.get("REMARK").toString() : "");
                md.setDatabaseType(2);
                md.setIsGather(true);
                md.setStatus(1);
                res.add(md);

            }
        });

        return res;
    }

    /**
     * 采集 数据库中 视图的真实创建语句 ( 包括  视图sql语句)
     */
    public String getCreateViewSqlFromDB(Long schemaId, String viewName) {

        McSchemaPO schema = schemaMapper.findById(schemaId);
        if (schema == null) {
            throw new MetaDataException("Schema[" + schemaId + "]未找到.");
        }
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if (!"2".equalsIgnoreCase(datasource.getType())) {
            throw new MetaDataException("错误的数据库类型");
        }

        String sql = "select text from user_views where view_name='" + viewName + "'";

        StringBuffer result = new StringBuffer();
        execSqlCommand(sqlExecService, datasource, schema, sql, new dealRowInterface() {

            @Override
            public void dealRow(int index, Map<String, Object> map, List<String> columnNames)
                    throws MetaDataException {

                String key = columnNames.get(0);
                result.append(map.get(key).toString());
            }

        });
        return result.toString();
    }

    /**
     * 采集 数据库中 视图或者表 对应的列字段信息
     */
    protected List<TableColumn> getFieldsFromDB(Long schemaId, String viewOrTableName,
            TablePkOracle pks) {

        McSchemaPO schema = schemaMapper.findById(schemaId);
        if (schema == null) {
            throw new MetaDataException("Schema[" + schemaId + "]未找到.");
        }
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if (!"2".equalsIgnoreCase(datasource.getType())) {
            throw new MetaDataException("错误的数据库类型");
        }

        String sql =
                "select t.COLUMN_ID LOCATION ,t.COLUMN_NAME COLUMNNAME , t.DATA_TYPE COLUMNTYPE," +
                        "				t.DATA_LENGTH TYPELENGTH, t.DATA_PRECISION TYPEPRECISION , t.DATA_SCALE TYPESCALE,"
                        +
                        "				t.NULLABLE ISNULL, t.DATA_DEFAULT DEFAULTVALUE ,c.COMMENTS DESCRIPTION "
                        +
                        "from user_tab_columns t,user_col_comments c where t.table_name = c.table_name and t.column_name = c.column_name and t.table_name = '"
                        + viewOrTableName + "'";

        List<TableColumn> res = new ArrayList<>();
        execSqlCommand(sqlExecService, datasource, schema, sql, new dealRowInterface() {

            @Override
            public void dealRow(int index, Map<String, Object> map, List<String> columnNames)
                    throws MetaDataException {
                String name = map.get("COLUMNNAME").toString();

                TableColumn tc = new TableColumn();
                tc.setColumnName(name);
                tc.setColumnType(map.get("COLUMNTYPE").toString());
                tc.setIsNull("Y".equalsIgnoreCase(
                        map.get("ISNULL") != null ? map.get("ISNULL").toString() : ""));
                tc.setDefaultValue(
                        map.get("DEFAULTVALUE") != null ? map.get("DEFAULTVALUE").toString() : "");
                tc.setLocation(map.get("LOCATION") != null ? Integer
                        .valueOf(map.get("LOCATION").toString()) : null);
                tc.setDescription(
                        map.get("DESCRIPTION") != null ? map.get("DESCRIPTION").toString() : "");

                String length =
                        map.get("TYPELENGTH") != null ? map.get("TYPELENGTH").toString() : null;
                String precision =
                        map.get("TYPEPRECISION") != null ? map.get("TYPEPRECISION").toString()
                                : null;
                if (precision != null) {
                    tc.setTypeLength(length);
                } else {
                    tc.setTypeLength(precision);
                }
                tc.setTypePrecision(
                        map.get("TYPESCALE") != null ? map.get("TYPESCALE").toString() : null);

                if (pks != null && pks.getCloumns() != null && pks.getCloumns().contains(name)) {
                    tc.setIsPk(true);
                    if (!StringUtils.isEmpty(pks.getSequenceName())) {
                        tc.setIsAutoIncrement(true);
                    }
                }
                res.add(tc);

            }

        });
        return res;
    }

    /**
     * 获取 表的 触发器列表
     *
     * @return 返回 key:触发器名称 , value:触发器创建语句
     */
    protected Map<String, String> getTriggerSqlMap(McSchemaPO schema, String tableName) {
        if (schema == null) {
            throw new MetaDataException("Schema 不能为空 .");
        }
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if (!"2".equalsIgnoreCase(datasource.getType())) {
            throw new MetaDataException("错误的数据库类型");
        }

        String sql =
                "SELECT trigger_name TRIGGERNAME, DBMS_LOB.SUBSTR( DBMS_METADATA.GET_DDL('TRIGGER',trigger_name) ,32767) TRISQL FROM all_triggers  where table_name=  '"
                        + tableName + "'";

        Map<String, String> res = Maps.newHashMap();
        execSqlCommand(sqlExecService, datasource, schema, sql, new dealRowInterface() {

            @Override
            public void dealRow(int index, Map<String, Object> map, List<String> columnNames)
                    throws MetaDataException {
                res.put(map.get("TRIGGERNAME").toString(), map.get("TRISQL").toString());
            }

        });
        return res;
    }

    /**
     * 从表的触发器列表中分析 主键是否有依赖自增的序列
     */
    protected String getSequenceFromTrigger(McSchemaPO schema, String tableName, String pkName) {

        Map<String, String> triggers = getTriggerSqlMap(schema, tableName);
        if (triggers != null) {
            for (Entry<String, String> tri : triggers.entrySet()) {
                //获取到的sql中 EDITIONABLE 和 REFERENCING 解析语法错误
                String sql = tri.getValue().replaceAll("(?i)EDITIONABLE", "")
                        .replaceAll("(?i)REFERENCING OLD AS \"OLD\" NEW AS \"NEW\"", "");

                SQLStatement statement = analyzerSql(sql);
                SQLCreateTriggerStatement stmt = (SQLCreateTriggerStatement) statement;
                if (stmt.getBody() != null) {
                    SQLBlockStatement body = (SQLBlockStatement) stmt.getBody();
                    if (body != null) {
                        String[] params = new String[3];
                        for (SQLStatement sm : body.getStatementList()) {
                            if (sm instanceof SQLIfStatement) {
                                SQLIfStatement smIf = (SQLIfStatement) sm;
                                for (SQLStatement smIfsm : smIf.getStatements()) {
                                    FindStatementSequence(smIfsm, params);
                                }
                            } else {
                                FindStatementSequence(sm, params);
                            }
                        }
                        if (!StringUtils.isEmpty(params[0]) && !StringUtils.isEmpty(params[1])
                                && params[1].equalsIgnoreCase(pkName)) {
                            //序列不为空且 主键一致,则找到序列,否则未找到
                            return params[0];
                        }
                    }

                }

            }
        }
        return null;
    }


    /**
     * 从 SQLStatement 中分析 序列名和 对应的主键
     *
     * @param params 结果参数 , 长度必须为3 , 第一个为查询到的序列名 , 第二个为查询到的主键名 , 第三个为select into 的变量名 (可能为空)
     */
    private void FindStatementSequence(SQLStatement sm, String[] params) {

        if (sm instanceof SQLSelectStatement) {
            //如果是 select 语句块
            SQLSelectStatement smSelect = (SQLSelectStatement) sm;
            OracleSelectQueryBlock oracleQb = (OracleSelectQueryBlock) smSelect.getSelect()
                    .getQueryBlock();
            if (oracleQb.getInto() != null) {
                if (oracleQb.getInto().getExpr() instanceof SQLPropertyExpr) {
                    //查询 into 表达式 ,是否是  :New.主键 模式
                    SQLPropertyExpr pkInto = (SQLPropertyExpr) oracleQb.getInto().getExpr();
                    if (":New".equalsIgnoreCase(pkInto.getOwner().toString())) {
                        params[1] = pkInto.getName();
                        params[2] = null;
                    }
                } else if (oracleQb.getInto().getExpr() instanceof SQLIdentifierExpr) {
                    //否则可能是 是 变量模式 , 获取变量名
                    SQLIdentifierExpr pkInto = (SQLIdentifierExpr) oracleQb.getInto().getExpr();
                    params[2] = pkInto.getName();
                }
            }
            //查询选择字段  判断是否有序列表达式 ,有则获取序列名
            for (SQLSelectItem sItem : oracleQb.getSelectList()) {
                if (sItem.getExpr() instanceof SQLSequenceExpr) {
                    SQLIdentifierExpr sequenceItem = (SQLIdentifierExpr) ((SQLSequenceExpr) sItem
                            .getExpr()).getSequence();
                    params[0] = sequenceItem.getName();
                    break;
                }
            }

        } else if (!StringUtils.isEmpty(params[2]) && sm instanceof SQLSetStatement) {
            //如果是  set 语句块 , intoField值已经在前面的 select块中获取到了
            SQLSetStatement smSet = (SQLSetStatement) sm;
            for (SQLAssignItem setItem : smSet.getItems()) {
                if (params[2].equals(setItem.getValue().toString())) {
                    if (setItem.getTarget() instanceof SQLPropertyExpr) {
                        SQLPropertyExpr pkInto = (SQLPropertyExpr) setItem.getTarget();
                        if (":New".equalsIgnoreCase(pkInto.getOwner().toString())) {
                            params[1] = pkInto.getName();
                            params[2] = null;
                        }
                    }
                }
            }
        }
    }

}

package com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyImpl.Option;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.ys.idatrix.db.api.sql.service.SqlExecService;
import com.ys.idatrix.metacube.common.enums.DBEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.mapper.TableColumnMapper;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto.DatabaseConnect;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto.DependencyNotExistException;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto.TablesDependency;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataBaseVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MySqlTableVO;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MysqlSQLAnalyzer extends BaseSQLAnalyzer {

    @Autowired
    private McSchemaMapper schemaMapper;

    @Autowired
    private  MetadataMapper metadataMapper ;
    @Autowired
    private TableColumnMapper tableColumnMapper;

    @Reference(check=false )
    private  SqlExecService sqlExecService ;

    @Override
    public String getDbType() {
        return JdbcConstants.MYSQL;
    }

    @Override
    public List<MySqlTableVO> getTablesFromDB(DatabaseConnect dbInfo,  String... tableFilter) {

        List<MySqlTableVO>  res  = new ArrayList<>();
        List<MetadataBaseVO> result = getTablesInfoFromDB(dbInfo, true, tableFilter) ;
        if( result != null ) {
            result.stream().forEach( mb -> {
                MySqlTableVO md =  new MySqlTableVO();
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
    public List<DBViewVO> getViewsFromDB( DatabaseConnect dbInfo ,String... viewFilter){

        List<DBViewVO>  res  = new ArrayList<>();
        List<MetadataBaseVO> result = getTablesInfoFromDB(dbInfo, false, viewFilter) ;
        if( result != null ) {

            result.stream().forEach( mb -> {
                DBViewVO md =  new DBViewVO();
                md.setName(mb.getName());
                md.setRemark(mb.getRemark());
                md.setDatabaseType(1);
                md.setIsGather(true);
                md.setStatus(1);
                md.setResourceType(2);
//                md.setViewDetail(new ViewDetail());
//                md.getViewDetail().setViewSql(getViewSelectSqlFromDB(dbInfo, mb.getName()));
                md.setVersion(1);
                res.add(md);
            });
        }
        return res;
    }


    @Override
    public List<TablesDependency> getTablesDependency(DatabaseConnect dbInfo,  String... tableFilter) {

        McSchemaPO schema = dbInfo.getSchema() ;
        Long schemaId = dbInfo.getSchemaId() ;

        List<TablesDependency> result =  new ArrayList<>(); ;
        List<MySqlTableVO> tables = getTablesFromDB(dbInfo,tableFilter);
        if( tables != null && tables.size() >0 ) {
            tables.stream().forEach(tab -> {

                SQLStatement stmt  = analyzerCreateSql(dbInfo, tab.getName()) ;
                try {
                    List<TableFkMysql> fks = getTableForeignkeys(dbInfo, tab.getName(),true, stmt);
                    if( fks != null && fks.size() > 0 ) {
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
    public  List<TableColumn> getViewColumns(DatabaseConnect dbInfo,  String viewName, String viewSql) {
        return getFieldsFromDB(dbInfo, viewName);
    }

    //===========================================获取 外键/主键/索引/字段 ============================================================

    /**
     *获取 表的 外键 列表
     * @param schemaId
     * @param tableName
     * @param ignoreDependency 当依赖的外键表不在系统中时是否忽略(不抛出DependencyNotExistException)
     * @param stmt create语句解析对象
     * @return
     * @throws DependencyNotExistException
     */
    public  List<TableFkMysql> getTableForeignkeys(DatabaseConnect dbInfo, String tableName, boolean ignoreDependency , SQLStatement stmt) throws DependencyNotExistException {
        if( stmt == null) {
            stmt = analyzerCreateSql (dbInfo, tableName);
        }
        MySqlCreateTableStatement mysqlStmt = (MySqlCreateTableStatement)stmt;
        McSchemaPO schema = dbInfo.getSchema();

        List<TableFkMysql> result = new ArrayList<>();
        for( SQLForeignKeyConstraint sqlForeign : mysqlStmt.findForeignKey() ){
            MysqlForeignKey foreign = ((MysqlForeignKey)sqlForeign) ;

            TableFkMysql fk = new TableFkMysql() ;
            fk.setName(deleteQuoted( foreign.getName().getSimpleName()));
            fk.setColumnNames(String.join(",",foreign.getReferencingColumns().stream().map(rc -> { return deleteQuoted(  rc.getSimpleName() ); } ).collect(Collectors.toList())));

            String ref_schemaName = deleteQuoted(foreign.getReferencedTable().getSchema());
            Long ref_schemaId = null ;
            if( StringUtils.isEmpty(ref_schemaName) || schema.getName().equalsIgnoreCase(ref_schemaName)) {
                ref_schemaName = schema.getName() ;
                ref_schemaId = schema.getId() ;
                fk.setReferenceSchemaName(ref_schemaName);
            }else {
                // 不同Schema下
                McSchemaPO fk_schema = schemaMapper.findByDbIdAndSchemaName(schema.getDbId(), ref_schemaName);
                if( fk_schema == null ) {
                    //不存在 , 忽略,进行下一个
                    continue ;
                }
                ref_schemaId = fk_schema.getId() ;
                ref_schemaName = fk_schema.getName() ;
                fk.setReferenceSchemaId(fk_schema.getId());
                fk.setReferenceSchemaName(ref_schemaName);
            }

            String ref_tableName =  deleteQuoted( foreign.getReferencedTableName().getSimpleName() ) ;
            fk.setReferenceTableName(ref_tableName);
            List<Metadata> ref_table = metadataMapper.queryMetaData(fk.getReferenceSchemaId(), ref_tableName, null);
            if( ref_table != null && !ref_table.isEmpty()) {
                fk.setReferenceTableId(ref_table.get(0).getId());
            }else if( !ignoreDependency ){
                //TODO 外键参考表不存在 , 需要先采集依赖表 , 需要先判断是否有循环依赖,避免死循环
                throw new DependencyNotExistException(ref_tableName, ref_schemaId, tableName, dbInfo.getSchemaId());
            }else {
            	continue ;
            }

            String ref_column_ids = null ;
            String ref_column_names = null ;
            for(SQLName ref_column_sql: foreign.getReferencedColumns() ) {
                String ref_column = deleteQuoted(  ref_column_sql.getSimpleName() ) ;
                TableColumn col = tableColumnMapper.selectByTableAndName(fk.getReferenceTableId(), ref_column);
                if( col == null ) {
                    //引用表没有该列,忽略该外键
                    continue ;
                }
                Long res_id = col.getId();
                if( ref_column_names == null ) {
                    ref_column_names = ref_column ;
                    ref_column_ids = res_id+"" ;
                }else {
                    ref_column_names = ref_column_names+","+ref_column ;
                    ref_column_ids = ref_column_ids+","+res_id ;
                }
            }
            fk.setReferenceColumnNames(ref_column_names);
            fk.setReferenceColumn(ref_column_ids);

            fk.setDeleteTrigger( foreign.getOnDelete()!= null ? foreign.getOnDelete().toString() : Option.RESTRICT.toString()) ;
            fk.setUpdateTrigger( foreign.getOnUpdate()!= null ? foreign.getOnUpdate().toString() : Option.RESTRICT.toString()) ;

            result.add(fk);

        };

        return result;
    }

    /**
     * 获取表的  主键
     * @param schemaId
     * @param tableName
     * @param stmt  create语句解析对象
     * @return
     */
    public  List<String> getTablePrimaryKeys(DatabaseConnect dbInfo,String tableName, SQLStatement stmt) {
        if( stmt == null) {
            stmt = analyzerCreateSql (dbInfo, tableName);
        }
        MySqlCreateTableStatement mysqlStmt = (MySqlCreateTableStatement)stmt;
        return mysqlStmt.findPrimaryKey().getColumns().stream().map(rc -> { return  ((SQLIdentifierExpr)rc.getExpr()).getName(); } ).collect(Collectors.toList()) ;
    }

    /**
     * 获取 表的 索引列表
     * @param schemaId
     * @param tableName
     * @param stmt  create语句解析对象
     * @return
     */
    public  List<TableIdxMysql> getTableIndexs(DatabaseConnect dbInfo,String tableName, SQLStatement stmt) {
        if( stmt == null) {
            stmt = analyzerCreateSql (dbInfo, tableName);
        }
        MySqlCreateTableStatement mysqlStmt = (MySqlCreateTableStatement)stmt;

        List<TableIdxMysql> result = new ArrayList<>();
        mysqlStmt.getTableElementList().forEach(SQLColumnDefinition -> {
            SQLTableElement element =  SQLColumnDefinition ;

            TableIdxMysql tim = null ;
            if (element instanceof SQLUniqueConstraint ) {
                if( element instanceof  MySqlPrimaryKey) {
                    //主键的默认索引 ,忽略
                    return ;
                }
                SQLUniqueConstraint unique = (SQLUniqueConstraint) element;

                tim = new TableIdxMysql();
                tim.setIndexType(DBEnum.MysqlIndexTypeEnum.UNIQUE.name());
                tim.setIndexName(( (unique.getName()!= null)? deleteQuoted( unique.getName().getSimpleName()):"" ));

                List<String> columnStr = new ArrayList<>();
                for (SQLSelectOrderByItem item : unique.getColumns()) {
                    SQLExpr columnExpr = item.getExpr();
                    if (columnExpr instanceof SQLIdentifierExpr) {
                        String keyColumName = ((SQLIdentifierExpr) columnExpr).getName();
                        keyColumName = SQLUtils.normalize(keyColumName);
                        columnStr.add(keyColumName) ;
                    }
                }

                tim.setColumnNames(String.join(",",columnStr));

            } else if (element instanceof MySqlTableIndex) {
                MySqlTableIndex indexs = (MySqlTableIndex) element ;

                tim = new TableIdxMysql();
                tim.setIndexType(DBEnum.MysqlIndexTypeEnum.NORMAL.name());
                tim.setIndexName(( (indexs.getName()!= null)? deleteQuoted( indexs.getName().getSimpleName()):"" ));

                List<String> columnStr = new ArrayList<>();
                for (SQLSelectOrderByItem orderByItem : indexs.getColumns() ) {
                    SQLExpr columnExpr = orderByItem.getExpr();
                    if (columnExpr instanceof SQLIdentifierExpr) {
                        String keyColumName = ((SQLIdentifierExpr) columnExpr).getName();
                        keyColumName = SQLUtils.normalize(keyColumName);
                        columnStr.add(keyColumName) ;
                    }
                }

                tim.setColumnNames(String.join(",",columnStr));
            }

            if( tim != null ) {
                tim.setIndexMethod(DBEnum.MysqlIndexMethodEnum.BTREE.getName());
                if( element instanceof MySqlKey) {
                    String type = ((MySqlKey)element ).getIndexType()  ;
                    if( !StringUtils.isEmpty(type)) {
                    	try {
                    		//如果是方法
                    		if( DBEnum.MysqlIndexMethodEnum.valueOf(type) != null ) {
                    			 tim.setIndexMethod(type);
                        	}
                    	}catch(Exception ignore1) { }
                    	try {
                    		//如果是类型
                    		if( DBEnum.MysqlIndexTypeEnum.valueOf(type) != null ) {
                    			 tim.setIndexType(type);
                        	}
                    	}catch(Exception ignore1) { }
                    	
                    }
                }
                result.add(tim);
            }
        });

        return result;
    }

    /**
     * 获取表的域字段列表
     * @param schemaId
     * @param tableName
     * @param stmt create语句解析对象
     * @return
     */
    public List<TableColumn> getTableColumns(DatabaseConnect dbInfo, String tableName, SQLStatement stmt) {
        if( stmt == null) {
            stmt = analyzerCreateSql (dbInfo, tableName);
        }
        MySqlCreateTableStatement mysqlStmt = (MySqlCreateTableStatement)stmt;

        List<TableColumn> result = new ArrayList<>();
        mysqlStmt.forEachColumn(SQLColumnDefinition ->{
            SQLColumnDefinition element =  SQLColumnDefinition ;

            TableColumn tc =new TableColumn();
            tc.setColumnName(deleteQuoted( element.getName().getSimpleName()));
            tc.setColumnType(element.getDataType().getName());
            tc.setTypeLength( element.getDataType().getArguments().size()>0? element.getDataType().getArguments().get(0).toString(): "0" ) ;
            tc.setTypePrecision( element.getDataType().getArguments().size()>1? element.getDataType().getArguments().get(1).toString(): "0" );
            tc.setIsPk( mysqlStmt.isPrimaryColumn(deleteQuoted( element.getName().getSimpleName()) ) );
            tc.setIsAutoIncrement( element.isAutoIncrement() );
            tc.setIsNull( (!element.containsNotNullConstaint()) );
            tc.setDefaultValue( element.getDefaultExpr()!= null&&!"NULL".equalsIgnoreCase(element.getDefaultExpr().toString())? element.getDefaultExpr().toString():null);
            tc.setIsUnsigned( ( element.getDataType() instanceof SQLDataTypeImpl )? ((SQLDataTypeImpl)element.getDataType()).isUnsigned() : false  );
            tc.setDescription( deleteQuoted(element.getComment()!= null ? element.getComment().toString() :"" ));

            result.add(tc);
        });


        return result;
    }

    //#########################################Mysql 分析器 专用有方法#########################################################

    /**
     * 获取表的 create语句解析对象
     * @param schemaId
     * @param tableName
     * @return
     */
    public SQLStatement analyzerCreateSql( DatabaseConnect dbInfo, String tableName ) {
        return analyzerSql(getCreateTableSqlFromDB(dbInfo, tableName)) ;
    }

    /**
     *  查询表或者视图的列表信息
     * @param schemaId
     * @param isTable ,是否是表, 否则是视图
     * @param filter  不为空时 返回列表中的表信息, 否则返回所有的表信息
     * @return
     */
    protected List<MetadataBaseVO> getTablesInfoFromDB(DatabaseConnect dbInfo,  boolean isTable, String... filter) {

        List<String> filterList =  Lists.newArrayList();
        if( filter != null && filter.length > 0 ) {
            for( String f : filter) {
                filterList.add(f);
            }
        }
        List<MetadataBaseVO>  res  = new ArrayList<>();

        String sql = "show table status where engine is not null" ;
        if( !isTable ) {
            sql = "show table status where engine is  null " ;
        }
        // String sql = "SELECT table_name name,  table_comment remark  from information_schema.TABLES  WHERE table_schema = '"+schema.getName()+"' ORDER BY table_name" ;
      
        execSqlCommand(sqlExecService, dbInfo, sql, new dealRowInterface() {

			@Override
			public void dealRow(int index, Map<String, Object> map, List<String> columnNames) throws MetaDataException {
				if( map.get("Name") == null ) {
					 return ;
				}
				String name =map.get("Name").toString() ;
				if( filterList != null && !filterList.isEmpty() && !filterList.contains(name) ) {
					return  ;
				}

				 MetadataBaseVO md =  new MetadataBaseVO();
				 md.setName(map.get("Name").toString());
				 md.setRemark(map.get("Comment").toString());
				 md.setDatabaseType(1);
				 md.setIsGather(true);
				 md.setStatus(1);
				 res.add(md);
				
			}
   		 
        });
        return res;
    }



    /**
     *  采集数据库中 表的真实 创建语句 ( 包括索引,外键 等信息)
     * @param schemaId
     * @param tableName
     * @return
     */
    protected String getCreateTableSqlFromDB(DatabaseConnect dbInfo, String tableName) {

        String sql = "SHOW CREATE TABLE "+ tableName ;
        
        StringBuffer result = new StringBuffer();
        execSqlCommand(sqlExecService, dbInfo, sql, new dealRowInterface() {
			@Override
			public void dealRow(int index, Map<String, Object> map, List<String> columnNames) throws MetaDataException {
				String key = columnNames.get(1);
				result.append( map.get(key).toString() );
				
			}
        	
        });
        return result.toString();
    }

    /**
     * 采集 数据库中 视图的真实创建语句 ( 包括  视图sql语句)
     * @param schemaId
     * @param viewName
     * @return
     */
    public String getViewSelectSqlFromDB(DatabaseConnect dbInfo, String viewName) {

        String sql = "SELECT  VIEW_DEFINITION  FROM  information_schema.views where table_name='"+ viewName+"'" ;
        
        StringBuffer result = new StringBuffer();
        execSqlCommand(sqlExecService, dbInfo, sql, new dealRowInterface() {

			@Override
			public void dealRow(int index, Map<String, Object> map, List<String> columnNames) throws MetaDataException {
				String key = columnNames.get(0);
				if( StringUtils.isEmpty(key) || map == null || map.get(key) == null  ) {
					log.error("处理数据为空sql["+sql+"],map["+map+"],columns["+columnNames+"]");
					return ;
				}
				result.append( map.get(key).toString() );
			}
        	
        });
        return result.toString();
    }
    
    /**
     * 采集 数据库中 视图的真实创建语句 ( 包括  视图sql语句)
     * @param schemaId
     * @param viewName
     * @return
     */
    @Deprecated
    public String getCreateViewSqlFromDB(DatabaseConnect dbInfo, String viewName) {

        String sql = "SHOW  CREATE  VIEW "+ viewName ;
        
        StringBuffer result = new StringBuffer();
        execSqlCommand(sqlExecService, dbInfo, sql, new dealRowInterface() {

			@Override
			public void dealRow(int index, Map<String, Object> map, List<String> columnNames) throws MetaDataException {
				String key = columnNames.get(1);
				if( StringUtils.isEmpty(key) || map == null || map.get(key) == null  ) {
					log.error("从dbproxy查询数据为空sql["+sql+"]");
					return ;
				}
				result.append( map.get(key).toString() );
			}
        	
        });
        return result.toString();
    }

    /**
     * 采集 数据库中 视图或者表 对应的列字段信息
     * @param schemaId
     * @param viewOrTableName
     * @return
     */
    protected List<TableColumn> getFieldsFromDB(DatabaseConnect dbInfo, String viewOrTableName) {

        String sql = "describe "+ viewOrTableName ;
        
        List<TableColumn> res = new ArrayList<>();
        execSqlCommand(sqlExecService, dbInfo, sql, new dealRowInterface() {

			@Override
			public void dealRow(int index, Map<String, Object> map, List<String> columnNames) throws MetaDataException {
				  TableColumn tc =new TableColumn();
				  tc.setColumnName(map.get("Field").toString());
				  tc.setIsNull( "YES".equalsIgnoreCase(map.get("Null")!= null&&!"NULL".equalsIgnoreCase(map.get("Null").toString())? map.get("Null").toString(): "" ) );
				  tc.setDefaultValue( map.get("Default")!= null&&!"NULL".equalsIgnoreCase(map.get("Default").toString()) ? map.get("Default").toString(): null );

				  String type = map.get("Type").toString();
				  if( !StringUtils.isEmpty(type)) {
	                    String[] types =type.split("\\s");
	                    tc.setColumnType(types[0].replaceAll("(?<=^.*)[^\\w].*", ""));
	                    String len = types[0].replaceAll(".*\\((?=\\d*)", "").replaceAll("(?<=^\\d*)\\D.*", "") ;
	                    tc.setTypeLength( StringUtils.isEmpty(len) ? "0" : len ) ;
	                    String preci = types[0].replaceAll(".*,(?=\\d*)", "").replaceAll("(?<=^\\d*)\\D.*", "");
	                    tc.setTypePrecision( StringUtils.isEmpty(preci) ? "0" : preci  );
	                    tc.setIsUnsigned( "unsigned".equalsIgnoreCase((types.length>1?types[1]:"").trim()) );
				  }
				  res.add(tc);
			}
        });
        return  res ;
    }


}

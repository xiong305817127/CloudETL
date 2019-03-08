package com.ys.idatrix.metacube.metamanage.service.impl;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.mapper.MetadataMapper;
import com.ys.idatrix.metacube.metamanage.service.*;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.BaseSQLAnalyzer.DependencyNotExistException;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.MysqlSQLAnalyzer;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.OracleSQLAnalyzer;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.SQLAnalyzer;
import com.ys.idatrix.metacube.metamanage.vo.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class McDirectMiningServiceImpl implements McDirectMiningService {

	@Autowired
	private McSchemaMapper schemaMapper;

	@Autowired
	private MetadataMapper metadataMapper;
	  
	@Autowired
	private SQLAnalyzer sqlAnalyzer ;

	@Autowired
	private MysqlSQLAnalyzer mysqlSQLAnalyzer ;

	@Autowired
	private OracleSQLAnalyzer oracleSQLAnalyzer ;
	
	@Autowired
	private MysqlTableService mysqlTableService ;

	@Autowired
	OracleTableService oracleTableService ;

	@Autowired
	@Qualifier("mysqlViewService")
	private ViewService mysqlViewService;

	@Autowired
	@Qualifier("oracleViewService")
	private ViewService oracleViewService ;

	private String getDbType( Integer databaseType ) {
		switch( databaseType) {
		case 1: return JdbcConstants.MYSQL ;
		case 2: return JdbcConstants.ORACLE ;
		
		default : return JdbcConstants.MYSQL ;
		}
	}
	
	@Override
	public List<? extends TableVO>  getTableAllInfo(Integer databaseType, Long schemaId ) {
		return sqlAnalyzer.getAllTables(getDbType(databaseType), schemaId);
	}

	@Override
	public List<? extends ViewVO>  getViewAllInfo( Integer databaseType, Long schemaId  ) {
		return sqlAnalyzer.getAllViews(getDbType(databaseType), schemaId) ;
	}
	
	@Override
	public List<? extends TableVO>  directMiningTables(Integer databaseType , Long schemaId, MetadataBaseVO... metadataBases ) {
		
		switch( databaseType) {
		case 1: return directMiningMysqlTables(schemaId,null,metadataBases) ;
		case 2: return directMiningOracleTables(schemaId,null,metadataBases );
		}
		
		return null ;
	}
	
	@Override
	public List<? extends ViewVO>  directMiningViews(Integer databaseType, Long schemaId,  MetadataBaseVO... metadataBases ) {
		
		switch( databaseType) {
		case 1: return directMiningMysqlViews(schemaId, metadataBases) ;
		case 2: return directMiningOracleViews(schemaId, metadataBases);
		}
		
		return null ;
	}
	
	/**
	 * 直采Mysql 表
	 * @param schemaId
	 * @param dependencyTables , 表依赖的引用表采集对象Map , 初始时传NUll , 方法本身递归时使用
	 * @param metadataBases
	 * @return
	 */
	public List<MySqlTableVO> directMiningMysqlTables( Long schemaId, Map<String,MySqlTableVO> dependencyTables ,  MetadataBaseVO... metadataBases) {
		List<MySqlTableVO> result = new ArrayList<>();
		if( metadataBases != null && metadataBases.length > 0 ) {
			if( dependencyTables == null ) {
				dependencyTables = Maps.newHashMap();
			}
			for( MetadataBaseVO mbv : metadataBases) {
				String tableName = mbv.getName() ;
				boolean  ignoreDependency = false ;
				
				String currentKey = schemaId+"."+tableName ;
				if( dependencyTables.containsKey(currentKey) ) {
					MySqlTableVO dmtv = dependencyTables.get(currentKey);
					if( dmtv != null ) {
						//在外键冲突时已经采集过该表  , 直接进行下一个表的采集
						result.add(dmtv) ;
						continue ;
					}else {
						//该表存在外键循环依赖 ,忽略外键
						ignoreDependency = true ;
					}
				}
				
				MySqlTableVO mtv = new MySqlTableVO() ;
				copyProperties(mbv, mtv);//mysqlSQLAnalyzer.getTablesFromDB(schemaId, tableName).get(0);
				mtv.setSchemaId(schemaId);
				mtv.setIsGather(true);
				
				SQLStatement stmt  = mysqlSQLAnalyzer.analyzerCreateSql(schemaId, tableName) ;
				
				try {
					
					List<TableFkMysql> fks = mysqlSQLAnalyzer.getTableForeignkeys(schemaId, tableName,ignoreDependency, stmt) ;
					mtv.setTableFkMysqlList(fks);
					
					List<TableIdxMysql> is = mysqlSQLAnalyzer.getTableIndexs(schemaId, tableName, stmt) ;
					mtv.setTableIndexList(is);
					List<TableColumn> cloumns = mysqlSQLAnalyzer.getTableColumns(schemaId, tableName, stmt) ;
					mtv.setTableColumnList(cloumns);
					
					mysqlTableService.addTable(mtv);
					
				} catch (DependencyNotExistException e) {
					//存储当前表信息,以备 不进行循环直采
					dependencyTables.put( currentKey , null );
					
					String dependencyKey = e.getDependencySchemaId()+"."+e.getDependencyTableName() ;
					//dependencyTables包括该依赖且为空时,为循环依赖,忽略该依赖直接再次进行直采,此次dependencyTables包含空,会进行忽略依赖采集
					//dependencyTables包括该依赖且不为空时,已经采集过该依赖,无需再采集,理论上不会出现,已经采集就不会再报依赖不存在异常
					if( !dependencyTables.containsKey(dependencyKey)  ) {
						//没有包含依赖表 ,新建依赖表
						MetadataBaseVO temp =new MetadataBaseVO();
						copyProperties(mbv, temp) ;
						temp.setName(e.getDependencyTableName());
						//创建依赖的表
						List<MySqlTableVO> dmtv = directMiningMysqlTables(e.getDependencySchemaId(),dependencyTables, temp);
						if( dmtv != null && dmtv.size() > 0) {
							//依赖表创建完成,存储,以备不进行重复采集
							dependencyTables.put(dependencyKey, dmtv.get(0)) ;
						}
						//该次依赖已经成功采集,且未采集当前table
						if( dependencyTables.get(currentKey) == null ){
							dependencyTables.remove(currentKey);
						}
					}
					//继续创建失败的表
					mtv = directMiningMysqlTables(schemaId,dependencyTables, mbv).get(0);
				}
				result.add(mtv) ;
			}
		}
		return result ;
	}

	/**
	 * 直采Mysql 视图
	 * @param schemaId
	 * @param metadataBases
	 * @return
	 */
	public List<DBViewVO> directMiningMysqlViews(Long schemaId, MetadataBaseVO... metadataBases)  {
		List<DBViewVO> result = new ArrayList<>();
		if( metadataBases != null && metadataBases.length > 0 ) {
			for( MetadataBaseVO mbv : metadataBases) {
				DBViewVO mvv = new DBViewVO() ;
				copyProperties(mbv, mvv ); //List<DBViewVO> vs = mysqlSQLAnalyzer.getViewsFromDB( schemaId, viewName).get(0) ;
				if( mvv.getViewDetail() == null || StringUtils.isEmpty( mvv.getViewDetail().getViewSql() ) ) {
					mvv.setViewDetail(new ViewDetail());
					mvv.getViewDetail().setViewSql(mysqlSQLAnalyzer.getCreateViewSqlFromDB(schemaId, mvv.getName()));
					mvv.setVersion(1);
				}
				mvv.setSchemaId(schemaId);
				mvv.setIsGather(true);
				result.add(mvv) ;
				//视图保存
				mysqlViewService.addView(mvv);
				//视图字段方法
				Metadata view = metadataMapper.findById(mvv.getId());
				if( view != null ) {
					mysqlViewService.saveOrUpdateViewColumns(view);
				}
			}
		}
		return result ;
	}

	/**
	 * 直采Oracle数据库表
	 * @param schemaId
	 * @param dependencyTables  表依赖的引用表采集对象Map , 初始时传NUll , 方法本身递归时使用
	 * @param metadataBases
	 * @return
	 */
	public List<OracleTableVO> directMiningOracleTables(Long schemaId, Map<String,OracleTableVO> dependencyTables , MetadataBaseVO... metadataBases) {

		 McSchemaPO schema = schemaMapper.findById( schemaId );
		 if( schema == null ) {
			 throw new MetaDataException("Schema["+schemaId+"]未找到.");
		 }
		 
		List<OracleTableVO> result = new ArrayList<>();
		if( metadataBases != null && metadataBases.length > 0 ) {
			if( dependencyTables == null ) {
				dependencyTables = Maps.newHashMap();
			}
			
			for( MetadataBaseVO mbv : metadataBases) {
				String tableName = mbv.getName() ;
				boolean  ignoreDependency = false ;
				
				String currentKey = schemaId+"."+tableName ;
				if( dependencyTables.containsKey(currentKey) ) {
					OracleTableVO dotv = dependencyTables.get(currentKey);
					if( dotv != null ) {
						//在外键冲突时已经采集过该表  , 直接进行下一个表的采集
						result.add(dotv) ;
						continue ;
					}else {
						//该表存在外键循环依赖 ,忽略外键
						ignoreDependency = true ;
					}
				}
				
				OracleTableVO otv = new OracleTableVO() ;
				copyProperties(mbv, otv);//mysqlSQLAnalyzer.getTablesFromDB(schemaId, tableName).get(0);
				otv.setSchemaId(schemaId);
				otv.setIsGather(true);
				
				try {
					
					List<TableFkOracle> fks = oracleSQLAnalyzer.getTableForeignkeys(schema, tableName, ignoreDependency);
					otv.setForeignKeyList(fks);
					
					List<String> ignoreIndexs = Lists.newArrayList() ;
					
					TablePkOracle pks = oracleSQLAnalyzer.getTablePrimaryKeys(schema, tableName);
					otv.setPrimaryKey(pks);
					if( pks != null ) {
						ignoreIndexs.add(pks.getName()) ;
					}else {
						pks = new TablePkOracle();
						pks.setSequenceStatus(1);
					}
					
					List<TableUnOracle> uniKeys = oracleSQLAnalyzer.getTableUniqueKey(schema, tableName);
					otv.setUniqueList(uniKeys);
					if(uniKeys != null && uniKeys.size()>0 ) {
						ignoreIndexs.addAll(uniKeys.stream().map(u -> u.getName()).collect(Collectors.toList())) ;
					}
					
					List<TableColumn> columns = oracleSQLAnalyzer.getTableColumns(schemaId, tableName,pks) ;
					otv.setColumnList(columns);
					
					List<TableIdxOracle> indexs = oracleSQLAnalyzer.getTableIndexs(schema, tableName, ignoreIndexs) ;
					otv.setIndexList(indexs);
		
					List<TableChOracle> checks = oracleSQLAnalyzer.getTableCheck(schema, tableName);
					otv.setCheckList(checks);
					
					TableSetOracle settings = oracleSQLAnalyzer.getTableSetting(schema, tableName);
					otv.setTableSetting(settings);
					
					oracleTableService.addTable(otv);
					
				} catch (DependencyNotExistException e) {
					//存储当前表信息,以备 不进行循环直采
					dependencyTables.put( currentKey , null );
					
					String dependencyKey = e.getDependencySchemaId()+"."+e.getDependencyTableName() ;
					//dependencyTables包括该依赖且为空时,为循环依赖,忽略该依赖直接再次进行直采,此次dependencyTables包含空,会进行忽略依赖采集
					//dependencyTables包括该依赖且不为空时,已经采集过该依赖,无需再采集,理论上不会出现,已经采集就不会再报依赖不存在异常
					if( !dependencyTables.containsKey(dependencyKey)  ) {
						//新建依赖表
						MetadataBaseVO temp =new MetadataBaseVO();
						copyProperties(mbv, temp) ;
						temp.setName(e.getDependencyTableName());
						//创建依赖的表
						List<OracleTableVO> dotv = directMiningOracleTables(e.getDependencySchemaId(),dependencyTables , temp);
						if( dotv != null && dotv.size() > 0) {
							//依赖表创建完成,存储,以备不进行重复采集
							dependencyTables.put(dependencyKey, dotv.get(0)) ;
						}
						//该次依赖已经成功采集,且未采集当前table
						if( dependencyTables.get(currentKey) == null ){
							dependencyTables.remove(currentKey);
						}
					}
					//继续创建失败的表
					otv = directMiningOracleTables(schemaId,dependencyTables, mbv).get(0);
				}
				result.add(otv) ;
			}
		}
		return result ;
	}

	/**
	 * 直采 Oracle 视图
	 * @param schemaId
	 * @param metadataBases
	 * @return
	 */
	public List<ViewVO> directMiningOracleViews(Long schemaId,  MetadataBaseVO... metadataBases) {
		List<ViewVO> result = new ArrayList<>();
		if( metadataBases != null && metadataBases.length > 0 ) {
			for( MetadataBaseVO mbv : metadataBases) {
				DBViewVO mvv = new DBViewVO() ;
				copyProperties(mbv, mvv ); //List<DBViewVO> vs = mysqlSQLAnalyzer.getViewsFromDB( schemaId, viewName).get(0) ;
				if( mvv.getViewDetail() == null || StringUtils.isEmpty( mvv.getViewDetail().getViewSql() ) ) {
					mvv.setViewDetail(new ViewDetail());
					mvv.getViewDetail().setViewSql(oracleSQLAnalyzer.getCreateViewSqlFromDB(schemaId, mvv.getName()));
					mvv.setVersion(1);
				}
				mvv.setSchemaId(schemaId);
				mvv.setIsGather(true);
				result.add(mvv) ;
				
				//List<TableColumn> cs = oracleSQLAnalyzer.getViewColumns(schemaId, mvv.getName(), mvv.getViewDetail().getViewSql());
				//System.out.println(cs);
				
				//视图保存
				oracleViewService.addView(mvv);
				//视图字段方法
				Metadata view = metadataMapper.findById(mvv.getId());
				if( view != null ) {
					oracleViewService.saveOrUpdateViewColumns(view);
				}
				
			}
		}
		return result ;
	}



}

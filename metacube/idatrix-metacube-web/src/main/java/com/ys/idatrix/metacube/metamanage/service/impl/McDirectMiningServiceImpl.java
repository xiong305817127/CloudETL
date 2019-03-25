package com.ys.idatrix.metacube.metamanage.service.impl;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.StringUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.RedisUtil;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.metamanage.domain.*;
import com.ys.idatrix.metacube.metamanage.mapper.McDatabaseMapper;
import com.ys.idatrix.metacube.metamanage.mapper.McSchemaMapper;
import com.ys.idatrix.metacube.metamanage.service.*;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.MysqlSQLAnalyzer;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.OracleSQLAnalyzer;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.SQLAnalyzer;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto.DatabaseConnect;
import com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto.DependencyNotExistException;
import com.ys.idatrix.metacube.metamanage.vo.request.*;
import com.ys.idatrix.metacube.metamanage.vo.response.DatasourceVO;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class McDirectMiningServiceImpl implements McDirectMiningService {

	@Autowired
	private McSchemaMapper schemaMapper;

    @Autowired
    private McDatabaseMapper databaseMapper;
	
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
	
	@Autowired
	GraphSyncService graphSyncService ;

	@Override
	public List<TableColumn> getViewColumns(Long schemaId, String viewName, String viewSql){
		DatabaseConnect dbInfo = translateDatabaseInfo(schemaId);
		switch( dbInfo.getDatabaseType()) {
		case JdbcConstants.MYSQL: return mysqlSQLAnalyzer.getViewColumns(dbInfo, viewName, viewSql) ;
		case JdbcConstants.ORACLE: return oracleSQLAnalyzer.getViewColumns(dbInfo, viewName, viewSql) ;
		}
		
		return null ;
	}
	
	@Override
	public  MiningTaskDto getMiningTask( Long schemaId,int resourceType ) {
		return getMiningCache(schemaId,resourceType, false) ;
	}
	
	@Override
	public List<? extends TableVO>  getTableAllInfo( Long schemaId ) {
		DatabaseConnect dbInfo = translateDatabaseInfo(schemaId);
		return sqlAnalyzer.getAllTables(dbInfo, true);
	}

	@Override
	public List<? extends ViewVO>  getViewAllInfo(  Long schemaId  ) {
		DatabaseConnect dbInfo = translateDatabaseInfo(schemaId);
		return sqlAnalyzer.getAllViews(dbInfo, true) ;
	}
	
	@Override
	@Async
	public void  directMiningTables( Long schemaId, MetadataBaseVO... metadataBases ) {
		//刷新当前线程用户
		UserUtils.refreshCacheMap( "DirectMining-"+schemaId );
		
		//初始化一个缓存
		MiningTaskDto cacheDto = getMiningCache(schemaId,1, true) ;
		cacheDto.init();
			
		try {
			DatabaseConnect dbInfo = translateDatabaseInfo(schemaId);
			switch( dbInfo.getDatabaseType() ) {
			case JdbcConstants.MYSQL:  directMiningMysqlTables(dbInfo,cacheDto, null,metadataBases) ;break ;
			case JdbcConstants.ORACLE:  directMiningOracleTables(dbInfo,cacheDto, null,metadataBases );break ;
			}
			
			//成功完成
			cacheDto.setEndTime(new Date());
			cacheDto.changeStatus(cacheDto.getErrors()!= null && !cacheDto.getErrors().isEmpty() ? 12 : 11 );
		}catch( Exception e) {
			log.error("[Error]直采数据库 ",cacheDto.getMessage());
			log.error("异常.",e);
			//异常完成
			cacheDto.setMessage(e.getMessage());
			cacheDto.setEndTime(new Date());
			cacheDto.changeStatus(13);
		}
		
		//清空当前线程用户
		UserUtils.clearCacheMap();
	}
	
	
	@Override
	@Async
	public void  directMiningViews( Long schemaId,  MetadataBaseVO... metadataBases ) {
		//刷新当前线程用户
		UserUtils.refreshCacheMap( "DirectMining-"+schemaId );
				
		//初始化一个缓存
		MiningTaskDto cacheDto = getMiningCache(schemaId, 2, true) ;
		cacheDto.init();
		try {
			DatabaseConnect dbInfo = translateDatabaseInfo(schemaId);
			switch( dbInfo.getDatabaseType()) {
			case JdbcConstants.MYSQL:  directMiningMysqlViews(dbInfo,cacheDto,  metadataBases) ;break ;
			case JdbcConstants.ORACLE:  directMiningOracleViews(dbInfo,cacheDto,  metadataBases);break ;
			}
			
			//成功完成
			cacheDto.setEndTime(new Date());
			cacheDto.changeStatus(cacheDto.getErrors()!= null && !cacheDto.getErrors().isEmpty() ? 12 : 11 );
		}catch( Exception e) {
			log.error("[Error]直采数据库 ",cacheDto.getMessage());
			log.error("异常.",e);
			//异常完成
			cacheDto.setMessage(e.getMessage());
			cacheDto.setEndTime(new Date());
			cacheDto.changeStatus(13);
		}
		
		//清空当前线程用户
		UserUtils.clearCacheMap();
		
	}
	
	
	/**
	 * 直采Mysql 表
	 * @param schemaId
	 * @param dependencyTables , 表依赖的引用表采集对象Map , 初始时传NUll , 方法本身递归时使用
	 * @param metadataBases
	 * @return
	 */
	public List<MySqlTableVO> directMiningMysqlTables( DatabaseConnect dbInfo, MiningTaskDto cacheDto, Map<String,MySqlTableVO> dependencyTables ,  MetadataBaseVO... metadataBases) {
		
		Long schemaId = dbInfo.getSchemaId() ;
		if( cacheDto == null ) {
			cacheDto = getMiningCache(dbInfo.getSchemaId(), 1, true) ;
		}
		
		List<MySqlTableVO> result = new ArrayList<>();
		if( metadataBases != null && metadataBases.length > 0 ) {
			if( dependencyTables == null ) {
				dependencyTables = Maps.newHashMap();
				cacheDto.changeStatus(1);
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
						cacheDto.addSuccess(dmtv.getName());
						
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
				
				SQLStatement stmt  = mysqlSQLAnalyzer.analyzerCreateSql(dbInfo, tableName) ;
				
				try {
					
					List<TableFkMysql> fks = mysqlSQLAnalyzer.getTableForeignkeys(dbInfo, tableName,ignoreDependency, stmt) ;
					mtv.setTableFkMysqlList(fks);
					
					List<TableIdxMysql> is = mysqlSQLAnalyzer.getTableIndexs(dbInfo, tableName, stmt) ;
					mtv.setTableIndexList(is);
					List<TableColumn> cloumns = mysqlSQLAnalyzer.getTableColumns(dbInfo, tableName, stmt) ;
					mtv.setTableColumnList(cloumns);
					
					mysqlTableService.addMiningTable(mtv);
					
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
						DatabaseConnect dbInfo1 = translateDatabaseInfo(e.getDependencySchemaId()) ;
						List<MySqlTableVO> dmtv = directMiningMysqlTables(dbInfo1,cacheDto, dependencyTables, temp);
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
					mtv = directMiningMysqlTables(dbInfo,cacheDto, dependencyTables, mbv).get(0);
				}catch( MetaDataException oe) {
					
					log.error("[Error]直采数据库 schema:"+dbInfo.getSchemaId()+",表:"+mtv.getName());
					log.error("异常.",oe);
					cacheDto.addError(mtv.getName());
					cacheDto.addErrorMessage(mtv.getName(), oe.getMessage());
					continue ;
				}
				result.add(mtv) ;
				cacheDto.addSuccess(mtv.getName());
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
	public List<DBViewVO> directMiningMysqlViews(DatabaseConnect dbInfo, MiningTaskDto cacheDto, MetadataBaseVO... metadataBases)  {
		List<DBViewVO> result = new ArrayList<>();
		if( metadataBases != null && metadataBases.length > 0 ) {
			
			if( cacheDto == null ) {
				cacheDto = getMiningCache(dbInfo.getSchemaId(), 2, true) ;
			}
			cacheDto.changeStatus(1);
			
			for( MetadataBaseVO mbv : metadataBases) {
				
				try {
					
					DBViewVO mvv = new DBViewVO() ;
					copyProperties(mbv, mvv ); //List<DBViewVO> vs = mysqlSQLAnalyzer.getViewsFromDB( schemaId, viewName).get(0) ;
					if( mvv.getViewDetail() == null || StringUtils.isEmpty( mvv.getViewDetail().getViewSql() ) ) {
						mvv.setViewDetail(new ViewDetail());
						mvv.getViewDetail().setViewSql(mysqlSQLAnalyzer.getViewSelectSqlFromDB(dbInfo, mvv.getName()));
						mvv.setVersion(1);
					}
					mvv.setSchemaId(dbInfo.getSchemaId());
					mvv.setIsGather(true);
					
					//视图保存
					mysqlViewService.addMiningView(mvv);
					result.add(mvv) ;
					cacheDto.addSuccess(mvv.getName());
					
				}catch( MetaDataException oe) {
					
					log.error("[Error]直采数据库 schema:"+mbv.getName());
					log.error("异常.",oe);
					cacheDto.addError(mbv.getName());
					cacheDto.addErrorMessage(mbv.getName(), oe.getMessage());
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
	public List<OracleTableVO> directMiningOracleTables(DatabaseConnect dbInfo,MiningTaskDto cacheDto,  Map<String,OracleTableVO> dependencyTables , MetadataBaseVO... metadataBases) {

		Long schemaId = dbInfo.getSchemaId() ;
		if( cacheDto == null ) {
			cacheDto = getMiningCache(dbInfo.getSchemaId(),1, true) ;
		}
		
		List<OracleTableVO> result = new ArrayList<>();
		if( metadataBases != null && metadataBases.length > 0 ) {
			if( dependencyTables == null ) {
				dependencyTables = Maps.newHashMap();
				cacheDto.changeStatus(1);
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
						cacheDto.addSuccess(dotv.getName());
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
					
					List<TableFkOracle> fks = oracleSQLAnalyzer.getTableForeignkeys(dbInfo, tableName, ignoreDependency);
					otv.setForeignKeyList(fks);
					
					List<String> ignoreIndexs = Lists.newArrayList() ;
					
					TablePkOracle pks = oracleSQLAnalyzer.getTablePrimaryKeys(dbInfo, tableName);
					otv.setPrimaryKey(pks);
					if( pks != null ) {
						ignoreIndexs.add(pks.getName()) ;
					}else {
						pks = new TablePkOracle();
						pks.setSequenceStatus(1);
					}
					
					List<TableUnOracle> uniKeys = oracleSQLAnalyzer.getTableUniqueKey(dbInfo, tableName);
					otv.setUniqueList(uniKeys);
					if(uniKeys != null && uniKeys.size()>0 ) {
						ignoreIndexs.addAll(uniKeys.stream().map(u -> u.getName()).collect(Collectors.toList())) ;
					}
					
					List<TableColumn> columns = oracleSQLAnalyzer.getTableColumns(dbInfo, tableName,pks) ;
					otv.setColumnList(columns);
					
					List<TableIdxOracle> indexs = oracleSQLAnalyzer.getTableIndexs(dbInfo, tableName, ignoreIndexs) ;
					otv.setIndexList(indexs);
		
					List<TableChOracle> checks = oracleSQLAnalyzer.getTableCheck(dbInfo, tableName);
					otv.setCheckList(checks);
					
					TableSetOracle settings = oracleSQLAnalyzer.getTableSetting(dbInfo, tableName);
					otv.setTableSetting(settings);
					
					oracleTableService.addMiningTable(otv);
					
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
						DatabaseConnect dbInfo1 = translateDatabaseInfo(e.getDependencySchemaId()) ;
						List<OracleTableVO> dotv = directMiningOracleTables(dbInfo1,cacheDto,  dependencyTables , temp);
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
					otv = directMiningOracleTables(dbInfo,cacheDto, dependencyTables, mbv).get(0);
				}catch( MetaDataException oe) {
					
					log.error("[Error]直采数据库 schema:"+mbv.getName());
					log.error("异常.",oe);
					cacheDto.addError(mbv.getName());
					cacheDto.addErrorMessage(mbv.getName(), oe.getMessage());
					continue ;
				}
				result.add(otv) ;
				cacheDto.addSuccess(otv.getName());
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
	public List<ViewVO> directMiningOracleViews(DatabaseConnect dbInfo,MiningTaskDto cacheDto, MetadataBaseVO... metadataBases) {
		
		List<ViewVO> result = new ArrayList<>();
		if( metadataBases != null && metadataBases.length > 0 ) {
			
			if( cacheDto == null ) {
				cacheDto = getMiningCache(dbInfo.getSchemaId(),2, true) ;
			}
			cacheDto.changeStatus(1);
			
			for( MetadataBaseVO mbv : metadataBases) {
				try {
					
					DBViewVO mvv = new DBViewVO() ;
					copyProperties(mbv, mvv ); //List<DBViewVO> vs = mysqlSQLAnalyzer.getViewsFromDB( schemaId, viewName).get(0) ;
					if( mvv.getViewDetail() == null || StringUtils.isEmpty( mvv.getViewDetail().getViewSql() ) ) {
						mvv.setViewDetail(new ViewDetail());
						mvv.getViewDetail().setViewSql(oracleSQLAnalyzer.getCreateViewSqlFromDB(dbInfo, mvv.getName()));
						mvv.setVersion(1);
					}
					mvv.setSchemaId( dbInfo.getSchemaId());
					mvv.setIsGather(true);
					
					//视图保存
					oracleViewService.addMiningView(mvv);
					result.add(mvv) ;
					cacheDto.addSuccess(mvv.getName());
					
				}catch( MetaDataException oe) {
					
					log.error("[Error]直采数据库 schema:"+mbv.getName());
					log.error("异常.",oe);
					cacheDto.addError(mbv.getName());
					cacheDto.addErrorMessage(mbv.getName(), oe.getMessage());
				}
			}
		}
		return result ;
	}

	
	private String getDbType( String databaseType ) {
		switch( databaseType) {
		case "1": return JdbcConstants.MYSQL ;
		case "2": return JdbcConstants.ORACLE ;
		
		default : return JdbcConstants.MYSQL ;
		}
	}
	
	private DatabaseConnect translateDatabaseInfo(Long schemaId ) {
		
		McSchemaPO schema = schemaMapper.findById( schemaId );
        if( schema == null ) {
            throw new MetaDataException("Schema["+schemaId+"]未找到.");
        }
        DatasourceVO datasource = databaseMapper.getDatasourceInfoById(schema.getDbId());
        if ( datasource == null ) {
            throw new MetaDataException("schema["+schema.getName()+"]对应的数据库未找到.");
        }
        return new DatabaseConnect(getDbType(datasource.getType()), schemaId, schema, datasource);
	}
	
	private final String  MiningCacheKey = "MetaCubeMiningCacheKey" ;
	private volatile LoadingCache<String,MiningTaskDto> cache ; //本地缓存
	
	/**
	 * 获取 支持任务的 缓存信息 
	 * @param schemaId
	 * @param force 当不存在时,是否强制生成一个缓存 
	 * @return
	 */
	private MiningTaskDto getMiningCache( Long schemaId ,int resourceType, boolean force) {
		
		String itemkey = schemaId+"-"+resourceType ;
		
		if( cache == null ) {
			synchronized (McDirectMiningServiceImpl.class) {
				if( cache == null ) {
					//初始化缓存
					cache = CacheBuilder.newBuilder()
							.expireAfterWrite(24, TimeUnit.HOURS)
							.build(new CacheLoader<String, MiningTaskDto>() {
					            @Override
					            public MiningTaskDto load(String key) throws Exception {
					            	//本地缓存中不存在,尝试从远程拉取
					            	MiningTaskDto dto = null;
					            	Object remoteCache = RedisUtil.hget(MiningCacheKey, key) ;
					            	if( remoteCache != null ) {
					            		//远程缓存中存在
					            		dto = (MiningTaskDto)remoteCache ;
					            		if( dto.isEnd() || ( System.currentTimeMillis() - dto.getStartTime().getTime() > 60*60*1000) ) {
					            			//已经结束  或者 运行时间已经大于1小时, 删除远程缓存
					            			RedisUtil.hdel(MiningCacheKey, key) ;
					            			dto = null ;
					            		}
					            	}
					            	if( dto == null ) {
					            		//远程中没有 , 缓存一个新建的对象
					            		String[] d = key.split("-",2);
					            		dto = new MiningTaskDto();
						            	dto.setStatus(-1);
						            	dto.setSchemaId(Long.valueOf(d[0]));
						            	dto.setResourceType(Integer.valueOf(d[1]));
						            	dto.setStartTime(new Date());
					            	}
					            	return dto ;
					            }
							});
				}
			}
		}
		
		MiningTaskDto dto;
		try {
			dto = cache.get(itemkey);
		} catch (ExecutionException e) {
			//远程中没有 , 缓存一个新建的对象
    		dto = new MiningTaskDto();
        	dto.setStatus(-1);
        	dto.setSchemaId(schemaId);
        	dto.setResourceType(resourceType);
        	dto.setStartTime(new Date());
        	cache.put(itemkey, dto);
		}
		if( dto.isEnd() && force ) {
			//已经结束  ,且开始新的缓存, 删除远程缓存
			RedisUtil.hdel(MiningCacheKey, itemkey ) ;
			//删除本地缓存
			cache.invalidate(itemkey);
		}else if( dto.getStatus() == -1 && !force ) {
			//缓存中不存在,且不需要强制生成一个 ,
			//删除本地缓存
			cache.invalidate(itemkey);
        	return null ;
		}
		return dto ;
	}
	
	@Getter
	@Setter
	public class MiningTaskDto {
		
		private Long schemaId ;
	    private Integer resourceType ; //1:表 2:视图"
		private Date startTime ;
		private Date endTime ;
		private int status = -1 ; //-1:新建 , 0: 初始化 , 1: 进行中 , 11:成功完成 ,  12: 部分完成, 13 :运行异常 
		private List<String> success;
		private List<String> errors;
		private Map<String,String> errorsMessages;
		private String message ;
		
		public MiningTaskDto init() {
			changeStatus(0);
			return this ;
		}
		
		public MiningTaskDto  addSuccess( String name) {
			if( success == null ) {
				success = new ArrayList<>();
			}
			success.add(name) ;
			
			return this ;
		}
		
		public MiningTaskDto  addError( String name) {
			if( errors == null ) {
				errors = new ArrayList<>();
			}
			errors.add(name) ;
			
			return this ;
		}
		
		public MiningTaskDto  addErrorMessage(String name, String mess) {
			if( errorsMessages == null ) {
				errorsMessages = new HashMap<>();
			}
			errorsMessages.put(name, mess) ;
			
			return this ;
		}
		
		public MiningTaskDto changeStatus( int status ) {
			
			if( status >= 0 && this.status != status ) {
				this.status = status ;
				//状态改变 , 更新远程缓存
				RedisUtil.hset(MiningCacheKey, schemaId+"", this ) ;
			}
			return this;
		}
		
		public Boolean isEnd() {
			return status > 10 ;
		}
		
		public void setMessage(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			
			long time = ( endTime!= null ?endTime.getTime():System.currentTimeMillis()) - startTime.getTime()  ;
			
			StringBuffer sb = new StringBuffer();
			sb.append("schema:").append(schemaId).append("的采集任务").append(isEnd()?"运行结束 , ":"正在运作中... , ")
			  .append("采集成功:").append(success!= null ?success.size():0).append("个, ")
			  .append("采集失败:").append(errors!= null ?errors.size():0).append("个, ")
			  .append("耗时:").append(time>1000? (time/1000) : time ).append(time>1000? "秒 ." : "毫秒." );
			
			if( !StringUtils.isEmpty(message) ) {
				 sb.append("处理出现异常:").append(message).append(".");
			}
			
			return sb.toString() ;
		}
		
	}

}

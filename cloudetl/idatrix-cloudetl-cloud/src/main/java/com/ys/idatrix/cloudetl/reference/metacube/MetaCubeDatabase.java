/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.reference.metacube;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.GenericDatabaseMeta;
import org.pentaho.di.core.database.MSSQLServerNativeDatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.plugins.DatabasePluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.StringObjectId;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeDbDatabaseDto;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeDbSchemaDto;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeDbTableDto;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeDbTableFieldDto;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeDbTableViews;
import com.ys.idatrix.metacube.api.beans.Database;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.api.beans.MetaFieldDTO;
import com.ys.idatrix.metacube.api.beans.ModuleTypeEnum;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.beans.Schema;
import com.ys.idatrix.metacube.api.beans.SchemaDetails;
import com.ys.idatrix.metacube.api.beans.TableViewDTO;
import com.ys.idatrix.metacube.api.service.MetadataDatabaseService;
import com.ys.idatrix.metacube.api.service.MetadataSchemaService;
import com.ys.idatrix.metacube.api.service.MetadataServiceProvide;

/**
 * Synchronize database connection data from MetaCube system
 * by calling its RPC APIs exported as dubbo services.
 * 
 * @author JW
 * @since 2017年5月24日
 *
 */
@Service
public class MetaCubeDatabase extends MetaCubeBase {
	
	public static final String ATRRIBUTE_METACUBE_DATABASE_ID = "METACUBE_DATABASE_ID";
	public static final String ATRRIBUTE_METACUBE_SCHEMA_ID = "METACUBE_SCHEMA_ID";
	public static final String ATRRIBUTE_METACUBE_TABLE_ID = "METACUBE_TABLE_ID";

	@Reference(check=false)
	private MetadataDatabaseService metadataDatabaseService;
	@Reference(check=false )
	private MetadataSchemaService metadataSchemaService;
	@Reference(check=false )
	private MetadataServiceProvide metadataServiceProvider;

	/**
	 * Adjust database type for requirements.
	 * @param dbType
	 * @return
	 */
	private String adjustDatabaseType(DatabaseTypeEnum dbType) {
		if( dbType== null || DatabaseTypeEnum.HDFS.equals( dbType ) || DatabaseTypeEnum.ELASTICSEARCH.equals(dbType)  ) {
			return null ;
		}
		switch(dbType) {
		case HBASE: return "HBASETABLE";
		case HIVE: return "HIVE3";
		case DM: return "DM7";
		default: return dbType.toString();
		}
	}
	
	private static DatabaseTypeEnum adjustDatabaseType(String dbType) {
		if( Utils.isEmpty(dbType) ) {
			return null ;
		}
		switch(dbType) {
		case "HBASETABLE" : return DatabaseTypeEnum.HBASE;
		case "HIVE3": return DatabaseTypeEnum.HIVE;
		case "DM7" : return DatabaseTypeEnum.DM;
		default: return DatabaseTypeEnum.valueOf(dbType);
		}
	}


	/**
	 * Adjust database type for requirements.
	 * @param dbType
	 * @return
	 */
	private String adjustDatabaseUrl(DatabaseMeta dbMeta) {
		if ( "HBASETABLE".equalsIgnoreCase(dbMeta.getPluginId())) {
			DatabaseInterface dataInterface = dbMeta.getDatabaseInterface();
			Object hbaseUrl = OsgiBundleUtils.invokeOsgiMethod(dataInterface, "getPhoenixUrl");
			if(hbaseUrl != null){
				return (String) hbaseUrl;
			}
		} 
		try {
			return dbMeta.getURL();
		} catch (KettleDatabaseException e) {
			return "";
		}
	}
	

	/**
	 * 从元数据读取数据库列表
	 * @return
	 */
	public List<MetaCubeDbDatabaseDto> getMetaCubeDbs(String owner ,  Boolean isRead) {
		CloudLogger.getInstance(owner).info(this, "getDatabaseMetaList()...");

		List<MetaCubeDbDatabaseDto> dbMetaList = new ArrayList<>();
		ResultBean<List<Database>> resultBean = metadataDatabaseService.listDatabaseWithModuleAuth(owner, ModuleTypeEnum.ETL, readOrWriteAction(isRead));
		CloudLogger.getInstance(owner).info(this, "getDatabaseMetaList result:", resultBean);
		if( resultBean != null  && resultBean.isSuccess() && resultBean.getData() != null ) {
			List<Database> dbInfos = resultBean.getData() ;
			for ( Database info : dbInfos ) {
				if (info == null || StringUtils.isEmpty(info.getIp() ) || StringUtils.isEmpty(info.getDatabaseType())) {
					continue;
				}
				String type = adjustDatabaseType(info.getDatabaseType());
					
				MetaCubeDbDatabaseDto mddd = new MetaCubeDbDatabaseDto();
				mddd.setName(generateConnectionDisplayName(type, info.getIp(), info.getPort(),null));
				mddd.setDatabaseId(info.getDatabaseId());
				mddd.setIp(info.getIp());
				mddd.setPort(info.getPort());
				mddd.setType(type);
				
				if( info.getSchemaList() != null && info.getSchemaList().size() > 0) {
					mddd.setSchemaList(
							info.getSchemaList().stream().map(sc -> {
								MetaCubeDbSchemaDto mdsd = new MetaCubeDbSchemaDto();
								mdsd.setSchemaId(sc.getSchemaId());
								mdsd.setSchemaName(sc.getSchemaName());
								mdsd.setUsername(sc.getUsername());
								mdsd.setPassword(sc.getPassword());
								mdsd.setServiceName(sc.getServiceName());
								
								mdsd.setName(generateConnectionDisplayName(type, info.getIp(), info.getPort(),sc.getSchemaName()));
								mdsd.setDatabaseId(info.getDatabaseId());
								mdsd.setDatabaseType(type);
								mdsd.setIp(info.getIp());
								mdsd.setPort(info.getPort());
								
								return mdsd ;
							}).collect(Collectors.toList())
					);
				}
				dbMetaList.add(mddd);
			}
		}
		return dbMetaList;
	}
	
	public List<MetaCubeDbSchemaDto> getMetacubeDbSchemas(String owner ,  Long databaseId ,  String connectionName , Boolean isRead){
		String[] ds = analysisDisplayName(connectionName) ;
		ds =  ds==null ? new String[] {null,null,"0"} : ds ;
		return getMetacubeDbSchemas(owner, databaseId, ds[1], Integer.valueOf(ds[2]), ds[0] , isRead);
	}
	
	public List<MetaCubeDbSchemaDto> getMetacubeDbSchemas(String owner , Long databaseId , String ip, Integer port, String databaseType, Boolean isRead) {
		CloudLogger.getInstance(owner).info(this,"getMetacubeDbSchemas("+ip+","+databaseType+")...");

		try {
			ResultBean<List<Schema>> resultBean = metadataSchemaService.listSchemaByIpAndDatabaseType(owner, ip, adjustDatabaseType(databaseType) ,ModuleTypeEnum.ETL , readOrWriteAction(isRead) );
			CloudLogger.getInstance(owner).info(this,"getMetacubeDbSchemas  result:", resultBean);
			if( resultBean != null  && resultBean.isSuccess() && resultBean.getData() != null ) {
				List<Schema> schemas = resultBean.getData() ;
				if( ( schemas != null && !schemas.isEmpty() ) ) {
					return schemas.stream().map(sc -> {
								MetaCubeDbSchemaDto mdsd = new MetaCubeDbSchemaDto();
								
								mdsd.setSchemaId(sc.getSchemaId());
								mdsd.setSchemaName(sc.getSchemaName());
								mdsd.setUsername(sc.getUsername());
								mdsd.setPassword(sc.getPassword());
								mdsd.setServiceName(sc.getServiceName());
								
								mdsd.setName(generateConnectionDisplayName(databaseType, ip, port,sc.getSchemaName() ));
								mdsd.setDatabaseId(databaseId);
								mdsd.setDatabaseType(databaseType);
								mdsd.setIp(ip);
								mdsd.setPort(port);
								
								return mdsd ;
							}).collect(Collectors.toList()) ;
				}
			} 
		} catch (Exception ex) {
			CloudLogger.getInstance(owner).error(this ,"getMetacubeDbSchemas 获取数据库schemas失败",ex);
		}
		return null;
	}
	
	public MetaCubeDbSchemaDto getMetacubeDbSchema(String owner , Long schemaId) {
		if(schemaId == null ) {
			return null;
		}
		CloudLogger.getInstance(owner).info(this,"getMetacubeDbSchema("+schemaId+")...");

		try {
			ResultBean<SchemaDetails> resultBean = metadataSchemaService.getSchemaById(owner , schemaId);
			CloudLogger.getInstance(owner).info(this,"getMetacubeDbSchemas  result:", resultBean);
			if( resultBean != null  && resultBean.isSuccess() && resultBean.getData() != null ) {
				SchemaDetails schema = resultBean.getData() ;
				if( ( schema != null ) ) {
					String databaseType = adjustDatabaseType(schema.getDatabaseType()) ;
					
					MetaCubeDbSchemaDto mdsd = new MetaCubeDbSchemaDto();
					
					mdsd.setSchemaId(schema.getSchemaId());
					mdsd.setSchemaName(schema.getSchemaName());
					mdsd.setUsername(schema.getUsername());
					mdsd.setPassword(schema.getPassword());
					mdsd.setServiceName(schema.getServiceName());
					
					mdsd.setName(generateConnectionDisplayName(databaseType, schema.getIp(), schema.getPort(),schema.getSchemaName() ));
					mdsd.setDatabaseId(schema.getDatabaseId());
					mdsd.setDatabaseType(databaseType);
					mdsd.setIp(schema.getIp());
					mdsd.setPort(schema.getPort());
					
					return mdsd ;
				}
			} 
		} catch (Exception ex) {
			CloudLogger.getInstance(owner).error(this ,"getMetacubeDbSchemas 获取数据库schemas失败",ex);
		}
		return null;
	}
	
	
	public List<DatabaseMeta> getDatabaseMetaList( String owner, Boolean isRead ){ 
		
		List<DatabaseMeta> result = new ArrayList<DatabaseMeta>();
		List<MetaCubeDbDatabaseDto> dbs = getMetaCubeDbs(owner, isRead);
		if( dbs != null && !dbs.isEmpty()) {
			dbs.stream().forEach(db -> {
				List<MetaCubeDbSchemaDto> schemaList = db.getSchemaList() ;
				if( schemaList == null || schemaList.size() == 0) {
					schemaList = getMetacubeDbSchemas(owner, db.getDatabaseId(), db.getIp(), db.getPort(), db.getType(),isRead);
				}
				if(schemaList == null ) {
					return  ;
				}
				result.addAll( schemaList.stream().map(s -> {
					return parseDatabaseMeta(owner , s);
					}).collect(Collectors.toList()) 
					);
			});
		}
		return result ;
	}
	
	
	public DatabaseMeta getDatabaseMeta( String owner,  Long schemaId ){ 
		MetaCubeDbSchemaDto schema = getMetacubeDbSchema(owner, schemaId);
		if(schema == null ) {
			return null ;
		}
		return parseDatabaseMeta(owner, schema) ;
	}
	

	/**
	 * Get DB tables from MetaCube.
	 * @param name
	 * @return
	 */
	public MetaCubeDbTableViews getMetaCubeDbTables(String owner , Long schemaId ,  Boolean isRead) {
		if(schemaId == null ) {
			return null;
		}
		CloudLogger.getInstance(owner).info(this ,"getMetaCubeDbTables("+schemaId+")...");

		try {
			ResultBean<TableViewDTO> resultBean = metadataServiceProvider.findTableOrViewBySchemaId(schemaId, owner, ModuleTypeEnum.ETL, readOrWriteAction(isRead));
			CloudLogger.getInstance(owner).info(this ,"getMetaCubeDbTables result:", resultBean);
			if( resultBean != null  && resultBean.isSuccess() && resultBean.getData() != null ) {
				TableViewDTO tablesAndViews = resultBean.getData() ;
				
				MetaCubeDbTableViews tableList = new MetaCubeDbTableViews();
				if(tablesAndViews.getTableList()!= null) {
					tablesAndViews.getTableList().stream().forEach(t -> {
						tableList.addTable(new MetaCubeDbTableDto(t.getMetaId().longValue(), t.getMetaName()));
					});
				}
				if(tablesAndViews.getViewList()!= null) {
					tablesAndViews.getViewList().stream().forEach(v -> {
						tableList.addView(new MetaCubeDbTableDto(v.getMetaId().longValue(), v.getMetaName()));
					});
				}
				return tableList;
			} 
		} catch (Exception ex) {
			CloudLogger.getInstance(owner).error(this ,"getMetaCubeDbTables, 获取数据库表信息失败",ex);
		}

		return null;
	}

	/**
	 * Get DB table fields from MetaCube.
	 * @param dbcName
	 * @param tableName
	 * @return
	 */
	public List<MetaCubeDbTableFieldDto> getMetaCubeDbFields(String owner , Long  metaId  ) {
		if(metaId == null ) {
			return null;
		}
		CloudLogger.getInstance(owner).info(this ,"getMetaCubeDbFields("+metaId+")...");

		try {
			ResultBean<List<MetaFieldDTO>> resultBean = metadataServiceProvider.findColumnListByTableIdOrViewId( metaId );
			CloudLogger.getInstance(owner).info(this ,"getMetaCubeDbFields result: ", resultBean);
			if( resultBean != null  && resultBean.isSuccess() && resultBean.getData() != null ) {
				List<MetaFieldDTO> fields = resultBean.getData() ;
				
				List<MetaCubeDbTableFieldDto> result = new ArrayList<>();
				fields.stream().forEach(f ->{
					
					MetaCubeDbTableFieldDto fieldsDto = new MetaCubeDbTableFieldDto();
					fieldsDto.setFieldName(f.getColumnName());
					fieldsDto.setFieldType(f.getDataType());
					fieldsDto.setFieldLength(f.getLength());
					fieldsDto.setIsPk(f.getIsPk());
					fieldsDto.setId(f.getId()+"");
					fieldsDto.setFieldPrecision(f.getTypePrecision());
					result.add(fieldsDto);
					
				});
				return result;
			} 
		} catch (Exception ex) {
			CloudLogger.getInstance(owner).error(this ,"getMetaCubeDbFields 获取数据库域信息失败 ",ex);
		}

		return null;
	}
	
	/**
	 * 转换元数据对象为 ETl数据库对象
	 * @param dbDto
	 * @param schemaDto
	 * @return
	 * @throws Exception
	 */
	private DatabaseMeta parseDatabaseMeta(String owner , MetaCubeDbSchemaDto schemaDto) {
		if( schemaDto == null) {
			return null;
		}

		DatabaseMeta databaseMeta = new DatabaseMeta();
		
		String type = schemaDto.getDatabaseType();
		if( Utils.isEmpty(type)) {
			CloudLogger.getInstance(owner).error(this ,"数据库插件类型错误,未找到类型." );
			return null ;
		}
		PluginInterface plugin = PluginRegistry.getInstance().getPlugin(DatabasePluginType.class, type );
		if (plugin == null) {
			CloudLogger.getInstance(owner).error(this ,"数据库插件类型错误,未知的类型:" + type );
			return null ;
		}
		try {
			DatabaseInterface databaseInterface = (DatabaseInterface) PluginRegistry.getInstance().loadClass(plugin);
	
			databaseInterface.setPluginId(type);
	
			databaseMeta.setDatabaseInterface(databaseInterface);
			databaseMeta.setObjectId(new StringObjectId(generateConnectionDisplayName(type, schemaDto.getIp(), schemaDto.getPort() ,schemaDto.getSchemaName())));
	
			databaseMeta.setName(schemaDto.getSchemaId().toString());
			databaseMeta.setDisplayName(generateConnectionDisplayName(type, schemaDto.getIp(), schemaDto.getPort() ,null ));
			
			databaseMeta.setDatabaseType(type);
			databaseMeta.setAccessType(0); //Native
			databaseMeta.setDBName( Const.NVL(schemaDto.getServiceName(), schemaDto.getSchemaName()) );
			databaseMeta.setHostname( schemaDto.getIp() );
			databaseMeta.setDBPort( schemaDto.getPort()+"" );
			databaseMeta.setUsername(schemaDto.getUsername());
			databaseMeta.setPassword(schemaDto.getPassword());
			databaseMeta.setPreferredSchemaName(schemaDto.getSchemaName());
	
			Properties attributes = databaseMeta.getAttributes();
			
			attributes.put(ATRRIBUTE_METACUBE_DATABASE_ID, schemaDto.getDatabaseId() ) ;
			attributes.put(ATRRIBUTE_METACUBE_SCHEMA_ID, schemaDto.getSchemaId() ) ;
			
			// for Generic
			attributes.put(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, Const.NVL(adjustDatabaseUrl(databaseMeta), ""));
			attributes.put(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS, databaseInterface.getDriverClass());
			// for MSSQL
			attributes.put(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY, Const.NVL(databaseInterface.getDriverClass(), ""));
	
			// Override properties defined in the idatrix configuration file.
			if ("true".equalsIgnoreCase(IdatrixPropertyUtil.getProperty("db.parameter.override"))) {
				// Override properties
				attributes.put("STREAM_RESULTS", "true".equalsIgnoreCase(IdatrixPropertyUtil.getProperty("db.parameter.stream.results", "true")) ? "Y" : "N");
	
				// Override extra properties
				databaseMeta.addExtraOption(databaseInterface.getPluginId(), "defaultFetchSize", IdatrixPropertyUtil.getProperty("db.parameter.defaultFetchSize", "1000"));
				databaseMeta.addExtraOption(databaseInterface.getPluginId(), "useCursorFetch", IdatrixPropertyUtil.getProperty("db.parameter.useCursorFetch", "true"));
			}
	
			// 额外的属性适配
			if ("ORACLE".equalsIgnoreCase(databaseInterface.getPluginId())) {
				if (IdatrixPropertyUtil.getBooleanProperty("idatrix.database.oracle.quote.all.field", true)) {
					databaseMeta.setQuoteAllFields(true);
				}
			}
			if ("DM7".equalsIgnoreCase(databaseInterface.getPluginId())) {
				if (IdatrixPropertyUtil.getBooleanProperty("idatrix.database.dm7.quote.all.field", true)) {
					databaseMeta.setQuoteAllFields(true);
				}
			}
			return databaseMeta;
			
		} catch (Exception e) {
			CloudLogger.getInstance(owner).error(this ,"转换数据库对象异常:" ,e);
			return null;
		}
		
	}
	
	public static Long getDatabaseIdFromMeta(DatabaseMeta dbMeta) {
		if(dbMeta == null ) {
			return null ;
		}
		return (Long) dbMeta.getAttributes().get(ATRRIBUTE_METACUBE_DATABASE_ID);
	}
	
	public static Long getSchemaIdFromMeta(DatabaseMeta dbMeta) {
		if(dbMeta == null ) {
			return null ;
		}
		return (Long) dbMeta.getAttributes().get(ATRRIBUTE_METACUBE_SCHEMA_ID);
	}
	
	private static String generateConnectionDisplayName(String type , String ip , Integer port ,String schema) {
		DatabaseTypeEnum adjustType = adjustDatabaseType(type) ;
		type =adjustType != null?adjustType.getName(): "UNKNOW";
		if( Utils.isEmpty( schema) ) {
			return type+"["+ip+":"+port+"]";
		}else {
			return type+"["+ip+":"+port+"/"+schema+"]";
		}
		
	}

	private static String[] analysisDisplayName(String name) {
		if(Utils.isEmpty(name)) {
			return null ;
		}
		
		String regex = "(.*)\\[(.*):(\\d+)/?(\\w*)\\]";
		Pattern pattern = Pattern.compile(regex);  
		Matcher matcher = pattern.matcher(name);  
		matcher.matches();
		String[] res = new String[matcher.groupCount()];
		if (matcher.groupCount() >= 3 ) {  
			res[0] = matcher.group(1) ;
			res[1] =  matcher.group(2) ;
			res[2] =  matcher.group(3) ;
			if( matcher.groupCount() == 4 ) {
				res[3] =  matcher.group(4) ;
			}
		}  
		return res ;
	}
	
}

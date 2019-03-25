/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.GenericDatabaseMeta;
import org.pentaho.di.core.database.MSSQLServerNativeDatabaseMeta;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.i18n.BaseMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.deploy.MetaCubeCategory;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto.DealRowsInterface;
import com.ys.idatrix.cloudetl.dto.common.TestResultDto;
import com.ys.idatrix.cloudetl.dto.db.DbAdvanceOption;
import com.ys.idatrix.cloudetl.dto.db.DbBriefDto;
import com.ys.idatrix.cloudetl.dto.db.DbConnectionDto;
import com.ys.idatrix.cloudetl.dto.db.DbOption;
import com.ys.idatrix.cloudetl.dto.db.DbSchemaDto;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.utils.EncryptUtil;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.reference.metacube.MetaCubeDatabase;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeDbDatabaseDto;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeDbSchemaDto;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeDbTableFieldDto;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeDbTableViews;
import com.ys.idatrix.cloudetl.service.CloudBaseService;

/**
 * DB connection service implementation. - Retrieve database meta from meta cube
 * - Synchronize database meta into meta store - Provide service for front end
 * clients
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
@Service
public class CloudMetaCubeDbServiceImpl extends CloudBaseService implements CloudMetaCubeDbService {

	@Autowired(required = false)
	private MetaCubeDatabase metaCubeDatabase;
	@Autowired
	private MetaCubeCategory metaCubeCategory;

	/**
	 * Get database meta list.
	 * 
	 * @return
	 * @throws Exception
	 */
/*	private Map<String,List<DatabaseMeta>> getDatabaseMetaList(String owner, Boolean isRead) throws Exception {

		switch (metaCubeCategory) {
		case IDATRIX:
			// Calling MetaCube RPC APIs to get meta data
			
		case PENTAHO:
			// Get meta data from local meta store
			break;
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// TODO. Only for lab, should be removed in production environment!
			// Get meta data from cloud transformation, only for testing!
			break;
		}

		return Maps.newHashMap();
	}*/
	

	/**
	 * Get database meta.
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@Override
	public DatabaseMeta getDatabaseMeta(String owner, Long schemaId) throws Exception {
		DatabaseMeta dbMeta = null;

		switch (metaCubeCategory) {
		case IDATRIX:
			// Calling MetaCube RPC APIs to get meta data
			dbMeta = metaCubeDatabase.getDatabaseMeta(owner,schemaId);
			break;
		case PENTAHO:
			// Get meta data from local meta store
			break;
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// TODO. Only for lab, should be removed in production environment!
			// Get meta data from cloud transformation, only for testing!
		}

		return dbMeta;
	}
	
	
	/**
	 * Get database meta list.
	 * 
	 * @return
	 * @throws Exception
	 */
	private Map<String,List<MetaCubeDbDatabaseDto>> getMetaCubeDBList(String owner, Boolean isRead) throws Exception {

		switch (metaCubeCategory) {
		case IDATRIX:
			// Calling MetaCube RPC APIs to get meta data
			return getUserNameList(owner, new ForeachCallback<String,MetaCubeDbDatabaseDto> (){
				@Override
				public List<MetaCubeDbDatabaseDto> getOne(String user) throws Exception {
					return  metaCubeDatabase.getMetaCubeDbs(user,isRead);
				}
			});
		case PENTAHO:
			// Get meta data from local meta store
			break;
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// TODO. Only for lab, should be removed in production environment!
			// Get meta data from cloud transformation, only for testing!
			break;
		}

		return Maps.newHashMap();
	}

	/**
	 * Get DB connection list.
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String,List<DbBriefDto>> getDbConnectionList(String owner, Boolean isRead) throws Exception {
		Map<String, List<MetaCubeDbDatabaseDto>> databases = getMetaCubeDBList(owner, isRead);;
		if (databases == null) {
			return Maps.newHashMap();
		}
		
		return databases.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey() , 
				entry -> entry.getValue().stream().map(cs -> {
					DbBriefDto db = new DbBriefDto();
					db.setOwner(entry.getKey());
					db.setDatabaseId(cs.getDatabaseId()) ;
					db.setName(cs.getName());
					db.setType(cs.getType());
					db.setIp(cs.getIp());
					db.setPort(cs.getPort());
					return db ; 
					}).collect(Collectors.toList()) ));
	}


	@Override
	public Map<String,PaginationDto<DbBriefDto>> getDbConnectionList(String owner , Boolean isRead ,boolean isMap , Integer page, Integer pageSize, String search) throws Exception {

		 Map<String, List<MetaCubeDbDatabaseDto>> databaseMaps = getMetaCubeDBList(owner,isRead);
		 return getPaginationMaps(isMap, page, pageSize, search, databaseMaps, new DealRowsInterface<DbBriefDto>() {
				@Override
				public DbBriefDto dealRow(Object obj, Object... params) throws Exception {
					String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
					MetaCubeDbDatabaseDto dbMeta = (MetaCubeDbDatabaseDto) obj;

					DbBriefDto db = new DbBriefDto();
					db.setOwner(eleOwner);
					db.setDatabaseId(dbMeta.getDatabaseId()) ;
					db.setName(dbMeta.getName());
					db.setType(dbMeta.getType());
					db.setIp(dbMeta.getIp());
					db.setPort(dbMeta.getPort());
					return db;
				}

				@Override
				public boolean match(Object obj, String search, Object... params) throws Exception {
					//String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
					MetaCubeDbDatabaseDto dbMeta = (MetaCubeDbDatabaseDto) obj;
					return defaultMatch(dbMeta.getName(), search);// dbMeta!=null &&
																	// dbMeta.getName().toLowerCase().contains(search.toLowerCase());
				}
			});
	}




	/**
	 * Do DB connection test, it calls testDbConnection to test the connection and
	 * return test result to front-end.
	 * 
	 * @param connectionName
	 * @return
	 * @throws Exception
	 */
	@Override
	public TestResultDto doDbConnectionTest(String owner , Long schemaId ) throws Exception {
		TestResultDto result = new TestResultDto();
		result.setSchemaId(schemaId);
		DatabaseMeta dbMeta = this.getDatabaseMeta(owner, schemaId);
		
		if (dbMeta == null) {
			result.setStatus(-1);
			result.setMessage("Not Found");
		} else {
			result.setName(dbMeta.getPreferredSchemaName());
			String message = testDbConnection(dbMeta);
			result.setStatus("Normal".equals(message) ? 0 : 1);
			result.setMessage(message);
		}

		return result;
	}
	
	/**
	 * Test DB connection, - The testing will try to connect to the DB to get the
	 * status.
	 * 
	 * @param dbMeta
	 * @return
	 */
	public String testDbConnection(DatabaseMeta dbMeta) {
		String[] remarks = dbMeta.checkParameters();
		if (remarks.length == 0) {

			String reportMessage = dbMeta.testConnection();
			if (reportMessage.startsWith(
					BaseMessages.getString(Database.class, "DatabaseMeta.report.ConnectionOk", dbMeta.getName()))) {
				return "Normal";
			}

			CloudLogger.getInstance().error("testDbConnection", reportMessage);
			return "Error";

		} else {
			CloudLogger.getInstance().error(this , remarks + "");
			return "Parameter Error";
		}
	}


	/**
	 * Get all DB connection available for current login user.
	 * 
	 * @return - list of database meta
	 * @throws Exception
	 */
	@Override
	public List<DatabaseMeta> getAllDbConnection(String owner) throws Exception {
		
		String user = Const.NVL(owner,CloudSession.getLoginUser());
		
		Map<String,List<DatabaseMeta>> metaList =  getUserNameList(owner, new ForeachCallback<String,DatabaseMeta> (){
			@Override
			public List<DatabaseMeta> getOne(String user) throws Exception {
				return  metaCubeDatabase.getDatabaseMetaList(user,null);
			}
		});
		
		if (metaList == null || metaList.isEmpty()) {
			return Lists.newArrayList();
		}
		return metaList.get(user);
	}
	
	/**
	 * Get DB connection by given connection name.
	 * 
	 * @param connectionName
	 * @return
	 * @throws Exception
	 */
	@Override
	public DbConnectionDto getDbConnection(String owner , Long schemaId) throws Exception {
		DatabaseMeta dbMeta = this.getDatabaseMeta(owner,schemaId);
		if (dbMeta == null) {
			return null;
		}

		DbConnectionDto dbConnection = new DbConnectionDto();
		dbConnection.setSchemaId(schemaId);
		dbConnection.setOwner( Const.NVL(owner, CloudSession.getResourceUser()) );
		dbConnection.setAccess(Integer.toString(dbMeta.getAccessType()));
		dbConnection.setDatabaseName(dbMeta.getDatabaseName());
		dbConnection.setHostname(dbMeta.getHostname());
		dbConnection.setName(dbMeta.getName());
		dbConnection.setPassword(EncryptUtil.getInstance().strEnc(dbMeta.getPassword(), dbMeta.getName(),
				dbMeta.getHostname(), dbMeta.getDatabasePortNumberString()));
		dbConnection.setPort(dbMeta.getDatabasePortNumberString());
		dbConnection.setSqlServerInstance(dbMeta.getSQLServerInstance()); // Only
																			// for
																			// MSSQL
		dbConnection.setUseDoubleDecimalSeparator(dbMeta.isUsingDoubleDecimalAsSchemaTableSeparator()); // Only
																										// for
																										// MSSQL
		dbConnection.setType(dbMeta.getPluginId());
		dbConnection.setUsername(dbMeta.getUsername());

		// Only for Generic
		Object v = dbMeta.getAttributes().get(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL);
		if (v instanceof String && !Utils.isEmpty((String) v)) {
			String url = (String) v;
			dbConnection.setUrl(url);
		} else {
			dbConnection.setUrl(dbMeta.getURL());
		}

		v = dbMeta.getAttributes().get(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS);
		if (v != null && v instanceof String) {
			String driver = (String) v;
			dbConnection.setDriver(driver);
			;
		} else {
			dbConnection.setDriver("");
		}

		// Only for MSSQL
		v = dbMeta.getAttributes().get(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY);
		if (v != null && v instanceof String) {
			String useIntegratedSecurity = (String) v;
			dbConnection.setUseIntegratedSecurity(Boolean.parseBoolean(useIntegratedSecurity));
		} else {
			dbConnection.setUseIntegratedSecurity(false);
		}

		// for Oracle
		dbConnection.setDataTableSpace(dbMeta.getDataTablespace());
		dbConnection.setIndexTableSpace(dbMeta.getIndexTablespace());

		// Get extra options
		List<DbOption> options = new ArrayList<>();
		dbMeta.getExtraOptions().entrySet().forEach((opt) -> {
			DbOption option = new DbOption();
			int dotIndex = opt.getKey().indexOf('.');
			String parameter = opt.getKey().substring(dotIndex + 1);
			option.setOptKey(parameter);
			option.setOptVal(opt.getValue());
			options.add(option);
		});
		dbConnection.setOptions(options);

		// Set advance option
		DbAdvanceOption advanceOptin = new DbAdvanceOption();
		advanceOptin.setQuoteAllFields(dbMeta.isQuoteAllFields());
		advanceOptin.setForceIdentifiersToLowercase(dbMeta.isForcingIdentifiersToLowerCase());
		advanceOptin.setForceIdentifiersToUppercase(dbMeta.isForcingIdentifiersToUpperCase());
		advanceOptin.setPreserveReservedWordCcase(dbMeta.preserveReservedCase());
		advanceOptin.setSupportsBooleanDataType(dbMeta.supportsBooleanDataType());
		advanceOptin.setSupportsTimestampDataType(dbMeta.supportsTimestampDataType());
		if (!Utils.isEmpty(dbMeta.getPreferredSchemaName())) {
			advanceOptin.setPreferredSchemaName(dbMeta.getPreferredSchemaName());
		}
		if (!Utils.isEmpty(dbMeta.getConnectSQL())) {
			advanceOptin.setSqlConnect(dbMeta.getConnectSQL());
		}
		dbConnection.setAdvanceOption(advanceOptin);

		// Set pooling
		if (dbMeta.isUsingConnectionPool()) {
			dbConnection.setUsePooling(dbMeta.isUsingConnectionPool());
			dbConnection.setPoolInitialSize(dbMeta.getInitialPoolSize());
			dbConnection.setPoolMaximumSize(dbMeta.getMaximumPoolSize());

			if (dbMeta.getConnectionPoolingProperties() != null && dbMeta.getConnectionPoolingProperties().size() > 0) {
				dbConnection.setPoolOptions(dbMeta.getConnectionPoolingProperties());
			}
		}

		// Set Clustered
		if (dbMeta.isPartitioned() && dbMeta.getPartitioningInformation() != null
				&& dbMeta.getPartitioningInformation().length > 0) {
			dbConnection.setClustered(dbMeta.isPartitioned());
			dbConnection.setPartitionOptions(Arrays.asList(dbMeta.getPartitioningInformation()));
		}

		return dbConnection;
	}

	/**
	 * Get the DB schema, it will be different per DB types.
	 * 
	 * @param connectionName
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<DbSchemaDto> getDbSchema(String owner, Long databaseId , String name, Boolean isRead) throws Exception {
		switch (metaCubeCategory) {
		case IDATRIX:
			// Calling MetaCube RPC APIs to get meta data
			List<MetaCubeDbSchemaDto> schemas = metaCubeDatabase.getMetacubeDbSchemas(owner, databaseId, name , isRead);
			if( schemas != null && !schemas.isEmpty()) {
				return schemas.stream().map(mcSchema -> {
					
					DbSchemaDto dSchema = new DbSchemaDto();
					dSchema.setOwner(owner);
					dSchema.setServerName(mcSchema.getServiceName());
					dSchema.setSchema(mcSchema.getSchemaName());
					dSchema.setSchemaId(mcSchema.getSchemaId());
					dSchema.setConnection(mcSchema.getName());
					return dSchema ;
				}).collect(Collectors.toList()) ;
			}
			return Lists.newArrayList();
		case PENTAHO:
			// Get meta data from local meta store
			break;
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// Do nothing!
		}
		return null ;
	}



	/**
	 * Get DB tables in given DB connection and schema.
	 * 
	 * @param connectionName
	 * @param schemaName
	 *            - if not given, uses default schema
	 * @return
	 * @throws Exception
	 */
	@Override
	public MetaCubeDbTableViews getDbTables(String owner, Long schemaId, Boolean isRead) throws Exception {
		switch (metaCubeCategory) {
		case IDATRIX:
			// Calling MetaCube RPC APIs to get meta data
			return  metaCubeDatabase.getMetaCubeDbTables(owner, schemaId, isRead);
		case PENTAHO:
			// Get meta data from local meta store
			break;
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// Do nothing!
		}
		return null;
	}

	/**
	 * Get DB table fields in given DB connection and table.
	 * 
	 * @param connectionName
	 * @param schemaName
	 *            - if not given, uses default schema
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<MetaCubeDbTableFieldDto> getDbTableFields(String owner,  Long tableId) throws Exception {
		switch (metaCubeCategory) {
		case IDATRIX:
			// Calling MetaCube RPC APIs to get meta data
			return metaCubeDatabase.getMetaCubeDbFields(owner, tableId) ;
		case PENTAHO:
			// Get meta data from local meta store
			break;
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// Do nothing!
		}

		return null ;
	}

	public String[] getDbTablePrimaryKey(String owner ,Long schemaId, String table) throws Exception{
		DatabaseMeta dbMeta = this.getDatabaseMeta(owner,schemaId);
		if (dbMeta != null) {
			Database db = new Database(new SimpleLoggingObject("CloudDbConnection", LoggingObjectType.DATABASE, null ), dbMeta);
			db.connect();
			String[] res = db.getPrimaryKeyColumnNames(dbMeta.getPreferredSchemaName(), table);
			db.disconnect();
			return res;
		}

		return new String[0];
	}

	public  String[] getProc(String owner , Long schemaId) throws Exception {
		String[] procs = null;
		DatabaseMeta dbInfo = this.getDatabaseMeta(owner,schemaId);
		if (dbInfo != null) {
			Database db = new Database(new SimpleLoggingObject("CloudDbConnection", LoggingObjectType.DATABASE, null ), dbInfo);
			try {
				db.connect();
				procs = db.getProcedures();
			} finally {
				db.disconnect();
			}
		}
		return null == procs ? new String[] {} : procs;
	}
	
	
}

/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.DBCache;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.GenericDatabaseMeta;
import org.pentaho.di.core.database.MSSQLServerNativeDatabaseMeta;
import org.pentaho.di.core.database.PartitionDatabaseMeta;
import org.pentaho.di.core.database.util.DatabaseUtil;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.plugins.DatabasePluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.deploy.MetaCubeCategory;
import com.ys.idatrix.cloudetl.deploy.MetaStoreCategory;
import com.ys.idatrix.cloudetl.dto.common.CheckResultDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto;
import com.ys.idatrix.cloudetl.dto.common.PaginationDto.DealRowsInterface;
import com.ys.idatrix.cloudetl.dto.common.ReturnCodeDto;
import com.ys.idatrix.cloudetl.dto.common.TestResultDto;
import com.ys.idatrix.cloudetl.dto.db.DbAdvanceOption;
import com.ys.idatrix.cloudetl.dto.db.DbBriefDto;
import com.ys.idatrix.cloudetl.dto.db.DbConnectionDto;
import com.ys.idatrix.cloudetl.dto.db.DbOption;
import com.ys.idatrix.cloudetl.dto.db.DbTableFieldDto;
import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.utils.EncryptUtil;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.repository.xml.CloudFileRepository;
import com.ys.idatrix.cloudetl.repository.xml.CloudTransformation;
import com.ys.idatrix.cloudetl.repository.xml.metastore.CloudDatabaseMetaStore;
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
public class CloudLocalDbServiceImpl extends CloudBaseService implements CloudLocalDbService {

	@Autowired
	private CloudDatabaseMetaStore cloudMetaStore;

	@Autowired
	private MetaStoreCategory metaStoreCategory;
	@Autowired
	private MetaCubeCategory metaCubeCategory;

	/**
	 * Get database meta list.
	 * 
	 * @return
	 * @throws Exception
	 */
	private Map<String,List<DatabaseMeta>> getDatabaseMetaList(String owner) throws Exception {

		switch (metaCubeCategory) {
		case IDATRIX:
			break;
		case PENTAHO:
			// Get meta data from local meta store
			return getMetaStoreList(owner, new ForeachCallback<IMetaStore,DatabaseMeta> (){
				@Override
				public List<DatabaseMeta> getOne(IMetaStore source) throws Exception {
					return  cloudMetaStore.getElements(source);
				}
			});
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// TODO. Only for lab, should be removed in production environment!
			// Get meta data from cloud transformation, only for testing!
			TransMeta transMeta = CloudTransformation.getInstance().getTransformation();
			Map<String,List<DatabaseMeta>> result = Maps.newHashMap() ;
			result.put(CloudSession.getResourceUser(), transMeta.getDatabases());
			syncDatabaseMeta(CloudSession.getResourceUser() , transMeta.getDatabases());
			return result ;
		}

		return Maps.newHashMap();
	}

	/**
	 * Get database meta.
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public DatabaseMeta getDatabaseMeta(String owner, String name ) throws Exception {
		DatabaseMeta dbMeta = null;

		switch (metaCubeCategory) {
		case IDATRIX:
			break;
		case PENTAHO:
			// Get meta data from local meta store
			dbMeta = cloudMetaStore.getElement(CloudApp.getInstance().getMetaStore(owner), name);
			break;
		case TENANT:
			// Get meta data from tenant third-part system
			// TODO.
			break;
		case DEFAULT:
			// TODO. Only for lab, should be removed in production environment!
			// Get meta data from cloud transformation, only for testing!
			TransMeta transMeta = null;
			transMeta = CloudTransformation.getInstance().getTransformation();

			if (transMeta != null) {
				dbMeta = transMeta.findDatabase(name);
			}
		}

		return dbMeta;
	}

	/**
	 * Save database meta into meta store.
	 * 
	 * @param dbMeta
	 * @param update
	 * @throws Exception
	 * @throws MetaStoreException
	 */
	private void saveDatabaseMetaIntoStore(String owner, DatabaseMeta dbMeta, boolean update)
			throws Exception, MetaStoreException {
		switch (metaStoreCategory) {
		case LOCAL:
			if (update) {
				// Update database meta into meta store
				cloudMetaStore.updateElement(CloudApp.getInstance().getMetaStore(owner), dbMeta);
			} else {
				// Create database meta into meta store
				cloudMetaStore.createElement(CloudApp.getInstance().getMetaStore(owner), dbMeta);
			}
			break;
		case CACHE:
			// TODO.
			break;
		case DATABASE:
			// TODO.
			break;
		case DEFAULT:
			TransMeta transMeta = CloudTransformation.getInstance().getTransformation();
			if (update) {
				transMeta.addOrReplaceDatabase(dbMeta);
			} else {
				transMeta.addDatabase(dbMeta);
			}
			 CloudFileRepository.getInstance().saveTrans(transMeta);
		}
	}

	/**
	 * Delete database meta from meta store.
	 * 
	 * @param dbMeta
	 * @throws Exception
	 */
	private void deleteDatabaseMetaFromStore(String owner, DatabaseMeta dbMeta) throws Exception {
		switch (metaStoreCategory) {
		case LOCAL:
			cloudMetaStore.deleteElement(CloudApp.getInstance().getMetaStore(owner), dbMeta);
			break;
		case CACHE:
			// TODO.
			break;
		case DATABASE:
			// TODO.
			break;
		case DEFAULT:
			TransMeta transMeta = CloudTransformation.getInstance().getTransformation();
			String[] dbConnNames = transMeta.getDatabaseNames();
			if (dbConnNames != null) {
				for (int i = 0; i < dbConnNames.length; i++) {
					if (dbConnNames[i].equals(dbMeta.getName())) {
						transMeta.removeDatabase(i);
					}
				}
			}
			 CloudFileRepository.getInstance().saveTrans(transMeta);
		}
	}

	/**
	 * Synchronize DB connection meat data into store.
	 * 
	 * @param metaList
	 * @throws Exception 
	 */
	private void syncDatabaseMeta(String owner ,List<DatabaseMeta> metaList) throws Exception {
		switch (metaStoreCategory) {
		case LOCAL:
			for (DatabaseMeta meta : metaList) {
				// Synchronize meta data to meta store.
				cloudMetaStore.updateElement(CloudApp.getInstance().getMetaStore(owner), meta);
			}
			break;
		case CACHE:
			// TODO.
			break;
		case DATABASE:
			// TODO.
			break;
		case DEFAULT:
			// Do nothing!
		}
	}


	/**
	 * Get DB connection list.
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String,List<DbBriefDto>> getDbConnectionList(String owner, boolean isTest ) throws Exception {
		Map<String, List<DatabaseMeta>> databases = getDatabaseMetaList(owner);
		if (databases == null) {
			return Maps.newHashMap();
		}
		
		return databases.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey() , 
				entry -> entry.getValue().stream().map(cs -> {
					DbBriefDto db = new DbBriefDto();
					db.setOwner(entry.getKey());
					db.setName(cs.getName());
					db.setType(cs.getPluginId());
					return db ; 
					}).collect(Collectors.toList()) ));
	}


	@Override
	public Map<String,PaginationDto<DbBriefDto>> getDbConnectionList(String owner , boolean isMap , Integer page, Integer pageSize, String search) throws Exception {

		 Map<String, List<DatabaseMeta>> databaseMaps = getDatabaseMetaList(owner);
		 return getPaginationMaps(isMap, page, pageSize, search, databaseMaps, new DealRowsInterface<DbBriefDto>() {
				@Override
				public DbBriefDto dealRow(Object obj, Object... params) throws Exception {
					String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
					DatabaseMeta dbMeta = (DatabaseMeta) obj;

					DbBriefDto db = new DbBriefDto();
					db.setOwner(eleOwner);
					db.setName(dbMeta.getName());
					db.setType(dbMeta.getPluginId());
					return db;
				}

				@Override
				public boolean match(Object obj, String search, Object... params) throws Exception {
					//String eleOwner = params != null && params.length > 0 ? (String) params[0] : CloudSession.getResourceUser();
					DatabaseMeta dbMeta = (DatabaseMeta) obj;
					return defaultMatch(dbMeta.getName(), search);// dbMeta!=null &&
																	// dbMeta.getName().toLowerCase().contains(search.toLowerCase());
				}
			});
	}

	/**
	 * Test DB connection, - The testing will try to connect to the DB to get the
	 * status.
	 * 
	 * @param dbMeta
	 * @return
	 */
	@Override
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
	 * Check if DB connection has been changed. It's important for tasks that are
	 * under executing to make out whether re-loading the connection.
	 * 
	 * @param connectionName
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean isDbConnectionChanged(String owner ,String connectionName) throws Exception {
		DatabaseMeta dbMeta = this.getDatabaseMeta(owner , connectionName);
		if (dbMeta != null && dbMeta.hasChanged()) {
			return true;
		}
		return false;
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
	public TestResultDto doDbConnectionTest(String owner ,String connectionName) throws Exception {
		TestResultDto result = new TestResultDto();
		result.setName(connectionName);

		DatabaseMeta dbMeta = this.getDatabaseMeta(owner, connectionName);
		if (dbMeta == null) {
			result.setStatus(-1);
			result.setMessage("Not Found");
		} else {
			String message = testDbConnection(dbMeta);
			result.setStatus("Normal".equals(message) ? 0 : 1);
			result.setMessage(message);
		}

		return result;
	}

	/**
	 * Check if DB connection name is existing. DB connection name must be unique in
	 * application, so that all front-end users can be address their connection
	 * correctly in any client side.
	 * 
	 * @param connectionName
	 * @return
	 * @throws Exception
	 */
	@Override
	public CheckResultDto checkDbConnectionName(String owner, String connectionName) throws Exception {
		DatabaseMeta dbMeta = this.getDatabaseMeta(owner, connectionName);
		if (dbMeta == null) {
			return new CheckResultDto(connectionName, false);
		}
		return new CheckResultDto(connectionName, true);
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
		Map<String,List<DatabaseMeta>> metaList = getDatabaseMetaList(user);
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
	public DbConnectionDto getDbConnection(String owner, String connectionName) throws Exception {
		DatabaseMeta dbMeta = this.getDatabaseMeta(owner,connectionName);
		if (dbMeta == null) {
			return null;
		}

		DbConnectionDto dbConnection = new DbConnectionDto();
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
	 * Add a DB connection into MetaStore in application repository.
	 * 
	 * @param dbConnection
	 * @return
	 * @throws MetaStoreException
	 * @throws Exception
	 */
	@Override
	public ReturnCodeDto addDbConnection(DbConnectionDto dbConnection) throws Exception {
		DatabaseMeta dbMeta = new DatabaseMeta();

		dbMeta.setName(dbConnection.getName());
		dbMeta.setDisplayName(dbMeta.getName());
		dbMeta.setDatabaseType(dbConnection.getType());
		dbMeta.setAccessType(Integer.parseInt(dbConnection.getAccess()));
		dbMeta.setDBPort(dbConnection.getPort());

		dbMeta.setHostname(dbConnection.getHostname());
		dbMeta.setDBName(dbConnection.getDatabaseName());
		dbMeta.setUsername(dbConnection.getUsername());
		dbMeta.setPassword(EncryptUtil.getInstance().strDec(dbConnection.getPassword(), dbConnection.getName(),
				dbConnection.getHostname(), dbConnection.getPort()));

		// Generic attributes
		dbMeta.getAttributes().put(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, Const.NVL(dbConnection.getUrl(), ""));
		dbMeta.getAttributes().put(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS,
				Const.NVL(dbConnection.getDriver(), ""));

		// MS SQL Server
		dbMeta.setSQLServerInstance(dbConnection.getSqlServerInstance());
		dbMeta.setUsingDoubleDecimalAsSchemaTableSeparator(dbConnection.isUseDoubleDecimalSeparator());

		// Microsoft SQL Server Use Integrated Security
		boolean flag = dbConnection.getUseIntegratedSecurity();
		if (flag) {
			dbMeta.getAttributes().put(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY, flag);
		}

		// Oracle
		dbMeta.setDataTablespace(dbConnection.getDataTableSpace());
		dbMeta.setIndexTablespace(dbConnection.getIndexTableSpace());

		// Set extra options
		List<DbOption> options = dbConnection.getOptions();
		if (options != null) {
			for (DbOption option : options) {
				dbMeta.addExtraOption(dbConnection.getType(), option.getOptKey(), option.getOptVal());
			}
		}

		// Set advance option
		DbAdvanceOption advanceOptin = dbConnection.getAdvanceOption();
		if (advanceOptin != null) {
			dbMeta.setQuoteAllFields(advanceOptin.isQuoteAllFields());
			dbMeta.setForcingIdentifiersToLowerCase(advanceOptin.isForceIdentifiersToLowercase());
			dbMeta.setForcingIdentifiersToUpperCase(advanceOptin.isForceIdentifiersToUppercase());
			dbMeta.setPreserveReservedCase(advanceOptin.isPreserveReservedWordCcase());
			dbMeta.setSupportsBooleanDataType(advanceOptin.isSupportsBooleanDataType());
			dbMeta.setSupportsTimestampDataType(advanceOptin.isSupportsTimestampDataType());
			if (!Utils.isEmpty(advanceOptin.getPreferredSchemaName())) {
				dbMeta.setPreferredSchemaName(advanceOptin.getPreferredSchemaName());
			}
			if (!Utils.isEmpty(advanceOptin.getSqlConnect())) {
				dbMeta.setConnectSQL(advanceOptin.getSqlConnect());
			}

		}

		// Set pooling
		if (dbConnection.isUsePooling()) {
			dbMeta.setUsingConnectionPool(dbConnection.isUsePooling());
			dbMeta.setInitialPoolSize(dbConnection.getPoolInitialSize());
			dbMeta.setMaximumPoolSize(dbConnection.getPoolMaximumSize());

			if (dbConnection.getPoolOptions() != null && dbConnection.getPoolOptions().size() > 0) {
				Properties properties = new Properties();
				for (Entry<Object, Object> po : dbConnection.getPoolOptions().entrySet()) {
					properties.put(po.getKey(), po.getValue());
				}
				dbMeta.setConnectionPoolingProperties(properties);
			}
		}

		// Set Clustered
		if (dbConnection.isClustered() && dbConnection.getPartitionOptions() != null
				&& dbConnection.getPartitionOptions().size() > 0) {
			dbMeta.setPartitioned(dbConnection.isClustered());
			dbMeta.setPartitioningInformation(
					dbConnection.getPartitionOptions().toArray(new PartitionDatabaseMeta[] {}));
		}
		
		//额外的属性适配
		if("ORACLE".equalsIgnoreCase(dbMeta.getPluginId())  ) {
			 Boolean oracleAllField = IdatrixPropertyUtil.getBooleanProperty("idatrix.database.oracle.quote.all.field",true);
			 if(oracleAllField) {
				 dbMeta.setQuoteAllFields(true);
			 }
		} 
		if( "DM7".equalsIgnoreCase(dbMeta.getPluginId())){
			  Boolean dm7AllField = IdatrixPropertyUtil.getBooleanProperty("idatrix.database.dm7.quote.all.field",true);
			  if(dm7AllField) {
				  dbMeta.setQuoteAllFields(true);
			  }
		}

		// Save database meta into meta store
		saveDatabaseMetaIntoStore(dbConnection.getOwner() , dbMeta, false);
		return new ReturnCodeDto(0, "Succeeded");
	}

	/**
	 * Update a DB connection into MetaStore in application repository.
	 * 
	 * @param dbConnection
	 * @return
	 * @throws MetaStoreException
	 * @throws Exception
	 */
	@Override
	public ReturnCodeDto updateDbConnection(DbConnectionDto dbConnection) throws Exception {
		DatabaseMeta dbMeta = this.getDatabaseMeta(dbConnection.getOwner() , dbConnection.getName());
		if (dbMeta == null) {
			return addDbConnection(dbConnection);
		}

		dbMeta.setName(dbConnection.getName());
		dbMeta.setDisplayName(dbMeta.getName());
		dbMeta.setDatabaseType(dbConnection.getType());
		dbMeta.setAccessType(Integer.parseInt(dbConnection.getAccess()));
		dbMeta.setDBPort(dbConnection.getPort());

		dbMeta.setHostname(dbConnection.getHostname());
		dbMeta.setDBName(dbConnection.getDatabaseName());
		dbMeta.setUsername(dbConnection.getUsername());
		dbMeta.setPassword(EncryptUtil.getInstance().strDec(dbConnection.getPassword(), dbConnection.getName(), dbConnection.getHostname(), dbConnection.getPort()));

		// Generic attributes
		dbMeta.getAttributes().put(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, Const.NVL(dbConnection.getUrl(), ""));
		dbMeta.getAttributes().put(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS,
				Const.NVL(dbConnection.getDriver(), ""));

		// MS SQL Server
		dbMeta.setSQLServerInstance(dbConnection.getSqlServerInstance());
		dbMeta.setUsingDoubleDecimalAsSchemaTableSeparator(dbConnection.isUseDoubleDecimalSeparator());

		// Microsoft SQL Server Use Integrated Security
		boolean flag = dbConnection.getUseIntegratedSecurity();
		if (flag) {
			dbMeta.getAttributes().put(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY, flag);
		}

		// Oracle
		dbMeta.setDataTablespace(dbConnection.getDataTableSpace());
		dbMeta.setIndexTablespace(dbConnection.getIndexTableSpace());

		// Set extra options
		List<DbOption> options = dbConnection.getOptions();
		if (options != null) {
			for (DbOption option : options) {
				dbMeta.addExtraOption(dbConnection.getType(), option.getOptKey(), option.getOptVal());
				// dbMeta.getExtraOptions().put(option.getOptKey(),
				// option.getOptVal());
			}
		}
		// Set advance option
		DbAdvanceOption advanceOptin = dbConnection.getAdvanceOption();
		if (advanceOptin != null) {
			dbMeta.setQuoteAllFields(advanceOptin.isQuoteAllFields());
			dbMeta.setForcingIdentifiersToLowerCase(advanceOptin.isForceIdentifiersToLowercase());
			dbMeta.setForcingIdentifiersToUpperCase(advanceOptin.isForceIdentifiersToUppercase());
			dbMeta.setPreserveReservedCase(advanceOptin.isPreserveReservedWordCcase());
			dbMeta.setSupportsBooleanDataType(advanceOptin.isSupportsBooleanDataType());
			dbMeta.setSupportsTimestampDataType(advanceOptin.isSupportsTimestampDataType());
			if (!Utils.isEmpty(advanceOptin.getPreferredSchemaName())) {
				dbMeta.setPreferredSchemaName(advanceOptin.getPreferredSchemaName());
			}
			if (!Utils.isEmpty(advanceOptin.getSqlConnect())) {
				dbMeta.setConnectSQL(advanceOptin.getSqlConnect());
			}

		}else {
			//额外的属性适配
			if("ORACLE".equalsIgnoreCase(dbMeta.getPluginId())  ) {
				 Boolean oracleAllField = IdatrixPropertyUtil.getBooleanProperty("idatrix.database.oracle.quote.all.field",true);
				 if(oracleAllField) {
					 dbMeta.setQuoteAllFields(true);
				 }
			} 
			if( "DM7".equalsIgnoreCase(dbMeta.getPluginId())){
				  Boolean dm7AllField = IdatrixPropertyUtil.getBooleanProperty("idatrix.database.dm7.quote.all.field",true);
				  if(dm7AllField) {
					  dbMeta.setQuoteAllFields(true);
				  }
			}
		}

		// Set pooling
		dbMeta.setUsingConnectionPool(dbConnection.isUsePooling());
		dbMeta.setInitialPoolSize(dbConnection.getPoolInitialSize());
		dbMeta.setMaximumPoolSize(dbConnection.getPoolMaximumSize());
		if (dbConnection.getPoolOptions() != null && dbConnection.getPoolOptions().size() > 0) {
			Properties properties = new Properties();
			for (Entry<Object, Object> po : dbConnection.getPoolOptions().entrySet()) {
				properties.put(po.getKey(), po.getValue());
			}
			dbMeta.setConnectionPoolingProperties(properties);
		} else {
			dbMeta.setConnectionPoolingProperties(new Properties());
		}

		// Set Clustered
		dbMeta.setPartitioned(dbConnection.isClustered());
		if (dbConnection.getPartitionOptions() != null && dbConnection.getPartitionOptions().size() > 0) {
			dbMeta.setPartitioningInformation(
					dbConnection.getPartitionOptions().toArray(new PartitionDatabaseMeta[] {}));
		}else {
			dbMeta.setPartitioningInformation(new PartitionDatabaseMeta[] {});
		}
		// Save database meta into meta store
		saveDatabaseMetaIntoStore(dbConnection.getOwner() , dbMeta, true);
		return new ReturnCodeDto(0, "Succeeded");
	}

	/**
	 * Delete a DB connection from MetaStore in application repository.
	 * 
	 * @param connectionName
	 * @return - connection name deleted
	 * @throws Exception
	 */
	@Override
	public ReturnCodeDto deleteDbConnection(String owner, String connectionName) throws Exception {
		DatabaseMeta meta = getDatabaseMeta(owner,connectionName);
		if (meta != null) {
			deleteDatabaseMetaFromStore(owner,meta);

			return new ReturnCodeDto(0, "Succeeded");
		}
		return new ReturnCodeDto(1, "Not existed");
	}

	/**
	 * Get the DB schema, it will be different per DB types.
	 * 
	 * @param connectionName
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<String> getDbSchema(String owner, String connectionName ) throws Exception {
		switch (metaCubeCategory) {
		case IDATRIX:
			// Calling MetaCube RPC APIs to get meta data
			break ;
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

		List<String> schemas = new ArrayList<>();
		DatabaseMeta dbMeta = this.getDatabaseMeta(owner,connectionName);
		if (dbMeta != null) {
			String schemaName = dbMeta.getPreferredSchemaName();
			if( Utils.isEmpty(schemaName) ) {
				if("MYSQL".equalsIgnoreCase(dbMeta.getPluginId())  || "DM7".equalsIgnoreCase(dbMeta.getPluginId())){
					schemaName = dbMeta.getDatabaseName() ;
				}else if("ORACLE".equalsIgnoreCase( dbMeta.getPluginId() )) {
					schemaName =  dbMeta.getUsername() ;
				}else if ( "POSTGRESQL".equalsIgnoreCase( dbMeta.getPluginId() )) {
					schemaName = "public" ;
				}	
			}
			schemas.add(schemaName);

			Database db = new Database(loggingObject, dbMeta);
			db.connect();
			String[] ss = db.getSchemas();
			if (ss != null && ss.length > 0) {
				schemas = Arrays.asList(ss);
			} 
			
			db.disconnect();
		} 
		return schemas;
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
	public List<String> getDbTables(String owner, String connectionName, String schemaName) throws Exception {
		switch (metaCubeCategory) {
		case IDATRIX:
			// Calling MetaCube RPC APIs to get meta data
			break;
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

		List<String> jdTables = new ArrayList<>();

		DatabaseMeta dbMeta = this.getDatabaseMeta(owner,connectionName);
		if (dbMeta != null) {
			if(!Utils.isEmpty(schemaName)) {
				dbMeta.setPreferredSchemaName(schemaName);
			}
			Database db = new Database(loggingObject, dbMeta);
			Map<String, Collection<String>> tableMap;
			db.connect();

			if (StringUtils.isEmpty(schemaName)) {
				tableMap = db.getTableMap();
			} else {
				tableMap = db.getTableMap(schemaName);
			}

			if (tableMap != null) {
				List<String> jdtList = new ArrayList<>();
				for (String schema : tableMap.keySet()) {
					// if (!schema.equals(schemaName)) continue;

					List<String> tables = new ArrayList<String>(tableMap.get(schema));
					Collections.sort(tables);
					for (String tableName : tables) {
						if (StringUtils.isEmpty(schemaName) && !StringUtils.isEmpty(schema)) {
							jdtList.add(schema + "." + tableName);
						} else {
							jdtList.add(tableName);
						}
					}
				}
				jdTables = jdtList ;
			}
			
			db.disconnect();
		} 
		return jdTables;
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
	public List<DbTableFieldDto>  getDbTableFields(String owner, String connectionName, String schemaName, String tableName,
			boolean isQuote) throws Exception {
		switch (metaCubeCategory) {
		case IDATRIX:
			// Calling MetaCube RPC APIs to get meta data
			break;
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
		
		
		List<DbTableFieldDto> jdtfList = new ArrayList<>();

		DatabaseMeta dbMeta = this.getDatabaseMeta(owner,connectionName);
		if (dbMeta != null) {
			if(!Utils.isEmpty(schemaName)) {
				dbMeta.setPreferredSchemaName(schemaName);
			}
			Database db = new Database(loggingObject, dbMeta);
			RowMetaInterface fields;

			db.connect();
			DBCache.getInstance().setActive(false);// 不使用缓存
			String schemaTable = dbMeta.getQuotedSchemaTableCombination(schemaName, tableName);
//			if ("POSTGRESQL".equals(dbMeta.getPluginId()) || "HIVE3".equals(dbMeta.getPluginId())) {
//				schemaTable = tableName;
//			}
			fields = db.getTableFields(schemaTable);

			
			if (fields != null) {
				for (int i = 0; i < fields.size(); i++) {
					ValueMetaInterface field = fields.getValueMeta(i);

					DbTableFieldDto jdtField = new DbTableFieldDto();
					jdtField.setType(field.getType());
					if (isQuote) {
						jdtField.setName(dbMeta.quoteField(field.getName()));
					} else {
						jdtField.setName(field.getName());
					}

					jdtfList.add(jdtField);
				}
			}
			db.disconnect();
		}

		return jdtfList;
	}


	@Override
	public String[] getDbTablePrimaryKey(String owner, String connection, String schema, String table) throws Exception {

		DatabaseMeta dbMeta = this.getDatabaseMeta(owner,connection);
		if (dbMeta != null) {
			if(!Utils.isEmpty(schema)) {
				dbMeta.setPreferredSchemaName(schema);
			}
			Database db = new Database(new SimpleLoggingObject("CloudDbConnection", LoggingObjectType.DATABASE, null ), dbMeta);
			db.connect();
			String[] res = db.getPrimaryKeyColumnNames(schema, table);
			db.disconnect();
			return res;
		}

		return new String[0];
	}

	/**
	 * 请求方法 - 查询数据库连接中的存储过程
	 * 
	 * @param transName
	 * @param connection
	 * @return
	 * @throws Exception
	 */
	@Override
	public String[] getProc(String owner, String connection) throws Exception {

		String[] procs = null;
		DatabaseMeta dbInfo = this.getDatabaseMeta(owner,connection);
		if (dbInfo != null) {
			Database db = new Database(loggingObject, dbInfo);
			try {
				db.connect();
				procs = db.getProcedures();
			} finally {
				db.disconnect();
			}
		}
		return null == procs ? new String[] {} : procs;
	}

	private String JNDI_FILE_NAME = "jdbc.properties";

	@SuppressWarnings("rawtypes")
	@Override
	public ReturnCodeDto createjndi(DbConnectionDto dbConnection) throws Exception {

		String type = dbConnection.getType();
		String name = dbConnection.getName();
		String url = dbConnection.getUrl();
		String username = dbConnection.getUsername();
		String password = EncryptUtil.getInstance().strDec(dbConnection.getPassword(), name, type, username);
		String classDriver = dbConnection.getDriver();
		if (Utils.isEmpty(url) || Utils.isEmpty(type) || Utils.isEmpty(name)) {
			throw new Exception("名字,url和类型 都不能为空.");
		}
		if (Utils.isEmpty(classDriver)) {
			PluginRegistry registry = PluginRegistry.getInstance();
			PluginInterface plugin = registry.findPluginWithId(DatabasePluginType.class, type);
			DatabaseInterface databaseInterface = (DatabaseInterface) registry.loadClass(plugin);
			classDriver = databaseInterface.getDriverClass();
		}

		if (checkJndiName(dbConnection.getOwner() , type, name).getResult()) {
			// 更新jndi数据源,清空DatabaseUtil中的缓存
			Map FoundDS = (Map) OsgiBundleUtils.getOsgiField(DatabaseUtil.class, "FoundDS", true);
			FoundDS.remove(type + "/jdbc/" + name);
		}

		String rootPath = CloudApp.getInstance().getRepositoryRootFolder() + CloudApp.getInstance().getUserJndiRepositoryPath(dbConnection.getOwner()) + type + CloudApp.SEPARATOR;
		if (!FilePathUtil.fileIsExist(rootPath, true)) {
			FilePathUtil.createFileIfNotExist(rootPath, true);
		}
		String filePath = rootPath + JNDI_FILE_NAME;
		if (!FilePathUtil.fileIsExist(filePath, false)) {
			FilePathUtil.createFileIfNotExist(filePath, false);
		}

		PropertiesConfiguration pps = getJndiProperties(filePath);

		pps.clearProperty(name + "/type");
		pps.addProperty(name + "/type", "javax.sql.DataSource");
		pps.clearProperty(name + "/driver");
		pps.addProperty(name + "/driver", classDriver);
		pps.clearProperty(name + "/url");
		pps.addProperty(name + "/url", url);
		pps.clearProperty(name + "/user");
		pps.addProperty(name + "/user", username);
		pps.clearProperty(name + "/password");
		pps.addProperty(name + "/password", password);

		saveJndiProperties(pps, filePath);
		return new ReturnCodeDto(0, "Success");
	}

	@Override
	public CheckResultDto checkJndiName(String owner, String type, String jndiName) throws Exception {
		DbConnectionDto jndi = getjndiByName(owner, type, jndiName);
		if (jndi == null) {
			return new CheckResultDto(jndiName, false);
		}
		return new CheckResultDto(jndiName, true);
	}

	@Override
	public ReturnCodeDto deletejndi(String owner, String type, String name) throws Exception {

		String rootPath = CloudApp.getInstance().getRepositoryRootFolder() + CloudApp.getInstance().getUserJndiRepositoryPath( owner ) + type + CloudApp.SEPARATOR;
		if (!FilePathUtil.fileIsExist(rootPath, true)) {
			return new ReturnCodeDto(1, "not found");
		}
		String filePath = rootPath + JNDI_FILE_NAME;
		if (!FilePathUtil.fileIsExist(filePath, false)) {
			return new ReturnCodeDto(1, "not found");
		}

		PropertiesConfiguration pps = getJndiProperties(filePath);

		pps.clearProperty(name + "/type");
		pps.clearProperty(name + "/driver");
		pps.clearProperty(name + "/url");
		pps.clearProperty(name + "/user");
		pps.clearProperty(name + "/password");

		saveJndiProperties(pps, filePath);

		return new ReturnCodeDto(0, "Success");
	}

	@Override
	public DbConnectionDto getjndiByName(String owner, String type, String name) throws Exception {

		String rootPath = CloudApp.getInstance().getRepositoryRootFolder() + CloudApp.getInstance().getUserJndiRepositoryPath( owner ) + type + CloudApp.SEPARATOR;
		if (!FilePathUtil.fileIsExist(rootPath, true)) {
			return null;
		}
		String filePath = rootPath + JNDI_FILE_NAME;
		if (!FilePathUtil.fileIsExist(filePath, false)) {
			return null;
		}

		PropertiesConfiguration pps = getJndiProperties(filePath);

		if (pps.containsKey(name + "/type")) {
			DbConnectionDto dbConnectionDto = new DbConnectionDto();
			dbConnectionDto.setType(type);
			dbConnectionDto.setName(name);
			dbConnectionDto.setDriver(pps.getString(name + "/driver"));
			dbConnectionDto.setUrl(pps.getString(name + "/url"));
			dbConnectionDto.setUsername(pps.getString(name + "/user"));
			dbConnectionDto.setPassword(EncryptUtil.getInstance().strEnc(pps.getString(name + "/password"), name, type,
					pps.getString(name + "/user")));
			return dbConnectionDto;

		}

		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DbConnectionDto> getjndiList(String owner, String type) throws Exception {

		List<DbConnectionDto> result = Lists.newArrayList();

		String rootPath = CloudApp.getInstance().getRepositoryRootFolder() + CloudApp.getInstance().getUserJndiRepositoryPath( owner ) + type + CloudApp.SEPARATOR;
		if (!FilePathUtil.fileIsExist(rootPath, true)) {
			return result;
		}
		String filePath = rootPath + JNDI_FILE_NAME;
		if (!FilePathUtil.fileIsExist(filePath, false)) {
			return result;
		}

		PropertiesConfiguration pps = getJndiProperties(filePath);

		if (!pps.isEmpty()) {
			List<String> keys = IteratorUtils.toList(pps.getKeys());
			List<String> nameList = keys.stream().map(k -> {
				String key = (String) k;
				int index = key.lastIndexOf('/');
				return key.substring(0, index);
			}).distinct().collect(Collectors.toList());

			for (String name : nameList) {
				DbConnectionDto dbConnectionDto = new DbConnectionDto();
				dbConnectionDto.setType(type);
				dbConnectionDto.setName(name);
				dbConnectionDto.setDriver(pps.getString(name + "/driver"));
				dbConnectionDto.setUrl(pps.getString(name + "/url"));
				dbConnectionDto.setUsername(pps.getString(name + "/user"));
				dbConnectionDto.setPassword(EncryptUtil.getInstance().strEnc(pps.getString(name + "/password"), name,
						type, pps.getString(name + "/user")));

				result.add(dbConnectionDto);
			}
		}

		return result;

	}

	private static Map<String, PropertiesConfiguration> PropertiesCache = Collections
			.synchronizedMap(new HashMap<String, PropertiesConfiguration>());

	private PropertiesConfiguration getJndiProperties(String filePath) throws Exception {

		if (PropertiesCache.containsKey(filePath)) {
			return PropertiesCache.get(filePath);
		} else {
			PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(filePath);
			propertiesConfiguration.setAutoSave(false);
			propertiesConfiguration.getLayout().setGlobalSeparator("=");
			FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
			fileChangedReloadingStrategy.setRefreshDelay(1000L);
			propertiesConfiguration.setReloadingStrategy(fileChangedReloadingStrategy);

			PropertiesCache.put(filePath, propertiesConfiguration);

			return propertiesConfiguration;
		}

	}

	private void saveJndiProperties(PropertiesConfiguration pps, String filePath) throws Exception {
		pps.save();
	}

}

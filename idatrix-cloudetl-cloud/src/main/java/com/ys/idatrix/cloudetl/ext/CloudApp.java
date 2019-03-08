/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.DBCache;
import org.pentaho.di.core.database.ConnectionPoolUtil;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.logging.DefaultLogLevel;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.plugins.DatabasePluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.metastore.MetaStoreConst;
import org.pentaho.di.repository.IUser;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.pentaho.di.repository.filerep.KettleFileRepositoryMeta;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.www.CarteSingleton;
import org.pentaho.di.www.SlaveSequence;
import org.pentaho.di.www.SlaveServerConfig;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.stores.delegate.DelegatingMetaStore;

import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.def.CloudConst;
import com.ys.idatrix.cloudetl.ext.utils.UnixPathUtil;
import com.ys.idatrix.cloudetl.repository.CloudRepository;
import com.ys.idatrix.cloudetl.repository.xml.metastore.vfsxml.VFSXmlMetaStore;

/**
 * Cloud ETL application environment initiation. <br/>
 * - Load settings for kettle log store <br/>
 * - Initiate application variables <br/>
 * - Initiate default repository & meta store <br/>
 * - Trigger metrics reporter <br/>
 * - Trigger resource monitor (TODO.) <br/>
 * 
 * @author JW
 * @since 05-12-2017
 * 
 */
public class CloudApp {

	public static final Log  logger = LogFactory.getLog("CloudApp");
	
	public static final String defaut_userId =".meta"; 
	
	// Resource or log path for user (must be ended with '/')
	public static final String SEPARATOR = "/";
	public static final String CLOUD_REPOSITORY_DIR = "cloud";
	public static final String TRANS_REPOSITORY_DIR = "trans";
	public static final String JOB_REPOSITORY_DIR = "jobs";
	public static final String LOG_REPOSITORY_DIR = "logs";
	public static final String JNDI_REPOSITORY_DIR = "jndi";
	
	private static CloudApp app;
	public  final LoggingObjectInterface loggingObject;
	private DatabaseMeta databaseMeta;
	private HashMap<String, DelegatingMetaStore> metaStores;
	
	private Repository repository;
	private DelegatingMetaStore metaStore;
	
	//###########################构造方法#####################################

	private CloudApp() {
		loggingObject = new SimpleLoggingObject(  CloudConst.APP_NAME , LoggingObjectType.CARTE, null );
		loadSettings();
		initDatabaseMeta();
		metaStores = new HashMap<>();

	}

	private void loadSettings() {
		LogLevel logLevel = LogLevel.getLogLevelForCode(IdatrixPropertyUtil.getProperty("kettle.logger.level", "Basic"));
		logger.info("设置Kettle系统默认日志级别:"+logLevel);
		DefaultLogLevel.setLogLevel(logLevel);
		KettleLogStore.getAppender().setMaxNrLines(Const.MAX_NR_LOG_LINES);

		DBCache.getInstance().setActive(true);
	}
	
	
	private void initDatabaseMeta() {
		if(  !IdatrixPropertyUtil.getBooleanProperty("idatrix.database.enable")) {
			databaseMeta = null;
			return ;
		}
		try {
			databaseMeta = new DatabaseMeta();
	
			String dbType = IdatrixPropertyUtil.getProperty("idatrix.database.type","MYSQL");
			PluginInterface plugin = PluginRegistry.getInstance().getPlugin(DatabasePluginType.class, dbType);
			DatabaseInterface  databaseInterface = (DatabaseInterface) PluginRegistry.getInstance().loadClass(plugin);
			databaseInterface.setPluginId(dbType);
			
			// config database
			String name = IdatrixPropertyUtil.getProperty("idatrix.database.name","defaultCloudETLDatabase");
			String access = IdatrixPropertyUtil.getProperty("idatrix.database.access","Native");
			String hostname = IdatrixPropertyUtil.getProperty("idatrix.database.ip");
			String port = IdatrixPropertyUtil.getProperty("idatrix.database.port","3306");
			String databaseName = IdatrixPropertyUtil.getProperty("idatrix.database.databaseName");
			String schema = IdatrixPropertyUtil.getProperty("idatrix.database.schema");
			String username = IdatrixPropertyUtil.getProperty("idatrix.database.username");
			String password = Encr.decryptPasswordOptionallyEncrypted(IdatrixPropertyUtil.getProperty("idatrix.database.password"));
			
			databaseMeta.setDatabaseInterface(databaseInterface);
			databaseMeta.setObjectId(new StringObjectId(name));
	
			databaseMeta.setName(name);
			databaseMeta.setDatabaseType(dbType);
			databaseMeta.setAccessType(DatabaseMeta.getAccessType(access));
			databaseMeta.setDBName(databaseName);
			if(!Utils.isEmpty(schema)) {
				databaseMeta.setPreferredSchemaName(schema);
			}
			databaseMeta.setHostname(hostname);
			databaseMeta.setDBPort(port);
			databaseMeta.setUsername(username);
			databaseMeta.setPassword(password);
			//databaseMeta.addExtraOption(dbType, "autoReconnect", "true");
			
			boolean usePooling = IdatrixPropertyUtil.getBooleanProperty("idatrix.database.use.pooling",true);
			if(usePooling) {
				databaseMeta.setUsingConnectionPool(true);
				
				Properties properties = new Properties();
				
				properties.setProperty(ConnectionPoolUtil.INITIAL_SIZE,  IdatrixPropertyUtil.getProperty("idatrix.database.pooling.initialSize","1") );
				properties.setProperty(ConnectionPoolUtil.MAX_ACTIVE   ,IdatrixPropertyUtil.getProperty("idatrix.database.pooling.maxActive","50") );
				properties.setProperty(ConnectionPoolUtil.MAX_IDLE   ,IdatrixPropertyUtil.getProperty("idatrix.database.pooling.maxIdle","5") );
				properties.setProperty(ConnectionPoolUtil.MAX_WAIT   ,IdatrixPropertyUtil.getProperty("idatrix.database.pooling.maxWait","120000") );
				properties.setProperty(ConnectionPoolUtil.DEFAULT_AUTO_COMMIT   , IdatrixPropertyUtil.getProperty("idatrix.database.pooling.defaultAutoCommit","true") );
				properties.setProperty(ConnectionPoolUtil.REMOVE_ABANDONED   , IdatrixPropertyUtil.getProperty("idatrix.database.pooling.removeAbandoned","true") );
				properties.setProperty(ConnectionPoolUtil.REMOVE_ABANDONED_TIMEOUT   , IdatrixPropertyUtil.getProperty("idatrix.database.pooling.removeAbandonedTimeout","60") );
				properties.setProperty(ConnectionPoolUtil.LOG_ABANDONED   ,IdatrixPropertyUtil.getProperty("idatrix.database.pooling.logAbandoned","false") );
				properties.setProperty(ConnectionPoolUtil.VALIDATION_QUERY   , IdatrixPropertyUtil.getProperty("idatrix.database.pooling.validationQuery"," select 1 ") );
				properties.setProperty(ConnectionPoolUtil.TEST_ON_BORROW   , IdatrixPropertyUtil.getProperty("idatrix.database.pooling.testOnBorrow","true") );
				databaseMeta.setConnectionPoolingProperties(properties);
			}
			
			if(IdatrixPropertyUtil.getBooleanProperty("idatrix.database.autosequence.enable",true)) {
				//增加获取 slave服务数据库序列 默认配置
				SlaveServerConfig slaveConfig = CarteSingleton.getInstance().getTransformationMap().getSlaveServerConfig();
				if(slaveConfig != null && slaveConfig.getAutoSequence() == null) {
					String tableName = "SEQ_TABLE";
					String sequence_field = "SEQ_NAME";
					String value_field = "SEQ_VALUE";
					
					Database database = getCloudDatabase() ;
					try {
						if (!database.checkTableExists(tableName)) {
							
							RowMetaInterface tableRowMeta = new RowMeta();
							tableRowMeta.addValueMeta( new ValueMetaString( sequence_field , 255, 0) );
							tableRowMeta.addValueMeta( new ValueMetaInteger( value_field , 18, 0) );
							
							String schemaTable = databaseMeta.getQuotedSchemaTableCombination(schema, tableName);
							String sql = database.getDDL(schemaTable, tableRowMeta, sequence_field, false, sequence_field, false);
							if (!Utils.isEmpty(sql)) {
								database.execStatements(sql);
							}
						}
					}finally {
						if(database != null ) {
							database.disconnect();
						}
					}
					
					slaveConfig.setAutomaticCreationAllowed(true);
					slaveConfig.setAutoSequence(new SlaveSequence("CloudAutoSequence", 0L, databaseMeta, schema, tableName, sequence_field, value_field));
					slaveConfig.readAutoSequences();
				}
				
			}
			
		
		} catch (KettleException e) {
			logger.error("初始化 cloud DatabaseMeta 异常:",e);
		}
	}

	public static CloudApp getInstance() {
		if (app == null) {
			synchronized (CloudApp.class) {
				if (app == null) {
					app = new CloudApp();
				}
			}
		}
		return app;
	}
	
	/**
	 * 服务启动完成后 调用进行初始化
	 * @throws Exception
	 */
	public void initDefault( ) throws Exception {
		// Connect meta store in the repository
		createDefaultRepository();
		// Initialize meta store in application
		// No user given to create a default meta store for any users!
		initUserMetaStores("");
	}

	//##############################数据库信息##################################
	
	public Database getCloudDatabase() {
		if(databaseMeta == null) {
			return null;
		}
		Database datebase = new Database(loggingObject, getCloudDatabaseMeta() );
		try {
			datebase.connect();
		} catch (KettleDatabaseException e) {
			logger.error("初始化 conect Database 异常:",e);
			return null;
		}
		return datebase;
	}
	
	public DatabaseMeta getCloudDatabaseMeta() {
		return (DatabaseMeta)databaseMeta.clone();
	}

	
	//##############################仓库信息##################################
	
	public Repository getRepository() {
		if (isDatabaseRepository()) {
			try {
				return initDbRepository();
			} catch (Exception e) {
				logger.error("初始化 数据库仓库 异常:",e);
			}
		}
		return repository;
	}

	

	private Repository initDbRepository() throws KettleException {
		if ( isDatabaseRepository() && databaseMeta != null ) {

			KettleDatabaseRepository dbRepository = ((KettleDatabaseRepository) repository);
			reconnectDatabase(dbRepository);
			return dbRepository;
		}
		return repository;
	}

	private Boolean reconnectDatabase(KettleDatabaseRepository reposi) {

		try {
			if (reposi.isConnected()) {

				IUser user = reposi.getSecurityProvider().getUserInfo();
				if (user != null && StringUtil.IsNumber(user.getDescription())) {
					Long startTime = Long.valueOf(user.getDescription());
					Long nowTime = System.currentTimeMillis();
					// 离上次连接没有超过1小时
					if ((startTime + 1000 * 60 * 60) >= nowTime) {
						// 设置当前连接时间
						reposi.getSecurityProvider().getUserInfo().setDescription(System.currentTimeMillis() + "");
						return true;
					}
				}

				// 连接断开,无法commit 会导致disconnect报错, 报错时,将autocommit设置成功true,再试
				reposi.disconnect();
				reposi.connect("admin", "admin", true);

			} else {
				reposi.connect("admin", "admin");
			}
			// 设置当前连接时间
			reposi.getSecurityProvider().getUserInfo().setDescription(System.currentTimeMillis() + "");
			return true;

		} catch (Exception e) {
			return false;
		}

	}
	
	public boolean isDatabaseRepository() {
		return repository != null && repository instanceof KettleDatabaseRepository;
	}

	// Select current repository
	public void selectRepository(Repository repo) {
		if (repository != null) {
			repository.disconnect();
		}
		repository = repo;
	}

	private void  createDefaultRepository() throws KettlePluginException {
		
		String type = IdatrixPropertyUtil.getProperty("idatrix.metadata.reposity.type","file");
		if("database".equalsIgnoreCase(type) && getCloudDatabaseMeta()!= null) {
			KettleDatabaseRepositoryMeta  meta = new KettleDatabaseRepositoryMeta();
			meta.setDescription("default");
			meta.setName("default");
			meta.setConnection(getCloudDatabaseMeta());
			
			KettleDatabaseRepository rep =  (KettleDatabaseRepository) PluginRegistry.getInstance().loadClass( RepositoryPluginType.class, meta, Repository.class );
			rep.init(meta);
			rep.create();
			
			repository =  rep;
		}else {
			String path = UnixPathUtil.unixPath(IdatrixPropertyUtil.getProperty("idatrix.metadata.reposity.root","/data/ETL/reposity/"));
			KettleFileRepositoryMeta meta = new KettleFileRepositoryMeta();
			meta.setBaseDirectory(path);
			meta.setDescription("default");
			meta.setName("default");
			meta.setReadOnly(false);
			meta.setHidingHiddenFiles(true);
	
			KettleFileRepository rep = new KettleFileRepository();
			rep.init(meta);
			
			System.setProperty(Const.PENTAHO_METASTORE_FOLDER, meta.getBaseDirectory() + File.separator + MetaStoreConst.META_FOLDER);
			
			repository =  rep;
		}
		
	}

	
	/**
	 * Get root folder for current repository.
	 * @return
	 */
	public  String getRepositoryRootFolder() {
		String userFolder = UnixPathUtil.unixPath(IdatrixPropertyUtil.getProperty("idatrix.metadata.reposity.root","/data/ETL/reposity/"));
		if (CloudApp.getInstance().getRepository() instanceof KettleFileRepository) {
			KettleFileRepository fileRepo = (KettleFileRepository) CloudApp.getInstance().getRepository();
			userFolder = fileRepo.getRepositoryMeta().getBaseDirectory();
		}
		return userFolder;
	}
	
	/**
	 * Get root folder for current repository.
	 * @return
	 */
	public  String getLocalRepositoryRootFolder() {
		String userFolder = IdatrixPropertyUtil.getProperty("idatrix.local.reposity.root",getRepositoryRootFolder() );
		int index = userFolder.indexOf("://");
		if(index > -1 ) {
			userFolder = userFolder.substring(index+3);
		}
		return userFolder;
	}
	
	//##############################metastore信息##################################
	

	public  Map<String,DelegatingMetaStore> getMetaStoreMaps( String owner ) throws Exception {
		 Map<String,DelegatingMetaStore> result = Maps.newHashMap();
		if( CloudSession.isRenterPrivilege() ) {
			if(Utils.isEmpty(owner)) {
				for(String user  : CloudRepository.getCurrentRenterUsers(null) ){
					result.put(user , getMetaStore(user));
				}
			}else {
				result.put(owner , getMetaStore(owner));
			}
		}else if( Utils.isEmpty(owner) || owner.equals(CloudSession.getLoginUser())) {
			result.put(CloudSession.getLoginUser(),  getMetaStore(CloudSession.getLoginUser()) );
		}
		return result;
	}
	
	/**
	 * Get meta store for given user.
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public  DelegatingMetaStore getMetaStore(String username) throws Exception {
		
		if(Utils.isEmpty(username)) {
			username = CloudSession.getResourceUser();
		}
		
		if(Utils.isEmpty(username)) {
			return getDefaultMetaStore();
		}

		DelegatingMetaStore dms =  null ;
		if (metaStores.containsKey(username)) {
			dms = metaStores.get(username); // Already had a meta store for the user in application!
		}
		if(dms == null ) {
			dms = initUserMetaStores(username);
		}
		return dms != null ? dms : getDefaultMetaStore();
	}

	/**
	 * Get default meta store (.meta).
	 * @return
	 */
	public  DelegatingMetaStore getDefaultMetaStore() {
		return metaStore;
	}


	private synchronized DelegatingMetaStore initUserMetaStores(String username) throws Exception {
		if (Utils.isEmpty(username) && metaStore != null) {
			return null; // Already had a default meta store for any users!
		}

		DelegatingMetaStore dms;
		Repository repos = repository;
		if (metaStores.containsKey(username)) {
			dms = metaStores.get(username); // Already had a meta store for the user in application!
			if (isDatabaseRepository()) {
				Repository dbrepository = initDbRepository();
				if (dms.getMetaStore(dbrepository.getMetaStore().getName()) != null) {
					dms.setActiveMetaStoreName(dbrepository.getMetaStore().getName());
					return dms;
				}
				repos = dbrepository;
			} else {
				return dms;
			}
		}

		dms = new DelegatingMetaStore();

		try {
			// Load at least one local meta store and add it to the delegating meta store
			IMetaStore localMetaStore = VFSXmlMetaStore.createMetaStore(null,true);
			dms.addMetaStore(localMetaStore);
			dms.setActiveMetaStoreName(localMetaStore.getName());

			// If there is a meta store in repository, let it activated
			if (repos != null) {
				// Connect meta store in the repository
				if (repos instanceof KettleDatabaseRepository) {
					if (!repos.isConnected()) {
						// repos.connect("admin", "admin"); // With login as admin!
						reconnectDatabase((KettleDatabaseRepository) repos);
					}
				} else {
					repos.connect(userRepositoryPath(username).substring(1), ""); // With login user account!
				}
			}
			if (repos != null && repos.getMetaStore() != null) {
				dms.addMetaStore(0, repos.getMetaStore());
				dms.setActiveMetaStoreName(repos.getMetaStore().getName());
			}

			// If the Pentaho name space doesn't exist, create it!
			if (!dms.namespaceExists(MetaStoreConst.NAMESPACE_PENTAHO)) {
				dms.createNamespace(MetaStoreConst.NAMESPACE_PENTAHO);
			}

			// If the iDatrix name space doesn't exist, create it!
			if (!dms.namespaceExists(MetaStoreConst.NAMESPACE_IDATRIX)) {
				dms.createNamespace(MetaStoreConst.NAMESPACE_IDATRIX);
			}

			// Thread.sleep(500); // JW: to avoid name space checking failure occasionally!
		} catch (MetaStoreException | KettleException e) {
			throw new Exception("[MetaStore] Error opening the repository metastore or local metastore!", e);
		}

		if (Utils.isEmpty(username)) {
			metaStore = dms;
		} else {
			metaStores.put(username, dms);
		}

		return dms;
	}

	//##############################仓库路径信息##################################

	/**
	 * Generate path for given user in repository.
	 * @param username
	 * @return
	 */
	private  String userRepositoryPath( String username) {
		
		try {
			String parent = getRenterIdRepositoryPath(null) ;
			String userRepoPath = Const.NVL( Const.NVL(username,CloudSession.getResourceUser() ) , MetaStoreConst.META_FOLDER);
			
			CloudRepository.createDir(parent, userRepoPath);
			return parent+userRepoPath + SEPARATOR;
		} catch (KettleException e) {
			logger.error("用户路径创建失败.",e);
			// If failed to create user folder, use root path "/" by instead
			return SEPARATOR;
		}
	}
	
	
	public  String getRenterIdRepositoryPath( String renterId) {
		try {
			String parent = SEPARATOR ;
		
			Boolean renterPrivilege = IdatrixPropertyUtil.getBooleanProperty("idatrix.renter.super.privilege.enable", false);
			if(renterPrivilege || ( !renterPrivilege && IdatrixPropertyUtil.getBooleanProperty("idatrix.use.renter.enable", false)) ) {
				String renterPath = Const.NVL(Const.NVL(renterId,CloudSession.getLoginRenterId() ) , "0") ;
				CloudRepository.createDir(parent, renterPath);
				return  parent+renterPath + SEPARATOR;
			}
			return parent ; 
		} catch (KettleException e) {
			logger.error("用户路径创建失败.",e);
			return SEPARATOR;
		}
	}
	

	/**
	 * Initiate user cloud repository.
	 * @param userRepoPath
	 * @return
	 */
	public  String getCloudRepositoryPath(String username) {
		String userRepoPath = userRepositoryPath( username );
		try {
			CloudRepository.createDir(userRepoPath, CLOUD_REPOSITORY_DIR);
			return userRepoPath + CLOUD_REPOSITORY_DIR + SEPARATOR;
		} catch (KettleException e) {
			logger.error("cloud路径创建失败.",e);
			return userRepoPath;
		}
	}

	/**
	 * Initiate user trans repository.
	 * @param userRepoPath
	 * @return
	 */
	public  String getUserTransRepositoryPath(String username,String group) {
		
		String userRepoPath = userRepositoryPath( username );
		
		group = Const.NVL(group, CloudRepository.DEFAULT_GROUP_NAME);
		if(CloudRepository.ALL_GROUP_NAME.equalsIgnoreCase(group)) {
			group = null;
		}
		
		try {
			String transDir = TRANS_REPOSITORY_DIR;
			if(!Utils.isEmpty(group)) {
				transDir = transDir+SEPARATOR+group ;
			}
			CloudRepository.createDir(userRepoPath, transDir);
			return userRepoPath + transDir + SEPARATOR;
		} catch (KettleException e) {
			logger.error("trans路径创建失败.",e);
			return userRepoPath;
		}
	}

	/**
	 * Initiate user jobs repository.
	 * @param userRepoPath
	 * @return
	 */
	public  String getUserJobsRepositoryPath(String username,String group) {
		String userRepoPath = userRepositoryPath( username );
		
		group = Const.NVL(group, CloudRepository.DEFAULT_GROUP_NAME);
		if(CloudRepository.ALL_GROUP_NAME.equalsIgnoreCase(group)) {
			group = null;
		}
		
		try {
			String jobDir = JOB_REPOSITORY_DIR;
			if(!Utils.isEmpty(group)) {
				jobDir = jobDir+SEPARATOR+group ;
			}
			CloudRepository.createDir(userRepoPath, jobDir);
			return userRepoPath + jobDir + SEPARATOR;
		} catch (KettleException e) {
			logger.error("job路径创建失败.",e);
			return userRepoPath;
		}
	}

	/**
	 * Initiate user logs repository.
	 * @param userRepoPath
	 * @return
	 */
	public  String getUserLogsRepositoryPath(String username) {
		String userRepoPath = userRepositoryPath( username );
		try {
			CloudRepository.createDir(userRepoPath, LOG_REPOSITORY_DIR);
			return userRepoPath + LOG_REPOSITORY_DIR + SEPARATOR;
		} catch (KettleException e) {
			logger.error("日志路径创建失败.",e);
			return userRepoPath;
		}
	}

	
	/**
	 * Get given current user Jndi path in repository.
	 * @param username
	 * @return
	 */
	public  String getUserJndiRepositoryPath(String username ) {
		String userRepoPath = userRepositoryPath( username );
		try {
			CloudRepository.createDir(userRepoPath, JNDI_REPOSITORY_DIR);
			return userRepoPath + JNDI_REPOSITORY_DIR + SEPARATOR;
		} catch (KettleException e) {
			logger.error("Jndi路径创建失败.",e);
			return userRepoPath;
		}
	}


}

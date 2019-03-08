/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.security;

import java.io.IOException;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hive.jdbc.HiveDriver;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ys.idatrix.cloudetl.util.EnvUtils;

/**
 * Hadoop数据安全管理插件实现 <br/>
 * IdatrixSecurityManager <br/>
 * @author XH
 * @since 2017年10月25日
 *
 */
public class IdatrixSecurityManager   {

	private Logger logger = LoggerFactory.getLogger(IdatrixSecurityManager.class);

	private final ConcurrentMap<String, UserGroupInformation> userUgiMap = new ConcurrentHashMap<>();

	private static IdatrixSecurityManager idatrixSecurityManager;

	private IdatrixSecurityProperties isp;

	private Configuration conf;

	private UserGroupInformation hdfsUser;

	private UserGroupInformation hiveUser;

	private UserGroupInformation hbaseUser;

	/**
	 * 最近登录时间(ms)，等于0表示不启用安全连接
	 */
	private long loginTimeMs = 0;

	private ReentrantLock lock = new ReentrantLock();

	private IdatrixSecurityManager() throws HadoopSecurityManagerException{
		logger.info(">>> IdatrixSecurityManager()");
		isp = IdatrixSecurityProperties.getInstance();

		if (isp.securityEnabled) {
			init(); // Only for kerberos authentication !!!
		}
	}

	public static IdatrixSecurityManager getInstance() throws HadoopSecurityManagerException{
		if(idatrixSecurityManager == null){
			synchronized (IdatrixSecurityManager.class) {
				if( idatrixSecurityManager == null ){
					idatrixSecurityManager = new IdatrixSecurityManager();
				}
			}
		}
		return idatrixSecurityManager;
	}

	private void init() throws HadoopSecurityManagerException {
		logger.info(">>> init()");

		try {
			System.setProperty("hadoop.home.dir", isp.hadoopHome);

			// hadoop.security.authentication 为 simple模式时 替换当前系统用户
			System.setProperty("HADOOP_PROXY_USER", isp.hdfsProxyUser);
			logger.info(">>> init() -> HADOOP_PROXY_USER = " + isp.hdfsProxyUser);

			this.conf = new Configuration();
			this.conf.setClassLoader(isp.classloader);
			System.setProperty("java.security.krb5.conf", isp.krb5ConfLocation);
			UserGroupInformation.setConfiguration(conf);

			// hdfs
			if (!StringUtils.isEmpty(isp.hdfsKeytab) && !StringUtils.isEmpty(isp.hdfsPrincipal)) {
				this.hdfsUser = UserGroupInformation.loginUserFromKeytabAndReturnUGI(isp.hdfsPrincipal, isp.hdfsKeytab);
				logger.info(">>> init() -> this.hdfsUser = " + this.hdfsUser);
			}

			//hive
			if (!StringUtils.isEmpty(isp.hiveKeytab) && !StringUtils.isEmpty(isp.hivePrincipal)) {
				// hive,如果和hdfs用户相同，无需再次登录
				if (isp.hivePrincipal.equals(isp.hdfsPrincipal)) {
					this.hiveUser = this.hdfsUser;
				} else {
					this.hiveUser = UserGroupInformation.loginUserFromKeytabAndReturnUGI(isp.hivePrincipal, isp.hiveKeytab);
				}
			}

			// hbase
			if (!StringUtils.isEmpty(isp.hbaseKeytab) && !StringUtils.isEmpty(isp.hbasePrincipal)) {
				// hbase,如果和hdfs用户相同，无需再次登录
				if (isp.hbasePrincipal.equals(isp.hdfsPrincipal)) {
					this.hbaseUser = this.hdfsUser;
				} else {
					this.hbaseUser = UserGroupInformation.loginUserFromKeytabAndReturnUGI(isp.hbasePrincipal, isp.hbaseKeytab);
				}
			}

			this.loginTimeMs = System.currentTimeMillis();
		} catch (IOException e) {
			throw new HadoopSecurityManagerException("Failed to login with kerberos ", e);
		}
	}

	public void destroy() {

	}

	/**
	 * 检查TGT超时，重新登陆
	 */
	private void checkTGTAndReloginFromKeytab() {
		logger.info(">>> checkTGTAndReloginFromKeytab()");

		if (lock.tryLock()) {
			try {
				long current = System.currentTimeMillis();
				if (loginTimeMs == 0L || isp.reloginIntervalMin == 0L) {
					return;
				}
				if (current < loginTimeMs + isp.reloginIntervalMin * 3600) {
					if (hdfsUser != null) {
						logger.info("loginUser (" + this.hdfsUser + ") already created, refreshing tgt.");
						hdfsUser.checkTGTAndReloginFromKeytab();
						userUgiMap.clear();
					}
					if (hiveUser != null && hiveUser != hdfsUser) {
						logger.info("loginUser (" + this.hiveUser + ") already created, refreshing tgt.");
						hiveUser.checkTGTAndReloginFromKeytab();
					}
					if (hbaseUser != null && hbaseUser != hdfsUser) {
						logger.info("loginUser (" + this.hbaseUser + ") already created, refreshing tgt.");
						hbaseUser.checkTGTAndReloginFromKeytab();
					}
					loginTimeMs = System.currentTimeMillis();
				}
			} catch (Exception e) {
				logger.error("checkTGTAndReloginFromKeytab error!", e);
			} finally {
				lock.unlock();
			}
		}
	}

	public Properties getPhoenixJDBCProps() {
		return isp.hbaseJDBCProps;
	}

	public UserGroupInformation getProxiedHDFSUser(String user) {
		logger.info(">>> getProxiedHDFSUser()");

		UserGroupInformation ugi = this.userUgiMap.get(user);
		if (ugi == null) {
			logger.info("proxy user " + user + " not exist. Creating new proxy user");
		}

		if (isp.securityEnabled) {
			ugi = UserGroupInformation.createProxyUser(user, getDefaultHDFSUser());
			this.userUgiMap.putIfAbsent(user, ugi);
		}

		return ugi;
	}

	public UserGroupInformation getProxiedHiveUser(String user) {
		logger.info(">>> getProxiedHiveUser()");

		if( hdfsUser == hiveUser ){
			return getProxiedHDFSUser(user);
		}

		UserGroupInformation ugi = this.userUgiMap.get(user+"-hive");
		if (ugi == null) {
			logger.info("proxy user " + user + " not exist. Creating new proxy user");
		}

		if (isp.securityEnabled) {
			ugi = UserGroupInformation.createProxyUser(user, getHiveUser());
			this.userUgiMap.putIfAbsent(user+"-hive", ugi);
		}

		return ugi;
	}

	public UserGroupInformation getProxiedHbaseUser(String user) {
		logger.info(">>> getProxiedHbaseUser()");
		if( hdfsUser == hbaseUser ){
			return getProxiedHDFSUser(user);
		}

		UserGroupInformation ugi = this.userUgiMap.get(user+"-hbase");
		if (ugi == null) {
			logger.info("proxy user " + user + " not exist. Creating new proxy user");
		}

		if (isp.securityEnabled) {
			ugi = UserGroupInformation.createProxyUser(user, getHbaseUser());
			this.userUgiMap.putIfAbsent(user+"-hbase", ugi);
		}

		return ugi;
	}

	public UserGroupInformation getDefaultHDFSUser() {
		checkTGTAndReloginFromKeytab();
		return hdfsUser;
	}

	public UserGroupInformation getHiveUser() {
		checkTGTAndReloginFromKeytab();
		return hiveUser;
	}

	public UserGroupInformation getHbaseUser() {
		checkTGTAndReloginFromKeytab();
		return hbaseUser;
	}

	/**
	 * 获取hdfs文件系统 :</br>
	 * 				         当 securityEnabled 为false, 使用HDFS管理用户连接HDFS</br>
	 * 				         当	securityEnabled 为true , iDatrix配置为true,是安全系统hdfs,无法连接抛出异常<br>
	 * 				         当 securityEnabled 为true , iDatrix配置为false,优先使用安全系统hdfs,无法连接返回 null<br>
	 * @param user
	 * @param rootName
	 * @param fileSystemOptions
	 * @return
	 * @throws HadoopSecurityManagerException
	 */
	public FileSystem getFs(String user, FileName rootName, FileSystemOptions fileSystemOptions) throws HadoopSecurityManagerException{
		logger.info(">>> getFs() -> user = " + user);
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

		FileSystem fs = null;
		if (isp.securityEnabled) {
			try {
				Thread.currentThread().setContextClassLoader(isp.classloader);
				UserGroupInformation curHdfsUser = null;

				if(!isp.hdfsUseAdmin && user!=null && user.length() !=0){
					curHdfsUser  = getProxiedHDFSUser(user);
					logger.info(">>> getFs() #1 -> curHdfsUser = " + curHdfsUser);
				}

				if(EnvUtils.isiDatrix() && curHdfsUser == null ){
					curHdfsUser = hdfsUser;
					logger.info(">>> getFs() #2 -> curHdfsUser = " + curHdfsUser);
				}

				if (curHdfsUser != null) {
					fs = curHdfsUser.doAs(new PrivilegedAction<FileSystem>() {
						public FileSystem run() {
							try {
								return FileSystem.get(conf);
							} catch (final IOException e) {
								throw new RuntimeException(e);
							}
						}
					});
				}
			} catch (Exception e) {
				if (EnvUtils.isiDatrix()) {
					throw new HadoopSecurityManagerException("user:"+user+"获取hdfs安全文件系统失败",e);
				}
			} finally {
				Thread.currentThread().setContextClassLoader(oldClassLoader);
			}

			if (fs == null && EnvUtils.isiDatrix()) {
				throw new HadoopSecurityManagerException("HDFS security config 异常, 请检查配置!");
			}
		} else { // Security disabled!
			try {
				Thread.currentThread().setContextClassLoader(isp.classloader);

				this.conf = new Configuration();
				this.conf.setClassLoader(isp.classloader);

				// Using root user 'hdfs' for HDFS if security disabled!
				System.setProperty("HADOOP_USER_NAME", isp.hdfsProxyUser);

				fs = FileSystem.get(conf);
			} catch (IOException e) {
				//e.printStackTrace();
				logger.error("getFs error", e);
			}  finally {
				Thread.currentThread().setContextClassLoader(oldClassLoader);
			}
		}

		logger.info(">>> getFs() -> return fs = " + (fs == null ? "NULL" : fs.getCanonicalServiceName()));
		return fs;
	}

	/**
	 * 获取Hive数据库连接</br>
	 * 				         当 securityEnabled 为false 返回 null</br>
	 * 				         当	securityEnabled 为true , iDatrix配置为true,是安全系统hive,无法连接抛出异常</br>
	 * 				         当 securityEnabled 为true , iDatrix配置为false,优先使用安全系统hive,无法连接返回 null </br>
	 * @param database
	 * @param user
	 * @return
	 * @throws HadoopSecurityManagerException
	 */
	public Connection getHiveJdbcConnection(String database, String user,DatabaseInterface hiveMeta) throws HadoopSecurityManagerException {
		logger.info(">>> getHiveJdbcConnection()");
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

		//use default hive
		boolean defaultHive = true ;
		if( !Utils.isEmpty(isp.hiveThriftServer)&& hiveMeta != null && !Utils.isEmpty(hiveMeta.getHostname()) && !hiveMeta.getHostname().equals(isp.hiveThriftServer) ) {
			//hive 地址不一致
			defaultHive = false ;
		}
		
		if(isp.securityEnabled && defaultHive) {
			JdbcConnectionEntry entry = null;
			try {
				Thread.currentThread().setContextClassLoader(isp.classloader);
				UserGroupInformation curHiveUser =  null;;
				if (!isp.hiveUseAdmin && user!=null && user.length() !=0){
					curHiveUser  = getProxiedHiveUser(user);
				}
				if (EnvUtils.isiDatrix() && curHiveUser == null){
					curHiveUser = hiveUser;
				}

				if (curHiveUser != null){
					checkTGTAndReloginFromKeytab();
					entry  = curHiveUser.doAs(new PrivilegedAction<JdbcConnectionEntry>() {
						public JdbcConnectionEntry run() {
							JdbcConnectionEntry result = new JdbcConnectionEntry();
							try {
								String hostName = Const.NVL(isp.hiveThriftServer, (hiveMeta!= null)?hiveMeta.getHostname():"");
								String port = Const.NVL(isp.hiveThriftPort, (hiveMeta!= null)?hiveMeta.getDatabasePortNumberString():"");
								String url = "jdbc:hive2://" + hostName + ":" + port + "/" + database + ";principal=" + isp.hivePrincipal;
								logger.info(url);
								Class.forName("org.apache.hive.jdbc.HiveDriver");
								//Connection conn = DriverManager.getConnection(url, "user01", "lch");
								Properties properties = new Properties();
								properties.put("user", Const.NVL(isp.hiveThriftpassword, user ));
								properties.put("password", Const.NVL(isp.hiveThriftpassword, "") );
								Connection conn = new HiveDriver().connect(url, properties);
								result.setConnection(conn);
							} catch (Exception e) {
								e.printStackTrace();
								logger.error("getHiveJdbcConnection error", e);
								result.setErrorMessage(e.getMessage());
							}
							return result;
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (EnvUtils.isiDatrix()) {
					throw new HadoopSecurityManagerException("user:"+user+" 获取hive安全数据库连接失败",e);
				}
			} finally {
				Thread.currentThread().setContextClassLoader(oldClassLoader);
			}

			if (EnvUtils.isiDatrix() && (entry ==null || entry.getConnection() == null)) {
				throw new HadoopSecurityManagerException(entry != null ? entry.getErrorMessage() : "Hbase security config 异常, 请检查配置!");
			}

			return entry != null ? entry.getConnection() : null;
		} else { // Security disabled!
			Connection conn = null;
			try {
				Thread.currentThread().setContextClassLoader(isp.classloader);
				// Using root user 'hdfs' for HDFS if security disabled!
				System.setProperty("HADOOP_USER_NAME", isp.hdfsProxyUser);
				
				String userName = ( hiveMeta!= null&&!Utils.isEmpty(hiveMeta.getUsername()))?hiveMeta.getUsername():Const.NVL(isp.hiveThriftUser , "hive") ;
				String password = ( hiveMeta!= null&&!Utils.isEmpty(hiveMeta.getPassword()))?hiveMeta.getPassword():Const.NVL(isp.hiveThriftpassword , "") ;
				
//				Properties properties = new Properties();
//				properties.put("user", userName);
//				properties.put("password", password);

				String hostName = ( hiveMeta!= null&&!Utils.isEmpty(hiveMeta.getHostname())) ?hiveMeta.getHostname():isp.hiveThriftServer;
				String port =  (hiveMeta!= null&&!Utils.isEmpty(hiveMeta.getDatabasePortNumberString()) )?hiveMeta.getDatabasePortNumberString():isp.hiveThriftPort;
				String url = "jdbc:hive2://" + hostName + ":" + port + "/" + database;
				logger.info(url);
				Class.forName("org.apache.hive.jdbc.HiveDriver");
				//conn = new HiveDriver().connect(url, properties);
				conn = DriverManager.getConnection(url, userName,password);
				//logger.info("DEBUG >>> getConnection: " + conn + ", ! without properties: " + properties);
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error("getHiveJdbcConnection error", e);
			} finally {
				Thread.currentThread().setContextClassLoader(oldClassLoader);
			}

			return conn;
		}
	}

	/**
	 * 获取hbase数据库连接 :</br>
	 * 				         当 securityEnabled 为false 返回 null</br>
	 * 				         当	securityEnabled 为true , iDatrix配置为true,是安全系统hbase,无法连接抛出异常</br>
	 * 				         当 securityEnabled 为true , iDatrix配置为false,优先使用安全系统hbase,无法连接返回 null </br>
	 * @param user
	 * @return
	 * @throws HadoopSecurityManagerException
	 */
	public Connection getHbaseJdbcConnection(String user, DatabaseInterface hbaseMeta) throws HadoopSecurityManagerException {
		logger.info(">>> getHbaseJdbcConnection()");
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

		if (isp.securityEnabled) {
			JdbcConnectionEntry entry =null;
			try {
				Thread.currentThread().setContextClassLoader(isp.classloader);
				UserGroupInformation curHbaseUser = null;
				if (!isp.hbaseUseAdmin && user!=null && user.length() !=0){
					curHbaseUser = getProxiedHbaseUser(user);
				}
				if (EnvUtils.isiDatrix() && curHbaseUser == null){
					curHbaseUser = hbaseUser;
				}
				logger.info("DEBUG >>> curHbaseUser = " + curHbaseUser);
				if (curHbaseUser != null) {
					checkTGTAndReloginFromKeytab();
					entry = curHbaseUser.doAs(new PrivilegedAction<JdbcConnectionEntry>() {
						public JdbcConnectionEntry run() {
							JdbcConnectionEntry result = new JdbcConnectionEntry();
							try {
								// phoenix jdbc url 
								String phoenixUrl = null ;
								if( !Utils.isEmpty(isp.hbaseZookeeperQuorum) ) {
									phoenixUrl =  getPhoenixUrl();
								}else {
									phoenixUrl = hbaseMeta.getURL(hbaseMeta.getHostname(), hbaseMeta.getDatabasePortNumberString(), hbaseMeta.getDatabaseName()) ;
								}
								logger.info(phoenixUrl);
								Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
								logger.info("DEBUG >>> org.apache.phoenix.jdbc.PhoenixDriver");
								Connection conn = DriverManager.getConnection(phoenixUrl, isp.hbaseJDBCProps);
								logger.info("DEBUG >>> getConnection: " + conn + ", with hbaseJDBCProps: " + isp.hbaseJDBCProps);
								result.setConnection(conn);
							} catch (Exception e) {
								logger.error("getPhoenixJdbcConnection error", e);
								result.setErrorMessage(e.getMessage());
							} 
							return result;
						}
					});
				}
			} catch(Exception e) {
				if (EnvUtils.isiDatrix()) {
					throw new HadoopSecurityManagerException("user:"+user+"获取hbase安全数据库连接失败",e);
				}
			} finally {
				Thread.currentThread().setContextClassLoader(oldClassLoader);
			}

			if ( EnvUtils.isiDatrix() && (entry ==null || entry.getConnection() == null )) {
				throw new HadoopSecurityManagerException(entry != null ? entry.getErrorMessage() : "Hbase security config 异常, 请检查配置!");
			}

			return entry != null ? entry.getConnection() : null;
		} else { // Security disabled!
			Connection conn = null;
			try {
				Thread.currentThread().setContextClassLoader(isp.classloader);
				//System.setProperty("hadoop.home.dir", isp.hadoopHome);
				Properties props = new Properties();
				props.setProperty("phoenix.schema.isNamespaceMappingEnabled", "true");

				// Using root user 'hdfs' for HDFS if security disabled!
				System.setProperty("HADOOP_USER_NAME", isp.hdfsProxyUser);

				String phoenixUrl = null ;
				try {
					phoenixUrl = hbaseMeta.getURL(hbaseMeta.getHostname(), hbaseMeta.getDatabasePortNumberString(), hbaseMeta.getDatabaseName());
				} catch (KettleDatabaseException e) {
				};
				if( Utils.isEmpty(phoenixUrl) ) {
					phoenixUrl =  getPhoenixUrl();
				}
				logger.info(phoenixUrl);
				Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
				logger.info("DEBUG >>> org.apache.phoenix.jdbc.PhoenixDriver");
				//DriverManager.registerDriver((Driver) (Class.forName("org.apache.phoenix.jdbc.PhoenixDriver").newInstance()));
				conn = DriverManager.getConnection(phoenixUrl, props);
				logger.info("DEBUG >>> getConnection: " + conn + ", with properties: " + props);
			} catch (SQLException | ClassNotFoundException e) {
				//e.printStackTrace();
				logger.error("getHbaseJdbcConnection error", e);
			} finally {
				Thread.currentThread().setContextClassLoader(oldClassLoader);
			}

			return conn;
		}
	}

	public String getPhoenixUrl() {
		return  "jdbc:phoenix:" + isp.hbaseZookeeperQuorum + ":" + isp.hbaseClientPort + ":" + (isp.hbaseZnodeParent.startsWith("/") ? isp.hbaseZnodeParent : ("/" + isp.hbaseZnodeParent));
	}

	public void setReloginIntervalMin(long reloginIntervalMin) {
		isp.reloginIntervalMin = reloginIntervalMin;
	}

	public PropertiesConfiguration getProperties(){
		return isp.properties;
	}

}

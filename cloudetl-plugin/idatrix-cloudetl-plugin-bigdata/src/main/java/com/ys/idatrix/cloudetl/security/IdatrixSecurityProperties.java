/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.security;

import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.pentaho.di.core.plugins.KettleLifecyclePluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ys.idatrix.cloudetl.fs.IdatrixHDFSFileProvider;
import com.ys.idatrix.cloudetl.util.EnvUtils;

/**
 * IdatrixSecurityProperties <br/>
 * @author JW
 * @since 2017年11月10日
 * 
 */
public class IdatrixSecurityProperties {
	
	private Logger logger = LoggerFactory.getLogger(IdatrixSecurityProperties.class);
	
	private static IdatrixSecurityProperties idatrixSecurityProperties;
	
	protected PropertiesConfiguration properties =null;
	
	protected ClassLoader classloader;
	
	protected String hadoopHome;

	protected boolean securityEnabled;

	protected String hdfsKeytab;

	protected boolean hdfsUseAdmin;

	protected String hdfsPrincipal;

	protected boolean hiveUseAdmin;

	protected String hiveKeytab;

	protected String hivePrincipal;

	protected String krb5ConfLocation;

	protected String hiveThriftServer;

	protected String hiveThriftPort;
	
	protected String hiveThriftUser;
	
	protected String hiveThriftpassword;

	protected boolean hbaseUseAdmin;

	protected String hbaseKeytab;

	protected String hbasePrincipal;

	protected String hbaseZookeeperQuorum;

	protected String hbaseClientPort;

	protected String hbaseZnodeParent;

	protected Properties hbaseJDBCProps;

	protected String hdfsProxyUser;
	
	/**
	 * 最近登录时间(min)，小于等于0表示不重新登录
	 */
	protected long reloginIntervalMin = 0;
	
	private IdatrixSecurityProperties() {
		logger.info(">>> IdatrixSecurityProperties()");
		classloader = IdatrixSecurityProperties.class.getClassLoader();
		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		try{
			Thread.currentThread().setContextClassLoader(classloader);
			PluginInterface pi = PluginRegistry.getInstance().findPluginWithId( KettleLifecyclePluginType.class, IdatrixHDFSFileProvider.PLUGIN_ID );
			logger.info(">>> IdatrixSecurityProperties() -> pi = " + pi);
			
			try {
				String classPath;
				if (pi == null || Utils.isEmpty(pi.getPluginDirectory().getPath())){
					classPath = classloader.getResource("").getPath() ;
				} else {
					classPath = pi.getPluginDirectory().getPath();
					EnvUtils.addPluginPathToClassLoader(pi, classloader);
				}

				properties = EnvUtils.loadProperties( classPath , "plugin.properties");
				logger.info(">>> IdatrixSecurityProperties() -> propertiesPath = " + properties.getPath());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 读取数据配置, 优先系统属性, 再plugins.properties配置文件, 再默认值
			securityEnabled= Boolean.valueOf( properties.getString("idatrix.kerberos.deployment","false") ) ;
			logger.info(">>> IdatrixSecurityProperties() -> securityEnabled = " + properties.getString("idatrix.kerberos.deployment"));

			hadoopHome=EnvUtils.getVlaueByProperties(properties, "security.hadoop.home.dir","" ) ;
			krb5ConfLocation= EnvUtils.getPathVlaueByProperties(properties, "security.kbr5conf.location") ;
			String security_kerberos_timeout = EnvUtils.getVlaueByProperties(properties, "security.kerberos.timeout","3");
			if(!Utils.isEmpty(security_kerberos_timeout)){
				reloginIntervalMin=Long.valueOf( security_kerberos_timeout );
			}

			hdfsUseAdmin=  Boolean.valueOf(EnvUtils.getVlaueByProperties(properties, "security.hdfs.useAdmin","false") );
			hdfsPrincipal= EnvUtils.getVlaueByProperties(properties, "security.hdfs.kerberos.principal") ;
			hdfsKeytab= EnvUtils.getPathVlaueByProperties(properties, "security.hdfs.kerberos.keytab") ;

			hiveUseAdmin=  Boolean.valueOf(EnvUtils.getVlaueByProperties(properties, "security.hive.useAdmin","false")) ;
			hivePrincipal= EnvUtils.getVlaueByProperties(properties, "security.hive.kerberos.principal") ;
			hiveKeytab= EnvUtils.getPathVlaueByProperties(properties, "security.hive.kerberos.keytab") ;
			hiveThriftServer= EnvUtils.getVlaueByProperties(properties, "security.spark.thrift.server") ;
			hiveThriftPort= EnvUtils.getVlaueByProperties(properties, "security.spark.thrift.port") ;
			hiveThriftUser= EnvUtils.getVlaueByProperties(properties, "security.spark.thrift.user") ;
			hiveThriftpassword= EnvUtils.getVlaueByProperties(properties, "security.spark.thrift.password") ;

			hbaseUseAdmin=  Boolean.valueOf(EnvUtils.getVlaueByProperties(properties, "security.hbase.useAdmin","false") );
			hbasePrincipal= EnvUtils.getVlaueByProperties(properties, "security.hbase.kerberos.principal") ;
			hbaseKeytab= EnvUtils.getPathVlaueByProperties(properties, "security.hbase.kerberos.keytab") ;
			hbaseZookeeperQuorum= EnvUtils.getVlaueByProperties(properties, "security.hbase.zookeeper.quorum") ;
			hbaseClientPort= EnvUtils.getVlaueByProperties(properties, "security.hbase.zookeeper.property.clientPort") ;
			hbaseZnodeParent= EnvUtils.getVlaueByProperties(properties, "security.zookeeper.znode.parent") ;
			hbaseJDBCProps = new Properties();
			hbaseJDBCProps.put("phoenix.schema.isNamespaceMappingEnabled",true);
			hbaseJDBCProps.put("phoenix.queryserver.withRemoteUserExtractor",true);

			hdfsProxyUser= EnvUtils.getVlaueByProperties(properties, "idatrix.hdfs.proxy.user","hdfs") ;
			logger.info(">>> IdatrixSecurityProperties() -> hdfsProxyUser = " + hdfsProxyUser);
		}finally{
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
	}
	
	public static IdatrixSecurityProperties getInstance() {
		if (idatrixSecurityProperties == null) {
			synchronized (IdatrixSecurityProperties.class) {
				if (idatrixSecurityProperties == null) {
					idatrixSecurityProperties = new IdatrixSecurityProperties();
				}
			}
		}
		return idatrixSecurityProperties;
	}

}

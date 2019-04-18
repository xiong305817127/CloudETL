/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.metastore;

import java.io.File;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.stores.xml.XmlMetaStore;
import org.pentaho.metastore.stores.xml.XmlUtil;
import org.pentaho.metastore.util.PentahoDefaults;

public class MetaStoreConst {
	public static final String META_FOLDER = ".meta";

	public static final String NAMESPACE_IDATRIX = "iDatrix";
	public static final String NAMESPACE_PENTAHO = PentahoDefaults.NAMESPACE;
	// Hadoop Cluster
	public static final String ELEMENT_TYPE_NAME_NAMED_CLUSTER = "NamedCluster";
	public static final String ELEMENT_TYPE_DESCRIPTION_NAMED_CLUSTER = "A NamedCluster";
	// Cluster Schema
	public static final String ELEMENT_TYPE_NAME_CLUSTER_SCHEMA = "ClusterSchema";
	public static final String ELEMENT_TYPE_DESCRIPTION_CLUSTER_SCHEMA = "A cluster schema";

	// Slave Server
	public static final String ELEMENT_TYPE_NAME_SLAVE_SERVER = "SlaveServer";
	public static final String ELEMENT_TYPE_DESCRIPTION_SLAVE_SERVER = "A slave server";

	// Spark Engine
	public static final String ELEMENT_TYPE_NAME_SPARK_ENGINE = "Spark Engine";
	public static final String ELEMENT_TYPE_DESCRIPTION_SPARK_ENGINE = "Defines an spark transformation execution engine";

	// Spark Run Configuration
	public static final String ELEMENT_TYPE_NAME_SPARK_RUN_CONFIG = "Spark Run Configuration";
	public static final String ELEMENT_TYPE_DESCRIPTION_SPARK_RUN_CONFIG = "Defines an spark run configuration";

	// Default Run Configuration
	public static final String ELEMENT_TYPE_NAME_DEFAULT_RUN_CONFIG = "Default Run Configuration";
	public static final String ELEMENT_TYPE_DESCRIPTION_DEFAULT_RUN_CONFIG = "Defines a default run configuration";

	// Database Connection
	public static final String ELEMENT_TYPE_NAME_DATABASE_CONNECTION = "Database connection";
	public static final String ELEMENT_TYPE_DESCRIPTION_DATABASE_CONNECTION = "This is the official central database connection metadata";
	/**
	 * Database MetaStore Elements
	 */
  public static final String DB_ATTR_ID_DESCRIPTION = "description";
  public static final String DB_ATTR_ID_PLUGIN_ID = "plugin_id";
  public static final String DB_ATTR_ID_ACCESS_TYPE = "access_type";
  public static final String DB_ATTR_ID_HOSTNAME = "host_name";
  public static final String DB_ATTR_ID_PORT = "port";
  public static final String DB_ATTR_ID_DATABASE_NAME = "database_name";
  public static final String DB_ATTR_ID_USERNAME = "username";
  public static final String DB_ATTR_ID_PASSWORD = "password";
  public static final String DB_ATTR_ID_SERVERNAME = "server_name";
  public static final String DB_ATTR_ID_DATA_TABLESPACE = "data_tablespace";
  public static final String DB_ATTR_ID_INDEX_TABLESPACE = "index_tablespace";
  public static boolean disableMetaStore; // Used for testing only

  // Extra information for 3rd party tools, not used by Kettle
  //
  public static final String DB_ATTR_DRIVER_CLASS = "driver_class";
  public static final String DB_ATTR_JDBC_URL = "jdbc_url";

  public static final String DB_ATTR_ID_ATTRIBUTES = "attributes";
	/**
	 * Slave Server MetaStore Elements
	 */
	public static final String SERVER_ATTR_ID_NAME = "name";
	public static final String SERVER_ATTR_ID_HOST_NAME = "hostname";
	public static final String SERVER_ATTR_ID_PORT = "port";
	public static final String SERVER_ATTR_ID_WEB_APP_NAME = "webAppName";
	public static final String SERVER_ATTR_ID_USER_NAME = "username";
	public static final String SERVER_ATTR_ID_PASSWORD = "password";
	public static final String SERVER_ATTR_ID_PROXY_HOSTNAME = "proxy_hostname";
	public static final String SERVER_ATTR_ID_PROXY_PORT = "proxy_port";
	public static final String SERVER_ATTR_ID_NON_PROXY_HOSTS = "non_proxy_hosts";
	public static final String SERVER_ATTR_ID_MASTER = "master";
	public static final String SERVER_ATTR_ID_SSL_MODE = "sslMode";

	/**
	 * Cluster Schema MetaStore Elements
	 */
	public static final String CLUSTER_ATTR_ID_NAME = "name";
	public static final String CLUSTER_ATTR_ID_BASE_PORT = "base_port";
	public static final String CLUSTER_ATTR_ID_BUFFER_SIZE = "sockets_buffer_size";
	public static final String CLUSTER_ATTR_ID_INTERVAL = "sockets_flush_interval";
	public static final String CLUSTER_ATTR_ID_COMPRESSED = "sockets_compressed";
	public static final String CLUSTER_ATTR_ID_DYNAMIC = "dynamic";
	public static final String CLUSTER_ATTR_ID_SLAVE_SERVERS = "slaveservers";

	/**
	 * Hadoop Cluster MetaStore Elements
	 */
	public static final String HADOOP_ATTR_ID_NAME = "name";
	public static final String HADOOP_ATTR_ID_STORAGE = "storageScheme";
	public static final String HADOOP_ATTR_ID_HDFS_HOST = "hdfsHost";
	public static final String HADOOP_ATTR_ID_HDFS_PORT = "hdfsPort";
	public static final String HADOOP_ATTR_ID_HDFS_USER_NAME = "hdfsUsername";
	public static final String HADOOP_ATTR_ID_HDFS_PASSWORD = "hdfsPassword";

	public static final String HADOOP_ATTR_ID_MAPR = "mapr";

	public static final String HADOOP_ATTR_ID_LAST_MOD_DATE = "lastModifiedDate";
	public static final String HADOOP_ATTR_ID_JOB_TRACKER_HOST = "jobTrackerHost";
	public static final String HADOOP_ATTR_ID_JOB_TRACKER_PORT = "jobTrackerPort";
	public static final String HADOOP_ATTR_ID_ZOOKEEPER_HOST = "zooKeeperHost";
	public static final String HADOOP_ATTR_ID_ZOOKEEPER_PORT = "zooKeeperPort";
	public static final String HADOOP_ATTR_ID_SHIM_IDENTIFIER = "shimIdentifier";
	public static final String HADOOP_ATTR_ID_OOZIE_URL = "oozieUrl";

	/**
	 * Spark Run Configuration MetaStore Elements
	 */
	public static final String SPARK_RC_ATTR_ID_NAME = "name";
	public static final String SPARK_RC_ATTR_ID_DESCRIPTION = "description";
	public static final String SPARK_RC_ATTR_ID_URL = "url";

	/**
	 * Default Run Configuration MetaStore Elements
	 */
	public static final String DEFAULT_RC_ATTR_ID_NAME = "name";
	public static final String DEFAULT_RC_ATTR_ID_CLUSTERED = "clustered";
	public static final String DEFAULT_RC_ATTR_ID_SERVER = "server";
	public static final String DEFAULT_RC_ATTR_ID_DESCRIPTION = "description";
	public static final String DEFAULT_RC_ATTR_ID_READ_ONLY = "readOnly";
	public static final String DEFAULT_RC_ATTR_ID_SEND_RESOURCES = "sendResources";
	public static final String DEFAULT_RC_ATTR_ID_LOG_LOCALLY = "logRemoteExecutionLocally";
	public static final String DEFAULT_RC_ATTR_ID_REMOTE = "remote";
	public static final String DEFAULT_RC_ATTR_ID_LOCAL = "local";
	public static final String DEFAULT_RC_ATTR_ID_SHOW_TRANS = "showTransformations";

  public static final String getDefaultPentahoMetaStoreLocation() {
    return System.getProperty( "user.home" ) + File.separator + ".pentaho";
  }

  public static IMetaStore openLocalPentahoMetaStore() throws MetaStoreException {
    return MetaStoreConst.openLocalPentahoMetaStore( true );
  }

  public static IMetaStore openLocalPentahoMetaStore( boolean allowCreate ) throws MetaStoreException {
    if ( disableMetaStore ) {
      return null;
    }
    String rootFolder = System.getProperty( Const.PENTAHO_METASTORE_FOLDER );
    if ( Utils.isEmpty( rootFolder ) ) {
      rootFolder = getDefaultPentahoMetaStoreLocation();
    }
    File rootFolderFile = new File( rootFolder );
    File metaFolder = new File( rootFolder + File.separator + XmlUtil.META_FOLDER_NAME );
    if ( !allowCreate && !metaFolder.exists() ) {
      return null;
    }
    if ( !rootFolderFile.exists() ) {
      rootFolderFile.mkdirs();
    }

    XmlMetaStore metaStore = new XmlMetaStore( rootFolder );
    if ( allowCreate ) {
      metaStore.setName( Const.PENTAHO_METASTORE_NAME );
    }
    return metaStore;
  }
}

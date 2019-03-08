package com.ys.idatrix.db.core.security;

import com.ys.idatrix.db.exception.HadoopSecurityManagerException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;

import java.sql.Connection;
import java.util.Properties;

/**
 * @ClassName: BaseSqlService
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public interface HadoopSecurityManager {

	FileSystem getFSAsUser(String user) throws HadoopSecurityManagerException;

	FileSystem getFSAsDefaultUser() throws HadoopSecurityManagerException;

	Properties getPhoenixJDBCProps();

	/**
	 * 获取代理用户
	 * 
	 * @param user
	 * @return
	 */
	UserGroupInformation getProxiedHDFSUser(String user);

	/**
	 * 获取当前用户，如果没有配置kerberos返回null
	 * 
	 * @return
	 */
	UserGroupInformation getDefaultHDFSUser();

	/**
	 * 获取sparksql jdbc连接
	 * 
	 * @param database
	 *            数据库名
	 * @return
	 * @throws HadoopSecurityManagerException
	 */
	Connection getSparkJdbcConnection(String database) throws HadoopSecurityManagerException;
	
	/**
	 * 获取sparksql jdbc连接
	 * 
	 *            数据库名
	 * @return
	 * @throws HadoopSecurityManagerException
	 */
	Connection getPhoenixJdbcConnection() throws HadoopSecurityManagerException;

	/**
	 * 是否加密
	 * @return
	 */
	boolean isSecurityEnabled();
}

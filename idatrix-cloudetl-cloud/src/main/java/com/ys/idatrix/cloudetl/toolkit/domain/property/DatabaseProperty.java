/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.domain.property;

import java.io.Serializable;

/**
 * DatabaseProperty <br/>
 * @author JW
 * @since 2017年11月16日
 * 
 */
public class DatabaseProperty extends BaseProperty implements Serializable{

	private static final long serialVersionUID = 6777798267071395639L;

	private String host;
	private String port;
	private String dbName;
	private String dbOwner;
	private String dbType; // mysql, oracle ..
	private String dbUrl;
	//oracle 独有
	private String instance;
	private String tableSpace;
	
	public DatabaseProperty(String name) {
		super(name);
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbOwner() {
		return dbOwner;
	}

	public void setDbOwner(String dbOwner) {
		this.dbOwner = dbOwner;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String getTableSpace() {
		return tableSpace;
	}

	public void setTableSpace(String tableSpace) {
		this.tableSpace = tableSpace;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	

}

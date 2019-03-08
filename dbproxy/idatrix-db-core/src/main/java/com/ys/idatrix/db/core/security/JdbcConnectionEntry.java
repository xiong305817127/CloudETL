package com.ys.idatrix.db.core.security;

import java.sql.Connection;

/**
 * @ClassName: JdbcConnectionEntry
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public class JdbcConnectionEntry {
	private Connection connection;

	private String errorMessage;

	public JdbcConnectionEntry() {
	}
	
	public JdbcConnectionEntry(Connection connection) {
		super();
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}

package com.ys.idatrix.cloudetl.security;

import java.sql.Connection;

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

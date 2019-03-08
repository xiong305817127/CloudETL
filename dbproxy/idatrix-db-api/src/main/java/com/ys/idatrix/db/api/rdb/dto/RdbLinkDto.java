package com.ys.idatrix.db.api.rdb.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName: RdbLinkDto
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RdbLinkDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String url;

	private String driverClassName;

	private String username;

	private String password;

	private String ip;

	private String port;

	private String dbName;

	private String type;

	public RdbLinkDto() {
		super();
	}

	public RdbLinkDto(String url, String driverClassName, String username, String password) {
		super();
		this.url = url;
		this.driverClassName = driverClassName;
		this.username = username;
		this.password = password;
	}

	public RdbLinkDto(String username, String password, String type, String ip, String port, String dbName) {
		this.username = username;
		this.password = password;
		this.type = type;
		this.ip = ip;
		this.port = port;
		this.dbName = dbName;
	}

	public RdbLinkDto(String url, String driverClassName, String username, String password, String type, String ip, String port, String dbName) {
		this.url = url;
		this.driverClassName = driverClassName;
		this.username = username;
		this.password = password;
		this.type = type;
		this.ip = ip;
		this.port = port;
		this.dbName = dbName;
	}


}

package com.idatrix.unisecurity.freeipa.common;

public class RangerBasicInfoModel implements IRangerBasicInfoModel{

	private String username;

	private String passwd;

	private String httpUrl;
	
	public RangerBasicInfoModel(String username, String passwd, String httpUrl) {
		this.username = username;
		this.passwd = passwd;
		this.httpUrl = httpUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}

}

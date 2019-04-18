package com.ys.idatrix.cloudetl.subscribe.api.dto.parts;

import java.io.Serializable;

public class CommonDto implements Serializable{
	
	private static final long serialVersionUID = 6080081083646291465L;
	
	private String userId;
	private String system = "CloudETL";
	private String flag;
	 
	public CommonDto(String userId) {
		super();
		this.userId = userId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	@Override
	public String toString() {
		return "CommonDto [userId=" + userId + ", system=" + system + ", flag=" + flag + "]";
	}
	 
	 
}

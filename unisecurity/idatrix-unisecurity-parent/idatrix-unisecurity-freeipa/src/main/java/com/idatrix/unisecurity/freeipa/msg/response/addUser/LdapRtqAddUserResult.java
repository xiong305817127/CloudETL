package com.idatrix.unisecurity.freeipa.msg.response.addUser;

public class LdapRtqAddUserResult {
	
	private AddUserResult result;
	
	private String version;
	
	public AddUserResult getResult() {
		return result;
	}
	
	public void setResult(AddUserResult result) {
		this.result = result;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return "LdapRtqAddUserResult [result=" + result + ", version=" + version + "]";
	}
	
}

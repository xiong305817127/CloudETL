package com.idatrix.unisecurity.freeipa.ldap.response.addUser2Group;

import com.idatrix.unisecurity.freeipa.msg.response.LdapResponseError;

public class LdapRtq {
	private LdapResponseError error;
	
	private String id;
	
	private String principal;
	
	private LdapRtqResult result;

	public LdapResponseError getError() {
		return error;
	}

	public void setError(LdapResponseError error) {
		this.error = error;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public LdapRtqResult getResult() {
		return result;
	}

	public void setResult(LdapRtqResult result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "LdapRtq [error=" + error + ", id=" + id + ", principal=" + principal + ", result=" + result + "]";
	}	

}

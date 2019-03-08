package com.idatrix.unisecurity.freeipa.msg.response;

public  class BasicResponse {
	private LdapResponseError error;
	
	private String id;
	
	private String principal;

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

	@Override
	public String toString() {
		return "BasicResponse [error=" + error + ", id=" + id + ", principal=" + principal + "]";
	}
	
}

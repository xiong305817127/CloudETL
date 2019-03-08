package com.idatrix.unisecurity.freeipa.msg.response;



import com.idatrix.unisecurity.freeipa.ldap.response.addUser2Group.LdapRtqResult;

public class AddUser2GroupResponseVO {

	
	private LdapRtqResult result;


	public LdapRtqResult getResult() {
		return result;
	}

	public void setResult(LdapRtqResult result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "LdapRtqAddUser2GroupVO [result=" + result + "]";
	}


}



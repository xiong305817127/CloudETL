package com.idatrix.unisecurity.freeipa.msg.response;

import com.idatrix.unisecurity.freeipa.msg.response.addUser.LdapRtqAddUserResult;

public class AddUserResponseVO extends BasicResponse {
	
	private LdapRtqAddUserResult result;

	public LdapRtqAddUserResult getResult() {
		return result;
	}

	public void setResult(LdapRtqAddUserResult result) {
		this.result = result;
	}
}

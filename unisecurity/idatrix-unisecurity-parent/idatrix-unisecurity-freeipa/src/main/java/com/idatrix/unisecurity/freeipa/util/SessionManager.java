package com.idatrix.unisecurity.freeipa.util;

public class SessionManager {
	private static SessionManager sessionManager = new SessionManager();
	
	private String ipa_session = null;
	
	private SessionManager() {

	}

	public static SessionManager getInstance() {
		return sessionManager;
	}

	public String getIpa_session() {
		return ipa_session;
	}

	public void setIpa_session(String ipa_session) {
		this.ipa_session = ipa_session;
	}

}

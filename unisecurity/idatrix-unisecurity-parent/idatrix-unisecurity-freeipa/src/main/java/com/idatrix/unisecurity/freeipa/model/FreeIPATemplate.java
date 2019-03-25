package com.idatrix.unisecurity.freeipa.model;

import com.idatrix.unisecurity.freeipa.common.IPACommonInfo;

public class FreeIPATemplate implements IFreeIPATemplate{
	private String ldapUrl;
	
	private String adminUser;
	
	private String adminPwd;
	
	private String login_url ;

	
	private String referUrl ;
	
	private String oprUrl;
	
	private String changePwdUrl;
//	public static final String CHNAGE_PASSWORD = "/ipa/session/change_password";
//
//	public static final String LOGIN_URL= "/ipa/session/login_password";
//	
//	public static final String REFER_URL = "/ipa/ui/";
//	
//	public static final String OPR_URL = "/ipa/session/json";
	
	public FreeIPATemplate(String ldapUrl, String adminUser, String adminPwd) {
		this.ldapUrl = ldapUrl;
		this.adminUser = adminUser;
		this.adminPwd =  adminPwd;
		this.login_url = this.ldapUrl + IPACommonInfo.LOGIN_URL;
		this.referUrl = this.ldapUrl + IPACommonInfo.REFER_URL;
		this.oprUrl = this.ldapUrl + IPACommonInfo.OPR_URL;
		this.changePwdUrl = this.ldapUrl + IPACommonInfo.CHNAGE_PASSWORD;
	}
	
	public String getLdapUrl() {
		return ldapUrl;
	}

	public String getLogin_url() {
		return login_url;
	}

	public String getReferUrl() {
		return referUrl;
	}

	public String getOprUrl() {
		return oprUrl;
	}

	public String getChangePwdUrl() {
		return changePwdUrl;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public String getAdminUser() {
		return adminUser;
	}

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public String getAdminPwd() {
		return adminPwd;
	}

	public void setAdminPwd(String adminPwd) {
		this.adminPwd = adminPwd;
	}

	@Override
	public String toString() {
		return "FreeIPATemplate [ldapUrl=" + ldapUrl + ", adminUser=" + adminUser + ", adminPwd=" + adminPwd
				+ ", login_url=" + login_url + ", referUrl=" + referUrl + ", oprUrl=" + oprUrl + ", changePwdUrl="
				+ changePwdUrl + "]";
	}

}

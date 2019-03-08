package com.idatrix.unisecurity.freeipa.proxy.factory;

import java.util.List;

import com.idatrix.unisecurity.freeipa.common.User;
import com.idatrix.unisecurity.freeipa.msg.resquest.LdapReqParamVO;

public interface ILdapHttpDataBuilder {

	public LdapReqParamVO buildReqModifyUserPassword(String userName, String password, String old_password) ;

	public LdapReqParamVO buildReqAddUser(String userName, String password, String sn, String givenname) ;

	public LdapReqParamVO buildQueryUser(String userName) ;

	public LdapReqParamVO buildQueryUser(List<String> useNameList) ;

	public LdapReqParamVO buildReqAddGroupList(List<String> groupList, String desciption) ;

	public LdapReqParamVO buildReqGroupIsExists(String group) ;

	public LdapReqParamVO buildReqDeleleGroupList(List<String> groupList) ;

	public LdapReqParamVO buildReqAddUserList(List<User> userList) ;

	public LdapReqParamVO buildReqDeleteUserList(List<String> userList) ;

	public LdapReqParamVO buildReqAddGroupsMember(List<String> groups, List<String> userList) ;

	public LdapReqParamVO buildReqRemoveUserListFormGroupList(List<String> groups, List<String> userList) ;
	
	public LdapReqParamVO buildReqFindUser(String userName) ;

}

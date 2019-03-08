package com.idatrix.unisecurity.ranger.usersync.process;

import java.util.List;

public interface ILdapMgrUserGroupBuilder {
	/**
	 * Delete接口， Ranger并没有实现，因此还不能使用
	 * @param groupName 分组名
	 * @param userName 用户名
	 */
	@Deprecated
	void delXGroupUserInfo(String groupName, String userName) ;

	void addOrUpdateGroup(String groupName) throws Exception ;

	void addOrUpdateUser(String userName) throws Exception ;

	void addOrUpdateUser(String userName, List<String> groups) throws Exception ;
}

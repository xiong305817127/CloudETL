package com.idatrix.unisecurity.freeipa.proxy;

import java.util.List;

import com.idatrix.unisecurity.freeipa.common.User;

public interface IFreeIPAProxy {
	//增加用户
	public boolean addUser(String username, String password) throws Exception;	
	//增加用户。与addUser()函数的区别： 此函数不会调用changeUserOwnPwd()，即用户登录kerberos或者freeipa的时候，会弹出对话框，首次登录，需要修改密码
	public boolean addUserList(List<User> userList) throws Exception;
	
	
	//删除不存在的用户，也是成功
	public boolean deleteUser(String name) throws Exception;
	//批量删除用户
	public boolean deleteUserList(List<String> userList) throws Exception;
	//修改用户密码	
	public boolean changeUserOwnPwd(String name, String oldPwd, String newPwd) throws Exception;
	
	//增加用户组
	public boolean addGroup(String groupName, String description) throws Exception;
	//批量增加用户组
	public boolean addGroupList(List<String> groupList) throws Exception;
	
	//删除用户组
	public boolean deleteGroup(String groupName) throws Exception;
	//批量删除用户组
	public boolean deleteGroupList(List<String> groupList) throws Exception;
	
	
	public boolean addUsers2Group(List<String> userList, String groupName) throws Exception; 	
	
	//将批量用户从用户组中删除
	public boolean removeUsersFromGroup(List<String> userList, String groupName) throws Exception;
	
	//将某个用户从多个用户组中删除
	public boolean  removeUserFromGroups(String userId, List<String> groupNames) throws Exception;
	
	public boolean removeUserListFromGroupList(List<String> userList, List<String> groupList) throws Exception;
	
	//将某个用户加入到多个用户组中
	public boolean  addUser2Groups(String userId, List<String> groupNames) throws Exception;;
	
	public boolean addUserList2GroupList(List<String> userList, List<String> groupList) throws Exception;
	
	
	public void deleteUserFromGroups(String userId, List<String> groupNames);
	
//	public List<String> getGroupsBasedOnUserName(String userName) throws Exception;
	
}

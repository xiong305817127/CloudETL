package com.idatrix.unisecurity.freeipa.proxy.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.idatrix.unisecurity.freeipa.common.User;
import com.idatrix.unisecurity.freeipa.model.FreeIPATemplate;
import com.idatrix.unisecurity.freeipa.msg.response.BasicResponse;
import com.idatrix.unisecurity.freeipa.msg.resquest.LdapReqParamVO;
import com.idatrix.unisecurity.freeipa.proxy.IFreeIPAProxy;
import com.idatrix.unisecurity.freeipa.proxy.factory.LdapHttpDataBuilder;
import com.idatrix.unisecurity.ranger.usersync.process.LdapMgrUserGroupBuilder;

public class FreeIPAProxyImpl extends BaseService implements IFreeIPAProxy {
	private static final Logger logger = Logger.getLogger(FreeIPAProxyImpl.class);
	
	private LdapMgrUserGroupBuilder userSync ;

	public FreeIPAProxyImpl(FreeIPATemplate rjdata, LdapHttpDataBuilder builder, LdapMgrUserGroupBuilder userSync) {
		super(rjdata, builder);
		this.userSync = userSync;
	}


	/**
	 * 增加一个用户
	 */
	@Override
	public boolean addUser(String username, String password) throws Exception {
		logger.info("Begin to add user in freeipa. username = " + username);
		LdapReqParamVO addVO = ldapHttpDataBuilder.buildReqAddUser(username, password, username, password);
		//
		HttpPost httpPost = this.generateHttpPost(addVO);
		// 发送消息
		CloseableHttpResponse response = super.excute(httpPost);
		this.postExecuteHttp(response);
		// freeipa规定：新创建的用户必须要修改密码。
		this.changePasswd(username, password, password);
		
		if(this.userSync != null) {
			this.userSync.addOrUpdateUser(username);
		}
		
		return true;
	}

	/**
	 * 修改用户密码
	 */

	public boolean changeUserOwnPwd(String name, String oldPwd, String newPwd) throws Exception {
		this.changePasswd(name, oldPwd, newPwd);
		return true;
	}

	@Override
	public boolean deleteUser(String name) throws Exception {

		if (StringUtils.isEmpty(name)) {
			logger.error("Begin to delete user in freeipa. username = " + name);
			return false;
		}
		logger.info("Begin to delete user in freeipa. username = " + name);

		List<String> list = new ArrayList<String>();
		list.add(name);
		return this.deleteUserList(list);

	}

	@Override
	public boolean deleteUserList(List<String> userList) throws Exception {

		if (CollectionUtils.isEmpty(userList)) {
			logger.error("Fail to add user list in FreeIPA. Reason: the userlist is empty.");
			return false;
		}
		LdapReqParamVO groupVO = ldapHttpDataBuilder.buildReqDeleteUserList(userList);
		HttpPost httpPost = this.generateHttpPost(groupVO);
		CloseableHttpResponse response = super.excute(httpPost);
		String retStr = this.postExecuteHttp(response);

		return false;
	}

	@Override
	public boolean addGroup(String groupName, String description) throws Exception {
		if (StringUtils.isEmpty(groupName)) {
			logger.error("Fail to add group in FreeIPA. Reason: the group name is empty.");
			return false;
		}
		logger.info("Begin to add group in Freeip. groupName = " + groupName);

		List<String> list = new ArrayList<String>();
		list.add(groupName);

		return this.addGroupList(list);

	}

	@Override
	public boolean addGroupList(List<String> groupList) throws Exception {
		if (CollectionUtils.isEmpty(groupList)) {
			logger.error("Fail to add group list in FreeIPA. Reason: the group name is empty.");
			return false;
		}

		LdapReqParamVO groupVO = ldapHttpDataBuilder.buildReqAddGroupList(groupList, "test");
		HttpPost httpPost = this.generateHttpPost(groupVO);
		CloseableHttpResponse response = super.excute(httpPost);
		String retStr = this.postExecuteHttp(response);
		// logger.info(retStr);
		
		if(this.userSync != null) {
			for(int i = 0; i < groupList.size(); i++) {
				this.userSync.addOrUpdateGroup(groupList.get(i));
			}
		}
		
		return true;
	}

	@Override
	public boolean deleteGroup(String groupName) throws Exception {
		if (StringUtils.isEmpty(groupName)) {
			logger.error("Fail to delete group in FreeIPA. Reason: the group name is empty.");
			return false;
		}
		List<String> groupList = new ArrayList<String>();
		groupList.add(groupName);

		return this.deleteGroupList(groupList);
	}

	@Override
	public boolean deleteGroupList(List<String> groupList) throws Exception {
		// TODO Auto-generated method stub
		if (CollectionUtils.isEmpty(groupList)) {
			logger.error("Fail to delete group list in FreeIPA. Reason: the group name is empty.");
			return false;
		}
		LdapReqParamVO groupVO = ldapHttpDataBuilder.buildReqDeleleGroupList(groupList);
		HttpPost httpPost = this.generateHttpPost(groupVO);
		CloseableHttpResponse response = super.excute(httpPost);
		String retStr = this.postExecuteHttp(response);
		// logger.info(retStr);

		return false;
	}

	@Override
	public boolean addUsers2Group(List<String> userList, String groupName) throws Exception {
		if (CollectionUtils.isEmpty(userList)) {
			logger.error("Fail to add user to group in freeipa. Reason: the user list is empty.");
			return false;
		}
		if (StringUtils.isEmpty(groupName)) {
			logger.error("Fail to add user to group in freeipa. Reason: the groupName is empty.");
			return false;
		}

		List<String> groupList = new ArrayList<String>();
		groupList.add(groupName);

		return this.addUserList2GroupList(userList, groupList);

	}

	@Override
	public boolean removeUserFromGroups(String userId, List<String> groupNames) throws Exception {
		if (StringUtils.isEmpty(userId)) {
			logger.error("Fail to add user to group in freeipa. Reason: the username is empty.");
			return false;
		}
		if (CollectionUtils.isEmpty(groupNames)) {
			logger.error("Fail to add user to group in freeipa. Reason: the groupNames is empty.");
			return false;
		}
		List<String> userList = new ArrayList<String>();
		userList.add(userId);

		return this.removeUserListFromGroupList(userList, groupNames);

	}

	public boolean removeUsersFromGroup(List<String> userList, String groupName) throws Exception {
		if (CollectionUtils.isEmpty(userList)) {
			logger.error("Fail to add user to group in freeipa. Reason: the user list is empty.");
			return false;
		}
		if (StringUtils.isEmpty(groupName)) {
			logger.error("Fail to add user to group in freeipa. Reason: the groupName is empty.");
			return false;
		}

		List<String> groupList = new ArrayList<String>();
		groupList.add(groupName);

		return this.removeUserListFromGroupList(userList, groupList);

	}

	@Override
	public boolean removeUserListFromGroupList(List<String> userList, List<String> groupList) throws Exception {
		if (CollectionUtils.isEmpty(userList) || CollectionUtils.isEmpty(groupList)) {
			logger.error("Fail to remove userlist from grouplist. Reason: userlist or grouplist is empty.");
			return false;
		}

		LdapReqParamVO groupVO = ldapHttpDataBuilder.buildReqRemoveUserListFormGroupList(groupList, userList);
		HttpPost httpPost = this.generateHttpPost(groupVO);
		CloseableHttpResponse response = super.excute(httpPost);
		String retStr = this.postExecuteHttp(response);
		logger.info(retStr);

		return true;

	}

	private HttpPost generateHttpPost(LdapReqParamVO paramVO) throws UnsupportedEncodingException {
		if (paramVO == null) {
			return null;
		}
		logger.info("Begin to generate HTTP Post : " + gson.toJson(paramVO));
		StringEntity stringEntity = new StringEntity(gson.toJson(paramVO));
		stringEntity.setContentType("application/json;charset=UTF-8");
		HttpPost httpPost = new HttpPost(this.ipaTmpl.getOprUrl());
		httpPost.setEntity(stringEntity);

		return httpPost;
	}

	private String postExecuteHttp(CloseableHttpResponse response) throws Exception {
		HttpEntity responseEntity = response.getEntity();
		String retStr = EntityUtils.toString(responseEntity);

		// LdapUserQueryReqRtVO retVO = gson.fromJson(retStr,
		// LdapUserQueryReqRtVO.class);
		// if (null != retVO && retVO.getError() != null) {
		// logger.error(retVO);
		// throw new Exception(retVO.getError().toString());
		// }
		logger.info(retStr);
		logger.info("--------------------------------");
		BasicResponse responseVo = gson.fromJson(retStr, BasicResponse.class);

		logger.info("responese: \n " + responseVo);
		if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
			throw new Exception(response.getStatusLine().getReasonPhrase());
		}

		if (responseVo.getError() != null) {
			throw new Exception(responseVo.getError().toString());
		}

		return retStr;
	}

	@Override
	public boolean addUser2Groups(String userId, List<String> groupNames) throws Exception {
		if (StringUtils.isEmpty(userId)) {
			logger.error("Fail to add user to group in freeipa. Reason: the username is empty.");
			return false;
		}

		if (CollectionUtils.isEmpty(groupNames)) {
			logger.error("Fail to add user to group in freeipa. Reason: the groupName is empty.");
			return false;
		}

		List<String> list = new ArrayList<String>();
		list.add(userId);

		return this.addUserList2GroupList(list, groupNames);

	}

	@Override
	public void deleteUserFromGroups(String userId, List<String> groupNames) {
		// TODO Auto-generated stub

	}

	@Override
	public boolean addUserList(List<User> userList) throws Exception {
		if (CollectionUtils.isEmpty(userList)) {
			logger.error("Fail to add user list in FreeIPA. Reason: the userlist is empty.");
			return false;
		}
		LdapReqParamVO groupVO = ldapHttpDataBuilder.buildReqAddUserList(userList);
		HttpPost httpPost = this.generateHttpPost(groupVO);
		CloseableHttpResponse response = super.excute(httpPost);
		String retStr = this.postExecuteHttp(response);
		if(logger.isDebugEnabled()) {
			logger.debug(retStr);
		}
		
		if(this.userSync != null) {
			for(int i = 0; i < userList.size(); i++) {
				this.userSync.addOrUpdateUser(userList.get(i).getUsername());
			}
		}

		return true;
	}

	@Override
	public boolean addUserList2GroupList(List<String> userList, List<String> groupList) throws Exception {

		if (CollectionUtils.isEmpty(userList) || CollectionUtils.isEmpty(groupList)) {
			logger.error("Fail to add user list in FreeIPA. Reason: the userlist or grouplist is empty.");

			return false;
		}

		LdapReqParamVO groupVO = ldapHttpDataBuilder.buildReqAddGroupsMember(groupList, userList);
		HttpPost httpPost = this.generateHttpPost(groupVO);
		CloseableHttpResponse response = super.excute(httpPost);
		String retStr = this.postExecuteHttp(response);
		logger.info(retStr);

		return false;
	}

	
//	public List<String> getGroupsBasedOnUserName(String userName) throws Exception {
//		if(StringUtils.isEmpty(userName)) {
//			logger.error("Fail to find user. Reason: username is empty.");
//			return null;
//		}
//		
//		LdapReqParamVO groupVO = ldapHttpDataBuilder.buildReqFindUser(userName);
//		HttpPost httpPost = this.generateHttpPost(groupVO);
//		CloseableHttpResponse response = super.excute(httpPost);
//		String retStr = this.postExecuteHttp(response);
//
//		
//		logger.info(responseVo);
//		
//		
//		
//		return null;
//	}

}

package com.idatrix.unisecurity.ranger.usersync.process;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.idatrix.unisecurity.freeipa.common.RangerBasicInfoModel;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import org.apache.log4j.Logger;
import org.apache.ranger.unixusersync.model.*;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于同步freeipa的各个用户。原生的Ranger提供了一个Freeipa的同步进程，但是同步速度较慢。
 * 将Ranger中的同步代码抽取出来。现阶段Ranger 0.7.0的删除用户（组）是有问题，无法删除。因此 此代码也是不能够删除用户与组。
 * 
 * @author Administrator
 *
 */
public class LdapMgrUserGroupBuilder implements ILdapMgrUserGroupBuilder{
	private static final Logger LOG = Logger.getLogger(LdapMgrUserGroupBuilder.class);

	private static final String AUTHENTICATION_TYPE = "hadoop.security.authentication";
	private String AUTH_KERBEROS = "kerberos";
	private static final String PRINCIPAL = "ranger.usersync.kerberos.principal";
	private static final String KEYTAB = "ranger.usersync.kerberos.keytab";
	private static final String NAME_RULE = "hadoop.security.auth_to_local";

	public static final String PM_USER_LIST_URI = "/service/xusers/users/"; // GET
	private static final String PM_ADD_USER_GROUP_INFO_URI = "/service/xusers/users/userinfo"; // POST

	private static final String PM_ADD_GROUP_USER_INFO_URI = "/service/xusers/groups/groupinfo"; // POST

	public static final String PM_GROUP_LIST_URI = "/service/xusers/groups/"; // GET
	private static final String PM_ADD_GROUP_URI = "/service/xusers/groups/"; // POST

	public static final String PM_USER_GROUP_MAP_LIST_URI = "/service/xusers/groupusers/"; // GET

	public static final String PM_GET_GROUP_USER_MAP_LIST_URI = "/service/xusers/groupusers/groupName/${groupName}"; // GET

	private static final String PM_ADD_LOGIN_USER_URI = "/service/users/default"; // POST
	private static final String GROUP_SOURCE_EXTERNAL = "1";
	private static String LOCAL_HOSTNAME = "unknown";

	private static final String PM_DEL_USER_GROUP_LINK_URI = "/service/xusers/group/${groupName}/user/${userName}";

	private String policyMgrBaseUrl;
	private String username;
	private String password;

	private UserGroupInfo usergroupInfo = new UserGroupInfo();
	private GroupUserInfo groupuserInfo = new GroupUserInfo();

	public LdapMgrUserGroupBuilder(RangerBasicInfoModel basicInfo) {
		this.policyMgrBaseUrl = basicInfo.getHttpUrl();
		this.username = basicInfo.getUsername();
		this.password = basicInfo.getPasswd();
	}

	/**
	 * Delete接口， Ranger并没有实现，因此还不能使用
	 * 
	 * @param groupName
	 * @param userName
	 */
	@Deprecated
	public void delXGroupUserInfo(String groupName, String userName) {

		try {

			Client c = getClient();

			String uri = "/service/xusers/secure/users/delete?forceDelete=true";
			WebResource r = c.resource(getURL(uri));

			ClientResponse response = r.delete(ClientResponse.class);

			if (LOG.isDebugEnabled()) {
				LOG.debug("RESPONSE: [" + response.toString() + "]");
			}

		} catch (Exception e) {

			LOG.warn("ERROR: Unable to delete GROUP: " + groupName + " from USER:" + userName, e);
		}

	}

	public void addOrUpdateGroup(String groupName) throws Exception {
		this.addGroupInfo(groupName);
	}

	public void addOrUpdateUser(String userName) throws Exception {
		LOG.info("Begin to sync user " + userName + " to Ranger.");
		this.addOrUpdateUser(userName, new ArrayList<String>());
	}

	public void addOrUpdateUser(String userName, List<String> groups) throws Exception {
		if (addMUser(userName) == null) {
			String msg = "Failed to add portal user";
			LOG.error(msg);
			throw new Exception(msg);
		}
		if (addUserGroupInfo(userName, groups) == null) {
			String msg = "Failed to add addorUpdate user group info";
			LOG.error(msg);
			throw new Exception(msg);
		}
	}

	private UserGroupInfo addUserGroupInfo(String userName, List<String> groups) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("==> LdapPolicyMgrUserGroupBuilder.addUserGroupInfo " + userName + " and groups");

		UserGroupInfo ret = null;
		XUserInfo user = null;
		System.out.println("INFO: addPMXAUser(" + userName + ")");

		user = addXUserInfo(userName);

		for (String g : groups) {
			System.out.println("INFO: addPMXAGroupToUser(" + userName + "," + g + ")");
		}

		addXUserGroupInfo(user, groups);
		return getUsergroupInfo(ret);

	}

	private XUserInfo addXUserInfo(String aUserName) {

		XUserInfo xuserInfo = new XUserInfo();

		xuserInfo.setName(aUserName);

		xuserInfo.setDescription(aUserName + " - add from Unix box");

		usergroupInfo.setXuserInfo(xuserInfo);

		return xuserInfo;
	}

	private void addXUserGroupInfo(XUserInfo aUserInfo, List<String> aGroupList) {

		List<XGroupInfo> xGroupInfoList = new ArrayList<XGroupInfo>();

		for (String groupName : aGroupList) {
			XGroupInfo group = addXGroupInfo(groupName);
			xGroupInfoList.add(group);
			addXUserGroupInfo(aUserInfo, group);
		}

		usergroupInfo.setXgroupInfo(xGroupInfoList);
	}

	private XGroupInfo addXGroupInfo(String aGroupName) {

		XGroupInfo addGroup = new XGroupInfo();

		addGroup.setName(aGroupName);

		addGroup.setDescription(aGroupName + " - add from Unix box");

		addGroup.setGroupType("1");

		addGroup.setGroupSource(GROUP_SOURCE_EXTERNAL);
		groupuserInfo.setXgroupInfo(addGroup);

		return addGroup;
	}

	private XUserGroupInfo addXUserGroupInfo(XUserInfo aUserInfo, XGroupInfo aGroupInfo) {

		XUserGroupInfo ugInfo = new XUserGroupInfo();

		ugInfo.setUserId(aUserInfo.getId());

		ugInfo.setGroupName(aGroupInfo.getName());

		return ugInfo;
	}

	private MUserInfo addMUser(String aUserName) {
		MUserInfo ret = null;
		MUserInfo userInfo = new MUserInfo();

		userInfo.setLoginId(aUserName);
		userInfo.setFirstName(aUserName);
		userInfo.setLastName(aUserName);

		return getMUser(userInfo, ret);
	}

	private MUserInfo getMUser(MUserInfo userInfo, MUserInfo ret) {
		Client c = getClient();

		WebResource r = c.resource(getURL(PM_ADD_LOGIN_USER_URI));

		Gson gson = new GsonBuilder().create();

		String jsonString = gson.toJson(userInfo);

		String response = r.accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE)
				.post(String.class, jsonString);

		System.out.println("RESPONSE[" + response + "]");

		ret = gson.fromJson(response, MUserInfo.class);

		System.out.println("MUser Creation successful " + ret);

		return ret;

	}

	private UserGroupInfo getUsergroupInfo(UserGroupInfo ret) {
		Client c = getClient();

		WebResource r = c.resource(getURL(PM_ADD_USER_GROUP_INFO_URI));

		Gson gson = new GsonBuilder().create();

		String jsonString = gson.toJson(usergroupInfo);

		System.out.println("USER GROUP MAPPING" + jsonString);

		String response = r.accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE)
				.post(String.class, jsonString);

		System.out.println("RESPONSE: [" + response + "]");

		ret = gson.fromJson(response, UserGroupInfo.class);

		return ret;
	}

	private synchronized Client getClient() {
		Client ret = null;
		if (policyMgrBaseUrl.startsWith("https://")) {

		} else {
			ClientConfig cc = new DefaultClientConfig();
			cc.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
			ret = Client.create(cc);
		}

		if (ret != null) {
			if (username != null && password != null) {
				ret.addFilter(new HTTPBasicAuthFilter(username, password));
			}
		}

		return ret;
	}

	private String getURL(String uri) {
		String ret = null;
		ret = policyMgrBaseUrl + (uri.startsWith("/") ? uri : ("/" + uri));
		return ret;
	}

	private XGroupInfo addGroupInfo(final String groupName) {
		XGroupInfo ret = null;
		XGroupInfo group = null;

		LOG.debug("INFO: addPMXAGroup(" + groupName + ")");
		group = addXGroupInfo(groupName);

		return getAddedGroupInfo(group);

	}

	private XGroupInfo getAddedGroupInfo(XGroupInfo group) {
		XGroupInfo ret = null;

		Client c = getClient();

		WebResource r = c.resource(getURL(PM_ADD_GROUP_URI));

		Gson gson = new GsonBuilder().create();

		String jsonString = gson.toJson(group);

		LOG.debug("Group" + jsonString);

		String response = r.accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE)
				.post(String.class, jsonString);

		LOG.debug("RESPONSE: [" + response + "]");

		ret = gson.fromJson(response, XGroupInfo.class);

		return ret;
	}

	// public static void main(String[] args) {
	// LdapMgrUserGroupBuilder builder = new LdapMgrUserGroupBuilder();
	//// builder.delXGroupUserInfo("ipausers", "testfreeipa01");
	// long t1 = System.currentTimeMillis();
	// for(int i = 0 ; i < 1000; i++) {
	//
	// String username = "test_luochong_" + i;
	// System.out.println("----------- begin to add user " + username + "-------");
	// try {
	// builder.addOrUpdateUser(username);
	// } catch (Throwable e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// long t2 = System.currentTimeMillis();
	//
	// System.out.println("Cost time : " + (t2 - t1) /1000);
	//
	//
	// }
}

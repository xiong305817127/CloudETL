package com.idatrix.unisecurity.freeipa.proxy.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.idatrix.unisecurity.freeipa.common.User;
import com.idatrix.unisecurity.freeipa.msg.resquest.LdapReqParamVO;

@Component
public class LdapHttpDataBuilder implements ILdapHttpDataBuilder{

	private static final String version = "2.49";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LdapReqParamVO buildReqModifyUserPassword(String userName, String password, String old_password) {

		LdapReqParamVO reqVO = new LdapReqParamVO();
		List params = new ArrayList();
		reqVO.setParams(params);
		Map<String, String> paramVO = new HashMap<String, String>();
		paramVO.put("version", version);
		paramVO.put("password", password);
		paramVO.put("current_password", old_password);
		reqVO.setMethod("passwd");
		List<Object> listInfo = new ArrayList<Object>();
		params.add(listInfo);
		listInfo.add(userName);
		params.add(paramVO);
		return reqVO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LdapReqParamVO buildReqAddUser(String userName, String password, String sn, String givenname) {
		LdapReqParamVO reqVO = new LdapReqParamVO();
		List params = new ArrayList();
		reqVO.setParams(params);
		Map<String, String> paramVO = new HashMap<String, String>();

		paramVO.put("version", version);
		paramVO.put("userpassword", password);
		paramVO.put("sn", sn);
		paramVO.put("givenname", givenname);

		reqVO.setMethod("user_add");

		List<Object> listInfo = new ArrayList<Object>();
		params.add(listInfo);
		listInfo.add(userName);

		params.add(paramVO);

		return reqVO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LdapReqParamVO buildQueryUser(String userName) {
		LdapReqParamVO reqVO = new LdapReqParamVO();

		List params = new ArrayList();
		reqVO.setParams(params);
		Map<String, String> paramVO = new HashMap<String, String>();
		paramVO.put("version", version);
		paramVO.put("sizelimit", "0");
		paramVO.put("pkey_only", "true");
		reqVO.setMethod("user_find");
		List subParams = new ArrayList();
		subParams.add(userName);
		params.add(subParams);

		params.add(paramVO);

		return reqVO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LdapReqParamVO buildQueryUser(List<String> useNameList) {

		LdapReqParamVO reqVO = new LdapReqParamVO();
		List params = new ArrayList();
		reqVO.setParams(params);
		reqVO.setMethod("batch");
		List paramsList = new ArrayList();

		List subParams = new ArrayList();

		for (int i = 0; i < useNameList.size(); i++) {
			LdapReqParamVO subReqVO = new LdapReqParamVO();
			subReqVO.setMethod("user_show");
			List<Object> subList = new ArrayList<Object>();

			List<String> tempList = new ArrayList<String>();
			tempList.add(useNameList.get(i));
			subList.add(tempList);

			Map subMap = new HashMap();
			subMap.put("no_members", "true");
			subList.add(subMap);
			subReqVO.setParams(subList);
			subParams.add(subReqVO);
		}

		paramsList.add(subParams);
		paramsList.add(new HashMap());

		reqVO.setParams(paramsList);

		return reqVO;
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LdapReqParamVO buildReqAddGroupList(List<String> groupList, String desciption) {
		LdapReqParamVO reqVO = new LdapReqParamVO();
		List params = new ArrayList();
		reqVO.setMethod("batch");
		reqVO.setParams(params);

		// LdapReqParamVO中的params中的每个参数一定由两个参数组成一个list与一个map，第一个为list,后一个为Map
		List paramsList = new ArrayList();
		Map paramsMap = new HashMap();
		params.add(paramsList);
		params.add(paramsMap);

		for (int i = 0; i < groupList.size(); i++) {
			LdapReqParamVO subReqVO = new LdapReqParamVO();
			paramsList.add(subReqVO);

			subReqVO.setMethod("group_add");
			List subParams = new ArrayList();
			subReqVO.setParams(subParams);

			// 首先是一个list但是其中只包含了一个用户
			List<String> subGroup = new ArrayList<String>();
			subGroup.add(groupList.get(i));
			subParams.add(subGroup);
			// 接着是一个map
			Map subMap = new HashMap();
			subMap.put("description", "test");

			subParams.add(subMap);
		}

		return reqVO;
	}

	public LdapReqParamVO buildReqGroupIsExists(String group) {
		LdapReqParamVO reqVO = new LdapReqParamVO();
		List params = new ArrayList();
		Map<String, Object> mapVo = new HashMap<String, Object>();
		List<String> groupLst = new ArrayList<String>();
		params.add(groupLst);
		params.add(mapVo);
		groupLst.add(group);
		mapVo.put("version", version);
		reqVO.setMethod("group_show");
		reqVO.setParams(params);
		return reqVO;
	}

	@SuppressWarnings("uncheck")
	public LdapReqParamVO buildReqDeleleGroupList(List<String> groupList) {
		LdapReqParamVO reqVO = new LdapReqParamVO();
		List params = new ArrayList();
		reqVO.setMethod("batch");
		reqVO.setParams(params);

		// LdapReqParamVO中的params中的每个参数一定由两个参数组成一个list与一个map，第一个为list,后一个为Map
		List paramsList = new ArrayList();
		Map paramsMap = new HashMap();
		params.add(paramsList);
		params.add(paramsMap);

		for (int i = 0; i < groupList.size(); i++) {
			LdapReqParamVO subReqVO = new LdapReqParamVO();
			paramsList.add(subReqVO);

			subReqVO.setMethod("group_del");
			List subParams = new ArrayList();
			subReqVO.setParams(subParams);

			// 首先是一个list但是其中只包含了一个用户
			List<String> subGroup = new ArrayList<String>();
			subGroup.add(groupList.get(i));
			subParams.add(subGroup);
			// 接着是一个map
			Map subMap = new HashMap();
			subParams.add(subMap);
		}
		return reqVO;

	}

	@SuppressWarnings("uncheck")
	public LdapReqParamVO buildReqAddUserList(List<User> userList) {
		LdapReqParamVO reqVO = new LdapReqParamVO();
		List params = new ArrayList();
		reqVO.setMethod("batch");
		reqVO.setParams(params);

		// LdapReqParamVO中的params中的每个参数一定由两个参数组成一个list与一个map，第一个为list,后一个为Map
		List paramsList = new ArrayList();
		Map paramsMap = new HashMap();
		params.add(paramsList);
		params.add(paramsMap);

		for (int i = 0; i < userList.size(); i++) {
			LdapReqParamVO subReqVO = new LdapReqParamVO();
			paramsList.add(subReqVO);

			subReqVO.setMethod("user_add");
			List subParams = new ArrayList();
			subReqVO.setParams(subParams);

			// 首先是一个list但是其中只包含了一个用户
			List<String> subGroup = new ArrayList<String>();
			subGroup.add(userList.get(i).getUsername());
			subParams.add(subGroup);
			// 接着是一个map
			Map subMap = new HashMap();
			subParams.add(subMap);
			subMap.put("givenname", userList.get(i).getUsername());
			subMap.put("sn", userList.get(i).getUsername());
			subMap.put("userpassword", userList.get(i).getPassword());
		}
		return reqVO;

	}

	@SuppressWarnings("uncheck")
	public LdapReqParamVO buildReqDeleteUserList(List<String> userList) {
		LdapReqParamVO reqVO = new LdapReqParamVO();
		List params = new ArrayList();
		reqVO.setMethod("batch");
		reqVO.setParams(params);

		// LdapReqParamVO中的params中的每个参数一定由两个参数组成一个list与一个map，第一个为list,后一个为Map
		List paramsList = new ArrayList();
		Map paramsMap = new HashMap();
		params.add(paramsList);
		params.add(paramsMap);

		for (int i = 0; i < userList.size(); i++) {
			LdapReqParamVO subReqVO = new LdapReqParamVO();
			paramsList.add(subReqVO);

			subReqVO.setMethod("user_del");
			List subParams = new ArrayList();
			subReqVO.setParams(subParams);

			// 首先是一个list但是其中只包含了一个用户
			List<String> subGroup = new ArrayList<String>();
			subGroup.add(userList.get(i));
			subParams.add(subGroup);
			// 接着是一个map
			Map subMap = new HashMap();
			subParams.add(subMap);
		}
		return reqVO;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LdapReqParamVO buildReqAddGroupsMember(List<String> groups, List<String> userList) {
		LdapReqParamVO reqVO = new LdapReqParamVO();
		List params = new ArrayList();
		reqVO.setMethod("batch");
		reqVO.setParams(params);

		// LdapReqParamVO中的params中的每个参数一定由两个参数组成一个list与一个map，第一个为list,后一个为Map
		List paramsList = new ArrayList();
		Map paramsMap = new HashMap();
		params.add(paramsList);
		params.add(paramsMap);

		for (int i = 0; i < groups.size(); i++) {
			LdapReqParamVO subReqVO = new LdapReqParamVO();
			paramsList.add(subReqVO);

			subReqVO.setMethod("group_add_member");
			List subParams = new ArrayList();
			subReqVO.setParams(subParams);

			// 首先是一个list但是其中只包含了一个用户
			List<String> subGroup = new ArrayList<String>();
			subGroup.add(groups.get(i));
			subParams.add(subGroup);
			// 接着是一个map
			Map subUser = new HashMap();
			// for(int j = 0; j < userList.size(); j++){
			subUser.put("user", userList);

			// }
			subParams.add(subUser);
		}

		return reqVO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LdapReqParamVO buildReqRemoveUserListFormGroupList(List<String> groups, List<String> userList) {
		LdapReqParamVO reqVO = new LdapReqParamVO();
		List params = new ArrayList();
		reqVO.setMethod("batch");
		reqVO.setParams(params);

		// LdapReqParamVO中的params中的每个参数一定由两个参数组成一个list与一个map，第一个为list,后一个为Map
		List paramsList = new ArrayList();
		Map paramsMap = new HashMap();
		params.add(paramsList);
		params.add(paramsMap);

		for (int i = 0; i < groups.size(); i++) {
			LdapReqParamVO subReqVO = new LdapReqParamVO();
			paramsList.add(subReqVO);

			subReqVO.setMethod("group_remove_member");
			List subParams = new ArrayList();
			subReqVO.setParams(subParams);

			// 首先是一个list但是其中只包含了一个用户
			List<String> subGroup = new ArrayList<String>();
			subGroup.add(groups.get(i));
			subParams.add(subGroup);
			// 接着是一个map
			Map subUser = new HashMap();
			subUser.put("user", userList);

			subParams.add(subUser);
		}

		return reqVO;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LdapReqParamVO buildReqFindUser(String userName) {
		LdapReqParamVO reqVO = new LdapReqParamVO();
		List params = new ArrayList();
		
		reqVO.setMethod("user_show");
		reqVO.setParams(params);
		
		List<String> paramsList =  new ArrayList<String>();
		Map paramMap  =  new HashMap();
		params.add(paramsList);
		params.add(paramMap);
		
		
		paramsList.add(userName);
		
		

		return reqVO;
	}

}

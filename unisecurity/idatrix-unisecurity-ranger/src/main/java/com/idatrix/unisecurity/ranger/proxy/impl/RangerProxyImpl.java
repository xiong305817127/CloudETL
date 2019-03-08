package com.idatrix.unisecurity.ranger.proxy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.idatrix.unisecurity.ranger.common.RangerResourceType;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoFactory;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.Access;
import com.idatrix.unisecurity.ranger.proxy.IProxyRanger;

public class RangerProxyImpl implements IProxyRanger {
	private static final Logger logger = Logger.getLogger(RangerProxyImpl.class);

	private String hdfsRepositoryName;

	private String hbaseRepositoryName;

	private String hiveRepositoryName;

	private String yarnRepositoryName;

	private RangerPolicyProxyImpl rangerImpl = null;

	private Map<String, List<String>> accessMap = new HashMap<String, List<String>>();

	public RangerProxyImpl(String hdfsRepositoryName, String hbaseRepositoryName, String hiveRepositoryName,
			String yarnRepositoryName, RangerPolicyProxyImpl rangerImpl) {
		this.hdfsRepositoryName = hdfsRepositoryName;
		this.hbaseRepositoryName = hbaseRepositoryName;
		this.hiveRepositoryName = hiveRepositoryName;
		this.yarnRepositoryName = yarnRepositoryName;
		this.rangerImpl = rangerImpl;

		this.init();
	}

	public String getHdfsRepositoryName() {
		return hdfsRepositoryName;
	}

	public void setHdfsRepositoryName(String hdfsRepositoryName) {
		this.hdfsRepositoryName = hdfsRepositoryName;
	}

	public String getHbaseRepositoryName() {
		return hbaseRepositoryName;
	}

	public void setHbaseRepositoryName(String hbaseRepositoryName) {
		this.hbaseRepositoryName = hbaseRepositoryName;
	}

	public String getHiveRepositoryName() {
		return hiveRepositoryName;
	}

	public void setHiveRepositoryName(String hiveRepositoryName) {
		this.hiveRepositoryName = hiveRepositoryName;
	}

	public String getYarnRepositoryName() {
		return yarnRepositoryName;
	}

	public void setYarnRepositoryName(String yarnRepositoryName) {
		this.yarnRepositoryName = yarnRepositoryName;
	}

	public RangerPolicyProxyImpl getRangerImpl() {
		return rangerImpl;
	}

	public void setRangerImpl(RangerPolicyProxyImpl rangerImpl) {
		this.rangerImpl = rangerImpl;
	}

	public void init() {

		// hdfs权限设置
		List<String> hdfsAccess = new ArrayList<String>();
		hdfsAccess.add("read");
		hdfsAccess.add("write");
		hdfsAccess.add("execute");
		accessMap.put(RangerResourceType.HDFS_TYPE, hdfsAccess);
		// hive权限设置
		List<String> hiveAccess = new ArrayList<String>();
		// 一定要注意hive只有如下几种权限:select, update, create, drop, alter, index, lock, all
		hiveAccess.add("select");
		hiveAccess.add("update");
		hiveAccess.add("create");
		hiveAccess.add("drop");
		hiveAccess.add("alter");
		hiveAccess.add("index");
		hiveAccess.add("lock");
		accessMap.put(RangerResourceType.HIVE_TYPE, hiveAccess);

		// hbase权限设置
		// 一定要注意hbase只有如下几种权限:read, write, create, admin
		List<String> hbaseAccess = new ArrayList<String>();
		hbaseAccess.add("read");
		hbaseAccess.add("write");
		hbaseAccess.add("create");
		hbaseAccess.add("admin");
		accessMap.put(RangerResourceType.HBASE_TYPE, hbaseAccess);

		// yarn权限设置
		// 一定要注意yarn只有如下几种权限:submit-app, admin-queue
		List<String> yarnAccess = new ArrayList<String>();
		yarnAccess.add("submit-app");
		yarnAccess.add("admin-queue");
		accessMap.put(RangerResourceType.YARN_TYPE, hbaseAccess);

	}

	public boolean updateUserHdfs(String path, List<String> users, List<String> acclist) {
		if (this.checkUserGroups(users)) {
			logger.error("Fail to update Hdfs -- " + path + " access in Ranger. Reason: the users list is empty.");
			return false;
		}
		return this.updateHdfs(path, users, null, acclist);
	}

	public boolean updateGroupHdfs(String path, List<String> group, List<String> acclist) {
		if (CollectionUtils.isEmpty(group)) {
			logger.error("Fail to update Hdfs -- " + path + " access in Ranger. Reason: the groups list is empty.");
			return false;
		}
		return this.updateHdfs(path, null, group, acclist);
	}

	public boolean updateUserHive(String database, String table, List<String> users, List<String> list) {
		if (this.checkUserGroups(users)) {
			logger.error("Fail to update hive -- " + database + ":" + table
					+ " access in Ranger. Reason: the users list is empty.");
			return false;
		}
		return this.updateHive(database, table, users, null, list);
	}

	public boolean updateGroupHive(String database, String table, List<String> group, List<String> list) {
		if (CollectionUtils.isEmpty(group)) {
			logger.error("Fail to update hive -- " + database + ":" + table
					+ " access in Ranger. Reason: the groups list is empty.");
			return false;
		}
		return this.updateHive(database, table, null, group, list);

	}

	public boolean updateUserHBase(String table, List<String> users, List<String> list) {
		// TODO Auto-generated method stub
		if (CollectionUtils.isEmpty(users)) {
			logger.error("Fail to update hbase -- " + table + " access in Ranger. Reason: the users list is empty.");
			return false;
		}
		return this.updateHBase(table, users, null, list);
	}

	public boolean updateGroupHBase(String table, List<String> group, List<String> list) {
		if (CollectionUtils.isEmpty(group)) {
			logger.error("Fail to update hbase -- " + table + " access in Ranger. Reason: the group list is empty.");
			return false;
		}
		return this.updateHBase(table, null, group, list);
	}

	public boolean updateUserYarn(String queue, List<String> users, List<String> list) {
		
		if(CollectionUtils.isEmpty(users)) {
			logger.error("Fail to update yarn -- " + queue + " access in Ranger. Reason: the users list is empty.");
			return false;
		}
		return this.updateYarn(queue, users, null, list);
	}

	public boolean updateGroupYarn(String queue, List<String> groups, List<String> list) {
		if(CollectionUtils.isEmpty(groups)) {
			logger.error("Fail to update yarn -- " + queue + " access in Ranger. Reason: the groups list is empty.");
			return false;
		}
		return this.updateYarn(queue, null, groups, list);
	}

	private boolean checkAccess(List<Access> list, String type) {
		List<String> accessList = this.accessMap.get(type);

		for (int i = 0; i < list.size(); i++) {
			if (!accessList.contains(list.get(i).getType())) {
				logger.error("The access - " + list.get(i).getType() + " is not " + type + " right. Please check.");
				return false;
			}
		}

		return true;
	}

	private boolean checkUserGroups(List<String> usergroups) {
		return CollectionUtils.isEmpty(usergroups);
	}

	private List<Access> converList(List<String> list) {
		List<Access> accList = new ArrayList<Access>();
		if (CollectionUtils.isEmpty(list)) {
			return accList;
		}
		for (int i = 0; i < list.size(); i++) {
			Access acc = new Access();
			acc.setIsAllowed("true");
			acc.setType(list.get(i));
			accList.add(acc);
		}

		return accList;
	}

	private boolean updateHdfs(String path, List<String> users, List<String> group, List<String> accList) {
		List<Access> list = this.converList(accList);
		if (!this.checkAccess(list, RangerResourceType.HDFS_TYPE)) {
			return false;
		}
		// 生成相应的PolicyInfo
		PolicyInfoVO infoVO = PolicyInfoFactory.generateHdfsPolicy(this.hdfsRepositoryName, path, users, group, list);
		return this.updatePolicy(infoVO, RangerResourceType.HDFS_TYPE);
	}

	private boolean updateHive(String database, String table, List<String> users, List<String> group,
			List<String> accList) {
		List<Access> list = this.converList(accList);
		if (!this.checkAccess(list, RangerResourceType.HIVE_TYPE)) {
			return false;
		}
		// 生成相应的PolicyInfo
		PolicyInfoVO infoVO = PolicyInfoFactory.generateHivePolicy(this.hiveRepositoryName, database, table, list,
				users, group);

		return this.updatePolicy(infoVO, RangerResourceType.HIVE_TYPE);
	}

	private boolean updateHBase(String table, List<String> users, List<String> group, List<String> accList) {
		List<Access> list = this.converList(accList);
		if (!this.checkAccess(list, RangerResourceType.HBASE_TYPE)) {
			return false;
		}
		// 生成相应的PolicyInfo
		PolicyInfoVO infoVO = PolicyInfoFactory.generateHBasePolicy(this.hbaseRepositoryName, table, list, users,
				group);

		return this.updatePolicy(infoVO, RangerResourceType.HBASE_TYPE);
	}
	
	private boolean updateYarn(String queue, List<String> users, List<String> group, List<String> accList) {
		List<Access> list = this.converList(accList);
		if (!this.checkAccess(list, RangerResourceType.YARN_TYPE)) {
			return false;
		}
		// 生成相应的PolicyInfo
		PolicyInfoVO infoVO = PolicyInfoFactory.generateYarnPolicy(this.yarnRepositoryName, queue, list, users, group);

		return this.updatePolicy(infoVO, RangerResourceType.YARN_TYPE);
	}

	private boolean updatePolicy(PolicyInfoVO infoVO, String type) {
		if (null == infoVO) {
			return false;
		}
		logger.info("Begin to update " + infoVO);
		try {
			rangerImpl.addPolicy(infoVO, type);
		} catch (Exception e) {
			logger.error("Fail to update " + type + " access in Ranger", e);
			return false;
		}
		return true;
	}
}

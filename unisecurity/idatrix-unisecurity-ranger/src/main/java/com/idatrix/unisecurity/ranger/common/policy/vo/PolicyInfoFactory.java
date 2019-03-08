package com.idatrix.unisecurity.ranger.common.policy.vo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.idatrix.unisecurity.ranger.common.RangerResourceType;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.Access;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.PolicyItem;
import com.idatrix.unisecurity.ranger.proxy.ProxyInfoFactory;

public class PolicyInfoFactory {

	public static String getStringDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	public static PolicyInfoVO generateYarnPolicy(String repositoryName, String queue, List<Access> accessList,
			List<String> user, List<String> groups) {
		PolicyInfoVO policyInfoVO = PolicyInfoFactory.generateHeadPolicy(repositoryName, RangerResourceType.YARN_TYPE);

		/*****************************
		 * Begin 设置需要控制的yarn目录
		 ***************************************************/
		Map<String, PolicyInfoVO.Resource> resources = new HashMap<String, PolicyInfoVO.Resource>();

		PolicyInfoVO.Resource resource = new PolicyInfoVO.Resource();
		// 这里的key为"queue" 固定的
		resources.put(RangerResourceType.YARN_MAP_KEY, resource);
		// 这里的values，就是对应的yarn
		List<String> listValues = new ArrayList<String>();

		listValues.add(queue);
		resource.setValues(listValues);
		// 默认为false，直接写死算了
		resource.setIsExcludes("false");
		// 是否需要进行目录递归
		resource.setIsRecursive("false");

		policyInfoVO.setResources(resources);

		/*********************** 设置权限 *********************************/

		PolicyItem item = PolicyInfoFactory.generatePolicyItem(accessList, user, groups);
		List<PolicyItem> policyItems = new ArrayList<PolicyItem>();
		policyItems.add(item);
		policyInfoVO.setPolicyItems(policyItems);
		return policyInfoVO;
	}

	public static PolicyInfoVO generateHBasePolicy(String repositoryName, String table, List<Access> accessList,
			List<String> users, List<String> groups) {
		PolicyInfoVO policyInfoVO = PolicyInfoFactory.generateHeadPolicy(repositoryName, RangerResourceType.HBASE_TYPE);
		Map<String, PolicyInfoVO.Resource> resources = new HashMap<String, PolicyInfoVO.Resource>();
		policyInfoVO.setResources(resources);

		/*****************************
		 * Begin 设置需要控制的hbase目录
		 ***************************************************/
		PolicyInfoVO.Resource tableResouce = new PolicyInfoVO.Resource();
		resources.put(RangerResourceType.HBASE_MAP_TABLE_KEY, tableResouce);
		List<String> values = new ArrayList<String>();
		values.add(table);
		tableResouce.setValues(values);
		tableResouce.setIsExcludes("false");
		tableResouce.setIsRecursive("false");

		// 对于列簇，与列，现阶段不做控制
		PolicyInfoVO.Resource columnfamily = new PolicyInfoVO.Resource();
		resources.put(RangerResourceType.HBASE_MAP_FAMILY_KEY, columnfamily);
		values = new ArrayList<String>();
		values.add("*");
		columnfamily.setValues(values);
		columnfamily.setIsExcludes("false");
		columnfamily.setIsRecursive("false");

		PolicyInfoVO.Resource column = new PolicyInfoVO.Resource();
		resources.put(RangerResourceType.HBASE_MAP_COLUMN_KEY, column);
		values = new ArrayList<String>();
		values.add("*");
		column.setValues(values);
		column.setIsExcludes("false");
		column.setIsRecursive("false");

		/*****************************
		 * End 设置需要控制的hbase目录
		 ***************************************************/

		PolicyItem item = PolicyInfoFactory.generatePolicyItem(accessList, users, groups);
		List<PolicyItem> policyItems = new ArrayList<PolicyItem>();
		policyItems.add(item);
		policyInfoVO.setPolicyItems(policyItems);

		return policyInfoVO;
	}

	public static PolicyInfoVO generateHivePolicy(String repositoryName, String database, String table,
			List<Access> accessList, List<String> user, List<String> groups) {
		PolicyInfoVO policyInfoVO = PolicyInfoFactory.generateHeadPolicy(repositoryName, RangerResourceType.HIVE_TYPE);
		Map<String, PolicyInfoVO.Resource> resources = new HashMap<String, PolicyInfoVO.Resource>();
		policyInfoVO.setResources(resources);

		/*****************************
		 * Begin 设置需要控制的hive目录
		 ***************************************************/
		PolicyInfoVO.Resource databaseResource = new PolicyInfoVO.Resource();
		resources.put(RangerResourceType.HIVE_MAP_DATABASE_KEY, databaseResource);
		List<String> values = new ArrayList<String>();
		values.add(database);
		databaseResource.setValues(values);
		databaseResource.setIsExcludes("false");
		databaseResource.setIsRecursive("false");

		// 对于列簇，与列，现阶段不做控制
		PolicyInfoVO.Resource tableResource = new PolicyInfoVO.Resource();
		resources.put(RangerResourceType.HIVE_MAP_TABLE_KEY, tableResource);
		values = new ArrayList<String>();
		values.add(table);
		tableResource.setValues(values);
		tableResource.setIsExcludes("false");
		tableResource.setIsRecursive("false");

		PolicyInfoVO.Resource columnResouce = new PolicyInfoVO.Resource();
		resources.put(RangerResourceType.HIVE_MAP_COLUMN_KEY, columnResouce);
		values = new ArrayList<String>();
		values.add("*");
		columnResouce.setValues(values);
		columnResouce.setIsExcludes("false");
		columnResouce.setIsRecursive("false");

		/*****************************
		 * End 设置需要控制的hive目录
		 ***************************************************/

		/*********************** 设置权限 *********************************/

		PolicyItem item = PolicyInfoFactory.generatePolicyItem(accessList, user, groups);
		List<PolicyItem> policyItems = new ArrayList<PolicyItem>();
		policyItems.add(item);
		policyInfoVO.setPolicyItems(policyItems);
		return policyInfoVO;
	}

	public static PolicyInfoVO generateHdfsPolicy(String repository, String resouce, List<String> users,
			List<String> groups, List<Access> access) {
		PolicyInfoVO policyInfoVO = generateHeadPolicy(repository, RangerResourceType.HDFS_TYPE);

		Map<String, PolicyInfoVO.Resource> resources = new HashMap<String, PolicyInfoVO.Resource>();
		policyInfoVO.setResources(resources);

		/*****************************
		 * Begin 设置需要控制的hdfs目录
		 ***************************************************/
		PolicyInfoVO.Resource path = new PolicyInfoVO.Resource();
		// 这里的key为"path" 固定的
		resources.put(RangerResourceType.HDFS_MAP_KEY, path);
		// 这里的values，就是对应的hdfs的目录
		List<String> values = new ArrayList<String>();
		path.setValues(values);
		// 默认为false，直接写死算了
		path.setIsExcludes("false");
		// 是否需要进行目录递归
		path.setIsRecursive("false");
		// 这里就是需要控制的hdfs目,如果有多个，就分别add进去。但是不建议一个policy有多个目录
		values.add(resouce);
		/*****************************
		 * End 设置需要控制的hdfs目录
		 ***************************************************/

		List<PolicyItem> policyItems = new ArrayList<PolicyItem>();

		PolicyItem item = generatePolicyItem(access, users, groups);
		policyItems.add(item);

		policyInfoVO.setPolicyItems(policyItems);

		return policyInfoVO;
	}

	private static PolicyItem generatePolicyItem(List<Access> access, List<String> users, List<String> groups) {
		PolicyItem item = new PolicyItem();
		item.setAccesses(access);
		item.setUsers(users);
		item.setGroups(groups);
		return item;
	}

	private static PolicyItem generatePolicyItem(String user, String group, List<Access> accesses) {
		PolicyItem item = new PolicyItem();
		item.setAccesses(accesses);
		List<String> groups = new ArrayList<String>();
		if (StringUtils.isNotEmpty(group)) {
			groups.add(group);
		}
		List<String> users = new ArrayList<String>();
		if (StringUtils.isNotEmpty(user)) {
			users.add(user);
		}
		item.setGroups(groups);
		item.setUsers(users);
		return item;
	}

	private static PolicyInfoVO generateHeadPolicy(String repository, String type) {
		PolicyInfoVO policyInfoVO = new PolicyInfoVO();
		// 设置repository名称,这个名称必须是存在的，否则就会抛出异常
		Random r = new Random();
//		r.setSeed(100);
		policyInfoVO.setService(repository);
		// 设置policy名称,这个名称随便设置，由用户自己写的,但是这个名称不能够重复

		policyInfoVO.setName(type + "_" + PolicyInfoFactory.getStringDateShort() + "_" + r.nextInt(100));

		// 是否加入审计，默认都是加入的,这是固定的
		policyInfoVO.setIsAuditEnabled("true");
		// 将此策略设置为enable, 固定的
		policyInfoVO.setIsEnabled("true");

		policyInfoVO.setResourceType(type);

		return policyInfoVO;
	}

}

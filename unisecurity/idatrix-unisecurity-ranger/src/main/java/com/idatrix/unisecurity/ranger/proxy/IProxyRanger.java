package com.idatrix.unisecurity.ranger.proxy;

import java.util.List;

import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.Access;

public interface IProxyRanger {
	public boolean updateUserHdfs(String path, List<String> users, List<String> accList);
	public boolean updateGroupHdfs(String path, List<String> group, List<String> list);
	
	public boolean updateUserHive(String database,  String table, List<String> users, List<String> list);
	public boolean updateGroupHive(String database, String table, List<String> group, List<String> list);
	

	public boolean updateUserHBase(String table,  List<String> users, List<String> list);
	public boolean updateGroupHBase( String table, List<String> group, List<String> list);
	

	public boolean updateUserYarn(String queue,  List<String> users, List<String> list);
	public boolean updateGroupYarn( String queue, List<String> group, List<String> list);
	
}

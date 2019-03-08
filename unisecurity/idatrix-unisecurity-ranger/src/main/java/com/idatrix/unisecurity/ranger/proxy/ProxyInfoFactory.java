package com.idatrix.unisecurity.ranger.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.idatrix.unisecurity.ranger.common.RangerResourceType;
import com.idatrix.unisecurity.ranger.common.RangerStaticInfo;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.Access;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.PolicyItem;
import com.idatrix.unisecurity.ranger.main.TestMain;
import com.idatrix.unisecurity.ranger.proxy.impl.RangerPolicyProxyImpl;

/**
 * 
 * @author Administrator
 * 用于生成PolicyInfoVO
 */
public class ProxyInfoFactory {
    public static Random r = new Random(100);
    
    
    public static PolicyInfoVO generateHBasePolicy(String repositoryName, String table, List<Access> accessList, List<String> users, List<String> groups) {
		PolicyInfoVO policyInfoVO = ProxyInfoFactory.generateHeadPolicy(repositoryName, RangerResourceType.HBASE_TYPE);
	    Map<String, PolicyInfoVO.Resource> resources = new HashMap<String, PolicyInfoVO.Resource>();
        policyInfoVO.setResources(resources);
        
        /*****************************Begin 设置需要控制的hbase目录***************************************************/
        PolicyInfoVO.Resource tableResouce = new PolicyInfoVO.Resource();
        resources.put(RangerResourceType.HBASE_MAP_TABLE_KEY, tableResouce);
        List<String> values = new ArrayList<String>();
        values.add(table);
        tableResouce.setValues(values);
        tableResouce.setIsExcludes("false");
        tableResouce.setIsRecursive("false");
        
        //对于列簇，与列，现阶段不做控制
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
        
        
        /*****************************End 设置需要控制的hbase目录***************************************************/
        
        PolicyItem item =  ProxyInfoFactory.generatePolicyItem(users, groups, accessList);
        List<PolicyItem> policyItems = new ArrayList<PolicyItem>();
        policyItems.add(item);
        policyInfoVO.setPolicyItems(policyItems);
       
        return policyInfoVO;
    }
    

    
	public static PolicyInfoVO generateHDFSPolicy( String repositoryName, String path1, List<Access> accessList, String user, String groups) {
		PolicyInfoVO policyInfoVO = ProxyInfoFactory.generateHeadPolicy(repositoryName, RangerResourceType.HDFS_TYPE);
		
        /*****************************Begin 设置需要控制的hdfs目录***************************************************/
        Map<String, PolicyInfoVO.Resource> resources = new HashMap<String, PolicyInfoVO.Resource>();

        PolicyInfoVO.Resource path = new PolicyInfoVO.Resource();
        //这里的key为"path"  固定的
        resources.put(RangerResourceType.HDFS_MAP_KEY, path);
        //这里的values，就是对应的hdfs的目录
        List<String> values = new ArrayList<String>();
        path.setValues(values);
        //默认为false，直接写死算了
        path.setIsExcludes("false");
        //是否需要进行目录递归
        path.setIsRecursive("false");
        //这里就是需要控制的hdfs目,如果有多个，就分别add进去。但是不建议一个policy有多个目录
        values.add(path1);
        
        policyInfoVO.setResources(resources);
        
        /***********************设置权限*********************************/
        
        PolicyItem item =  ProxyInfoFactory.generatePolicyItem(user, groups, accessList);
        List<PolicyItem> policyItems = new ArrayList<PolicyItem>();
        policyItems.add(item);
        policyInfoVO.setPolicyItems(policyItems);  
		return policyInfoVO;
	}
	
	
	public static PolicyInfoVO generateHdfsPolicy( String repositoryName, String queue, List<Access> accessList, String user, String groups) {
		PolicyInfoVO policyInfoVO = ProxyInfoFactory.generateHeadPolicy(repositoryName, RangerResourceType.YARN_TYPE);
		
        /*****************************Begin 设置需要控制的hdfs目录***************************************************/
        Map<String, PolicyInfoVO.Resource> resources = new HashMap<String, PolicyInfoVO.Resource>();

        PolicyInfoVO.Resource resource = new PolicyInfoVO.Resource();
        //这里的key为"queue"  固定的
        resources.put(RangerResourceType.YARN_MAP_KEY, resource);
        //这里的values，就是对应的yarn
        List<String> listValues = new ArrayList<String>();
        
        listValues.add(queue);
        resource.setValues(listValues);
        //默认为false，直接写死算了
        resource.setIsExcludes("false");
        //是否需要进行目录递归
        resource.setIsRecursive("false");
   
        policyInfoVO.setResources(resources);
        
        /***********************设置权限*********************************/
        
        PolicyItem item =  ProxyInfoFactory.generatePolicyItem(user, groups, accessList);
        List<PolicyItem> policyItems = new ArrayList<PolicyItem>();
        policyItems.add(item);
        policyInfoVO.setPolicyItems(policyItems);  
		return policyInfoVO;
	}
	
	public static PolicyInfoVO generateHivePolicy( String repositoryName, String database, String table, List<Access> accessList, String user, String groups) {
		PolicyInfoVO policyInfoVO = ProxyInfoFactory.generateHeadPolicy(repositoryName, RangerResourceType.HIVE_TYPE);
	    Map<String, PolicyInfoVO.Resource> resources = new HashMap<String, PolicyInfoVO.Resource>();
        policyInfoVO.setResources(resources);
        
        /*****************************Begin 设置需要控制的hive目录***************************************************/
        PolicyInfoVO.Resource databaseResource = new PolicyInfoVO.Resource();
        resources.put(RangerResourceType.HIVE_MAP_DATABASE_KEY, databaseResource);
        List<String> values = new ArrayList<String>();
        values.add(database);
        databaseResource.setValues(values);
        databaseResource.setIsExcludes("false");
        databaseResource.setIsRecursive("false");
        
        //对于列簇，与列，现阶段不做控制
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
        
        
        /*****************************End 设置需要控制的hive目录***************************************************/
        
        /***********************设置权限*********************************/
        
        PolicyItem item =  ProxyInfoFactory.generatePolicyItem(user, groups, accessList);
        List<PolicyItem> policyItems = new ArrayList<PolicyItem>();
        policyItems.add(item);
        policyInfoVO.setPolicyItems(policyItems);  
		return policyInfoVO;
	}
	
	private static PolicyItem generatePolicyItem(List<String> users, List<String> groups, List<Access> accesses) {
		PolicyItem item =  new PolicyItem();
		item.setAccesses(accesses);
		item.setGroups(groups);
		item.setUsers(users);
		return item;
	}
	
	private static PolicyItem generatePolicyItem(String user, String group, List<Access> accesses) {
		PolicyItem item =  new PolicyItem();
		item.setAccesses(accesses);
		List<String> groups = new ArrayList<String>();
		if(StringUtils.isNotEmpty(group))
		{
			groups.add(group);
		}
		List<String> users = new ArrayList<String>();
		if(StringUtils.isNotEmpty(user)) {
			users.add(user);
		}
		item.setGroups(groups);
		item.setUsers(users);
		return item;
	}
	
	
    private static PolicyInfoVO generateHeadPolicy(String repositoryName, String type) {
		PolicyInfoVO policyInfoVO = new PolicyInfoVO();
		policyInfoVO.setService(repositoryName);
		policyInfoVO.setName(RangerStaticInfo.POLICY_NAME_PRE  + type+ "_" + System.currentTimeMillis());
        //是否加入审计，默认都是加入的,这是固定的
        policyInfoVO.setIsAuditEnabled("true");
        //将此策略设置为enable, 固定的
        policyInfoVO.setIsEnabled("true");
        
        policyInfoVO.setResourceType(type);
        return policyInfoVO;
    }
	
	    
//	    
//	    public static PolicyInfoVO  buildHdfsPolicy(){
//	        PolicyInfoVO policyInfoVO = TestMain.buildPolicy("lcCluster_hadoop");
//	        policyInfoVO.setResourceType(RangerResourceType.HDFS_TYPE);
//	       
//	        Map<String, PolicyInfoVO.Resource> resources = new HashMap<String, PolicyInfoVO.Resource>();
//	        policyInfoVO.setResources(resources);
//	        
//	        /*****************************Begin 设置需要控制的hdfs目录***************************************************/
//	        PolicyInfoVO.Resource path = new PolicyInfoVO.Resource();
//	        //这里的key为"path"  固定的
//	        resources.put("path", path);
//	        //这里的values，就是对应的hdfs的目录
//	        List<String> values = new ArrayList<String>();
//	        path.setValues(values);
//	        //默认为false，直接写死算了
//	        path.setIsExcludes("false");
//	        //是否需要进行目录递归
//	        path.setIsRecursive("false");
//	        //这里就是需要控制的hdfs目,如果有多个，就分别add进去。但是不建议一个policy有多个目录
//	        values.add("/user");
//	        /*****************************End  设置需要控制的hdfs目录***************************************************/
//	        
//	        
//	        List<PolicyItem> policyItems = new ArrayList<PolicyItem>();
//	        policyInfoVO.setPolicyItems(policyItems);
//	        PolicyItem plicyItem = new PolicyItem();
//	        policyItems.add(plicyItem);
//	        
//	        /***********************************Begin 设置HDFS读写权限********************************************************************/
//	        //一定要注意hdfs只有如下几种权限:read, write, execute 这三种权限
//	        List<Access> accesses = new ArrayList<Access>();
//	        plicyItem.setAccesses(accesses);
//	        //设置对应的read权限
//	        Access sccess = new Access();
//	        sccess.setIsAllowed("true");
//	        sccess.setType("read");
//	        accesses.add(sccess);
//	        //设置对应的write权限
//	        sccess = new Access();
//	        sccess.setIsAllowed("true");
//	        sccess.setType("write");
//	        accesses.add(sccess);
//	        //设置对应的execute权限
////	        sccess = new Access();
////	        sccess.setIsAllowed("true");
////	        sccess.setType("execute");
////	        accesses.add(sccess);
//	        /***********************************End 设置HDFS读写权限*************************************************/
//
//	        /*************************Begin 设置用户名**************************************************/
//	        List<String> users = new ArrayList<String>();
//	        plicyItem.setUsers(users);
//	        users.add("user02");
//	        
//	        /*************************End 设置用户名**************************************************/
//	        return policyInfoVO;
//	    }
//	    
//	    
//	    
//	    public static PolicyInfoVO  buildHBasePolicy(){
//	        PolicyInfoVO policyInfoVO = TestMain.buildPolicy("szdev_hbase");
//	        policyInfoVO.setResourceType(RangerResourceType.HBASE_TYPE);
//
//	        Map<String, PolicyInfoVO.Resource> resources = new HashMap<String, PolicyInfoVO.Resource>();
//	        policyInfoVO.setResources(resources);
//	        
//	        /*****************************Begin 设置需要控制的hbase目录***************************************************/
//	        PolicyInfoVO.Resource table = new PolicyInfoVO.Resource();
//	        resources.put("table", table);
//	        List<String> values = new ArrayList<String>();
//	        values.add("test1");
//	        table.setValues(values);
//	        table.setIsExcludes("false");
//	        table.setIsRecursive("false");
//	        
//	        
//	        PolicyInfoVO.Resource columnfamily = new PolicyInfoVO.Resource();
//	        resources.put("column-family", columnfamily);
//	        values = new ArrayList<String>();
//	        values.add("*");
//	        columnfamily.setValues(values);
//	        columnfamily.setIsExcludes("false");
//	        columnfamily.setIsRecursive("false");      
//	        
//	        PolicyInfoVO.Resource column = new PolicyInfoVO.Resource();
//	        resources.put("column", column);
//	        values = new ArrayList<String>();
//	        values.add("*");
//	        column.setValues(values);
//	        column.setIsExcludes("false");
//	        column.setIsRecursive("false");    
//	        /*****************************End 设置需要控制的hbase目录***************************************************/
//
//	        
//	        List<PolicyItem> policyItems = new ArrayList<PolicyItem>();
//	        policyInfoVO.setPolicyItems(policyItems);
//	        PolicyItem plicyItem = new PolicyItem();
//	        policyItems.add(plicyItem);
//	        
//	        /***********************************Begin 设置HBase 权限********************************************************************/
//	        //一定要注意hbase只有如下几种权限:read, write, create, admin 
//	        List<Access> accesses = new ArrayList<Access>();
//	        plicyItem.setAccesses(accesses);
//	        //设置对应的read权限
//	        Access sccess = new Access();
//	        sccess.setIsAllowed("true");
//	        sccess.setType("read");
//	        accesses.add(sccess);
//	        //设置对应的write权限
//	        sccess = new Access();
//	        sccess.setIsAllowed("true");
//	        sccess.setType("write");
//	        accesses.add(sccess);
//	        //设置对应的create权限
//	        sccess = new Access();
//	        sccess.setIsAllowed("true");
//	        sccess.setType("create");
//	        accesses.add(sccess);
//	        /***********************************End 设置hbase读写权限*************************************************/
//	 
//	        /*************************Begin 设置用户名**************************************************/
//	        List<String> users = new ArrayList<String>();
//	        plicyItem.setUsers(users);
//	        users.add("user03");
//	        
//	        /*************************End 设置用户名**************************************************/
//	        
//	        return policyInfoVO;
//
//	    }
//	    
//	    public static PolicyInfoVO  buildHivePolicy(){
//	        PolicyInfoVO policyInfoVO = TestMain.buildPolicy("szdev_hive");
//	        policyInfoVO.setResourceType(RangerResourceType.HIVE_TYPE);
//
//	        Map<String, PolicyInfoVO.Resource> resources = new HashMap<String, PolicyInfoVO.Resource>();
//	        policyInfoVO.setResources(resources);
//	        
//	        /*****************************Begin 设置需要控制的hive 资源***************************************************/
//	        // database 的名称
//	        PolicyInfoVO.Resource database = new PolicyInfoVO.Resource();
//	        resources.put("database", database); //这个key值是固定的
//	        List<String> values = new ArrayList<String>();
//	        values.add("default"); //数据库的名称
//	        database.setValues(values);
//	        database.setIsExcludes("false");
//	        database.setIsRecursive("false");
//	        
//	        //table名称
//	        PolicyInfoVO.Resource tables = new PolicyInfoVO.Resource();
//	        resources.put("table", tables); //这个key值是固定的
//	        values = new ArrayList<String>();
//	        values.add("persons");
//	        tables.setValues(values);
//	        tables.setIsExcludes("false");
//	        tables.setIsRecursive("false");
//	        
//	        //column名称
//	        PolicyInfoVO.Resource columns = new PolicyInfoVO.Resource();
//	        resources.put("column", columns);
//	        values = new ArrayList<String>();
//	        values.add("*");
//	        columns.setValues(values);
//	        columns.setIsExcludes("false");
//	        columns.setIsRecursive("false");      
//	        /*****************************End 设置需要控制的hive 资源***************************************************/
//
//	        
//	        List<PolicyItem> policyItems = new ArrayList<PolicyItem>();
//	        policyInfoVO.setPolicyItems(policyItems);
//	        PolicyItem plicyItem = new PolicyItem();
//	        policyItems.add(plicyItem);
//	        
//	        /***********************************Begin 设置hive 权限********************************************************************/
//	        //一定要注意hive只有如下几种权限:select, update, create, drop, alter, index, lock, all
//	        List<Access> accesses = new ArrayList<Access>();
//	        plicyItem.setAccesses(accesses);
//	        //设置对应的read权限
//	        Access sccess = new Access();
//	        sccess.setIsAllowed("true");
//	        sccess.setType("select");
//	        accesses.add(sccess);
//	        //设置对应的write权限
//	        sccess = new Access();
//	        sccess.setIsAllowed("true");
//	        sccess.setType("update");
//	        accesses.add(sccess);
//	        //设置对应的create权限
//	        sccess = new Access();
//	        sccess.setIsAllowed("true");
//	        sccess.setType("create");
//	        accesses.add(sccess);
//	        /***********************************End 设置hbase读写权限*************************************************/
//	        
//	        
//	        /*************************Begin 设置用户名**************************************************/
//	        List<String> users = new ArrayList<String>();
//	        plicyItem.setUsers(users);
//	        users.add("user03");
//	        
//	        /*************************End 设置用户名**************************************************/ 
//	        
//	        return policyInfoVO;
//
//	    }
//	    
//	    
//	    public static PolicyInfoVO  buildYarnPolicy(){
//	        PolicyInfoVO policyInfoVO = TestMain.buildPolicy("lcCluster_yarn");
//	        policyInfoVO.setResourceType(RangerResourceType.YARN_TYPE);
//
//	        Map<String, PolicyInfoVO.Resource> resources = new HashMap<String, PolicyInfoVO.Resource>();
//	        policyInfoVO.setResources(resources);
//	        /*****************************Begin 设置需要控制的yarn的队列 资源***************************************************/
//	        // 队列 的名称
//	        PolicyInfoVO.Resource queue = new PolicyInfoVO.Resource();
//	        resources.put("queue", queue); //这个key值是固定的
//	        List<String> values = new ArrayList<String>();
//	        values.add("que1"); //数据库的名称
//	        queue.setValues(values);
//	        queue.setIsExcludes("false");
//	        queue.setIsRecursive("false");
//	        
//	        /*****************************End 设置需要控制的yarn的队列 资源***************************************************/     
//	        
//	        List<PolicyItem> policyItems = new ArrayList<PolicyItem>();
//	        policyInfoVO.setPolicyItems(policyItems);
//	        PolicyItem plicyItem = new PolicyItem();
//	        policyItems.add(plicyItem);
//	        
//	        
//	        /***********************************Begin 设置yarn 权限********************************************************************/
//	        //一定要注意yarn只有如下几种权限:submit-app, admin-queue
//	        List<Access> accesses = new ArrayList<Access>();
//	        plicyItem.setAccesses(accesses);
//	        //设置对应的submit-app权限
//	        Access sccess = new Access();
//	        sccess.setIsAllowed("true");
//	        sccess.setType("submit-app");
//	        accesses.add(sccess);
//	        //设置对应的admin-queue权限
//	        sccess = new Access();
//	        sccess.setIsAllowed("true");
//	        sccess.setType("admin-queue");
//	        accesses.add(sccess);
//
//	        /***********************************End 设置yarn读写权限*************************************************/
//	        
//	        
//	        /*************************Begin 设置用户名**************************************************/
//	        List<String> users = new ArrayList<String>();
//	        plicyItem.setUsers(users);
//	        users.add("user01");
//	        
//	        /*************************End 设置用户名**************************************************/  
//	        
//	        return policyInfoVO;
//
//	    }

//		public static void main(String[] args) throws Exception {
//			// TODO Auto-generated method stub
//			PolicyInfoVO policyInfoVO = TestMain.buildYarnPolicy();
//
//	        RangerPolicyProxyImpl impl = new RangerPolicyProxyImpl();
//	        
//	        impl.addPolicy(policyInfoVO, "yarn");
//
//	        
//		}
}

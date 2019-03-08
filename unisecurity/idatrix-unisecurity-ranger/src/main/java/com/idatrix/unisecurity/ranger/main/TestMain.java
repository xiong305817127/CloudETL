package com.idatrix.unisecurity.ranger.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.idatrix.unisecurity.ranger.common.RangerResourceType;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyUserInfoVo;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.Access;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.PolicyItem;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.Resource;
import com.idatrix.unisecurity.ranger.proxy.IProxyRanger;
import com.idatrix.unisecurity.ranger.proxy.impl.RangerPolicyProxyImpl;
import com.idatrix.unisecurity.ranger.proxy.impl.RangerProxyImpl;

public class TestMain {



	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	      ApplicationContext ctx=new FileSystemXmlApplicationContext("src/main/resource/ranger-site.xml");
	      RangerProxyImpl rangerProxy=(RangerProxyImpl) ctx.getBean("rangerProxy");

		
//		IProxyRanger rangerProxy = new RangerProxyImpl("lcCluster_hadoop", "lcCluster_hbase", "lcCluster_hive",
//				"lcCluster_yarn", "http://hadoop143.example.com:6080", "admin", "admin");
		List<String> users = new ArrayList<String>();
		users.add("user02");
//		List<Access> accesses = new ArrayList<Access>();
//		Access sccess = new Access();
//		sccess.setIsAllowed("true");
//		sccess.setType("read");
//		accesses.add(sccess);
//		// 设置对应的write权限
//		sccess = new Access();
//		sccess.setIsAllowed("true");
//		sccess.setType("write");
//		accesses.add(sccess);
//		// 设置对应的execute权限
//		sccess = new Access();
//		sccess.setIsAllowed("true");
//		sccess.setType("execute");
//		accesses.add(sccess);
		
		List<String> accesses = new ArrayList<String>();
		accesses.add("update");
//		accesses.add("read");
//		accesses.add("write");

//		rangerProxy.updateUserHdfs("/test", users, accesses);
		// RangerPolicyProxyImpl impl = new RangerPolicyProxyImpl();

		// impl.addPolicy(policyInfoVO, "hdfs");
		
//		rangerProxy.updateUserHdfs("/user", users, accesses);
//		rangerProxy.updateGroupHdfs("/user", users, accesses);
//		rangerProxy.updateUserHBase("test", users, accesses);
		rangerProxy.updateUserHive("default", "test", users, accesses);
		

	}

}

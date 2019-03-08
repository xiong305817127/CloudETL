package com.test.main;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.idatrix.unisecurity.freeipa.model.FreeIPATemplate;
import com.idatrix.unisecurity.freeipa.proxy.factory.LdapHttpDataBuilder;
import com.idatrix.unisecurity.freeipa.proxy.impl.FreeIPAProxyImpl;

public class TestMain {
	

	
	public static void main(String[] args) {
        ApplicationContext cxt = new ClassPathXmlApplicationContext("idatrix-freeipa.xml");     
        FreeIPATemplate userDao = (FreeIPATemplate)cxt.getBean("freeipaTempalte");     
        LdapHttpDataBuilder httpBuilder = (LdapHttpDataBuilder)cxt.getBean("ldapHttpDataBuilder"); 
        
		FreeIPAProxyImpl impl = new FreeIPAProxyImpl(userDao, httpBuilder,null);

		
        try {
			impl.addUser("user15", "lch");
//			impl.deleteUser("user14");
//        	impl.isUserExist("user");
//        	impl.addGroup("testGroup1234" , "this is a test.");
//        	impl.deleteGroup("testGroup1234");
        	
        	List<String> list = new ArrayList<String>();
        	list.add("gp1");
        	list.add("gp2");
        	list.add("user12");
//        	list.add("user13");
//        	impl.addGroupList(list, "test");
//
//        	impl.addUsers2Group(list, "testGroup"); 
//        	impl.removeUserFromGroup(list, "testGroup");
        	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		FreeIPAProxyImpl impl = new FreeIPAProxyImpl(getRjdataTemplate(), new LdapHttpDataBuilder());
//		Person p = new Person();
//		p.setName("user04");
//		p.setPassword("lch");
//		
//		try {
////			impl.logins("user04", "lch");
//			impl.changePasswd("user07", "lch", "lch");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}

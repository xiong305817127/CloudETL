package com.idatrix.unisecurity.ranger.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.Access;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.PolicyItem;

public class Test {
    public static boolean compareList(List<String>oldList, List<String> newList){
        if(CollectionUtils.isEmpty(oldList) || CollectionUtils.isEmpty(newList)){
            return false;
        }
        if(CollectionUtils.isSubCollection(newList,oldList )){
            return true;
        }
        
        return false;
    }
    
	public static Map<String, List<String>> dealWithList(List<String> oldList, List<String> newList){
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> ABList = new ArrayList<String>();
		List<String> A_BList = new ArrayList<String>();
		List<String> B_AList = new ArrayList<String>();
		for(int i = 0; i < oldList.size(); i++) {
			boolean tag =  true;
			for(int j = 0; j < newList.size(); j++) {
				if(StringUtils.equals(oldList.get(i), newList.get(j))) {
					ABList.add(oldList.get(i)); //第i个值在newList中
					newList.remove(j);
					tag = false;
					break;
				}
			}
			if(tag) {
				//tag值不为false，表明这个值不在newList中
				A_BList.add(oldList.get(i));
			}
		}
		
		for(int k = 0; k < newList.size(); k++) {
			B_AList.add(newList.get(k));
		}
		
		map.put("AB", ABList);
		map.put("A_B", A_BList);
		map.put("B_A", B_AList);
		
		return map;

	}
	
	public static void print(List<String> list, String key) {
		System.out.println("-------------" + key + " = " + list.size());
		for(int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
	}
	
	public static void mergerPolicyItem2NewList(List<PolicyItem> rangerList, List<PolicyItem> newRangerList) {
	
		for(int i = 0; i < rangerList.size(); i++) {
			PolicyItem oldItem = rangerList.get(i);
			boolean tag = true;
			System.out.println(" --- newrangerList size = " + newRangerList.size());
			if(newRangerList.size() == 0) {
				newRangerList.add(oldItem);
				continue;
			}

			for(int j = 0; j < newRangerList.size(); j++) {
				PolicyItem item =  newRangerList.get(j);
				//如果权限相等，则查看一下这个是组用户，还是什么
				if(Test.compareAccess(oldItem.getAccesses(), item.getAccesses())) {
					if(CollectionUtils.isEmpty(oldItem.getUsers())) {
						//如果用户信息为空，那么就是组用户，则需要查看一下，查找 到的item用户信息是否也是空，如果也是空，就可以将它们直接add进去
						if(CollectionUtils.isEmpty(item.getUsers())) {
							item.getGroups().addAll(oldItem.getGroups());
							tag = false;
							break;
						}
					}else {
						//如果用户信息不为空，
						//如果item的用户信息也不为空，南昌直接将它加上去即可
						if(CollectionUtils.isNotEmpty(item.getUsers())) {
							item.getUsers().addAll(oldItem.getUsers());
							tag =  false;
							break;
						}
					}
				}
			}
			if(tag) {
				newRangerList.add(oldItem);		

			}
		}
	}
	
	public static boolean compareAccess(List<Access> oldAcces, List<Access> newAccess) {
	
		if(oldAcces == null || newAccess == null) {
			return false;
		}
		if(oldAcces.size() != newAccess.size()) {
			return false;
		}
		int num = 0;
		for(int i = 0; i < oldAcces.size(); i++) {
			for(int j = 0; j  < newAccess.size(); j++) {
				if(oldAcces.get(i).getType().equals(newAccess.get(j).getType())) {
					num++;
					break;
				}
			}
			
		}
		System.out.println("num = " + num + " . oldAccess size = " + oldAcces.size());

		if(num != oldAcces.size()) {
			System.out.println("num = " + num + " . oldAccess size = " + oldAcces.size());
			return false;
		}
		
		return true;
	}
	
	
	
	public static List<String> getSubList(List<String> rangerList, List<String> addList){
		List<String> newList = new ArrayList<String>();
		if(CollectionUtils.isEqualCollection(rangerList, addList)) {
			return newList;
		}
		for(int i = 0; i < rangerList.size(); i++) {
			boolean tag = false;
			for(int j = 0; j  < addList.size(); j++) {
				if(StringUtils.equals(rangerList.get(i), addList.get(j))) {
					tag =  true;
					break;
				}
			}
			if(!tag) {
				newList.add(rangerList.get(i));
			}
		}

		return newList;
	}
	
	public static Access getAccess(String key) {
		Access access =  new Access();
		 access.setType(key);
		 return access;
	}
	
	public static PolicyItem getPolicyItem(List<String> users, List<String> groups, List<String> access) {

		PolicyItem titem =  new PolicyItem();
		List<Access> list = new ArrayList<Access>();
		for(int i = 0; i < access.size(); i++) {
			list.add(getAccess(access.get(i)));
		}
		titem.setAccesses(list);
		titem.setUsers(users);
		titem.setGroups(groups);
		return titem;
	}
	
	public static void main(String[] args) {
//		List<String> oldList = new ArrayList<String>();
//		oldList.add("/*");
//		oldList.add("/app");
//		List<String> newList = new ArrayList<String>();
//		newList.add("/user");
//		newList.add("/*");
//		
//		List<String> list = Test.getSubList(oldList, newList);
//		Test.print(list, "sublist");
//		public static void mergerPolicyItem2NewList(List<PolicyItem> rangerList, List<PolicyItem> newRangerList) {
		List<PolicyItem> rangerList =  new ArrayList<PolicyItem>();
		List<PolicyItem> newList =  new ArrayList<PolicyItem>();

		
		List<String> access = new ArrayList<String>();
		access.add("read");
		access.add("write");
		
		List<String> access1 = new ArrayList<String>();
		access1.add("read");
		
		List<String> access2 = new ArrayList<String>();
		access1.add("read");
		access.add("write");
		access.add("execute");
		
		List<String> users1 = new ArrayList<String>();
		users1.add("user01");
		
		List<String> users2 = new ArrayList<String>();
		users2.add("user02");
		
		List<String> groups1 = new ArrayList<String>();
		groups1.add("group1");
		
		PolicyItem item1 = Test.getPolicyItem(users1, null, access1);
//		System.out.println(item1);

		PolicyItem item2 = Test.getPolicyItem(users2, null, access1);
//		System.out.println(item2);

		rangerList.add(item1);
		rangerList.add(item2);
		
		Test.mergerPolicyItem2NewList(rangerList, newList);
		
		System.out.println("----------------------- newList size = " + newList.size());
		for(int i = 0; i < newList.size(); i++) {
			System.out.println(newList.get(i));
		} 
	}

}

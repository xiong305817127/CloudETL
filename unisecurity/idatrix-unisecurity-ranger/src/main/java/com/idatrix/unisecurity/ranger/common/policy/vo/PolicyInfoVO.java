package com.idatrix.unisecurity.ranger.common.policy.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.idatrix.unisecurity.ranger.common.vo.RangerBaseVO;

public class PolicyInfoVO extends RangerBaseVO {

	//策略ID，这个值由ranger给出。在创建的时候，这个值为空。但是删除策略的时候，
	//会根据这个值进行删除
	private String id;
	
	//资源类型。对应hdfs, hbase等。用户通过枚举类型 RangerResourceType来给定值.
	private String resourceType;

    //是否启用。直接将它设置为"true"吧
    private String isEnabled;
    //是否递归, 建议直接将它设置为true
	private String isRecursive;
	//与Ranger的Tag相对应。这个参数值不用理会。即不用设置
	private String version;
	//对应的repository名称。这个值很重要
	private String service;
	//策略名称，这个值由用户设置，只要求不重复即可
	private String name;
	//策略类型，用户不用设置
	private String policyType;
	//是否加入审计，建议设置为true
	private String isAuditEnabled;
	 //资源信息。需要加入到权限控制的资源信息。resources为一个Map类型，对于key与value都是有比较严格的说明的。
	private Map<String, PolicyInfoVO.Resource> resources;
	//HIVE的策略配置，hbase\hdfs\yarn的策略，可以不用理它
	private List<RowFilterPolicyItem> rowFilterPolicyItems;
	//HIVE的策略配置，hbase\hdfs\yarn的策略，可以不用理它
	private List<DataMaskPolicyItem> dataMaskPolicyItems;
	//权限控制的策略
	private List<PolicyItem> policyItems;

	private String role_id;
	private String tenant_id;
	
	
	public static PolicyInfoVO  getPolicyInfo(PolicyInfoVO old, Map<String, PolicyInfoVO.Resource> resources) {
		PolicyInfoVO policy = new PolicyInfoVO();
		policy.setId(old.getId());
		policy.setResourceType(old.getResourceType());
		policy.setIsAuditEnabled(old.getIsAuditEnabled());
		policy.setIsEnabled(old.getIsEnabled());
		policy.setIsRecursive(old.getIsRecursive());
		policy.setVersion(old.getVersion());
		policy.setService(old.getService());
		policy.setName(old.getName() + "");
		policy.setPolicyType(old.getPolicyType());
		policy.setResources(resources);
		policy.setRowFilterPolicyItems(old.getRowFilterPolicyItems());
		policy.setDataMaskPolicyItems(old.getDataMaskPolicyItems());
		policy.setPolicyItems(old.getPolicyItems());
		policy.setRole_id(old.getRole_id());
		policy.setTenant_id(old.getTenant_id());	
		return policy;
	}
	
    
    public String getResourceType() {
        return resourceType;
    }

    
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    
	public String getRole_id() {
		return role_id;
	}

	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}

	public String getTenant_id() {
		return tenant_id;
	}

	public void setTenant_id(String tenant_id) {
		this.tenant_id = tenant_id;
	}

	public Map<String, PolicyInfoVO.Resource> getResources() {
		return resources;
	}

	public void setResources(Map<String, PolicyInfoVO.Resource> resources) {
		this.resources = resources;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public String getIsAuditEnabled() {
		return isAuditEnabled;
	}

	public void setIsAuditEnabled(String isAuditEnabled) {
		this.isAuditEnabled = isAuditEnabled;
	}

	public List<PolicyItem> getPolicyItems() {
		return policyItems;
	}

	public void setPolicyItems(List<PolicyItem> policyItems) {
		this.policyItems = policyItems;
	}
	
	
	

	public List<RowFilterPolicyItem> getRowFilterPolicyItems() {
		return rowFilterPolicyItems;
	}

	public void setRowFilterPolicyItems(
			List<RowFilterPolicyItem> rowFilterPolicyItems) {
		this.rowFilterPolicyItems = rowFilterPolicyItems;
	}

	public List<DataMaskPolicyItem> getDataMaskPolicyItems() {
		return dataMaskPolicyItems;
	}

	public void setDataMaskPolicyItems(List<DataMaskPolicyItem> dataMaskPolicyItems) {
		this.dataMaskPolicyItems = dataMaskPolicyItems;
	}




	public static class PolicyItem {
		private List<Access> accesses;
		private List<String> users;
		private List<String> groups;
		private List<String> conditions;

		private String delegateAdmin;
		
		public PolicyItem() {
			
		}
		
		public PolicyItem(PolicyItem old) {
			accesses =  new ArrayList<Access>();
			for(int i = 0; i < old.getAccesses().size(); i++) {
				Access ac =  new Access();
				ac.setIsAllowed(old.getAccesses().get(i).getIsAllowed());
				ac.setType(old.getAccesses().get(i).getType());
				accesses.add(ac);
			}
			this.users = this.copyList(old.getUsers());
			this.groups = this.copyList(old.getGroups());
			this.conditions = this.copyList(old.getConditions());
			this.delegateAdmin = this.getDelegateAdmin();
		}
		
		private List<String> copyList(List<String> old){
			List<String> list = new ArrayList<String>();
			if(CollectionUtils.isEmpty(old)) {
				return list;
			}
			for(int i = 0; i < old.size(); i++) {
				list.add(old.get(i));
			}
			return list;
		}

		public List<Access> getAccesses() {
			return accesses;
		}

		public void setAccesses(List<Access> accesses) {
			this.accesses = accesses;
		}

		public List<String> getUsers() {
			return users;
		}

		public void setUsers(List<String> users) {
			this.users = users;
		}

		public List<String> getGroups() {
			return groups;
		}

		public void setGroups(List<String> groups) {
			this.groups = groups;
		}

		public List<String> getConditions() {
			return conditions;
		}

		public void setConditions(List<String> conditions) {
			this.conditions = conditions;
		}

		public String getDelegateAdmin() {
			return delegateAdmin;
		}

		public void setDelegateAdmin(String delegateAdmin) {
			this.delegateAdmin = delegateAdmin;
		}

		@Override
		public String toString() {
			return "PolicyItem [accesses=" + accesses + ", users=" + users
					+ ", groups=" + groups + ", conditions=" + conditions
					+ ", delegateAdmin=" + delegateAdmin + "]";
		}

	}

	/**
	 * 权限控制，根据不同的控制，如hdfs的权限为read, write, execute
	 * @author 
	 *
	 */
	public static class Access {
		private String type;
		private String isAllowed;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getIsAllowed() {
			return isAllowed;
		}

		public void setIsAllowed(String isAllowed) {
			this.isAllowed = isAllowed;
		}
		
		public boolean compareAccess(Access access) {
			return StringUtils.equals(this.type, access.getType());
		}

		@Override
		public String toString() {
			return "Access [type=" + type + ", isAllowed=" + isAllowed + "]";
		}

	}

	public static class Resource {
		private List<String> values;
		private String isExcludes;
		private String isRecursive;
		
		public static  Resource getDefaultResource(String values) {
			Resource r =  new Resource();
			List<String> list = new ArrayList<String>(1);
			list.add(values);
			r.setValues(list);
			r.setIsExcludes("false");
			r.setIsRecursive("true");
			return r;
		}
		
		public static  Resource getDefaultResource(List<String> values) {
			Resource r =  new Resource();
			r.setValues(values);
			r.setIsExcludes("false");
			r.setIsRecursive("true");
			return r;
		}

		public List<String> getValues() {
			return values;
		}

		public void setValues(List<String> values) {
			this.values = values;
		}

		public String getIsExcludes() {
			return isExcludes;
		}

		public void setIsExcludes(String isExcludes) {
			this.isExcludes = isExcludes;
		}

		public String getIsRecursive() {
			return isRecursive;
		}

		public void setIsRecursive(String isRecursive) {
			this.isRecursive = isRecursive;
		}

		@Override
		public String toString() {
			return "Resource [values=" + values + ", isExcludes=" + isExcludes
					+ ", isRecursive=" + isRecursive + "]";
		}

	}
	
	/**
	 * HIVE模糊过滤策略
	 * @author 
	 *
	 */
	public static class DataMaskPolicyItem {
		private List<String> groups;
		private List<String> users;
		private List<String> conditions;
		private String delegateAdmin;
		private List<Access> accesses;
		private Map<String, Object> dataMaskInfo;
		public List<String> getGroups() {
			return groups;
		}
		public void setGroups(List<String> groups) {
			this.groups = groups;
		}
		public List<String> getUsers() {
			return users;
		}
		public void setUsers(List<String> users) {
			this.users = users;
		}
		public List<String> getConditions() {
			return conditions;
		}
		public void setConditions(List<String> conditions) {
			this.conditions = conditions;
		}
		public String getDelegateAdmin() {
			return delegateAdmin;
		}
		public void setDelegateAdmin(String delegateAdmin) {
			this.delegateAdmin = delegateAdmin;
		}
		public List<Access> getAccesses() {
			return accesses;
		}
		public void setAccesses(List<Access> accesses) {
			this.accesses = accesses;
		}
		public Map<String, Object> getDataMaskInfo() {
			return dataMaskInfo;
		}
		public void setDataMaskInfo(Map<String, Object> dataMaskInfo) {
			this.dataMaskInfo = dataMaskInfo;
		}
		@Override
		public String toString() {
			return "DataMaskPolicyItem [groups=" + groups + ", users=" + users
					+ ", conditions=" + conditions + ", delegateAdmin="
					+ delegateAdmin + ", accesses=" + accesses
					+ ", dataMaskInfo=" + dataMaskInfo + "]";
		}
	}
	
	
	/**
	 * hive行过滤策略
	 * @author 
	 *
	 */
	public static class RowFilterPolicyItem{
		private List<String> groups;
		private List<String> users;
		private List<String> conditions;
		private String delegateAdmin;
		private List<Access> accesses;
		private Map<String, Object> rowFilterInfo;
		public List<String> getGroups() {
			return groups;
		}
		public void setGroups(List<String> groups) {
			this.groups = groups;
		}
		public List<String> getUsers() {
			return users;
		}
		public void setUsers(List<String> users) {
			this.users = users;
		}
		public List<String> getConditions() {
			return conditions;
		}
		public void setConditions(List<String> conditions) {
			this.conditions = conditions;
		}
		public String getDelegateAdmin() {
			return delegateAdmin;
		}
		public void setDelegateAdmin(String delegateAdmin) {
			this.delegateAdmin = delegateAdmin;
		}
		public List<Access> getAccesses() {
			return accesses;
		}
		public void setAccesses(List<Access> accesses) {
			this.accesses = accesses;
		}
		public Map<String, Object> getRowFilterInfo() {
			return rowFilterInfo;
		}
		public void setRowFilterInfo(Map<String, Object> rowFilterInfo) {
			this.rowFilterInfo = rowFilterInfo;
		}
		@Override
		public String toString() {
			return "rowFilterPolicyItem [groups=" + groups + ", users=" + users
					+ ", conditions=" + conditions + ", delegateAdmin="
					+ delegateAdmin + ", accesses=" + accesses
					+ ", rowFilterInfo=" + rowFilterInfo + "]";
		}
	}

	public String getIsRecursive() {
		return isRecursive;
	}

	public void setIsRecursive(String isRecursive) {
		this.isRecursive = isRecursive;
	}

	@Override
	public String toString() {
		return "PolicyInfoVO [id=" + id + ", isEnabled=" + isEnabled
				+ ", version=" + version + ", service=" + service + ", name="
				+ name + ", policyType=" + policyType + ", isAuditEnabled="
				+ isAuditEnabled + ", resources=" + resources
				+ ", policyItems=" + policyItems + ", getStatusCode()="
				+ getStatusCode() + ", getMsgDesc()=" + getMsgDesc()
				+ ", toString()=" + super.toString() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + "]";
	}

}

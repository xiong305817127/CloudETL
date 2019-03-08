package com.idatrix.unisecurity.ranger.proxy.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.ListCellRenderer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.idatrix.unisecurity.ranger.common.BaseService;
import com.idatrix.unisecurity.ranger.common.RangerResourceType;
import com.idatrix.unisecurity.ranger.common.RangerStaticInfo;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyUserInfoVo;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.Access;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.PolicyItem;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO.Resource;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyUserInfoVo.VXGroups;
import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyUserInfoVo.VXUsers;
import com.idatrix.unisecurity.ranger.common.repository.vo.RangerServiceListVO;
import com.idatrix.unisecurity.ranger.common.repository.vo.RepositoryService;


public class RangerPolicyProxyImpl extends BaseService {

	private static final Logger logger = Logger.getLogger(RangerPolicyProxyImpl.class);
	
	public RangerPolicyProxyImpl(String username, String passwd, String httpUrl) {
		super(username, passwd, httpUrl);
	}


	/**
	 * @Title: updatePolicy @Description: 用于更新策略。 @param @param
	 *         policyInfoVO @param @return @param @throws Exception @return
	 *         PolicyInfoVO @throws
	 */
	private PolicyInfoVO updatePolicy(PolicyInfoVO policyInfoVO) throws Exception {

		logger.info("update Policy ...");
		if (null == policyInfoVO) {
			throw new Exception("update Policy,policyInfoVO can not be null.");
		}

		// String rangerHttpUrl = RangerStaticInfo.RANGER_URL;

		String serviceUrl = this.getUrl() + RangerStaticInfo.RANGER_POLICY + policyInfoVO.getId();
		HttpPut httpPost = new HttpPut(serviceUrl);

		StringEntity stringEntity = new StringEntity(gson.toJson(policyInfoVO));
		stringEntity.setContentType("application/json;charset=UTF-8");
		httpPost.setEntity(stringEntity);

		CloseableHttpResponse response = super.excute(httpPost);
		HttpEntity responseEntity = response.getEntity();
		String retStr = EntityUtils.toString(responseEntity);
		PolicyInfoVO retVO = gson.fromJson(retStr, PolicyInfoVO.class);
		checkResult(retVO);
		return retVO;
	}

	/**
	 * @Description: 删除一个policy @param @param policyID
	 *               ：需要删除的policyID @param @return @param @throws Exception
	 *               设定文件 @throws
	 */
	public void deletePolicy(String policyID) throws Exception {
		logger.info("delete Policy by id ...");
		if (StringUtils.isEmpty(policyID)) {
			throw new Exception("delete Policy,policyID can not be null.");
		}
		String serviceUrl = this.getUrl() + RangerStaticInfo.RANGER_POLICY + policyID;
		HttpDelete httpGet = new HttpDelete(serviceUrl);
		httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
		CloseableHttpResponse response = super.excute(httpGet);
		HttpEntity responseEntity = response.getEntity();
		if (null != responseEntity) {
			String retStr = EntityUtils.toString(responseEntity);
			checkResult(super.getResultVO(responseEntity, retStr));
			logger.info(retStr);
		}
	}

	/**
	 * @Description: 创建一个新的policy @param @param PolicyInfoVO
	 *               ：需要创建的policy对8象 @param @return @param @throws Exception @throws
	 */
	private PolicyInfoVO createPolicy(PolicyInfoVO policyInfoVO) throws Exception {
		if (null == policyInfoVO) {
			throw new Exception("create Policy,policyInfoVO can not be null.");
		}

		logger.info("Create Policy ... policyInfoVO:" + gson.toJson(policyInfoVO));

		String serviceUrl = this.getUrl() + RangerStaticInfo.RANGER_POLICY;// rangerHttpUrl +
																			// "/service/plugins/policies";
		HttpPost httpPost = new HttpPost(serviceUrl);

		StringEntity stringEntity = new StringEntity(gson.toJson(policyInfoVO));
		stringEntity.setContentType("application/json;charset=UTF-8");
		httpPost.setEntity(stringEntity);

		CloseableHttpResponse response = super.excute(httpPost);
		HttpEntity responseEntity = response.getEntity();
		String retStr = EntityUtils.toString(responseEntity);
		logger.info(retStr);
		PolicyInfoVO retVO = gson.fromJson(retStr, PolicyInfoVO.class);
		checkResult(retVO);
		return retVO;
	}
	
	/**
	 * @Description: Ranger权限管理入口 
	 * 
	 * @param infoVO
	 * @param type
	 * @return
	 * @throws Exception
	 */

	public boolean addPolicy(PolicyInfoVO infoVO, String type) throws Exception {
		return this.addPolicy(infoVO, type, false);
	}

	/**
	 * 
	 * @Description: 插入某个policy信息
	 *  @param type : 插入的類型。 是hdfs, hbase, hive還是yarn
	 *  @param PolicyInfoVO：需要加入的policy
	 *              
	 *  @param recuise： 用于判斷是否為递歸調用
	 *  @return @param @throws Exception @throws
	 * @throws Exception
	 */

	private boolean addPolicy(PolicyInfoVO infoVo, String type, boolean recuise) throws Exception {
		// 1. 首先对于infoVo进行检查，如果有resource中包含了多个资源，则需要切割
		List<PolicyInfoVO> list = this.cutOffPolicyByResouce(infoVo, type);

		// 对于每个policyInfo进行判断
		for (int i = 0; i < list.size(); i++) {
			PolicyUserInfoVo retVo = this.getPolicyByResource(list.get(i), type);
			if (retVo == null) {
				//如果没有查找到，则证明这个资源路径不存在，可以直接新建
				this.createPolicy(list.get(i));
			} else {
				//如果在ranger中存在类似的资源路径，就需要单独处理。
				this.updatePolicyUserInfo(retVo, infoVo, type, recuise);
			}
		}
		return true;
	}

	/**
	 * @Description: 创建策略的时候，查找发现对应的资源信息已经存在，此时，就需要对它进行单独处理
	 * @param rangerUserInfo
	 * @param addPolicy
	 * @throws Exception
	 */
	private void updatePolicyUserInfo(PolicyUserInfoVo rangerUserInfo, PolicyInfoVO addPolicy, String type,
			boolean recuise) throws Exception {
		List<PolicyInfoVO> rangerPolicyList = rangerUserInfo.getPolicies();
		boolean needCreate = true;
		for (int i = 0; i < rangerPolicyList.size(); i++) {
			// 对已经存在的进行处理原则是：如果在这个policy中存在与addPolicy重合的用户或者组信息才需要处理，
			int tag = this.checkPolicy(rangerPolicyList.get(i), addPolicy, type, recuise);
			if (tag == 0) {
				continue;
			}
			if (tag == 1) {
				needCreate = false;
				this.updatePolicy(rangerPolicyList.get(i));
			}
			// 如果tag等于2，表明原有的资源信息已经被切分了，此时就需要再判断一下，这个切分之后，会不会和已存的ranger上面相冲突。
			if (tag == 2) {
				this.deletePolicy(rangerPolicyList.get(i).getId());

				this.addPolicy(rangerPolicyList.get(i), type, true);
				// 调用完addPolicy之后，还需要将原来的那个干掉。
			}

		}

		if (needCreate)
			this.createPolicy(addPolicy);
	}

	/**
	 * @Description 用于检查两个policy的关系。
	 *              从ranger上面查询到的policy信息，其resource可能包含多个资源信息，但是addPolicy只能包含一个资源信息（前面已经处理）
	 *              对于这种情况，有几种处理方式： 1） ranger中的策略中的用户信息与需要增加，完全不相同 2） ranger中用户信息
	 *              与需要增加的策略 有部分重合 最好的解决办法: 1) 首先将ranger的资源进行切分，切成两块：
	 *              与addPolicy相同的,记为B， 与addPolicy中不同的, 记为A 2) 切分完成后，再将A
	 *              update到ranger中， 将addPolicy create到ranger中 有一种情况：以hdfs为例，
	 *              如果addPolicy的资源路径为"/user" , 那么就有可能查出来的为"/*",
	 *              对于这种情况不处理。我们认为这两个策略的资源是完全不同，我们只做完整的字符串的比对
	 * 
	 * @param rangerPolicy
	 * @param addPolicy
	 * @return 0 --- 不用做任务动作 1 --- 只用udpate老的 2 ---- update + create
	 */
	private int checkPolicy(PolicyInfoVO rangerPolicy, PolicyInfoVO addPolicy, String type, boolean recurse) {

		// 如果policy名字相同，而且recure为true，表明此策略是递归调用进来的。它查找到它自身，直接return
		if (recurse && rangerPolicy.getName().equals(addPolicy.getName())) {
			return 0;
		}

		// 将从ranger查到的数据串进行切分。切分的原因是为了下面的计算方便.
		List<Map<String, PolicyInfoVO.Resource>> cutResource = this.cutoffResource(rangerPolicy.getResources(), type);

		int totoal = cutResource.size();

		// 切分完成后，再比较需要增加的policy与切分后，如果有与addPolicy相同的资源信息，则将它从cutResource中删除.
		for (int i = 0; i < cutResource.size(); i++) {
			// 如果有相同的，则证明已经找到，就需要单独进行处理
			if (this.compareMapInfo(cutResource.get(i), addPolicy.getResources(), type)) {
				cutResource.remove(i);
			}
		}
		// 经过上一步处理后，cutResource的数组长度没有变化，证明，可以不处理。它对应一种情况: HDFS的资源路径配置为/*
		if (cutResource.size() == totoal) {
			return 0;
		}
		// 如果cutResource的值等0，则证明原来的rangerPolicy只有一个资源文件，而且与我们的完全相等
		if (cutResource.size() == 0) {
			List<PolicyItem> itemList = this.mergerPolicyItem(rangerPolicy.getPolicyItems(), addPolicy.getPolicyItems());
			rangerPolicy.setPolicyItems(itemList);
			return 1;
		} else {
			// 如果cutResource的长度不为0，则证明我们需要进行处理, 现在的rangerPolicy中的资源路径是去掉了addPolicy中的资源的。 
			rangerPolicy.setResources(this.mergeResource(cutResource));
			
			//修改addPolicy中的权限，两种情况：
			//rangerPolicy 资源为/test /user   用户为lch, user01  而 addPolicy 资源路径为/user , 用户为user01
			//1. 首先rangerPolicy的资源修改为/test， 而用户权限可以不用管它
			
			//2. addPolicy的用户权限需要增加rangerPolicy中对应的lch权限信息
			
			List<PolicyItem> list = this.deleteUserInPolicy(rangerPolicy, addPolicy);
			
			List<PolicyItem> itemList1 =  this.mergerPolicyItem(list, addPolicy.getPolicyItems());
			addPolicy.setPolicyItems(itemList1);

			return 2;

		}
	}

	private List<PolicyItem> deleteUserInPolicy(PolicyInfoVO rangerPolicy, PolicyInfoVO addPolicy) {
		List<PolicyItem> additems =  this.copyArrayList(addPolicy.getPolicyItems()) ;// new ArrayList<PolicyInfoVO.PolicyItem>(addPolicy.getPolicyItems())  ;
		List<PolicyItem> rangeritem = this.copyArrayList(rangerPolicy.getPolicyItems());// new ArrayList<PolicyInfoVO.PolicyItem>(rangerPolicy.getPolicyItems());		
		for (int i = 0; i < additems.size(); i++) {
			PolicyItem newItem = additems.get(i);
			for (int j = 0; j < rangeritem.size(); j++) {
				PolicyItem oldItem = rangeritem.get(j);
				// 1. 首先比较user
				if (CollectionUtils.isNotEmpty(oldItem.getUsers())) {
					this.deleteItemInList(oldItem.getUsers(), newItem.getUsers());
				} else {
					this.deleteItemInList(oldItem.getGroups(), newItem.getGroups());
				}
			}
		}
		return rangeritem;
	}
	
	private List<PolicyItem>  copyArrayList(List<PolicyItem> oldlist){
		List<PolicyItem> newList = new ArrayList<PolicyItem>();
		for(int i = 0; i < oldlist.size(); i++) {
			PolicyItem ob =  new PolicyItem(oldlist.get(i));
			newList.add(ob);
		}
		
		return newList;
	}

	private void deleteItemInList(List<String> oldList, List<String> newList) {
		if (CollectionUtils.isEmpty(newList) || CollectionUtils.isEmpty(oldList)) {
			return;
		}

		for (int i = 0; i < oldList.size(); i++) {
			for (int j = 0; j < newList.size(); j++) {
				if (StringUtils.equals(oldList.get(i), newList.get(j))) {
					oldList.remove(i);
				}
			}
		}
	}

	/**
	 * @Description : 用于将原来切分的resource，再合并。需要说明的是，相比原来的resource，现在的resource是经过裁减后，即去掉了需要增加的权限的资源路径的。
	 * @param cutResource
	 * @return
	 */
	private Map<String, PolicyInfoVO.Resource> mergeResource(List<Map<String, PolicyInfoVO.Resource>> cutResource) {

		Map<String, PolicyInfoVO.Resource> newMap = new HashMap<String, PolicyInfoVO.Resource>();
		int num = 0;
		for (int i = 0; i < cutResource.size(); i++) {
			for (Entry<String, PolicyInfoVO.Resource> entry : cutResource.get(i).entrySet()) {
				String key = entry.getKey();
				Resource r = entry.getValue();
				if (num == 0) {
					newMap.put(key, r);
				} else {
					newMap.get(key).getValues().addAll(r.getValues());
				}
			}
			num++;
		}
		return newMap;
	}

	/**
	 * @Description : 用于合并两个policy，需要注意，这两个policy一定有相同的resource。
	 *              现在合并的，并且将newPolicy的权限信息加到oldPolicy中，
	 * 
	 * @param rangerPolicy
	 * @param addPolicy
	 */

	
	private List<PolicyItem> mergerPolicyItem(List<PolicyItem> rangerList, List<PolicyItem> addList) {
		List<PolicyItem> newRangerList = new ArrayList<PolicyItem>();
		//首先删除即在rangerList，又在addList中的用户或组住处
		
		
		this.deteletUserFromList(rangerList, addList);  

		//把经过删除的队列赋值组新的队列
		this.mergerPolicyItem2NewList(rangerList, newRangerList);
		this.mergerPolicyItem2NewList(addList, newRangerList);
		return newRangerList;
	}

	private void deteletUserFromList(List<PolicyItem> rangerList, List<PolicyItem> addList) {
		for (int i = 0; i < addList.size(); i++) {
			PolicyItem addItem = addList.get(i);
			for (int j = 0; j < rangerList.size(); j++) {
				PolicyItem rItem = rangerList.get(j);
				if (CollectionUtils.isNotEmpty(addItem.getUsers())) {
					if (CollectionUtils.isNotEmpty(rItem.getUsers())) {
						List<String> subList = this.getSubList(rItem.getUsers(), addItem.getUsers());
						if (subList.size() == 0) {
							rangerList.remove(j);

						} else {
							rItem.setUsers(subList);
						}
					}
					continue;
				}
				if (CollectionUtils.isNotEmpty(addItem.getGroups())) {
					if (CollectionUtils.isEmpty(rItem.getUsers())) {
						List<String> subList = this.getSubList(rItem.getGroups(), addItem.getGroups());
						if (subList.size() == 0) {
							rangerList.remove(j);

						} else {
							rItem.setGroups(subList);
						}
					}
					continue;
				}
			}
		}
	}

	private void mergerPolicyItem2NewList(List<PolicyItem> rangerList, List<PolicyItem> newRangerList) {

		// 对于现有的PolicyItem进行重新整理, 按照三个维度,
		// 1. 相同的权限
		// 2. 如果权限相同，再区分组与用户
		// 3. 考虑同一个用户，或者组，如果修改权限的时候，
		for (int i = 0; i < rangerList.size(); i++) {
			PolicyItem oldItem = rangerList.get(i);
			if (CollectionUtils.isEmpty(oldItem.getAccesses())) {
				continue;
			}

			if (newRangerList.size() == 0) {
				newRangerList.add(oldItem);
				continue;
			}	
			boolean tag = true;
			for (int j = 0; j < newRangerList.size(); j++) {
				PolicyItem item = newRangerList.get(j);
				// 如果权限相等，则查看一下这个是组用户，还是什么
				if (this.compareAcessess(oldItem.getAccesses(), item.getAccesses())) {
					// 需要判断一下，当前权限是否为空，如果为空，就不用加入了。
					if (CollectionUtils.isEmpty(oldItem.getUsers())) {
						// 如果用户信息为空，那么就是组用户，则需要查看一下，查找 到的item用户信息是否也是空，如果也是空，就可以将它们直接add进去
						if (CollectionUtils.isEmpty(item.getUsers())) {
							item.getGroups().addAll(oldItem.getGroups());
							tag = false;
							break;
						}
					} else {
						// 如果用户信息不为空，
						// 如果item的用户信息也不为空，直接将它加上去即可
						if (CollectionUtils.isNotEmpty(item.getUsers())) {
							item.getUsers().addAll(oldItem.getUsers());
							tag = false;
							break;
						}
					}
				}
			}
			if (tag) {
				newRangerList.add(oldItem);
			}
		}
	}

	private boolean compareAcessess(List<Access> oldAccess, List<Access> newAccess) {
		if (oldAccess == null || newAccess == null) {
			return false;
		}
		if (oldAccess.size() != newAccess.size()) {
			return false;
		}
		int num = 0;
		for (int i = 0; i < oldAccess.size(); i++) {
			for (int j = 0; j < newAccess.size(); j++) {
				if (oldAccess.get(i).compareAccess(newAccess.get(j))) {
					num++;
					break;
				}
			}

		}
		if (num == oldAccess.size()) {
			return true;
		}
		return false;
	}

	/**
	 * @Description 获取在rangerList，而不在addList子串,
	 * @param rangerPolicy
	 * @param addPolicy
	 * @return List<String>
	 */
	private List<String> getSubList(List<String> rangerList, List<String> addList) {
		List<String> newList = new ArrayList<String>();
		if (rangerList == null) {
			return newList;
		}
		// 如果rangerList不为空，而addList为空，则直接将rangerList中的所有值拷由到newList中
		if (addList == null) {
			newList.addAll(rangerList);
			return newList;
		}

		for (int i = 0; i < rangerList.size(); i++) {
			boolean tag = false;
			for (int j = 0; j < addList.size(); j++) {
				if (StringUtils.equals(rangerList.get(i), addList.get(j))) {
					tag = true;
					break;
				}
			}
			if (!tag) {
				newList.add(rangerList.get(i));
			}
		}
		return newList;
	}

	public void print(List<String> list) {
		System.out.println("------------ " + list.size());
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
	}
	public void print(List<PolicyItem> list, String key) {
		System.out.println("-------------" + key + " = " + list.size());
		for (int i = 0; i < list.size(); i++) {
			this.print(list.get(i).getUsers());
		}
	}

	/*
	 * 根据资源类型，以及资源路径，在ranger中进行查找
	 */
	private PolicyUserInfoVo getPolicyByResource(PolicyInfoVO infoVo, String type) {
		//1. 生成查找的url串
		String url = this.generateFindURL(infoVo, type);
		logger.info("url = " + url);
		String retStr = null;
		try {
			retStr = this.executeHttpGet(url);
		} catch (Exception e) {
			logger.error("Fail to get policy from " + infoVo.getService(), e);
			return null;
		}
		PolicyUserInfoVo retVO = gson.fromJson(retStr, PolicyUserInfoVo.class);

		return retVO;
	}

	private String generateFindURL(PolicyInfoVO infoVo, String type) {

		if (StringUtils.isEmpty(infoVo.getService())) {
			logger.error("Fail to get policy of repository. Reason: the name of repository is empty");
			return null;
		}
		RepositoryService repo = null;
		try {
			repo = this.SearchRepository(infoVo.getService());
		} catch (Exception e) {
			logger.error("Fail to find " + infoVo.getService(), e);
			return null;
		}
		// 不存在，直接返回
		if (null == repo) {
			logger.error("The repository -- " + infoVo.getService() + " is not exist. Please check.");
			return null;
		}

		// 2. 查找此repository下面的所有的policy, 首先构造相应的URL
		// http://admin24.example.com:6080/service/plugins/policies/service/3?page=0&pageSize=25&startIndex=0&policyType=0&_=1502261063682
		StringBuilder urlSb = new StringBuilder();
		urlSb.append(this.getUrl() + RangerStaticInfo.RANGER_POLICY).append("service/").append(repo.getId())
				.append("?page=0&pageSize=1000&startIndex=0&policyType=0&_=1502261063682");

		if (type.equals(RangerResourceType.HDFS_TYPE)) {
			urlSb.append("&resource%3Apath").append("=").append(infoVo.getResources().get("path").getValues().get(0));

		} else if (type.equals(RangerResourceType.HBASE_TYPE)) {
			urlSb.append("&resource%3Atable").append("=").append(infoVo.getResources().get("table").getValues().get(0));

		} else if (type.equals(RangerResourceType.HIVE_TYPE)) {
			// resource%3Adatabase=defalut&resource%3Atable=test&_=1504939847919
			urlSb.append("&resource%3Adatabase").append("=")
					.append(infoVo.getResources().get("database").getValues().get(0)).append("&resource%3Atable=")
					.append(infoVo.getResources().get("table").getValues().get(0));

		} else if (type.equals(RangerResourceType.YARN_TYPE)) {
			urlSb.append("&resource%3Aqueue").append("=").append(infoVo.getResources().get("queue").getValues().get(0));
		}

		return urlSb.toString();
	}

	/**
	 * @Description: 用于检查PolicyInfoVO中的资源信息，如果有多个资源，则需要将进行切割
	 * 
	 * 
	 * @param @param
	 *            infoVo ：
	 */
	private List<PolicyInfoVO> cutOffPolicyByResouce(PolicyInfoVO infoVo, String type) {
		List<PolicyInfoVO> list = new ArrayList<PolicyInfoVO>();
		Map<String, PolicyInfoVO.Resource> map = infoVo.getResources();
		
		List<Map<String, PolicyInfoVO.Resource>> reList = this.cutoffResource(map, type);
		for (int i = 0; i < reList.size(); i++) {
			PolicyInfoVO info = PolicyInfoVO.getPolicyInfo(infoVo, reList.get(i));
			list.add(info);
		}

		return list;
	}

	private List<Map<String, PolicyInfoVO.Resource>> cutoffResource(Map<String, PolicyInfoVO.Resource> resources,
			String type) {
		List<Map<String, PolicyInfoVO.Resource>> reList = new ArrayList<Map<String, PolicyInfoVO.Resource>>();
		// 针对不同的资源类型，进行处理
		if (type.equals(RangerResourceType.HDFS_TYPE)) {
			this.cutOffResourceHdfsYarn(resources, RangerResourceType.HDFS_MAP_KEY, reList);

		} else if (type.equals(RangerResourceType.YARN_TYPE)) {
			this.cutOffResourceHdfsYarn(resources, RangerResourceType.YARN_MAP_KEY, reList);
		} else if (type.equals(RangerResourceType.HBASE_TYPE)) {
			this.cutOffResourceHBaseHive(resources, RangerResourceType.HBASE_MAP_TABLE_KEY,
					RangerResourceType.HBASE_MAP_FAMILY_KEY, RangerResourceType.HBASE_MAP_COLUMN_KEY, reList);
		} else if (type.equals(RangerResourceType.HIVE_TYPE)) {
			this.cutOffResourceHBaseHive(resources, RangerResourceType.HIVE_MAP_DATABASE_KEY,
					RangerResourceType.HIVE_MAP_TABLE_KEY, RangerResourceType.HIVE_MAP_COLUMN_KEY, reList);
		}

		return reList;
	}

	/**
	 * 输入的两个map都是按照要求进行切割，即已经调用cutoffResource()处理后的,即每个里面只有一个参数，只需要比较它们是否相等即可
	 * 
	 * @param map1
	 * @param map2
	 * @param type
	 * @return
	 */
	private boolean compareMapInfo(Map<String, PolicyInfoVO.Resource> map1, Map<String, PolicyInfoVO.Resource> map2,
			String type) {
		if (type.equals(RangerResourceType.HDFS_TYPE)) {

			List<String> list1 = map1.get(RangerResourceType.HDFS_MAP_KEY).getValues();
			List<String> list2 = map2.get(RangerResourceType.HDFS_MAP_KEY).getValues();
			return this.compareList(list1, list2);

		} else if (type.equals(RangerResourceType.YARN_TYPE)) {
			List<String> list1 = map1.get(RangerResourceType.YARN_MAP_KEY).getValues();
			List<String> list2 = map2.get(RangerResourceType.YARN_MAP_KEY).getValues();
			return this.compareList(list1, list2);
		} else if (type.equals(RangerResourceType.HBASE_TYPE)) {
			// 1. 从上比较到下面
			List<String> list1 = map1.get(RangerResourceType.HBASE_MAP_TABLE_KEY).getValues();
			List<String> list2 = map2.get(RangerResourceType.HBASE_MAP_TABLE_KEY).getValues();
			if (!this.compareList(list1, list2)) {
				return false;
			}
			List<String> tlist1 = map1.get(RangerResourceType.HBASE_MAP_FAMILY_KEY).getValues();
			List<String> tlist2 = map2.get(RangerResourceType.HBASE_MAP_FAMILY_KEY).getValues();
			if (!this.compareList(tlist1, tlist2)) {
				return false;
			}
			List<String> clist1 = map1.get(RangerResourceType.HBASE_MAP_COLUMN_KEY).getValues();
			List<String> clist2 = map2.get(RangerResourceType.HBASE_MAP_COLUMN_KEY).getValues();
			return this.compareList(clist1, clist2);

		} else if (type.equals(RangerResourceType.HIVE_TYPE)) {
			// 1. 从上比较到下面
			List<String> list1 = map1.get(RangerResourceType.HIVE_MAP_DATABASE_KEY).getValues();
			List<String> list2 = map2.get(RangerResourceType.HIVE_MAP_DATABASE_KEY).getValues();
			if (!this.compareList(list1, list2)) {
				return false;
			}
			List<String> tlist1 = map1.get(RangerResourceType.HIVE_MAP_TABLE_KEY).getValues();
			List<String> tlist2 = map2.get(RangerResourceType.HIVE_MAP_TABLE_KEY).getValues();
			if (!this.compareList(tlist1, tlist2)) {
				return false;
			}
			List<String> clist1 = map1.get(RangerResourceType.HIVE_MAP_COLUMN_KEY).getValues();
			List<String> clist2 = map2.get(RangerResourceType.HIVE_MAP_COLUMN_KEY).getValues();
			return this.compareList(clist1, clist2);
		}

		return true;
	}

	private void cutOffResourceHBaseHive(Map<String, PolicyInfoVO.Resource> resources, String mapKey1, String mapKey2,
			String mapKey3, List<Map<String, PolicyInfoVO.Resource>> reList) {
		List<String> tables = resources.get(mapKey1).getValues();
		List<String> families = resources.get(mapKey2).getValues();
		List<String> columns = resources.get(mapKey3).getValues();
		for (int i = 0; i < tables.size(); i++) {
			PolicyInfoVO.Resource tab = PolicyInfoVO.Resource.getDefaultResource(tables.get(i));
			for (int j = 0; j < families.size(); j++) {
				PolicyInfoVO.Resource fam = PolicyInfoVO.Resource.getDefaultResource(families.get(j));
				for (int k = 0; k < columns.size(); k++) {
					PolicyInfoVO.Resource colu = PolicyInfoVO.Resource.getDefaultResource(columns.get(j));
					Map<String, PolicyInfoVO.Resource> map = new HashMap<String, PolicyInfoVO.Resource>();
					map.put(mapKey1, tab);
					map.put(mapKey2, fam);
					map.put(mapKey3, colu);
					reList.add(map);
				}
			}
		}
	}

	private void cutOffResourceHdfsYarn(Map<String, PolicyInfoVO.Resource> resources, String mapKey,
			List<Map<String, PolicyInfoVO.Resource>> reList) {
		List<String> values = resources.get(mapKey).getValues();
		for (int i = 0; i < values.size(); i++) {
			Map<String, PolicyInfoVO.Resource> map = new HashMap<String, PolicyInfoVO.Resource>();
			PolicyInfoVO.Resource resource = PolicyInfoVO.Resource.getDefaultResource(values.get(i));
			map.put(mapKey, resource);
			reList.add(map);
		}
	}

	private void printList(String key, List<String> list) {
		StringBuilder sb = new StringBuilder();
		sb.append(key).append(": ");
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i)).append("  ");
		}
		System.out.println(sb.toString());
	}

	

	private boolean compareList(List<String> oldList, List<String> newList) {

		if (CollectionUtils.isEmpty(oldList) && CollectionUtils.isEmpty(newList)) {
			return false;
		}

		if (CollectionUtils.isEmpty(oldList) || CollectionUtils.isEmpty(newList)) {
			return false;
		}
		if (CollectionUtils.isEqualCollection(newList, oldList)) {
			return true;
		}

		return false;
	}

	/**
	 * @Description: 获取某个repository所有的policy信息 @param @param repository
	 *               ：需要获取repolistory的名称 @param @return @param @throws
	 *               Exception @throws
	 */
	public PolicyUserInfoVo getAllPolicyOfRepository(String repository) {
		// 根据repository的名称去查找，看在ranger上面是否存在
		if (StringUtils.isEmpty(repository)) {
			logger.error("Fail to get policy of repository. Reason: the name of repository is empty");
			return null;
		}
		RepositoryService repo = null;
		try {
			repo = this.SearchRepository(repository);
		} catch (Exception e) {
			logger.error("Fail to find " + repository, e);
			return null;
		}
		// 不存在，直接返回
		if (null == repo) {
			logger.error("The repository -- " + repository + " is not exist. Please check.");
			return null;
		}
		// 2. 查找此repository下面的所有的policy, 首先构造相应的URL
		// http://admin24.example.com:6080/service/plugins/policies/service/3?page=0&pageSize=25&startIndex=0&policyType=0&_=1502261063682
		StringBuilder urlSb = new StringBuilder();
		// 这里的pageSize设置一个比较大的值，
		urlSb.append(this.getUrl() + RangerStaticInfo.RANGER_POLICY).append("service/").append(repo.getId())
				.append("?page=0&pageSize=25000&startIndex=0&policyType=0&_=1502261063682");
		String retStr = null;
		try {
			retStr = this.executeHttpGet(urlSb.toString());
		} catch (Exception e) {
			logger.error("Fail to get policy from " + repository, e);
			return null;
		}
		PolicyUserInfoVo retVO = gson.fromJson(retStr, PolicyUserInfoVo.class);

		return retVO;
	}

	private String executeHttpGet(String url) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Content-Type", "application/json;charset=UTF-8");
		httpGet.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		CloseableHttpResponse response = super.excute(httpGet);
		HttpEntity responseEntity = response.getEntity();
		String retStr = EntityUtils.toString(responseEntity);
		return retStr;

	}

	public PolicyUserInfoVo getRangerPolicy(int resitoryId, String username, String resource) throws Exception {
		StringBuilder urlsb = new StringBuilder();
		urlsb.append(this.getUrl() + RangerStaticInfo.RANGER_POLICY).append("service/").append(resitoryId);
		urlsb.append("?page=0&pageSize=2500&total_pages=1&totalCount=2&startIndex=0&policyType=0&");
		if (!StringUtils.isEmpty(username)) {
			urlsb.append("user=").append(username).append("&");
		}
		urlsb.append("_=1502180762935");
		System.out.println(urlsb.toString());

		HttpGet httpGet = new HttpGet(urlsb.toString());
		httpGet.addHeader("Content-Type", "application/json;charset=UTF-8");
		httpGet.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		CloseableHttpResponse response = super.excute(httpGet);
		HttpEntity responseEntity = response.getEntity();
		String retStr = EntityUtils.toString(responseEntity);
		logger.info(retStr);
		PolicyUserInfoVo retVO = gson.fromJson(retStr, PolicyUserInfoVo.class);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new Exception("Fail to get policy. statusCode = " + response.getStatusLine().getStatusCode());
		}
		return retVO;

	}



	/**
	 * @Description: 判断用户是否存在。需要注意，如果用户不存在，创建policy或者update的时候，都会失败 @param @param
	 *               username ：需要检查的用户信息 @param @return :
	 *               如果全部用户都存在，则返回true，否则返回false @param @throws Exception @throws
	 */

	public boolean checkUser(List<String> username) {
		int ret = 0;
		for (int i = 0; i < username.size(); i++) {
			try {
				PolicyUserInfoVo retVo = this.checkUserGroup(username.get(i), "user");
				if (this.isExist(username.get(i), retVo, "user")) {
					ret++;
				}
			} catch (Exception e) {
				return false;
			}
		}
		if (ret == username.size()) {
			return true;
		}
		return false;
	}

	/**
	 * @Description: 判断用户组是否存在。需要注意，如果用户组不存在，创建policy或者update的时候，都会失败 @param @param
	 *               groups ：需要检查的用户组信息 @param @return :
	 *               如果全部用户组都存在，则返回true，否则返回false @param @throws Exception @throws
	 */

	public boolean checkGroup(List<String> groups) {

		int count = 0;
		for (int i = 0; i < groups.size(); i++) {
			try {
				PolicyUserInfoVo retVo = this.checkUserGroup(groups.get(i), "group");
				if (this.isExist(groups.get(i), retVo, "group")) {
					count++;
				}
			} catch (Exception e) {
				return false;
			}
		}
		if (count == groups.size()) {
			return true;
		}
		return false;
	}

	private boolean isExist(String name, PolicyUserInfoVo policyVo, String type) {
		if (type.equals("user")) {
			List<VXUsers> users = policyVo.getvXUsers();
			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).getName().equals(name)) {
					return true;
				}
			}
		} else {
			List<VXGroups> users = policyVo.getvXGroups();
			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).getName().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}

	private PolicyUserInfoVo checkUserGroup(String name, String type) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getUrl()).append("/service/xusers/");
		if (type.equals("user")) {
			sb.append("users");
		} else if (type.equals("group")) {
			sb.append("groups");
		} else {
			return null;
		}
		HttpGet httpGet = new HttpGet(sb.toString());
		httpGet.addHeader("Content-Type", "application/json;charset=UTF-8");
		httpGet.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		CloseableHttpResponse response = super.excute(httpGet);
		HttpEntity responseEntity = response.getEntity();
		String retStr = EntityUtils.toString(responseEntity);
		PolicyUserInfoVo retVO = gson.fromJson(retStr, PolicyUserInfoVo.class);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new Exception("");
		}
		return retVO;
	}



	public RangerServiceListVO SearchRepository() throws Exception {
		logger.info("Search repository  ...");
		RangerServiceListVO retVO = null;

		HttpGet httpGet = new HttpGet(this.getUrl() + RangerStaticInfo.RANGER_SEARCH_ALL_REPOSITIORY);
		// HttpDelete httpGet = new HttpDelete(serviceUrl);
		httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
		CloseableHttpResponse response = super.excute(httpGet);
		HttpEntity responseEntity = response.getEntity();
		if (null != responseEntity) {
			String retStr = EntityUtils.toString(responseEntity);
//			System.out.println(retStr);
			checkResult(super.getResultVO(responseEntity, retStr));
			retVO = gson.fromJson(retStr, RangerServiceListVO.class);
		}

		return retVO;
	}

	public RepositoryService SearchRepository(String repositoryName) throws Exception {
		RangerServiceListVO ret = this.SearchRepository();
		if (ret == null) {
			return null;
		}
		List<RepositoryService> list = ret.getServices();
		for (int i = 0; i < list.size(); i++) {
			if (repositoryName.equalsIgnoreCase(list.get(i).getName())) {
				return list.get(i);
			}
		}

		return null;
	}

}

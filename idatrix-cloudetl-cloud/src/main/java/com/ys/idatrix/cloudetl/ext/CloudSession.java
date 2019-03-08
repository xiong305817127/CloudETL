/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.www.CarteSingleton;
import org.pentaho.di.www.SlaveServerConfig;
import org.pentaho.metastore.stores.delegate.DelegatingMetaStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Maps;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.sso.client.UserHolder;
import com.idatrix.unisecurity.sso.client.model.SSOUser;
import com.ys.idatrix.cloudetl.reference.user.CloudUserService;
import com.ys.idatrix.cloudetl.repository.database.SystemSettingDao;

/**
 * Cloud ETL user session environment initiation. <br/>
 * 	- Initiate user repository (for cloud/trans/jobs/logs) <br/>
 * 	- Initiate user meta store (for pentaho or idatrix resources) <br/>
 * 	- User session information (login user name, role, etc.) <br/>
 * 
 * @author JW
 * @since 2017年7月20日
 *
 */
public class CloudSession {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CloudSession.class);
	
	private static final ThreadLocal<Map<String,String>> userThreadLocal = new ThreadLocal<Map<String,String>>();

	// SSO properties for user
	public static final String SSO_PROPERTY_IS_RENT = "isRenter";
	public static final String SSO_PROPERTY_RENT_ID = "renterId";
	public static final String SSO_PROPERTY_USER_NAME = "username";
	public static final String SSO_PROPERTY_USER_ROLE = "role";

	// Session attributes for user
	public static final String ATTR_SESSION_IS_RENTER = "ATTR_SESSION_IS_RENTER";
	public static final String ATTR_SESSION_RENTER_ID = "ATTR_SESSION_RENTER_ID";
	public static final String ATTR_SESSION_USER_ROLE = "ATTR_SESSION_USER_ROLE";
	public static final String ATTR_SESSION_USER_NAME = "ATTR_SESSION_USER_NAME";
	public static final String ATTR_SESSION_RESOURCE_USER = "ATTR_SESSION_RESOURCE_USER";


	public static HttpServletRequest getRequest() {
		try {
			return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		} catch (Exception e) {
			return null;
		}
	}

	public static HttpSession getSession() {
		try {
			return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession(true);
		} catch (Exception e) {
			return null;
		}
	}

	public static HttpServletResponse getResponse() {
		try {
			return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
		} catch (Exception e) {
			return null;
		}
	}

	public static ServletContext getContext() {
		try {
			return ContextLoader.getCurrentWebApplicationContext().getServletContext();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取当前登录的用户名(执行者)
	 * @return
	 */
	public static String getLoginUser() {
		String username = "";

		// Get login user from session
		HttpSession session = getSession();
		if (session != null) {
			Object loginUser = session.getAttribute(ATTR_SESSION_USER_NAME);
			username = loginUser != null ? (String) loginUser : "";
			LOGGER.debug("[SESSION] USER: " + username );
		}

		// If user not found in session, try get it from SSO again
		if (Utils.isEmpty(username)) {
			SSOUser user = UserHolder.getUser( getRequest() );
			username = user != null ? (String) user.getProperty(SSO_PROPERTY_USER_NAME) : "";
			LOGGER.debug("[SSO] USER: " + username );
		}
		
		// If user not found in sso, try get it from Thread again
		if (Utils.isEmpty(username)) {
			username = ( getThreadInfo() != null && getThreadInfo().containsKey(ATTR_SESSION_USER_NAME)) ? (String) getThreadInfo().get(ATTR_SESSION_USER_NAME) : "";
			LOGGER.debug("[SSO] Thread USER: " + username );
		}
		
		// If user not found in Thread, try get it from Thread Name again
		if(Utils.isEmpty(username)){
			username = Utils.getExecUserByThreadName(null);
			LOGGER.debug("[Thread] Thread USER: " + username );
		}
		
		if(Utils.isEmpty(username) && !IdatrixPropertyUtil.getBooleanProperty("idatrix.security.deployment")){
			//未开启安全认证登录
			SlaveServerConfig slaveConfig = CarteSingleton.getSlaveServerConfig() ;
			if( slaveConfig != null && slaveConfig.getSlaveServer() != null) {
				username = slaveConfig.getSlaveServer().getUsername();
			}
		}

		return username;
	}


	/**
	 * 获取当前登录的租户Id
	 */
	public static String getLoginRenterId(){
		String renterId = "";

		HttpSession session = getSession();
		if (session != null) {
			Object loginRenter = session.getAttribute(ATTR_SESSION_RENTER_ID);
			renterId = loginRenter != null ? (String) loginRenter : "";
			LOGGER.debug("[SESSION] renterId: " + renterId );
		}

		// If renterId not found in session, try get it from SSO again
		if (Utils.isEmpty(renterId)) {
			SSOUser user = UserHolder.getUser( getRequest() );
			renterId = user != null ? (String) user.getProperty(SSO_PROPERTY_RENT_ID) : "";
			LOGGER.debug("[SSO] USER renterId: " + renterId );
		}
		
		// If renterId not found in sso, try get it from Thread again
		if (Utils.isEmpty(renterId)) {
			renterId = ( getThreadInfo() != null &&  getThreadInfo().containsKey(ATTR_SESSION_RENTER_ID) ) ? getThreadInfo().get(ATTR_SESSION_RENTER_ID) : "";
			LOGGER.debug("[SSO] Thread USER renterId: " + renterId );
		}
		
		// If renterId not found in sso, try get it from unisecurity again
		if (Utils.isEmpty(renterId) || "0".equals(renterId)) {
			CloudUserService cloudUserService = PluginFactory.getBean(CloudUserService.class);
			if( cloudUserService != null ) {
				User userInfo = cloudUserService.getUserInfo(getLoginUser()); //注意不能调用 getRenterInfo ,会造成死循环
				if(  userInfo != null ) {
					renterId = userInfo.getRenterId().toString() ;
					setThreadInfo(ATTR_SESSION_RENTER_ID,renterId);
				}
			}
		}
		
		if(Utils.isEmpty(renterId) && !IdatrixPropertyUtil.getBooleanProperty("idatrix.security.deployment")){
			//未开启安全认证登录
			return "0" ;
		}
		
		return renterId;
	}
	
	/**
	 * 当前登录用户是否是租户
	 */
	public static boolean isLoginRenter(){
		String isRenter = "";

		HttpSession session = getSession();
		if (session != null) {
			Object loginIsRenter = session.getAttribute(ATTR_SESSION_IS_RENTER);
			isRenter = loginIsRenter != null ? loginIsRenter.toString() : "";
			LOGGER.debug("[SESSION] isRenter: " + isRenter );
		}

		// If isRenter not found in session, try get it from SSO again
		if (Utils.isEmpty(isRenter)) {
			SSOUser user = UserHolder.getUser( getRequest() );
			isRenter = user != null ?  Boolean.toString( (Boolean) user.getProperty(SSO_PROPERTY_IS_RENT) ): "";
			LOGGER.debug("[SSO] USER isRenter: " + isRenter );
		}
		
		// If renterId not found in sso, try get it from Thread again
		if (Utils.isEmpty(isRenter)) {
			isRenter = ( getThreadInfo() != null &&  getThreadInfo().containsKey(ATTR_SESSION_IS_RENTER) ) ? getThreadInfo().get(ATTR_SESSION_IS_RENTER) : "";
			LOGGER.debug("[SSO] Thread USER isRenter: " + isRenter );
		}
		// If isRenter not found in sso, try get it from unisecurity again
		if (Utils.isEmpty(isRenter)) {
			CloudUserService cloudUserService = PluginFactory.getBean(CloudUserService.class);
			if( cloudUserService != null ) {
				Boolean isRenterT = cloudUserService.isRenter(getLoginUser()); //注意不能调用 getRenterInfo ,会造成死循环
				if(  isRenterT != null ) {
					isRenter = isRenterT.toString() ;
				}
			}
		}
		
		if(Utils.isEmpty(isRenter) && !IdatrixPropertyUtil.getBooleanProperty("idatrix.security.deployment")){
			//未开启安全认证登录
			return true ;
		}
		
		return Boolean.valueOf(isRenter);
	}

	/**
	 * 获取当前要操作的资源的拥有者
	 * @return
	 */
	public static String getResourceUser() {
		String resourceUser = getCurrentResourceUser();
		if(Utils.isEmpty(resourceUser)  ){
			resourceUser = getLoginUser() ;
		}
		return resourceUser;
	}
	
	public static String getCurrentResourceUser() {
		String username = "";

		// Get login user from session
		HttpSession session = getSession();
		if (session != null) {
			Object resourceUser = session.getAttribute(ATTR_SESSION_RESOURCE_USER);
			username = resourceUser != null ? (String) resourceUser : "";
			LOGGER.debug("[SESSION] USER: " + username );
		}

		// If user not found in sso, try get it from Thread again
		if (Utils.isEmpty(username)) {
			username = ( getThreadInfo() != null && getThreadInfo().containsKey(ATTR_SESSION_RESOURCE_USER)) ? (String) getThreadInfo().get(ATTR_SESSION_RESOURCE_USER) : "";
			LOGGER.debug("[SSO] Thread USER: " + username );
		}
		
		// If user not found in Thread, try get it from Thread Name again
		if(Utils.isEmpty(username)){
			username = Utils.getOwnerUserByThreadName(null);
			LOGGER.debug("[Thread] Thread USER: " + username );
		}
		
		return username;
	}
	
	/**
	 * 当前登录用户(执行者)是否拥有租户权限 <br>
	 * 默认是  当前系统配置可使用超级权限
	 * @return
	 */
	public static boolean isRenterPrivilege() {
		Boolean privilege = IdatrixPropertyUtil.getBooleanProperty("idatrix.renter.super.privilege.enable", false);
		return privilege  ;
		
	}
	
	/**
	 * 当前登录用户(执行者)是否拥有超级管理权限 <br>
	 * 默认是  当前系统配置可使用超级权限,且当前登录用户是租户
	 * @return
	 */
	public static String SuperPrivilegeRoleId = "SuperPrivilegeRoleId" ;
	public static boolean isSuperPrivilege() {
		if( isRenterPrivilege() ) {
			if( isLoginRenter() ) {
				//租户 默认拥有权限
				return true ;
			}
			//操作自己的资源,拥有权限
			if( getLoginUser().equals(getCurrentResourceUser())) {
				return true ;
			}
			//是否配置了管理员权限
			try {
				String roleId =  SystemSettingDao.getInstance().getSettingValue(SuperPrivilegeRoleId) ;
				if(!Utils.isEmpty( roleId)) {
					CloudUserService cloudUserService = PluginFactory.getBean(CloudUserService.class);
					if( cloudUserService != null ) {
						 List<String> users = cloudUserService.getUserNamesNyRolesAndRenter(Integer.valueOf(roleId), Long.valueOf(getLoginRenterId())); 
						if( users.contains(getLoginUser() ) ) {
							return true;
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("获取管理权限角色失败.",e);
			}
			
			return false ; 
		}else {
			//未启用租户机制,各自都有权限
			return true ;
		}
	}
	
	private static Map<String,String> getThreadInfo() {
		return userThreadLocal.get();
	}
	
	/**
	 * 设置当前线程用户信息<br>
	 * keys : <br>
	 * 	ATTR_SESSION_IS_RENTER 		: 登陆用户是否是租户 <br>
	 * 	ATTR_SESSION_RENTER_ID 		: 登陆用户租户ID  <br>
	 * 	ATTR_SESSION_USER_ROLE 		: 登陆用户角色             <br>
	 * 	ATTR_SESSION_USER_NAME 		: 登陆用户名		<br>
	 * 	ATTR_SESSION_RESOURCE_USER 	: 要使用的资源用户名  <br>
	 * @param key
	 * @param value
	 */
	public static void setThreadInfo(String key , String value) {
		if( userThreadLocal.get() == null ) {
			Map<String,String> user = Maps.newConcurrentMap() ;
			user.put(Const.NVL(key, ATTR_SESSION_USER_NAME),value);
			userThreadLocal.set(user );
		}else {
			userThreadLocal.get().put(Const.NVL(key, ATTR_SESSION_USER_NAME),value);
		}
	}
	
	/**
	 * 设置当前线程登录用户名
	 * @param username
	 */
	public static void setThreadLoginUser(String username ) {
		if( userThreadLocal.get() == null ) {
			Map<String,String> user = Maps.newConcurrentMap() ;
			user.put( ATTR_SESSION_USER_NAME ,username);
			userThreadLocal.set(user );
		}else {
			userThreadLocal.get().put(  ATTR_SESSION_USER_NAME ,username);
		}
	}
	
	/**
	 * 设置当前线程要使用的资源拥有者名
	 * @param username
	 */
	public static void setThreadResourceUser(String username ) {
		if( userThreadLocal.get() == null ) {
			Map<String,String> user = Maps.newConcurrentMap() ;
			user.put( ATTR_SESSION_RESOURCE_USER ,username);
			userThreadLocal.set(user );
		}else {
			userThreadLocal.get().put(  ATTR_SESSION_RESOURCE_USER ,username);
		}
	}
	
	/**
	 * 清理当前线程用户信息
	 * @param keys
	 */
	public static void clearThreadInfo(String... keys) {
		if( userThreadLocal.get() != null ) {
			if( Utils.isEmpty(keys) ) {
				userThreadLocal.remove();
			}else  {
				for( String key : keys ) {
					userThreadLocal.get().remove(key);
				}
			}
		}
		HttpSession session = getSession();
		if (session != null) {
			session.removeAttribute(ATTR_SESSION_IS_RENTER );
			session.removeAttribute(ATTR_SESSION_RENTER_ID );
			session.removeAttribute(ATTR_SESSION_USER_ROLE );
			session.removeAttribute(ATTR_SESSION_USER_NAME  );
			session.removeAttribute(ATTR_SESSION_RESOURCE_USER );
		}
	}
	
	//#####################################当前 session 资源#########################################################
	
	public static DelegatingMetaStore getMetaStore() throws Exception {
		return CloudApp.getInstance().getMetaStore( getResourceUser());
	}
	
	/**
	 * Initiate user cloud repository.
	 * @param userRepoPath
	 * @return
	 */
	public static String getCloudRepositoryPath() {
		return CloudApp.getInstance().getCloudRepositoryPath(getResourceUser());
	}

	/**
	 * Initiate user trans repository.
	 * @param userRepoPath
	 * @return
	 */
	public static String getUserTransRepositoryPath(String group) {
		return CloudApp.getInstance().getUserTransRepositoryPath(getResourceUser(),group);
	}

	/**
	 * Initiate user jobs repository.
	 * @param userRepoPath
	 * @return
	 */
	public static String getUserJobsRepositoryPath(String group) {
		return CloudApp.getInstance().getUserJobsRepositoryPath(getResourceUser(),group);
	}

	/**
	 * Initiate user logs repository.
	 * @param userRepoPath
	 * @return
	 */
	public static  String getUserLogsRepositoryPath() {
		return CloudApp.getInstance().getUserLogsRepositoryPath(getResourceUser());
	}

	
	/**
	 * Get given current user Jndi path in repository.
	 * @param username
	 * @return
	 */
	public static String getUserJndiRepositoryPath() {
		return CloudApp.getInstance().getUserJndiRepositoryPath(getResourceUser());
	}
	

}

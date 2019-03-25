package com.idatrix.unisecurity.server.token;

import com.alibaba.fastjson.JSON;
import com.idatrix.unisecurity.common.domain.ClientSystem;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.utils.SpringContextUtil;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.core.shiro.filter.KickoutSessionFilter;
import com.idatrix.unisecurity.core.shiro.session.dao.ShiroSessionRepository;
import com.idatrix.unisecurity.properties.LoginProperties;
import com.idatrix.unisecurity.user.Config;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端：
 * 令牌管理
 * 做了一个本地缓存
 */
public class TokenManager {

	private static Logger logger = LoggerFactory.getLogger(TokenManager.class);

	// 设置为守护线程
	private static final Timer timer = new Timer(true);

	private static final Config config = SpringContextUtil.getBean(Config.class);

    private static final ShiroSessionRepository shiroSessionRepository = SpringContextUtil.getBean(ShiroSessionRepository.class);

    private static final LoginProperties loginProperties = SpringContextUtil.getBean(LoginProperties.class);

	// 令牌存储结构，支持同步的HashMap，定时有效性管理部分，当我们MAP中的某一个实体已经过期的时候我们要从MAP中直接移除，对于在迭代的过移中要进行一些改变，例如移出或者新增
	// 如果使用的普通的MAP，他会报一个同步的错误，使用同步的map会进行一个对应的处理
	private static final ConcurrentHashMap<String, Token> DATA_MAP = new ConcurrentHashMap<String, Token>();

	private static class Token {
		private UUser loginUser; // 登录用户对象
		private Date expired; // 过期时间
	}

	// 避免静态类被实例化
	private TokenManager() {
	}

	static {
		// 每隔1分种，执行1次
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				for (Entry<String, Token> entry : DATA_MAP.entrySet()) {
					// 循环每一个会话缓存
					String vt = entry.getKey();

					Token token = entry.getValue();
					Date expired = token.expired;
					Date now = new Date();

					// 当前时间大于过期时间
					if (now.compareTo(expired) > 0) {
						// 因为令牌支持自动延期服务，并且应用客户端缓存机制后，
						// 令牌最后访问时间是存储在客户端的，所以服务端向所有客户端发起一次timeout通知，
						// 客户端根据lastAccessTime + tokenTimeout计算是否过期,
						// 若未过期，用各客户端最大有效期更新当前过期时间
						List<ClientSystem> clientSystems = config.getClientSystems();
						Date maxClientExpired = expired;// 客户端中最大活动时间
						for (ClientSystem clientSystem : clientSystems) {
							Date clientExpired = clientSystem.noticeTimeout(vt, config.getTokenTimeout()); // 获取客户端的最后过期时间
							if (clientExpired != null && clientExpired.compareTo(now) > 0) {
								maxClientExpired = maxClientExpired.compareTo(clientExpired) < 0 ? clientExpired : maxClientExpired;
							}
						}
						if (maxClientExpired.compareTo(now) > 0) { // 客户端过期时间大于当前
							logger.debug("更新过期时间到" + maxClientExpired);
                            // 更新map中的过期时间
                            token.expired = maxClientExpired;
							// 更新shiro-redis的值,只有在客户端时间大于安全中的记录时间时才会去更新。而且每次更新都是最后的一个时间。
							Session userSession = shiroSessionRepository.getSession(vt);
							if(userSession != null) {
								// 重置过期时间
								userSession.setTimeout(loginProperties.getLoginTokenKeyTimeout() * 1000); // 注意这里是毫秒，所以需要转换一下
								//shiroSessionRepository.saveSession(userSession);
							}
						} else {
							logger.debug("清除过期token：" + vt);
							DATA_MAP.remove(vt);
							for (ClientSystem clientSystem : clientSystems) {// 通知各子系统退出
								clientSystem.noticeLogout(vt);
							}
						}
					}
				}
			}
		}, 60 * 1000, 60 * 1000);
	}

	/**
	 * 验证令牌有效性
	 * @return
	 */
	public static UUser validate(String vt) {
		logger.info("validate vt :"+ vt);
	    if (vt == null) {
	    	return null;
	    }
		Token token = DATA_MAP.get(vt);
	    // 先从本地缓存中取值
	    if(token != null){
	    	logger.info("server 本地缓存中获取到ssoUser：" + token);
			return token.loginUser;
		}

		// 如果当前服务器缓存中是空的，去shiro-redis中去取值
		Session session = shiroSessionRepository.getSession(vt);
		//Session session = sessionDAO.doReadSession(vt);
		if(session != null){
			logger.info("server 根据sessionId获取成功session");
			if(session.getAttribute(KickoutSessionFilter.KICKOUT_STATUS)!=null && (Boolean)session.getAttribute(KickoutSessionFilter.KICKOUT_STATUS) == true) {
				logger.info("server 当前的用户被标记为踢出状态");
				return null;
			}
			Object obj = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
			if(obj == null){
				return null;
			}
			SimplePrincipalCollection coll = (SimplePrincipalCollection) obj;
			UUser loginUser = (UUser) coll.getPrimaryPrincipal();

            token = new Token();
            token.loginUser = loginUser;
            long timeout = session.getTimeout();
            token.expired = new Date(new Date().getTime() + timeout);
			DATA_MAP.put(vt, token);

			// 更新shiro-redis的值
			shiroSessionRepository.saveSession(session);
			//sessionDAO.update(session);
			logger.info("server 获取user成功！！！");
			return token.loginUser;
		}
		return null;
	}

	/**
	 * 验证令牌有效性
	 * @return
	 */
	public static ResultVo validate(String vt, String code) {
		logger.debug("validate vt :"+ vt);
		if (StringUtils.isEmpty(vt)) {
			return ResultVoUtils.error(ResultEnum.USER_NOT_LOGIN.getCode(), ResultEnum.USER_NOT_LOGIN.getMessage());
		}
		Token token = DATA_MAP.get(vt);
		// 先从服务端本地缓存中取值
		if(token != null){
			logger.debug("server 本地缓存中获取到ssoUser：" + token);
			return ResultVoUtils.ok(token.loginUser);
		}
		// 如果当前服务端缓存中是空的，去shiro-redis中去取值
		Session userSession = shiroSessionRepository.getSession(vt);
		if(userSession == null){
			return ResultVoUtils.error(ResultEnum.USER_LOGIN_OVERDUE.getCode(), ResultEnum.USER_LOGIN_OVERDUE.getMessage());
		}
		logger.debug("server 根据sessionId获取成功session");
		if(userSession.getAttribute(KickoutSessionFilter.KICKOUT_STATUS)!=null && (Boolean)userSession.getAttribute(KickoutSessionFilter.KICKOUT_STATUS) == true) {
			logger.debug("server 当前的用户被标记为踢出状态");
			return ResultVoUtils.error(ResultEnum.USER_KICKED_OUT.getCode(), ResultEnum.USER_KICKED_OUT.getMessage());
		}
		Object obj = userSession.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
		if(obj == null){
			return ResultVoUtils.error(ResultEnum.USER_LOGIN_OVERDUE.getCode(), ResultEnum.USER_LOGIN_OVERDUE.getMessage());
		}
		SimplePrincipalCollection coll = (SimplePrincipalCollection) obj;
		UUser loginUser = (UUser) coll.getPrimaryPrincipal();

        // 更新shiro-redis的过期时间
        userSession.setTimeout(loginProperties.getLoginTokenKeyTimeout() * 1000);

        token = new Token();
		token.loginUser = loginUser;
		long timeout = userSession.getTimeout();
		token.expired = new Date(new Date().getTime() + timeout);
		DATA_MAP.put(vt, token);

		// shiroSessionRepository.saveSession(userSession);
		logger.debug("server 获取user成功！！！");
		return ResultVoUtils.ok(token.loginUser);
	}

	/**
	 * 用户授权成功后将授权信息存入
	 * @param vt
	 * @param loginUser
	 */
	public static synchronized void addToken(String vt, UUser loginUser) {
		Token token = new Token();
		// 设置user
		token.loginUser = loginUser;
		// 设置过期时间
		Date now = new Date();
        token.expired = new Date(now.getTime() + loginProperties.getLoginTokenKeyTimeout() * 1000L);
		logger.debug("tokenManager addToken user :", JSON.toJSONString(loginUser));
		logger.debug("tokenManager addToken expired :", token.expired);
		DATA_MAP.put(vt, token);
	}
	
	public static void invalid(String vt) {
		logger.info("tokenManager remove vt :" + vt);
		if (vt != null) {
			DATA_MAP.remove(vt);
		}
	}
}

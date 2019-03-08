package com.idatrix.unisecurity.core.shiro.token.manager;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.utils.SpringContextUtil;
import com.idatrix.unisecurity.core.shiro.session.CustomSessionManager;
import com.idatrix.unisecurity.core.shiro.token.SampleRealm;
import com.idatrix.unisecurity.core.shiro.token.ShiroToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.SimplePrincipalCollection;

import java.util.List;

/**
 * 开发公司：粤数大数据
 * author gongjian
 */
public class ShiroTokenManager {
    
    // 用户登录管理
    public static final SampleRealm realm = SpringContextUtil.getBean("sampleRealm", SampleRealm.class);

    // 用户session管理
    public static final CustomSessionManager customSessionManager = SpringContextUtil.getBean("customSessionManager", CustomSessionManager.class);

    /**
     * 获取当前用户的Session
     * @return
     */
    public static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    /**
     * 获取当前登录的用户User对象
     * @return
     */
    public static UUser getToken() {
        UUser token = (UUser) SecurityUtils.getSubject().getPrincipal();
        return token;
    }

    /**
     * 获取当前用户NAME
     * @return
     */
    public static String getUsername() {
        return getToken().getUsername();
    }

    /**
     * 获取当前用户ID
     * @return
     */
    public static Long getUserId() {
        return getToken() == null ? null : getToken().getId();
    }

    /**
     * 把值放入到当前登录用户的Session里
     * @param key
     * @param value
     */
    public static void setVal2Session(Object key, Object value) {
        getSession().setAttribute(key, value);
    }

    /**
     * 从当前登录用户的Session里取值
     * @param key
     * @return
     */
    public static Object getVal2Session(Object key) {
        return getSession().getAttribute(key);
    }

    /**
     * 删除session中的某个值
     * @param key
     */
    public static void remove2Session(Object key) {
        getSession().removeAttribute(key);
    }

    /**
     *获取当前会话的id
     */
    public static String getSessionId() {
        return (String) getSession().getId();
    }

    /**
     * 判断是否登录
     * @return
     */
    public static boolean isLogin() {
        return null != SecurityUtils.getSubject().getPrincipal();
    }

    public static Boolean isAuthenticatedOrIsRemembered(){
        if(SecurityUtils.getSubject().isAuthenticated() || SecurityUtils.getSubject().isRemembered()){
            // 当期用户已经通过shiro认证 或 已被记住
            return true;
        }
        return false;
    }

    /**
     * 登录
     * @param user
     * @param rememberMe
     * @return
     */
    public static synchronized UUser login(UUser user, Boolean rememberMe) {
        ShiroToken token = new ShiroToken(user.getUsername(), user.getPswd());
        token.setRememberMe(rememberMe);
        SecurityUtils.getSubject().login(token);
        return getToken();
    }

    /**
     * 退出登录
     */
    public static void logout() {
        SecurityUtils.getSubject().logout();
    }

    /**
     * 清空当前用户权限信息。
     * 目的：为了在判断权限的时候，再次会再次 <code>doGetAuthorizationInfo(...)  </code>方法。
     * ps：	当然你可以手动调用  <code> doGetAuthorizationInfo(...)  </code>方法。
     * 这里只是说明下这个逻辑，当你清空了权限，<code> doGetAuthorizationInfo(...)  </code>就会被再次调用。
     */
    public static void clearNowUserAuth() {
        /**
         * 这里需要获取到shrio.xml 配置文件中，对Realm的实例化对象。才能调用到 Realm 父类的方法。
         */
        /**
         * 获取当前系统的Realm的实例化对象，方法一（通过 @link org.apache.shiro.web.mgt.DefaultWebSecurityManager 或者它的实现子类的{Collection<Realm> getRealms()}方法获取）。
         * 获取到的时候是一个集合。Collection<Realm>
         RealmSecurityManager securityManager =
         (RealmSecurityManager) SecurityUtils.getSecurityManager();
         SampleRealm realm = (SampleRealm)securityManager.getRealms().iterator().next();
         */
        /**
         * 方法二、通过ApplicationContext 从Spring容器里获取实列化对象。
         */
        realm.clearCachedAuthorizationInfo();
    }

    /**
     * 根据UserIds清空权限信息。
     * @param
     */
    public static void clearUserAuthByUserId(Long... userIds) {
        if (null == userIds || userIds.length == 0) return;
        List<SimplePrincipalCollection> result = customSessionManager.getSimplePrincipalCollectionByUserId(userIds);
        if(CollectionUtils.isNotEmpty(result)) {
            for (SimplePrincipalCollection simplePrincipalCollection : result) {
                realm.clearCachedAuthorizationInfo(simplePrincipalCollection);
            }
        }
    }

    /**
     * 方法重载
     * @param userIds
     */
    public static void clearUserAuthByUserId(List<Long> userIds) {
        if (null == userIds || userIds.size() == 0) {
            return;
        }
        clearUserAuthByUserId(userIds.toArray(new Long[0]));
    }
}

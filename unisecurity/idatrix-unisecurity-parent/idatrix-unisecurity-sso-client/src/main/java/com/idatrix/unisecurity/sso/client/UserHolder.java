package com.idatrix.unisecurity.sso.client;

import com.idatrix.unisecurity.sso.client.model.SSOUser;
import com.idatrix.unisecurity.sso.client.utils.CookieUtil;
import com.idatrix.unisecurity.sso.client.utils.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 供业务系统使用的用户对象获取工具类
 */
public class UserHolder {

    private static Logger logger = LoggerFactory.getLogger(UserHolder.class);

    // 当前缓存是根据 VT 来存用户信息
    private static final ConcurrentHashMap<String, SSOUser> userCache = new ConcurrentHashMap<String, SSOUser>();

    // 当前缓存是根据 Name 来存用户信息
    // 将当前登录用户信息存放到ThreadLocal中，这样在没有单独开线程的情况下，业务系统任意代码位置都可以取得当前user
    // thread local 会出现数据穿透问题，所有线程获取的都是最新的user.
    private static final ThreadLocal<SSOUser> userThreadLocal = new ThreadLocal<SSOUser>();

    private UserHolder() {

    }

    /**
     * 获取SSOUser实例，此方法从ThreadLocal中获取，当调用处代码与请求主线程不处于同一线程时，此方法无效
     *
     * @return
     */
    public static SSOUser getUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String vt = CookieUtil.getVT(request);
        if (vt == null) {
            return null;
        }
        logger.debug(request.getAttribute("reqId") + ":" + request.getRequestedSessionId() + ",UserHolder get user VT:" + vt);
        SSOUser user = userCache.get(vt);
        if (user == null) {
            return null;
        }
        logger.debug(request.getAttribute("reqId") + ":" + request.getRequestedSessionId() + ",UserHolder get user user:" + GsonUtil.toJson(user));
        return user;
    }

    /**
     * 用户加入到request和threadLocal供业务系统调用<br>
     * 以default为方法作用范围，仅本包内代码可访问，将此方法对用户代码隐藏
     *
     * @param user
     * @param request
     * @return
     */
    public static void set(SSOUser user, String vt, HttpServletRequest request) {
        logger.debug(request.getAttribute("reqId") + "::" + request.getRequestedSessionId() + " :UserHolder set user vt:" + vt);
        if (userCache.get(vt) == null) {
            logger.debug(request.getAttribute("reqId") + "::" + request.getRequestedSessionId() + " :UserHolder set user vt=null:" + vt);
            logger.debug(request.getAttribute("reqId") + "::" + request.getRequestedSessionId() + " :UserHolder set user vt=null user:" + GsonUtil.toJson(user));
            userCache.put(vt, user);
        }
    }

    public static void remove(String vt) {
        userCache.remove(vt);
    }

    public static void destory() {
        userCache.clear();
    }


    /**
     * 从thread local获取SSOUser
     * just for cloudetl
     * @param args
     * @return public static SSOUser getUser(String args) {
    return userThreadLocal.get();
    } */

    /**
     * 设置user to threadlocal
     * @param username just for cloudetl

    public static void setUser(String username) {
    Map<String, Object> propertes = new HashMap<String, Object>();
    propertes.put("username", username);
    SSOUser user = new SSOUserImpl(username, propertes);
    userThreadLocal.set(user);
    }*/

    /*
    public static getRequest() {
        ServletRequestAttributes requestAttributs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        logger.debug("servlet request requestAttributs =" + requestAttributs);
        if (requestAttributs == null) {
            return null;
        }
        HttpServletRequest request = requestAttributs.getRequest();
    }*/
}

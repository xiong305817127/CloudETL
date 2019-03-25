package com.ys.idatrix.metacube.common.utils;

import com.alibaba.druid.util.StringUtils;
import com.idatrix.unisecurity.sso.client.UserHolder;
import com.idatrix.unisecurity.sso.client.model.SSOUser;
import com.ys.idatrix.metacube.common.exception.MetaDataException;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2019/1/15. 如果使用单元测试，则设置一个默认值
 */
public class UserUtils {

    private static String USER_NAME = "username";

    private static String IS_RENTER = "isRenter";

    private static String RENTER_ID = "renterId";

    private static String ROLE_CODES = "roleCodes";

    //异步执行 等获取不到用户时,临时存储用户的缓存
    private static volatile Map<String , SSOUser > threadCacheMap;
    
    // user id
    public static Long getUserId() {
        return Long.parseLong(getUser().getId());
    }

    // user name
    public static String getUserName() {
    	return (String) getUser().getProperties().get(USER_NAME);
    }

    // renterId
    public static Long getRenterId() {
            return Long.parseLong((String) getUser().getProperties().get(RENTER_ID));
    }

    // is renter
    public static Boolean isRenter() {
        return (Boolean) getUser().getProperties().get(IS_RENTER);
    }

    // role codes 一个用户可能对应多个角色
    public static List<String>  getRoleCodes() {
        List<String> list = new ArrayList<>();
        if (getUser().getProperties().get(ROLE_CODES) != null) {
            list = (List<String>) getUser().getProperties().get(ROLE_CODES);
        }
        return list;
    }

    /**
     * 增加缓存 , 在主线程中调用 , 当使用  @Async 等线程池 异步时方式时 , 在主线程中调用 <br><br>
     * 在 异步线程中调用  refreshCacheMap 方法 配合使用
     * 
     * @param key 自定义key(由于主从线程名称不可控),要根据异步方法参数变化,在 线程执行启动时 根据 自定义Id 调用 refreshCacheMap方法
     */
    public static void addCacheMap(String key ) {
    	SSOUser user = UserHolder.getUser(getRequest());
    	if( user == null ) {
    		return ;
    	}
    	addCacheMap(key, user);
    }
    
    /**
     * 刷新缓存key(缓存key替换) , 在异步线程中执行 , 将addCacheMap方法增加的 缓存的key 替换为当前线程名称.<br><br>
     * 配合  主线程 中调用的 addCacheMap 方法
     * @param key addCacheMap方法时 使用的自定义Key
     */
    public static void refreshCacheMap(String key ) {
    	if( threadCacheMap == null || !threadCacheMap.containsKey(key)) {
    		return ;
    	}
    	SSOUser user = threadCacheMap.get(key);
    	threadCacheMap.remove(key);
    	threadCacheMap.put(Thread.currentThread().getName(), user);
    }
    
    /**
     * 清理掉当前缓存 , 由于使用线程池 , 线程可能复用 ,在线程完毕时清理点当前缓存
     */
    public static void clearCacheMap( ) {
    	String key = Thread.currentThread().getName() ;
    	if(threadCacheMap != null && threadCacheMap.containsKey(key)) {
    		threadCacheMap.remove(key) ;
    	}
    }
    
    /**
     * 增加缓存 
     * @param key 默认使用当前线程名 
     * @param user 需要缓存的 用户信息对象
     */
    public static void addCacheMap(String key , SSOUser user) {
    	if( user == null ) {
    		return ;
    	}
    	if( threadCacheMap == null ) {
    		synchronized (UserUtils.class) {
    			if( threadCacheMap == null ) {
    				threadCacheMap = new ConcurrentHashMap<>() ;
    			}
			}
    	}
    	
    	if( StringUtils.isEmpty(key) ) {
    		key = Thread.currentThread().getName() ;
    	}
    	threadCacheMap.put(key, user);
    }
    
    private static SSOUser getUser() {
    	
    	SSOUser user = UserHolder.getUser(getRequest());
    	if( user != null ) {
    		return user ;
    	}
    	String key = Thread.currentThread().getName() ;
    	if(threadCacheMap != null && threadCacheMap.containsKey(key)) {
    		return threadCacheMap.get(key) ;
    	}

    	throw new MetaDataException("用户信息未找到!");
    }
    
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributs == null) {
            return null;
        }
        HttpServletRequest request = requestAttributs.getRequest();
        return request;
    }
    
}

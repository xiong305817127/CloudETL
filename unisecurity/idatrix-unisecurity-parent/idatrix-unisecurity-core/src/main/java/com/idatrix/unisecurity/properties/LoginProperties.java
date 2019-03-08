package com.idatrix.unisecurity.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 登录有关的一些属性配置
 * @ClassName LoginProperties
 * @Description TODO
 * @Author ouyang
 * @Date 2018/9/28 14:10
 * @Version 1.0
 **/
@Component
public class LoginProperties {

    @Value("${SECURE_MODE}")
    private Boolean secureMode;//是否强制为https请求

    // ===================== 登录成功后的令牌key start

    @Value("${LOGIN_TOKEN_KEY}")
    private String loginTokenKey;

    @Value("${REMEMBER_MY_TOKEN_KEY}")
    private String rememberMyTokenKey;

    @Value("${COOKIE.USER.NAME.KEY}")
    private String cookieUserNameKey;

    // ===================== 登录成功后的令牌key end



    // ==================== 令牌超时时间 start

    @Value("${LOGIN_TOKEN_KEY_TIMEOUT}")
    private Long loginTokenKeyTimeout;

    @Value("${REMEMBER_MY_TOKEN_KEY_TIMEOUT}")
    private Long rememberMyTokenKeyTimeout;

    // ==================== 令牌超时时间 end


    // =============== 限制失败登录次数 有关的配置 start

    @Value("${USER_LOGIN_COUNT_KEY}")
    private String userLoginCountKey;//用户登录次数的key

    @Value("${USER_IS_LOCK_KEY}")
    private String userIsLockKey;//用户是否被锁定的key

    @Value("${MAX_LOGIN_COUNT}")
    private Integer maxLoginCount;//最大登录次数

    @Value("${LOCK_TIME}")
    private Integer lockTime;//锁定时间

    // =============== 限制失败登录次数 有关的配置 end


    public Boolean getSecureMode() {
        return secureMode;
    }

    public void setSecureMode(Boolean secureMode) {
        this.secureMode = secureMode;
    }

    public String getLoginTokenKey() {
        return loginTokenKey;
    }

    public void setLoginTokenKey(String loginTokenKey) {
        this.loginTokenKey = loginTokenKey;
    }

    public String getRememberMyTokenKey() {
        return rememberMyTokenKey;
    }

    public void setRememberMyTokenKey(String rememberMyTokenKey) {
        this.rememberMyTokenKey = rememberMyTokenKey;
    }

    public Long getLoginTokenKeyTimeout() {
        return loginTokenKeyTimeout;
    }

    public void setLoginTokenKeyTimeout(Long loginTokenKeyTimeout) {
        this.loginTokenKeyTimeout = loginTokenKeyTimeout;
    }

    public Long getRememberMyTokenKeyTimeout() {
        return rememberMyTokenKeyTimeout;
    }

    public void setRememberMyTokenKeyTimeout(Long rememberMyTokenKeyTimeout) {
        this.rememberMyTokenKeyTimeout = rememberMyTokenKeyTimeout;
    }

    public String getUserLoginCountKey() {
        return userLoginCountKey;
    }

    public void setUserLoginCountKey(String userLoginCountKey) {
        this.userLoginCountKey = userLoginCountKey;
    }

    public String getUserIsLockKey() {
        return userIsLockKey;
    }

    public void setUserIsLockKey(String userIsLockKey) {
        this.userIsLockKey = userIsLockKey;
    }

    public Integer getMaxLoginCount() {
        return maxLoginCount;
    }

    public void setMaxLoginCount(Integer maxLoginCount) {
        this.maxLoginCount = maxLoginCount;
    }

    public Integer getLockTime() {
        return lockTime;
    }

    public void setLockTime(Integer lockTime) {
        this.lockTime = lockTime;
    }

    public String getCookieUserNameKey() {
        return cookieUserNameKey;
    }

    public void setCookieUserNameKey(String cookieUserNameKey) {
        this.cookieUserNameKey = cookieUserNameKey;
    }
}

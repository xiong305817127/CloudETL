package com.idatrix.unisecurity.user.service;

import com.idatrix.unisecurity.common.domain.ClientSystem;
import com.idatrix.unisecurity.common.domain.Credential;
import com.idatrix.unisecurity.common.domain.UUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 认证处理器
 */
public interface IAuthenticationHandler {

    /**
     * 加载所有的子系统列表
     * 查询数据库的方式
     *
     * @return
     */
    List<ClientSystem> loadClientSystem();

    /**
     * 加载所有的子系统列表
     * 加载配置文件方式
     *
     * @param configProperties
     * @throws Exception
     */
    List<ClientSystem> loadClientSystems(Properties configProperties) throws Exception;

    /**
     * 登录认证
     * 接受页面传递的参数，认证失败后，将失败信息返回
     *
     * @param credential
     * @param isValidateCode
     * @return
     * @throws Exception
     */
    UUser authenticate(Credential credential, Boolean isValidateCode) throws Exception;

    /**
     * 登录认证
     * 接受页面传递的参数，认证失败后，将失败信息返回
     *
     * @param name
     * @param password
     * @param rememberMe
     * @param backUrl
     * @param request
     * @param response
     * @param resultMap
     * @return
     * @throws Exception
     */
    Map<String, Object> authenticate(String name, String password, Boolean rememberMe, String backUrl,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception;

    void validateSuccess(UUser uUser, String backUrl, Map resultMap, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 获取当前登录用户所拥有系统ID列表
     *
     * @return 返回null表示全部
     * @throws Exception
     */
    List<ClientSystem> getClientSystems(UUser uUser) throws Exception;


    /**
     * 用户登出，也会将记住我清除
     * @param request
     * @param response
     * @return
     */
    void logout(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 通知客户端清除某个令牌的缓存
     * @param token
     */
    void systemLogout(String token);

    /**
     * 自动登录
     *
     * @param lt
     * @return
     * @throws Exception
     */
    UUser autoLogin(String lt) throws Exception;

    /**
     * 生成自动登录标识，也就是lt
     *
     * @param loginUser
     * @return
     * @throws Exception
     */
    String loginToken(UUser loginUser) throws Exception;

    /**
     * 清除用户自动登录信息
     *
     * @param loginUser
     * @return
     * @throws Exception
     */
    void clearLoginToken(UUser loginUser) throws Exception;
}

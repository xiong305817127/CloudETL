package com.idatrix.unisecurity.user;

import com.idatrix.unisecurity.common.domain.ClientSystem;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.user.service.IAuthenticationHandler;
import com.idatrix.unisecurity.user.service.IPreLoginHandler;
import com.idatrix.unisecurity.user.service.UserSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 应用配置信息
 */
public class Config implements ResourceLoaderAware {

    private static Logger logger = LoggerFactory.getLogger(Config.class);

    private ResourceLoader resourceLoader;

    private IPreLoginHandler preLoginHandler; // 登录前预处理器

    private IAuthenticationHandler authenticationHandler; // 鉴权处理器

    private List<ClientSystem> clientSystems = new ArrayList<ClientSystem>();// 子系统信息列表

    private UserSerializer userSerializer; // 用户信息转换序列化实现

    private String loginViewName = "/login"; // 登录页面视图名称 没有被用到

    private String indexViewName = "#home"; // 没有被用到

    private int tokenTimeout = 30; // 令牌有效期，单位为分钟，默认30分钟

    private boolean secureMode = false; // 是否必须为https

    private int autoLoginExpDays = 365; // 自动登录状态有效期限，默认一年

    private boolean validateCode = true;// 是否要判断验证码

    private String freeipaSwitch; //freeipa 开关

    /**
     * 重新加载配置，以支持热部署，用于做初始化
     *
     * @throws Exception
     */
    public void refreshConfig() throws Exception {
        // 加载config.properties
        Properties configProperties = new Properties();

        try {
            Resource resource = resourceLoader.getResource("classpath:config.properties");
            configProperties.load(resource.getInputStream());
        } catch (IOException e) {
            logger.warn("在classpath下未找到配置文件config.properties");
        }

        // vt有效期参数
        String configTokenTimeout = (String) configProperties.get("tokenTimeout");
        if (configTokenTimeout != null) {
            try {
                tokenTimeout = Integer.parseInt(configTokenTimeout);
                logger.debug("config.properties设置tokenTimeout={}", tokenTimeout);
            } catch (NumberFormatException e) {
                logger.warn("tokenTimeout参数配置不正确");
            }
        }

        // 是否仅https安全模式下运行
        String configScureMode = configProperties.getProperty("secureMode");
        if (configScureMode != null) {
            this.secureMode = Boolean.parseBoolean(configScureMode);
            logger.debug("config.properties设置secureMode={}", this.secureMode);
        }

        // 自动登录有效期
        String configAutoLoginExpDays = configProperties.getProperty("autoLoginExpDays");
        if (configAutoLoginExpDays != null) {
            try {
                autoLoginExpDays = Integer.parseInt(configAutoLoginExpDays);
                logger.debug("config.properties设置autoLoginExpDays={}", autoLoginExpDays);
            } catch (NumberFormatException e) {
                logger.warn("autoLoginExpDays参数配置不正确");
            }
        }

        // 加载客户端系统配置列表
        try {
            loadClientSystems(configProperties);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("加载client system配置失败");
        }

        try {
            Resource resource = resourceLoader.getResource("classpath:login_config.properties");
            configProperties.load(resource.getInputStream());
        } catch (IOException e) {
            logger.warn("在classpath下未找到配置文件login_config.properties");
        }
        this.validateCode = Boolean.parseBoolean(String.valueOf(configProperties.get("validate_code")));
        this.freeipaSwitch = String.valueOf(configProperties.get("freeipa.switch"));
    }

    // 加载客户端系统配置列表
    @SuppressWarnings("unchecked")
    private void loadClientSystems(Properties configProperties) throws Exception {
        clientSystems.clear();// 先清空
        List<ClientSystem> list = authenticationHandler.loadClientSystems(configProperties);
        clientSystems.addAll(list);
    }

    /**
     * 应用停止时执行，做清理性工作，如通知客户端logout，销毁时运行
     */
    public void destroy() {
        for (ClientSystem clientSystem : clientSystems) {
            clientSystem.noticeShutdown();
        }
    }

    /**
     * 获取当前认证处理器
     *
     * @return
     */
    public IAuthenticationHandler getAuthenticationHandler() {
        return authenticationHandler;
    }

    public void setAuthenticationHandler(IAuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }

    /**
     * 获取登录前预处理器
     *
     * @return
     */
    public IPreLoginHandler getPreLoginHandler() {
        return preLoginHandler;
    }

    public void setPreLoginHandler(IPreLoginHandler preLoginHandler) {
        this.preLoginHandler = preLoginHandler;
    }

    /**
     * 获取登录页面视图名称
     *
     * @return
     */
    public String getLoginViewName() {
        return loginViewName;
    }

    public void setLoginViewName(String loginViewName) {
        this.loginViewName = loginViewName;
    }

    public String getIndexViewName() {
        return indexViewName;
    }

    public void setIndexViewName(String indexViewName) {
        this.indexViewName = indexViewName;
    }

    /**
     * 获取令牌有效期，单位为分钟
     *
     * @return
     */
    public int getTokenTimeout() {
        return tokenTimeout;
    }

    public void setTokenTimeout(int tokenTimeout) {
        this.tokenTimeout = tokenTimeout;
    }

    /**
     * 客户端系统列表
     *
     * @return
     */
    public List<ClientSystem> getClientSystems() {
        return clientSystems;
    }

    public void setClientSystems(List<ClientSystem> clientSystems) {
        this.clientSystems = clientSystems;
    }

    /**
     * 获取指定用户的可用系统列表
     *
     * @return
     * @throws Exception
     */
    public List<ClientSystem> getClientSystems(UUser uUser) throws Exception {
        return getAuthenticationHandler().getClientSystems(uUser);
    }

    @Override
    public void setResourceLoader(ResourceLoader loader) {
        this.resourceLoader = loader;
    }

    public boolean isSecureMode() {
        return secureMode;
    }

    public int getAutoLoginExpDays() {
        return autoLoginExpDays;
    }

    public UserSerializer getUserSerializer() {
        return userSerializer;
    }

    public void setUserSerializer(UserSerializer userSerializer) {
        this.userSerializer = userSerializer;
    }

    public boolean isValidateCode() {
        return validateCode;
    }

    public String getFreeipaSwitch() {
        return freeipaSwitch;
    }

    public void setFreeipaSwitch(String freeipaSwitch) {
        this.freeipaSwitch = freeipaSwitch;
    }
}

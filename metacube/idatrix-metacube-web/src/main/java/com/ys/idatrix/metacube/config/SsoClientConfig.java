package com.ys.idatrix.metacube.config;

import com.idatrix.unisecurity.sso.client.SSOFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2019/1/15.
 */
@Slf4j
@Configuration
public class SsoClientConfig {

    @Value("${sso.serverBaseUrl}")
    private String serverBaseUrl;

    @Value("${sso.serverInnerAddress}")
    private String serverInnerAddress;

    @Value("${sso.projectName:/security}")
    private String projectName;

    @ConditionalOnExpression(value = "${sso.enabled:true}")
    @Bean
    public FilterRegistrationBean ssoFilterRegistration() {
        log.info("注册SSO过滤器...");
        FilterRegistrationBean registration = new FilterRegistrationBean(new SSOFilter());
        registration.addUrlPatterns("/*");
        registration.addInitParameter("serverBaseUrl", serverBaseUrl);
        registration.addInitParameter("serverInnerAddress", serverInnerAddress);
        registration.addInitParameter("projectName", projectName);
        registration.addInitParameter("excludes",
                "(.*)swagger-ui(.*)|(.*)v2(.*)|(.*)swagger-resources(.*)|(.*)webjars(.*)|(.*)data/share(.*)");
        registration.setName("ssoFilter");
        registration.setOrder(1);
        return registration;
    }

}

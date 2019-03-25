package com.idatrix.resource.portal.service.Impl;

import com.idatrix.resource.portal.service.IPortalSystemConfigService;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * 系统配置相关：如默认登陆用户，根据配置用户或部门，获取租户ID等功能
 */
@PropertySource("classpath:init.properties")
@Service("portalSystemConfigService")
public class PortalSystemConfigServiceImpl implements IPortalSystemConfigService {

    /*资源门户系统默认浏览用户名：生产默认情况是租户建立一个default用户*/
    @Value("${portal.visit.user}")
    private String visitUser = "default";

    @Autowired
    private UserService userService;

    /**
     * 获取资源门户用户名称
     *
     * @return
     */
    @Override
    public String getPortalUserName() {
        User userInfo =userService.findByUserName(visitUser);
        if(userInfo!=null){
            return userInfo.getUsername();
        }
        return "default";
    }

    /**
     * 获取资源门户默认租户ID
     *
     * @return
     */
    @Override
    public Long getPortalRentId() {

        User userInfo =userService.findByUserName(visitUser);
        if(userInfo!=null){
            return userInfo.getRenterId();
        }
        return 0L;
    }



}

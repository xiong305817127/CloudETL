package com.idatrix.resource.portal.service;

/**
 * 系统配置相关：如默认登陆用户，根据配置用户或部门，获取租户ID等功能
 */
public interface IPortalSystemConfigService {

    /**
     * 获取资源门户用户名称
     * @return
     */
    String getPortalUserName();

    /**
     * 获取资源门户默认租户ID
     * @return
     */
    Long getPortalRentId();
}

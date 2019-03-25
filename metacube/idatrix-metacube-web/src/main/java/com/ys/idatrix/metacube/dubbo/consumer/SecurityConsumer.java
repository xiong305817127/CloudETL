package com.ys.idatrix.metacube.dubbo.consumer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.idatrix.unisecurity.api.domain.Organization;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.domain.Role;
import com.idatrix.unisecurity.api.service.OrganizationService;
import com.idatrix.unisecurity.api.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.ys.idatrix.metacube.common.exception.MetaDataException;

import java.util.List;

/**
 * 安全服务消费
 *
 * @author wzl
 */
@Component
public class SecurityConsumer {

    @Reference
    private OrganizationService organizationService;

    @Reference
    private UserService userService;

    @Bean
    public OrganizationService getOrganizationService() {
        return organizationService;
    }

    @Bean
    public UserService getUserService() {
        return userService;
    }

    /**
     * 根据租户id获取租户信息
     *
     * @param renterId 租户id
     */
    public User findRenterInfoByRenterId(Long renterId) {
        return userService.findRenterByRenterId(renterId);
    }


    /**
     * 根据用户ID得到用户组织
     * @param userId
     * @return
     */
    public List<Organization> findOrganizationsByUserId(Long userId){
        return userService.findOrganizationsByUserId(userId);
    }

    /**
     * 查询跨租户的组织机构
     * @return
     */
    public List<Organization> findOrganizations(){
        return userService.findOrganizations();
    }

    /**
     * 根据租户id查询顶层组织
     *
     * @param renterId 租户id
     */
    public Organization findTopOrgByRenterId(Long renterId) {
        return organizationService.findByRenterId(renterId);
    }

    /**
     * 根据租户id查询所有的所属组织
     *
     * @param renterId 租户id
     */
    public List<Organization> listAscriptionDept(Long renterId) {
        return organizationService.findAllAscriptionDept(renterId);
    }

    /**
     * 查询用户的所属组织
     *
     * @param username 用户名
     */
    public Organization getAscriptionDeptByUserName(String username) {
        Organization ascriptionDept = organizationService.findAscriptionDeptByUserName(username);
        if (ascriptionDept == null) {
            // 当前用户没有设置部门
            throw new MetaDataException("当前用户还没有设置部门，请设置后再操作");
        }
        return ascriptionDept;
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     */
    public User findByUserName(String username) {
        return userService.findByUserName(username);
    }

    /**
     * 获取租户下的角色列表
     *
     * @param renterId
     * @return
     */
    public List<Role> findRoleListByRenterId(Long renterId) {
        return userService.findRoleListByRenterId(renterId);
    }

}

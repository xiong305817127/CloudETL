package com.idatrix.unisecurity.api.service;

import com.idatrix.unisecurity.api.domain.*;

import java.util.Date;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    Organization getUserOrganizationByUserId(Long userId);

    User findPermitByUserId(Long userId);

    List<User> findUsersByDeptId(Long deptId);

    List<Organization> findOrganizationsByUserId(Long userId);

    List<User> findSFTPUser();

    User getAuthorUser(String username, String password);

    List<User> findUsersByRenterId(Long i);

    /**
     * 获取同一租户下所有用户名
     * @param userName
     * @return
     */
    List<String> findRenterUsersByUsername(String userName);

    /**
     * 获取租户对应的用户信息
     * @param renterId
     * @return
     */
    User findRenterByRenterId(Long renterId);

    List<Organization> findOrganizations();

    List<Role> findRoles();

    /**
     * 根据租户ID获取租户下的所有角色
     * @param renterId
     * @return
     */
    List<Role> findRoleListByRenterId(Long renterId);

    List<User> findUsersByDeptAndRole(int deptId, int roleId);

    /**
     * 根据角色id获取用户
     * @param roleId
     * @param renterId
     * @return
     */
    List<User> findUserByRoleAndRenter(int roleId, Long renterId);

    List<Integer> findParentIdsByDeptCode(String deptCode, Long renterId);

    /**
     * 根据社会信用代码和租户id获取所有父部门id及本部门id
     *
     * @param unifiedCreditCode
     * @param renterId
     * @return
     */
    List<Integer> findParentIdsByUnifiedCreditCode(String unifiedCreditCode, Long renterId);

    /**
     * 根据用户名获取用户信息
     *
     * @param [username]
     * @return com.idatrix.unisecurity.api.domain.User
     * @author oyr
     * @date 2018/8/15 9:13
     */
    User findByUserName(String username);

    /**
     * 根据用户名获取用户对应所有角色的编码
     * @param username
     * @return
     */
    List<String> findRoleCodesByUserName(String username);

    /**
     * 根据租户ID获取用户今天的登录情况
     *
     * @return
     */
    NowLoginResult findNowLoginInfoByRenterId(Long renterId);

    /**
     * 根据租户ID获取所属部门下的登录情况
     *
     * @return
     */
    List<OrganizationUserLoginInfo> findDeptUserLoginInfoByRentId(Long renterId);

    /**
     * 根据租户ID和一个确定的时间段获取时间段中每一天的登录次数登录单位
     *
     * @return
     */
    List<LoginDateInfo> findUserLoginInfoByRenterIdAndTimeSlot(Long renterId, Date startLoginDate, Date lastLoginDate);

}

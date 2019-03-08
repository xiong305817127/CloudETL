package com.idatrix.unisecurity.provider.serivce;

import com.alibaba.fastjson.JSON;
import com.idatrix.unisecurity.api.domain.*;
import com.idatrix.unisecurity.api.service.UserService;
import com.idatrix.unisecurity.common.dao.LoginCountProviderMapper;
import com.idatrix.unisecurity.common.dao.OrganizationProviderMapper;
import com.idatrix.unisecurity.common.dao.RoleProviderMapper;
import com.idatrix.unisecurity.common.dao.UUserProviderMapper;
import com.idatrix.unisecurity.provider.utils.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrganizationProviderMapper organizationMapper;

    @Autowired
    private RoleProviderMapper roleMapper;

    @Autowired
    private UUserProviderMapper userMapper;

    @Override
    public Organization getUserOrganizationByUserId(Long userId) {
        Organization organization = null;
        logger.debug("getUserOrganizationByUserId start params:{}", userId);
        try {
            organization = organizationMapper.getUserOrganizationByUserId(userId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("getUserOrganizationByUserId return :{}", JSON.toJSON(organization));
        return organization;
    }

    @Override
    public User findPermitByUserId(Long userId) {
        User user = null;
        logger.debug("findPermitByUserId start params:{}", userId);
        try {
            user = userMapper.getUserInfo(userId);
            List<UPermission> permissions = userMapper.findPermitByUserId(userId);
            if (user != null) {
                user.setPermissionList(permissions);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return user;
    }

    @Override
    public List<User> findUsersByDeptId(Long deptId) {
        logger.debug("findUsersByDeptId start params:{}", deptId);
        try {
            return organizationMapper.findUsersByDeptId(deptId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 获取用户所在租户下所有组织查询接口
     */
    @Override
    public List<Organization> findOrganizationsByUserId(Long userId) {
        logger.debug("findOrganizationsByUserId start params:{}", userId);
        List<Organization> list = null;
        try {
            list = organizationMapper.findOrganizationsByUserId(userId);
            logger.debug("findOrganizationsByUserId return :{}", JSON.toJSON(list));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<User> findSFTPUser() {
        logger.debug("findSFTPUser start");
        try {
            return userMapper.findSFTPUser();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public User getAuthorUser(String username, String password) {
        logger.info("getAuthorUser start {},{}", username, password);
        return userMapper.getAuthorUser(username, UserManager.md5Pswd(password));
    }


    /**
     * 根据租户Id查询用户
     */
    @Override
    public List<User> findUsersByRenterId(Long renterId) {
        logger.debug("findUsersByRenterId start params:{}", renterId);
        try {
            return userMapper.findUsersByRenterId(renterId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    /**
     * 根据租户Id查询用户
     */
    @Override
    public User findRenterByRenterId(Long renterId) {
        logger.debug("findUsersByRenterId start params:{}", renterId);
        try {
            return userMapper.findRenterByRenterId(renterId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<String> findRenterUsersByUsername(String username) {
        logger.debug("findRenterUsersByUsername start params:{}", username);
        List<String> list = userMapper.findRenterUsersByUsername(username);
        logger.debug("findRenterUsersByUsername result:{}", JSON.toJSON(list));
        return list;
    }

    /**
     * 查找租户下面的所有部门
     */
    @Override
    public List<Organization> findOrganizations() {
        logger.debug("findOrganizations start.....");
        List<Organization> list = organizationMapper.findOrganizations();
        logger.debug("findOrganizations result:{}", JSON.toJSON(list));
        return list;
    }

    /**
     * 查找租户下面的所有角色
     */
    @Override
    public List<Role> findRoles() {
        logger.debug("find all Roles start.....");
        List<Role> list = roleMapper.findRoles();
        logger.debug("find all roles result:{}", JSON.toJSON(list));
        return list;
    }

    @Override
    public List<Role> findRoleListByRenterId(Long renterId) {
        List<Role> roles = roleMapper.findRoleListByRenterId(renterId);
        return roles;
    }

    @Override
    public List<User> findUsersByDeptAndRole(int deptId, int roleId) {
        logger.debug("find all users ByDeptAndRole start，params deptId:" + deptId + "======== roleId:" + roleId);
        List<User> list = userMapper.findUsersByDeptAndRole(deptId, roleId);
        logger.debug("find all roles ByDeptAndRole result:{}", JSON.toJSON(list));
        return list;
    }

    @Override
    public List<User> findUserByRoleAndRenter(int roleId, Long renterId) {
        logger.debug("find user by role");
        List<User> list = userMapper.findUserByRoleAndRenter(roleId, renterId);
        logger.debug("find user by role result:{}", JSON.toJSON(list));
        return list;
    }

    /**
     * 根据部门编号获取所有父部门id及本部门id
     */
    @Override
    public List<Integer> findParentIdsByDeptCode(String deptCode, Long rentId) {
        logger.debug("find all Parent id by Dept code start，params deptCode:" + deptCode);
        if (StringUtils.isEmpty(deptCode)
                || rentId == null) {
            return null;
        }
        List<Integer> list = organizationMapper.findParentIdsByDeptCode(deptCode, rentId);
        logger.debug("find all Parent id by Dept code result:{}", JSON.toJSON(list));
        return list;
    }

    @Override
    public List<Integer> findParentIdsByUnifiedCreditCode(String unifiedCreditCode, Long rentId) {
        logger.info("find all Parent id by unified credit code start params unifiedCreditCode:" + unifiedCreditCode);
        if (StringUtils.isEmpty(unifiedCreditCode) || rentId == null) {
            return null;
        }
        List<Integer> list = organizationMapper.findParentIdsByUnifiedCreditCode(unifiedCreditCode, rentId);
        logger.info("find all Parent id by unified credit code result:{}", JSON.toJSON(list));
        return list;
    }

    @Override
    public User findByUserName(String username) {
        User user = userMapper.findByUserName(username);
        return user;
    }

    @Override
    public List<String> findRoleCodesByUserName(String username) {
        List<String> list = userMapper.findRoleCodesByUserName(username);
        return list;
    }

    @Autowired
    private LoginCountProviderMapper loginCountMapper;

    @Override
    public NowLoginResult findNowLoginInfoByRenterId(Long renterId) {
        logger.info("=====根据租户ID获取用户今天的登录情况=====");
        try {
            // 今日一共登录次数
            int nowLoginCount = loginCountMapper.findNowLoginCountByRenterId(renterId);

            // 今日登录的用户数
            int nowLoginUserCount = loginCountMapper.findNowLoginUserCountByRenterId(renterId);

            // 今日登录的组织数
            int nowLoginDeptCount = loginCountMapper.findNowLoginDeptCountByRenterId(renterId);

            // 所有的登录次数
            int allLoginCount = loginCountMapper.findAllLoginCountByRenterId(renterId);

            return new NowLoginResult(nowLoginCount, nowLoginUserCount, nowLoginDeptCount, allLoginCount);
        } catch (Exception e) {
            logger.error("根据租户ID获取用户今天的登录情况 error：{}", e.getMessage());
        }
        return null;
    }

    @Override
    public List<OrganizationUserLoginInfo> findDeptUserLoginInfoByRentId(Long renterId) {
        logger.info("=====根据租户ID获取所属部门下的登录情况=====");
        try {
            List<OrganizationUserLoginInfo> list = loginCountMapper.findDeptUserLoginInfoByRentId(renterId);
            return list;
        } catch (Exception e) {
            logger.error("根据租户ID获取所属部门下的登录情况 error：{}", e.getMessage());
        }
        return null;
    }

    @Override
    public List<LoginDateInfo> findUserLoginInfoByRenterIdAndTimeSlot(Long renterId, Date startLoginDate, Date lastLoginDate) {
        logger.info("=====根据租户ID和一个确定的时间段获取时间段中每一天的登录次数登录单位=====");
        try {
            List<LoginDateInfo> result = new ArrayList<>();
            List<LoginDateInfo> list = loginCountMapper.findUserLoginInfoByRenterIdAndTimeSlot(renterId, startLoginDate, lastLoginDate);
            Map<String, LoginDateInfo> loginDateMap =
                    list.stream().collect(Collectors.toMap((key -> key.getLoginDate()), (value -> value)));
            List<String> dayList = getRecentDayList(startLoginDate, lastLoginDate);
            for (String dayStr : dayList) {
                LoginDateInfo loginDateInfo = loginDateMap.get(dayStr);
                if (loginDateInfo == null) {
                    loginDateInfo = new LoginDateInfo(dayStr, 0, 0, 0);
                    result.add(loginDateInfo);
                    continue;
                }
                result.add(loginDateInfo);
            }
            return result;
        } catch (Exception e) {
            logger.error("根据租户ID和一个确定的时间段获取时间段中每一天的登录次数登录单位 error：{}", e.getMessage());
        }
        return null;
    }

    public static List<String> getRecentDayList(Date startTime, Date endTime) {
        List<String> dayList = new ArrayList<String>();
        if (startTime.after(endTime)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = startTime;
        cal.setTime(date);
        String dateString = sdf.format(cal.getTime());
        while (cal.getTime().before(endTime)) {
            dateString = sdf.format(cal.getTime());
            dayList.add(dateString);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dayList;
    }

}

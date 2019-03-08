package com.idatrix.unisecurity.renter.service.impl;

import com.idatrix.unisecurity.common.dao.*;
import com.idatrix.unisecurity.common.domain.*;
import com.idatrix.unisecurity.common.exception.SecurityException;
import com.idatrix.unisecurity.common.utils.Constants;
import com.idatrix.unisecurity.common.utils.EmailUtil;
import com.idatrix.unisecurity.common.utils.MathUtil;
import com.idatrix.unisecurity.common.utils.SecurityStringUtils;
import com.idatrix.unisecurity.core.mybatis.BaseMybatisDao;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.freeipa.model.FreeIPATemplate;
import com.idatrix.unisecurity.freeipa.proxy.factory.LdapHttpDataBuilder;
import com.idatrix.unisecurity.freeipa.proxy.impl.FreeIPAProxyImpl;
import com.idatrix.unisecurity.organization.service.OrganizationService;
import com.idatrix.unisecurity.permission.bo.URoleBo;
import com.idatrix.unisecurity.permission.service.RoleService;
import com.idatrix.unisecurity.properties.EmailProperties;
import com.idatrix.unisecurity.ranger.usersync.process.LdapMgrUserGroupBuilder;
import com.idatrix.unisecurity.renter.service.RenterService;
import com.idatrix.unisecurity.user.Config;
import com.idatrix.unisecurity.user.manager.UserManager;
import com.idatrix.unisecurity.user.service.SynchUserToBbs;
import com.idatrix.unisecurity.user.service.UUserService;
import com.ys.idatrix.metacube.api.service.MetadataDatabaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by james on 2017/5/26.
 */
@Service
public class RenterServiceImpl extends BaseMybatisDao<URenterMapper> implements RenterService {


    @Autowired(required = false)
    private MetadataDatabaseService metadataDatabaseService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UUserMapper userMapper;

    @Autowired
    private URenterMapper renterMapper;

    @Autowired
    URoleMapper roleMapper;

    @Autowired
    UUserRoleMapper userRoleMapper;

    @Autowired
    OrganizationMapper organizationMapper;

    @Autowired
    URolePermissionMapper rolePermissionMapper;

    @Autowired
    UPermissionMapper permissionMapper;

    @Autowired
    RoleService roleService;

    @Autowired(required = false)
    private Config config;

    @Autowired(required = false)
    MailLogMapper mailLogMapper;

    @Autowired(required = false)
    FreeIPATemplate freeIPATemplate;

    @Autowired(required = false)
    LdapHttpDataBuilder ldapHttpDataBuilder;

    @Autowired(required = false)
    LdapMgrUserGroupBuilder ldapMgrUserGroupBuilder;

    @Autowired
    private UUserService userService;

    @Autowired
    private SynchUserToBbs synchUserToBbs;

    @Autowired
    private EmailProperties emailProperties;

    @Override
    public Pagination<URenter> findPage(Map<String, Object> resultMap, Integer pageNo, Integer pageSize) {
        return super.findPage(resultMap, pageNo, pageSize);
    }

    /**
     * 创建租户，创建用户，创建角色，关联用户和角色
     *
     * @param renter
     * @return
     * @throws Exception
     */
    @Override
    public Integer addRenter(URenter renter) throws Exception {
        int count;
        Date date = new Date();
        // 创建租户
        renter.setCreateTime(date);
        renter.setLastUpdatedBy(date);
        renter.setRenterStatus(1l);// 设置状态
        count = renterMapper.insertRenter(renter);

        // 创建部门
        Organization org = new Organization();
        org.setRenterId(renter.getId());// 租户ID
        org.setDeptCode(MathUtil.getRandom620(6));// 部门编号
        // 部门编号不能重复

        while (organizationService.findDeptCountByCode(org) > 0) {
            org.setDeptCode(MathUtil.getRandom620(6));
        }
        org.setCreateTime(date);
        org.setLastUpdatedBy(date);
        org.setIsActive(true);
        org.setDeptName(renter.getRenterName());
        organizationMapper.insertOrganization(org);

        // 创建用户
        UUser uUser = new UUser();
        uUser.setDeptId(org.getId());
        uUser.setRenterId(renter.getId());// 租户id
        uUser.setUsername(renter.getAdminAccount());// 用户账号
        // 用户密码
        String password = "123456"; // 默认密码 123456
        if (Constants.SWITCH.equals(emailProperties.getMailSwitch())) { // 如果邮箱开关开启则使用随机密码并且发送邮件
            password = MathUtil.getRandom620(6);
        }
        uUser.setPswd(password);
        uUser.setRealName(renter.getAdminName());// 用户名
        uUser.setEmail(renter.getAdminEmail());// 邮箱
        uUser.setPhone(renter.getAdminPhone());// 手机
        uUser.setStatus(1L);// 设置状态
        // user insert
        userService.insertSelective(uUser);

        // 创建角色
        URole role = new URole();
        role.setRenterId(renter.getId());
        role.setName(renter.getRenterName() + "租户管理员");
        role.setCode(MathUtil.getRandom620(6));// set 唯一code
        while (roleService.findRoleByCode(role.getCode()) > 0) {
            role.setCode(MathUtil.getRandom620(6));
        }
        role.setType(MathUtil.getRandom620(6));
        while (roleService.findRoleByType(role.getType()) > 0) {
            role.setType(MathUtil.getRandom620(6));
        }
        role.setIsActive(true); // 设置标识
        role.setCreateTime(date);
        role.setLastUpdateTime(date);
        roleMapper.insertSelective(role);

        // 创建用户角色关系
        UUserRole uUserRole = new UUserRole();
        uUserRole.setUid(uUser.getId());
        Long roleId = role.getId();
        uUserRole.setRid(roleId);
        userRoleMapper.insertSelective(uUserRole);

        // 角色关联权限（拥有租户开通系统时所有的权限）
        List<Long> permissionIds = permissionMapper.findPermissionIdsBySystemIds(renter.getOpenedResource().split(","));
        rolePermissionMapper.permissionToRenterAdminRole(roleId, permissionIds, date);

        List<String> groupList = new ArrayList<String>();
        groupList.add("d_" + org.getId());
        groupList.add("r_" + roleId);

        // 新增租户，同步到freeipa
        if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
            FreeIPAProxyImpl proxy = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
            proxy.addUser("u_" + uUser.getId(), password);
            proxy.addGroupList(groupList);
            proxy.addUser2Groups("u_" + uUser.getId(), groupList);
        }

        // 如果邮箱开关开着，需要发送邮件
        if (Constants.SWITCH.equals(emailProperties.getMailSwitch())) {
            // post email
            String subject = "新建租户密码通知";
            String emailContent = "尊敬的 " + renter.getAdminName() + "(先生/女士)：感谢您使用粤数大数据融合共享平台，您的租户账号的初始密码为： " + password;
            MailLog mailLog = new MailLog();
            mailLog.setContent(emailContent);
            mailLog.setSendServer(emailProperties.getUser());
            mailLog.setRecipient(renter.getAdminEmail());
            mailLog.setStatus("S"); // 发送中
            mailLog.setSubject(subject);
            int id = generateId();
            mailLog.setId(id);
            mailLogMapper.insert(mailLog);
            try {
                EmailUtil.getInstance().postEmail(renter.getAdminEmail(), subject, emailContent, mailLogMapper, mailLog);
            } catch (Exception e) {
                logger.info("post email error" + e.getMessage());
                mailLog.setStatus("F");
                mailLog.setMsg("发送失败");
                mailLogMapper.update(mailLog);
            }
        }

        // 新增租户后，通知元数据注册或修改平台数据库信息
        metadataDatabaseService.registerOrUpdatePlatformDatabaseInfo(renter.getId());
        return count;
    }

    private int generateId() {
        int maxId = mailLogMapper.getMaxId();
        if (maxId == 0) {
            return 1;
        } else {
            maxId += 1;
            return maxId;
        }
    }

    @Override
    public int updateByPrimaryKeySelective(URenter uRenter) throws Exception {
        URenter renter = renterMapper.findRenterById(uRenter.getId());
        String renterName = renter.getRenterName();

        Date date = new Date();
        uRenter.setLastUpdatedBy(date);
        // 修改租户信息
        int count = renterMapper.updateByPrimaryKeySelective(uRenter);

        // 获取租户管理员角色
        URole renterRole = roleMapper.getRoleByName(renter.getId(), renterName + "租户管理员");
        if (!renter.getRenterName().equals(uRenter.getRenterName())) {
            // 租户name有所改变，那么租户管理员角色名字随之改变
            renterRole.setName(uRenter.getRenterName() + "租户管理员");
            renterRole.setLastUpdateTime(date);
            roleMapper.updateByPrimaryKeySelective(renterRole);
        }

        String[] resources = uRenter.getOpenedResource().split(","); // 租户开通的系统权限

        // 同步租户管理员角色的系统权限
        rolePermissionMapper.deleteByRid(renterRole.getId());// 先删除，重新修改
        List<Long> permissionIds = permissionMapper.findPermissionIdsBySystemIds(resources);// 租户开通系统资源下对应的期限
        rolePermissionMapper.permissionToRenterAdminRole(renterRole.getId(), permissionIds, date);

        // 同步租户下所有（不包括租户管理员角色）角色的权限，这里只需要将租户没有开通的权限删除即可。
        rolePermissionMapper.roleSynchPermissionByRenter(uRenter.getId(), resources);

        // 同步的去修改管理员信息
        UUser uUser = userService.getUserByUsername(uRenter.getAdminAccount());
        uUser.setRealName(uRenter.getAdminName());
        uUser.setEmail(uRenter.getAdminEmail());
        uUser.setPhone(uRenter.getAdminPhone());
        uUser.setLastUpdatedDate(date);
        userMapper.updateByPrimaryKeySelective(uUser);
        // 用户修改同步到bbs中
        synchUserToBbs.updateUser(uUser.getUsername(), uUser.getPswd(), uUser.getPhone(), uUser.getEmail(), true);

        // 修改租户后，通知元数据注册或修改平台数据库信息
        metadataDatabaseService.registerOrUpdatePlatformDatabaseInfo(renter.getId());

        return count;
    }

    @Override
    public Integer deleteRenterById(String ids) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int successCount = 0;
        String[] idArray = null;
        if (SecurityStringUtils.contains(ids, ",")) {
            idArray = ids.split(",");
        } else {
            idArray = new String[]{ids};
        }
        for (String idx : idArray) {
            List<String> groupNames = new ArrayList<String>();
            Long id = Long.valueOf(idx);
            String account = renterMapper.getAccountById(id);
            userMapper.updateStatusByUsername(account);
            UUser user = userMapper.getUserByUsername(account);
            successCount += renterMapper.deleteByPrimaryKey(id);
            groupNames.add("d_" + user.getDeptId());

            try {
                List<URoleBo> roleList = userMapper.selectRoleByUserId(user.getId());
                for (URoleBo role : roleList) {
                    groupNames.add("r_" + role.getId());
                }
                if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
                    // delete ladp user
                    FreeIPAProxyImpl proxy = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
                    proxy.deleteUser("u_" + user.getId());
                    proxy.deleteUserFromGroups("u_" + user.getId(), groupNames);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return successCount;
    }

    @Override
    public void updateRenterInfo(URenter uRenter) {
        renterMapper.updateRenterInfo(uRenter);
    }

    @Override
    public int isExist(URenter uRenter) {
        return renterMapper.isExist(uRenter);
    }

    @Override
    public URenter findRenterById(Long id) {
        return renterMapper.findRenterById(id);
    }

    @Override
    public boolean isRentByUserName(String username) {
        return renterMapper.isRentByUserName(username) > 0 ? true : false;
    }

    @Override
    public URenter findByAdminAccount(String username) {
        return renterMapper.findByAdminAccount(username);
    }

    @Transactional
    @Override
    public int restRenterPassword(Long renterId) {
        URenter renter = renterMapper.findRenterById(renterId);
        if (renter == null) {
            throw new SecurityException(500, "租户不存在，重置密码失败");
        }
        // 管理员账号
        String adminAccount = renter.getAdminAccount();
        UUser user = userService.getUserByUsername(adminAccount);
        if (user == null) {
            throw new SecurityException(500, "租户缺少对应的管理员用户，重置密码失败");
        }
        // 重置密码为123456
        user.setPswd("123456");
        UserManager.md5Pswd(user);
        // 并且将用户操作次数清空
        user.setVisitTimes(0l);
        int count = userService.updateByPrimaryKeySelective(user);
        return count;
    }

    @Override
    public void updateStatus(String renterIds, Long status) {
        String[] idArray;
        if (StringUtils.contains(renterIds, ",")) {
            idArray = renterIds.split(",");
        } else {
            idArray = new String[]{renterIds};
        }
        // 修改租户状态
        renterMapper.updateStatus(idArray, status);

        // 禁用租户下的所有的用户，包括租户管理员
        userService.updateUserStatusByRenterIds(idArray, status);
    }

}

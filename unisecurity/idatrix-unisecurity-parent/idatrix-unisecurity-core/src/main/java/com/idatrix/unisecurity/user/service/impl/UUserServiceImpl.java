package com.idatrix.unisecurity.user.service.impl;

import com.idatrix.unisecurity.common.dao.ClientSystemMapper;
import com.idatrix.unisecurity.common.dao.ImportMsgMapper;
import com.idatrix.unisecurity.common.dao.UUserMapper;
import com.idatrix.unisecurity.common.dao.UUserRoleMapper;
import com.idatrix.unisecurity.common.domain.*;
import com.idatrix.unisecurity.common.exception.SecurityException;
import com.idatrix.unisecurity.common.utils.Constants;
import com.idatrix.unisecurity.common.utils.HttpCodeUtils;
import com.idatrix.unisecurity.common.utils.LoggerUtils;
import com.idatrix.unisecurity.core.jedis.JedisClient;
import com.idatrix.unisecurity.core.mybatis.BaseMybatisDao;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.core.shiro.session.CustomSessionManager;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.freeipa.model.FreeIPATemplate;
import com.idatrix.unisecurity.freeipa.proxy.IFreeIPAProxy;
import com.idatrix.unisecurity.freeipa.proxy.factory.LdapHttpDataBuilder;
import com.idatrix.unisecurity.freeipa.proxy.impl.FreeIPAProxyImpl;
import com.idatrix.unisecurity.permission.bo.URoleBo;
import com.idatrix.unisecurity.permission.bo.UserRoleAllocationBo;
import com.idatrix.unisecurity.properties.LoginProperties;
import com.idatrix.unisecurity.ranger.usersync.process.LdapMgrUserGroupBuilder;
import com.idatrix.unisecurity.renter.service.RenterService;
import com.idatrix.unisecurity.user.Config;
import com.idatrix.unisecurity.user.manager.UserManager;
import com.idatrix.unisecurity.user.service.SynchUserToBbs;
import com.idatrix.unisecurity.user.service.SynchUserToSsz;
import com.idatrix.unisecurity.user.service.UUserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class UUserServiceImpl extends BaseMybatisDao<UUserMapper> implements UUserService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CustomSessionManager customSessionManager;

    @Autowired(required = false)
    private UUserMapper userMapper;

    @Autowired(required = false)
    private UUserRoleMapper userRoleMapper;

    @Autowired(required = false)
    private ClientSystemMapper clientSystemMapper;

    @Autowired(required = false)
    private FreeIPATemplate freeIPATemplate;

    @Autowired(required = false)
    private LdapHttpDataBuilder ldapHttpDataBuilder;

    @Autowired(required = false)
    private LdapMgrUserGroupBuilder ldapMgrUserGroupBuilder;

    @Autowired
    private RenterService renterService;
/*
    @Autowired(required = false)
    private UserInfoSyncService userInfoSyncService;*/

    @Autowired(required = false)
    ImportMsgMapper importMsgMapper;

    @Autowired(required = false)
    Config config;

    @Autowired
    private JedisClient jedisClient;

    @Autowired
    private LoginProperties loginProperties;

    @Autowired
    private SynchUserToBbs synchUserToBbs;

    @Autowired
    private SynchUserToSsz synchUserToSsz;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<UUser> findAll() {
        return userMapper.selectAll();
    }

    @Override
    public UUser insert(UUser entity) {
        userMapper.insert(entity);
        return entity;
    }

    @Override
    public UUser insertSelective(UUser user) {
        // 参数补齐
        Date date = new Date();
        user.setCreateTime(date);
        user.setLastUpdatedDate(date);
        UserManager.md5Pswd(user);
        // insert
        userMapper.insertSelective(user);
        // 同步到 bbs 中
        synchUserToBbs.addUser(user.getUsername(), user.getPswd(), user.getEmail(), user.getPhone());
        // 同步到神算子中
        synchUserToSsz.addUser(user.getUsername());
        return user;
    }

    @Override
    public UUser selectByPrimaryKey(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKey(UUser entity) {
        return userMapper.updateByPrimaryKey(entity);
    }

    @Override
    public int updateByPrimaryKeySelective(UUser entity) {
        // 修改用户信息
        entity.setLastUpdatedDate(new Date());
        userMapper.updateByPrimaryKeySelective(entity);
        UUser user = selectByPrimaryKey(entity.getId());
        // 如果用户是租户，还需更新租户信息
        if (renterService.isRentByUserName(user.getUsername())) {
            URenter uRenter = new URenter();
            uRenter.setAdminAccount(user.getUsername());
            uRenter.setAdminEmail(user.getEmail());
            uRenter.setAdminPhone(user.getPhone());
            uRenter.setLastUpdatedBy(new Date());
            renterService.updateRenterInfo(uRenter);
        }
        // 修改用户同步到bbs中
        synchUserToBbs.updateUser(user.getUsername(), user.getPswd(), user.getPhone(), user.getEmail(), true);
        return 0;
    }

    @Override
    public UUser login(String username, String pswd) {
        UUser uUser = userMapper.getUserByUsername(username);
        if (uUser.getPswd().equals(pswd)) {
            userMapper.updateVisitTimesByUsername(username);
            return uUser;
        }
        return null;
    }

    public void updateVisitTimesByUsername(String username) {
        userMapper.updateVisitTimesByUsername(username);
    }

    @Override
    public void updateLoginUserInfo(UUser user) {
        // 修改用户信息
        userMapper.updateByPrimaryKeySelective(user);
        // 登录成功后，记录登录次数
        userMapper.updateVisitTimesByUsername(user.getUsername());
    }

    @Override
    public int updateUserStatusByRenterIds(String[] renterIds, Long status) {
        return userMapper.updateUserStatusByRenterIds(renterIds, status);
    }

    @Override
    public UUser getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pagination<UUser> findByPage(Map<String, Object> resultMap,
                                        Integer pageNo, Integer pageSize) {
        return super.findPage(resultMap, pageNo, pageSize);
    }

    @Override
    public Integer deleteUserById(String ids) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        String[] idArray = null;
        if (StringUtils.contains(ids, ",")) {
            idArray = ids.split(",");
        } else {
            idArray = new String[]{ids};
        }

        Set<String> userNames = new HashSet<>();

        // 删除用户
        List<String> groupList = new ArrayList<String>();
        int count = 0;
        for (String id : idArray) {
            UUser user = selectByPrimaryKey(Long.valueOf(id));

            int number = deleteByPrimaryKey(Long.valueOf(id));
            if (number > 0) {
                // 大于0，那么代表当前的删除成功
                userNames.add(user.getUsername());

                // 删除user 与 role的关联记录
                userRoleMapper.deleteByUserId(Long.parseLong(id));

                /*// 查询group
                try {
                    if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
                        // 删除部门 group
                        List<URoleBo> roles = selectRoleByUserId(Long.valueOf(id));
                        groupList.add("D_" + user.getDeptId());
                        for (URoleBo role : roles) {
                            groupList.add("R_" + role.getId());
                        }
                        userInfoSyncService.userInfoSync(Integer.parseInt(id), user.getUsername(), 1, 3);
                        FreeIPAProxyImpl proxy = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
                        proxy.deleteUser("U_" + id);
                        proxy.removeUserFromGroups("U_" + id, groupList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LoggerUtils.fmtError(getClass(), e, "根据IDS删除用户出现错误，ids[%s]", ids);
                }*/

                // 同步user to 元数据

                // 计数，删除了几条数据
                count += number;
            }
        }

        String strUserNames = StringUtils.join(userNames.toArray(), ",");

        // delete同步到bbs中
        synchUserToBbs.deleteUser(strUserNames);

        // delete同步到神算子中
        synchUserToSsz.deleteUser(strUserNames);

        return count;
    }

    @Override
    public Map<String, Object> updateForbidUserById(Long id, Long status) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            UUser user = selectByPrimaryKey(id);
            user.setStatus(status);
            updateByPrimaryKeySelective(user);

            //如果当前用户在线，需要标记并且踢出
            customSessionManager.forbidUserById(id, status);

            resultMap.put("status", HttpCodeUtils.NORMAL_STATUS);
        } catch (Exception e) {
            resultMap.put("status", HttpCodeUtils.SERVER_INNER_ERROR_STATUS);
            resultMap.put("message", "操作失败，请刷新再试！");
            LoggerUtils.fmtError(getClass(), "禁止或者激活用户登录失败，id[%s],status[%s]", id, status);
        }
        return resultMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Pagination<UserRoleAllocationBo> findUserAndRole(ModelMap modelMap,
                                                            Integer pageNo, Integer pageSize) {
        return super.findPage("findUserAndRole", "findCount", modelMap, pageNo, pageSize);
    }

    @Override
    public List<URoleBo> selectRoleByUserId(Long id) {
        return userMapper.selectRoleByUserId(id);
    }

    @Override
    public int addRole2User(Long userId, String rids) {
        List<Long> oldRids = userRoleMapper.findRoleIdsByUserId(userId);

        // 删除当前用户角色的关联
        userRoleMapper.deleteByUserId(userId);

        int count = 0;
        try {
            if (StringUtils.isNotBlank(rids)) {
                String[] idArray = null;
                if (StringUtils.contains(rids, ",")) {
                    idArray = rids.split(",");
                } else {
                    idArray = new String[]{rids};
                }
                //添加新的。
                List<Long> roleList = new ArrayList<Long>();
                List<Long> containList = new ArrayList<Long>();
                for (String rid : idArray) {
                    if (StringUtils.isNotBlank(rid)) {
                        if (oldRids.contains(Long.parseLong(rid))) {
                            containList.add(Long.parseLong(rid));
                        }
                        roleList.add(Long.parseLong(rid));
                        UUserRole entity = new UUserRole(userId, Long.valueOf(rid));
                        if (CollectionUtils.isEmpty(userRoleMapper.find(entity)))
                            count += userRoleMapper.insertSelective(entity);
                    }
                }
                roleList.removeAll(containList); // 新增list
                oldRids.removeAll(containList); //删除list

                List<String> addRoleList = new ArrayList<String>();
                for (Long id : roleList) {
                    addRoleList.add("r_" + id);
                }

                List<String> remvoeRoleList = new ArrayList<String>();
                for (Long id : oldRids) {
                    remvoeRoleList.add("r_" + id);
                }

                try {
                    logger.info("addRole2User>>> addUser2Groups     userId =" + userId + "groups = " + addRoleList);
                    if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
                        IFreeIPAProxy proxy = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
                        proxy.addUser2Groups("u_" + String.valueOf(userId), addRoleList);

                        logger.info("addRole2User>>> remvoeUser2Groups userId =" + userId + "groups = " + remvoeRoleList);
                        proxy.removeUserFromGroups("u_" + String.valueOf(userId), remvoeRoleList);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    LoggerUtils.fmtError(getClass(), e, "addRole2User id[%s],user id[%s]", userId);
                }
            }
        } catch (Exception e) {
            throw new SecurityException(500, "用户关联角色失败！！！信息：" + e.getMessage());
        }
        //清空用户的权限，迫使再次获取权限的时候，得重新加载
        ShiroTokenManager shiroTokenManager = new ShiroTokenManager();
        shiroTokenManager.clearUserAuthByUserId(userId);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addUsersToRole(Long roleId, String uids) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int count = 0;
        try {
            // 删除旧的关联关系
            List<Long> oldUsers = userRoleMapper.findUserIdByRoleId(roleId);
            userRoleMapper.deleteByRoleId(roleId);

            if (StringUtils.isNotBlank(uids)) {
                String[] uArray = null;
                if (StringUtils.contains(uids, ",")) {
                    uArray = uids.split(",");
                } else {
                    uArray = new String[]{uids};
                }

                // 添加新的。
                List<Long> ids = new ArrayList<Long>();
                List<Long> containLst = new ArrayList<Long>();
                for (String uid : uArray) {
                    if (StringUtils.isNotBlank(uid)) {
                        if (oldUsers.contains(Long.parseLong(uid))) {
                            containLst.add(Long.parseLong(uid));
                        }
                        ids.add(Long.parseLong(uid));
                        UUserRole entity = new UUserRole(Long.valueOf(uid), roleId);
                        if (CollectionUtils.isEmpty(userRoleMapper.find(entity)))
                            count += userRoleMapper.insertSelective(entity);
                    }
                }
                List<Long> addLst = new ArrayList<Long>();
                ids.removeAll(containLst);
                oldUsers.removeAll(containLst);
                List<String> addUsrs = new ArrayList<String>();
                for (Long id : ids) {
                    addUsrs.add("u_" + id);
                }

                List<String> removeUsrs = new ArrayList<String>();
                for (Long id : oldUsers) {
                    removeUsrs.add("u_" + id);
                }

                try {
                    if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
                        IFreeIPAProxy proxy = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
                        //	                List ids = Arrays.asList(uids);
                        if (!CollectionUtils.isEmpty(addUsrs)) {
                            logger.info("add user to group freeipa ids= " + uids + ">>> group =" + roleId);
                            proxy.addUsers2Group(addUsrs, "r_" + roleId);
                        }
                        if (!CollectionUtils.isEmpty(removeUsrs)) {
                            logger.info("removeUsersFromGroup freeipa ids= " + removeUsrs + ">>> group =" + roleId);
                            proxy.removeUsersFromGroup(removeUsrs, "r_" + roleId);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LoggerUtils.fmtError(getClass(), e, "addUsersToRole>>>addUsers2Group 出现错误，uids[%s]", uids);
                }
            }
        } catch (Exception e) {
            throw new SecurityException(500, "用户关联角色失败！！！信息：" + e.getMessage());
        }
        return count;
    }

    @Override
    public int deleteRoleByUserIds(String userIds) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("userIds", userIds);
        int count = userRoleMapper.deleteRoleByUserIds(resultMap);
        return count;
    }

    @Override
    public int findUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }

    @Override
    public UUser getUser(String username) {
        return userMapper.getUser(username);
    }

    @Override
    public List<UserData> export(String ids) {
        List<UserData> ulist = new ArrayList<>();
        UUser user = null;
        UserData userData = null;
        if (StringUtils.isNotBlank(ids)) {
            String[] idArr = ids.split(",");
            for (int i = 0; i < idArr.length; i++) {
                user = userMapper.selectByPrimaryKey(Long.valueOf(idArr[i]));
                userData = new UserData();
                userData.setUsername(user.getUsername());
                userData.setRealName(user.getRealName());
                if (user.getSex() == null)
                    userData.setSex("未知");
                else if (user.getSex() == 1L)
                    userData.setSex("男");
                else if (user.getSex() == 2L)
                    userData.setSex("女");
                else
                    userData.setSex("未知");
                userData.setAge(user.getAge());
                userData.setCardId(user.getCardId());
                userData.setEmail(user.getEmail());
                userData.setPhone(user.getPhone());
                if (user != null)
                    ulist.add(userData);
            }
        }
        return ulist;
    }

    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addUserToOrg(Long orgId, String uIds) throws Exception {
        /**
         * 先记录下之前的当前组织关联的用户
         * 将当前组织下关联的用户清空
         * 当前组织再去关联新的用户
         * 计算出要被删除的和要新增的关联关系后发送给free api
         */
        // 先记录下之前的当前组织关联的用户
        List<Long> oldRelations = findUserIdsByOrganizationId(orgId);

        // 将当前组织下关联的用户清空
        userMapper.userClearOrganizationId(orgId);

        // 当前组织再去关联新的用户
        String[] idArray = null;
        if (StringUtils.contains(uIds, ",")) {
            idArray = uIds.split(",");
        } else {
            idArray = new String[]{uIds};
        }
        int count = userMapper.organizationToUser(orgId, idArray);

        // 计算出要被删除的和要新增的关联关系后发送给free api
        List<Long> addList = new ArrayList<Long>();
        List<Long> contianList = new ArrayList<Long>();

        for (String userId : idArray) {
            if (StringUtils.isNotBlank(userId)) {
                if (oldRelations.contains(Long.parseLong(userId))) {
                    // 当前关联关系之前是存在的。
                    contianList.add(Long.parseLong(userId));
                }
                addList.add(Long.parseLong(userId));
            }
        }

        addList.removeAll(contianList); // 新增list
        oldRelations.removeAll(contianList);// 删除list

        List ids = new ArrayList<String>();
        for (Long id : addList) {
            ids.add("u_" + id);
        }

        List<String> remvoeIds = new ArrayList<String>();
        for (Long id : oldRelations) {
            remvoeIds.add("u_" + id);
        }

        try {
            IFreeIPAProxy proxy = null;
            if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
                if (!CollectionUtils.isEmpty(ids)) {
                    logger.info("addUserToOrg addUsers2Group ids：{}, group：{}", uIds, orgId, orgId);
                    proxy = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
                    proxy.addUsers2Group(ids, "d_" + orgId);
                }

                if (!CollectionUtils.isEmpty(remvoeIds)) {
                    logger.info("addUserToOrg removeUsers2Group ids：{},group：{}", remvoeIds, orgId);
                    proxy.removeUsersFromGroup(remvoeIds, "d_" + orgId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtils.fmtError(getClass(), e, "addUserToOrg出现错误，ids[%s]", uIds);
        }
        return count;


/*

        if (!CollectionUtils.isEmpty(oldRelations)) {
            HashMap<String, List> params = new HashMap<String, List>();
            params.put("list", oldRelations);
            userMapper.clearOrganizationId(params);
        }

        int count = 0;
        if (StringUtils.isNotBlank(uIds)) {
            String[] idArray = null;
            if (StringUtils.contains(uIds, ",")) {
                idArray = uIds.split(",");
            } else {
                idArray = new String[]{uIds};
            }

            List<Long> addList = new ArrayList<Long>();
            List<Long> contianList = new ArrayList<Long>();
            for (String userId : idArray) {
                if (StringUtils.isNotBlank(userId)) {
                    if (oldRelations.contains(Long.parseLong(userId))) {
                        // 之前就已经是存在的。
                        contianList.add(Long.parseLong(userId));
                    }
                    addList.add(Long.parseLong(uid));
                    UUser entity = userMapper.selectByPrimaryKey(Long.parseLong(uid));
                    entity.setDeptId(orgId);
                    count += userMapper.updateByPrimaryKeySelective(entity);
                }
            }

            addList.removeAll(contianList); //新增list
            oldRelations.removeAll(contianList);// remove list

            List ids = new ArrayList<String>();
            for (Long id : addList) {
                ids.add("u_" + id);
            }

            List<String> remvoeIds = new ArrayList<String>();
            for (Long id : oldRelations) {
                remvoeIds.add("u_" + id);
            }

            try {
                IFreeIPAProxy proxy = null;
                if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
                    if (!CollectionUtils.isEmpty(ids)) {
                        logger.info("addUserToOrg addUsers2Group ids：{}, group：{}", uIds, orgId, orgId);
                        proxy = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
                        proxy.addUsers2Group(ids, "d_" + orgId);
                    }

                    if (!CollectionUtils.isEmpty(remvoeIds)) {
                        logger.info("addUserToOrg>>>removeUsers2Group ids= " + remvoeIds + ":::::: group =" + orgId);
                        proxy.removeUsersFromGroup(remvoeIds, "d_" + orgId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LoggerUtils.fmtError(getClass(), e, "addUserToOrg出现错误，ids[%s]", uIds);
            }
        }
        return count;
        */
    }

    private List<Long> findUserIdsByOrganizationId(Long orgId) {
        return userMapper.findUserIdsByOrganizationId(orgId);
    }

    @Override
    public int findUserByEmail(String email) {
        return userMapper.findUserByEmail(email);
    }

    @Override
    public List<UUser> findUsersByOrganizationId(Long deptId) {
        return userMapper.findUsersByOrganizationId(deptId);
    }

    @Override
    public List<UUser> findUsersByRoleId(Long roleId) {
        return userMapper.findUsersByRoleId(roleId);
    }

    @Override
    public int findUserByPhone(String phone) {
        return userMapper.findUserByPhone(phone);
    }

    @Override
    public List<UUser> getUsersByuserNames(List<String> userNames) {
        return null;
    }

    @Override
    public Integer addOrgToUser(Long orgId, String uId) {
        logger.debug("addOrgToUser params userId：{},orgId：{}", uId, orgId);
        UUser oldDept = userMapper.selectByPrimaryKey(Long.parseLong(uId));
        Long oldDeptId = oldDept.getDeptId();
        logger.debug("addOrgToUser params oldDeptId：{},orgId：{}", oldDeptId, orgId);

        oldDept.setDeptId(orgId);
        int count = userMapper.updateByPrimaryKeySelective(oldDept);
        try {
            List<String> userList = new ArrayList<String>();
            if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
                userList.add("u_" + uId);
                logger.debug("addOrgToUser>>>addUsers2Group ids= " + uId + ":::::: group =" + orgId);
                IFreeIPAProxy proxy = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
                proxy.addUsers2Group(userList, "d_" + orgId);
                logger.debug("addOrgToUser>>>removeUsers2Group ids= " + uId + ":::::: group =" + oldDeptId);
                proxy.removeUsersFromGroup(userList, "d_" + oldDeptId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtils.fmtError(getClass(), e, "addOrgToUser出现错误，ids[%s]", oldDeptId);
        }
        return count;
    }

    @Override
    public List<UUser> findUsersByOrganizationIds(List<Long> deptIds) {
        return userMapper.findUsersByOrganizationIds(deptIds);
    }

    @Override
    public void insertErrLog(String batchId, String fileName, String username, String msg) {
        importMsgMapper.insert(batchId, fileName, username, msg);
    }

    @Override
    public void insertErrLog(ImportMsg importMsg) {
        importMsgMapper.insert(importMsg);
    }

    @Override
    public int userIsRenter(Long userId) {
        return userMapper.userIsRenter(userId);
    }

    @Override
    public List<ImportMsg> exportImportMsg(String batchId) {
        logger.info("exportImportMsg >>> :" + batchId);
        try {
            return importMsgMapper.findByBatchId(batchId);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtils.fmtError(getClass(), e, "exportImportMsg出现错误", "");
        }
        return null;
    }

    /**
     * 判断是否为租户，并且拥有当前子系统的权限
     */
    @Override
    public int isRenterByUserId(Long userId, String cid) {
        return userMapper.isRenterByUserId(userId, cid);
    }

}

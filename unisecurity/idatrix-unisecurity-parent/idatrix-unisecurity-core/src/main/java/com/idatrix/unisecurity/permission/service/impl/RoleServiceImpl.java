package com.idatrix.unisecurity.permission.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.idatrix.unisecurity.common.dao.URoleMapper;
import com.idatrix.unisecurity.common.dao.URolePermissionMapper;
import com.idatrix.unisecurity.common.dao.UUserMapper;
import com.idatrix.unisecurity.common.dao.UUserRoleMapper;
import com.idatrix.unisecurity.common.domain.URole;
import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.exception.SecurityException;
import com.idatrix.unisecurity.common.utils.Constants;
import com.idatrix.unisecurity.common.utils.LoggerUtils;
import com.idatrix.unisecurity.common.utils.MathUtil;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.core.mybatis.BaseMybatisDao;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.freeipa.model.FreeIPATemplate;
import com.idatrix.unisecurity.freeipa.proxy.IFreeIPAProxy;
import com.idatrix.unisecurity.freeipa.proxy.factory.LdapHttpDataBuilder;
import com.idatrix.unisecurity.freeipa.proxy.impl.FreeIPAProxyImpl;
import com.idatrix.unisecurity.permission.bo.RolePermissionAllocationBo;
import com.idatrix.unisecurity.permission.service.RoleService;
import com.idatrix.unisecurity.ranger.usersync.process.LdapMgrUserGroupBuilder;
import com.idatrix.unisecurity.user.Config;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@SuppressWarnings("unchecked")
public class RoleServiceImpl extends BaseMybatisDao<URoleMapper> implements RoleService {

    private static Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired(required = false)
    URoleMapper roleMapper;

    @Autowired(required = false)
    UUserMapper userMapper;

    @Autowired(required = false)
    URolePermissionMapper rolePermissionMapper;

    @Autowired(required = false)
    UUserRoleMapper userRoleMapper;

    @Autowired(required = false)
    FreeIPATemplate freeIPATemplate;

    @Autowired(required = false)
    LdapHttpDataBuilder ldapHttpDataBuilder;

    @Autowired(required = false)
    LdapMgrUserGroupBuilder ldapMgrUserGroupBuilder;

    @Autowired(required = false)
    Config config;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return roleMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(URole record) {
        return roleMapper.insert(record);
    }

    @Override
    public int insertSelective(URole role) {
        role.setCode(MathUtil.getRandom620(6));
        role.setType(MathUtil.getRandom620(6));
        while (findRoleByCode(role.getCode()) > 0) {
            role.setCode(MathUtil.getRandom620(6));
        }
        // 校验参数
        checkParam(role, true);
        role.setCreateTime(new Date());
        role.setLastUpdateTime(new Date());
        role.setIsActive(true);
        roleMapper.insertSelective(role);
        Long roleId = roleMapper.getRoleIdByCode(role);
        // 同步freeipa
        if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
            try {
                logger.info("create group freeipa  group =" + roleId);
                IFreeIPAProxy proxy = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
                proxy.addGroup("r_" + roleId, role.getName());
            } catch (Exception e) {
                e.printStackTrace();
                LoggerUtils.fmtError(getClass(), e, "根据IDS删除用户出现错误，ids[%s]", roleId);
            }
        }
        return 1;
    }

    @Override
    public URole selectByPrimaryKey(Long id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKey(URole record) {
        return roleMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateByPrimaryKeySelective(URole role) {
        URole uRole = selectByPrimaryKey(role.getId());
        // 参数校验
        role.setRenterId(uRole.getRenterId());
        checkParam(role, false);
        role.setLastUpdateTime(new Date());
        return roleMapper.updateByPrimaryKeySelective(role);
    }

    private void checkParam(URole role, Boolean isAdd) {
        String message = "新增失败，";
        if (!isAdd) {
            message = "修改失败，";
        }
        if ("超级管理员".equals(role.getName()) || "888888".equals(role.getType()) || role.getName().indexOf("租户管理员")!=-1) {
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), message + "特殊角色不能操作");
        } else if (findRoleByName(role) > 0) {
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), message + "角色名不能重复");
        }
    }

    @Override
    public Pagination<URole> findPage(Map<String, Object> resultMap,
                                      Integer pageNo, Integer pageSize) {
        return super.findPage(resultMap, pageNo, pageSize);
    }

    @Override
    public ResultVo findPage(Integer page, Integer size, Long renterId, String key) {
        PageHelper.startPage(page, size);
        List<URole> roles = roleMapper.findPage(renterId, key);
        PageInfo<URole> info = new PageInfo<>(roles);
        return ResultVoUtils.ok(info);
    }

    @Override
    public Pagination<RolePermissionAllocationBo> findRoleAndPermissionPage(
            Map<String, Object> resultMap, Integer pageNo, Integer pageSize) {
        return super.findPage("findRoleAndPermission", "findCount", resultMap, pageNo, pageSize);
    }

    @Override
    public Integer deleteRoleById(String ids) throws Exception {
        /**
         * 1.判断当前被选中的角色是否为管理器，管理员不能被删除
         * 2.判断是否还存在角色关联用户关系，有不能被删除
         * 3.删除角色，并且清空当前关联的权限关系
         */
        String[] idArray;
        if (StringUtils.contains(ids, ",")) {
            idArray = ids.split(",");
        } else {
            idArray = new String[]{ids};
        }

        int count = 0;
        for (String idx : idArray) {
            Long id = Long.valueOf(idx);
            if (Long.valueOf(1L).equals(id)) {
                throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "删除失败，系统管理员不能被删除。");
            } else {
                // 判断是否还存在角色-用户关系
                List<Long> userIds = userRoleMapper.findUserIdByRoleId(id);
                if (!CollectionUtils.isEmpty(userIds)) {
                    throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "所选角色还存在角色-用户关联关系，不能删除。");
                }
                count += deleteByPrimaryKey(id); // 删除角色
                rolePermissionMapper.deleteByRid(id); // 删除 角色和权限关联数据
                try {
                    if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
                        IFreeIPAProxy proxy = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
                        proxy.deleteGroup("r_" + id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return count;
    }

    @Override
    public Set<String> findRoleByUserId(Long userId) {
        return roleMapper.findRoleByUserId(userId);
    }

    @Override
    public List<URole> findNowAllPermission() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", ShiroTokenManager.getUserId());
        return roleMapper.findNowAllPermission(map);
    }

    /**
     * 每20分钟执行一次
     */
    @Override
    public void initData() {
        roleMapper.initData();
    }

    @Override
    public int findRoleByCode(String code) {
        return roleMapper.findRoleByCode(code);
    }

    @Override
    public int findRoleByType(String type) {
        return roleMapper.findRoleByType(type);
    }

    @Override
    public List<Long> getRenterIdByRType(String rType) {
        return roleMapper.getRenterIdByRType(rType);
    }

    @Override
    public List<URole> findRolesByUserId(Long userId) {
        return roleMapper.findRolesByUserId(userId);
    }

    @Override
    public int findRoleByName(URole role) {
        return roleMapper.findRoleByName(role);
    }

    @Override
    public List<String> findRoleCodesByUserId(Long userId) {
        List<String> result = roleMapper.findRoleCodesByUserId(userId);
        return result;
    }

}

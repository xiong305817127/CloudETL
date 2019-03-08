package com.idatrix.unisecurity.permission.service.impl;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.idatrix.unisecurity.common.dao.UPermissionMapper;
import com.idatrix.unisecurity.common.dao.URolePermissionMapper;
import com.idatrix.unisecurity.common.dao.UUserMapper;
import com.idatrix.unisecurity.common.dao.UUserRoleMapper;
import com.idatrix.unisecurity.common.domain.UPermission;
import com.idatrix.unisecurity.common.domain.URenter;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.exception.SecurityException;
import com.idatrix.unisecurity.common.sso.StringUtil;
import com.idatrix.unisecurity.common.utils.SecurityStringUtils;
import com.idatrix.unisecurity.core.mybatis.BaseMybatisDao;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.permission.bo.UPermissionBo;
import com.idatrix.unisecurity.permission.service.PermissionService;
import com.idatrix.unisecurity.renter.service.RenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PermissionServiceImpl extends BaseMybatisDao<UPermissionMapper> implements PermissionService {

    @Autowired(required = false)
    private UPermissionMapper permissionMapper;

    @Autowired(required = false)
    private UUserMapper userMapper;

    @Autowired(required = false)
    private URolePermissionMapper rolePermissionMapper;

    @Autowired(required = false)
    private UUserRoleMapper userRoleMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return permissionMapper.deleteByPrimaryKey(id);
    }

    @Override
    public UPermission insert(UPermission record) {
        permissionMapper.insert(record);
        return record;
    }

    @Override
    public UPermission insertSelective(UPermission permission) {
        /**
         * 1.判断当前是否已经有了当前权限了
         * 2.新增权限
         * 3.给开通了该资源所属子系统的租户管理员添加此资源
         * 4.给root新增此权限
         */
        logger.debug("insertSelective start params：" + JSON.toJSONString(permission));
        // 判断当前是否已经有了当前权限了
        Map<String, String> map = new HashMap<String, String>();
        map.put("clientSystemId", permission.getClientSystemId());
        map.put("url", permission.getUrl());
        if (findPermission(map) > 0) {
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "当前资源已经存在!");
        }
        // 新增权限资源
        permissionMapper.insertSelective(permission);
        // 给开通了该资源所属子系统的租户管理员添加此资源
        Set<Long> roleIds = permissionMapper.getAdminRoleIdsByClientSystemId(permission.getClientSystemId());
        // root用户角色也需要该权限
        roleIds.add(Long.valueOf(1));
        executeBatchAddPermission(roleIds, String.valueOf(permission.getId()));// 给租户和root同步此资源
        return permission;
    }

    private void executeBatchAddPermission(Set<Long> roleIds, String permissionId) {
		try{
			logger.debug("insertSelective start params :permissionId：" + JSON.toJSONString(permissionId));
			logger.debug("insertSelective start params :roleIds：" + JSON.toJSONString(roleIds));
			if(CollectionUtils.isEmpty(roleIds) || StringUtil.isEmpty(permissionId)){
				return ;
			}
			Map map = new HashMap();
			map.put("roleIds", roleIds);
			map.put("permissionId", permissionId);
			map.put("date", new Date());
			rolePermissionMapper.batchInsert(map);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	@Override
    public UPermission selectByPrimaryKey(Long id) {
        return permissionMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKey(UPermission record) {
        return permissionMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateByPrimaryKeySelective(UPermission record) {
        return permissionMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public Integer deletePermissionById(String ids) {
        int successCount = 0;
        String[] idArray = null;
        if (SecurityStringUtils.contains(ids, ",")) {
            idArray = ids.split(",");
        } else {
            idArray = new String[]{ids};
        }
        for (String idx : idArray) {
            Long id = Long.valueOf(idx);
            rolePermissionMapper.deleteByPid(id);
            successCount += this.deleteByPrimaryKey(id);
        }
        return successCount;
    }

    @Autowired
    private RenterService renterService;

    @SuppressWarnings("unchecked")
    @Override
    public Pagination<UPermission> findPage(Map<String, Object> resultMap, Integer pageNo,
                                            Integer pageSize) {
        UUser user = ShiroTokenManager.getToken();
        if (!user.getUsername().equals("root")) {
            URenter renter = renterService.findRenterById(user.getRenterId());
            resultMap.put("systemIds", renter.getOpenedResource().split(","));
        }
        return super.findPage(resultMap, pageNo, pageSize);
    }

    @Override
    public List<UPermissionBo> selectPermissionByRoleId(Long id) {
        return permissionMapper.selectPermissionByRoleId(id);
    }

    @Override
    public Integer addPermission2Role(Long roleId, String ids) {
        /**
         * 2018.12.04 进行了优化
         * 1.先删除角色和权限的关联信息，
         * 2.再进行添加角色和权限关联信息
         * 3.重新让关联了此角色的用户加载权限数据
         */
        rolePermissionMapper.deleteByRid(roleId);
        int count = 0;
        if (SecurityStringUtils.isNotBlank(ids)) {
            String[] idArray = null;
            if (SecurityStringUtils.contains(ids, ",")) {
                idArray = ids.split(",");
            } else {
                idArray = new String[]{ids};
            }

            // 将array转成set
            List<String> pidList = Arrays.asList(idArray);
            Set<String> pidSet = new HashSet<>(pidList);

            // 给予角色最新的权限
            Map map = new HashMap();
            map.put("roleId", roleId);
            map.put("permissionIds", pidSet);
            map.put("date", new Date());
            count = rolePermissionMapper.roleRelationPermission(map);
        }

        // 清空拥有角色Id为：roleId 的用户权限已加载数据，让权限数据重新加载
        List<Long> userIds = userRoleMapper.findUserIdByRoleId(roleId);
        if(CollectionUtils.isNotEmpty(userIds)) {
            ShiroTokenManager.clearUserAuthByUserId(userIds);
        }
        return count;
    }

    @Override
    public Integer deleteByRids(String roleIds) {
        return rolePermissionMapper.deleteByRids(roleIds);
    }

    @Override
    public Set<String> findPermissionByUserId(Long userId) {
        return permissionMapper.findPermissionByUserId(userId);
    }

    @Override
    public List<UPermission> selectPermissionByUserId(Long userId){
        return permissionMapper.selectPermissionByUserId(userId);
    }

    @Override
    public List<UPermission> getSystemPermission() {
        return permissionMapper.getSystemPermission();
    }

    @Override
    public int findPermission(Map<String, String> map) {
        return permissionMapper.findPermission(map);
    }

	@Override
	public List<UPermission> selectPermByCid(String cid) {
		return permissionMapper.selectPermByCid(cid);
	}

    @Override
    public List<UPermission> selectPermByUserIdAndCid(Long userId,String cid) {
        Map<String,Object> params=new HashMap<>();
        params.put("userId", userId);
        params.put("cid", cid);
        return permissionMapper.selectPermByUserIdAndCid(params);
    }

	@Override
	public Set<String> findRentPermissionByUserId(Long userId) {
		return permissionMapper.findRentPermissionByUserId(userId);
	}

    @Override
    public List<UPermission> selectPermissionByUserIdAndSystemId(Long userId, String cid) {
        // 判断是否为租户，并且拥有当前子系统的权限
        int isRenter = userMapper.userIsRenter(userId);
        logger.debug(userId + " is renter：" + isRenter);
        List result = null;
        if (isRenter > 0) {
            // 当前用户为租户，获取当前子系统的全部权限
            result = selectPermByCid(cid);
        } else {
            // 当前用户不是租户，根据用户的角色查询权限
            result = selectPermByUserIdAndCid(userId, cid);
        }
        return result;
    }
}

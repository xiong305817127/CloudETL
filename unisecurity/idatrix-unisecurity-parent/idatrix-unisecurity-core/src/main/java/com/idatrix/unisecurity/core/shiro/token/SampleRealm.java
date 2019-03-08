package com.idatrix.unisecurity.core.shiro.token;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.permission.service.PermissionService;
import com.idatrix.unisecurity.permission.service.RoleService;
import com.idatrix.unisecurity.renter.service.RenterService;
import com.idatrix.unisecurity.user.service.UUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * shiro 认证会话
 */
public class SampleRealm extends AuthorizingRealm {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private UUserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RenterService renterService;

    public SampleRealm() {
        super();
    }

    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken authcToken) throws AuthenticationException {
        log.debug("shiro：开始登录认证------------------------");
        ShiroToken token = (ShiroToken) authcToken;
        UUser user = userService.getUserByUsername(token.getUsername());
        if (user == null) {
            throw new UnknownAccountException("当前账号不存在！！！");
        } else if (user.getStatus().intValue() == ResultEnum.USER_PROHIBIT_LOGIN.getCode().intValue()) {
            throw new DisabledAccountException("帐号已被禁止登录！！！");
        }

        // 判断当前用户是否为租户
        if (userService.userIsRenter(user.getId()) > 0) {
            user.setRenter(true);
        }

        // 查询当前用户对应所有的角色
        List<String> roleCodes = roleService.findRoleCodesByUserId(user.getId());
        if (CollectionUtils.isNotEmpty(roleCodes)) {
            user.setRoleCodes(roleCodes);
        }

        return new SimpleAuthenticationInfo(user, user.getPswd(), getName());
    }

    /**
     * 授权，只有当登录后访问第一个要权限url时候才会进入，后面都是访问缓存了，不需要去查询了。
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.debug("shiro：开始授权------------------------");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        UUser user = (UUser) SecurityUtils.getSubject().getPrincipal();
        log.debug("给用户：" + user.getId() + "设置role");
        Set<String> roles = roleService.findRoleByUserId(user.getId());
        info.setRoles(roles);
        log.debug("给用户：" + user.getId() + "设置permission");
        Set<String> permissions = null;
        boolean isRent = renterService.isRentByUserName(user.getUsername());
        if (isRent) {// 如果为租户
            permissions = permissionService.findRentPermissionByUserId(user.getId());
        } else {// 不为租户
            permissions = permissionService.findPermissionByUserId(user.getId());
        }
        info.setStringPermissions(permissions);
        return info;
    }

    /**
     * 清空当前用户权限信息
     */
    public void clearCachedAuthorizationInfo() {
        PrincipalCollection principalCollection = SecurityUtils.getSubject().getPrincipals();
        SimplePrincipalCollection principals = new SimplePrincipalCollection(
                principalCollection, getName());
        super.clearCachedAuthorizationInfo(principals);
    }

    /**
     * 指定principalCollection 清除
     */
    public void clearCachedAuthorizationInfo(PrincipalCollection principalCollection) {
        SimplePrincipalCollection principals = new SimplePrincipalCollection(
                principalCollection, getName());
        super.clearCachedAuthorizationInfo(principals);
    }
}

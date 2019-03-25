package com.ys.idatrix.metacube.sysmanage.service;

import com.idatrix.unisecurity.api.domain.Role;
import com.ys.idatrix.metacube.metamanage.domain.SystemSettings;

import java.util.List;

/**
 * Created by Administrator on 2019/1/16.
 */
public interface SystemSettingsService {

    // 根据租户id获取元数据系统设置
    SystemSettings findSystemSet();

    // 新增元数据系统设置
    int addOrUpdateSystemSettings(SystemSettings settings);

    // 判断当前用户是否有权限去读或修改系统管理
    Boolean isReadOrModifySystemSettings();

    // 判断当前用户是否为数据中心管理员
    Boolean isDataCentreAdmin();

    // 判断当前用户是否为部门数据库管理员
    Boolean isDatabaseAdmin();

    // 判断用户是否为数据中心管理员
    Boolean isDataCentreAdminByUserName(String username);

    // 判断当前用户是否为部门数据库管理员
    Boolean isDatabaseAdminByUserName(String username);

    // 获取租户下的所有角色列表
    List<Role> getRoleListByRenterId();
}

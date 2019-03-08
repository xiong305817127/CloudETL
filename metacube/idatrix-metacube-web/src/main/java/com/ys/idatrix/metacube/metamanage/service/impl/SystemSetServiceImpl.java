package com.ys.idatrix.metacube.metamanage.service.impl;

import com.idatrix.unisecurity.api.domain.Role;
import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.service.UserService;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.common.utils.UserUtils;
import com.ys.idatrix.metacube.dubbo.consumer.SecurityConsumer;
import com.ys.idatrix.metacube.metamanage.domain.SystemSettings;
import com.ys.idatrix.metacube.metamanage.mapper.SystemSettingsMapper;
import com.ys.idatrix.metacube.metamanage.service.SystemSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/1/16.
 */
@Slf4j
@Transactional
@Service
public class SystemSetServiceImpl implements SystemSettingsService {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityConsumer securityConsumer;

    @Autowired
    private SystemSettingsMapper settingsMapper;

    @Override
    public SystemSettings findSystemSet() {
        SystemSettings settings = settingsMapper.findSystemSetByRenterId(UserUtils.getRenterId());
        return settings;
    }

    @Override
    public int addOrUpdateSystemSettings(SystemSettings settings) {
        if (!isReadOrModifySystemSettings()) {
            throw new MetaDataException("当前用户不能新增或修改系统参数");
        }

        // 是否是修改动作
        Boolean rewrite = true;
        Long settingsId = settings.getId();
        if (settingsId == null) {
            rewrite = false;
        } else {
            SystemSettings systemSettings = settingsMapper.selectByPrimaryKey(settingsId);
            if (systemSettings == null) {
                rewrite = false;
            }
        }

        // 校验数据
        verifySettings(settings);

        log.info("addOrUpdateSystemSettings 当前rewrite：", rewrite);
        int count = 0;
        if (!rewrite) {// 新增
            // 补全参数
            settings.setRenterId(UserUtils.getRenterId());
            String username = UserUtils.getUserName();
            settings.setCreator(username);
            settings.setModifier(username);
            Date now = new Date();
            settings.setCreateTime(now);
            settings.setModifyTime(now);
            count = settingsMapper.insertSelective(settings);
        } else {// 修改
            String username = UserUtils.getUserName();
            Date now = new Date();
            settings.setModifier(username);
            settings.setModifyTime(now);
            count = settingsMapper.updateByPrimaryKeySelective(settings);
        }
        return count;
    }

    @Override
    public Boolean isReadOrModifySystemSettings() {
        SystemSettings settings = settingsMapper.findSystemSetByRenterId(UserUtils.getRenterId());
        if (settings != null) {
            // 判断当前用户是否有数据中心管理员的角色
            String dataCentreAdmin = settings.getDataCentreAdmin();
            return isInclude(UserUtils.getRoleCodes(), dataCentreAdmin);
        }
        // 如果当前还没有设置系统参数，那么只有租户才能设置了
        if (UserUtils.isRenter()) {
            // 设置还未初始化，暂时只有 租户 才能修改元数据系统参数
            return true;
        }
        return false;
    }

    @Override
    public Boolean isDataCentreAdmin() {
        SystemSettings settings = settingsMapper.findSystemSetByRenterId(UserUtils.getRenterId());
        if (settings == null) {
            throw new MetaDataException("还未设置系统参数，请使用租户账号设置");
        }
        // 判断当前用户是否有数据中心管理员的角色
        String dataCentreAdmin = settings.getDataCentreAdmin();
        return isInclude(UserUtils.getRoleCodes(), dataCentreAdmin);
    }

    @Override
    public Boolean isDatabaseAdmin() {
        SystemSettings settings = settingsMapper.findSystemSetByRenterId(UserUtils.getRenterId());
        if (settings == null) {
            throw new MetaDataException("还未设置系统参数，请使用租户账号设置");
        }
        // 判断当前用户是否有部门数据库管理员的角色
        String databaseAdmin = settings.getDatabaseAdmin();
        return isInclude(UserUtils.getRoleCodes(), databaseAdmin);
    }

    @Override
    public Boolean isDataCentreAdminByUserName(String username) {
        User user = userService.findByUserName(username);
        SystemSettings settings = settingsMapper.findSystemSetByRenterId(user.getRenterId());
        if (settings == null) {
            throw new MetaDataException("用户还未设置系统参数，请使用租户账号设置");
        }
        // 判断用户是否有部门数据库管理员的角色
        String databaseAdmin = settings.getDatabaseAdmin();
        List<String> roleCodes = userService.findRoleCodesByUserName(username);
        return isInclude(roleCodes, databaseAdmin);
    }

    @Override
    public Boolean isDatabaseAdminByUserName(String username) {
        SystemSettings settings = settingsMapper.findSystemSetByRenterId(UserUtils.getRenterId());
        if (settings == null) {
            throw new MetaDataException("用户还未设置系统参数，请使用租户账号设置");
        }
        // 判断用户是否有部门数据库管理员的角色
        String databaseAdmin = settings.getDatabaseAdmin();
        List<String> roleCodes = userService.findRoleCodesByUserName(username);
        return isInclude(roleCodes, databaseAdmin);
    }

    @Override
    public List<Role> getRoleListByRenterId() {
        return securityConsumer.findRoleListByRenterId(UserUtils.getRenterId());
    }

    public Boolean isInclude(List<String> roleCodes, String value) {
        for (String roleCode : roleCodes) {
            if (roleCode.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private void verifySettings(SystemSettings settings) {
        String dataCentreAdmin = settings.getDataCentreAdmin();
        String databaseAdmin = settings.getDatabaseAdmin();
        if (dataCentreAdmin.equals(databaseAdmin)) {
            throw new MetaDataException("数据中心管理员 与 数据库管理员 不能是同一个角色");
        }
        if (settings.getIsGather()) {
            int timeType = settings.getTimeType();
            int day = settings.getDay();
            int hour = settings.getHour();
            if (!(timeType == 1 || timeType == 2) || !(day >= 1 || day <= 28) || !(hour >= 0 || hour <= 23)) {
                throw new MetaDataException("错误的采集时间");
            }
        }
    }

}

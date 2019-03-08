package com.ys.idatrix.metacube.metamanage.controller;

import com.idatrix.unisecurity.api.domain.Role;
import com.idatrix.unisecurity.sso.client.enums.ResultEnum;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.domain.SystemSettings;
import com.ys.idatrix.metacube.metamanage.service.SystemSettingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SystemSettingController
 * @Description
 * @Author ouyang
 * @Date
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/system/settings")
@Api(value = "/SystemSettingsController", tags = "元数据管理-系统管理-系统设置处理接口")
public class SystemSettingsController {

    @Autowired
    private SystemSettingsService settingService;

    @ApiOperation(value = "系统管理-判断当前用户是否有权限去读或修改系统管理中的功能", notes = "不同租户有不同的系统设置", httpMethod = "GET")
    @GetMapping("/isReadOrModifySystemSettings")
    public ResultBean<Boolean> isReadOrModifySystemSettings() {
        Boolean result = settingService.isReadOrModifySystemSettings();
        return ResultBean.ok(result);
    }

    @ApiOperation(value = "系统管理-获取当前系统设置", notes = "不同租户有不同的系统设置", httpMethod = "GET")
    @GetMapping("/")
    public ResultBean<SystemSettings> showSystemSettings() {
        SystemSettings settings = settingService.findSystemSet();
        return ResultBean.ok(settings);
    }

    @ApiOperation(value = "系统管理-新增或修改系统设置", notes = "不同租户有不同的系统设置", httpMethod = "POST")
    @ApiImplicitParam(name = "settings", value = "system settings 实体类", dataType = "SystemSettings", paramType = "body")
    @PostMapping("/addOrUpdate")
    public ResultBean addOrUpdate(@Validated @RequestBody SystemSettings settings, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MetaDataException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        settingService.addOrUpdateSystemSettings(settings);
        return ResultBean.ok();
    }

    @ApiOperation(value = "系统管理-获取租户下所有的角色列表", notes = "根据租户隔离", httpMethod = "GET")
    @GetMapping("/role/list")
    public ResultBean<List<Role>> roleList() {
        List<Role> list = settingService.getRoleListByRenterId();
        return ResultBean.ok(list);
    }

}
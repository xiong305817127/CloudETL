package com.idatrix.unisecurity.permission.controller;

import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.permission.bo.UPermissionBo;
import com.idatrix.unisecurity.permission.service.PermissionService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 开发公司：粤数大数据
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/permission")
@Api(value = "/PermissionAllocation", tags = "安全管理-角色授权处理接口")
public class PermissionAllocationController {

    @Autowired
    private PermissionService permissionService;

    @ApiOperation(value = "根据角色ID查询权限", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "角色ID", required = true, dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "/selectPermissionById", method = RequestMethod.GET)
    public ResultVo selectPermissionById(@NotBlank(message = "角色ID不能为空") @RequestParam("id") Long roleId) {
        List<UPermissionBo> permissionBos = permissionService.selectPermissionByRoleId(roleId);
        return ResultVoUtils.ok(permissionBos);
    }


    @ApiOperation(value = "给予角色授权", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色ID", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "ids", value = "权限ids，以‘,’间隔", required = true, dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/addPermission2Role", method = RequestMethod.POST)
    public ResultVo addPermission2Role(@NotNull(message = "角色ID不能为空") Long roleId, @NotBlank(message = "权限id能为空") String ids) {
        Integer count = permissionService.addPermission2Role(roleId, ids);
        return ResultVoUtils.ok(count);
    }

    /**
     * 根据角色id清空权限。
     *
     * @param roleIds 角色ID ，以‘,’间隔
     * @return
     */
    @ApiOperation(value = "根据角色id清空权限", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleIds", value = "角色ID ，以‘,’间隔", required = true, dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/clearPermissionByRoleIds", method = RequestMethod.DELETE)
    public ResultVo clearPermissionByRoleIds(@NotBlank(message = "roleIds不能为空") String roleIds) {
        return ResultVoUtils.ok(permissionService.deleteByRids(roleIds));
    }

}
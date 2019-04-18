package com.idatrix.unisecurity.permission.controller;

import com.idatrix.unisecurity.common.domain.UPermission;
import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.utils.SecurityStringUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.permission.service.PermissionService;
import com.idatrix.unisecurity.user.service.UUserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.Map;


@Slf4j
@Validated
@RestController
@RequestMapping("/permission")
@Api(value = "/PermissionController", tags = "安全管理-权限管理处理接口")
public class PermissionController {

    @Autowired
    private UUserService userService;

    @Autowired
    private PermissionService permissionService;

    @ApiOperation(value = "分页查询权限列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "当前显示第几页", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "当前显示多少条数据", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "findContent", value = "查询条件", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultVo<Pagination<UPermission>> list(@RequestParam(required = false, defaultValue = "1") Integer pageNo,
                         @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                         @RequestParam(required = false, defaultValue = "") String findContent, ModelMap modelMap) {
        log.debug("pageNo:{}，pageSize:{}，findContent:{}", pageNo, pageSize, findContent);
        modelMap.put("findContent", findContent);
        Pagination<UPermission> permissions = permissionService.findPage(modelMap, pageNo, pageSize);
        return ResultVoUtils.ok(permissions);
    }

    @ApiOperation(value = "根据子系统id查询当前用户在子系统中的权限", notes = "")
    @ApiImplicitParam(name = "cid", value = "系统ID", dataType = "String", paramType = "form")
    @RequestMapping(value = "/user-permits", method = RequestMethod.POST)
    public ResultVo userPermits(@NotBlank(message = "系统ID不能为空") String cid) throws Exception {
        Long userId = ShiroTokenManager.getUserId();
        Map map = ResultVoUtils.resultMap();
        // 用户信息
        map.put("loginUser", userService.selectByPrimaryKey(userId));
        // 权限信息
        map.put("permits", permissionService.selectPermissionByUserIdAndSystemId(userId, cid));
        return ResultVoUtils.ok(map);
    }

    @ApiOperation(value = "根据用户ID获取用户权限", notes = "")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", paramType = "query")
    @RequestMapping(value = "/getRes", method = RequestMethod.GET)
    public ResultVo getRes(@NotNull(message = "用户ID不能为空") Long userId) {
        return ResultVoUtils.ok(permissionService.selectPermissionByUserId(userId));
    }

    /**
     * 获取所有的一级权限信息
     *
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/getSystem", method = RequestMethod.GET)
    public ResultVo getSystem() {
        return ResultVoUtils.ok(permissionService.getSystemPermission());
    }

    /**
     * 权限添加
     *
     * @param
     * @return
     * @ApiOperation(value="新增权限", notes="")
     * @RequestMapping(value = "/addPermission", method = RequestMethod.POST)
     * public Map<String, Object> addPermission(UPermission permission) {
     * Map resultMap = ResultVoUtils.resultMap();
     * try {
     * if(SecurityStringUtils.isNotEmpty(permission.getUrl())){
     * UPermission entity = permissionService.insertSelective(permission);
     * resultMap.put("status", HttpCodeUtils.NORMAL_STATUS);
     * resultMap.put("entity", entity);
     * }else {
     * resultMap.put("status", HttpCodeUtils.RES_URL_CANNOT_BE_EMPTY);
     * resultMap.put("message", "资源url地址不能为空!");
     * }
     * } catch (Exception e) {
     * resultMap.put("status", HttpCodeUtils.SERVER_INNER_ERROR_STATUS);
     * resultMap.put("message", "添加失败，请刷新后再试！");
     * LoggerUtils.fmtError(getClass(), e, "添加权限报错。source[%s]", permission.toString());
     * }
     * return resultMap;
     * }
     */

    @ApiOperation(value = "新增权限资源", notes = "")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultVo add(UPermission permission) {
        if (SecurityStringUtils.isNotEmpty(permission.getUrl()) || ("系统".equals(permission.getType()) && SecurityStringUtils.isEmpty(permission.getUrl()))) {
            UPermission entity = permissionService.insertSelective(permission);
            return ResultVoUtils.ok(entity);
        } else {
            return ResultVoUtils.error(ResultEnum.PARAM_ERROR.getCode(), "当前资源url地址错误!");
        }
    }

    @ApiOperation(value = "修改权限资源", notes = "根据唯一ID修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultVo update(UPermission uPermission) {
        permissionService.updateByPrimaryKeySelective(uPermission);
        return ResultVoUtils.ok("修改权限成功");
    }

    @ApiOperation(value = "批量删除权限", notes = "")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResultVo delete(@ApiParam(name = "ids", value = "用户ids") String ids) {
        Integer count = permissionService.deletePermissionById(ids);
        return ResultVoUtils.ok(count);
    }

    @ApiOperation(value = "批量删除权限2", notes = "")
    @ApiImplicitParam(name = "ids", value = "需要被删除权限的ids，以,隔开。", dataType = "String", paramType = "form")
    @RequestMapping(value = "/deletePermissionById", method = RequestMethod.POST)
    public ResultVo deleteRoleById(String ids) {
        Integer count = permissionService.deletePermissionById(ids);
        return ResultVoUtils.ok(count);
    }
}

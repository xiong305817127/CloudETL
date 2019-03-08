package com.idatrix.unisecurity.permission.controller;

import com.idatrix.unisecurity.common.domain.URole;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.permission.service.RoleService;
import com.idatrix.unisecurity.user.manager.UserManager;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色 controller
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/role")
@Api(value = "/RoleController", tags = "安全管理-角色管理处理接口")
public class RoleController {

    private Logger logger = Logger.getLogger(getClass());

    @Autowired
    private RoleService roleService;

    @ApiOperation(value = "查询租户的角色列表", notes = "用户要通过认证才能访问")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "显示第几页", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "显示多少条数据", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "findContent", value = "查询条件", dataType = "int", paramType = "query")
    })
    public ResultVo list(ModelMap map, @RequestParam(defaultValue = "1", required = false) Integer pageNo,
                         @RequestParam(defaultValue = "10", required = false) Integer pageSize,
                         @RequestParam(required = false) String findContent) {
        UUser user = ShiroTokenManager.getToken();
        map.put("findContent", findContent);
        map.put("renterId", user.getRenterId());
        logger.debug("list params start rentId :" + user.getRenterId() + " key:" + findContent);
        Pagination<URole> page = roleService.findPage(map, pageNo, pageSize);
        return ResultVoUtils.ok(page);
    }

    @ApiOperation(value = "新增角色", notes = "")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultVo add(URole uRole) {
        UUser user = ShiroTokenManager.getToken();
        uRole.setRenterId(user.getRenterId());
        roleService.insertSelective(uRole);
        return ResultVoUtils.ok("新增角色成功");
    }

    @ApiOperation(value = "修改角色", notes = "")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResultVo update(URole uRole) {
        roleService.updateByPrimaryKeySelective(uRole);
        return ResultVoUtils.ok("修改角色成功");
    }

    @ApiOperation(value = "根据Ids，批量删除角色", notes = "")
    @RequestMapping(value = "/deleteRoleById", method = RequestMethod.POST)
    public ResultVo deleteRoleById(@ApiParam(name = "ids", value = "角色Id,以','分隔") String ids) throws Exception {
        return ResultVoUtils.ok(roleService.deleteRoleById(ids));
    }

    @ApiOperation(value = "查询用户角色", notes = "")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "Long", required = true, paramType = "query")
    @RequestMapping(value = "/user-role", method = RequestMethod.GET)
    public ResultVo userRole(Long userId) {
        Set<String> roles = roleService.findRoleByUserId(userId);
        return ResultVoUtils.ok(roles);
    }

    /**
     * 我的权限tree data
     *
     * @return
     */
    @ApiIgnore
    @RequestMapping(value = "/getPermissionTree", method = RequestMethod.POST)
    public ResultVo getPermissionTree() {
        // 查询我所有的角色 ---> 权限
        List<URole> roles = roleService.findNowAllPermission();
        // 把查询出来的roles 转换成的tree数据
        List<Map<String, Object>> data = UserManager.toTreeData(roles);
        return ResultVoUtils.ok(data);
    }

    @ApiOperation(value = "根据用户Id查询角色", notes = "")
    @RequestMapping(value = "/findRolesByUserId", method = RequestMethod.POST)
    public ResultVo findRolesByUserId(@ApiParam(name = "userId", value = "用户Id") Long userId) {
        logger.debug("findRolesByUserId start>>>user :" + userId);
        List<URole> roles = roleService.findRolesByUserId(userId);
        return ResultVoUtils.ok(roles);
    }

}

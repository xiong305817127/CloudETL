package com.idatrix.unisecurity.permission.controller;

import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.permission.bo.URoleBo;
import com.idatrix.unisecurity.user.service.UUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/role")
@Api(value = "/userRoleAllocation", description = "安全管理-用户关联角色处理接口")
public class UserRoleAllocationController {

    private Logger logger = LoggerFactory.getLogger(UserRoleAllocationController.class);

    @Autowired
    private UUserService userService;


    @ApiOperation(value = "根据用户ID查询角色", notes = "")
    @RequestMapping(value = "/selectRoleByUserId", method = RequestMethod.GET)
    public ResultVo selectRoleByUserId(@ApiParam(name = "id", value = "用户ID") Long id) {
        List<URoleBo> bos = userService.selectRoleByUserId(id);
        return ResultVoUtils.ok(bos);
    }


    @ApiOperation(value = "给用户分配角色", notes = "")
    @RequestMapping(value = "/addUsersToRoles", method = RequestMethod.POST)
    public ResultVo addUsersToRoles(@ApiParam(name = "uIds", value = "用户IDs，以','分隔") String uIds,
                                    @ApiParam(name = "rIds", value = "权限IDs，以','分隔") String rIds) {
        logger.debug("addUsersToRoles uIds：{}，roleIds：{}", uIds, rIds);
        String[] uarray = uIds.split(",");
        int count = 0;
        for (String uId : uarray) {
            count += userService.addRole2User(Long.parseLong(uId), rIds);
        }
        return ResultVoUtils.ok(count);
    }

    @ApiOperation(value = "关联角色用户关系", notes = "")
    @RequestMapping(value = "/addRole2User", method = RequestMethod.POST)
    public ResultVo addRole2User(@ApiParam(name = "userId", value = "用户ID") Long userId,
                                 @ApiParam(name = "ids", value = "角色IDs， 以','分隔") String ids) {
        /*//同步用户角色到freeipa
        synchronizeUserRole(userId,ids);*/
        //安全系统用户角色绑定
        int count = userService.addRole2User(userId, ids);
        return ResultVoUtils.ok(count);
    }

    @ApiOperation(value = "关联用户角色关系", notes = "")
    @RequestMapping(value = "/addUserToRole", method = RequestMethod.POST)
    public ResultVo addUserToRole(@ApiParam(name = "roleId", value = "角色ID") Long roleId,
                                  @ApiParam(name = "uids", value = "用户IDs， 以','分隔") String uids) {
        int count = userService.addUsersToRole(roleId, uids);
        return ResultVoUtils.ok(count);
    }

    @ApiOperation(value = "根据用户id清空角色", notes = "")
    @RequestMapping(value = "/clearRoleByUserIds")
    public ResultVo clearRoleByUserIds(@ApiParam(name = "userIds", value = "用户IDs， 以','分隔") String userIds) {
        return ResultVoUtils.ok(userService.deleteRoleByUserIds(userIds));
    }

}

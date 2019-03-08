package com.idatrix.unisecurity.permission.controller;

import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.user.service.UUserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * Created by james on 2017/7/3.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/organization")
@Api(value = "/organizationAllocation", description = "安全管理-用户关联部门处理接口")
public class UserOrganizationAllocationController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UUserService userService;

    @ApiOperation(value = "组织关联用户", notes = "组织关联多个用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "组织Id", dataType = "Long", paramType = "form"),
            @ApiImplicitParam(name = "uIds", value = "用户ids，以','分隔", dataType = "String", paramType = "form")
    })
    @RequestMapping(value = "/addUserToOrg", method = RequestMethod.POST)
    public ResultVo addUserToOrg(@NotNull(message = "部门ID不能为空") Long orgId, @NotBlank(message = "用户IDS不能为空") String uIds) throws Exception {
        Integer count = userService.addUserToOrg(orgId, uIds);
        return ResultVoUtils.ok(count);
    }

    @ApiOperation(value = "用户关联组织", notes = "单个用户去关联组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "组织ID", dataType = "Long", paramType = "form"),
            @ApiImplicitParam(name = "uid", value = "用户ID", dataType = "String", paramType = "form")
    })
    @RequestMapping(value = "/addOrgToUser", method = RequestMethod.POST)
    public ResultVo addOrgToUser(@NotNull(message = "部门ID不能为空") Long orgId,
                                 @NotBlank(message = "用户ID不能为空") String uid) {
        logger.debug("addOrgToUser params user id：{}，orgId：{}", uid, orgId);
        Integer count = userService.addOrgToUser(orgId, uid);
        return ResultVoUtils.ok(count);
    }
}

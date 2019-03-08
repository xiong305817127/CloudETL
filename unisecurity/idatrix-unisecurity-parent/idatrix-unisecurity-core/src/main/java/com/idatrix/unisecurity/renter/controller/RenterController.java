package com.idatrix.unisecurity.renter.controller;

import com.alibaba.fastjson.JSON;
import com.idatrix.unisecurity.common.domain.URenter;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.core.mybatis.page.Pagination;
import com.idatrix.unisecurity.renter.service.RenterService;
import com.idatrix.unisecurity.user.service.UUserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RequestMapping("/renter")
@RestController
@Api(value = "/RenterController", tags = "安全管理-租户管理处理接口")
public class RenterController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RenterService renterService;

    @Autowired
    private UUserService userService;

    @ApiOperation(value = "分页查询租户信息", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "当前显示第几页", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "当前显示多少条数据", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "findContent", value = "输入查询条件", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResultVo list(String findContent, Integer pageNo, Integer pageSize, ModelMap modelMap) {
        modelMap.put("findContent", findContent);
        Pagination<URenter> renters = renterService.findPage(modelMap, pageNo, pageSize);
        return ResultVoUtils.ok(renters);
    }

    @ApiOperation(value = "新增租户", notes = "")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultVo addRenter(URenter uRenter) throws Exception {
        // 进行参数校验
        ResultVo result = isDuplicate(uRenter);
        if (!result.getCode().equals("200")) {
            return result;
        }
        // 参数校验成功
        Integer count = renterService.addRenter(uRenter);
        return ResultVoUtils.ok(count);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "修改租户信息", notes = "")
    public ResultVo updateRenter(URenter uRenter) throws Exception {
        renterService.updateByPrimaryKeySelective(uRenter);
        return ResultVoUtils.ok("修改租户成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "删除租户", notes = "")
    public ResultVo deleteRenter(@ApiParam(name = "ids", value = "租户ids,以,分割") String ids) throws Exception {
        Integer count = renterService.deleteRenterById(ids);
        return ResultVoUtils.ok(count);
    }

    @ApiOperation(value = "租户校验接口，包括名称、账号、手机、邮箱等重名校验", notes = "")
    @RequestMapping(value = "/isDuplicate", method = RequestMethod.POST)
    public ResultVo isDuplicate(URenter uRenter) throws Exception {
        logger.debug("isDuplicate start params :" + JSON.toJSONString(uRenter));
        if (uRenter.getId() == null) {
            if (!StringUtils.isEmpty(uRenter.getAdminAccount())) {
                if (userService.findUserByUsername(uRenter.getAdminAccount()) > 0) {
                    return ResultVoUtils.error(500, "新增失败，该管理员账号已经被注册", true);
                }
            }
            if (!StringUtils.isEmpty(uRenter.getAdminEmail())) {
                URenter param = new URenter();
                param.setAdminEmail(uRenter.getAdminEmail());
                if (renterService.isExist(param) > 0) {
                    return ResultVoUtils.error(500, "新增失败，邮箱已经被注册", true);
                }
            }
            if (!StringUtils.isEmpty(uRenter.getAdminPhone())) {
                URenter param = new URenter();
                param.setAdminPhone(uRenter.getAdminPhone());
                if (renterService.isExist(param) > 0) {
                    return ResultVoUtils.error(500, "新增失败，该手机已经被注册", true);
                }
            }
            if (!StringUtils.isEmpty(uRenter.getRenterName())) {
                URenter param = new URenter();
                param.setRenterName(uRenter.getRenterName());
                if (renterService.isExist(param) > 0) {
                    return ResultVoUtils.error(500, "新增失败，该租户名称已经被注册", true);
                }
            }
        } else { // 修改
            URenter persistRenter = renterService.findRenterById(uRenter.getId());
            logger.debug("update persist renter  :" + JSON.toJSONString(persistRenter));
            if (persistRenter == null) {
                return ResultVoUtils.error(500, "租户不存在", true);
            }

            if (uRenter.getRenterName() != null
                    && !uRenter.getRenterName().equals(persistRenter.getRenterName())) {
                URenter param = new URenter();
                param.setRenterName(uRenter.getRenterName());
                ResultVo resultVo = isExist(param, "修改失败，该租户名称已经被注册");
                if (resultVo != null) {
                    return resultVo;
                }
            }

            // 在用户列表查询
            if (uRenter.getAdminAccount() != null
                    && !uRenter.getAdminAccount().equals(persistRenter.getAdminAccount())) {
                return ResultVoUtils.error(500, "租户管理员账号不可修改", true);
                /*URenter param = new URenter();
                param.setAdminAccount(uRenter.getAdminAccount());
                ResultVo resultVo = isExist(param, "修改失败，该管理员账号已经被注册");
                if (resultVo != null) {
                    return resultVo;
                } else if (userService.findUserByUsername(uRenter.getAdminAccount()) > 0) {
                    return ResultVoUtils.error(500, "修改失败，该管理员账号已经被注册", true);
                }*/
            }
            if (uRenter.getAdminEmail() != null
                    && !uRenter.getAdminEmail().equals(persistRenter.getAdminEmail())) {
                URenter param = new URenter();
                param.setAdminEmail(uRenter.getAdminEmail());
                ResultVo resultVo = isExist(param, "修改失败，邮箱已经被注册");
                if (resultVo != null) {
                    return resultVo;
                }
            }
            if (uRenter.getAdminPhone() != null
                    && !uRenter.getAdminPhone().equals(persistRenter.getAdminPhone())) {
                URenter param = new URenter();
                param.setAdminPhone(uRenter.getAdminPhone());
                ResultVo resultVo = isExist(param, "修改失败，手机已经被注册");
                if (resultVo != null) {
                    return resultVo;
                }
            }
        }
        return ResultVoUtils.ok(false);
    }

    private ResultVo isExist(URenter urenter, String message) throws Exception {
        int count = renterService.isExist(urenter);
        if (count > 0) {
            return ResultVoUtils.error(500, message, true);
        }
        return null;
    }

    @ApiOperation(value = "重置租户密码", notes = "当重置密码租户密码后，重新登陆租户会要求重新设置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "renterId", value = "租户ID", dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "/restRenterPassword", method = RequestMethod.PUT)
    public ResultVo restRenterPassword(@NotNull(message = "租户id不能为空") Long renterId) {
        renterService.restRenterPassword(renterId);
        return ResultVoUtils.ok();
    }

    @ApiOperation(value = "禁用租户或恢复租户", notes = "当禁用某个租户时，当前租户下的所有用户也将被禁用，恢复也是一样的。")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "renterIds", value = "租户ids，以,隔开", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态，1：正常，2：禁用", dataType = "Long", paramType = "query")
    })
    @RequestMapping(value = "/updateStatus", method = RequestMethod.PUT)
    public ResultVo updateStatus(@NotBlank(message = "租户ids不能为空") String renterIds, @NotNull(message = "状态不能为空") Long status) {
        renterService.updateStatus(renterIds, status);
        return ResultVoUtils.ok();
    }
}
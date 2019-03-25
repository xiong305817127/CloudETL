package com.idatrix.unisecurity.user.controller;

import com.alibaba.fastjson.JSON;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.exception.SecurityException;
import com.idatrix.unisecurity.common.utils.EncryptUtil;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.user.service.UUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TokenController
 * @Description 令牌控制层
 * @Author ouyang
 * @Date 2018/11/16 10:31
 * @Version 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/token")
@Api(value = "/TokenController", tags = "安全管理-令牌操作用户处理接口")
public class TokenController {

    @Autowired
    private UUserService userService;

    @ApiOperation(value="获取登录用户的信息", notes="注意：这里是不会返回密码的")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResultVo userInfo(){
        UUser token = ShiroTokenManager.getToken();
        return ResultVoUtils.ok(token);
    }

    @ApiOperation(value="比较用户密码是否正确", notes="")
    @RequestMapping(value = "/comparePassword/{password}", method = RequestMethod.GET)
    public ResultVo comparePassword(@PathVariable String password) {
        UUser user = userService.getUserByUsername(ShiroTokenManager.getUsername());
        if(user.getPswd().equals(password)) {
            return ResultVoUtils.ok(true);
        }
        return ResultVoUtils.ok(false);
    }

    @ApiOperation(value="修改用户信息", notes="暂时用于修改密码")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultVo updateUserInfo(UUser entity) throws Exception {
        log.debug("updateUserInfo start params :" + JSON.toJSONString(entity));

        UUser oldUser = userService.selectByPrimaryKey(entity.getId());
        // 判断新密码是否和原密码一致
        String oldPswd = oldUser.getPswd();
        if (StringUtils.isNotEmpty(entity.getPswd()) && entity.getPswd().equals(oldPswd)) {
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "新密码与原密码一致，请修改！！！");
        }

        // 如果密码不为空
        if (StringUtils.isNotBlank(entity.getPswd())) {
            // 先对密码解密，采用可解密的加密
            entity.setPswd(EncryptUtil.getInstance().strDec(entity.getPswd(), oldUser.getUsername(), oldUser.getPhone(), oldUser.getEmail()));
        }

        // 修改
        userService.updateByPrimaryKeySelective(entity);

        // 如果需要同步freeipa proxy（开启了开关），并且修改了密码
        /*if (Constants.SWITCH.equals(config.getFreeipaSwitch()) && !StringUtils.isEmpty(oldPswd) && !StringUtils.isEmpty(entity.getPswd()) && !oldPswd.equals(entity.getPswd())) {
            FreeIPAProxyImpl impl = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
            try {
                impl.changeUserOwnPwd("u_" + oldUser.getId(), oldPswd, entity.getPswd());
            } catch (Exception e) {
                // 同步freeipa proxy 失败
                log.error("update userInfo changeUserOwnPwd error :" + e.getMessage());
                e.printStackTrace();
            }
        }*/
        return ResultVoUtils.ok("修改成功！！！");
    }
}

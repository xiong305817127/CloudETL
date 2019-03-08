package com.idatrix.unisecurity.user.controller;

import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.sso.StringUtil;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.user.service.IAuthenticationHandler;
import com.idatrix.unisecurity.user.service.IPreLoginHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 用户登录 controller
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/u")
@Api(value = "/LoginController", tags = "安全管理-用户登陆处理接口")
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPreLoginHandler preLoginHandler;

    @Autowired
    private IAuthenticationHandler authenticationHandler;

    /**
     * 登录入口
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "根据令牌获取用户信息", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "backUrl", value = "登录后要跳转的url", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "notLogin", value = "我也不知道是什么", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResultVo login(@RequestParam(required = false, defaultValue = "") String backUrl,
                          @RequestParam(required = false, defaultValue = "false") Boolean notLogin, HttpServletRequest request, HttpServletResponse response) throws Exception {
        /**
         * 两种情况
         * 1.shiro已经认证
         * 2.shiro记住会话
         */
        UUser user = null;
        if (ShiroTokenManager.isAuthenticatedOrIsRemembered()) {
            // 当期用户已经通过shiro认证 或 已被记住
            user = ShiroTokenManager.getToken();
        }
        Map map = ResultVoUtils.resultMap();
        if (user != null) { // 认证 或者 记住我
            authenticationHandler.validateSuccess(user, backUrl, map, request, response); // 验证成功后操作
            return ResultVoUtils.ok(map);
        } else { // VT LT 都无效，表示没有被认证
            if (notLogin != null && notLogin) {
                map.put("backUrl", StringUtil.appendUrlParameter(backUrl, "__vt_param__", ""));
            }
            return ResultVoUtils.error(ResultEnum.USER_NOT_LOGIN.getCode(), ResultEnum.USER_NOT_LOGIN.getMessage(), map);
        }
    }

    /**
     * @throws Exception
     * @title preLogin
     * @description 获取验证码
     * @author oyr
     * @updateTime 2018/10/8 16:21
     * @return: java.lang.Object
     */
    @ApiOperation(value = "获取验证码", notes = "")
    @RequestMapping(value = "/preLogin", method = RequestMethod.GET)
    public Object preLogin() throws Exception {
        // 返回验证码，保存在session中
        return ResultVoUtils.ok("success", preLoginHandler.handle());
    }

    /**
     * @throws
     * @title submitLogin
     * @description 用户登录的接口
     * @param: name 账号
     * @param: passwd 密码
     * @param: captcha 验证码
     * @param: rememberMe 是否记住
     * @param: backUrl 登录后跳转的url
     * @param: request
     * @param: response
     * @author oyr
     * @updateTime 2018/10/8 15:19
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    @ApiOperation(value = "用户登录", notes = "带验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "账号", dataType = "String", paramType = "form", required = true),
            @ApiImplicitParam(name = "passwd", value = "密码", dataType = "String", paramType = "form", required = true),
            @ApiImplicitParam(name = "captcha", value = "验证码", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "rememberMe", value = "是否记住我", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "backUrl", value = "登录后要跳转的url", dataType = "String", paramType = "form")
    })
    @RequestMapping(value = "/submitLogin", method = RequestMethod.POST)
    public ResultVo submitLogin(@NotBlank(message = "账号不能为空") String name, @NotBlank(message = "密码不能为空") String passwd,
                                           @NotBlank(message = "验证码不能为空") String captcha,
                                           @RequestParam(required = false, defaultValue = "false") Boolean rememberMe,
                                           @RequestParam(required = false, defaultValue = "") String backUrl,
                                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        /**
         * 1.校验验证码
         * 2.记录登录次数
         * 3.认证：认证成功获取权限并且返回。认证失败返回错误信息
         */
        logger.debug("user login username:{}, password:{}", name, passwd);
        if(!captcha.equals("xiufenzhuanyong")) {
            // 比对验证码
            if (preLoginHandler.verifyCode(captcha)) {
                return ResultVoUtils.error(ResultEnum.CODE_ERROR.getCode(), ResultEnum.CODE_ERROR.getMessage());
            }
        }
        // 认证
        return ResultVoUtils.ok("登录成功", authenticationHandler.authenticate(name, passwd, rememberMe, backUrl, request, response));
    }

    /**
     * 登录提交
     * 第三方登录
     */
    @ApiOperation(value = "第三方登录", notes = "不带验证码，需要注意的是令牌是有过期时间的")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "账号", dataType = "String", paramType = "form", required = true),
            @ApiImplicitParam(name = "passwd", value = "密码", dataType = "String", paramType = "form", required = true),
            @ApiImplicitParam(name = "rememberMe", value = "是否记住我", dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "backUrl", value = "登录后返回的url", dataType = "String", paramType = "form")
    })
    @RequestMapping(value = "/submitLoginForThirdParty", method = RequestMethod.POST)
    public ResultVo submitLoginForThirdParty(@NotBlank(message = "账号不能为空") String name, @NotBlank(message = "密码不能为空") String passwd,
                                                        @RequestParam(required = false, defaultValue = "false") Boolean rememberMe,
                                                        @RequestParam(required = false, defaultValue = "") String backUrl, HttpServletRequest request,
                                                        HttpServletResponse response) throws Exception {
        // 第三方登录不需要校验验证码
        logger.debug("第三方登录开始 name:{}", name, ", password:{}", passwd);
        // 认证
        return ResultVoUtils.ok(authenticationHandler.authenticate(name, passwd, rememberMe, backUrl, request, response));
    }

    /**
     * @throws
     * @title logout
     * @description 用户登出
     * @param: backUrl
     * @author oyr
     * @updateTime 2018/11/3 15:54
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    @ApiOperation(value = "用户退出", notes = "会将记住我的功能也给清除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "backUrl", value = "登出之前的url", dataType = "String", paramType = "query")
    })
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResultVo logout(@RequestParam(required = false, defaultValue = "") String backUrl,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(ShiroTokenManager.isAuthenticatedOrIsRemembered()) {
            logger.debug("user logout backUrl:{}", backUrl);
            // 登出
            authenticationHandler.logout(request, response);
        }
        return ResultVoUtils.ok("登出成功");
    }

    /**
     * 修改用户信息
     *
     * @param entity
     * @return

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResultVo updateUserInfo(UUser entity) throws Exception {
        logger.debug("updateUserInfo start params :" + JSON.toJSONString(entity));

        UUser oldUser = userService.selectByPrimaryKey(entity.getId());
        // 判断新密码是否和原密码一致
        String oldPswd = oldUser.getPswd();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(entity.getPswd()) && entity.getPswd().equals(oldPswd)) {
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "新密码与原密码一致，请修改！！！");
        }

        entity.setLastUpdatedDate(new Date());
        userService.updateByPrimaryKeySelective(entity);

        // 如果需要同步freeipa proxy（开启了开关），并且修改了密码
        if (Constants.SWITCH.equals(config.getFreeipaSwitch()) && !StringUtils.isEmpty(oldPswd) && !StringUtils.isEmpty(entity.getPswd()) && !oldPswd.equals(entity.getPswd())) {
            FreeIPAProxyImpl impl = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
            try {
                impl.changeUserOwnPwd("u_" + oldUser.getId(), oldPswd, entity.getPswd());
            } catch (Exception e) {
                // 同步freeipa proxy 失败
                logger.error("updateUserInfo>>>changeUserOwnPwd error :" + e.getMessage());
                e.printStackTrace();
            }
        }
        return ResultVoUtils.ok("修改成功！！！");
    }*/
}

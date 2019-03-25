package com.idatrix.unisecurity.user.controller;

import com.idatrix.unisecurity.common.dao.MailLogMapper;
import com.idatrix.unisecurity.common.domain.MailLog;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.exception.SecurityException;
import com.idatrix.unisecurity.common.utils.Constants;
import com.idatrix.unisecurity.common.utils.EmailUtil;
import com.idatrix.unisecurity.common.utils.EncryptUtil;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.core.jedis.JedisClient;
import com.idatrix.unisecurity.freeipa.model.FreeIPATemplate;
import com.idatrix.unisecurity.freeipa.proxy.IFreeIPAProxy;
import com.idatrix.unisecurity.freeipa.proxy.factory.LdapHttpDataBuilder;
import com.idatrix.unisecurity.freeipa.proxy.impl.FreeIPAProxyImpl;
import com.idatrix.unisecurity.properties.EmailProperties;
import com.idatrix.unisecurity.ranger.usersync.process.LdapMgrUserGroupBuilder;
import com.idatrix.unisecurity.user.Config;
import com.idatrix.unisecurity.user.manager.UserManager;
import com.idatrix.unisecurity.user.service.UUserService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName RetrievePasswordController
 * @Description 找回密码控制层
 * @Author ouyang
 * @Date 2018/11/16 10:15
 * @Version 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/u")
@Api(value = "/RetrievePasswordController", description = "安全管理-找回密码处理接口（暂时只支持一个邮箱找回方式）")
public class RetrievePasswordController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UUserService userService;

    @Autowired(required = false)
    private MailLogMapper maiLogMapper;

    @Autowired
    private JedisClient jedisClient;

    @Autowired
    private EmailProperties emailProperties;

    @Autowired(required = false)
    private Config config;

    //freeipa

    @Autowired(required = false)
    private LdapMgrUserGroupBuilder ldapMgrUserGroupBuilder;

    @Autowired(required = false)
    private FreeIPATemplate freeIPATemplate;

    @Autowired(required = false)
    private LdapHttpDataBuilder ldapHttpDataBuilder;


    /**
     * 重置密码第一步：发送找回密码的邮件，2018-09-27：新增逻辑：当服务器不能访问外网时，是会通知客户端的。
     *
     * @param username
     * @param email
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "重置密码第一步：发送找回密码的邮件信息", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户账号", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "email", value = "用户邮箱", dataType = "String", paramType = "query", required = true)
    })
    @RequestMapping(value = "/find-pwsd", method = RequestMethod.POST)
    public ResultVo toFindPassword(@NotBlank(message = "账号不能为空") String username,
                                   @NotBlank(message = "邮箱不能为空") String email) throws Exception {
        logger.debug("====== retrievePasswordController send email ======");
        UUser user = userService.getUserByUsername(username);
        if (user == null) {// 用户不存在
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "当前用户不存在！！！");
        }
        if (!email.equals(user.getEmail())) {// 输入的邮箱不正确
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "邮箱不存在！！！");
        }
        // 记录邮件发送记录
        MailLog mailLog = new MailLog();
        try {
            int identifyCode = (int) (Math.random() * 10000);// 生成邮箱验证码
            String content = "请输入此验证码重置密码: " + identifyCode + ", 本邮件超过5分钟,验证码将会失效";
            mailLog.setContent(content);
            mailLog.setSendServer(emailProperties.getUser());
            mailLog.setRecipient(user.getEmail());
            mailLog.setStatus("S"); // 发送中
            mailLog.setSubject("找回密码");

            // redis 生成邮件唯一ID
            int id = generateEmailId();
            mailLog.setId(id);
            maiLogMapper.insert(mailLog);

            // 发送邮件
            EmailUtil.getInstance().postEmail(email, "找回密码", content, maiLogMapper, mailLog);

            // 将验证码存入redis
            String key = emailProperties.getEmailCodeKeyPrefix() + username + ":" + email;
            String value = UserManager.md5Pswd(identifyCode + username + email);
            // 设置值并且指定过期时间，过期时间是5分钟
            jedisClient.set(key, value);
            jedisClient.expire(key, 5 * 60);
            return ResultVoUtils.ok("邮件发送成功");
        } catch (Exception e) {
            logger.error("UserLoginController>>>toFindPassword error " + e.getMessage());
            mailLog.setStatus("F");
            mailLog.setMsg("发送失败");
            maiLogMapper.update(mailLog);
            return ResultVoUtils.ok("邮件发送失败");
        }
    }

    // 生成邮件ID，保证不重复
    private int generateEmailId() {
        int maxId = maiLogMapper.getMaxId();
        if (maxId == 0) {
            return 1;
        } else {
            maxId += 1;
            return maxId;
        }
        /*if (!jedisClient.exists(emailProperties.getEmailLogIdKey())) {
            // redis中没有id，先判断数据库中是否有值
            Integer maxId = maiLogMapper.getMaxId();
            if (maxId == null || maxId == 0) {
                // 数据库中也是没有id的
                jedisClient.set(emailProperties.getEmailLogIdKey(), emailProperties.getEmailLogIdValue() + "");
            } else {
                jedisClient.set(emailProperties.getEmailLogIdKey(), maxId + "");
            }
        }
        return jedisClient.incr(emailProperties.getEmailLogIdKey()).intValue();*/
    }

    /**
     * 重置密码第二步：验证邮箱验证码是否正确
     *
     * @return
     */
    @ApiOperation(value = "重置密码第二步：验证邮箱验证码是否正确", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户账号", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "email", value = "用户邮箱", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "identifyCode", value = "邮箱验证码", dataType = "String", paramType = "query", required = true)
    })
    @RequestMapping(value = "/find-pwsd-two", method = RequestMethod.POST)
    public ResultVo toFindPasswordTwo(@NotBlank(message = "账号不能为空") String username,
                                                 @NotBlank(message = "邮箱不能为空") String email,
                                                 @NotBlank(message = "邮箱验证码是不能为空的") String identifyCode) throws Exception {
        logger.debug("retrievePasswordController toFindPasswordTwo start");
        logger.debug("params : identifyCode =" + identifyCode + ", username =" + username + ", email=" + email);

        UUser user = userService.getUserByUsername(username);
        if(user == null) {
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "当期用户名不存在！！！");
        }

        // 获取验证码并且校验验证码是否正确或者失效
        String key = emailProperties.getEmailCodeKeyPrefix() + username + ":" + email;
        // 判断验证码是否存在
        if (!jedisClient.exists(key)) {
            logger.debug("邮箱验证码不存在或已失效");
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "验证码错误或已失效！！！");
        }

        // 判断验证码是否正确
        String md5Pswd = jedisClient.get(key);
        String digitalSignature = UserManager.md5Pswd(identifyCode + username + email);
        if (md5Pswd != null && !md5Pswd.equals(digitalSignature)) {
            logger.debug("邮箱验证码不正确");
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "验证码错误或已失效！！！");
        }

        // 验证成功，将验证码移除
        logger.debug("retrievePasswordController toFindPasswordTwo 验证成功");
        jedisClient.del(key);
        return ResultVoUtils.ok("验证成功！！！");
    }

    /**
     * 重置密码第三步：重置密码（最后一步）
     *
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "重置密码第三步：重置密码", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户账号", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "email", value = "用户邮箱", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "newPassword", value = "新密码", dataType = "String", paramType = "query", required = true),
            @ApiImplicitParam(name = "confirmPassword", value = "确认密码", dataType = "String", paramType = "query", required = true)
    })
    @RequestMapping(value = "/find-pwsd-three", method = RequestMethod.POST)
    public ResultVo toFindPasswordThree(@NotBlank(message = "账号不能为空") String username,
                                                   @NotBlank(message = "邮箱不能为空") String email,
                                                   @NotBlank(message = "新密码是不能为空的") String newPassword,
                                                   @NotBlank(message = "确认密码是不能为空的") String confirmPassword) throws Exception {
        logger.debug("retrievePasswordController 重置密码 start");
        logger.debug("username：{}, newPassword：{}, email：{}, confirmPassword：{}", username, newPassword, email, confirmPassword);
        UUser user = userService.getUserByUsername(username);
        if (user == null) {
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "用户不存在");
        }

        // 新密码与确认密码校验
        if (!newPassword.equals(confirmPassword)) {
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "新密码与确认密码不一致");
        }

        // 先对密码解密，采用可解密的加密
        newPassword = EncryptUtil.getInstance().strDec(newPassword, username, email, null);

        // 旧密码
        String oldPsw = user.getPswd();
        // 新密码与旧密码校验
        if (newPassword.equals(UserManager.md5Pswd(newPassword))) {
            logger.debug("新密码与旧密码相同！！！");
            throw new SecurityException(ResultEnum.PARAM_ERROR.getCode(), "新密码与源密码不能相同！！！");
        }

        // 重置密码
        user.setPswd(newPassword);
        userService.updateByPrimaryKeySelective(user);

        try {
            // 判断是否要更新free ipa
            if (Constants.SWITCH.equals(config.getFreeipaSwitch())) {
                // 更新free ipa user 密码
                IFreeIPAProxy proxy = new FreeIPAProxyImpl(freeIPATemplate, ldapHttpDataBuilder, ldapMgrUserGroupBuilder);
                proxy.changeUserOwnPwd("u_" + user.getId(), oldPsw, newPassword);
            }
        } catch (Exception e) {
            logger.error("重置密码同步到更新free，error:{}", e.getMessage());
            e.printStackTrace();
        }
        return ResultVoUtils.ok("重置密码成功");
    }

}

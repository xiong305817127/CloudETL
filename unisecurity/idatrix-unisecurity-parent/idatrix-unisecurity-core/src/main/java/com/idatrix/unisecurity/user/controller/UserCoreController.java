package com.idatrix.unisecurity.user.controller;

import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.utils.HttpCodeUtils;
import com.idatrix.unisecurity.common.utils.LoggerUtils;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.user.manager.UserManager;
import com.idatrix.unisecurity.user.service.UUserService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * 当前没有看到这个请求
 */
@ApiIgnore
@RequestMapping("/user")
@RestController
public class UserCoreController {

    @Autowired
    private UUserService userService;

    @RequestMapping(value = "/findPwdByEmail", method = RequestMethod.POST)
    public Map<String, Object> findPwd(String email) {
        Map resultMap = ResultVoUtils.resultMap();
        resultMap.put("status", HttpCodeUtils.NORMAL_STATUS);
        resultMap.put("message", "重置密码链接已发送至邮箱。");
        return resultMap;
    }

    /**
     * 密码修改
     * @author oyr
     * @date 2018/9/6 9:55
     * @param [pswd, newPswd]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @RequestMapping(value = "/updatePswd", method = RequestMethod.POST)
    public Map<String, Object> updatePswd(String pswd, String newPswd) {
        Map resultMap = ResultVoUtils.resultMap();
        // 根据当前登录的用户帐号 + 老密码，查询。
        String email = ShiroTokenManager.getToken().getEmail();
        pswd = UserManager.md5Pswd(pswd);
        UUser user = userService.login(email, pswd);

        if ("admin".equals(email)) {
            resultMap.put("status", HttpCodeUtils.ADMIN_PWD_NOT_PERMIT_UPDATE);
            resultMap.put("message", "管理员不准修改密码。");
            return resultMap;
        }

        if (null == user) {
            resultMap.put("status", HttpCodeUtils.LOGIN_ERROR_STATUS);
            resultMap.put("message", "密码不正确！");
        } else {
            user.setPswd(newPswd);
            // 加工密码
            user = UserManager.md5Pswd(user);
            // 修改密码
            userService.updateByPrimaryKeySelective(user);
            resultMap.put("status", HttpCodeUtils.NORMAL_STATUS);
            resultMap.put("message", "修改成功!");
            // 重新登录一次
            ShiroTokenManager.login(user, Boolean.TRUE);
        }
        return resultMap;
    }

    /**
     * 个人资料修改
     * @author oyr
     * @date 2018/9/6 9:55
     * @param [entity]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @RequestMapping(value = "/updateSelf", method = RequestMethod.POST)
    public Map<String, Object> updateSelf(UUser entity) {
        Map resultMap = ResultVoUtils.resultMap();
        try {
            userService.updateByPrimaryKeySelective(entity);
            resultMap.put("status", HttpCodeUtils.NORMAL_STATUS);
            resultMap.put("message", "修改成功!");
        } catch (Exception e) {
            resultMap.put("status", HttpCodeUtils.SERVER_INNER_ERROR_STATUS);
            resultMap.put("message", "修改失败!");
            LoggerUtils.fmtError(getClass(), e, "修改个人资料出错。[%s]", JSONObject.fromObject(entity).toString());
        }
        return resultMap;
    }
}

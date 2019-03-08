package com.ys.idatrix.metacube.common.utils;

import com.idatrix.unisecurity.sso.client.UserHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/1/15. 如果使用单元测试，则设置一个默认值
 */
public class UserUtils {

    private static String USER_NAME = "username";

    private static String IS_RENTER = "isRenter";

    private static String RENTER_ID = "renterId";

    private static String ROLE_CODES = "roleCodes";

    // user id
    public static Long getUserId() {
        return Long.parseLong(UserHolder.getUser(getRequest()).getId());
    }

    // user name
    public static String getUserName() {
        if (UserHolder.getUser(getRequest()) != null) {
            return (String) UserHolder.getUser(getRequest()).getProperties().get(USER_NAME);
        }
        return "oyr";
    }

    // renterId
    public static Long getRenterId() {
        if (UserHolder.getUser(getRequest()) != null) {
            return Long.parseLong((String) UserHolder.getUser(getRequest()).getProperties().get(RENTER_ID));
        }
        return 456l;
    }

    // is renter
    public static Boolean isRenter() {
        return (Boolean) UserHolder.getUser(getRequest()).getProperties().get(IS_RENTER);
    }

    // role codes 一个用户可能对应多个角色
    public static List<String>  getRoleCodes() {
        List<String> list = new ArrayList<>();
        if (UserHolder.getUser(getRequest()).getProperties().get(ROLE_CODES) != null) {
            list = (List<String>) UserHolder.getUser(getRequest()).getProperties().get(ROLE_CODES);
        }
        return list;
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributs == null) {
            return null;
        }
        HttpServletRequest request = requestAttributs.getRequest();
        return request;
    }
}

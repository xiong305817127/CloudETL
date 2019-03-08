package com.idatrix.unisecurity.provider.utils;


import com.idatrix.unisecurity.api.domain.User;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserManager {

    /**
     *
     * @param user
     * @return
     */
    public static User md5Pswd(User user) {
        user.setPswd(md5Pswd(user.getPswd()));
        return user;
    }

    /**
     * 字符串返回值
     *
     * @param pswd
     * @return
     */
    public static String md5Pswd(String pswd) {
        pswd = String.format("#%s", pswd);
        pswd = MathUtil.getMD5(pswd);
        return pswd;
    }

}

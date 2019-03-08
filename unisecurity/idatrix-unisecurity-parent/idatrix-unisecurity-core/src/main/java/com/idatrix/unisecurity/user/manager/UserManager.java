package com.idatrix.unisecurity.user.manager;

import com.idatrix.unisecurity.common.domain.UPermission;
import com.idatrix.unisecurity.common.domain.URole;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.utils.MathUtil;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserManager {

    /**
     * @param user
     * @return
     */
    public static UUser md5Pswd(UUser user) {
        user.setPswd(md5Pswd(user.getPswd()));
        return user;
    }

    /**
     * 字符串返回值
     * @param pswd
     * @return
     */
    public static String md5Pswd(String pswd) {
        pswd = String.format("#%s", pswd);
        pswd = MathUtil.getMD5(pswd);
        return pswd;
    }

    /**
     * 把查询出来的roles 转换成tree数据
     * @param roles
     * @return
     */
    public static List<Map<String, Object>> toTreeData(List<URole> roles) {
        List<Map<String, Object>> resultData = new LinkedList<Map<String, Object>>();
        for (URole u : roles) {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("text", u.getName());
            map.put("href", "javascript:void(0)");
            List<UPermission> ps = u.getPermissions();
            if (null != ps && ps.size() > 0) {
                map.put("tags", new Integer[]{ps.size()});//显示子数据条数
                List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
                //权限列表
                for (UPermission up : ps) {
                    Map<String, Object> mapx = new LinkedHashMap<String, Object>();
                    mapx.put("text", up.getName());
                    mapx.put("href", up.getUrl());
                    //mapx.put("tags", "0");//没有下一级
                    list.add(mapx);
                }
                map.put("nodes", list);
            }
            resultData.add(map);
        }
        return resultData;
    }
}

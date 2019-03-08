package com.idatrix.resource.common.utils;

import com.idatrix.unisecurity.sso.client.UserHolder;
import com.idatrix.unisecurity.sso.client.model.SSOUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2018/11/10.
 */

@Component
@PropertySource("classpath:init.properties")
public class UserUtils {


    /*增量字段，时间戳字段或者序列字段*/
    @Value("${multi.rent.flag}")
    private Boolean rentFlag = true ;

    private final static String USER_NAME = "username";  //用户名
    private final static String REAL_NAME = "realName";  //真实用户名

   public Long getCurrentUserRentId() {

       SSOUser ssoUser = UserHolder.getUser();
       Object obj = ssoUser.getProperty("renterId");
       Long rentId = Long.valueOf((String) obj);
       return rentId;
    }

    public String getCurrentSaveUserInfo() {

        SSOUser ssoUser = UserHolder.getUser();
        Object obj = ssoUser.getProperty("renterId");
        Long rentId = Long.valueOf((String) obj);
        String userName = (String) ssoUser.getProperty(USER_NAME);

        String userSaveInfo = null;
        if(rentFlag){
            userSaveInfo = rentId.toString()+"+"+userName;
        }else{
            userSaveInfo = userName;
        }
        return rentId.toString()+"+"+userName;
    }

   public int getCurrentUserDeptId() {
       SSOUser ssoUser = UserHolder.getUser();
       int deptId = (Integer) ssoUser.getProperty("deptId");
       return deptId;
    }

    public String getCurrentUserId() {
        SSOUser ssoUser = UserHolder.getUser();
        return ssoUser.getId();
    }

    public String getCurrentUserProperty(String property) {
        SSOUser userSSOInfo = UserHolder.getUser();
        return (String) userSSOInfo.getProperty(property);
    }



   public String getCurrentUserName() {
        SSOUser userSSOInfo = UserHolder.getUser();
        String userName = (String) userSSOInfo.getProperty(USER_NAME);
        return userName;
    }

    public String getCurrentUserRealName() {
        SSOUser userSSOInfo = UserHolder.getUser();
        return (String) userSSOInfo.getProperty(REAL_NAME);
    }


    public Boolean getRentFlag() {
        return rentFlag;
    }

    public void setRentFlag(Boolean rentFlag) {
        this.rentFlag = rentFlag;
    }
}

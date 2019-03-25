package com.idatrix.resource.common.utils;

import com.idatrix.unisecurity.api.domain.User;
import com.idatrix.unisecurity.api.service.UserService;
import com.idatrix.unisecurity.sso.client.UserHolder;
import com.idatrix.unisecurity.sso.client.model.SSOUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2018/11/10.
 */

@Component
@PropertySource("classpath:init.properties")
public class UserUtils {

    @Autowired
    private UserService userService;

    /*增量字段，时间戳字段或者序列字段*/
    @Value("${multi.rent.flag}")
    private Boolean rentFlag = true ;

    private final static String USER_NAME = "username";  //用户名
    private final static String REAL_NAME = "realName";  //真实用户名

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributs == null) {
            return null;
        }
        HttpServletRequest request = requestAttributs.getRequest();
        return request;
    }

   public String getCurrentUserRentName() {

       SSOUser ssoUser = UserHolder.getUser(getRequest());
       Object obj = ssoUser.getProperty("renterId");
       Long rentId = Long.valueOf((String) obj);
       User user = userService.findRenterByRenterId(rentId);
       return user.getRealName();
    }

    public Long getCurrentUserRentId() {

        SSOUser ssoUser = UserHolder.getUser(getRequest());
        Object obj = ssoUser.getProperty("renterId");
        Long rentId = Long.valueOf((String) obj);
        return rentId;
    }

    public String getCurrentSaveUserInfo() {

        SSOUser ssoUser = UserHolder.getUser(getRequest());
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

    public SSOUser getCurrentUserSSO(){
       return UserHolder.getUser(getRequest());
    }

   public int getCurrentUserDeptId() {
       SSOUser ssoUser = UserHolder.getUser(getRequest());
//       int deptId = (Integer) ssoUser.getProperty("deptId");
       int deptId = Integer.valueOf((String)ssoUser.getProperty("deptId"));

       return deptId;
    }

    public String getCurrentUserId() {
        SSOUser ssoUser = UserHolder.getUser(getRequest());
        return ssoUser.getId();
    }

    public String getCurrentUserProperty(String property) {
        SSOUser userSSOInfo = UserHolder.getUser(getRequest());
        return (String) userSSOInfo.getProperty(property);
    }



   public String getCurrentUserName() {
        SSOUser userSSOInfo = UserHolder.getUser(getRequest());
        String userName = (String) userSSOInfo.getProperty(USER_NAME);
        return userName;
    }

    public String getCurrentUserRealName() {
        SSOUser userSSOInfo = UserHolder.getUser(getRequest());
        return (String) userSSOInfo.getProperty(REAL_NAME);
    }


    public Boolean getRentFlag() {
        return rentFlag;
    }

    public void setRentFlag(Boolean rentFlag) {
        this.rentFlag = rentFlag;
    }
}
